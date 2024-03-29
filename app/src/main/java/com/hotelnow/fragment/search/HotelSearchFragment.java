package com.hotelnow.fragment.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.activity.AllRoomTypeActivity;
import com.hotelnow.activity.AreaHotelActivity;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.FilterHotelActivity;
import com.hotelnow.activity.HotelSearchActivity;
import com.hotelnow.activity.MapHotelActivity;
import com.hotelnow.activity.SearchResultActivity;
import com.hotelnow.adapter.SearchBannerPagerAdapter;
import com.hotelnow.adapter.SearchResultStayAdapter;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.model.BannerItem;
import com.hotelnow.model.KeyWordItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FlowLayout;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
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
import java.util.List;
import java.util.Map;

public class HotelSearchFragment extends Fragment implements OnMapReadyCallback {

    private SharedPreferences _preferences;
    private ListView mlist;
    private View EmptyView;
    private View HeaderView;
    private TextView tv_review_count;
    private RelativeLayout btn_location, btn_date, btn_location2, btn_date2;
    private ArrayList<SearchResultItem> mItems = new ArrayList<>();
    private SearchResultStayAdapter adapter;
    private String banner_id, search_txt;
    private LinearLayout btn_filter;
    private int Page = 1;
    private int total_count;
    private String s_position = "", city = "", sub_city = "";
    private DbOpenHelper dbHelper;
    private TextView tv_location, tv_date, page, tv_location2, tv_date2;
    private String ec_date = "", ee_date = "";
    private Button bt_scroll;
    private String category = "", facility = "", price_min = "", person_count = "", price_max = "", order_kind = "", score = "";
    private RelativeLayout bannerview, h_filter;
    private ArrayList<BannerItem> mBannerItems = new ArrayList<>();
    private ViewPagerCustom autoViewPager;
    private SearchBannerPagerAdapter bannerAdapter;
    public static int markNowPosition = 0;
    private static int PAGES = 0;
    private static int nowPosition = 0;
    private FlowLayout popular_keyword;
    private List<KeyWordItem> mKeywordList = new ArrayList<>();
    private String title_text;
    private LinearLayout count_view, empty_image, layout_popular;
    private TextView tv_count;
    private int filter_cnt = 0;
    private boolean _hasLoadedOnce = false; // your boolean field
    private DialogAlert dialogAlert;
    private long gapDay = 0;
    // 구글 맵 참조변수 생성
    private GoogleMap mMap;
    private IconGenerator mainIconFactory;
    private BitmapDrawable bitmapdraw = null;
    private Bitmap b = null;
    private Bitmap smallMarker = null;
    private MapView mapView;
    private View clickmap;
    private LatLngBounds.Builder mapbuilder;
    private CameraUpdate cu;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_h_result, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

        HeaderView = getLayoutInflater().inflate(R.layout.layout_fragment_search_map_filter_header, null, false);
        btn_location = (RelativeLayout) HeaderView.findViewById(R.id.btn_location);
        btn_date = (RelativeLayout) HeaderView.findViewById(R.id.btn_date);
        tv_review_count = (TextView) HeaderView.findViewById(R.id.tv_review_count);

        tv_location = (TextView) HeaderView.findViewById(R.id.tv_location);
        tv_date = (TextView) HeaderView.findViewById(R.id.tv_date);
        bannerview = (RelativeLayout) HeaderView.findViewById(R.id.bannerview);
        autoViewPager = (ViewPagerCustom) HeaderView.findViewById(R.id.autoViewPager);
        page = (TextView) HeaderView.findViewById(R.id.page);
        clickmap = (View) HeaderView.findViewById(R.id.clickmap);

