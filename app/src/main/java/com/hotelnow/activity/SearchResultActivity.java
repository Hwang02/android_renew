package com.hotelnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.adapter.SectionsPagerAdapter;
import com.hotelnow.utils.NonSwipeableViewPager;

public class SearchResultActivity extends AppCompatActivity {

    TabLayout tabLayout;
    NonSwipeableViewPager view_pager;
    SectionsPagerAdapter mSectionsPagerAdapter;
    int m_Selecttab = 0;
    String search_txt, banner_id;
    TextView title_text;
    RelativeLayout toast_layout;
    ImageView ico_favorite;
    TextView tv_toast;
    String order_kind="", page = "", banner_name="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();

        m_Selecttab = intent.getIntExtra("tab",0);
        search_txt = intent.getStringExtra("search");
        banner_id = intent.getStringExtra("banner_id");
        banner_name = intent.getStringExtra("banner_name");
        order_kind = intent.getStringExtra("order_kind");
        page = intent.getStringExtra("page");

        if(TextUtils.isEmpty(order_kind)){
            order_kind = "";
        }

        if(TextUtils.isEmpty(page)){
            page = "";
        }

        if(TextUtils.isEmpty(banner_name)){
            banner_name = "";
        }

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        view_pager = (NonSwipeableViewPager) findViewById(R.id.view_pager);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        tabLayout.addTab(tabLayout.newTab().setText("숙소"));
        tabLayout.addTab(tabLayout.newTab().setText("액티비티"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        title_text = (TextView) findViewById(R.id.title_text);
        if(TextUtils.isEmpty(order_kind)) {
            if(!TextUtils.isEmpty(banner_name)){
                title_text.setText(banner_name);
            }
            else {
                title_text.setText(search_txt);
            }
        }
        else {
            title_text.setText("내 주변 바로보기");
        }

        if(m_Selecttab == 0) {
            tabLayout.getTabAt(0).select();
        }
        else {
            tabLayout.getTabAt(1).select();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), search_txt, banner_id, order_kind, title_text.getText().toString());
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

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finished();
            }
        });

        title_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(page)) {
                    Intent intent = new Intent(SearchResultActivity.this, SearchActivity.class);
                    startActivityForResult(intent, 80);
                }
                finish();
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
                }, 2000);
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
                }, 2000);
    }

    public void hideprogress(){
        findViewById(R.id.wrapper).setVisibility(View.GONE);
    }

    public void showprogress(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
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
