package com.hotelnow.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.hotelnow.adapter.MapActivityAdapter;
import com.hotelnow.adapter.MapHotelAdapter;
import com.hotelnow.utils.GlobalUtils;
import com.hotelnow.utils.LogUtil;
import com.thebrownarrow.customstyledmap.CustomMap;
import com.thebrownarrow.model.MyLocation;

import java.util.ArrayList;

public class MapAcvitityActivity extends AppCompatActivity {

    private ArrayList<MyLocation> latLngsArrayList;

    private Animation slide_out_down, slide_in_up;
    public static GoogleMap map;
    private SupportMapFragment supportMapFragment;
    private Marker previousSelectedMarker;
    private static ViewPager event_pager;
    private MapActivityAdapter mapAdapter;
    private int prePosition = 0;

    CustomMap customMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MapAcvitityActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MapAcvitityActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();

                finish();
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
        setContents();
    }

    private void setContents() {
        latLngsArrayList = new ArrayList<>();
        latLngsArrayList.clear();
        //테스트 용
        latLngsArrayList = GlobalUtils.getLatLongArray();

        slide_out_down = AnimationUtils.loadAnimation(MapAcvitityActivity.this, R.anim.slide_out_down);
        slide_in_up = AnimationUtils.loadAnimation(MapAcvitityActivity.this, R.anim.slide_in_up);

        event_pager = (ViewPager) findViewById(R.id.event_pager);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                customMap = new CustomMap(map, latLngsArrayList, MapAcvitityActivity.this, false);

                try {
//                    customMap.setCustomMapStyle(R.drawable.map_marker_stay);
                    // Customise the styling of the base map using a JSON object defined in a raw resource file.
                } catch (Resources.NotFoundException e) {
                    Log.e("Explore detail activity", "Can't find style. Error: " + e);
                }

                handleMap();
                customMap.addCustomPin();
                mapAdapter = new MapActivityAdapter(MapAcvitityActivity.this, latLngsArrayList);
                event_pager.setAdapter(mapAdapter);
                event_pager.setClipToPadding(false);
                event_pager.setOffscreenPageLimit(0);

                event_pager.setPageMargin(20);

                customMap.addSelectedCustomPin(0);

            }
        });


        event_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                MyLocation location = latLngsArrayList.get(position);
                Point mappoint = map.getProjection().toScreenLocation(
                        new LatLng(location.getLatitude(), location.getLongitude()));
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

    private void handleMap() {
        if (map != null) {
            if (ActivityCompat.checkSelfPermission(MapAcvitityActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapAcvitityActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        slide_in_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation arg0) {
                                event_pager.setVisibility(View.VISIBLE);
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
                    customMap = new CustomMap(map, latLngsArrayList, MapAcvitityActivity.this, false);
                    customMap.addSelectedCustomPin(0);

                    handleMap();

                }
            });

        }
    }
}
