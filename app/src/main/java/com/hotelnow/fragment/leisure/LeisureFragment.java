package com.hotelnow.fragment.leisure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.hotelnow.R;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.adapter.ActivityHotDealLeisureAdapter;
import com.hotelnow.adapter.LeisureAdapter;
import com.hotelnow.databinding.FragmentLeisureBinding;
import com.hotelnow.model.ActivityHotDealItem;
import com.hotelnow.model.BannerItem;
import com.hotelnow.model.DefaultItem;
import com.hotelnow.model.RecentCityItem;
import com.hotelnow.model.RecentItem;
import com.hotelnow.model.ThemeItem;
import com.hotelnow.model.ThemeSpecialItem;
import com.hotelnow.model.TopItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeisureFragment extends Fragment {

    private FragmentLeisureBinding mLeisureBinding;
    private ArrayList<Object> objects = new ArrayList<>();
    private String strdate, strdate2;
    private DbOpenHelper dbHelper;
    private String[] FavoriteStayList;
    private String[] FavoriteActivityList;
    public List<RecentItem> mFavoriteStayItem = new ArrayList<>();
    public List<RecentItem> mFavoriteActivityItem = new ArrayList<>();
    private LeisureAdapter adapter;
    public ArrayList<TopItem> mTopItem = new ArrayList<>();
    public ArrayList<ThemeSpecialItem> mThemeSItem = new ArrayList<>();
    public ArrayList<ThemeItem> mThemeItem = new ArrayList<>();
    public ArrayList<DefaultItem> mDefaultItem = new ArrayList<>();
    public ArrayList<ActivityHotDealItem> mActivityItem = new ArrayList<>();
    public ArrayList<BannerItem> mPbanerItem = new ArrayList<>();
    public String sel_location = "", sel_location_id = "", sel_theme = "", sel_theme_id = "";
    private SharedPreferences _preferences;
    private String cookie;
    private String s_Area = "";
    private String s_Area_id = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onResume() {
        super.onResume();

//        mFirebaseAnalytics.setCurrentScreen(getActivity(), "홈_화면3", null);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        mLeisureBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_leisure, container, false);
        View inflate = mLeisureBinding.getRoot();

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        dbHelper = new DbOpenHelper(getActivity());
        objects = new ArrayList<>();

        adapter = new LeisureAdapter(getActivity(), LeisureFragment.this, objects, dbHelper);
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cookie = _preferences.getString("userid", null);

        mLeisureBinding.recyclerView.setAdapter(adapter);
        mLeisureBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFavoriteStayItem = dbHelper.selectAllFavoriteStayItem();
        mFavoriteActivityItem = dbHelper.selectAllFavoriteActivityItem();
        mFavoriteStayItem = dbHelper.selectAllFavoriteStayItem();
        mFavoriteActivityItem = dbHelper.selectAllFavoriteActivityItem();
        if (mFavoriteStayItem.size() > 0) {
            FavoriteStayList = new String[mFavoriteStayItem.size()];
            for (int i = 0; i < mFavoriteStayItem.size(); i++) {
                FavoriteStayList[i] = mFavoriteStayItem.get(i).getSel_id();
            }
        }
        if (mFavoriteActivityItem.size() > 0) {
            FavoriteActivityList = new String[mFavoriteActivityItem.size()];
            for (int i = 0; i < mFavoriteActivityItem.size(); i++) {
                FavoriteActivityList[i] = mFavoriteActivityItem.get(i).getSel_id();
            }
        }

        strdate = Util.setCheckinout().get(0);
        strdate2 = Util.setCheckinout().get(1);

        getObject();

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) getView().findViewById(R.id.swipe);
        swipeView.setColorSchemeColors(R.color.purple, R.color.purple_cc, R.color.green);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.showProgress();
                swipeView.setRefreshing(true);
                objects.clear();
                adapter.notifyDataSetChanged();
                getObject();
                allRefresh();
                swipeView.setRefreshing(false);
            }
        });

    }

    private void getObject() {
        MainActivity.showProgress();
        String url = CONFIG.mainHome + "/activity";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                MainActivity.hideProgress();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        MainActivity.hideProgress();
                        return;
                    }

                    if (dbHelper.selectAllRecentCity("A").size() > 0) {
                        List<RecentCityItem> RecentArea = dbHelper.selectAllRecentCity("A");
                        s_Area = RecentArea.get(0).getSel_city_ko();
                        s_Area_id = RecentArea.get(0).getSel_city_id();
                    } else {
                        s_Area = "서울";
                        s_Area_id = "10000";
                    }

                    mTopItem.clear();
                    mTopItem.add(new TopItem(s_Area, s_Area_id, "", "테마전체", ""));
                    sel_location = "서울";
                    sel_theme = "테마전체";
                    objects.add(mTopItem.get(0));

                    if (obj.has("promotion_banners")) {
                        JSONArray p_banner = new JSONArray(obj.getJSONArray("promotion_banners").toString());
                        if (p_banner.length() > 0) {
                            mPbanerItem.clear();
                            for (int i = 0; i < p_banner.length(); i++) {
                                mPbanerItem.add(new BannerItem(
                                        p_banner.getJSONObject(i).getString("id"),
                                        p_banner.getJSONObject(i).getString("order"),
                                        p_banner.getJSONObject(i).getString("category"),
                                        p_banner.getJSONObject(i).getString("image"),
                                        p_banner.getJSONObject(i).getString("keyword"),
                                        p_banner.getJSONObject(i).getString("type"),
                                        p_banner.getJSONObject(i).getString("evt_type"),
                                        p_banner.getJSONObject(i).getString("event_id"),
                                        p_banner.getJSONObject(i).has("link") ? p_banner.getJSONObject(i).getString("link") : "",
                                        p_banner.getJSONObject(i).getString("title"),
                                        ""
                                ));
                            }
                            objects.add(mPbanerItem.get(0));
                        }
                    }

                    if (obj.has("activity_hot_deals")) {
                        JSONArray mActivity = new JSONArray(obj.getJSONObject("activity_hot_deals").getJSONArray("deals").toString());
                        mActivityItem.clear();
                        if (obj.getJSONObject("activity_hot_deals").getJSONArray("deals").length() > 0) {
                            for (int i = 0; i < mActivity.length(); i++) {
                                mActivityItem.add(new ActivityHotDealItem(
                                        mActivity.getJSONObject(i).getString("id"),
                                        mActivity.getJSONObject(i).getString("name"),
                                        mActivity.getJSONObject(i).getString("sale_price"),
                                        mActivity.getJSONObject(i).getString("sale_rate"),
                                        mActivity.getJSONObject(i).getString("latitude"),
                                        mActivity.getJSONObject(i).getString("longitude"),
                                        mActivity.getJSONObject(i).getString("benefit_text"),
                                        mActivity.getJSONObject(i).getString("img_url"),
                                        mActivity.getJSONObject(i).getString("location"),
                                        mActivity.getJSONObject(i).getString("category_code"),
                                        mActivity.getJSONObject(i).getString("category"),
                                        mActivity.getJSONObject(i).getString("review_score"),
                                        mActivity.getJSONObject(i).getString("grade_score"),
                                        mActivity.getJSONObject(i).getString("is_hot_deal"),
                                        mActivity.getJSONObject(i).getString("is_add_reserve"),
                                        mActivity.getJSONObject(i).has("coupon_count") ? mActivity.getJSONObject(i).getInt("coupon_count") : 0
                                ));
                            }
                            objects.add(mActivityItem.get(0));
                        }
                    }
                    //클릭율이 낮아서 삭제
