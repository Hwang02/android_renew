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
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.fragment.detail.HotelFullImageFragment;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.TuneWrap;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;


public class FullImageViewActivity extends FragmentActivity {

    String hid, name;
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
        hid = intent.getStringExtra("hid");
        idx = intent.getIntExtra("idx", 0);
        name = intent.getStringExtra("name");

        TuneWrap.Event("PortraitView", hid);

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
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        Api.get(CONFIG.portraitUrl+"/"+hid, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(FullImageViewActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(FullImageViewActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    JSONArray data = obj.getJSONArray("data");
                    portraitImgs = new String[data.length()];
                    caption1 = new String[data.length()];
                    caption2 = new String[data.length()];
                    ll_image_list.removeAllViews();
                    for (int i = 0; i < data.length(); i++) {
                        portraitImgs[i] = data.getJSONObject(i).getString("portrait");
                        caption1[i] = data.getJSONObject(i).getString("caption1");
                        caption2[i] = data.getJSONObject(i).getString("caption2");

                        View child = getLayoutInflater().inflate(R.layout.layout_fullimage_sub_item, null);
                        RoundedImageView image = child.findViewById(R.id.image);
                        Ion.with(image).load(portraitImgs[i]);
                        child.setTag(i);

                        child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                idx = (int)v.getTag();
                                initPageMark();
                                setPortraitMsg(idx);
                                pager.setCurrentItem(idx);
                            }
                        });

                        ll_image_list.addView(child);
                    }

                    showPager();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

            }
        });
    }

    private void showPager() {
        PAGES = portraitImgs.length;
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

                setPortraitMsg(markPrevPosition);
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
        setPortraitMsg(idx);
    }

    private void initPageMark() {
        page.setText(markNowPosition+1+"/"+portraitImgs.length);

        markPrevPosition = markNowPosition;
    }

    private void setPortraitMsg(int position) {
        if (caption1 != null) {
            if(caption1[position].length()>0)
               img_title.setText(caption1[position]);
            else
                img_title.setText("");
        }
        if (caption2 != null) {
            if (caption2[position].length() > 0)
                img_title2.setText(caption2[position]);
            else
                img_title2.setText("");
        }
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

            return HotelFullImageFragment.newInstance(FullImageViewActivity.this, position, landscapeImgs[position]);
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
