package com.hotelnow.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hotelnow.R;
import com.hotelnow.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.hotelnow.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.hotelnow.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.FlowLayout;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.TuneWrap;

import java.util.ArrayList;
import java.util.List;

public class FilterHotelActivity extends Activity {

    private FlowLayout filter1, filter2, filter3, filter5;
    private String[] orderbyarr, orderbycodearr, categorytextarr, categorycodearr, usepersonarr, ratearr, facilityarr;
    private CrystalRangeSeekbar rangeSeekbar;
    private TextView select_price;
    private Button btn_save;
    private LinearLayout btn_refresh;
    private String mOrderby = "";
    private LinearLayout filter6_1, filter6_2, filter6_3, filter6_4, filter6_5, filter6_6, filter6_7;
    List<String> categories = new ArrayList<String>();
    List<String> configCategories = new ArrayList<String>();
    List<String> facilities = new ArrayList<String>();
    List<String> configFacilities = new ArrayList<String>();
    List<String> usepersons = new ArrayList<String>();
    List<String> configUsepersons = new ArrayList<String>();
    String minVal = "0";
    String maxVal = "600000";
    String mRate = "";
    LocationManager locManager; // 위치 정보 프로바이더
    LocationListener locationListener; // 위치 정보가 업데이트시 동작
    String lat = "", lng = "";
    ProgressDialog dialog;
    private boolean is_reset = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotel_filter);

        filter1 = (FlowLayout) findViewById(R.id.filter1); // 정렬
        filter2 = (FlowLayout) findViewById(R.id.filter2); // 호텔등급 및 그외 유형
        filter3 = (FlowLayout) findViewById(R.id.filter3); // 투숙인원
        filter5 = (FlowLayout) findViewById(R.id.filter5); // 리뷰평점
        filter6_1 = (LinearLayout) findViewById(R.id.filter6_1); // 부대시설
        filter6_2 = (LinearLayout) findViewById(R.id.filter6_2); // 부대시설
        filter6_3 = (LinearLayout) findViewById(R.id.filter6_3); // 부대시설
        filter6_4 = (LinearLayout) findViewById(R.id.filter6_4); // 부대시설
        filter6_5 = (LinearLayout) findViewById(R.id.filter6_5); // 부대시설
        filter6_6 = (LinearLayout) findViewById(R.id.filter6_6); // 부대시설
        filter6_7 = (LinearLayout) findViewById(R.id.filter6_7); // 부대시설

        orderbyarr = getResources().getStringArray(R.array.list_orderby);
        categorytextarr = getResources().getStringArray(R.array.category_text);
        categorycodearr = getResources().getStringArray(R.array.category_code);
        usepersonarr = getResources().getStringArray(R.array.use_person);
        ratearr = getResources().getStringArray(R.array.review_rate);
        orderbycodearr = getResources().getStringArray(R.array.list_orderby_code);
        facilityarr = getResources().getStringArray(R.array.facility_text);
        rangeSeekbar = (CrystalRangeSeekbar) findViewById(R.id.rangeSeekbar1);
        select_price = (TextView) findViewById(R.id.select_price);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_refresh = (LinearLayout) findViewById(R.id.btn_refresh);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locManager.removeUpdates(locationListener);
                CONFIG.lat = location.getLatitude() + "";
                CONFIG.lng = location.getLongitude() + "";
                dialog.dismiss();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                CONFIG.sel_orderby = mOrderby;
                CONFIG.sel_category = (categories != null && categories.size() > 0) ? TextUtils.join("|", categories) : null; // 등급
                CONFIG.sel_facility = (facilities != null && facilities.size() > 0) ? TextUtils.join("|", facilities) : null; // 부대시설
                CONFIG.sel_useperson = (usepersons != null && usepersons.size() > 0) ? TextUtils.join("|", usepersons) : null; // 투숙인원
                CONFIG.sel_max = maxVal; // 가격
                CONFIG.sel_min = minVal; // 가격
                CONFIG.sel_rate = mRate; // 평점
                String cate = "", useper = "";
                if (CONFIG.sel_category != null) {
                    cate = CONFIG.sel_category.replace("|", ",");
                }

                if (CONFIG.sel_useperson != null) {
                    useper = CONFIG.sel_useperson.replace("|", ",");
                }

                TuneWrap.Event("filter", cate, CONFIG.sel_min + "~" + CONFIG.sel_max, useper, CONFIG.sel_rate);
