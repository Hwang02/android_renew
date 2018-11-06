package com.hotelnow.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import com.hotelnow.R;
import com.hotelnow.databinding.ActivityMainBinding;
import com.hotelnow.fragment.favorite.FavoriteFragment;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.leisure.LeisureFragment;
import com.hotelnow.fragment.mypage.MypageFragment;
import com.hotelnow.fragment.reservation.ReservationFragment;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;

public class MainActivity extends FragmentActivity {
    private static ActivityMainBinding mbinding;
    private FragmentTransaction transaction;
    private final int FAVPAGE = 1;
    private final int RESERVPAGE = 2;
    private final int MYPAGE = 3;
    private final int SELECTPAGE = 4;
    private final int HOTELPAGE = 5;
    private final int LEISUREPAGE = 6;
    private static Context mContext;
    private SharedPreferences _preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setStatusColor(this);

        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mContext = this;
        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // 하단 탭 버튼 동작 제거
        mbinding.navigation.enableAnimation(false);
        mbinding.navigation.enableShiftingMode(false);
        mbinding.navigation.enableItemShiftingMode(false);

        // 홈 상단 탭
        mbinding.tabLayout.addTab(mbinding.tabLayout.newTab().setText("추천"));
        mbinding.tabLayout.addTab(mbinding.tabLayout.newTab().setText("숙소"));
        mbinding.tabLayout.addTab(mbinding.tabLayout.newTab().setText("액티비티"));

        mbinding.layoutSearch.searchMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        //상단 toolbar
        setTitle();

        //상단 탭 화면 이동
        mbinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mbinding.toolbar.setVisibility(View.VISIBLE);
                switch (tab.getPosition()){
                    case 0:{ // 추천
                        LogUtil.e("xxxxx","111111");
                        setTapMove(SELECTPAGE, false);
                        break;
                    }
                    case 1:{ // 호텔
                        LogUtil.e("xxxxx","222222");
                        setTapMove(HOTELPAGE, false);
                        break;
                    }
                    case 2:{ // 엑티비티
                        LogUtil.e("xxxxx","33333");
                        setTapMove(LEISUREPAGE, false);
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //하단 탭 화면 이동
        mbinding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()){
                    case R.id.home:{
                        mbinding.toolbar.setVisibility(View.VISIBLE);
                        LogUtil.e("xxxxx","555555");
                        //홈은 다른곳 이동시 값 저장 필요
                        int mPosition = 4;
                        if(mbinding.tabLayout.getSelectedTabPosition() == 0){
                            mPosition = SELECTPAGE;
                        }
                        else if(mbinding.tabLayout.getSelectedTabPosition() == 1){
                            mPosition = HOTELPAGE;
                        }
                        else if(mbinding.tabLayout.getSelectedTabPosition() == 2){
                            mPosition = LEISUREPAGE;
                        }
                        setTapMove(mPosition, false);
                        break;
                    }
                    case R.id.fav:{
                        LogUtil.e("xxxxx","666666");
                        setTapMove(FAVPAGE, false);
                        mbinding.tabLayout.setVisibility(View.GONE);
                        mbinding.toolbar.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.reserv:{
                        LogUtil.e("xxxxx","77777");
                        setTapMove(RESERVPAGE, false);
                        mbinding.tabLayout.setVisibility(View.GONE);
                        mbinding.toolbar.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.mypage:{
                        LogUtil.e("xxxxx","888888");
                        setTapMove(MYPAGE, false);
                        mbinding.tabLayout.setVisibility(View.GONE);
                        mbinding.toolbar.setVisibility(View.GONE);
                        break;
                    }
                }
                return false;
            }
        });

