package com.hotelnow.activity;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.hotelnow.R;
import com.hotelnow.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.hotelnow.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.hotelnow.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.hotelnow.utils.FlowLayout;

public class FilterHotelActivity extends Activity {

    private FlowLayout filter1, filter2, filter3, filter5, filter6;
    private String[] orderbyarr, categorytextarr, categorycodearr, usepersonarr, ratearr, facilityarr;
    private CrystalRangeSeekbar rangeSeekbar;
    private TextView select_price;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotel_filter);

        filter1 = (FlowLayout) findViewById(R.id.filter1); // 정렬
        filter2 = (FlowLayout) findViewById(R.id.filter2); // 호텔등급 및 그외 유형
        filter3 = (FlowLayout) findViewById(R.id.filter3); // 투숙인원
        filter5 = (FlowLayout) findViewById(R.id.filter5); // 리뷰평점
        filter6 = (FlowLayout) findViewById(R.id.filter6); // 부대시설

        orderbyarr = getResources().getStringArray(R.array.list_orderby);
        categorytextarr = getResources().getStringArray(R.array.category_text);
        categorycodearr = getResources().getStringArray(R.array.category_code);
        usepersonarr = getResources().getStringArray(R.array.use_person);
        ratearr = getResources().getStringArray(R.array.review_rate);
        facilityarr = getResources().getStringArray(R.array.facility_text);
        rangeSeekbar = (CrystalRangeSeekbar) findViewById(R.id.rangeSeekbar1);
        select_price = (TextView) findViewById(R.id.select_price);

        setOrderby();
        setCategory();
        setUsePerson();
        setRangePrice();
        setRate();
        setFacility();
    }

    // 단일 선택
    private void setOrderby(){
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<orderbyarr.length;i++){
            CheckBox ch = new CheckBox(FilterHotelActivity.this);
            ch.setId(i);
            ch.setTag(i);
            ch.setText(orderbyarr[i]);
            ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            ch.setTextColor(Color.WHITE);
            ch.setGravity(Gravity.CENTER);
            ch.setBackgroundResource(R.drawable.style_checkbox_filter);
            ch.setButtonDrawable(android.R.color.transparent);
            ch.setTextColor(myColorStateList);

            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                    for(int j = 0; j<filter1.getChildCount(); j++){
                        filter1.getChildAt(j).setSelected(false);
                    }
                    filter1.getChildAt((int)v.getTag()).setSelected(true);
                }
            });
// 이전 부분 적용
//            if(configCategories.contains(categoriCodes[i])){
//                ch.setChecked(true);
//            }

            filter1.addView(ch);
        }
    }

    // 다중선탁
    private void setCategory(){
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<categorytextarr.length;i++){
            CheckBox ch = new CheckBox(FilterHotelActivity.this);
            ch.setId(i);
            ch.setTag(categorycodearr[i]);
            ch.setText(categorytextarr[i]);
            ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            ch.setTextColor(Color.WHITE);
            ch.setGravity(Gravity.CENTER);
            ch.setBackgroundResource(R.drawable.style_checkbox_filter2);
            ch.setButtonDrawable(android.R.color.transparent);
            ch.setTextColor(myColorStateList);

            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
//                    for(int j = 0; j<filter2.getChildCount(); j++){
//                        filter2.getChildAt(j).setSelected(false);
//                    }
//                    filter2.getChildAt((int)v.getTag()).setSelected(true);
                }
            });
// 이전 부분 적용
//            if(configCategories.contains(categoriCodes[i])){
//                ch.setChecked(true);
//            }

            filter2.addView(ch);
        }
    }

    // 단일 선택
    private void setUsePerson(){
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<usepersonarr.length;i++){
            CheckBox ch = new CheckBox(FilterHotelActivity.this);
            ch.setId(i);
            ch.setTag(i);
            ch.setText(usepersonarr[i]);
            ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            ch.setTextColor(Color.WHITE);
            ch.setGravity(Gravity.CENTER);
            ch.setBackgroundResource(R.drawable.style_checkbox_filter);
            ch.setButtonDrawable(android.R.color.transparent);
            ch.setTextColor(myColorStateList);

            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                    for(int j = 0; j<filter3.getChildCount(); j++){
                        filter3.getChildAt(j).setSelected(false);
                    }
                    filter3.getChildAt((int)v.getTag()).setSelected(true);
                }
            });
