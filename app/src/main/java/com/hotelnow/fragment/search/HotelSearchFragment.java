package com.hotelnow.fragment.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.activity.AreaHotelActivity;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.FilterHotelActivity;
import com.hotelnow.activity.MapHotelActivity;
import com.hotelnow.activity.SearchActivity;
import com.hotelnow.activity.SearchResultActivity;
import com.hotelnow.adapter.SearchBannerPagerAdapter;
import com.hotelnow.adapter.SearchResultStayAdapter;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FlowLayout;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.hotelnow.utils.ViewPagerCustom;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HotelSearchFragment extends Fragment {

    private SharedPreferences _preferences;
    private ListView mlist;
    private View EmptyView;
    private View HeaderView;
    private ImageView map_img;
    private TextView tv_review_count;
    private RelativeLayout btn_location, btn_date;
    private ArrayList<SearchResultItem> mItems = new ArrayList<>();
    private SearchResultStayAdapter adapter;
    private String banner_id, search_txt;
    private LinearLayout btn_filter;
    private int Page = 1;
    private int total_count;
    private String s_position = "",city = "",sub_city = "";
    private DbOpenHelper dbHelper;
    private TextView tv_location, tv_date, page;
    private String ec_date ="", ee_date="";
    private Button bt_scroll;
    private String category ="", facility="", price_min="", person_count="", price_max="", order_kind="", score="";
    private RelativeLayout bannerview;
    private ArrayList<BannerItem> mBannerItems = new ArrayList<>();
    private ViewPagerCustom autoViewPager;
    private SearchBannerPagerAdapter bannerAdapter;
    public static int markNowPosition = 0;
    private static int PAGES = 0;
    private static int nowPosition = 0;
    private FlowLayout popular_keyword;
    private List<KeyWordItem> mKeywordList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_h_result, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

        search_txt = getArguments().getString("search_txt");
        banner_id = getArguments().getString("banner_id");
        order_kind = getArguments().getString("order_kind");

        mlist = (ListView) getView().findViewById(R.id.h_list);
        HeaderView = getLayoutInflater().inflate(R.layout.layout_search_map_filter_header, null, false);
        btn_location = (RelativeLayout) HeaderView.findViewById(R.id.btn_location);
        btn_date = (RelativeLayout)HeaderView.findViewById(R.id.btn_date);
        tv_review_count = (TextView) HeaderView.findViewById(R.id.tv_review_count);
        map_img = (ImageView) HeaderView.findViewById(R.id.map_img);
        tv_location = (TextView) HeaderView.findViewById(R.id.tv_location);
        tv_date = (TextView) HeaderView.findViewById(R.id.tv_date);
        bannerview = (RelativeLayout) HeaderView.findViewById(R.id.bannerview);
        autoViewPager = (ViewPagerCustom) HeaderView.findViewById(R.id.autoViewPager);
        page = (TextView) HeaderView.findViewById(R.id.page);
        btn_filter = (LinearLayout) getView().findViewById(R.id.btn_filter);
        bt_scroll = (Button)getView().findViewById(R.id.bt_scroll);

        View empty = getLayoutInflater().inflate(R.layout.layout_search_empty, null, false);
        popular_keyword = (FlowLayout) empty.findViewById(R.id.filter1);

        ((ViewGroup)mlist.getParent()).addView(empty);
        mlist.setEmptyView(empty);

        ec_date = Util.setCheckinout().get(0);
        ee_date = Util.setCheckinout().get(1);

        long count = Util.diffOfDate2(ec_date, ee_date);
        tv_date.setText(Util.formatchange5(ec_date)+" - "+Util.formatchange5(ee_date)+", "+count+"박");

        mlist.addHeaderView(HeaderView);
        adapter = new SearchResultStayAdapter(getActivity(), 0, mItems, HotelSearchFragment.this, dbHelper);
        mlist.setAdapter(adapter);

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AreaHotelActivity.class);
                startActivityForResult(intent, 80);
            }
        });
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                intent.putExtra("ec_date", ec_date);
                intent.putExtra("ee_date", ee_date);
                startActivityForResult(intent, 70);
            }
        });
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FilterHotelActivity.class);
                startActivityForResult(intent, 60);
            }
        });
        bt_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlist.smoothScrollToPosition(0);
            }
        });

        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView)view.findViewById(R.id.hid);
                Intent intent = new Intent(getActivity(), DetailHotelActivity.class);
                intent.putExtra("hid", hid.getText().toString());
                intent.putExtra("save", true);
                startActivityForResult(intent, 50);
            }
        });

        getSearch();

    }

    public void getSearch(){
        String url = CONFIG.search_stay_list;
        if(!TextUtils.isEmpty(city)){
            url +="&city="+city;
        }
        if(!TextUtils.isEmpty(sub_city)){
            url +="&sub_city="+sub_city;
        }
        if (!TextUtils.isEmpty(search_txt)) {
            url += "&search_text=" + search_txt;
        }
        if(!TextUtils.isEmpty(banner_id)){
            url +="&banner_id="+banner_id;
        }
        if(!TextUtils.isEmpty(ec_date)){
            url +="&ec_date="+ec_date;
        }
        if(!TextUtils.isEmpty(ee_date)){
            url +="&ee_date="+ee_date;
        }
        if(!TextUtils.isEmpty(category)){
            url +="&category="+category;
        }
        if(!TextUtils.isEmpty(facility)){
            url +="&facility="+facility;
        }
        if(!TextUtils.isEmpty(price_min)){
            url +="&price_min="+price_min;
        }
        if(!TextUtils.isEmpty(person_count)){
            url +="&person_count="+person_count;
        }
        if(!TextUtils.isEmpty(price_max)){
            url +="&price_max="+price_max;
        }
        if(!TextUtils.isEmpty(score)){
            url +="&score="+score;
        }
        if(!TextUtils.isEmpty(order_kind)){
            url +="&order_kind="+order_kind;
            if(order_kind.equals("distance")){
                url +="&lat="+CONFIG.lat+"&lng="+CONFIG.lng;
            }
        }

        url +="&per_page=20";

        if(Page < 2 || total_count != mItems.size()) {
            Api.get(url + "&page=" + Page, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(isAdded()) {
                            final JSONArray list = obj.getJSONArray("lists");
                            final JSONArray bannerlist = obj.getJSONArray("region_banners");
                            JSONObject entry = null;
                            JSONObject bannerentry = null;

                            final String total_cnt = "총 " + obj.getString("total_count") + "개의 객실이 있습니다";
                            SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
                            builder.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.purple)), 2, 2 + obj.getString("total_count").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_review_count.setText(builder);

                            if (bannerlist.length() > 0) {
                                mBannerItems.clear();
                                bannerview.setVisibility(View.VISIBLE);
                                for (int j = 0; j < bannerlist.length(); j++) {
                                    bannerentry = bannerlist.getJSONObject(j);
                                    mBannerItems.add(new BannerItem(
                                            "",
                                            "",
                                            "",
                                            bannerentry.getString("image"),
                                            "",
                                            "",
                                            bannerentry.getString("evt_type"),
                                            bannerentry.getString("event_id"),
                                            bannerentry.getString("link")
                                    ));
                                }

                                PAGES = mBannerItems.size();
                                bannerAdapter = new SearchBannerPagerAdapter(getActivity(), mBannerItems);
                                autoViewPager.setClipToPadding(false);
                                autoViewPager.setOffscreenPageLimit(mBannerItems.size());
                                autoViewPager.setPageMargin(20);
                                autoViewPager.setAdapter(bannerAdapter); //Auto Viewpager에 Adapter 장착
                                autoViewPager.setCurrentItem(mBannerItems.size() * 10);
                                autoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                        autoViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        nowPosition = position;
                                        markNowPosition = position % PAGES;
                                        page.setText(markNowPosition + 1 + " / " + PAGES + " +");
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });
                                page.setText("1 / " + mBannerItems.size() + " +");

                                autoViewPager.startAutoScroll();
                            } else {
                                bannerview.setVisibility(View.GONE);
                                autoViewPager.stopAutoScroll();
                            }

                            final JSONArray popular_keywords = obj.getJSONArray("popular_keywords");
                            mKeywordList.clear();
                            for (int i = 0; i < popular_keywords.length(); i++) {
                                mKeywordList.add(new KeyWordItem(
                                        popular_keywords.getJSONObject(i).getString("id"),
                                        popular_keywords.getJSONObject(i).getString("order"),
                                        popular_keywords.getJSONObject(i).getString("category"),
                                        popular_keywords.getJSONObject(i).getString("image"),
                                        popular_keywords.getJSONObject(i).getString("keyword"),
                                        popular_keywords.getJSONObject(i).getString("type"),
                                        popular_keywords.getJSONObject(i).getString("evt_type"),
                                        popular_keywords.getJSONObject(i).getString("event_id"),
                                        popular_keywords.getJSONObject(i).has("link") ? popular_keywords.getJSONObject(i).getString("link") : "",
                                        popular_keywords.getJSONObject(i).getString("bannerable_id")
                                ));
                            }

                            setPopular();

                            for (int i = 0; i < list.length(); i++) {
                                entry = list.getJSONObject(i);
                                mItems.add(new SearchResultItem(
                                        entry.getString("id"),
                                        entry.getString("hotel_id"),
                                        entry.getString("name"),
                                        entry.getString("address"),
                                        entry.getString("category"),
                                        entry.getString("street1"),
                                        entry.getString("street2"),
                                        entry.getDouble("latitude"),
                                        entry.getDouble("longuitude"),
                                        entry.getString("privateDealYN"),
                                        entry.getString("landscape"),
                                        entry.getString("sale_price"),
                                        entry.getString("normal_price"),
                                        entry.getString("sale_rate"),
                                        entry.getInt("items_quantity"),
                                        entry.getString("special_msg"),
                                        entry.getString("review_score"),
                                        entry.getString("grade_score"),
                                        entry.getString("real_grade_score"),
                                        entry.getString("distance"),
                                        entry.getString("distance_real"),
                                        entry.getString("normal_price_avg"),
                                        entry.getString("city"),
                                        entry.getString("is_private_deal"),
                                        entry.getString("is_hot_deal"),
                                        entry.getString("is_add_reserve"),
                                        entry.getInt("coupon_count"),
                                        i == 0 ? true : false
                                ));
                                if (Page == 1)
                                    s_position += "&markers=icon:http://hotelnow.s3.amazonaws.com/etc/20181101_173010_uXfZWjNIzK.png%7C" + entry.getString("latitude") + "%2C" + entry.getString("longuitude");
                            }

                            if (mItems.size() > 0) {
                                btn_filter.setVisibility(View.VISIBLE);
                                bt_scroll.setVisibility(View.VISIBLE);
                            } else {
                                btn_filter.setVisibility(View.GONE);
                                bt_scroll.setVisibility(View.GONE);
                            }

                            String mapStr = "https://maps.googleapis.com/maps/api/staticmap?" +
                                    s_position +
                                    "&scale=2&sensor=false&language=ko&size=360x130" + "&key=" + BuildConfig.google_map_key2;
                            Ion.with(map_img).load(mapStr);

                            map_img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), MapHotelActivity.class);
                                    intent.putExtra("search_data", mItems);
                                    intent.putExtra("Page", Page);
                                    intent.putExtra("total_count", total_count);
                                    intent.putExtra("city", city);
                                    intent.putExtra("city_name", tv_location.getText().toString());
                                    intent.putExtra("sub_city", sub_city);
                                    intent.putExtra("search_txt", search_txt);
                                    intent.putExtra("banner_id", banner_id);
                                    intent.putExtra("ec_date", ec_date);
                                    intent.putExtra("ee_date", ee_date);
                                    intent.putExtra("category", category);
                                    intent.putExtra("facility", facility);
                                    intent.putExtra("price_min", price_min);
                                    intent.putExtra("person_count", person_count);
                                    intent.putExtra("price_max", price_max);
                                    intent.putExtra("score", score);
                                    intent.putExtra("order_kind", order_kind);
                                    if (order_kind.equals("distance")) {
                                        intent.putExtra("lat", CONFIG.lat);
                                        intent.putExtra("lng", CONFIG.lng);
                                    }
                                    startActivityForResult(intent, 90);
                                }
                            });


                            total_count = obj.getInt("total_count");
                            adapter.notifyDataSetChanged();
                            Page++;
                        }
                    } catch (Exception e) {
                        if(isAdded()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void setPopular(){
        popular_keyword.removeAllViews();
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_pressed}, new int[]{-android.R.attr.state_pressed}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<mKeywordList.size();i++){
            TextView tv = new TextView(getActivity());
            tv.setId(i);
            tv.setTag(i);
            tv.setText(mKeywordList.get(i).getKeyword());
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setTextColor(getResources().getColor(R.color.termtext));
            tv.setGravity(Gravity.LEFT);
            tv.setBackgroundResource(R.drawable.style_checkbox_keyword);
//            tv.setButtonDrawable(android.R.color.transparent);
            tv.setTextColor(myColorStateList);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    banner_id = mKeywordList.get((int)v.getTag()).getId();
                    setClear();
                }
            });

            popular_keyword.addView(tv);
        }
    }

    public void setClear(){
        Page =1;
        total_count = 0;
        ec_date = Util.setCheckinout().get(0);
        ee_date = Util.setCheckinout().get(1);
        city = "";
        sub_city="";
        price_max="600000";
        price_min="0";
        category = "";
        facility = "";
        tv_location.setText("지역선택");
        search_txt = "";
        long count = Util.diffOfDate2(ec_date, ee_date);
        tv_date.setText(Util.formatchange5(ec_date)+" - "+Util.formatchange5(ee_date)+", "+count+"박");

        mItems.clear();
        getSearch();
    }

    public void setLike(final int position, final boolean islike){
        final String sel_id = mItems.get(position).getId();
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
                            ((SearchResultActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false,  sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((SearchResultActivity)getActivity()).showIconToast("관심 상품 담기 취소", false);
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
                            ((SearchResultActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((SearchResultActivity)getActivity()).showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if(requestCode == 90 && responseCode == 90) {
            getActivity().setResult(80);
            getActivity().finish();
        }
        if(requestCode == 80 && responseCode == 80){
            tv_location.setText(data.getStringExtra("city"));
            city = data.getStringExtra("city_code");
            sub_city = data.getStringExtra("subcity_code");
            if(city.equals(sub_city)){
                sub_city = "";
            }
            Page = 1;
            total_count = 0;
            mItems.clear();
            getSearch();
        } else if(requestCode == 70 && responseCode == 80){
            ec_date = data.getStringExtra("ec_date");
            ee_date = data.getStringExtra("ee_date");
            long gap = Util.diffOfDate2(ec_date, ee_date);
            tv_date.setText(Util.formatchange5(ec_date)+" - "+Util.formatchange5(ee_date)+", "+gap+"박");
            Page = 1;
            total_count = 0;
            mItems.clear();
            getSearch();
        }
        else if(requestCode == 60 && responseCode == 80){

            LogUtil.e("xxxxx", CONFIG.sel_max);
            price_max = CONFIG.sel_max;
            LogUtil.e("xxxxx", CONFIG.sel_min);
            price_min = CONFIG.sel_min;
            if(!TextUtils.isEmpty(CONFIG.sel_category)) {
                LogUtil.e("xxxxx", CONFIG.sel_category);
                category = CONFIG.sel_category.replace("|",",");
            }
            if(!TextUtils.isEmpty(CONFIG.sel_facility)) {
                LogUtil.e("xxxxx", CONFIG.sel_facility);
                facility = CONFIG.sel_facility.replace("|", ",");
            }
            if(!TextUtils.isEmpty(CONFIG.sel_orderby)) {
                LogUtil.e("xxxxx", CONFIG.sel_orderby);
                order_kind = CONFIG.sel_orderby;
            }
            if(!TextUtils.isEmpty(CONFIG.sel_rate)) {
                LogUtil.e("xxxxx", CONFIG.sel_rate);
                if(CONFIG.sel_rate.equals("0"))
                    score = "2";
                else if(CONFIG.sel_rate.equals("1")){
                    score = "3";
                }
                else if(CONFIG.sel_rate.equals("2")){
                    score = "4";
                }
                else if(CONFIG.sel_rate.equals("3")){
                    score = "5";
                }
            }
            if(!TextUtils.isEmpty(CONFIG.sel_useperson)) {
                LogUtil.e("xxxxx", CONFIG.sel_useperson);
                person_count = CONFIG.sel_useperson.replace("0", "1").replace("3","5").replace("2", "3|4").replace("1", "2").replace("|", ",");
            }

            Page = 1;
            total_count = 0;
            mItems.clear();
            getSearch();
        }
        else if(requestCode == 50 && responseCode == 80){
            adapter.notifyDataSetChanged();
        }
    }

}
