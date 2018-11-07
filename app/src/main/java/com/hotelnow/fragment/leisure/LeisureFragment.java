package com.hotelnow.fragment.leisure;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.adapter.ActivityHotDealAdapter;
import com.hotelnow.adapter.ActivityHotDealLeisureAdapter;
import com.hotelnow.adapter.HotelAdapter;
import com.hotelnow.adapter.LeisureAdapter;
import com.hotelnow.databinding.FragmentHomeBinding;
import com.hotelnow.databinding.FragmentLeisureBinding;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.model.ActivityHotDealItem;
import com.hotelnow.fragment.model.Banner;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.fragment.model.PrivateDealItem;
import com.hotelnow.fragment.model.RecentItem;
import com.hotelnow.fragment.model.RecentListItem;
import com.hotelnow.fragment.model.SingleHorizontal;
import com.hotelnow.fragment.model.SingleVertical;
import com.hotelnow.fragment.model.StayHotDealItem;
import com.hotelnow.fragment.model.SubBannerItem;
import com.hotelnow.fragment.model.ThemeItem;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.hotelnow.fragment.model.TopItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

        dbHelper = new DbOpenHelper(getActivity());
        objects = new ArrayList<>();

        adapter = new LeisureAdapter(getActivity(), LeisureFragment.this, objects, dbHelper);
        adapter.setHasStableIds(true);

        mLeisureBinding.recyclerView.setAdapter(adapter);
        mLeisureBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFavoriteStayItem = dbHelper.selectAllFavoriteStayItem();
        mFavoriteActivityItem = dbHelper.selectAllFavoriteActivityItem();
        mFavoriteStayItem = dbHelper.selectAllFavoriteStayItem();
        mFavoriteActivityItem = dbHelper.selectAllFavoriteActivityItem();
        if(mFavoriteStayItem.size()>0){
            FavoriteStayList = new String[mFavoriteStayItem.size()];
            for(int i =0; i<mFavoriteStayItem.size();i++){
                FavoriteStayList[i] = mFavoriteStayItem.get(i).getSel_id();
            }
        }
        if(mFavoriteActivityItem.size()>0){
            FavoriteActivityList = new String[mFavoriteActivityItem.size()];
            for(int i =0; i<mFavoriteActivityItem.size();i++){
                FavoriteActivityList[i] = mFavoriteActivityItem.get(i).getSel_id();
            }
        }

        strdate = Util.setCheckinout().get(0);
        strdate2 = Util.setCheckinout().get(1);

        getObject();

    }

    private void getObject() {
        MainActivity.showProgress();
        String url = CONFIG.mainHome+"/activity";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                MainActivity.hideProgress();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mTopItem.clear();
                    mTopItem.add(new TopItem("전체","0","0",strdate, strdate2));
                    objects.add(mTopItem.get(0));

                    if(obj.has("promotion_banners")){
                        JSONArray p_banner = new JSONArray(obj.getJSONArray("promotion_banners").toString());
                        mPbanerItem.clear();
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

                    if(obj.has("activity_hot_deals")){
                        JSONArray mActivity = new JSONArray(obj.getJSONObject("activity_hot_deals").getJSONArray("deals").toString());
                        mActivityItem.clear();
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
                                        mActivity.getJSONObject(i).getString("grade_score"),
                                        mActivity.getJSONObject(i).getString("is_hot_deal"),
                                        mActivity.getJSONObject(i).getString("is_add_reserve"),
                                        mFavoriteActivityItem.size() > 0 && Arrays.asList(FavoriteActivityList).contains(mActivity.getJSONObject(i).getString("id")) ? true : false
                                ));
                            }
                            objects.add(mActivityItem.get(0));
                        }
                    }
                    if(obj.has("theme_show")){
                        if(obj.getJSONObject("theme_show").length() >0) {
                            JSONObject mTheme_show = obj.getJSONObject("theme_show");
                            JSONObject mTheme = mTheme_show.getJSONObject("theme");
                            JSONArray mItems = new JSONArray(mTheme_show.getJSONArray("lists").toString());
                            mTheme.getString("id");
                            mTheme.getString("title");
                            mTheme.getString("sub_title");
                            mTheme.getString("subject");
                            mTheme.getString("detail");
                            mTheme.getString("notice");
                            mThemeItem.clear();
                            for (int i = 0; i < mItems.length(); i++) {
                                mThemeItem.add(new ThemeItem(
                                        mItems.getJSONObject(i).getString("id"),
                                        mItems.getJSONObject(i).getString("name"),
                                        mItems.getJSONObject(i).getString("landscape"),
                                        mItems.getJSONObject(i).has("product_id") ? mItems.getJSONObject(i).getString("product_id") : "",
                                        mTheme.getString("id"),
                                        mItems.getJSONObject(i).has("wo") ? mItems.getJSONObject(i).getString("wo") : "",
                                        mTheme.getString("theme_color"),
                                        mTheme.getString("title"),
                                        mItems.getJSONObject(i).getString("sale_price"),
                                        mItems.getJSONObject(i).getString("normal_price")
                                ));
                            }
                            objects.add(mThemeItem.get(0));
                        }
                    }
                    if(obj.has("theme_lists")){
                        JSONArray mThemeS = new JSONArray(obj.getJSONArray("theme_lists").toString());
                        mThemeSItem.clear();
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
                    mDefaultItem.clear();
                    mDefaultItem.add(new DefaultItem("bottom"));
                    objects.add(mDefaultItem.get(0));

                    adapter.notifyDataSetChanged();
                    MainActivity.hideProgress();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
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

    public RecyclerView getRecyclerView(){
        return mLeisureBinding.recyclerView;
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
        if(requestCode == 80 && resultCode == 80){
            mTopItem.clear();
            mTopItem.add(new TopItem(data.getStringExtra("city"), data.getStringExtra("city_code"), data.getStringExtra("subcity_code"), data.getStringExtra("ec_date"), data.getStringExtra("ee_date")));
            adapter.setHeaderRefresh();
        }
    }

    public void setActivityLike(final int position, final boolean islike, final ActivityHotDealLeisureAdapter adapter){
        final String sel_id = getActivityData().get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch(Exception e){
            Log.e(CONFIG.TAG, e.toString());
        }
        if(islike){// 취소
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
                            Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        getActivityData().get(position).setIslike(!islike);
                        dbHelper.deleteFavoriteTheme(false,  sel_id,"A");
                        LogUtil.e("xxxx", "찜하기 취소");
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
        else{// 성공
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
                            Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getActivityData().get(position).setIslike(!islike);
                        dbHelper.insertFavoriteItem(sel_id,"A");
                        LogUtil.e("xxxx", "찜하기 성공");
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
    }

}
