package com.hotelnow.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hotelnow.R;
import com.hotelnow.adapter.MapHotelAdapter;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.SmoothPager;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;
import com.thebrownarrow.customstyledmap.CustomMap;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MapHotelActivity extends AppCompatActivity {

    private ArrayList<SearchResultItem> latLngsArrayList;

    private Animation slide_out_down, slide_in_up;
    public static GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private Marker previousSelectedMarker;
    private static SmoothPager event_pager;
    private MapHotelAdapter mapAdapter;
    private int prePosition = 0;
    CustomMap customMap;
    private TextView total_item;
    private int Page = 1;
    private int total_count=0;
    private String city, sub_city, search_txt, banner_id, ec_date, ee_date,category,facility,price_min,person_count,price_max,score,order_kind,city_name;
    private TextView title_text, subtitle_text;
    DbOpenHelper dbHelper;
    RelativeLayout toast_layout;
    ImageView ico_favorite;
    TextView tv_toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper = new DbOpenHelper(this);
        Intent intent = getIntent();
        latLngsArrayList = new ArrayList<>();
        latLngsArrayList.clear();
        latLngsArrayList = (ArrayList<SearchResultItem>)intent.getSerializableExtra("search_data");
        total_count = intent.getIntExtra("total_count",0);
        city = intent.getStringExtra("city");
        city_name = intent.getStringExtra("city_name");
        sub_city = intent.getStringExtra("sub_city");
        search_txt = intent.getStringExtra("search_txt");
        banner_id = intent.getStringExtra("banner_id");
        ec_date = intent.getStringExtra("ec_date");
        ee_date = intent.getStringExtra("ee_date");
        category = intent.getStringExtra("category");
        facility = intent.getStringExtra("facility");
        price_min = intent.getStringExtra("price_min");
        person_count = intent.getStringExtra("person_count");
        price_max =  intent.getStringExtra("price_max");
        score = intent.getStringExtra("score");
        order_kind =  intent.getStringExtra("order_kind");

        Page = intent.getIntExtra("Page",1);

        TextView total_item = (TextView)findViewById(R.id.total_item);
        TextView title_text = (TextView)findViewById(R.id.title_text);
        TextView subtitle_text = (TextView)findViewById(R.id.subtitle_text);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);

        if(TextUtils.isEmpty(intent.getStringExtra("title_text"))){
            title_text.setText("숙소");
        }
        else {
            title_text.setText(intent.getStringExtra("title_text"));
        }
        String sub ="";
        if(!TextUtils.isEmpty(city_name) && !city_name.equals("지역선택")){
            sub = city_name+", ";
        }
        if(!TextUtils.isEmpty(ec_date) && !TextUtils.isEmpty(ee_date)){
            long gap = Util.diffOfDate2(ec_date, ee_date);
            sub += Util.formatchange5(ec_date)+" - "+Util.formatchange5(ee_date)+"("+gap+"박)";
        }

        subtitle_text.setText(sub);

        Spannable spannable = new SpannableString("총 "+Util.numberFormat(total_count)+"개의 숙소");
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2+(Util.numberFormat(total_count)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2+(Util.numberFormat(total_count)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        total_item.setText(spannable);

        findViewById(R.id.title_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                setResult(3000);
                finish();
            }
        });

        findViewById(R.id.re_search).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                finish();
            }
        });

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(MapHotelActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MapHotelActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("지도를 사용하기 위해서는 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
        setContents();
    }

    private void setContents() {

        slide_out_down = AnimationUtils.loadAnimation(MapHotelActivity.this, R.anim.slide_out_down);
        slide_in_up = AnimationUtils.loadAnimation(MapHotelActivity.this, R.anim.slide_in_up);

        event_pager = (SmoothPager) findViewById(R.id.event_pager);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                customMap = new CustomMap(map, latLngsArrayList, MapHotelActivity.this, true);
                try {
//                    customMap.setCustomMapStyle(R.drawable.map_marker_stay);
                    // Customise the styling of the base map using a JSON object defined in a raw resource file.
                } catch (Resources.NotFoundException e) {
                    Log.e("Explore detail activity", "Can't find style. Error: " + e);
                }

                handleMap();
                customMap.addCustomPin();
                mapAdapter = new MapHotelAdapter(MapHotelActivity.this, latLngsArrayList, dbHelper);
                event_pager.setAdapter(mapAdapter);
                event_pager.setOffscreenPageLimit(4);
                event_pager.setPageMargin(20);
                customMap.addSelectedCustomPin(0);

            }
        });


        event_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                SearchResultItem location = latLngsArrayList.get(position);
                Point mappoint = map.getProjection().toScreenLocation(
                        new LatLng(location.getLatitude(), location.getLonguitude()));
                mappoint.set(mappoint.x, mappoint.y - 30);
                map.animateCamera(CameraUpdateFactory.newLatLng(map.getProjection().fromScreenLocation(mappoint)));

                LogUtil.e("prePosition", prePosition+"");
                LogUtil.e("position", position+"");

                latLngsArrayList.get(position).setIsfocus(true);
                latLngsArrayList.get(prePosition).setIsfocus(false);

                customMap.addSelectedCustomPin(position);
                mapAdapter.notifyDataSetChanged();
                prePosition = position;

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        if(total_count != latLngsArrayList.size()) {
            Api.get(url + "&page=" + ++Page, new Api.HttpCallback() {
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
                            Toast.makeText(MapHotelActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }

                        final JSONArray list = obj.getJSONArray("lists");
                        JSONObject entry = null;

                        for (int i = 0; i < list.length(); i++) {
                            entry = list.getJSONObject(i);
                            latLngsArrayList.add(new SearchResultItem(
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
                                    false,
                                    0
                            ));
                        }

                        total_count = obj.getInt("total_count");
                        mapAdapter.notifyDataSetChanged();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(MapHotelActivity.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                }
            });
        }
        else{
            findViewById(R.id.wrapper).setVisibility(View.GONE);
        }
    }

    private void handleMap() {
        if (map != null) {
            if (ActivityCompat.checkSelfPermission(MapHotelActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapHotelActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);

            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    final int mPosition = (int) marker.getTag();

                    previousSelectedMarker = marker;

                    if (event_pager.getVisibility() != View.VISIBLE) {

                        event_pager.startAnimation(slide_in_up);
                        findViewById(R.id.ll_count).startAnimation(slide_in_up);
                        slide_in_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation arg0) {
                                event_pager.setVisibility(View.VISIBLE);
                                findViewById(R.id.ll_count).setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {

                            }

                            @Override
                            public void onAnimationEnd(Animation arg0) {

                            }
                        });
//                        event_pager.setCurrentItem(mPosition, true);
                        event_pager.setCurrentItem(mPosition);
                    } else {
//                        event_pager.setCurrentItem(mPosition, true);
                        event_pager.setCurrentItem(mPosition);
                    }

                    return false;
                }
            });

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    if (event_pager.getVisibility() == View.VISIBLE) {
                        event_pager.startAnimation(slide_out_down);
                        findViewById(R.id.ll_count).startAnimation(slide_out_down);
                        slide_out_down.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation arg0) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {

                            }

                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                event_pager.setVisibility(View.GONE);
                                findViewById(R.id.ll_count).setVisibility(View.GONE);
                                findViewById(R.id.ll_count).clearAnimation();
                                event_pager.clearAnimation();
                            }
                        });
                    }
                }
            });


        } else {
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    map = googleMap;
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    customMap = new CustomMap(map, latLngsArrayList, MapHotelActivity.this, true);
                    customMap.addSelectedCustomPin(0);

                    handleMap();

                }
            });

        }
    }

    public void setLike(final String s_id, boolean islike){
        final String sel_id = s_id;
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
                    Toast.makeText(MapHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            return;
                        }

                        TuneWrap.Event("favorite_stay_del", sel_id);

                        dbHelper.deleteFavoriteItem(false,  sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 취소");
                        showIconToast("관심 상품 담기 취소", false);
                        mapAdapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
        else{// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(MapHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            return;
                        }

                        TuneWrap.Event("favorite_stay", sel_id);

                        dbHelper.insertFavoriteItem(sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        showIconToast("관심 상품 담기 성공", true);
                        mapAdapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
    }

    public void showToast(String msg){
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

    public void showIconToast(String msg, boolean is_fav){
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);

        if(is_fav) { // 성공
            ico_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        }
        else{ // 취소
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
