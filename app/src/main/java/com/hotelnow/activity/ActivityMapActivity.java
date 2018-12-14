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
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.LatLngBounds;
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

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by susia on 15. 12. 24..
 */
public class ActivityMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private SharedPreferences _preferences;
    private Marker preMarker = null;
    private GoogleMap mGoogleMap;
    private String hid;
    private String from = "";
    private String lat = "37.506292";
    private String lng = "127.053612";
    private String hotel_name;
    private String category;
    private boolean flag_use_location;
    private String sel_hotel_id;
    private DialogConfirm dialogConfirm;
    private Marker main;
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
    private String deal_name="";
    private String url;
    private int zoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_map);

        Util.setStatusColor(this);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        flag_use_location = _preferences.getBoolean("flag_use_location", false);

        info = (RelativeLayout) findViewById(R.id.info);

        Intent intent = getIntent();
        if(intent != null){
            hid = intent.getStringExtra("hid");
            lat = intent.getStringExtra("lat");
            lng = intent.getStringExtra("lng");
            deal_name = intent.getStringExtra("deal_name");
        }

        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) fragment;

        if (mapFragment == null) {
            Toast.makeText(this, getString(R.string.cant_use_map), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mapFragment.getMapAsync(this);
        }

        tv_title_hotel = (TextView) findViewById(R.id.tv_title_hotel);
        tv_title_hotel.setText(deal_name);

        findViewById(R.id.kimgisa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirm = new DialogConfirm(
                        getString(R.string.alert_notice),
                        getString(R.string.booking_kimkisa_ask)+"\n목적지 : "+deal_name,
                        getString(R.string.alert_no),
                        getString(R.string.alert_yes),
                        ActivityMapActivity.this,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogConfirm.dismiss();
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = "kimgisa://navigate?name="+deal_name+"&coord_type=wgs84&pos_x="+lng+"&pos_y="+lat+"&return_uri=com.hotelnow.activities.ActLoading&key="+CONFIG.kimgisaKey;
                                String strAppPackage = "com.locnall.KimGiSa";
                                PackageManager pm = getPackageManager();

                                try {
                                    Intent intent = new Intent();
                                    intent.setClassName(strAppPackage, "com.locnall.KimGiSa.Engine.SMS.CremoteActivity");
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);

                                    Toast.makeText(ActivityMapActivity.this, "카카오내비를 구동합니다.", Toast.LENGTH_SHORT).show();
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

    public void getList(){
        setCustomMarkerView();
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        url = CONFIG.ticketUrl+"?order_kind=distance"+"&lat="+lat+"&lng="+lng+"&page=1";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Log.e(CONFIG.TAG, "expection is ", e);
                Toast.makeText(ActivityMapActivity.this, getString(R.string.error_hotel_location), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ActivityMapActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    JSONArray data = obj.getJSONArray("lists");
                    JSONArray feed = new JSONArray(data.toString());

                    for(int i = 0; i < feed.length(); i++) {
                        LatLng hotel = new LatLng(data.getJSONObject(i).getDouble("latitude"), data.getJSONObject(i).getDouble("longitude"));
//                        IconGenerator iconFactory = new IconGenerator(ActivityMapActivity.this);
//                        int color = getResources().getIdentifier(data.getJSONObject(i).getString("category"), "color", getPackageName());
//                        iconFactory.setColor(getResources().getColor(color));
//                        iconFactory.setTextAppearance(R.style.iconGenText);

                        tv_marker.setBackgroundResource(R.drawable.map_marker_price_act);
                        tv_marker.setTextColor(ContextCompat.getColor(ActivityMapActivity.this, R.color.white));
                        tv_marker.setText(Util.numberFormat(data.getJSONObject(i).getInt("sale_price")));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(Util.numberFormat(data.getJSONObject(i).getInt("sale_price")));
                        markerOptions.position(hotel);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(ActivityMapActivity.this, marker_root_view)));

                        mGoogleMap.setInfoWindowAdapter(new MapWindowAdapter(ActivityMapActivity.this));
                        Marker sub = mGoogleMap.addMarker(markerOptions);
                        sub.setTitle(data.getJSONObject(i).getString("name"));
                        sub.setSnippet(data.getJSONObject(i).getString("deal_id"));
                        objs.add(data.getJSONObject(i));
                        markers.add(sub);

                    }
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.e(CONFIG.TAG, "expection is ", e);
                    Toast.makeText(ActivityMapActivity.this, getString(R.string.error_hotel_location), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

            }
        });
    }

    private void setCustomMarkerView() {

        marker_root_view = LayoutInflater.from(this).inflate(R.layout.layout_marker, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }

    public void setUpMap() {
        zoom = 12;
        getList();

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker selmarker) {

                if (selmarker.getSnippet().equals(hid)) {
                    findViewById(R.id.noselect).setVisibility(View.VISIBLE);
                    info.setVisibility(View.GONE);
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
                    tv_marker.setBackgroundResource(R.drawable.map_marker_price_act);
                    tv_marker.setTextColor(ContextCompat.getColor(ActivityMapActivity.this, R.color.white));
                    try {
                        tv_marker.setText(Util.numberFormat(preobj.getInt("sale_price")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    preMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(ActivityMapActivity.this, marker_root_view)));
                }
                preMarker = selmarker;
                preobj = obj;
                selmarker.showInfoWindow();

                try {
                    sel_hotel_id = obj.getString("deal_id");
                    hotel_name = obj.getString("name");
                    String grade_score = obj.getString("grade_score");
                    int sale_price = obj.getInt("sale_price");
                    String category = obj.getString("category");

                    tv_marker.setBackgroundResource(R.drawable.map_marker_price_act_selected);
                    tv_marker.setTextColor(ContextCompat.getColor(ActivityMapActivity.this, R.color.activitytxt));
                    tv_marker.setText(Util.numberFormat(obj.getInt("sale_price")));
                    selmarker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(ActivityMapActivity.this, marker_root_view)));

                    TextView tv_score = (TextView) findViewById(R.id.tv_score);
                    TextView tv_catagory = (TextView) findViewById(R.id.tv_catagory);
                    TextView tv_hotelname = (TextView) findViewById(R.id.tv_hotelname);
                    TextView tv_price = (TextView) findViewById(R.id.tv_price);
                    TextView detail_btn = (TextView) findViewById(R.id.detail_btn);

                    tv_catagory.setText(category);
                    tv_hotelname.setText(hotel_name);
                    tv_price.setText(Util.numberFormat(sale_price));
                    tv_score.setText(grade_score);
                    detail_btn.setText(Html.fromHtml(getResources().getString(R.string.map_detail2)));

                    detail_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent returnIntent = new Intent(ActivityMapActivity.this, DetailActivityActivity.class);
                            returnIntent.putExtra("tid", sel_hotel_id);
                            returnIntent.putExtra("evt", "N");
                            returnIntent.putExtra("save", true);
                            startActivityForResult(returnIntent, 81);

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

        setMainMarker();

        if (flag_use_location) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ActivityCompat.checkSelfPermission(ActivityMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivityMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_LOCATION);
                }
            } else {
                mGoogleMap.setMyLocationEnabled(true);
            }
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
