package com.hotelnow.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.adapter.HotDealPagerAdapter;

public class HotDealActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager view_pager;
    HotDealPagerAdapter mAdapter;
    int m_Selecttab = 0;
    TextView title_text;
    RelativeLayout toast_layout;
    ImageView ico_favorite;
    TextView tv_toast;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotdeal);

        Intent intent = getIntent();

        m_Selecttab = intent.getIntExtra("tab",0);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        tabLayout.addTab(tabLayout.newTab().setText("숙소"));
        tabLayout.addTab(tabLayout.newTab().setText("액티비티"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        title_text = (TextView) findViewById(R.id.title_text);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (m_Selecttab == 0) {
                            tabLayout.getTabAt(0).select();
                            view_pager.setCurrentItem(0);
                        } else {
                            tabLayout.getTabAt(1).select();
                            view_pager.setCurrentItem(1);
                        }
                    }
                },100);

        mAdapter = new HotDealPagerAdapter(this, getSupportFragmentManager());
        view_pager.setAdapter(mAdapter);

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

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finished();
            }
        });
    }

    public void showToast(String msg){
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);
        findViewById(R.id.ico_favorite).setVisibility(View.GONE);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 1500);
    }

    public void showIconToast(String msg, boolean is_fav){
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);

        if(is_fav) { // 성공
            ico_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        }
        else{ // 취소
            ico_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite);
        }
        ico_favorite.setVisibility(View.VISIBLE);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 1500);
    }


    public void toolbarAnimateShow() {
        tabLayout.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tabLayout.setVisibility(View.VISIBLE);
                        findViewById(R.id.message).setVisibility(View.VISIBLE);
                        findViewById(R.id.line).setVisibility(View.VISIBLE);
                        findViewById(R.id.magrin_view).setVisibility(View.VISIBLE);
                    }
                });
    }

    public void toolbarAnimateHide() {
        tabLayout.animate()
                .translationY(-tabLayout.getHeight() - findViewById(R.id.message).getHeight() -1)
                .setInterpolator(new LinearInterpolator())
                .setDuration(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tabLayout.setVisibility(View.GONE);
                        findViewById(R.id.message).setVisibility(View.GONE);
                        findViewById(R.id.line).setVisibility(View.GONE);
                        findViewById(R.id.magrin_view).setVisibility(View.GONE);
                    }
            });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void finished(){
        setResult(80);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 80 && resultCode == 80){
            setResult(80);
            finish();
        }
    }
}
