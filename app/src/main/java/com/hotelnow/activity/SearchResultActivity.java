package com.hotelnow.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hotelnow.R;
import com.hotelnow.adapter.SectionsPagerAdapter;

public class SearchResultActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager view_pager;
    SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout.addTab(tabLayout.newTab().setText("숙소"));
        tabLayout.addTab(tabLayout.newTab().setText("액티비티"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.getTabAt(0).select();

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
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
