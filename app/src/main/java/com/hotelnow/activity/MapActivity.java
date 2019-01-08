package com.hotelnow.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by susia on 15. 12. 24..
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private String hid;
    private String from = "";
    private String lat = "37.506292";
    private String lng = "127.053612";
    private int zoom;
    private String title;
    private String hotel_name;
    private String category;
    private SharedPreferences _preferences;
    private boolean flag_use_location;
    private String sel_hotel_id;
    private DialogConfirm dialogConfirm;
    private Marker main;
    private Marker preMarker = null;
    private JSONObject preobj = null;
    private RelativeLayout info;
    private IconGenerator mainIconFactory;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private ArrayList<JSONObject> objs = new ArrayList<JSONObject>();
    private int MY_PERMISSION_LOCATION;
    private boolean isTicket = false;
    private String ec_date, ee_date;
    private View marker_root_view;
    private TextView tv_marker, tv_title_hotel;
    private int m_height = 90;
    private int m_width = 90;
    private int height = 72;
    private int width = 72;
    private BitmapDrawable bitmapdraw = null;
    private Bitmap b = null;
    private Bitmap smallMarker = null;
    private Bitmap sel_smallMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_map);

        Util.setStatusColor(this);

        _preferences = PreferenceManager.getDefaultSharedPreferences(MapActivity.this);
        flag_use_location = _preferences.getBoolean("flag_use_location", false);

        info = (RelativeLayout) findViewById(R.id.info);

        Intent intent = getIntent();
        ec_date = intent.getStringExtra("ec_date");
        ee_date = intent.getStringExtra("ee_date");
        findViewById(R.id.noselect).setVisibility(View.VISIBLE);

        if (intent != null) {
           isTicket = intent.getBooleanExtra("isTicket",false);
            if(isTicket) {
                title = intent.getStringExtra("deal_name");
                lat = intent.getStringExtra("lat") == null ? "37.506292" : intent.getStringExtra("lat");
                lng = intent.getStringExtra("lng") == null ? "127.053612" : intent.getStringExtra("lng");
                from = intent.getStringExtra("from");
                hid = "0";
                zoom = 12;

                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                SupportMapFragment mapFragment = (SupportMapFragment) fragment;

                if (mapFragment == null) {
                    Toast.makeText(MapActivity.this, getString(R.string.cant_use_map), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    mapFragment.getMapAsync(MapActivity.this);
                }
            } else {
                from = intent.getStringExtra("from");
                hid = intent.getStringExtra("hid");
                title = intent.getStringExtra("title");
                String url = CONFIG.locationUrl + "/" + hid;
                findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
                Api.get(url, new Api.HttpCallback() {
                    @Override
                    public void onFailure(Response response, Exception e) {
                        Log.e(CONFIG.TAG, "expection is ", e);
                        Toast.makeText(MapActivity.this, getString(R.string.error_hotel_location), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);

                            if (!obj.getString("result").equals("success")) {
                                Toast.makeText(MapActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                findViewById(R.id.wrapper).setVisibility(View.GONE);
                                return;
                            }

                            JSONArray data = obj.getJSONArray("data");

                            lat = data.getJSONObject(0).getString("latitude");
                            lng = data.getJSONObject(0).getString("longuitude");
                            zoom = data.getJSONObject(0).getInt("map_zoom");
                            title = data.getJSONObject(0).getString("name").replace("&amp;", "&");
                            category = data.getJSONObject(0).getString("category");

//                            getActionBar().setTitle(title);
                            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

                            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
                            SupportMapFragment mapFragment = (SupportMapFragment) fragment;

                            if (mapFragment == null) {
                                Toast.makeText(MapActivity.this, getString(R.string.cant_use_map), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                mapFragment.getMapAsync(MapActivity.this);
                            }
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                        } catch (Exception e) {
                            Log.e(CONFIG.TAG, "expection is ", e);
                            Toast.makeText(MapActivity.this, getString(R.string.error_hotel_location), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                        }

                    }
                });
            }
        }

        tv_title_hotel = (TextView) findViewById(R.id.tv_title_hotel);
        tv_title_hotel.setText(title);

        findViewById(R.id.kimgisa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirm = new DialogConfirm(
                        getString(R.string.alert_notice),
                        getString(R.string.booking_kimkisa_ask)+"\n목적지 : "+title,
                        getString(R.string.alert_no),
                        getString(R.string.alert_yes),
                        MapActivity.this,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogConfirm.dismiss();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = "kimgisa://navigate?name="+title+"&coord_type=wgs84&pos_x="+lng+"&pos_y="+lat+"&return_uri=com.hotelnow.activities.ActLoading&key="+CONFIG.kimgisaKey;
                                String strAppPackage = "com.locnall.KimGiSa";
                                PackageManager pm = getPackageManager();

                                try {
                                    Intent intent = new Intent();
                                    intent.setClassName(strAppPackage, "com.locnall.KimGiSa.Engine.SMS.CremoteActivity");
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);

                                    Toast.makeText(MapActivity.this, "카카오내비를 구동합니다.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + strAppPackage));
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException anfe) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + strAppPackage));
                                        startActivity(intent);
                                    }
                                }
                                dialogConfirm.dismiss();

                            }
                        });
                dialogConfirm.setCancelable(false);
                dialogConfirm.show();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setMainTicketMarker(){
        LatLng position = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
        mainIconFactory = new IconGenerator(MapActivity.this);
//        int color = getResources().getIdentifier(category, "color", getPackageName());
        mainIconFactory.setColor(getResources().getColor(R.color.blacktxt));
        mainIconFactory.setTextAppearance(R.style.iconGenTextMain);

        bitmapdraw = (BitmapDrawable) getResources().getDrawable(com.thebrownarrow.customstyledmap.R.drawable.map_marker_activity_selected);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, m_width, m_height, false);

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(position);

        main = mGoogleMap.addMarker(markerOptions);
        main.setSnippet(hid);
    }

    private void setMainMarker(){
        LatLng position = new LatLng(Double.valueOf(lat), Double.valueOf(lng));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));

        bitmapdraw = (BitmapDrawable) getResources().getDrawable(com.thebrownarrow.customstyledmap.R.drawable.map_marker_stay_selected);
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, m_width, m_height, false);

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(position);

        main = mGoogleMap.addMarker(markerOptions);
        main.setSnippet(hid);
    }

    private void getNearHotels(){
        String ec,ee;

        if(ec_date != null && ee_date != null) {
            ec = ec_date;
            ee = ee_date;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatterdt = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");

            // 서버타임있는지 확인하고 없으면 설정
            if(CONFIG.svr_date == null) {
                long time = System.currentTimeMillis();
                CONFIG.svr_date = new Date(time);
            }

            Date dateObj = new Date();

            // 현재 시간 object 설정
            try {
                dateObj = CONFIG.svr_date;
            } catch(Exception e){}

            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();

            startCal.setTime(dateObj);
            endCal.setTime(dateObj);
            endCal.add(Calendar.DATE, 1);

            if (CurHourFormat.format(dateObj).equals("00") || CurHourFormat.format(dateObj).equals("01")) {
                startCal.add(Calendar.DATE, -1);
                endCal.add(Calendar.DATE, -1);
            }

            ec = formatterdt.format(startCal.getTime());
            ee = formatterdt.format(endCal.getTime());
        }

        String diffDays = String.valueOf(Util.diffOfDate(ec.replace("-", ""), ee.replace("-", "")));
        String nearurl = "";

        setCustomMarkerView();
        if(!isTicket) {
            nearurl = CONFIG.locationNearUrl + "/" + lat + "/" + lng + "?ec_date=" + ec + "&ee_date=" + ee + "&date_term=" + diffDays;
        }
        else{
            nearurl = CONFIG.locationNearUrl + "/" + lat + "/" + lng + "?ec_date=" + ec + "&ee_date=" + ee + "&date_term=" + diffDays+"&is_q=Y";
        }
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        Api.get(nearurl, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Log.e(CONFIG.TAG, "expection is ", e);
                Toast.makeText(MapActivity.this, getString(R.string.error_near_hotel_location), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONArray data = new JSONArray(body);

                    for(int i = 0; i < data.length(); i++) {
                        if (hid.equals(data.getJSONObject(i).getString("id"))) continue;

                        LatLng hotel = new LatLng(data.getJSONObject(i).getDouble("latitude"), data.getJSONObject(i).getDouble("longuitude"));
                        IconGenerator iconFactory = new IconGenerator(MapActivity.this);
                        int color = getResources().getIdentifier(data.getJSONObject(i).getString("category"), "color", getPackageName());
                        iconFactory.setColor(getResources().getColor(color));
                        iconFactory.setTextAppearance(R.style.iconGenText);

                        tv_marker.setBackgroundResource(R.drawable.map_marker_price);
                        tv_marker.setTextColor(ContextCompat.getColor(MapActivity.this, R.color.white));
                        tv_marker.setText(Util.numberFormat(data.getJSONObject(i).getInt("sale_price")));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(Util.numberFormat(data.getJSONObject(i).getInt("sale_price")));
                        markerOptions.position(hotel);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapActivity.this, marker_root_view)));

                        mGoogleMap.setInfoWindowAdapter(new MapWindowAdapter(MapActivity.this));
                        Marker sub = mGoogleMap.addMarker(markerOptions);
                        sub.setTitle(data.getJSONObject(i).getString("name"));
                        sub.setSnippet(data.getJSONObject(i).getString("id"));
                        objs.add(data.getJSONObject(i));
                        markers.add(sub);
                    }
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
//                    Log.e(CONFIG.TAG, "expection is ", e);
                    Toast.makeText(MapActivity.this, getString(R.string.error_near_hotel_location), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

            }
        });

    }

    private void setCustomMarkerView() {

        marker_root_view = LayoutInflater.from(this).inflate(R.layout.layout_marker, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        setUpMap();
    }

    public void setUpMap() {
        if (mGoogleMap != null) {
            if (from.equals("pdetail") || isTicket) {
                if (!from.equals("tdetail")) {
                    getNearHotels();

                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker selmarker) {

                            if (selmarker.getSnippet().equals(hid)) {
                                info.setVisibility(View.GONE);
                                findViewById(R.id.noselect).setVisibility(View.VISIBLE);
                                return true;
                            }

                            JSONObject obj = new JSONObject();

                            for (int i = 0; i < markers.size(); i++) {
                                if (selmarker.equals(markers.get(i))) {
                                    obj = objs.get(i);
                                    break;
                                }
                            }

                            if (obj.length() == 0) return true;

                            if (preMarker != null && preobj !=null) {
//                                preMarker.setAlpha(0.7f);
                                tv_marker.setBackgroundResource(R.drawable.map_marker_price);
                                tv_marker.setTextColor(ContextCompat.getColor(MapActivity.this, R.color.white));
                                try {
                                    tv_marker.setText(Util.numberFormat(preobj.getInt("sale_price")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                preMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapActivity.this, marker_root_view)));
                            }
                            preMarker = selmarker;
                            preobj = obj;
                            selmarker.showInfoWindow();

                            try {
                                sel_hotel_id = obj.getString("id");
                                hotel_name = obj.getString("name");
                                String review_score = obj.getString("review_score");
                                String grade_score = obj.getString("grade_score");
                                int sale_price = obj.getInt("sale_price");
                                String hotel_category = obj.getString("category");
                                String category_name = obj.getString("category_name");

                                tv_marker.setBackgroundResource(R.drawable.map_marker_price_selected);
                                tv_marker.setTextColor(ContextCompat.getColor(MapActivity.this, R.color.purple));
                                tv_marker.setText(Util.numberFormat(obj.getInt("sale_price")));
                                selmarker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapActivity.this, marker_root_view)));

                                TextView tv_score = (TextView) findViewById(R.id.tv_score);
                                TextView tv_catagory = (TextView) findViewById(R.id.tv_catagory);
                                TextView tv_hotelname = (TextView) findViewById(R.id.tv_hotelname);
                                TextView tv_price = (TextView) findViewById(R.id.tv_price);
                                TextView detail_btn = (TextView) findViewById(R.id.detail_btn);
                                TextView line_score = (TextView) findViewById(R.id.line_score);
                                ImageView img_score = (ImageView) findViewById(R.id.img_score);

                                if(grade_score.equals("0")){
                                    tv_score.setVisibility(View.GONE);
                                    line_score.setVisibility(View.GONE);
                                    img_score.setVisibility(View.INVISIBLE);
                                }
                                else{
                                    tv_score.setVisibility(View.VISIBLE);
                                    line_score.setVisibility(View.VISIBLE);
                                    img_score.setVisibility(View.VISIBLE);
                                }
                                tv_score.setText(grade_score);
                                tv_catagory.setText(category_name);
                                tv_hotelname.setText(hotel_name);
                                tv_price.setText(Util.numberFormat(sale_price));

                                detail_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        Intent returnIntent = new Intent();
//
//                                        setResult(81, returnIntent);
//                                        finish();
                                        Intent returnIntent = new Intent(MapActivity.this, DetailHotelActivity.class);
                                        returnIntent.putExtra("hid", sel_hotel_id);
                                        returnIntent.putExtra("ec_date", ec_date);
                                        returnIntent.putExtra("ee_date", ee_date);
                                        returnIntent.putExtra("evt", "N");
                                        returnIntent.putExtra("save", true);
                                        startActivity(returnIntent);

                                    }
                                });
                                findViewById(R.id.noselect).setVisibility(View.GONE);
                                info.setVisibility(View.VISIBLE);

                            } catch (Exception e) {
                                Log.e(CONFIG.TAG, e.toString());
                            }
                            return true;
                        }

                    });
                }
            }
            if(isTicket) {
                setMainTicketMarker();
            } else {
                setMainMarker();
            }
            if (flag_use_location) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_LOCATION);
                    }
                } else {
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }

        } else {
            Toast.makeText(MapActivity.this, getString(R.string.not_support_device), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSION_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!Util.chkLocationService(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "기기의 설정-위치 속성을 활성화해야 내위치 확인 기능이 정상 작동합니다.", Toast.LENGTH_SHORT).show();
            }
            Util.setPreferenceValues(_preferences, "flag_use_location", true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                mGoogleMap.setMyLocationEnabled(true);
            }

        } else {
            Util.setPreferenceValues(_preferences, "flag_use_location", false);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public class MapWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private Context context = null;

        protected MapWindowAdapter(Context context) {
            this.context = context;
        }

        // Hack to prevent info window from displaying: use a 0dp/0dp frame
        @Override
        public View getInfoWindow(Marker marker) {
            View v = ((Activity) context).getLayoutInflater().inflate(R.layout.map_info_window, null);
            if(isTicket && marker.getSnippet().equals("0")){
                v.findViewById(R.id.txt).setBackgroundColor(Color.parseColor("#4f2680"));
            }
            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.map_info_window, null);
            if(isTicket && marker.getSnippet().equals("0")){
                view.findViewById(R.id.txt).setBackgroundColor(Color.parseColor("#4f2680"));
            }
            return view;
        }
    }
}