        mapView = (MapView) HeaderView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        init();
    }

    public void getSearch() {
        ((SearchResultActivity) getActivity()).showprogress();
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
//            if(order_kind.equals("distance")){
            url += "&lat=" + CONFIG.lat + "&lng=" + CONFIG.lng;
//            }
        }
        if (!TextUtils.isEmpty(title_text) && title_text.equals("내 주변 바로보기")) {
            url += "&location_go=Y";
        }

        url += "&per_page=20";

        if (Page < 2 || total_count != mItems.size()) {
            Api.get(url + "&page=" + Page, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    if (isAdded()) {
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        ((SearchResultActivity) getActivity()).hideprogress();
                    }
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            ((SearchResultActivity) getActivity()).hideprogress();
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isAdded()) {
                            final JSONArray list = obj.getJSONArray("lists");
                            final JSONArray bannerlist = obj.getJSONArray("region_banners");
                            JSONObject entry = null;
                            JSONObject bannerentry = null;

                            final String total_cnt = "총 " + Util.numberFormat(obj.getInt("total_count")) + "개의 상품이 검색되었습니다";
                            SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
                            builder.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.purple)), 2, 2 + Util.numberFormat(obj.getInt("total_count")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2 + Util.numberFormat(obj.getInt("total_count")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                                bannerAdapter = new SearchBannerPagerAdapter(getActivity(), mBannerItems);
                                autoViewPager.setClipToPadding(false);
                                autoViewPager.setOffscreenPageLimit(mBannerItems.size());
                                autoViewPager.setPageMargin(20);
                                autoViewPager.setAdapter(bannerAdapter); //Auto Viewpager에 Adapter 장착
                                autoViewPager.setCurrentItem(mBannerItems.size() * 5);
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

//                                autoViewPager.startAutoScroll();
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
                            if (Page == 1) {
                                mMap.clear();
                                mapbuilder = new LatLngBounds.Builder();
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
                                if (Page == 1 && !TextUtils.isEmpty(entry.getString("latitude"))&& !TextUtils.isEmpty(entry.getString("longuitude"))){
                                    setMainMarker(entry.getString("latitude"), entry.getString("longuitude"));
                                }
                            }

                            if (mItems.size() > 0) {
//                                btn_filter.setVisibility(View.VISIBLE);
                                bt_scroll.setVisibility(View.VISIBLE);
                                empty_image.setVisibility(View.GONE);

                                if(Page == 1){
                                    int padding = 50;
                                    /**create the bounds from latlngBuilder to set into map camera*/
                                    LatLngBounds bounds = mapbuilder.build();
                                    /**create the camera with bounds and padding to set into map*/
                                    cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    /**call the map call back to know map is loaded or not*/
                                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                        @Override
                                        public void onMapLoaded() {
                                            /**set animated zoom camera into map*/
                                            mMap.animateCamera(cu);

                                        }
                                    });
                                }
                            } else {
//                                btn_filter.setVisibility(View.GONE);
                                bt_scroll.setVisibility(View.GONE);
                                empty_image.setVisibility(View.VISIBLE);
                                h_filter.bringToFront();
                                if (obj.has("previous_date_msg")) {
                                    dialogAlert = new DialogAlert(
                                            getString(R.string.alert_notice),
                                            obj.getString("previous_date_msg"),
                                            getActivity(),
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

                            clickmap.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    CONFIG.search_data = new ArrayList<>();
                                    CONFIG.search_data = mItems;
                                    if(mItems != null) {
                                        Intent intent = new Intent(getActivity(), MapHotelActivity.class);

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
                                        intent.putExtra("title_text", title_text);
                                        if (order_kind != null && order_kind.equals("distance")) {
                                            intent.putExtra("lat", CONFIG.lat);
                                            intent.putExtra("lng", CONFIG.lng);
                                        }
                                        startActivityForResult(intent, 90);
                                    }
                                }
                            });


                            total_count = obj.getInt("total_count");
                            adapter.notifyDataSetChanged();
                            Page++;
                        }

                        ((SearchResultActivity) getActivity()).hideprogress();
                    } catch (Exception e) {
                        if (isAdded()) {
                            Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                            ((SearchResultActivity) getActivity()).hideprogress();
                        }
                    }
                }
            });
        } else {
            ((SearchResultActivity) getActivity()).hideprogress();
        }
    }

    private void setPopular() {
        popular_keyword.removeAllViews();
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_pressed}, new int[]{-android.R.attr.state_pressed}},
                new int[]{getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext)});
        if (mKeywordList.size() > 0) {
            layout_popular.setVisibility(View.VISIBLE);
            for (int i = 0; i < mKeywordList.size(); i++) {
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
                tv.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        Util.clearSearch();
                        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                        intent.putExtra("banner_id", mKeywordList.get((int) v.getTag()).getId());
                        intent.putExtra("banner_name", mKeywordList.get((int) v.getTag()).getLink());
                        startActivityForResult(intent, 80);
                        getActivity().finish();
                    }
                });

                popular_keyword.addView(tv);
            }
        } else {
            layout_popular.setVisibility(View.GONE);
        }
    }

    public void setClear() {
        Page = 1;
        total_count = 0;
        ec_date = Util.setCheckinout().get(0);
        ee_date = Util.setCheckinout().get(1);
        city = "";
        sub_city = "";
        price_max = "600000";
        price_min = "0";
        category = "";
        facility = "";
        tv_location.setText("지역선택");
        tv_location2.setText("지역선택");
        search_txt = "";
        long count = Util.diffOfDate2(ec_date, ee_date);
        tv_date.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + count + "박");
        tv_date2.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + count + "박");
        mItems.clear();
        getSearch();
    }

    public void setLike(final int position, final boolean islike) {
        final String sel_id = mItems.get(position).getId();

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
                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((SearchResultActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false, sel_id, "H");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((SearchResultActivity) getActivity()).showIconToast("관심 상품 담기 취소", false);
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
                            ((SearchResultActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id, "H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((SearchResultActivity) getActivity()).showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        if(mMap != null) {
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setAllGesturesEnabled(false);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (requestCode == 90 && responseCode == 90) {
            getActivity().setResult(80);
            getActivity().finish();
        }
        if (requestCode == 80 && responseCode == 80) {
            if(data != null) {
                tv_location.setText(data.getStringExtra("city"));
                tv_location2.setText(data.getStringExtra("city"));
                city = data.getStringExtra("city_code");
                sub_city = data.getStringExtra("subcity_code");
                if (city.equals(sub_city)) {
                    sub_city = "";
                }
                Page = 1;
                total_count = 0;
                mItems.clear();
                adapter.notifyDataSetChanged();
                empty_image.setVisibility(View.GONE);
                layout_popular.setVisibility(View.GONE);
                getSearch();
            }
        } else if (requestCode == 70 && responseCode == 80) {
            ec_date = data.getStringExtra("ec_date");
            ee_date = data.getStringExtra("ee_date");
            long gap = Util.diffOfDate2(ec_date, ee_date);
            tv_date.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + gap + "박");
            tv_date2.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + gap + "박");
            Page = 1;
            total_count = 0;
            mItems.clear();
            adapter.notifyDataSetChanged();
            empty_image.setVisibility(View.GONE);
            layout_popular.setVisibility(View.GONE);
            getSearch();
        } else if (requestCode == 60 && responseCode == 80) {
            filter_cnt = 0;
            LogUtil.e("xxxxx", CONFIG.sel_max);
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
            adapter.notifyDataSetChanged();
            empty_image.setVisibility(View.GONE);
            layout_popular.setVisibility(View.GONE);
            getSearch();
        } else if (requestCode == 50 && responseCode == 80) {
            adapter.notifyDataSetChanged();
        } else if (requestCode == 90 && responseCode == 3000) {
            adapter.notifyDataSetChanged();
        } else if (requestCode == 90 && responseCode == 4000) {
            getActivity().finish();
        }
    }

    private void init() {
        // preference
        if(isAdded()) {

            search_txt = getArguments().getString("search_txt");
            banner_id = getArguments().getString("banner_id");
            order_kind = getArguments().getString("order_kind");
            if (order_kind != null && order_kind.equals("distance")) {
                filter_cnt = 1;
            }
            title_text = getArguments().getString("title_text");
            getArguments().clear();
            mlist = (ListView) getView().findViewById(R.id.h_list);
            btn_filter = (LinearLayout) getView().findViewById(R.id.btn_filter);
            bt_scroll = (Button) getView().findViewById(R.id.bt_scroll);
            count_view = (LinearLayout) getView().findViewById(R.id.count_view);
            tv_count = (TextView) getView().findViewById(R.id.tv_count);

            View empty = getLayoutInflater().inflate(R.layout.layout_search_empty, null, false);
            popular_keyword = (FlowLayout) empty.findViewById(R.id.filter1);

            tv_location2 = empty.findViewById(R.id.tv_location);
            tv_date2 = empty.findViewById(R.id.tv_date);
            btn_location2 = (RelativeLayout) empty.findViewById(R.id.btn_location);
            btn_date2 = (RelativeLayout) empty.findViewById(R.id.btn_date);
            empty_image = (LinearLayout) empty.findViewById(R.id.empty_image);
            layout_popular = (LinearLayout) empty.findViewById(R.id.popular_keyword);
            h_filter = (RelativeLayout) getView().findViewById(R.id.h_filter);
            ((ViewGroup) mlist.getParent()).addView(empty);
            mlist.setEmptyView(empty);

            ec_date = Util.setCheckinout().get(0);
            ee_date = Util.setCheckinout().get(1);

            long count = Util.diffOfDate2(ec_date, ee_date);
            tv_date.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + count + "박");
            tv_date2.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date) + ", " + count + "박");

            mlist.addHeaderView(HeaderView);
            adapter = new SearchResultStayAdapter(getActivity(), 0, mItems, HotelSearchFragment.this, dbHelper);
            mlist.setAdapter(adapter);
            empty_image.setVisibility(View.GONE);
            layout_popular.setVisibility(View.GONE);

            btn_location.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(getActivity(), AreaHotelActivity.class);
                    startActivityForResult(intent, 80);
                }
            });
            btn_date.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Api.get(CONFIG.server_time, new Api.HttpCallback() {
                        @Override
                        public void onFailure(Response response, Exception throwable) {
                            Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        @Override
                        public void onSuccess(Map<String, String> headers, String body) {
                            try {
                                JSONObject obj = new JSONObject(body);
                                if (!TextUtils.isEmpty(obj.getString("server_time"))) {
                                    long time = obj.getInt("server_time") * (long) 1000;
                                    CONFIG.svr_date = new Date(time);
                                    Intent intent = new Intent(getActivity(), CalendarActivity.class);
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
            btn_location2.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(getActivity(), AreaHotelActivity.class);
                    startActivityForResult(intent, 80);
                }
            });
            btn_date2.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Api.get(CONFIG.server_time, new Api.HttpCallback() {
                        @Override
                        public void onFailure(Response response, Exception throwable) {
                            Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        @Override
                        public void onSuccess(Map<String, String> headers, String body) {
                            try {
                                JSONObject obj = new JSONObject(body);
                                if (!TextUtils.isEmpty(obj.getString("server_time"))) {
                                    long time = obj.getInt("server_time") * (long) 1000;
                                    CONFIG.svr_date = new Date(time);
                                    Intent intent = new Intent(getActivity(), CalendarActivity.class);
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
            btn_filter.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
//                Util.clearSearch();
                    if (order_kind != null && order_kind.equals("distance")) {
                        CONFIG.sel_orderby = order_kind;
                    }
                    Intent intent = new Intent(getActivity(), FilterHotelActivity.class);
                    startActivityForResult(intent, 60);
                }
            });
            bt_scroll.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mlist.setSelection(0);
                }
            });

            if (filter_cnt == 0) {
                count_view.setVisibility(View.GONE);
            } else {
                count_view.setVisibility(View.VISIBLE);
            }
            tv_count.setText(filter_cnt + "");

            mlist.setOnItemClickListener(new OnSingleItemClickListener() {
                @Override
                public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView hid = (TextView) view.findViewById(R.id.hid);
                    if (hid != null) {
                        Intent intent = new Intent(getActivity(), DetailHotelActivity.class);
                        intent.putExtra("hid", hid.getText().toString());
                        intent.putExtra("sdate", ec_date);
                        intent.putExtra("edate", ee_date);
                        intent.putExtra("save", true);
                        startActivityForResult(intent, 50);
                    }
                }
            });

            getSearch();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);

        LatLng position = new LatLng(37.506839, 127.066234);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5));
    }

    private void setMainMarker(String lat, String lng) {
        LatLng position = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        mainIconFactory = new IconGenerator(getActivity());
        mainIconFactory.setColor(getResources().getColor(R.color.blacktxt));
        mainIconFactory.setTextAppearance(R.style.iconGenTextMain);

        bitmapdraw = (BitmapDrawable) getResources().getDrawable(com.thebrownarrow.customstyledmap.R.drawable.map_marker_stay);
        b = bitmapdraw.getBitmap();

        float scale = getResources().getDisplayMetrics().density;
        int p = (int) (24 * scale + 0.5f);

        smallMarker = Bitmap.createScaledBitmap(b, p, p, false);

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(position);

        mMap.addMarker(markerOptions);
        mapbuilder.include(position);
    }
}
