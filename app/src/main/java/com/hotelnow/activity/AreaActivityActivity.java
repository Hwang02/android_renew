package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.adapter.AreaResultAdapter;
import com.hotelnow.adapter.AreaSelectAdapter;
import com.hotelnow.fragment.model.CityItem;
import com.hotelnow.fragment.model.RecentCityItem;
import com.hotelnow.fragment.model.SubCityItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class AreaActivityActivity extends Activity {
    private DbOpenHelper dbHelper;
    private List<CityItem> mCity;
    private List<RecentCityItem> mRecentCity;
    private ListView select_view;
    private AreaSelectAdapter mSelectAdapter;
    private AreaResultAdapter mResultAdapter;
    private LinearLayout month_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_area);

        dbHelper = new DbOpenHelper(this);
        mCity = dbHelper.selectAllActivityCity();

        // tabview
        mSelectAdapter = new AreaSelectAdapter( this, 0, mCity);
        select_view = (ListView) findViewById(R.id.select_view);
        select_view.setAdapter(mSelectAdapter);
        // tabview

        // 최근 본 지역
        month_list = (LinearLayout) findViewById(R.id.month_list);

        select_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int tabPostion = select_view.getCheckedItemPosition();
                String cityCode = mCity.get(tabPostion).getCity_code();
                String cityKo = mCity.get(tabPostion).getCity_ko();
                String subCityCode = "x";
                String subCityKo = "x";
                String option = "A";
                dbHelper.insertRecentCity(cityCode, cityKo, subCityCode, subCityKo, option);

                Intent intent = new Intent();
                intent.putExtra("id", cityCode);
                intent.putExtra("name", cityKo);
                setResult(80, intent);
                finish();
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getMonthList();
    }

    public void getMonthList(){
        month_list.removeAllViews();
        mRecentCity = dbHelper.selectAllRecentCity("A");
        for(int i =0; i<mRecentCity.size(); i++){
            View view_recent = LayoutInflater.from(AreaActivityActivity.this).inflate(R.layout.layout_recent_area_item, null);
            TextView tv_recent = (TextView) view_recent.findViewById(R.id.tv_recent);
            tv_recent.setText(mRecentCity.get(i).getSel_city_ko());
            view_recent.setTag(i);
            view_recent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.e("xxxxx", (int)v.getTag()+"");
                    Intent intent = new Intent();
                    intent.putExtra("id", mRecentCity.get((int)v.getTag()).getSel_city_id());
                    intent.putExtra("name", mRecentCity.get((int)v.getTag()).getSel_city_ko());
                    setResult(80, intent);
                    finish();
                }
            });
            month_list.addView(view_recent);
        }
    }
}