// 이전 부분 적용
//            if(configCategories.contains(categoriCodes[i])){
//                ch.setChecked(true);
//            }

            filter3.addView(ch);
        }
    }

    private void setRangePrice(){
        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {

                if (String.valueOf(maxValue).equals("60")) {
                    select_price.setText(minValue+"원 ~"+maxValue+"원 이상");
                } else {
                    select_price.setText(minValue+"원 ~"+maxValue+"원 이상");
                }
            }
        });

        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });
    }

    private void setRate(){
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<ratearr.length;i++){
            View child = getLayoutInflater().inflate(R.layout.layout_rate_item, null);
            child.setId(i);
            child.setTag(i);
            TextView mTitle = (TextView)child.findViewById(R.id.rate_title);
            mTitle.setText(ratearr[i]);
            mTitle.setTextColor(myColorStateList);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int j = 0; j<filter5.getChildCount(); j++){
                        filter5.getChildAt(j).setSelected(false);
                    }
                    filter5.getChildAt((int)v.getTag()).setSelected(true);
                }
            });
// 이전 부분 적용
//            if(configCategories.contains(categoriCodes[i])){
//                ch.setChecked(true);
//            }

            filter5.addView(child);
        }
    }

    private void setFacility(){
        ColorStateList fColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        int[] mFaArray = { R.drawable.facility_0,R.drawable.facility_1,R.drawable.facility_2,R.drawable.facility_3,R.drawable.facility_4,R.drawable.facility_5,R.drawable.facility_6,R.drawable.facility_7,R.drawable.facility_8,R.drawable.facility_9,R.drawable.facility_10,R.drawable.facility_11,R.drawable.facility_12,R.drawable.facility_13,R.drawable.facility_14,R.drawable.facility_15,R.drawable.facility_16,R.drawable.facility_17,R.drawable.facility_18,R.drawable.facility_19,R.drawable.facility_20,R.drawable.facility_21,R.drawable.facility_22,R.drawable.facility_23,R.drawable.facility_24,R.drawable.facility_25,R.drawable.facility_26,R.drawable.facility_27,R.drawable.facility_28,R.drawable.facility_29,R.drawable.facility_30,R.drawable.facility_31,R.drawable.facility_32,R.drawable.facility_33,R.drawable.facility_34 };

        for(int i=0;i<facilityarr.length;i++){
            View child = getLayoutInflater().inflate(R.layout.layout_facility_item, null);
            child.setId(i);
            child.setTag(i);
            TextView facility_title = (TextView)child.findViewById(R.id.facility_title);
            ImageView facility_icon = (ImageView)child.findViewById(R.id.facility_icon);
            facility_title.setText(facilityarr[i]);
            facility_title.setTextColor(fColorStateList);
            Drawable drawable = getResources().getDrawable(mFaArray[i]);
            facility_icon.setBackground(drawable);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(filter6.getChildAt((int)v.getTag()).isSelected()){
                        filter6.getChildAt((int)v.getTag()).setSelected(false);
                    }
                    else {
                        filter6.getChildAt((int) v.getTag()).setSelected(true);
                    }
                }
            });
//            CheckBox ch = new CheckBox(this);
//            ch.setId(i);
//            ch.setTag(String.valueOf(i));
//            ch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            ch.setText(facilityarr[i]);
//            ch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//            ch.setTextColor(Color.WHITE);
//            ch.setGravity(Gravity.CENTER);
//            ch.setButtonDrawable(android.R.color.transparent);
//            ch.setTextColor(fColorStateList);
//            ch.setBackgroundResource(R.drawable.style_checkbox_filter2);
//            Drawable drawable = getResources().getDrawable(mFaArray[i]);
//            ch.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
//            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
//                     //TODO Auto-generated method stub
//                    if(isChecked == true){
//                        facilities.add(v.getTag().toString());
//                    } else {
//                        facilities.remove(v.getTag().toString());
//                    }
//
//                }
//            });

//            if(configFacilities.contains(String.valueOf(i))){
//                ch.setChecked(true);
//            }

            filter6.addView(child);
        }
    }

}
