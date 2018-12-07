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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.facebook.CallbackManager;
import com.hotelnow.R;
import com.hotelnow.databinding.ActivityMainBinding;
import com.hotelnow.fragment.favorite.FavoriteFragment;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.leisure.LeisureFragment;
import com.hotelnow.fragment.mypage.MypageFragment;
import com.hotelnow.fragment.reservation.ReservationFragment;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

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
    private boolean is_refresh = false;
    private DbOpenHelper dbHelper;
    private int myPosition = 0;
    public static CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setStatusColor(this);

        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mContext = this;
        dbHelper = new DbOpenHelper(this);
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

        mbinding.layoutSearch.searchMain.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                myPosition = mbinding.tabLayout.getSelectedTabPosition();
                startActivityForResult(intent,80);
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
                        CONFIG.TabLogin=false;
                        setTapMove(FAVPAGE, false);
                        mbinding.tabLayout.setVisibility(View.GONE);
                        mbinding.toolbar.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.reserv:{
                        LogUtil.e("xxxxx","77777");
                        CONFIG.TabLogin=false;
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

        // facebook
        callbackManager = CallbackManager.Factory.create();

        // 딥링크, push
        String action = getIntent().getStringExtra("action");
        String data = getIntent().getStringExtra("data");
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String recipeStr1 = data.substring(data.lastIndexOf("?") + 1);
            LogUtil.e("recipeStr1", recipeStr1);
            LogUtil.e("action", action);
            LogUtil.e("data", data);
            if (recipeStr1.contains("hotel_id") && recipeStr1.contains("date") && recipeStr1.contains("e_date") && recipeStr1.contains("is_event")) {
                setTapMove(SELECTPAGE, false);
                String[] recipeStr2 = recipeStr1.split("#");
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    for (String param : recipeStr2[0].split("&")) {
                        String pair[] = param.split("=");
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = "";
                        if (pair.length > 1) {
                            value = URLDecoder.decode(pair[1], "UTF-8");
                        }
                        map.put(key, value);
                    }

                    if(!map.get("hotel_id").equals("")) {
                        Intent intent = new Intent(this, DetailHotelActivity.class);
                        intent.putExtra("hid", map.get("hotel_id"));
                        intent.putExtra("sdate", map.get("date"));
                        intent.putExtra("edate", map.get("e_date"));
                        intent.putExtra("save", true);
                        startActivityForResult(intent, 80);
                    }
                    else if(!map.get("evt_id").equals("")) {
                        Intent intentEvt = new Intent(this, EventActivity.class);
                        intentEvt.putExtra("idx", Integer.valueOf(map.get("evt_id")));
                        startActivityForResult(intentEvt, 80);
                    }
                    else if(!map.get("t_id").equals("")) {
                        Intent intentTheme = new Intent(this, ThemeSpecialHotelActivity.class);
                        intentTheme.putExtra("tid", String.valueOf(map.get("t_id")));
                        intentTheme.putExtra("from", "evt");
                        startActivityForResult(intentTheme, 80);
                    }
                }
                catch (Exception e) {
                    LogUtil.e(CONFIG.TAG, e.toString());
                }
            }
            else if (recipeStr1.contains("hotel_id")){
                setTapMove(SELECTPAGE, false);
                String[] recipeStr3 = recipeStr1.split("#");
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    for (String param : recipeStr3[0].split("&")) {
                        String pair[] = param.split("=");
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = "";
                        if (pair.length > 1) {
                            value = URLDecoder.decode(pair[1], "UTF-8");
                        }
                        map.put(key, value);
                    }
                    Intent intentDetail = new Intent(this, DetailHotelActivity.class);
                    intentDetail.putExtra("hid", map.get("hotel_id"));
                    intentDetail.putExtra("evt", "N");
                    intentDetail.putExtra("save", true);
                    startActivityForResult(intentDetail, 80);
                }
                catch (Exception e) {
                    LogUtil.e(CONFIG.TAG, e.toString());
                }
            }
            else if(recipeStr1.contains("ticket_id")){
                setTapMove(SELECTPAGE, false);
                String[] recipeStr2 = recipeStr1.split("#");
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    for (String param : recipeStr2[0].split("&")) {
                        String pair[] = param.split("=");
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = "";
                        if (pair.length > 1) {
                            value = URLDecoder.decode(pair[1], "UTF-8");
                        }
                        map.put(key, value);
                    }

                    Intent intent = new Intent(this, DetailActivityActivity.class);
                    intent.putExtra("tid", String.valueOf(map.get("ticket_id")));
                    intent.putExtra("save", true);
                    startActivityForResult(intent, 80);
                }
                catch (Exception e) {
                    LogUtil.e(CONFIG.TAG, e.toString());
                }
            }
            else if(recipeStr1.contains("theme_id")){
                setTapMove(SELECTPAGE, false);
                String[] recipeStr4 = recipeStr1.split("#");
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    for (String param : recipeStr4[0].split("&")) {
                        String pair[] = param.split("=");
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = "";
                        if (pair.length > 1) {
                            value = URLDecoder.decode(pair[1], "UTF-8");
                        }
                        map.put(key, value);
                    }
                    Intent intentTheme = null;
                    if(map.get("theme_id").contains("Q")) {
                        intentTheme = new Intent(this, ThemeSpecialActivityActivity.class);
                    } else {
                        intentTheme = new Intent(this, ThemeSpecialHotelActivity.class);
                    }
                    intentTheme.putExtra("tid", String.valueOf(map.get("theme_id").replace("H", "").replace("Q","")));
                    intentTheme.putExtra("from", "evt");
                    startActivityForResult(intentTheme, 80);
                }
                catch (Exception e) {
                    LogUtil.e(CONFIG.TAG, e.toString());
                }
            }
            else if(recipeStr1.contains("event_id")){
                setTapMove(SELECTPAGE, false);
                String[] recipeStr4 = recipeStr1.split("#");
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    for (String param : recipeStr4[0].split("&")) {
                        String pair[] = param.split("=");
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = "";
                        if (pair.length > 1) {
                            value = URLDecoder.decode(pair[1], "UTF-8");
                        }
                        map.put(key, value);
                    }
                    Intent intentEvt = new Intent(this, EventActivity.class);
                    intentEvt.putExtra("idx", Integer.valueOf(map.get("event_id")));
                    startActivityForResult(intentEvt, 80);
                }
                catch (Exception e) {
                    LogUtil.e(CONFIG.TAG, e.toString());
                }
            }
            else if(recipeStr1.contains("move_ticket_list")){
               // ticket tab
                setTapMove(LEISUREPAGE, true);
            }
            else if(recipeStr1.contains("move_coupon")){
                Intent intentEvt = new Intent(this, MyCouponActivity.class);
                startActivityForResult(intentEvt, 80);
            }
            else if(recipeStr1.contains("move_theme_list")){
                Intent intent = new Intent(this, ThemeSAllActivity.class);
                startActivityForResult(intent, 80);
            }
            else if(recipeStr1.contains("move_booking_list")){
                String[] recipeStr4 = recipeStr1.split("#");
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    for (String param : recipeStr4[0].split("&")) {
                        String pair[] = param.split("=");
                        String key = URLDecoder.decode(pair[0], "UTF-8");
                        String value = "";
                        if (pair.length > 1) {
                            value = URLDecoder.decode(pair[1], "UTF-8");
                        }
                        map.put(key, value);
                    }

                    if(map.get("kind_booked") == null) {// ticket 예약리스트 탭
                        CONFIG.sel_reserv = 1;
                        setTapMove(RESERVPAGE, false);
                    }
                    else {//hotel 예약리스트 탭
                        CONFIG.sel_reserv = 0;
                        setTapMove(RESERVPAGE, false);
                    }
                }
                catch (Exception e) {
                    LogUtil.e(CONFIG.TAG, e.toString());
                    setTapMove(SELECTPAGE, false);
                }
            }
            else {
                setTapMove(SELECTPAGE, false);
            }
        }
        else {
            setTapMove(SELECTPAGE, false);
        }
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
                                    return;
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
                    if(is_refresh)
                        moveTabRefresh();
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
                is_refresh = true;
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
                is_refresh = true;
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
                is_refresh = true;
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
                                    return;
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
                    is_refresh = true;
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
                                    return;
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
                    is_refresh = true;
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

    public void setTapdelete(String tag){
        transaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        transaction.commitAllowingStateLoss();
    }

    public void showToast(String msg){
        mbinding.toastLayout.setVisibility(View.VISIBLE);
        mbinding.tvToast.setText(msg);
        mbinding.icoFavorite.setVisibility(View.GONE);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mbinding.toastLayout.setVisibility(View.GONE);
                    }
                }, 2000);
    }

    public void showIconToast(String msg, boolean is_fav){
        mbinding.toastLayout.setVisibility(View.VISIBLE);
        mbinding.tvToast.setText(msg);

        if(is_fav) { // 성공
            mbinding.icoFavorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        }
        else{ // 취소
            mbinding.icoFavorite.setBackgroundResource(R.drawable.ico_titbar_favorite);
        }
        mbinding.icoFavorite.setVisibility(View.VISIBLE);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mbinding.toastLayout.setVisibility(View.GONE);
                    }
                }, 2000);
    }

    public void moveTabReservation(){
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        setHide();
                        mbinding.navigation.setCurrentItem(2);
                    }
                }, 100);
    }

    public void moveTabRefresh(){
        HomeFragment fm = (HomeFragment) getSupportFragmentManager().findFragmentByTag("SELECTPAGE");

        if(fm != null) {
            if (dbHelper.selectAllRecentItem("10").size() > 0 && !CONFIG.isRecent) {
                fm.getRecentData(true);
                CONFIG.isRecent = true;
            } else {
                fm.getRecentData(false);
                fm.setLikeRefresh(false);
            }
        }
    }

    public void moveTabRefresh2(){
        HotelFragment fm = (HotelFragment) getSupportFragmentManager().findFragmentByTag("HOTELPAGE");
        if(fm != null) {
            fm.allRefresh();
        }
    }

    public void moveTabRefresh3(){
        LeisureFragment fm = (LeisureFragment) getSupportFragmentManager().findFragmentByTag("LEISUREPAGE");
        if(fm != null) {
            fm.allRefresh();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(myPosition == 0){
            moveTabRefresh();
        }
        else if(myPosition == 1) {
            moveTabRefresh2();
        }
        else if(myPosition == 2) {
            moveTabRefresh3();
        }

        if(CONFIG.Mypage_Search){
            moveTabReservation();
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
