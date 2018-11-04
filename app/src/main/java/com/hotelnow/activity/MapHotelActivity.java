package com.hotelnow.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.adapter.MapHotelAdapter;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.GlobalUtils;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.SmoothPager;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;
import com.thebrownarrow.customstyledmap.CustomMap;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

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
    private String banner_id, search_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        latLngsArrayList = new ArrayList<>();
        latLngsArrayList.clear();
        latLngsArrayList = (ArrayList<SearchResultItem>)intent.getSerializableExtra("search_data");
        total_count = intent.getIntExtra("total_count",0);
        Page = intent.getIntExtra("Page",1);
        TextView total_item = (TextView)findViewById(R.id.total_item);
        Spannable spannable = new SpannableString("총 "+total_count+"개의 숙소");
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2+(total_count+"").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        total_item.setText(spannable);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MapHotelActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

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
                mapAdapter = new MapHotelAdapter(MapHotelActivity.this, latLngsArrayList);
                event_pager.setAdapter(mapAdapter);
                event_pager.setClipToPadding(false);
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
        search_txt = "서울";
        if(!TextUtils.isEmpty(search_txt)){
            url +="&search_text="+search_txt;
        }
        if(!TextUtils.isEmpty(banner_id)){
            url +="&banner_id="+banner_id;
        }

        url +="&per_page=20";

        if(total_count != latLngsArrayList.size()) {
            Api.get(url + "&page=" + ++Page, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(MapHotelActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final JSONArray list = obj.getJSONArray("lists");
                        JSONObject entry = null;

//                        final String total_cnt = "총 " + obj.getString("total_count") + "개의 객실이 있습니다";
//                        SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
//                        builder.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.purple)), 2, 2 + obj.getString("total_count").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        tv_review_count.setText(builder);

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
                                    false
                            ));
                        }

                        total_count = obj.getInt("total_count");
                        mapAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(MapHotelActivity.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
                        event_pager.setCurrentItem(mPosition, true);
                    } else {
                        event_pager.setCurrentItem(mPosition, true);
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
}
