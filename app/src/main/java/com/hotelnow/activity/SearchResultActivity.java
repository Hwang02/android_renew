package com.hotelnow.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.search.ActivitySearchFragment;
import com.hotelnow.fragment.search.HotelSearchFragment;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.TuneWrap;

public class SearchResultActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private FrameLayout view_pager;
    private int m_Selecttab = 0;
    private String search_txt, banner_id;
    private TextView title_text;
    private RelativeLayout toast_layout;
    private ImageView ico_favorite;
    private TextView tv_toast;
    private String order_kind = "", page = "", banner_name = "";
    private Bundle bundle;
    private FragmentTransaction childFt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();

        m_Selecttab = intent.getIntExtra("tab", 0);
        search_txt = intent.getStringExtra("search");
        banner_id = intent.getStringExtra("banner_id");
        banner_name = intent.getStringExtra("banner_name");
        order_kind = intent.getStringExtra("order_kind");
        page = intent.getStringExtra("page");

        if (TextUtils.isEmpty(order_kind)) {
            order_kind = "";
        }

        if (TextUtils.isEmpty(page)) {
            page = "";
        }

        if (TextUtils.isEmpty(banner_name)) {
            banner_name = "";
        }

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        view_pager = (FrameLayout) findViewById(R.id.view_pager);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        tabLayout.addTab(tabLayout.newTab().setText("숙소"));
        tabLayout.addTab(tabLayout.newTab().setText("액티비티"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        title_text = (TextView) findViewById(R.id.title_text);
        if (TextUtils.isEmpty(order_kind)) {
            if (!TextUtils.isEmpty(banner_name)) {
                title_text.setText(banner_name);
            } else {
                title_text.setText(search_txt);
            }
        } else {
            title_text.setText("내 주변 바로보기");
        }

        tabLayout.getTabAt(m_Selecttab).select();
        if (m_Selecttab == 0) {
            view_pager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TuneWrap.Event("search_list_stay");
                    Fragment hotelSearchFragment = new HotelSearchFragment();
                    bundle = new Bundle(4); // 파라미터는 전달할 데이터 개수
                    bundle.putString("search_txt", search_txt); // key , value
                    bundle.putString("banner_id", banner_id); // key , value
                    bundle.putString("order_kind", order_kind);
                    bundle.putString("title_text", banner_name);
                    hotelSearchFragment.setArguments(bundle);
                    setChildFragment(hotelSearchFragment, m_Selecttab);
                }
            }, 100);

        } else {
            view_pager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TuneWrap.Event("search_list_activity");
                    Fragment activitySearchFragment = new ActivitySearchFragment();
                    bundle = new Bundle(4); // 파라미터는 전달할 데이터 개수
                    bundle.putString("search_txt", search_txt); // key , value
                    bundle.putString("banner_id", banner_id); // key , value
                    bundle.putString("order_kind", order_kind);
                    bundle.putString("title_text", banner_name);
                    activitySearchFragment.setArguments(bundle);
                    setChildFragment(activitySearchFragment, m_Selecttab);
                }
            }, 100);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fg;
                if (tab.getPosition() == 0) {
                    TuneWrap.Event("search_list_stay");
                    fg = new HotelSearchFragment();
                }
                else {
                    TuneWrap.Event("search_list_activity");
                    fg = new ActivitySearchFragment();
                }
                bundle = new Bundle(4); // 파라미터는 전달할 데이터 개수
                bundle.putString("search_txt", search_txt); // key , value
                bundle.putString("banner_id", banner_id); // key , value
                bundle.putString("order_kind", order_kind);
                bundle.putString("title_text", banner_name);
                fg.setArguments(bundle);
                setChildFragment(fg, tab.getPosition());
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
                if (!TextUtils.isEmpty(page)) {
                    Intent intent = new Intent(SearchResultActivity.this, SearchActivity.class);
                    startActivityForResult(intent, 80);
                }
                finish();
            }
        });
    }

    private void setChildFragment(Fragment child, int tag) {
        childFt = getSupportFragmentManager().beginTransaction();

        if(tag == 0){
            if (getSupportFragmentManager().findFragmentByTag("1Search") != null) {
                childFt.hide(getSupportFragmentManager().findFragmentByTag("1Search"));
            }
            if (getSupportFragmentManager().findFragmentByTag("0Search") == null) {
                childFt.add(R.id.view_pager, child, "0Search");
                LogUtil.e("view", "hotel");
            }
            else{
                childFt.show(getSupportFragmentManager().findFragmentByTag("0Search"));
                LogUtil.e("view", "hotel1");
            }
        }
        else{
            if (getSupportFragmentManager().findFragmentByTag("0Search") != null) {
                childFt.hide(getSupportFragmentManager().findFragmentByTag("0Search"));
            }
            if (getSupportFragmentManager().findFragmentByTag("1Search") == null) {
                childFt.add(R.id.view_pager, child, "1Search");
                LogUtil.e("view", "activity");
            } else {
                childFt.show(getSupportFragmentManager().findFragmentByTag("1Search"));
                LogUtil.e("view", "activity1");
            }
        }
        childFt.commitAllowingStateLoss();
    }

    public void setChildDelete(int tag) {
        childFt = getSupportFragmentManager().beginTransaction();

        if(tag == 0){
            LogUtil.e("delete", "activity");
            if (getSupportFragmentManager().findFragmentByTag("1Reservation") != null) {
                LogUtil.e("delete", "activity1");
                childFt.remove(getSupportFragmentManager().findFragmentByTag("1Reservation"));
            }
            LogUtil.e("delete", "activity2");
        }
        else{
            LogUtil.e("delete", "hotel");
            if (getSupportFragmentManager().findFragmentByTag("0Reservation") != null) {
                LogUtil.e("delete", "hotel1");
                childFt.remove(getSupportFragmentManager().findFragmentByTag("0Reservation"));
            }
            LogUtil.e("delete", "hotel2");
        }
        childFt.commitAllowingStateLoss();
    }


    public void showToast(String msg) {
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);
        findViewById(R.id.ico_favorite).setVisibility(View.GONE);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 1000);
    }

    public void showIconToast(String msg, boolean is_fav) {
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);

        if (is_fav) { // 성공
            ico_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        } else { // 취소
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

    public void hideprogress() {
        findViewById(R.id.wrapper).setVisibility(View.GONE);
    }

    public void showprogress() {
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void finished() {
        setResult(80);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 80 && resultCode == 80) {
            setResult(80);
            finish();
        }
    }
}
