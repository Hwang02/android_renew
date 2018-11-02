package com.hotelnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.hotelnow.R;
import com.hotelnow.adapter.SectionsPagerAdapter;
import com.hotelnow.utils.LogUtil;

public class SearchResultActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager view_pager;
    SectionsPagerAdapter mSectionsPagerAdapter;
    int m_Selecttab = 0;
    String search_txt, banner_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();

        m_Selecttab = intent.getIntExtra("tab",0);
        search_txt = intent.getStringExtra("search");
        banner_id = intent.getStringExtra("banner_id");

        LogUtil.e("xxxxx", m_Selecttab+"");

        if(TextUtils.isEmpty(banner_id)) LogUtil.e("xxxxx", search_txt);
        LogUtil.e("xxxxx", banner_id+"");

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout.addTab(tabLayout.newTab().setText("숙소"));
        tabLayout.addTab(tabLayout.newTab().setText("액티비티"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if(m_Selecttab == 0) {
            tabLayout.getTabAt(0).select();
        }
        else {
            tabLayout.getTabAt(1).select();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), search_txt, banner_id);
        view_pager.setAdapter(mSectionsPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