//                    if (obj.has("theme_show")) {
//                        if (obj.getJSONObject("theme_show").length() > 0) {
//                            JSONObject mTheme_show = obj.getJSONObject("theme_show");
//                            if (mTheme_show.getJSONObject("theme") != null) {
//                                JSONObject mTheme = mTheme_show.getJSONObject("theme");
//                                JSONArray mItems = new JSONArray(mTheme_show.getJSONArray("lists").toString());
//                                mThemeItem.clear();
//                                if (mTheme.getString("theme_flag").equals("H")) { // 호텔일때
//                                    for (int i = 0; i < mItems.length(); i++) {
//                                        mThemeItem.add(new ThemeItem(
//                                                mItems.getJSONObject(i).getString("id"),
//                                                mItems.getJSONObject(i).getString("name"),
//                                                mItems.getJSONObject(i).getString("landscape"),
//                                                mItems.getJSONObject(i).has("product_id") ? mItems.getJSONObject(i).getString("product_id") : "",
//                                                mTheme.getString("id"),
//                                                mTheme.getString("theme_flag"),
//                                                mTheme.getString("theme_color"),
//                                                mTheme.getString("title"),
//                                                mItems.getJSONObject(i).getString("sale_price"),
//                                                mItems.getJSONObject(i).getString("normal_price")
//                                        ));
//                                    }
//                                    if(mThemeItem.size() > 0)
//                                        objects.add(mThemeItem.get(0));
//                                } else if(mTheme.getString("theme_flag").equals("Q")) {
//                                    for (int i = 0; i < mItems.length(); i++) {
//                                        mThemeItem.add(new ThemeItem(
//                                                mItems.getJSONObject(i).getString("id"),
//                                                mItems.getJSONObject(i).getString("name"),
//                                                mItems.getJSONObject(i).getString("landscape"),
//                                                mItems.getJSONObject(i).has("product_id") ? mItems.getJSONObject(i).getString("product_id") : "",
//                                                mTheme.getString("id"),
//                                                mTheme.getString("theme_flag"),
//                                                mTheme.getString("theme_color"),
//                                                mTheme.getString("title"),
//                                                mItems.getJSONObject(i).getString("sale_price"),
//                                                mItems.getJSONObject(i).getString("normal_price")
//                                        ));
//                                    }
//                                    if(mThemeItem.size() > 0)
//                                        objects.add(mThemeItem.get(0));
//                                }
//                            }
//                        }
//                    }
                    if (obj.has("theme_lists")) {
                        JSONArray mThemeS = new JSONArray(obj.getJSONArray("theme_lists").toString());
                        if (mThemeS.length() > 0) {
                            mThemeSItem.clear();
                            for (int i = 0; i < mThemeS.length(); i++) {
                                mThemeSItem.add(new ThemeSpecialItem(
                                        mThemeS.getJSONObject(i).getString("id"),
                                        mThemeS.getJSONObject(i).getString("title"),
                                        mThemeS.getJSONObject(i).getString("sub_title"),
                                        mThemeS.getJSONObject(i).getString("img_main_top"),
                                        mThemeS.getJSONObject(i).getString("img_main_list"),
                                        mThemeS.getJSONObject(i).getString("theme_flag"),
                                        mThemeS.getJSONObject(i).getString("subject"),
                                        mThemeS.getJSONObject(i).getString("detail"),
                                        mThemeS.getJSONObject(i).getString("notice"),
                                        mThemeS.getJSONObject(i).getString("img_background")
                                ));
                            }
                            objects.add(mThemeSItem.get(0));
                        }
                    }
                    mDefaultItem.clear();
                    mDefaultItem.add(new DefaultItem("bottom"));
                    objects.add(mDefaultItem.get(0));

                    adapter.notifyDataSetChanged();
                    MainActivity.hideProgress();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    MainActivity.hideProgress();
                }
            }
        });
    }

    public ArrayList<TopItem> getTopItem() {
        return mTopItem;
    }

    public ArrayList<ActivityHotDealItem> getActivityData() {
        return mActivityItem;
    }

    public ArrayList<ThemeItem> getThemeData() {
        return mThemeItem;
    }

    public ArrayList<ThemeSpecialItem> getThemeSpecialData() {
        return mThemeSItem;
    }

    public ArrayList<DefaultItem> getDefaultItem() {
        return mDefaultItem;
    }

    public ArrayList<BannerItem> getPbannerData() {
        return mPbanerItem;
    }

    public RecyclerView getRecyclerView() {
        return mLeisureBinding.recyclerView;
    }


    public void allRefresh() {
        adapter.setAllRefresh();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 80 && resultCode == 80) {
            mTopItem.clear();
            mTopItem.add(new TopItem(data.getStringExtra("name"), data.getStringExtra("id"), "", sel_theme, sel_theme_id));
            adapter.setHeaderRefresh();
        } else if (requestCode == 60 && resultCode == 80) {
            mTopItem.clear();
            mTopItem.add(new TopItem(sel_location, sel_location_id, "", data.getStringExtra("name"), data.getStringExtra("id")));
            adapter.setHeaderRefresh();
        } else if (requestCode == 70) {
            adapter.setAllRefresh();
//            if (resultCode == 110) {
                ((MainActivity) getActivity()).setTitle();
                ((MainActivity) getActivity()).setTapdelete("MYPAGE");
//            }
        } else if (resultCode == 110) {
            ((MainActivity) getActivity()).setTitle();
            ((MainActivity) getActivity()).setTapdelete("MYPAGE");
        }
        if (resultCode == 100) {
            if (cookie == null) {
                CONFIG.TabLogin = false;
                return;
            }
            ((MainActivity) getActivity()).moveTabReservation();
        }
    }

    public void setActivityLike(final int position, final boolean islike, final ActivityHotDealLeisureAdapter adapter) {
        final String sel_id = getActivityData().get(position).getId();
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
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((MainActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false, sel_id, "A");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 취소", false);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        } else {// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((MainActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id, "A");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        }
    }

    public void setThemeLike(final int position, final boolean islike, final RecyclerView.Adapter adapter) {
        final String sel_id = getThemeData().get(position).getId();
        final String sel_type = getThemeData().get(position).getTheme_flag();
        JSONObject paramObj = new JSONObject();
        try {
            if (sel_type.equals("H")) {
                paramObj.put("type", "stay");
            } else {
                paramObj.put("type", "activity");
            }
            paramObj.put("id", sel_id);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }
        if (islike) {// 취소
            Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((MainActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        if (sel_type.equals("H")) {
                            dbHelper.deleteFavoriteItem(false, sel_id, "H");
                        } else {
                            dbHelper.deleteFavoriteItem(false, sel_id, "A");
                        }
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 취소", false);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        } else {// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((MainActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }
                        if (sel_type.equals("H")) {
                            dbHelper.insertFavoriteItem(sel_id, "H");
                        } else {
                            dbHelper.insertFavoriteItem(sel_id, "A");
                        }
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        }
    }

}
