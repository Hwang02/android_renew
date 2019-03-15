package com.hotelnow.fragment.hotdeal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.HotDealActivity;
import com.hotelnow.adapter.HotdaelActivityAdapter;
import com.hotelnow.fragment.reservation.ReservationFragment;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HotDealActivityFragment extends Fragment {

    private SharedPreferences _preferences;
    private DbOpenHelper dbHelper;
    private ListView mlist;
    private HotdaelActivityAdapter adapter;
    private ArrayList<SearchResultItem> mActivityItem = new ArrayList<>();
    private boolean _hasLoadedOnce = false; // your boolean field
    private RelativeLayout wrapper;
    boolean firstDragFlag = true;
    boolean dragFlag = false;   //현재 터치가 드래그 인지 확인
    float startYPosition = 0, endYPosition = 0;       //터치이벤트의 시작점의 Y(세로)위치

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a_result, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getData() {
        wrapper.setVisibility(View.VISIBLE);
        Api.get(CONFIG.hotdeal_list + "/activity_hot_deals", new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                wrapper.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        wrapper.setVisibility(View.GONE);
                        return;
                    }
                    if (isAdded()) {
                        if (obj.has("activity_hot_deals")) {
                            JSONArray mActivity = new JSONArray(obj.getJSONObject("activity_hot_deals").getJSONArray("deals").toString());
                            mActivityItem.clear();
                            if (mActivity.length() > 0) {
                                for (int i = 0; i < mActivity.length(); i++) {
                                    mActivityItem.add(new SearchResultItem(
                                            mActivity.getJSONObject(i).getString("id"),
                                            "",
                                            mActivity.getJSONObject(i).getString("name"),
                                            "",
                                            mActivity.getJSONObject(i).getString("category"),
                                            mActivity.getJSONObject(i).has("location") ? mActivity.getJSONObject(i).getString("location") : "",
                                            "",
                                            0,
                                            0,
                                            "N",
                                            mActivity.getJSONObject(i).getString("img_url"),
                                            mActivity.getJSONObject(i).getString("sale_price"),
                                            "",
                                            mActivity.getJSONObject(i).getString("sale_rate"),
                                            0,
                                            mActivity.getJSONObject(i).getString("benefit_text"),
                                            mActivity.getJSONObject(i).getString("review_score"),
                                            mActivity.getJSONObject(i).getString("grade_score"),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "N",
                                            mActivity.getJSONObject(i).getString("is_hot_deal"),
                                            mActivity.getJSONObject(i).getString("is_add_reserve"),
                                            mActivity.getJSONObject(i).getInt("coupon_count"),
                                            i == 0 ? true : false,
                                            0
                                    ));
                                }

                                if (mActivity.length() > 1) {
                                    mlist.setOnTouchListener(new View.OnTouchListener() {
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
                                                            ((HotDealActivity) getActivity()).toolbarAnimateHide();
                                                        }
                                                        //시작 Y가 끝 보다 작다면 터치가 위에서 아래로 이러우졌다는 것이고, 스크롤이 올라갔다는 뜻이다.
                                                        else if ((startYPosition < endYPosition) && (endYPosition - startYPosition) > 10) {
                                                            //TODO 스크롤 업 시 작업
                                                            LogUtil.e("xxxxxxx", "up");
                                                            ((HotDealActivity) getActivity()).toolbarAnimateShow();
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
                                getView().findViewById(R.id.bt_scroll).setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            } else {
                                getView().findViewById(R.id.bt_scroll).setVisibility(View.GONE);
                            }
                        }
                    }
                    wrapper.setVisibility(View.GONE);
                } catch (Exception e) {
                    if (isAdded()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        wrapper.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void setLike(final int position, final boolean islike) {
        final String sel_id = mActivityItem.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }
        if (islike) {// 취소
            Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((HotDealActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false, sel_id, "A");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((HotDealActivity) getActivity()).showIconToast("관심 상품 담기 취소", false);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        } else {// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((HotDealActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        TuneWrap.Event("favorite_activity", sel_id);

                        dbHelper.insertFavoriteItem(sel_id, "A");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((HotDealActivity) getActivity()).showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (requestCode == 50 && responseCode == 90) {
            getActivity().setResult(80);
            getActivity().finish();
        } else if (requestCode == 50 && responseCode == 80) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(isFragmentVisible_);
//        if (this.isVisible()) {
        // we check that the fragment is becoming visible
        if (isFragmentVisible_ && !_hasLoadedOnce) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }, 500);

            _hasLoadedOnce = true;
        }
//        }
    }

    private void init() {
        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

        mlist = (ListView) getView().findViewById(R.id.h_list);
        adapter = new HotdaelActivityAdapter(getActivity(), 0, mActivityItem, HotDealActivityFragment.this, dbHelper);
        mlist.setAdapter(adapter);

        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView) view.findViewById(R.id.hid);
                Intent intent = new Intent(getActivity(), DetailActivityActivity.class);
                intent.putExtra("tid", hid.getText().toString());
                intent.putExtra("save", true);
                startActivityForResult(intent, 50);
            }
        });

        wrapper = getView().findViewById(R.id.wrapper);

        getView().findViewById(R.id.bt_scroll).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mlist.smoothScrollToPosition(0);
                ((HotDealActivity) getActivity()).toolbarAnimateShow();
            }
        });

        getData();
    }
}
