package com.hotelnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.detail.HotelFullImageFragment;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;


public class FullImageViewActivityActivity extends FragmentActivity {

    String tid, name;
    Integer idx;
    private TextView tv_title_hotel, img_title, page, img_title2;
    private ViewPager pager;
    private LinearLayout ll_image_list;
    String portraitImgs[];
    String[] caption1;
    String[] caption2;
    static int PAGES = 0;
    static int LOOPS = 1000;
    static int FIRST_PAGE = 0;
    int markNowPosition = 0;
    int markPrevPosition = 0;
    private MyPagerAdapter mPagerAdapter;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);

        page = (TextView) findViewById(R.id.page);
        img_title = (TextView) findViewById(R.id.img_title);
        img_title2 = (TextView) findViewById(R.id.img_title2);
        tv_title_hotel = (TextView) findViewById(R.id.tv_title_hotel);
        ll_image_list = (LinearLayout) findViewById(R.id.ll_image_list);

        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        idx = intent.getIntExtra("idx", 0);
        portraitImgs = intent.getStringArrayExtra("imgs");
        name = intent.getStringExtra("name");
        total = intent.getIntExtra("total", 0);

        tv_title_hotel.setText(name);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getPortraitInfo();
    }

    private void getPortraitInfo() {
        ll_image_list.removeAllViews();
        for (int i = 0; i < total; i++) {
            View child = getLayoutInflater().inflate(R.layout.layout_fullimage_sub_item, null);
            RoundedImageView image = child.findViewById(R.id.image);
            Ion.with(image).load(portraitImgs[i]);
            child.setTag(i);

            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    idx = (int) v.getTag();
                    initPageMark();
                    pager.setCurrentItem(idx);
                }
            });

            ll_image_list.addView(child);
        }

        showPager();
    }


    private void showPager() {
        PAGES = total;
        FIRST_PAGE = PAGES * LOOPS / 2 + idx;

        markNowPosition = idx;

        pager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), portraitImgs);
        pager.setAdapter(mPagerAdapter);
        pager.setCurrentItem(FIRST_PAGE, true);
        pager.setOffscreenPageLimit(3);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                markNowPosition = position % PAGES;
                page.setText(markNowPosition+1+"/"+PAGES);
                markPrevPosition = markNowPosition;
            }

            @Override
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
                markNowPosition = position % PAGES;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        initPageMark();
    }

    private void initPageMark() {
        page.setText(markNowPosition+1+"/"+total);

        markPrevPosition = markNowPosition;
    }


    private class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private String landscapeImgs[];

        public MyPagerAdapter(FragmentManager fm, String[] imgs) {
            super(fm);
            this.landscapeImgs = imgs;
        }

        @Override
        public Fragment getItem(int position) {
            position = position % PAGES;

            return HotelFullImageFragment.newInstance(FullImageViewActivityActivity.this, position, landscapeImgs[position]);
        }

        @Override
        public int getCount() {
            return PAGES * LOOPS;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    }
}
