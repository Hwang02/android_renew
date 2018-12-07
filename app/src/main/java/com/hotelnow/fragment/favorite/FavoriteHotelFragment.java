package com.hotelnow.fragment.favorite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.adapter.FavoriteHotelAdapter;
import com.hotelnow.fragment.model.FavoriteStayItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.NonScrollListView;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;

public class FavoriteHotelFragment extends Fragment {

    private SharedPreferences _preferences;
    private NonScrollListView mlist;
    private ImageView map_img;
    private TextView tv_review_count;
    private RelativeLayout btn_location, btn_date;
    private ArrayList<FavoriteStayItem> mItems = new ArrayList<>();
    private String banner_id, search_txt;
    private LinearLayout btn_filter;
    private FavoriteHotelAdapter adapter;
    private Button btn_go_login;
    private RelativeLayout main_view;
    private TextView btn_go_list;
    private static String ee_date =null, ec_date = null;
    private DbOpenHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_h, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

//        search_txt = getArguments().getString("search_txt");
//        banner_id = getArguments().getString("banner_id");

        mlist = (NonScrollListView) getView().findViewById(R.id.h_list);
        adapter = new FavoriteHotelAdapter(getActivity(), FavoriteHotelFragment.this, 0, mItems);
        mlist.setAdapter(adapter);
        btn_go_login = (Button) getView().findViewById(R.id.btn_go_login);
        main_view = (RelativeLayout) getView().findViewById(R.id.main_view);
        btn_go_list = (TextView) getView().findViewById(R.id.btn_go_list);
        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView) view.findViewById(R.id.hid);
                Intent intent = new Intent(getActivity(), DetailHotelActivity.class);
                intent.putExtra("hid", hid.getText().toString());
                intent.putExtra("save", true);
                startActivityForResult(intent, 70);
            }
        });
        authCheck();
    }

    public void authCheck() {
        MainActivity.showProgress();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", _preferences.getString("userid", null));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
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
                        btn_go_login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivityForResult(intent, 80);
                            }
                        });
                        ((FavoriteFragment)getParentFragment()).setCancelView(true);
                    } else {
                        mlist.setEmptyView(getView().findViewById(R.id.empty_view));
                        getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.footerview);
                        btn_go_list.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity)getActivity()).setTapMove(5, true);
                            }
                        });
                        ((FavoriteFragment)getParentFragment()).setCancelView(false);
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
        String url = CONFIG.like_list+"?only_id=N&type=stay";

        if(ec_date != null || ee_date != null){
            url += url+"&checkin_date="+ec_date+"&checkout_date="+ee_date;
        }

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

                    if(obj.getJSONArray("stay").length() >0) {
                        final JSONArray list = obj.getJSONArray("stay");
                        JSONObject entry = null;
                        for (int i = 0; i < list.length(); i++) {
                            entry = list.getJSONObject(i);
                            mItems.add(new FavoriteStayItem(
                                    entry.getString("id"),
                                    entry.getString("name"),
                                    entry.getString("category"),
                                    entry.getString("street1"),
                                    entry.getString("street2"),
                                    entry.getString("landscape"),
                                    entry.getString("sale_price"),
                                    entry.getString("sale_rate"),
                                    entry.getInt("items_quantity"),
                                    entry.getString("special_msg"),
                                    entry.getString("grade_score"),
                                    entry.getString("real_grade_score"),
                                    entry.getString("is_private_deal"),
                                    entry.getString("is_hot_deal"),
                                    entry.getString("is_add_reserve"),
                                    entry.getInt("coupon_count")
                            ));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    MainActivity.hideProgress();
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
            getFavorite();
            MainActivity.showProgress();
        }
    }

    public void setDateRefresh(String ecc_date, String eee_date){
        ec_date = ecc_date;
        ee_date = eee_date;
        mItems.clear();
        getFavorite();
        MainActivity.showProgress();
    }

    public void setLike(final int position){
        MainActivity.showProgress();
        final String sel_id = mItems.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "stay");
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
                        ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                        return;
                    }

                    dbHelper.deleteFavoriteItem(false,  sel_id,"H");
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
            paramObj.put("type", "stay");

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
                        ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                        return;
                    }

                    dbHelper.selectAllFavoriteStayItem();
                    LogUtil.e("xxxx", "찜하기 전체 취소");
                    ((MainActivity)getActivity()).showToast("관심 상품 삭제 완료");
                    mItems.clear();
                    getFavorite();
                }catch (JSONException e){
                    MainActivity.hideProgress();
                }
            }
        });
    }

}