        mbinding.navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                //재선택시 동작 없음.
            }
        });

        setTapMove(SELECTPAGE, false);
    }

    public static void showProgress(){ mbinding.wrapper.setVisibility(View.VISIBLE); }
    public static void hideProgress(){
        try {
            mbinding.wrapper.setVisibility(View.GONE);
        } catch (Exception e){
            Util.doRestart(mContext);
        }
    }

    public void setTapMove(int mPosition, boolean isMove){
        transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
        switch (mPosition){
            case SELECTPAGE:{
                if(isMove) {
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    setHide();
                                    mbinding.navigation.setCurrentItem(0);
                                    mbinding.tabLayout.getTabAt(0).select();
                                }
                            }, 100);
                }
                else {
                    if (getSupportFragmentManager().findFragmentByTag("SELECTPAGE") == null) {
                        transaction.add(mbinding.screenContainer.getId(), new HomeFragment(), "SELECTPAGE");
                    } else {
                        transaction.show(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                    }

                    if (getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("HOTELPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("MYPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                    }
                    transaction.commitAllowingStateLoss();
                    mbinding.tabLayout.setVisibility(View.VISIBLE);
                    mbinding.toolbar.setVisibility(View.VISIBLE);
                }
                break;
            }
            case FAVPAGE:{
                if(getSupportFragmentManager().findFragmentByTag("FAVPAGE") == null) {
                    transaction.add(mbinding.screenContainer.getId(), new FavoriteFragment(), "FAVPAGE");
                }
                else{
                    transaction.remove(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                    transaction.add(mbinding.screenContainer.getId(), new FavoriteFragment(), "FAVPAGE");
                }

                if(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("HOTELPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("MYPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                }
                transaction.commitAllowingStateLoss();
                break;
            }
            case RESERVPAGE:{
                if(getSupportFragmentManager().findFragmentByTag("RESERVPAGE") == null) {
                    transaction.add(mbinding.screenContainer.getId(), new ReservationFragment(), "RESERVPAGE");
                }
                else{
                    transaction.remove(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                    transaction.add(mbinding.screenContainer.getId(), new ReservationFragment(), "RESERVPAGE");
                }

                if(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("HOTELPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("MYPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                }
                transaction.commitAllowingStateLoss();
                break;
            }
            case MYPAGE:{
                if(getSupportFragmentManager().findFragmentByTag("MYPAGE") == null) {
                    transaction.add(mbinding.screenContainer.getId(), new MypageFragment(), "MYPAGE");
                }
                else{
                    transaction.show(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                }

                if(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("HOTELPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                }
                if(getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                }
                transaction.commitAllowingStateLoss();
                break;
            }
            case HOTELPAGE:{
                if(isMove) {
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    setHide();
                                    mbinding.navigation.setCurrentItem(0);
                                    mbinding.tabLayout.getTabAt(1).select();
                                }
                            }, 100);
                }
                else {
                    if (getSupportFragmentManager().findFragmentByTag("HOTELPAGE") == null) {
                        transaction.add(mbinding.screenContainer.getId(), new HotelFragment(), "HOTELPAGE");
                    } else {
                        transaction.show(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                    }

                    if (getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("MYPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                    }
                    transaction.commitAllowingStateLoss();
                    mbinding.tabLayout.setVisibility(View.VISIBLE);
                    mbinding.toolbar.setVisibility(View.VISIBLE);
                }
                break;
            }
            case LEISUREPAGE:{
                if(isMove) {
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    setHide();
                                    mbinding.navigation.setCurrentItem(0);
                                    mbinding.tabLayout.getTabAt(2).select();
                                }
                            }, 100);
                }
                else {
                    if (getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") == null) {
                        transaction.add(mbinding.screenContainer.getId(), new LeisureFragment(), "LEISUREPAGE");
                    } else {
                        transaction.show(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                    }

                    if (getSupportFragmentManager().findFragmentByTag("HOTELPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("MYPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                    }
                    transaction.commitAllowingStateLoss();
                    mbinding.tabLayout.setVisibility(View.VISIBLE);
                    mbinding.toolbar.setVisibility(View.VISIBLE);
                }
                break;
            }

        }
    }

    private void setHide(){
        if (getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") != null) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
        }
        if (getSupportFragmentManager().findFragmentByTag("HOTELPAGE") != null) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
        }
        if (getSupportFragmentManager().findFragmentByTag("MYPAGE") != null) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
        }
        if (getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
        }
        if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
        }
        if (getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
            transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
        }
    }

    public void setTitle(){
        mbinding.layoutSearch.txtSearch.setText( _preferences.getString("username", "나우")+"님, 어떤 여행을 찾고 계세요?");
    }
}
