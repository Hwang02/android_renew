package com.hotelnow.fragment.favorite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.adapter.FavoriteActivityAdapter;
import com.hotelnow.fragment.model.FavoriteStayItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.NonScrollListView;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteActivityFragment extends Fragment {

    private SharedPreferences _preferences;
    private NonScrollListView mlist;
    private ImageView map_img;
    private TextView tv_review_count;
    private RelativeLayout btn_location, btn_date;
    private ArrayList<FavoriteStayItem> mItems = new ArrayList<>();
    private String banner_id, search_txt;
    private LinearLayout btn_filter;
    private FavoriteActivityAdapter adapter;
    private Button btn_go_login;
    private RelativeLayout main_view;
    private TextView btn_go_list;
    private DbOpenHelper dbHelper;
    private boolean _hasLoadedOnce= false; // your boolean field
    boolean firstDragFlag = true;
    boolean dragFlag = false;   //현재 터치가 드래그 인지 확인
    float startYPosition = 0, endYPosition =0;       //터치이벤트의 시작점의 Y(세로)위치
    private NestedScrollView scroll;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_a, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void authCheck() {
        MainActivity.showProgress();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", Util.decode(_preferences.getString("userid", null).replace("HN|","")));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
            paramObj.put("ver", Util.getAppVersionName(getActivity()));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                MainActivity.hideProgress();
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (obj.getString("result").equals("0")) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("email", null);
                        prefEditor.putString("username", null);
                        prefEditor.putString("phone", null);
                        prefEditor.putString("userid", null);
                        prefEditor.commit();

                        mlist.setEmptyView(getView().findViewById(R.id.login_view));
                        getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.white);
                        btn_go_login.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivityForResult(intent, 80);
                            }
                        });
                        ((FavoriteFragment)getParentFragment()).isdelete(false);
                        MainActivity.hideProgress();
                    } else {
                        mlist.setEmptyView(getView().findViewById(R.id.empty_view));
                        getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.footerview);
                        btn_go_list.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                ((MainActivity)getActivity()).setTapMove(6, true);
                            }
                        });

                        getFavorite();
                    }
                } catch (Exception e) {
                    MainActivity.hideProgress();
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFavorite(){
        MainActivity.showProgress();
        String url = CONFIG.like_list+"?only_id=N&type=activity";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                MainActivity.hideProgress();
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        MainActivity.hideProgress();
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(obj.getJSONArray("activity").length() >0) {
                        final JSONArray list = obj.getJSONArray("activity");
                        JSONObject entry = null;
                        for (int i = 0; i < list.length(); i++) {
                            entry = list.getJSONObject(i);
                            mItems.add(new FavoriteStayItem(
                                    entry.getString("id"),
                                    entry.getString("name"),
                                    entry.getString("category"),
                                    entry.getString("location"),
                                    entry.has("street2") ? entry.getString("street2"):"",
                                    entry.getString("img_url"),
                                    entry.getString("sale_price"),
                                    entry.getString("sale_rate"),
                                    entry.has("items_quantity") ? entry.getInt("items_quantity") : 0,
                                    entry.getString("benefit_text"),
                                    entry.getString("grade_score"),
                                    entry.getString("real_grade_score"),
                                    entry.has("is_private_deal") ? "Y" : "N",
                                    entry.getString("is_hot_deal"),
                                    entry.getString("is_add_reserve"),
                                    entry.getInt("coupon_count")
                            ));
                        }
                        if(obj.getJSONArray("activity").length() >1) {
                            // 상단 애니메이션을 위한 터치
                            scroll.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent ev) {

                                    switch (ev.getAction()) {
                                        case MotionEvent.ACTION_MOVE:       //터치를 한 후 움직이고 있으면
                                            dragFlag = true;
                                            if (firstDragFlag) {     //터치후 계속 드래그 하고 있다면 ACTION_MOVE가 계속 일어날 것임으로 무브를 시작한 첫번째 터치만 값을 저장함
                                                startYPosition = ev.getY(); //첫번째 터치의 Y(높이)를 저장
                                                firstDragFlag = false;   //두번째 MOVE가 실행되지 못하도록 플래그 변경
                                            }

                                            break;

                                        case MotionEvent.ACTION_UP:
                                            endYPosition = ev.getY();
                                            firstDragFlag = true;

                                            if (dragFlag) {  //드래그를 하다가 터치를 실행

                                                // 시작Y가 끝 Y보다 크다면 터치가 아래서 위로 이루어졌다는 것이고, 스크롤은 아래로내려갔다는 뜻이다.
                                                // (startYPosition - endYPosition) > 10 은 터치로 이동한 거리가 10픽셀 이상은 이동해야 스크롤 이동으로 감지하겠다는 뜻임으로 필요하지 않으면 제거해도 된다.
                                                if ((startYPosition > endYPosition) && (startYPosition - endYPosition) > 10) {
                                                    //TODO 스크롤 다운 시 작업
                                                    LogUtil.e("xxxxxxx", "down");
                                                    ((FavoriteFragment) getParentFragment()).toolbarAnimateHide();
                                                }
                                                //시작 Y가 끝 보다 작다면 터치가 위에서 아래로 이러우졌다는 것이고, 스크롤이 올라갔다는 뜻이다.
                                                else if ((startYPosition < endYPosition) && (endYPosition - startYPosition) > 10) {
                                                    //TODO 스크롤 업 시 작업
                                                    LogUtil.e("xxxxxxx", "up");
                                                    ((FavoriteFragment) getParentFragment()).toolbarAnimateShow(0);
                                                }
                                            }

                                            startYPosition = 0.0f;
                                            endYPosition = 0.0f;
                                            break;
                                    }
                                    return false;
                                }

                            });
                        }
                        ((FavoriteFragment)getParentFragment()).isdelete(true);
                    }
                    else{
                        ((FavoriteFragment)getParentFragment()).isdelete(false);
                    }
                    adapter.notifyDataSetChanged();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.hideProgress();
                        }
                    },500);
                } catch (Exception e) {
                    MainActivity.hideProgress();
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 80){
            authCheck();
            ((MainActivity)getActivity()).setTitle();
            ((MainActivity)getActivity()).setTapdelete("MYPAGE");
            CONFIG.TabLogin=true;
        }
        else if(requestCode == 70 && resultCode == 80){
            mItems.clear();
            adapter.notifyDataSetChanged();
            MainActivity.showProgress();
            getFavorite();
        }
    }

    public void setLike(final int position){
        MainActivity.showProgress();
        final String sel_id = mItems.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch(Exception e){
            Log.e(CONFIG.TAG, e.toString());
        }

        Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                MainActivity.hideProgress();
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        MainActivity.hideProgress();
                        ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                        return;
                    }
                    TuneWrap.Event("favorite_activity_del", sel_id);
                    dbHelper.deleteFavoriteItem(false,  sel_id,"A");
                    LogUtil.e("xxxx", "찜하기 취소");
                    ((MainActivity)getActivity()).showIconToast("관심 상품 담기 취소", false);
                    mItems.clear();
                    getFavorite();
                    MainActivity.hideProgress();
                }catch (JSONException e){
                    MainActivity.hideProgress();
                }
            }
        });
    }

    public void setDeleteAll(){
        MainActivity.showProgress();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("all_flag", "Y");
            paramObj.put("type", "activity");

        } catch(Exception e){
            Log.e(CONFIG.TAG, e.toString());
        }

        Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                MainActivity.hideProgress();
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        MainActivity.hideProgress();
                        ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                        return;
                    }

                    dbHelper.deleteFavoriteItem(true, "", "A");
                    LogUtil.e("xxxx", "찜하기 전체 취소");
                    ((MainActivity)getActivity()).showToast("관심 상품 삭제 완료");
                    mItems.clear();
                    getFavorite();
                    MainActivity.hideProgress();
                }catch (JSONException e){
                    MainActivity.hideProgress();
                }
            }
        });
    }

    public void setDateRefresh(String ecc_date, String eee_date){
        mItems.clear();
        adapter.notifyDataSetChanged();
        MainActivity.showProgress();
        getFavorite();
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(isFragmentVisible_);
        // we check that the fragment is becoming visible
        if (isFragmentVisible_ && !_hasLoadedOnce) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            },500);

            _hasLoadedOnce = true;
        }
        else if(isFragmentVisible_ && CONFIG.TabLogin && _hasLoadedOnce){
            CONFIG.TabLogin=false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            },500);
        }
        if(isFragmentVisible_ && ((FavoriteFragment)getParentFragment()) != null) {
            if (adapter != null && adapter.getCount() > 0) {
                ((FavoriteFragment) getParentFragment()).isdelete(true);
            } else {
                ((FavoriteFragment) getParentFragment()).isdelete(false);
            }
        }
    }

    private void init(){
        // preference
        TuneWrap.Event("favorite_activity");

        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

//        search_txt = getArguments().getString("search_txt");
//        banner_id = getArguments().getString("banner_id");

        mlist = (NonScrollListView) getView().findViewById(R.id.h_list);
        scroll = (NestedScrollView) getView().findViewById(R.id.scroll);
        adapter = new FavoriteActivityAdapter(getActivity(), FavoriteActivityFragment.this,0, mItems);
        mlist.setAdapter(adapter);
        btn_go_login = (Button) getView().findViewById(R.id.btn_go_login);
        main_view = (RelativeLayout) getView().findViewById(R.id.main_view);
        btn_go_list = (TextView) getView().findViewById(R.id.btn_go_list);
        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView) view.findViewById(R.id.hid);

                TuneWrap.Event("favorite_activity_product", hid.getText().toString());
                Intent intent = new Intent(getActivity(), DetailActivityActivity.class);
                intent.putExtra("tid", hid.getText().toString());
                intent.putExtra("save", true);
                startActivityForResult(intent, 70);
            }
        });

        authCheck();
    }

}
