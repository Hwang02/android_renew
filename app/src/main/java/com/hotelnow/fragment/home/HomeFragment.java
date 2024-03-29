package com.hotelnow.fragment.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.hotelnow.R;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.adapter.HomeAdapter;
import com.hotelnow.databinding.FragmentHomeBinding;
import com.hotelnow.model.ActivityHotDealItem;
import com.hotelnow.model.BannerItem;
import com.hotelnow.model.DefaultItem;
import com.hotelnow.model.KeyWordItem;
import com.hotelnow.model.PrivateDealItem;
import com.hotelnow.model.RecentCityItem;
import com.hotelnow.model.RecentItem;
import com.hotelnow.model.RecentListItem;
import com.hotelnow.model.StayHotDealItem;
import com.hotelnow.model.SubBannerItem;
import com.hotelnow.model.ThemeItem;
import com.hotelnow.model.ThemeSpecialItem;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mHomeBinding;
    private List<Object> objects = null;
    public ArrayList<StayHotDealItem> mHotelItem = new ArrayList<>();
    public ArrayList<PrivateDealItem> mPrivatedealItem = new ArrayList<>();
    public ArrayList<ActivityHotDealItem> mActivityItem = new ArrayList<>();
    public ArrayList<ThemeSpecialItem> mThemeSItem = new ArrayList<>();
    public ArrayList<ThemeItem> mThemeItem = new ArrayList<>();
    public ArrayList<DefaultItem> mDefaultItem = new ArrayList<>();
    public ArrayList<BannerItem> mPbanerItem = new ArrayList<>();
    public ArrayList<SubBannerItem> mEbanerItem = new ArrayList<>();
    public ArrayList<KeyWordItem> mKeywordItem = new ArrayList<>();
    public List<RecentItem> mRecentItem = new ArrayList<>();
    public List<RecentItem> mFavoriteStayItem = new ArrayList<>();
    public List<RecentItem> mFavoriteActivityItem = new ArrayList<>();
    public ArrayList<RecentListItem> mRecentListItem = new ArrayList<>();
    private HomeAdapter adapter;
    private DbOpenHelper dbHelper;
    private String[] FavoriteStayList;
    private String[] FavoriteActivityList;
    public SharedPreferences _preferences;
    private String cookie;
    private int api_count = 0;


    @Override
    public void onResume() {
        super.onResume();

//        mFirebaseAnalytics.setCurrentScreen(getActivity(), "홈_화면1", null);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        mHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View inflate = mHomeBinding.getRoot();

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


        dbHelper = new DbOpenHelper(getActivity());
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cookie = _preferences.getString("userid", null);
        objects = new ArrayList<>();
        adapter = new HomeAdapter(getActivity(), HomeFragment.this, objects, dbHelper);
        mHomeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHomeBinding.recyclerView.setAdapter(adapter);

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

        if (dbHelper.selectAllRecentCity("H").size() > 0) {
            List<RecentCityItem> RecentArea = dbHelper.selectAllRecentCity("H");
            boolean del_city = true;
            if (RecentArea.size() > 0) {
                for (int i = 0; i < RecentArea.size(); i++) {
                    for (int k = 0; k < dbHelper.selectAllSubCityMain().size(); k++) {
                        if (dbHelper.selectAllSubCityMain().get(k).getSubcity_code().equals(RecentArea.get(i).getSel_subcity_id())) {
                            del_city = false;
                            break;
                        }
                    }
                }
            } else {
                del_city = true;
            }

            if (del_city) {
                dbHelper.deleteRecentCity("H");
            }
        }

        if (dbHelper.selectAllRecentCity("A").size() > 0) {
            List<RecentCityItem> RecentArea = dbHelper.selectAllRecentCity("A");
            boolean del_city = true;
            for (int j = 0; j < RecentArea.size(); j++) {
                for (int i = 0; i < dbHelper.selectAllActivityCity().size(); i++) {
                    if (dbHelper.selectAllActivityCity().get(i).getCity_code().equals(RecentArea.get(j).getSel_city_id())) {
                        del_city = false;
                        break;
                    }
                }
                if (del_city) {
                    dbHelper.deleteRecentCity("A");
                }
            }
        }

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) getView().findViewById(R.id.swipe);
        swipeView.setColorSchemeColors(R.color.purple, R.color.purple_cc, R.color.green);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.showProgress();
                swipeView.setRefreshing(true);
                getRecentData(true);
                adapter.allRefresh(true);
                swipeView.setRefreshing(false);
            }
        });

        getRecentData(true);
    }

    public void getRecentData(final boolean isStart) {
        MainActivity.showProgress();
        String url = CONFIG.mainRecent + "/check";
        //최근 본 상품
        mRecentItem = dbHelper.selectAllRecentItem("10");
        if (mRecentItem.size() > 0) {
            try {
                JSONArray jArray = new JSONArray();//배열
                for (int i = 0; i < mRecentItem.size(); i++) {
                    String option = "1";
                    if (mRecentItem.get(i).getSel_option().equals("H")) {
                        option = "1";
                    } else {
                        option = "2";
                    }
                    JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                    sObject.put("flag", option);
                    sObject.put("id", mRecentItem.get(i).getSel_id());
                    jArray.put(sObject);
                }

                LogUtil.e("JSON Test", jArray.toString());
                JSONObject params = new JSONObject();
                params.put("recent_items", jArray.toString());

                Api.post(url, params.toString(), new Api.HttpCallback() {

                    @Override
                    public void onFailure(Response response, Exception throwable) {
                        MainActivity.hideProgress();
                        Activity activity = getActivity();
                        if(activity != null && isAdded()) {
                            Toast.makeText(activity, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);
                            if (!obj.has("result") || !obj.getString("result").equals("success")) {
                                if (obj.has("recent_items")) {
                                    if (obj.getJSONArray("recent_items").length() == 0) {
                                        dbHelper.deleteRecentItem();
                                    }
                                }
                            } else if (obj.has("recent_items")) {
                                mRecentListItem.clear();
                                if (obj.getJSONArray("recent_items").length() > 0) {
                                    JSONArray p_recent = new JSONArray(obj.getJSONArray("recent_items").toString());
                                    for (int i = 0; i < p_recent.length(); i++) {
                                        if (p_recent.getJSONObject(i).getString("flag").equals("1")) {
                                            mRecentListItem.add(new RecentListItem(
                                                    p_recent.getJSONObject(i).getString("flag"),
                                                    p_recent.getJSONObject(i).getString("id"),
                                                    p_recent.getJSONObject(i).getString("name"),
                                                    p_recent.getJSONObject(i).getString("now"),
                                                    p_recent.getJSONObject(i).getString("view_yn"),
                                                    p_recent.getJSONObject(i).getString("img_url")
                                            ));
                                        } else {
                                            mRecentListItem.add(new RecentListItem(
                                                    p_recent.getJSONObject(i).getString("flag"),
                                                    p_recent.getJSONObject(i).getString("id"),
                                                    p_recent.getJSONObject(i).getString("name"),
                                                    p_recent.getJSONObject(i).getString("now"),
                                                    p_recent.getJSONObject(i).getString("view_yn"),
                                                    p_recent.getJSONObject(i).getString("img_url")
                                            ));
                                        }
                                    }
                                }
                            }
                            if (isStart) {
                                // 리스트 호출
                                objects.clear();
                                adapter.notifyDataSetChanged();
                                getObject();
                                CONFIG.isRecent = true;
                            } else {
                                adapter.refreshRecent();
                                MainActivity.hideProgress();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.hideProgress();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.hideProgress();
            }
        } else {
            CONFIG.isRecent = false;
            mRecentListItem.clear();
            adapter.allRefresh(true);
            if (isStart || mRecentItem.size() == 0) {
                objects.clear();
                adapter.notifyDataSetChanged();
                getObject();

            } else {
                MainActivity.hideProgress();
            }
        }
    }

    private void getObject() {
        String url = CONFIG.mainHome + "/home";

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
                        if(isAdded()) {
                            Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.hideProgress();
                        return;
                    }
                    api_count++;
                    if (obj.has("promotion_banners")) {
                        JSONArray p_banner = new JSONArray(obj.getJSONArray("promotion_banners").toString());
                        mPbanerItem.clear();
                        if (p_banner.length() > 0) {
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
                    if (obj.has("popular_keywords")) {
                        JSONArray mKeyword = new JSONArray(obj.getJSONArray("popular_keywords").toString());
                        mKeywordItem.clear();
                        if (mKeyword.length() > 0) {
                            for (int i = 0; i < mKeyword.length(); i++) {
                                mKeywordItem.add(new KeyWordItem(
                                        mKeyword.getJSONObject(i).getString("id"),
                                        mKeyword.getJSONObject(i).getString("order"),
                                        mKeyword.getJSONObject(i).getString("category"),
                                        mKeyword.getJSONObject(i).getString("image"),
                                        mKeyword.getJSONObject(i).getString("keyword"),
                                        mKeyword.getJSONObject(i).getString("type"),
                                        mKeyword.getJSONObject(i).getString("evt_type"),
                                        mKeyword.getJSONObject(i).getString("event_id"),
                                        mKeyword.getJSONObject(i).has("link") ? mKeyword.getJSONObject(i).getString("link") : "",
                                        mKeyword.getJSONObject(i).getString("bannerable_id")
                                ));
                            }
                            objects.add(mKeywordItem.get(0));
                        }
                    }

                    if (mRecentListItem.size() > 0) {
                        objects.add(mRecentListItem.get(0));
                    }

                    if (obj.has("event_banners")) {
                        JSONArray e_banner = new JSONArray(obj.getJSONArray("event_banners").toString());
                        mEbanerItem.clear();
                        if (e_banner.length() > 0) {
                            for (int i = 0; i < e_banner.length(); i++) {
                                mEbanerItem.add(new SubBannerItem(
                                        e_banner.getJSONObject(i).getString("id"),
                                        e_banner.getJSONObject(i).getString("order"),
                                        e_banner.getJSONObject(i).getString("category"),
                                        e_banner.getJSONObject(i).getString("image"),
                                        e_banner.getJSONObject(i).getString("keyword"),
                                        e_banner.getJSONObject(i).getString("type"),
                                        e_banner.getJSONObject(i).getString("evt_type"),
                                        e_banner.getJSONObject(i).getString("event_id"),
                                        e_banner.getJSONObject(i).has("link") ? e_banner.getJSONObject(i).getString("link") : "",
                                        e_banner.getJSONObject(i).getString("title")
                                ));
                            }
                            objects.add(mEbanerItem.get(0));
                        }
                    }
                    if (obj.has("private_deals")) {
                        JSONArray mPrivate = new JSONArray(obj.getJSONArray("private_deals").toString());
                        mPrivatedealItem.clear();
                        if (mPrivate.length() > 0) {
                            for (int i = 0; i < mPrivate.length(); i++) {
                                mPrivatedealItem.add(new PrivateDealItem(
                                        mPrivate.getJSONObject(i).getString("id"),
                                        mPrivate.getJSONObject(i).getString("name"),
                                        mPrivate.getJSONObject(i).getString("category_code"),
                                        mPrivate.getJSONObject(i).getString("category"),
                                        mPrivate.getJSONObject(i).getString("landscape"),
                                        mPrivate.getJSONObject(i).getString("review_score"),
                                        mPrivate.getJSONObject(i).getString("grade_score"),
                                        mPrivate.getJSONObject(i).getString("sale_rate"),
                                        mPrivate.getJSONObject(i).getString("sale_price"),
                                        mPrivate.getJSONObject(i).getString("normal_price"),
                                        mPrivate.getJSONObject(i).getString("is_hot_deal"),
                                        mPrivate.getJSONObject(i).getString("is_add_reserve"),
                                        mPrivate.getJSONObject(i).has("coupon_count") ? mPrivate.getJSONObject(i).getInt("coupon_count") : 0
                                ));
                            }
                            objects.add(mPrivatedealItem.get(0));
                        }
                    }
                    if (obj.has("stay_hot_deals")) {
                        JSONArray mStay = new JSONArray(obj.getJSONObject("stay_hot_deals").getJSONArray("deals").toString());
                        mHotelItem.clear();
                        if (mStay.length() > 0) {
                            for (int i = 0; i < mStay.length(); i++) {
                                mHotelItem.add(new StayHotDealItem(
                                        mStay.getJSONObject(i).getString("id"),
                                        mStay.getJSONObject(i).getString("name"),
                                        mStay.getJSONObject(i).getString("category_code"),
                                        mStay.getJSONObject(i).getString("category"),
                                        mStay.getJSONObject(i).getString("landscape"),
                                        mStay.getJSONObject(i).getString("special_msg"),
                                        mStay.getJSONObject(i).getString("review_score"),
                                        mStay.getJSONObject(i).getString("grade_score"),
                                        mStay.getJSONObject(i).getString("sale_price"),
                                        mStay.getJSONObject(i).getString("normal_price"),
                                        mStay.getJSONObject(i).getString("sale_rate"),
                                        mStay.getJSONObject(i).getString("items_quantity"),
                                        mStay.getJSONObject(i).getString("is_private_deal"),
                                        mStay.getJSONObject(i).getString("is_hot_deal"),
                                        mStay.getJSONObject(i).getString("is_add_reserve"),
                                        mStay.getJSONObject(i).has("coupon_count") ? mStay.getJSONObject(i).getInt("coupon_count") : 0
                                ));
                            }
                            objects.add(mHotelItem.get(0));
                        }
                    }
                    if (obj.has("activity_hot_deals")) {
                        JSONArray mActivity = new JSONArray(obj.getJSONObject("activity_hot_deals").getJSONArray("deals").toString());
                        mActivityItem.clear();
                        if (mActivity.length() > 0) {
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
                        mThemeSItem.clear();
                        if (mThemeS.length() > 0) {
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

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.hideProgress();
                        }
                    }, 500);

                } catch (JSONException e) {
                    e.printStackTrace();
                    MainActivity.hideProgress();
                }
            }
        });
    }

    public ArrayList<StayHotDealItem> getHotelData() {
        return mHotelItem;
    }

    public ArrayList<SubBannerItem> getEbannerData() {
        return mEbanerItem;
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

    public ArrayList<KeyWordItem> getKeywordData() {
        return mKeywordItem;
    }

    public ArrayList<RecentListItem> getRecentListItem() {
        return mRecentListItem;
    }

    public ArrayList<PrivateDealItem> getPrivateDealItem() {
        return mPrivatedealItem;
    }

    public RecyclerView getRecyclerView() {
        return mHomeBinding.recyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void setPrivateLike(final int position, final boolean islike, final RecyclerView.Adapter adapter) {
        final String sel_id = getPrivateDealItem().get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "stay");
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

                        dbHelper.deleteFavoriteItem(false, sel_id, "H");
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

                        dbHelper.insertFavoriteItem(sel_id, "H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        }
        setLikeRefresh(true);
    }

    public void setActivityLike(final int position, final boolean islike, final RecyclerView.Adapter adapter) {
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
        setLikeRefresh(true);
    }

    public void setRecentLike(final int position, final boolean islike, final RecyclerView.Adapter adapter) {
        final String sel_id = getRecentListItem().get(position).getId();
        final String sel_type = getRecentListItem().get(position).getFlag();
        JSONObject paramObj = new JSONObject();
        try {
            if (sel_type.equals("1")) {
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

                        if (sel_type.equals("1")) {
                            dbHelper.deleteFavoriteItem(false, sel_id, "H");
                        } else {
                            dbHelper.deleteFavoriteItem(false, sel_id, "A");
                        }
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 취소", false);
                        setLikeRefresh(true);
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
                        if (sel_type.equals("1")) {
                            dbHelper.insertFavoriteItem(sel_id, "H");
                        } else {
                            dbHelper.insertFavoriteItem(sel_id, "A");
                        }
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 성공", true);
                        setLikeRefresh(true);
                    } catch (JSONException e) {

                    }
                }
            });
        }
    }

    public void setStayLike(final int position, final boolean islike, final RecyclerView.Adapter adapter) {
        final String sel_id = getHotelData().get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "stay");
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

                        dbHelper.deleteFavoriteItem(false, sel_id, "H");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 취소", false);
                        setLikeRefresh(true);
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

                        dbHelper.insertFavoriteItem(sel_id, "H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((MainActivity) getActivity()).showIconToast("관심 상품 담기 성공", true);
                        setLikeRefresh(true);
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
                        setLikeRefresh(true);
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
                        setLikeRefresh(true);
                    } catch (JSONException e) {

                    }
                }
            });
        }
    }

    public void setLikeRefresh(boolean isRecent) {
        adapter.allRefresh(isRecent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 80) {
            mFavoriteStayItem = dbHelper.selectAllFavoriteStayItem();
            mFavoriteActivityItem = dbHelper.selectAllFavoriteActivityItem();
            if (resultCode == 100) {
                if (cookie == null) {
                    CONFIG.TabLogin = false;
                    return;
                }
                ((MainActivity) getActivity()).moveTabReservation();
            } else if (resultCode == 110) {
                ((MainActivity) getActivity()).setTitle();
                ((MainActivity) getActivity()).setTapdelete("MYPAGE");
            }
            if (getRecentListItem().size() > 0) {
                getRecentData(false);
                if (resultCode == 80) {
                    setLikeRefresh(false);
                }
            } else {
                getRecentData(true);
            }

            if (resultCode == 200){
                // 프라이빗 딜 전체보기 후 호텔 탭 이동
                ((MainActivity) getActivity()).setTapMove(5, true);
            }
        }
        else if (requestCode == 200){
            // 프라이빗 딜 전체보기 후 호텔 탭 이동
            ((MainActivity) getActivity()).setTapMove(5, true);
        }
    }
}
