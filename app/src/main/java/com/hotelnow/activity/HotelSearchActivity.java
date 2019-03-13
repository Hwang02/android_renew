package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
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
import com.hotelnow.adapter.SearchBannerPagerAdapter;
import com.hotelnow.adapter.SearchStayAdapter;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.hotelnow.utils.ViewPagerCustom;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HotelSearchActivity extends Activity {

    private SharedPreferences _preferences;
    private ListView mlist;
    private View EmptyView;
    private View HeaderView;
    private ImageView map_img;
    private TextView tv_review_count;
    private RelativeLayout btn_location, btn_date;
    private ArrayList<SearchResultItem> mItems = new ArrayList<>();
    private SearchStayAdapter adapter;
    private String banner_id, search_txt;
    private LinearLayout btn_filter;
    private int Page = 1;
    private int total_count;
    private String s_position = "", city = "", sub_city = "";
    private DbOpenHelper dbHelper;
    private TextView tv_location, tv_date, page;
    private String ec_date = "", ee_date = "";
    private Button bt_scroll;
    private String category = "", facility = "", price_min = "", person_count = "", price_max = "", order_kind = "", score = "";
    private RelativeLayout bannerview;
    private ArrayList<BannerItem> mBannerItems = new ArrayList<>();
    private ViewPagerCustom autoViewPager;
    private SearchBannerPagerAdapter bannerAdapter;
    public static int markNowPosition = 0;
    private static int PAGES = 0;
    private static int nowPosition = 0;
    private TextView title_text;
    RelativeLayout toast_layout, title_search;
    ImageView ico_favorite;
    TextView tv_toast, tv_elocation, tv_edate, empty_title, empty_sub;
    private DialogAlert dialogAlert;
    private int filter_cnt = 0;
    private LinearLayout count_view;
    private TextView tv_count;
    private long gapDay = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ha_search);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new DbOpenHelper(this);

        TuneWrap.Event("stay_list");

        Intent intent = getIntent();
        ec_date = intent.getStringExtra("ec_date");
        ee_date = intent.getStringExtra("ee_date");
        city = intent.getStringExtra("city_code");
        sub_city = intent.getStringExtra("subcity_code");
        if (city.equals(sub_city)) {
            sub_city = "";
        }

        if (ec_date == null && ee_date == null) {
            ec_date = Util.setCheckinout().get(0);
            ee_date = Util.setCheckinout().get(1);
        }

        title_text = (TextView) findViewById(R.id.title_text);
        mlist = (ListView) findViewById(R.id.h_list);
        HeaderView = getLayoutInflater().inflate(R.layout.layout_search_map_filter_header, null, false);
        btn_location = (RelativeLayout) HeaderView.findViewById(R.id.btn_location);
        btn_date = (RelativeLayout) HeaderView.findViewById(R.id.btn_date);
        tv_review_count = (TextView) HeaderView.findViewById(R.id.tv_review_count);
        map_img = (ImageView) HeaderView.findViewById(R.id.map_img);
        tv_location = (TextView) HeaderView.findViewById(R.id.tv_location);
        tv_date = (TextView) HeaderView.findViewById(R.id.tv_date);
        bannerview = (RelativeLayout) HeaderView.findViewById(R.id.bannerview);
        autoViewPager = (ViewPagerCustom) HeaderView.findViewById(R.id.autoViewPager);
        page = (TextView) HeaderView.findViewById(R.id.page);
        btn_filter = (LinearLayout) findViewById(R.id.btn_filter);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        bt_scroll = (Button) findViewById(R.id.bt_scroll);
        title_search = (RelativeLayout) findViewById(R.id.title_search);
        count_view = (LinearLayout) findViewById(R.id.count_view);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_location.setText(intent.getStringExtra("city"));

        View empty = getLayoutInflater().inflate(R.layout.layout_h_search_empty, null, false);
        tv_elocation = (TextView) empty.findViewById(R.id.tv_location);
        tv_elocation.setText(intent.getStringExtra("city"));
        tv_edate = (TextView) empty.findViewById(R.id.tv_date);
        empty_title = (TextView) empty.findViewById(R.id.title);
        empty_sub = (TextView) empty.findViewById(R.id.sub);
        ((ViewGroup) mlist.getParent()).addView(empty);
        mlist.setEmptyView(empty);

