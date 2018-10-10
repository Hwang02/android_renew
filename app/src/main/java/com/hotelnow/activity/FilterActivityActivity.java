package com.hotelnow.activity;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.hotelnow.R;
import com.hotelnow.fragment.model.ActivityThemeItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class FilterActivityActivity extends Activity {

    private FlowLayout filter1, filter2;
    private String[] orderbyarr;
    private List<ActivityThemeItem> facilityarr;
    private DbOpenHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activity_filter);

        filter1 = (FlowLayout) findViewById(R.id.filter1); // 정렬
        filter2 = (FlowLayout) findViewById(R.id.filter2); // 액티비티 테마

        dbHelper = new DbOpenHelper(this);

        orderbyarr = getResources().getStringArray(R.array.list_orderby);
        facilityarr = dbHelper.selectAllActivityTheme();

        setOrderby();
        setFacility();
    }

    private void setFacility() {
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<facilityarr.size();i++){
            CheckBox ch = new CheckBox(FilterActivityActivity.this);
            ch.setId(i);
            ch.setTag(facilityarr.get(i).getQcategory_id());
            ch.setText(facilityarr.get(i).getQcategory_ko());
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
    private void setOrderby(){
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_selected}, new int[]{-android.R.attr.state_selected}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<orderbyarr.length;i++){
            CheckBox ch = new CheckBox(FilterActivityActivity.this);
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
}
