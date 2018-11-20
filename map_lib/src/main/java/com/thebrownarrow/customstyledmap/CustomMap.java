package com.thebrownarrow.customstyledmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thebrownarrow.model.SearchResultItem;

import java.util.ArrayList;

/**
 * Created by iblinfotech on 04/03/17.
 */

public class CustomMap {
    private GoogleMap googleMap;
    private Context mContext;
    private ArrayList<SearchResultItem> latLngsArrayList;
    private int height = 72;
    private int width = 72;
    private int m_height = 90;
    private int m_width = 90;
    private BitmapDrawable bitmapdraw = null;
    private Bitmap b = null;
    private Bitmap smallMarker = null;
    private Bitmap sel_smallMarker = null;
    private boolean ishotel = true;

    public CustomMap(GoogleMap googleMap, ArrayList<SearchResultItem> latLng, Context context, boolean ishotel) {
        this.googleMap = googleMap;
        this.mContext = context;
        this.latLngsArrayList = latLng;
        this.ishotel = ishotel;
    }

    public void addCustomPin() {
        if (googleMap != null) {
            googleMap.clear();

            for (int i = 0; i < latLngsArrayList.size(); i++) {
                addPin(latLngsArrayList.get(i), i);
            }
        }
    }

    public void addSelectedCustomPin(int position) {
        if (googleMap != null) {
            googleMap.clear();

            for (int i = 0; i < latLngsArrayList.size(); i++) {
                addMarkerSelectedPin(latLngsArrayList.get(i), i, position);
            }
        }
    }

    private static boolean isZooming = false;
    private static boolean isZoomingOut = false;

    public void addPin(SearchResultItem myLocation, int position) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LatLng locationPoint = new LatLng(myLocation.getLatitude(), myLocation.getLonguitude());
        googleMap.setMyLocationEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationPoint));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

        if(ishotel) {
            bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_marker_stay);
        } else {
            bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_marker_activity);
        }
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        googleMap.addMarker(new MarkerOptions().position(locationPoint)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)))
                .setTag(position);
    }

    private void addMarkerSelectedPin(SearchResultItem mLocation, int position, int selectedPosition) {
        LatLng locationPoint = new LatLng(mLocation.getLatitude(), mLocation.getLonguitude());
//        map.moveCamera(CameraUpdateFactory.newLatLng(locationPoint));

        if (position == selectedPosition) {
            if(ishotel) {
                bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_marker_stay_selected);
            } else {
                bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_marker_activity_selected);
            }
            b=bitmapdraw.getBitmap();
            sel_smallMarker = Bitmap.createScaledBitmap(b, m_width, m_height, false);

            googleMap.addMarker(new MarkerOptions().position(locationPoint)
                    .icon(BitmapDescriptorFactory.fromBitmap(sel_smallMarker)))
                    .setTag(position);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 12));
        } else {
            if(ishotel) {
                bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_marker_stay);
            }
            else {
                bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.map_marker_activity);
            }
            b=bitmapdraw.getBitmap();
            smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            googleMap.addMarker(new MarkerOptions().position(locationPoint)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)))
                    .setTag(position);
        }
    }

}
