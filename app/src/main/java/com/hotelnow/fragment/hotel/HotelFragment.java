package com.hotelnow.fragment.hotel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.hotelnow.adapter.HotelAdapter;
import com.hotelnow.databinding.FragmentHotelBinding;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.fragment.model.DefaultItem;
import com.hotelnow.fragment.model.PrivateDealItem;
import com.hotelnow.fragment.model.RecentCityItem;
import com.hotelnow.fragment.model.RecentItem;
import com.hotelnow.fragment.model.StayHotDealItem;
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

public class HotelFragment extends Fragment {

    private FragmentHotelBinding mHotelBinding;
    private ArrayList<Object> objects = new ArrayList<>();
    private DbOpenHelper dbHelper;
    public ArrayList<TopItem> mTopItem = new ArrayList<>();
    public ArrayList<StayHotDealItem> mHotelItem = new ArrayList<>();
    public ArrayList<PrivateDealItem> mPrivatedealItem = new ArrayList<>();
    public ArrayList<ThemeSpecialItem> mThemeSItem = new ArrayList<>();
    public ArrayList<ThemeItem> mThemeItem = new ArrayList<>();
    public ArrayList<DefaultItem> mDefaultItem = new ArrayList<>();
    public ArrayList<BannerItem> mPbanerItem = new ArrayList<>();
    public List<RecentItem> mFavoriteStayItem = new ArrayList<>();
    public List<RecentItem> mFavoriteActivityItem = new ArrayList<>();
    private HotelAdapter adapter;
    private String[] FavoriteStayList;
    private String[] FavoriteActivityList;
    private String strdate, strdate2;
    private SharedPreferences _preferences;
    private String cookie;
    private  String s_Area = "";
    private String s_Area_id = "";
    private String s_subArea_id = "";

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        mHotelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_hotel, container, false);
        View inflate = mHotelBinding.getRoot();

        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = new DbOpenHelper(getActivity());
        objects = new ArrayList<>();

        adapter = new HotelAdapter(getActivity(), HotelFragment.this, objects, dbHelper);
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cookie = _preferences.getString("userid", null);

        mHotelBinding.recyclerView.setAdapter(adapter);
        mHotelBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        String url = CONFIG.mainHome+"/stay";

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
                        MainActivity.hideProgress();
                        return;
                    }

                    if(dbHelper.selectAllRecentCity("H").size()>0) {
                        List<RecentCityItem> RecentArea = dbHelper.selectAllRecentCity("H");
                        s_Area = RecentArea.get(0).getSel_subcity_ko();
                        s_Area_id = RecentArea.get(0).getSel_city_id();
                        s_subArea_id = RecentArea.get(0).getSel_subcity_id();
                    }
                    else{
                        s_Area = "서울전체";
                        s_Area_id = "100_seoul";
                        s_subArea_id = "100_seoul";
                    }

                    mTopItem.clear();
                    mTopItem.add(new TopItem(s_Area, s_Area_id, s_subArea_id, strdate, strdate2));
                    objects.add(mTopItem.get(0));

                    if(obj.has("promotion_banners")){
                        JSONArray p_banner = new JSONArray(obj.getJSONArray("promotion_banners").toString());
                        if(p_banner.length()>0) {
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

                    if(obj.has("private_deals")){
                        JSONArray mPrivate = new JSONArray(obj.getJSONArray("private_deals").toString());
                        mPrivatedealItem.clear();
                        if(mPrivate.length()>0) {
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

                    if(obj.has("stay_hot_deals")){
                        JSONArray mStay = new JSONArray(obj.getJSONObject("stay_hot_deals").getJSONArray("deals").toString());
                        mHotelItem.clear();
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

                    if(obj.has("theme_show")){
                        if(obj.getJSONObject("theme_show").length() >0) {
                            JSONObject mTheme_show = obj.getJSONObject("theme_show");
                            if (mTheme_show.getJSONObject("theme") != null) {
                                JSONObject mTheme = mTheme_show.getJSONObject("theme");
                                JSONArray mItems = new JSONArray(mTheme_show.getJSONArray("lists").toString());

                                mThemeItem.clear();
                                for (int i = 0; i < mItems.length(); i++) {
                                    mThemeItem.add(new ThemeItem(
                                            mItems.getJSONObject(i).getString("id"),
                                            mItems.getJSONObject(i).getString("name"),
                                            mItems.getJSONObject(i).getString("landscape"),
                                            mItems.getJSONObject(i).has("product_id") ? mItems.getJSONObject(i).getString("product_id") : "",
                                            mTheme.getString("id"),
                                            mTheme.getString("theme_flag"),
                                            mTheme.getString("theme_color"),
                                            mTheme.getString("title"),
                                            mItems.getJSONObject(i).getString("sale_price"),
                                            mItems.getJSONObject(i).getString("normal_price")
                                    ));
                                }
                                if (mThemeItem.size() > 0)
                                    objects.add(mThemeItem.get(0));
                            }
                        }
                    }
                    if(obj.has("theme_lists")){
                        JSONArray mThemeS = new JSONArray(obj.getJSONArray("theme_lists").toString());
                        if(mThemeS.length() >0) {
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
                    MainActivity.hideProgress();
                }
            }
        });
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

    public ArrayList<TopItem> getTopItem() {
        return mTopItem;
    }
    public ArrayList<StayHotDealItem> getHotelData() {
        return mHotelItem;
    }
    public ArrayList<BannerItem> getPbannerData() {
        return mPbanerItem;
    }

    public ArrayList<PrivateDealItem> getPrivateDealItem() {
        return mPrivatedealItem;
    }

    public RecyclerView getRecyclerView(){
        return mHotelBinding.recyclerView;
    }

    public void setStayLike(final int position, final boolean islike, final RecyclerView.Adapter adapter){
        final String sel_id = getHotelData().get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "stay");
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
                            ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false,  sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((MainActivity)getActivity()).showIconToast("관심 상품 담기 취소", false);
                        allRefresh();
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
                            ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((MainActivity)getActivity()).showIconToast("관심 상품 담기 성공", true);
                        allRefresh();
                    }catch (JSONException e){

                    }
                }
            });
        }
    }

    public void setPrivateLike(final int position, final boolean islike, final RecyclerView.Adapter adapter){
        final String sel_id = getPrivateDealItem().get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "stay");
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
                            ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }
                        dbHelper.deleteFavoriteItem(false,  sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((MainActivity)getActivity()).showIconToast("관심 상품 담기 취소", false);
                        allRefresh();
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
                            ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }
                        dbHelper.insertFavoriteItem(sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((MainActivity)getActivity()).showIconToast("관심 상품 담기 성공", true);
                        allRefresh();
                    }catch (JSONException e){

                    }
                }
            });
        }
    }

    public void setThemeLike(final int position, final boolean islike, final RecyclerView.Adapter adapter){
        final String sel_id = getThemeData().get(position).getId();
        final String sel_type = getThemeData().get(position).getTheme_flag();
        JSONObject paramObj = new JSONObject();
        try {
            if(sel_type.equals("H")) {
                paramObj.put("type", "stay");
            }
            else{
                paramObj.put("type", "activity");
            }
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
                            ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        if(sel_type.equals("H")) {
                            dbHelper.deleteFavoriteItem(false, sel_id, "H");
                        }
                        else{
                            dbHelper.deleteFavoriteItem(false, sel_id, "A");
                        }
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((MainActivity)getActivity()).showIconToast("관심 상품 담기 취소", false);
                        allRefresh();
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
                            ((MainActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }
                        if(sel_type.equals("H")) {
                            dbHelper.insertFavoriteItem(sel_id, "H");
                        }
                        else{
                            dbHelper.insertFavoriteItem(sel_id, "A");
                        }
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((MainActivity)getActivity()).showIconToast("관심 상품 담기 성공", true);
                        allRefresh();
                    }catch (JSONException e){

                    }
                }
            });
        }
    }

    public void allRefresh(){
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
        if(requestCode == 80 && resultCode == 80){
            if(data != null) {
                mTopItem.clear();
                mTopItem.add(new TopItem(data.getStringExtra("city"), data.getStringExtra("city_code"), data.getStringExtra("subcity_code"), data.getStringExtra("ec_date"), data.getStringExtra("ee_date")));
                adapter.setHeaderRefresh();
            }
        }
        else if(requestCode == 70){
            adapter.setAllRefresh();
        }
        else if(resultCode == 110){
            ((MainActivity)getActivity()).setTitle();
            ((MainActivity)getActivity()).setTapdelete("MYPAGE");
        }
        if(resultCode == 100){
            if(cookie == null) {
                CONFIG.TabLogin=false;
                return;
            }
            ((MainActivity) getActivity()).moveTabReservation();
        }
    }
}
