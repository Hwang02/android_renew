package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;

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
    private String strdate, strdate2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_area);

        dbHelper = new DbOpenHelper(this);
        mCity = dbHelper.selectAllCity();
        mSubCity = new ArrayList<SubCityItem>();

        // tabview
        mSelectAdapter = new AreaSelectAdapter(this, 0, mCity);
        select_view = (ListView) findViewById(R.id.select_view);
        select_view.setAdapter(mSelectAdapter);
        // tabview

        // resultview
        mResultAdapter = new AreaResultAdapter(this, 0, mSubCity);
        result_view = (ListView) findViewById(R.id.result_view);
        result_view.setAdapter(mResultAdapter);
        // end resultview

        // 홈 - 호텔 진입시
        strdate = getIntent().getStringExtra("ec_date");
        strdate2 = getIntent().getStringExtra("ee_date");

        // 최근 본 지역
        month_list = (LinearLayout) findViewById(R.id.month_list);

        select_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getMonthList();
                    mSubCity.clear();
                    findViewById(R.id.layout_noarea).setVisibility(View.VISIBLE);
                    result_view.setVisibility(View.GONE);
                } else {
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

                TuneWrap.Event("city_stay", cityKo, subCityKo);

                Intent intent = new Intent();
                intent.putExtra("city", mSubCity.get(position).getSubcity_ko());
                intent.putExtra("city_code", mCity.get(tabPostion).getCity_code());
                intent.putExtra("subcity_code", mSubCity.get(position).getSubcity_code());
                intent.putExtra("ec_date", TextUtils.isEmpty(strdate) ? "" : strdate);
                intent.putExtra("ee_date", TextUtils.isEmpty(strdate2) ? "" : strdate2);
                setResult(80, intent);
                finish();
            }
        });

        //default 0번째 선택
        select_view.performItemClick(select_view, 0, select_view.getAdapter().getItemId(0));
        findViewById(R.id.title_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                finish();
            }
        });

        getMonthList();
    }

    public void getMonthList() {
        month_list.removeAllViews();
        mRecentCity = dbHelper.selectAllRecentCity("H");
        if (mRecentCity.size() > 0) {
            for (int i = 0; i < mRecentCity.size(); i++) {
                View view_recent = LayoutInflater.from(AreaHotelActivity.this).inflate(R.layout.layout_recent_area_item, null);
                TextView tv_recent = (TextView) view_recent.findViewById(R.id.tv_recent);
                ImageView ico_location = (ImageView) view_recent.findViewById(R.id.ico_location);
                ico_location.setVisibility(View.VISIBLE);
                tv_recent.setText(mRecentCity.get(i).getSel_city_ko() + " > " + mRecentCity.get(i).getSel_subcity_ko());
                view_recent.setTag(i);
                view_recent.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        LogUtil.e("xxxxx", (int) v.getTag() + "");
                        Intent intent = new Intent();
                        intent.putExtra("city", mRecentCity.get((int) v.getTag()).getSel_subcity_ko());
                        intent.putExtra("city_code", mRecentCity.get((int) v.getTag()).getSel_city_id());
                        intent.putExtra("subcity_code", mRecentCity.get((int) v.getTag()).getSel_subcity_id());
                        intent.putExtra("ec_date", TextUtils.isEmpty(strdate) ? "" : strdate);
                        intent.putExtra("ee_date", TextUtils.isEmpty(strdate2) ? "" : strdate2);
                        setResult(80, intent);
                        finish();
                    }
                });
                month_list.addView(view_recent);
            }
        } else {
//            최근 조회 지역이 없습니다
            View view_recent = LayoutInflater.from(AreaHotelActivity.this).inflate(R.layout.layout_recent_area_item, null);
            ImageView ico_location = (ImageView) view_recent.findViewById(R.id.ico_location);
            TextView tv_recent = (TextView) view_recent.findViewById(R.id.tv_recent);
            ico_location.setVisibility(View.GONE);
            tv_recent.setText("최근 조회 지역이 없습니다");
            month_list.addView(view_recent);
        }
    }
}
