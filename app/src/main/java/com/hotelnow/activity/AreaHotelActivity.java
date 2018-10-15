package com.hotelnow.activity;

import android.app.Activity;
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

public class AreaHotelActivity extends Activity {
    private DbOpenHelper dbHelper;
    private List<CityItem> mCity;
    private List<SubCityItem> mSubCity;
    private List<RecentCityItem> mRecentCity;
    private ListView select_view, result_view;
    private AreaSelectAdapter mSelectAdapter;
    private AreaResultAdapter mResultAdapter;
    private LinearLayout month_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_area);

        dbHelper = new DbOpenHelper(this);
        mCity = dbHelper.selectAllCity();
        mSubCity = new ArrayList<SubCityItem>();

        // tabview
        mSelectAdapter = new AreaSelectAdapter( this, 0, mCity);
        select_view = (ListView) findViewById(R.id.select_view);
        select_view.setAdapter(mSelectAdapter);
        // tabview

        // resultview
        mResultAdapter = new AreaResultAdapter( this, 0, mSubCity);
        result_view = (ListView) findViewById(R.id.result_view);
        result_view.setAdapter(mResultAdapter);
        // end resultview

        // 최근 본 지역
        month_list = (LinearLayout) findViewById(R.id.month_list);

        select_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    getMonthList();
                    mSubCity.clear();
                    findViewById(R.id.layout_noarea).setVisibility(View.VISIBLE);
                    result_view.setVisibility(View.GONE);
                }
                else {
                    mSubCity.clear();
                    findViewById(R.id.layout_noarea).setVisibility(View.GONE);
                    result_view.setVisibility(View.VISIBLE);
                    mSubCity.addAll(dbHelper.selectAllSubCity(mCity.get(position).getCity_code()));
                    mResultAdapter.notifyDataSetChanged();
                }

                view.setSelected(true);
            }
        });

        result_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int tabPostion = select_view.getCheckedItemPosition();
                String cityCode = mCity.get(tabPostion).getCity_code();
                String cityKo = mCity.get(tabPostion).getCity_ko();
                String subCityCode = mSubCity.get(position).getSubcity_code();
                String subCityKo = mSubCity.get(position).getSubcity_ko();
                String option = "H";
                dbHelper.insertRecentCity(cityCode, cityKo, subCityCode, subCityKo, option);
//                getMonthList();
                // 검색페이지 이동
            }
        });

        //default 0번째 선택
        select_view.performItemClick(select_view,0, select_view.getAdapter().getItemId(0));

        getMonthList();
    }

    public void getMonthList(){
        month_list.removeAllViews();
        mRecentCity = dbHelper.selectAllRecentCity("H");
        for(int i =0; i<mRecentCity.size(); i++){
            View view_recent = LayoutInflater.from(AreaHotelActivity.this).inflate(R.layout.layout_recent_area_item, null);
            TextView tv_recent = (TextView) view_recent.findViewById(R.id.tv_recent);
            tv_recent.setText(mRecentCity.get(i).getSel_subcity_ko());
            view_recent.setTag(i);
            view_recent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.e("xxxxx", (int)v.getTag()+"");
                    // 검색페이지 이동
                }
            });
            month_list.addView(view_recent);
        }
    }
}