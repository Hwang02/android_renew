package com.hotelnow.fragment.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.adapter.HomeAdapter;
import com.hotelnow.databinding.FragmentHomeBinding;
import com.hotelnow.fragment.model.ActivityHotDealItem;
import com.hotelnow.fragment.model.Banner;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.fragment.model.RecentItem;
import com.hotelnow.fragment.model.RecentListItem;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.fragment.model.SubBannerItem;
import com.hotelnow.fragment.model.ThemeItem;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mHomeBinding;
    private List<Object> objects = null;
    public ArrayList<StayHotDealItem> mHotelItem = new ArrayList<>();
    public ArrayList<ActivityHotDealItem> mActivityItem = new ArrayList<>();
    public ArrayList<ThemeSpecialItem> mThemeSItem = new ArrayList<>();
    public ArrayList<ThemeItem> mThemeItem = new ArrayList<>();
    public ArrayList<DefaultItem> mDefaultItem = new ArrayList<>();
    public ArrayList<BannerItem> mPbanerItem = new ArrayList<>();
    public ArrayList<SubBannerItem> mEbanerItem = new ArrayList<>();
    public ArrayList<KeyWordItem> mKeywordItem = new ArrayList<>();
    public List<RecentItem> mRecentItem = new ArrayList<>();
    public ArrayList<RecentListItem> mRecentListItem = new ArrayList<>();
    private HomeAdapter adapter;
    private DbOpenHelper dbHelper;

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

        dbHelper = new DbOpenHelper(getActivity());
        objects = new ArrayList<>();
        adapter = new HomeAdapter(getActivity(), HomeFragment.this, objects, dbHelper);
        mHomeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHomeBinding.recyclerView.setAdapter(adapter);

        getRecentData(true);

    }

    public void getRecentData(final boolean isStart){
        String url = CONFIG.mainRecent;
        //최근 본 상품
        mRecentItem = dbHelper.selectAllRecentItem();
        try {
            JSONArray jArray = new JSONArray();//배열
            for (int i = 0; i < mRecentItem.size(); i++) {
                String option = "1";
                if(mRecentItem.get(i).getSel_option().equals("H")){
                    option = "1";
                }
                else{
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
                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("true")) {
                            Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(obj.has("recent_items")){
                            mRecentListItem.clear();
                            JSONArray p_recent = new JSONArray(obj.getJSONArray("recent_items").toString());
                            for(int i = 0; i < p_recent.length(); i++){
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
                        if(isStart) {
                            // 리스트 호출
                            getObject();
                        }
                        else{
                            adapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getObject() {
        String url = CONFIG.mainHome;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(obj.has("promotion_banners")){
                        JSONArray p_banner = new JSONArray(obj.getJSONArray("promotion_banners").toString());
                        for(int i = 0; i < p_banner.length(); i++){
                            mPbanerItem.add(new BannerItem(
                                    p_banner.getJSONObject(i).getString("id"),
                                    p_banner.getJSONObject(i).getString("order"),
                                    p_banner.getJSONObject(i).getString("category"),
                                    p_banner.getJSONObject(i).getString("image"),
                                    p_banner.getJSONObject(i).getString("keyword"),
                                    p_banner.getJSONObject(i).getString("type"),
                                    p_banner.getJSONObject(i).getString("evt_type"),
                                    p_banner.getJSONObject(i).getString("event_id"),
                                    p_banner.getJSONObject(i).has("link") ? p_banner.getJSONObject(i).getString("link") : ""
                            ));
                        }
                        objects.add(mPbanerItem.get(0));
                    }
                    if(obj.has("popular_keywords")){
                        JSONArray mKeyword = new JSONArray(obj.getJSONArray("popular_keywords").toString());
                        for(int i = 0; i < mKeyword.length(); i++){
                            mKeywordItem.add(new KeyWordItem(
                                    mKeyword.getJSONObject(i).getString("id"),
                                    mKeyword.getJSONObject(i).getString("order"),
                                    mKeyword.getJSONObject(i).getString("category"),
                                    mKeyword.getJSONObject(i).getString("image"),
                                    mKeyword.getJSONObject(i).getString("keyword"),
                                    mKeyword.getJSONObject(i).getString("type"),
                                    mKeyword.getJSONObject(i).getString("evt_type"),
                                    mKeyword.getJSONObject(i).getString("event_id"),
                                    mKeyword.getJSONObject(i).has("link") ? mKeyword.getJSONObject(i).getString("link") : ""
                            ));
                        }
                        objects.add(mKeywordItem.get(0));
                    }
                    if(mRecentListItem.size()>0){
                        objects.add(mRecentListItem.get(0));
                    }
                    if(obj.has("event_banners")){
                        JSONArray e_banner = new JSONArray(obj.getJSONArray("event_banners").toString());
                        for(int i = 0; i < e_banner.length(); i++){
                            mEbanerItem.add(new SubBannerItem(
                                    e_banner.getJSONObject(i).getString("id"),
                                    e_banner.getJSONObject(i).getString("order"),
                                    e_banner.getJSONObject(i).getString("category"),
                                    e_banner.getJSONObject(i).getString("image"),
                                    e_banner.getJSONObject(i).getString("keyword"),
                                    e_banner.getJSONObject(i).getString("type"),
                                    e_banner.getJSONObject(i).getString("evt_type"),
                                    e_banner.getJSONObject(i).getString("event_id"),
                                    e_banner.getJSONObject(i).has("link") ? e_banner.getJSONObject(i).getString("link") : ""
                            ));
                        }
                        objects.add(mEbanerItem.get(0));
                    }
                    if(obj.has("stay_hot_deals")){
                        JSONArray mStay = new JSONArray(obj.getJSONObject("stay_hot_deals").getJSONArray("deals").toString());
                        if(obj.getJSONObject("stay_hot_deals").getJSONArray("deals").length()>0) {
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
                                        mStay.getJSONObject(i).getString("items_quantity")
                                ));
                            }
                            objects.add(mHotelItem.get(0));
                        }
                    }
                    if(obj.has("activity_hot_deals")){
                        JSONArray mActivity = new JSONArray(obj.getJSONObject("activity_hot_deals").getJSONArray("deals").toString());
                        if(obj.getJSONObject("activity_hot_deals").getJSONArray("deals").length()>0) {
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
                                        mActivity.getJSONObject(i).getString("grade_score")
                                ));
                            }
                            objects.add(mActivityItem.get(0));
                        }
                    }
                    if(obj.has("theme_show")){
                        JSONObject mTheme_show = obj.getJSONObject("theme_show");
                        JSONObject mTheme = mTheme_show.getJSONObject("theme");
                        JSONArray mItems = new JSONArray(mTheme_show.getJSONArray("lists").toString());
                        mTheme.getString("id");
                        mTheme.getString("title");
                        mTheme.getString("sub_title");
                        mTheme.getString("filter_start_color");
                        mTheme.getString("subject");
                        mTheme.getString("detail");
                        mTheme.getString("notice");

                        for(int i = 0; i < mItems.length(); i++){
                            mThemeItem.add(new ThemeItem(
                                    mItems.getJSONObject(i).getString("id"),
                                    mItems.getJSONObject(i).getString("name"),
                                    mItems.getJSONObject(i).getString("landscape"),
                                    mItems.getJSONObject(i).getString("product_id"),
                                    mTheme.getString("id"),
                                    mItems.getJSONObject(i).getString("wo"),
                                    mTheme.getString("filter_start_color")
                            ));
                        }
                        objects.add(mThemeItem.get(0));
                    }
                    if(obj.has("theme_lists")){
                        JSONArray mThemeS = new JSONArray(obj.getJSONArray("theme_lists").toString());
                        for(int i = 0; i < mThemeS.length(); i++){
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

                    mDefaultItem.add(new DefaultItem("bottom"));
                    objects.add(mDefaultItem.get(0));

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
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

    public RecyclerView getRecyclerView(){
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

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 80){
//
//        }
//    }
}