//        가져와서 적용
        long count = Util.diffOfDate2(ec_date, ee_date);
        tv_date.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + count + "박");
        tv_edate.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + count + "박");
        mlist.addHeaderView(HeaderView);
        adapter = new SearchStayAdapter(this, 0, mItems, dbHelper);
        mlist.setAdapter(adapter);

        empty_title.setVisibility(View.GONE);
        empty_sub.setVisibility(View.GONE);

        tv_elocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotelSearchActivity.this, AreaHotelActivity.class);
                startActivityForResult(intent, 80);
            }
        });
        tv_edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api.get(CONFIG.server_time, new Api.HttpCallback() {
                    @Override
                    public void onFailure(Response response, Exception throwable) {
                        Toast.makeText(HotelSearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);
                            if (!TextUtils.isEmpty(obj.getString("server_time"))) {
                                long time = obj.getInt("server_time") * (long) 1000;
                                CONFIG.svr_date = new Date(time);
                                Intent intent = new Intent(HotelSearchActivity.this, CalendarActivity.class);
                                intent.putExtra("ec_date", ec_date);
                                intent.putExtra("ee_date", ee_date);
                                startActivityForResult(intent, 70);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotelSearchActivity.this, AreaHotelActivity.class);
                startActivityForResult(intent, 80);
            }
        });
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api.get(CONFIG.server_time, new Api.HttpCallback() {
                    @Override
                    public void onFailure(Response response, Exception throwable) {
                        Toast.makeText(HotelSearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);
                            if (!TextUtils.isEmpty(obj.getString("server_time"))) {
                                long time = obj.getInt("server_time") * (long) 1000;
                                CONFIG.svr_date = new Date(time);
                                Intent intent = new Intent(HotelSearchActivity.this, CalendarActivity.class);
                                intent.putExtra("ec_date", ec_date);
                                intent.putExtra("ee_date", ee_date);
                                startActivityForResult(intent, 70);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Util.clearSearch();
                LogUtil.e("xxxxx", "xxxxxx");
                Intent intent = new Intent(HotelSearchActivity.this, FilterHotelActivity.class);
                startActivityForResult(intent, 60);
            }
        });
        bt_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlist.smoothScrollToPosition(0);
            }
        });

        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView) view.findViewById(R.id.hid);
                Intent intent = new Intent(HotelSearchActivity.this, DetailHotelActivity.class);
                intent.putExtra("hid", hid.getText().toString());
                intent.putExtra("sdate", ec_date);
                intent.putExtra("edate", ee_date);
                intent.putExtra("save", true);
                startActivityForResult(intent, 50);
            }
        });

        title_search.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(HotelSearchActivity.this, SearchActivity.class);
                startActivityForResult(intent, 50);
                finish();
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(70);
                finish();
            }
        });

        if (filter_cnt == 0) {
            count_view.setVisibility(View.GONE);
        } else {
            count_view.setVisibility(View.VISIBLE);
        }

        getSearch();
    }

    public void getSearch() {
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.search_stay_list;
        if (!TextUtils.isEmpty(city)) {
            url += "&city=" + city;
        }
        if (!TextUtils.isEmpty(sub_city)) {
            url += "&sub_city=" + sub_city;
        }
        if (!TextUtils.isEmpty(search_txt)) {
            url += "&search_text=" + search_txt;
        }
        if (!TextUtils.isEmpty(banner_id)) {
            url += "&banner_id=" + banner_id;
        }
        if (!TextUtils.isEmpty(ec_date)) {
            url += "&ec_date=" + ec_date;
        }
        if (!TextUtils.isEmpty(ee_date)) {
            url += "&ee_date=" + ee_date;
        }
        if (!TextUtils.isEmpty(category)) {
            url += "&category=" + category;
        }
        if (!TextUtils.isEmpty(facility)) {
            url += "&facility=" + facility;
        }
        if (!TextUtils.isEmpty(price_min)) {
            url += "&price_min=" + price_min;
        }
        if (!TextUtils.isEmpty(person_count)) {
            url += "&person_count=" + person_count;
        }
        if (!TextUtils.isEmpty(price_max)) {
            url += "&price_max=" + price_max;
        }
        if (!TextUtils.isEmpty(score)) {
            url += "&score=" + score;
        }
        if (!TextUtils.isEmpty(order_kind)) {
            url += "&order_kind=" + order_kind;
            if (order_kind.equals("distance")) {
                url += "&lat=" + CONFIG.lat + "&lng=" + CONFIG.lng;
            }
        }

        url += "&per_page=20";

        if (Page < 2 || total_count != mItems.size()) {
            Api.get(url + "&page=" + Page, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(HotelSearchActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }

                        final JSONArray list = obj.getJSONArray("lists");
                        final JSONArray bannerlist = obj.getJSONArray("region_banners");
                        JSONObject entry = null;
                        JSONObject bannerentry = null;

                        if (Page == 1) {
                            s_position = "";
                        }
                        final String total_cnt = "총 " + Util.numberFormat(obj.getInt("total_count")) + "개의 상품이 검색되었습니다";
                        SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
                        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple)), 2, 2 + obj.getString("total_count").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2 + obj.getString("total_count").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                                        bannerentry.getString("link"),
                                        bannerentry.getString("title"),
                                        bannerentry.getString("sub_title")
                                ));
                            }

                            PAGES = mBannerItems.size();
                            bannerAdapter = new SearchBannerPagerAdapter(HotelSearchActivity.this, mBannerItems);
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
                                    page.setText(markNowPosition + 1 + " / " + PAGES);
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });
                            page.setText("1 / " + mBannerItems.size());