//                if(is_reset)
//                    intent.putExtra("is_reset", is_reset);
                setResult(80, intent);
                finish();
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CONFIG.sel_orderby = "recommendation";
                CONFIG.sel_category = null;
                CONFIG.sel_facility = null;
                CONFIG.sel_useperson = null;
                CONFIG.sel_max = "600000";
                maxVal = "600000";
                minVal = "0";
                CONFIG.sel_min = "0";
                CONFIG.sel_rate = null;
                filter1.removeAllViews();
                filter2.removeAllViews();
                filter3.removeAllViews();
                filter5.removeAllViews();
                filter6_1.removeAllViews();
                filter6_2.removeAllViews();
                filter6_3.removeAllViews();
                filter6_4.removeAllViews();
                filter6_5.removeAllViews();
                filter6_6.removeAllViews();
                filter6_7.removeAllViews();
                categories.clear();
                facilities.clear();
                usepersons.clear();
                configFacilities.clear();
                configCategories.clear();
                configUsepersons.clear();
                mRate = null;
                mOrderby = "recommendation";
                rangeSeekbar.setNormalizedMinValue(Integer.valueOf("0"));
                rangeSeekbar.setNormalizedMaxValue(Integer.valueOf("100"));
                setOrderby();
                setCategory();
                setUsePerson();
                setRangePrice();
                setRate();
                filter6_1.post(new Runnable() {
                    @Override
                    public void run() {
                        setFacility();
                    }
                });

                select_price.setText("0만원 ~ 60만원 이상");
                is_reset = true;
            }
        });

        mOrderby = CONFIG.sel_orderby;
        mRate = CONFIG.sel_rate;

        // 카테고리
        if (CONFIG.sel_category != null) {
            String[] tmpCat = CONFIG.sel_category.split("\\|");

            for (int i = 0; i < tmpCat.length; i++) {
                configCategories.add(tmpCat[i]);
            }
        }

        // 부대시설
        if (CONFIG.sel_facility != null) {
            String[] tmpFac = CONFIG.sel_facility.split("\\|");

            for (int i = 0; i < tmpFac.length; i++) {
                configFacilities.add(tmpFac[i]);
            }
        }

        // 수용인원
        if (CONFIG.sel_useperson != null) {
            String[] tmpFac = CONFIG.sel_useperson.split("\\|");

            for (int i = 0; i < tmpFac.length; i++) {
                configUsepersons.add(tmpFac[i]);
            }
        }

        // 선택 금액
        if (CONFIG.sel_max.equals("") && CONFIG.sel_min.equals("")) {
            select_price.setText("0만원 ~ 60만원 이상");
        } else {
            select_price.setText(CONFIG.sel_min.replace("0000", "") + "만원 ~ " + CONFIG.sel_max.replace("0000", "") + "만원 이상");
            rangeSeekbar.setMinStartValue(Integer.valueOf(CONFIG.sel_min.replace("0000", "")));
            rangeSeekbar.setMaxStartValue(Integer.valueOf(CONFIG.sel_max.replace("0000", "")));
            rangeSeekbar.setMinStartValue();
            rangeSeekbar.setMaxStartValue();
        }

        setOrderby();
        setCategory();
        setUsePerson();
        setRangePrice();
        setRate();
        filter6_1.post(new Runnable() {
            @Override
            public void run() {
                setFacility();
            }
        });
    }

    // 단일 선택
    private void setOrderby() {
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}},
                new int[]{getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext)});

        for (int i = 0; i < orderbyarr.length; i++) {
            CheckBox ch = new CheckBox(FilterHotelActivity.this);
//            ch.setId(i);
            ch.setTag(i);
            ch.setText(orderbyarr[i]);
            ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ch.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            ch.setTextColor(Color.WHITE);
            ch.setGravity(Gravity.CENTER);
            ch.setBackgroundResource(R.drawable.style_checkbox_filter);
            ch.setButtonDrawable(android.R.color.transparent);
            ch.setTextColor(myColorStateList);

            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                    for (int j = 0; j < filter1.getChildCount(); j++) {
                        filter1.getChildAt(j).setSelected(false);
                        mOrderby = "";
                    }
                    filter1.getChildAt((int) v.getTag()).setSelected(true);

                    if ((int) v.getTag() == 0)
                        mOrderby = "recommendation";
                    else if ((int) v.getTag() == 1) {
                        mOrderby = "distance";
                        getMyLocation();

                    } else if ((int) v.getTag() == 2)
                        mOrderby = "rate";
                    else if ((int) v.getTag() == 3)
                        mOrderby = "price_low";
                    else if ((int) v.getTag() == 4)
                        mOrderby = "price_high";

                    is_reset = false;
                }
            });
            // 이전 부분 적용
            if (mOrderby != null) {
                if (mOrderby.equals(orderbycodearr[i])) {
                    ch.setSelected(true);
                }
            } else {
                if (i == 0) {
                    ch.setSelected(true);
                    mOrderby = "recommendation";
                }
            }

            filter1.addView(ch);
        }
    }

    // 다중선탁
    private void setCategory() {
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
                new int[]{getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext)});

        for (int i = 0; i < categorytextarr.length; i++) {
            CheckBox ch = new CheckBox(FilterHotelActivity.this);
//            ch.setId(i);
            ch.setTag(categorycodearr[i]);
            ch.setText(categorytextarr[i]);
            ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ch.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            ch.setTextColor(Color.WHITE);
            ch.setGravity(Gravity.CENTER);
            ch.setBackgroundResource(R.drawable.style_checkbox_filter2);
            ch.setButtonDrawable(android.R.color.transparent);
            ch.setTextColor(myColorStateList);

            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                    if (isChecked) {
                        categories.add(v.getTag().toString());
                    } else {
                        categories.remove(v.getTag().toString());
                    }
                    is_reset = false;
                }
            });
            // 이전 부분 적용
            if (configCategories.contains(ch.getTag() + "")) {
                ch.setChecked(true);
            }

            filter2.addView(ch);
        }
    }

    // 다중 선택
    private void setUsePerson() {
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
                new int[]{getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext)});

        for (int i = 0; i < usepersonarr.length; i++) {
            CheckBox ch = new CheckBox(FilterHotelActivity.this);
//            ch.setId(i);
            ch.setTag(i);
            ch.setText(usepersonarr[i]);
            ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ch.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            ch.setTextColor(Color.WHITE);
            ch.setGravity(Gravity.CENTER);
            ch.setBackgroundResource(R.drawable.style_checkbox_filter2);
            ch.setButtonDrawable(android.R.color.transparent);
            ch.setTextColor(myColorStateList);

            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                    if (isChecked) {
                        usepersons.add(v.getTag().toString());
                    } else {
                        usepersons.remove(v.getTag().toString());
                    }
                    is_reset = false;
                }
            });
            // 이전 부분 적용
            if (configUsepersons.contains(i + "")) {
                ch.setChecked(true);
            }

            filter3.addView(ch);
        }
    }

    private void setRangePrice() {
        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {

                if (String.valueOf(maxValue).equals("60")) {
                    select_price.setText(minValue + "만원 ~ " + maxValue + "만원 이상");
                } else {
                    select_price.setText(minValue + "만원 ~ " + maxValue + "만원 이상");
                }
                maxVal = String.valueOf(maxValue) + "0000";
                if (Integer.valueOf(String.valueOf(minValue)) == 0)
                    minVal = String.valueOf(minValue);
                else
                    minVal = String.valueOf(minValue) + "0000";

                is_reset = false;
            }
        });

        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });
    }

    private void setRate() {
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}},
                new int[]{getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext)});

        for (int i = 0; i < ratearr.length; i++) {
            View child = getLayoutInflater().inflate(R.layout.layout_rate_item, null);
//            child.setId(i);
            child.setTag(i);
            TextView mTitle = (TextView) child.findViewById(R.id.rate_title);
            mTitle.setText(ratearr[i]);
            mTitle.setTextColor(myColorStateList);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (filter5.getChildAt((int) v.getTag()).isSelected()) {
                        for (int j = 0; j < filter5.getChildCount(); j++) {
                            filter5.getChildAt(j).setSelected(false);
                        }
                        mRate = "";
                    } else {
                        for (int j = 0; j < filter5.getChildCount(); j++) {
                            filter5.getChildAt(j).setSelected(false);
                        }
                        filter5.getChildAt((int) v.getTag()).setSelected(true);
                        mRate = v.getTag().toString();
                    }
                    is_reset = false;
                }
            });
            // 이전 부분 적용
            if (mRate != null && mRate.equals(i + "")) {
                child.setSelected(true);
                mRate = i + "";
            }

            filter5.addView(child);
        }
    }

    private void setFacility() {
        ColorStateList fColorStateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}},
                new int[]{getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext)});

        int[] mFaArray = {R.drawable.facility_0, R.drawable.facility_1, R.drawable.facility_2, R.drawable.facility_3, R.drawable.facility_4, R.drawable.facility_5, R.drawable.facility_6, R.drawable.facility_7, R.drawable.facility_8, R.drawable.facility_9, R.drawable.facility_10, R.drawable.facility_11, R.drawable.facility_12, R.drawable.facility_13, R.drawable.facility_14, R.drawable.facility_15, R.drawable.facility_16, R.drawable.facility_17, R.drawable.facility_18, R.drawable.facility_19, R.drawable.facility_20, R.drawable.facility_21, R.drawable.facility_22, R.drawable.facility_23, R.drawable.facility_24, R.drawable.facility_25, R.drawable.facility_26, R.drawable.facility_27, R.drawable.facility_28, R.drawable.facility_29, R.drawable.facility_30, R.drawable.facility_31, R.drawable.facility_32, R.drawable.facility_33, R.drawable.facility_34};

        FlowLayout.LayoutParams lparam = new FlowLayout.LayoutParams(
                FlowLayout.LayoutParams.MATCH_PARENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        lparam.width = (int) (filter6_1.getWidth() / 5);
        LogUtil.e("xxxx", lparam.width + "");

        for (int i = 0; i < facilityarr.length; i++) {
            View child = getLayoutInflater().inflate(R.layout.layout_facility_item, null);
//            child.setId(i);
            child.setTag(i);
            final TextView facility_title = (TextView) child.findViewById(R.id.facility_title);
            ImageView facility_icon = (ImageView) child.findViewById(R.id.facility_icon);
            facility_title.setText(facilityarr[i]);
            facility_title.setTextColor(fColorStateList);
            Drawable drawable = getResources().getDrawable(mFaArray[i]);
            facility_icon.setBackground(drawable);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((int) v.getTag() <= 4) {
                        if (filter6_1.getChildAt((int) v.getTag()).isSelected()) {
                            filter6_1.getChildAt((int) v.getTag()).setSelected(false);
                            facilities.remove(v.getTag().toString());
                        } else {
                            filter6_1.getChildAt((int) v.getTag()).setSelected(true);
                            facilities.add(v.getTag().toString());
                        }
                    } else if ((int) v.getTag() > 4 && (int) v.getTag() <= 9) {
                        if (filter6_2.getChildAt((int) v.getTag() - 5).isSelected()) {
                            filter6_2.getChildAt((int) v.getTag() - 5).setSelected(false);
                            facilities.remove(v.getTag().toString());
                        } else {
                            filter6_2.getChildAt((int) v.getTag() - 5).setSelected(true);
                            facilities.add(v.getTag().toString());
                        }
                    } else if ((int) v.getTag() > 9 && (int) v.getTag() <= 14) {
                        if (filter6_3.getChildAt((int) v.getTag() - 10).isSelected()) {
                            filter6_3.getChildAt((int) v.getTag() - 10).setSelected(false);
                            facilities.remove(v.getTag().toString());
                        } else {
                            filter6_3.getChildAt((int) v.getTag() - 10).setSelected(true);
                            facilities.add(v.getTag().toString());
                        }
                    } else if ((int) v.getTag() > 14 && (int) v.getTag() <= 19) {
                        if (filter6_4.getChildAt((int) v.getTag() - 15).isSelected()) {
                            filter6_4.getChildAt((int) v.getTag() - 15).setSelected(false);
                            facilities.remove(v.getTag().toString());
                        } else {
                            filter6_4.getChildAt((int) v.getTag() - 15).setSelected(true);
                            facilities.add(v.getTag().toString());
                        }
                    } else if ((int) v.getTag() > 19 && (int) v.getTag() <= 24) {
                        if (filter6_5.getChildAt((int) v.getTag() - 20).isSelected()) {
                            filter6_5.getChildAt((int) v.getTag() - 20).setSelected(false);
                            facilities.remove(v.getTag().toString());
                        } else {
                            filter6_5.getChildAt((int) v.getTag() - 20).setSelected(true);
                            facilities.add(v.getTag().toString());
                        }
                    } else if ((int) v.getTag() > 24 && (int) v.getTag() <= 29) {
                        if (filter6_6.getChildAt((int) v.getTag() - 25).isSelected()) {
                            filter6_6.getChildAt((int) v.getTag() - 25).setSelected(false);
                            facilities.remove(v.getTag().toString());
                        } else {
                            filter6_6.getChildAt((int) v.getTag() - 25).setSelected(true);
                            facilities.add(v.getTag().toString());
                        }
                    } else if ((int) v.getTag() > 29 && (int) v.getTag() <= 34) {
                        if (filter6_7.getChildAt((int) v.getTag() - 30).isSelected()) {
                            filter6_7.getChildAt((int) v.getTag() - 30).setSelected(false);
                            facilities.remove(v.getTag().toString());
                        } else {
                            filter6_7.getChildAt((int) v.getTag() - 30).setSelected(true);
                            facilities.add(v.getTag().toString());
                        }
                    }
                    is_reset = false;
                }
            });

            // 이전 부분 적용
            if (configFacilities.contains(i + "")) {
                child.setSelected(true);
                facilities.add(i + "");

            }

            if (i <= 4) {
                filter6_1.addView(child, lparam);
            } else if (i > 4 && i <= 9) {
                filter6_2.addView(child, lparam);
            } else if (i > 9 && i <= 14) {
                filter6_3.addView(child, lparam);
            } else if (i > 14 && i <= 19) {
                filter6_4.addView(child, lparam);
            } else if (i > 19 && i <= 24) {
                filter6_5.addView(child, lparam);
            } else if (i > 24 && i <= 29) {
                filter6_6.addView(child, lparam);
            } else if (i > 29 && i <= 34) {
                filter6_7.addView(child, lparam);
            }
        }
    }

    private void getMyLocation() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(FilterHotelActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(FilterHotelActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(FilterHotelActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        2000, 0, locationListener);
                CONFIG.lat = "37.506839";
                CONFIG.lng = "127.066234";

                dialog = new ProgressDialog(FilterHotelActivity.this);
                dialog.setMessage(getString(R.string.location_loading));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(FilterHotelActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
                CONFIG.lat = "37.506839";
                CONFIG.lng = "127.066234";
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("현재 위치값을 얻어오기 위해 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }
}