//                            autoViewPager.startAutoScroll();
                        } else {
                            bannerview.setVisibility(View.GONE);
                            autoViewPager.stopAutoScroll();
                        }

                        gapDay = Util.diffOfDate2(ec_date, ee_date);

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
                                    i == 0 ? true : false,
                                    gapDay
                            ));
                            if (Page == 1)
                                s_position += "&markers=icon:http://hotelnow.s3.amazonaws.com/etc/20181101_173010_uXfZWjNIzK.png%7C" + entry.getString("latitude") + "%2C" + entry.getString("longuitude");
                        }

                        if (mItems.size() > 0) {
                            bt_scroll.setVisibility(View.VISIBLE);
                            btn_filter.setVisibility(View.VISIBLE);
                            empty_title.setVisibility(View.GONE);
                            empty_sub.setVisibility(View.GONE);
                        } else {
                            bt_scroll.setVisibility(View.GONE);
                            btn_filter.setVisibility(View.VISIBLE);
                            empty_title.setVisibility(View.VISIBLE);
                            empty_sub.setVisibility(View.VISIBLE);
                            if (obj.has("previous_date_msg")) {
                                dialogAlert = new DialogAlert(
                                        getString(R.string.alert_notice),
                                        obj.getString("previous_date_msg"),
                                        HotelSearchActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogAlert.dismiss();
                                            }
                                        });
                                dialogAlert.setCancelable(false);
                                dialogAlert.show();
                            }
                        }

                        String mapStr = "https://maps.googleapis.com/maps/api/staticmap?" +
                                s_position +
                                "&scale=2&sensor=false&language=ko&size=360x130" + "&key=" + BuildConfig.google_map_key2;
                        Ion.with(map_img).load(mapStr);

                        map_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TuneWrap.Event("stay_list_map");

                                Intent intent = new Intent(HotelSearchActivity.this, MapHotelActivity.class);
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

                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                }
            });
        } else {
            findViewById(R.id.wrapper).setVisibility(View.GONE);
        }
    }

    public void setLike(final int position, final boolean islike) {
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        final String sel_id = mItems.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "stay");
            paramObj.put("id", sel_id);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
            findViewById(R.id.wrapper).setVisibility(View.GONE);
        }
        if (islike) {// 취소
            Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(HotelSearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }

                        TuneWrap.Event("favorite_stay_del", sel_id);

                        dbHelper.deleteFavoriteItem(false, sel_id, "H");
                        LogUtil.e("xxxx", "찜하기 취소");
                        showIconToast("관심 상품 담기 취소", false);
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                }
            });
        } else {// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(HotelSearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }

                        TuneWrap.Event("favorite_stay", sel_id);

                        dbHelper.insertFavoriteItem(sel_id, "H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        showIconToast("관심 상품 담기 성공", true);
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (requestCode == 90 && responseCode == 90) {
            setResult(80);
            finish();
        }
        if (requestCode == 80 && responseCode == 80) {
            tv_location.setText(data.getStringExtra("city"));
            tv_elocation.setText(data.getStringExtra("city"));
            city = data.getStringExtra("city_code");
            sub_city = data.getStringExtra("subcity_code");
            if (city.equals(sub_city)) {
                sub_city = "";
            }
            Page = 1;
            total_count = 0;
            mItems.clear();
            empty_title.setVisibility(View.GONE);
            empty_sub.setVisibility(View.GONE);
            getSearch();
        } else if (requestCode == 70 && responseCode == 80) {
            ec_date = data.getStringExtra("ec_date");
            ee_date = data.getStringExtra("ee_date");
            long gap = Util.diffOfDate2(ec_date, ee_date);
            tv_date.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + gap + "박");
            tv_edate.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + gap + "박");
            Page = 1;
            total_count = 0;
            mItems.clear();
            empty_title.setVisibility(View.GONE);
            empty_sub.setVisibility(View.GONE);
            getSearch();
        } else if (requestCode == 60 && responseCode == 80) {
            filter_cnt = 0;
            price_max = "";
            price_min = "";
            facility = "";
            order_kind = "recommendation";
            score = "";
            person_count = "";

            price_max = CONFIG.sel_max;
            LogUtil.e("xxxxx", CONFIG.sel_min);
            price_min = CONFIG.sel_min;
            if (!price_max.equals("600000") || !price_min.equals("0")) {
                filter_cnt++;
            }
            if (!TextUtils.isEmpty(CONFIG.sel_category)) {
                LogUtil.e("xxxxx", CONFIG.sel_category);
                category = CONFIG.sel_category.replace("|", ",");
                filter_cnt++;
            } else {
                category = "";
            }
            if (!TextUtils.isEmpty(CONFIG.sel_facility)) {
                LogUtil.e("xxxxx", CONFIG.sel_facility);
                facility = CONFIG.sel_facility.replace("|", ",");
                filter_cnt++;
            } else {
                facility = "";
            }
            if (!TextUtils.isEmpty(CONFIG.sel_orderby)) {
                LogUtil.e("xxxxx", CONFIG.sel_orderby);
                order_kind = CONFIG.sel_orderby;
                if (!order_kind.equals("recommendation")) {
                    filter_cnt++;
                }
            } else {
                order_kind = "";
            }
            if (!TextUtils.isEmpty(CONFIG.sel_rate)) {
                LogUtil.e("xxxxx", CONFIG.sel_rate);
                if (CONFIG.sel_rate.equals("0"))
                    score = "2";
                else if (CONFIG.sel_rate.equals("1")) {
                    score = "3";
                } else if (CONFIG.sel_rate.equals("2")) {
                    score = "4";
                } else if (CONFIG.sel_rate.equals("3")) {
                    score = "5";
                }
                filter_cnt++;
            } else {
                score = "";
            }
            if (!TextUtils.isEmpty(CONFIG.sel_useperson)) {
                LogUtil.e("xxxxx", CONFIG.sel_useperson);

                person_count = CONFIG.sel_useperson.replace("3", "5").replace("2", "3|4").replace("1", "2").replace("0", "1").replace("|", ",");
                filter_cnt++;
            } else {
                person_count = "";
            }

            if (filter_cnt == 0) {
                count_view.setVisibility(View.GONE);
            } else {
                count_view.setVisibility(View.VISIBLE);
            }
            tv_count.setText(filter_cnt + "");
            Page = 1;
            total_count = 0;
            mItems.clear();
            empty_title.setVisibility(View.GONE);
            empty_sub.setVisibility(View.GONE);
            getSearch();
        } else if (requestCode == 50 && responseCode == 80) {
            adapter.notifyDataSetChanged();
        } else if (responseCode == 100) {
            setResult(100);
            finish();
        } else if (requestCode == 90 && responseCode == 3000) {
            adapter.notifyDataSetChanged();
        }
    }

    public void showToast(String msg) {
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);
        findViewById(R.id.ico_favorite).setVisibility(View.GONE);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 1500);
    }

    public void showIconToast(String msg, boolean is_fav) {
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);

        if (is_fav) { // 성공
            ico_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        } else { // 취소
            ico_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite);
        }
        ico_favorite.setVisibility(View.VISIBLE);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 1500);
    }

}
