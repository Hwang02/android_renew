package com.hotelnow.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.databinding.ActivityMainBinding;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogFull;
import com.hotelnow.fragment.favorite.FavoriteFragment;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.fragment.hotel.HotelFragment;
import com.hotelnow.fragment.leisure.LeisureFragment;
import com.hotelnow.fragment.mypage.MypageFragment;
import com.hotelnow.fragment.reservation.ReservationFragment;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FindDebugger;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private static long back_pressed;
    private DialogAlert dialogAlert;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String push_type = "";
    private String bid;
    private String hid;
    private String isevt;
    private int evtidx;
    private String evttag = "";
    private String sdate = null;
    private String edate = null;
    private String recipeStr1 = null;

    public boolean isDebugged() {
        LogUtil.e("MainActivity", "Checking for debuggers...");

        boolean tracer = false;
        try {
            tracer = FindDebugger.hasTracerPid();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (FindDebugger.isBeingDebugged() || tracer) {
            LogUtil.e("MainActivity", "Debugger was detected");
            return true;
        } else {
            LogUtil.e("MainActivity", "No debugger was detected.");
            return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!BuildConfig.DEBUG && isDebugged()) {
            dialogAlert = new DialogAlert("알림", "디버깅 탐지로 앱을 종료 합니다.", MainActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAlert.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            dialogAlert.show();
            dialogAlert.setCancelable(false);
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.setStatusColor(this);

//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        Bundle params = new Bundle();
//        params.putString("PAGE", "MainActivity-Start");
//        params.putString("NAME", "HWANG");
//        mFirebaseAnalytics.logEvent("Screen_view2", params);

        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mContext = this;
        dbHelper = new DbOpenHelper(this);
        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mbinding.navigation.setTextSize((float) 10);

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
                startActivityForResult(intent, 80);
            }
        });

        //상단 toolbar
        setTitle();

        wrapTabIndicatorToTitle(mbinding.tabLayout, Util.dptopixel(this, 30), Util.dptopixel(this, 30));
        //상단 탭 화면 이동
        mbinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mbinding.toolbar.setVisibility(View.VISIBLE);
                switch (tab.getPosition()) {
                    case 0: { // 추천
                        wrapTabIndicatorToTitle(mbinding.tabLayout, Util.dptopixel(getApplicationContext(), 30), Util.dptopixel(getApplicationContext(), 30));
                        LogUtil.e("xxxxx", "111111");
                        TuneWrap.Event("home_button");
                        setTapMove(SELECTPAGE, false);
                        break;
                    }
                    case 1: { // 호텔
                        wrapTabIndicatorToTitle(mbinding.tabLayout, Util.dptopixel(getApplicationContext(), 30), Util.dptopixel(getApplicationContext(), 30));
                        LogUtil.e("xxxxx", "222222");
                        TuneWrap.Event("stay_button");
                        setTapMove(HOTELPAGE, false);
                        break;
                    }
                    case 2: { // 엑티비티
                        wrapTabIndicatorToTitle(mbinding.tabLayout, Util.dptopixel(getApplicationContext(), 20), Util.dptopixel(getApplicationContext(), 20));
                        LogUtil.e("xxxxx", "33333");
                        TuneWrap.Event("activity_button");
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
                switch (item.getItemId()) {
                    case R.id.home: {
                        mbinding.toolbar.setVisibility(View.VISIBLE);
                        LogUtil.e("xxxxx", "555555");
                        //홈은 다른곳 이동시 값 저장 필요
                        int mPosition = 4;
                        if (mbinding.tabLayout.getSelectedTabPosition() == 0) {
                            mPosition = SELECTPAGE;
                        } else if (mbinding.tabLayout.getSelectedTabPosition() == 1) {
                            mPosition = HOTELPAGE;
                        } else if (mbinding.tabLayout.getSelectedTabPosition() == 2) {
                            mPosition = LEISUREPAGE;
                        }

                        setTapMove(mPosition, false);
                        break;
                    }
                    case R.id.fav: {
                        LogUtil.e("xxxxx", "666666");
                        CONFIG.TabLogin = false;
                        setTapMove(FAVPAGE, false);
                        break;
                    }
                    case R.id.reserv: {
                        LogUtil.e("xxxxx", "77777");
                        CONFIG.TabLogin = false;
                        setTapMove(RESERVPAGE, false);
                        break;
                    }
                    case R.id.mypage: {
                        LogUtil.e("xxxxx", "888888");
                        setTapMove(MYPAGE, false);
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
        if (getIntent().getStringExtra("push_type") != null) {
            push_type = getIntent().getStringExtra("push_type");
            bid = getIntent().getStringExtra("bid");
            hid = getIntent().getStringExtra("hid");
            isevt = getIntent().getStringExtra("isevt");
            evtidx = getIntent().getIntExtra("evtidx", 0);
            sdate = getIntent().getStringExtra("sdate");
            edate = getIntent().getStringExtra("edate");
            evttag = getIntent().getStringExtra("evttag");
            // 1 : 예약 상세|booking_id / 4: 리뷰|booking_id, 2 : 인덱스 페이지 이동|이벤트id, 3: 상품 상세|hotel_id, 5:적립금 확인, 6:테마리스트 이동 7:쿠폰 리스트 8: 티켓상세 10: 숙소탭 11: 액티비티탭
            if (push_type != null) {
                if (push_type.equals("1") || push_type.equals("4")) {
                    setTapMove(SELECTPAGE, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                Intent intentBooking = new Intent(MainActivity.this, ReservationHotelDetailActivity.class);
                                intentBooking.putExtra("from_push", true);
                                intentBooking.putExtra("bid", bid);
                                startActivityForResult(intentBooking, 80);
                                TuneWrap.Event("PUSH", bid + "");
                        }
                    }, 1000);
                } else if (push_type.equals("2")) {
                    setTapMove(SELECTPAGE, false);
                    if (evtidx > 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intentEvt = new Intent(MainActivity.this, EventActivity.class);
                                intentEvt.putExtra("idx", evtidx);
                                startActivityForResult(intentEvt, 80);
                                TuneWrap.Event("PUSH", evtidx+"");
                            }
                        }, 1000);
                    }
                } else if (push_type.equals("3")) {
                    setTapMove(SELECTPAGE, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentDeal = new Intent(MainActivity.this, DetailHotelActivity.class);
                            intentDeal.putExtra("hid", hid);
                            intentDeal.putExtra("evt", "N");
                            intentDeal.putExtra("save", true);
                            intentDeal.putExtra("sdate", sdate);
                            intentDeal.putExtra("edate", edate);
                            startActivityForResult(intentDeal, 80);
                            TuneWrap.Event("PUSH", hid);
                        }
                    }, 1000);
                } else if (push_type.equals("5")) {
                    tabStatus(false);
                    mbinding.navigation.setCurrentItem(MYPAGE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentSave = new Intent(MainActivity.this, MySaveActivity.class);
                            startActivity(intentSave);
                            TuneWrap.Event("PUSH", "mypoint");
                        }
                    }, 1000);
                } else if (push_type.equals("6")) {
                    setTapMove(SELECTPAGE, false);
                    if (!evttag.equals(null) && evttag.equals("Q")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intentTheme = new Intent(MainActivity.this, ThemeSpecialActivityActivity.class);
                                intentTheme.putExtra("tid", String.valueOf(evtidx));
                                intentTheme.putExtra("from", "evt");
                                startActivity(intentTheme);
                                TuneWrap.Event("PUSH", evtidx+"");
                            }
                        }, 1000);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intentTheme = new Intent(MainActivity.this, ThemeSpecialHotelActivity.class);
                                intentTheme.putExtra("tid", String.valueOf(evtidx));
                                intentTheme.putExtra("from", "evt");
                                startActivity(intentTheme);
                                TuneWrap.Event("PUSH", evtidx+"");
                            }
                        }, 1000);
                    }
                } else if (push_type.equals("7")) {
                    tabStatus(false);
                    mbinding.navigation.setCurrentItem(MYPAGE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentCoupon = new Intent(MainActivity.this, MyCouponActivity.class);
                            startActivity(intentCoupon);
                            TuneWrap.Event("PUSH", "coupon");
                        }
                    }, 1000);

                } else if (push_type.equals("8")) {
                    setTapMove(SELECTPAGE, false);
                    // 티켓상세
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentticket = new Intent(MainActivity.this, DetailActivityActivity.class);
                            intentticket.putExtra("tid", String.valueOf(evtidx));
                            intentticket.putExtra("save", true);
                            startActivity(intentticket);
                            TuneWrap.Event("PUSH", evtidx+"");
                        }
                    }, 1000);
                } else if(push_type.equals("10")) {
                    setTapMove(HOTELPAGE, true);
                    TuneWrap.Event("PUSH", "staymain");
                } else if(push_type.equals("11")) {
                    setTapMove(LEISUREPAGE, true);
                    TuneWrap.Event("PUSH", "activitymain");
                } else{
                    setTapMove(SELECTPAGE, false);
                    TuneWrap.Event("PUSH", "mainpage");
                }
            } else {
                setTapMove(SELECTPAGE, false);
                TuneWrap.Event("PUSH", "mainpage");
            }
        } else {
            String action = getIntent().getStringExtra("action");
            String data = getIntent().getStringExtra("data");

            if (Intent.ACTION_VIEW.equals(action) && data != null) {
                recipeStr1 = data.substring(data.lastIndexOf("?") + 1);
                LogUtil.e("recipeStr1", recipeStr1);
                LogUtil.e("action", action);
                LogUtil.e("data", data);
                if (recipeStr1.contains("hotel_id") && recipeStr1.contains("date") && recipeStr1.contains("e_date") && recipeStr1.contains("is_event")) {
                    setTapMove(SELECTPAGE, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
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

                                if (!map.get("hotel_id").equals("")) {
                                    Intent intent = new Intent(MainActivity.this, DetailHotelActivity.class);
                                    intent.putExtra("hid", map.get("hotel_id"));
                                    intent.putExtra("sdate", map.get("date"));
                                    intent.putExtra("edate", map.get("e_date"));
                                    intent.putExtra("save", true);
                                    startActivityForResult(intent, 80);
                                } else if (!map.get("evt_id").equals("")) {
                                    Intent intentEvt = new Intent(MainActivity.this, EventActivity.class);
                                    intentEvt.putExtra("idx", Integer.valueOf(map.get("evt_id")));
                                    startActivityForResult(intentEvt, 80);
                                } else if (!map.get("t_id").equals("")) {
                                    Intent intentTheme = new Intent(MainActivity.this, ThemeSpecialHotelActivity.class);
                                    intentTheme.putExtra("tid", String.valueOf(map.get("t_id")));
                                    intentTheme.putExtra("from", "evt");
                                    startActivityForResult(intentTheme, 80);
                                }
                            } catch (Exception e) {
                                LogUtil.e(CONFIG.TAG, e.toString());
                            }
                        }
                    }, 1000);
                } else if (recipeStr1.contains("hotel_id")) {
                    setTapMove(SELECTPAGE, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String[] recipeStr3 = recipeStr1.split("#");
                            try {
                                final Map<String, String> map = new HashMap<String, String>();
                                for (String param : recipeStr3[0].split("&")) {
                                    String pair[] = param.split("=");
                                    String key = URLDecoder.decode(pair[0], "UTF-8");
                                    String value = "";
                                    if (pair.length > 1) {
                                        value = URLDecoder.decode(pair[1], "UTF-8");
                                    }
                                    map.put(key, value);
                                }

                                int fDayLimit = _preferences.getInt("future_day_limit", 180);
                                String checkurl = CONFIG.checkinDateUrl + "/" + map.get("hotel_id") + "/" + fDayLimit;

                                Api.get(checkurl, new Api.HttpCallback() {
                                    @Override
                                    public void onFailure(Response response, Exception e) {
                                        Toast.makeText(MainActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    @Override
                                    public void onSuccess(Map<String, String> headers, String body) {
                                        try {
                                            JSONObject obj = new JSONObject(body);
                                            JSONArray aobj = obj.getJSONArray("data");

                                            String checkin = "";
                                            String checkout = "";
                                            Intent intentDetail = new Intent(MainActivity.this, DetailHotelActivity.class);
                                            intentDetail.putExtra("hid", map.get("hotel_id"));
                                            if (aobj.length() > 0) {
                                                checkin = aobj.getString(0);
                                                checkout = Util.getNextDateStr(checkin);
                                                intentDetail.putExtra("sdate", checkin);
                                                intentDetail.putExtra("edate", checkout);
                                            }
                                            intentDetail.putExtra("evt", "N");
                                            intentDetail.putExtra("save", true);
                                            startActivityForResult(intentDetail, 80);

                                        } catch (Exception e) {
                                            // Log.e(CONFIG.TAG, e.toString());
                                            LogUtil.e("aobj.length() : ",e.getMessage()+"");
                                            Toast.makeText(MainActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                    }
                                });
                            } catch (Exception e) {
                                LogUtil.e(CONFIG.TAG, e.toString());
                            }
                        }
                    }, 1000);
                } else if (recipeStr1.contains("ticket_id")) {
                    setTapMove(SELECTPAGE, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
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

                                Intent intent = new Intent(MainActivity.this, DetailActivityActivity.class);
                                intent.putExtra("tid", String.valueOf(map.get("ticket_id")));
                                intent.putExtra("save", true);
                                startActivityForResult(intent, 80);
                            } catch (Exception e) {
                                LogUtil.e(CONFIG.TAG, e.toString());
                            }
                        }
                    }, 1000);
                } else if (recipeStr1.contains("theme_id")) {
                    setTapMove(SELECTPAGE, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
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
                                if (map.get("theme_id").contains("Q")) {
                                    intentTheme = new Intent(MainActivity.this, ThemeSpecialActivityActivity.class);
                                } else {
                                    intentTheme = new Intent(MainActivity.this, ThemeSpecialHotelActivity.class);
                                }
                                intentTheme.putExtra("tid", String.valueOf(map.get("theme_id").replace("H", "").replace("Q", "")));
                                intentTheme.putExtra("from", "evt");
                                startActivityForResult(intentTheme, 80);
                            } catch (Exception e) {
                                LogUtil.e(CONFIG.TAG, e.toString());
                            }
                        }
                    }, 1000);
                } else if (recipeStr1.contains("event_id")) {
                    setTapMove(SELECTPAGE, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
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
                                Intent intentEvt = new Intent(MainActivity.this, EventActivity.class);
                                intentEvt.putExtra("idx", Integer.valueOf(map.get("event_id")));
                                startActivityForResult(intentEvt, 80);
                            } catch (Exception e) {
                                LogUtil.e(CONFIG.TAG, e.toString());
                            }
                        }
                    }, 1000);
                } else if (recipeStr1.contains("move_ticket_list")) {
                    // ticket tab
                    setTapMove(LEISUREPAGE, true);
                } else if (recipeStr1.contains("move_hotel_list")) {
                    // ticket tab
                    setTapMove(HOTELPAGE, true);
                } else if (recipeStr1.contains("move_coupon")) {
                    tabStatus(false);
                    mbinding.navigation.setCurrentItem(MYPAGE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentCoupon = new Intent(MainActivity.this, MyCouponActivity.class);
                            startActivity(intentCoupon);
                        }
                    }, 1000);
                } else if (recipeStr1.contains("move_favoriteH")) {
                    CONFIG.sel_fav = 0;
                    mbinding.navigation.setCurrentItem(FAVPAGE);
                } else if (recipeStr1.contains("move_favoriteQ")) {
                    CONFIG.sel_fav = 1;
                    mbinding.navigation.setCurrentItem(FAVPAGE);
                } else if (recipeStr1.contains("move_theme_list")) {
                    setTapMove(SELECTPAGE, false);
                    Intent intent = new Intent(this, ThemeSAllActivity.class);
                    startActivityForResult(intent, 80);
                } else if (recipeStr1.contains("move_booking_list")) {
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

                        if (map.get("kind_booked") == null || map.get("kind_booked").equals("H")) {///hotel 예약리스트 탭
                            CONFIG.sel_reserv = 0;
                        } else {//ticket 예약리스트 탭
                            CONFIG.sel_reserv = 1;
                        }
                        mbinding.navigation.setCurrentItem(RESERVPAGE);
                    } catch (Exception e) {
                        LogUtil.e(CONFIG.TAG, e.toString());
                        setTapMove(SELECTPAGE, false);
                    }
                } else {
                    setTapMove(SELECTPAGE, false);
                }
            } else {
                if (getIntent().getBooleanExtra("reservation", false)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mbinding.navigation.setCurrentItem(RESERVPAGE);
                        }
                    }, 500);
                } else {
                    setTapMove(SELECTPAGE, false);
                }
            }
        }
    }

    public static void showProgress() {
        mbinding.wrapper.setVisibility(View.VISIBLE);
        mbinding.wrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public static void hideProgress() {
        mbinding.wrapper.setVisibility(View.GONE);
    }

    public void setTapMove(int mPosition, boolean isMove) {
        showProgress();
        transaction = getSupportFragmentManager().beginTransaction();
        switch (mPosition) {
            case SELECTPAGE: {
                TuneWrap.Event("home");
                tabStatus(true);
                if (isMove) {
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
                } else {
                    if (getSupportFragmentManager().findFragmentByTag("SELECTPAGE") == null) {
                        transaction.add(mbinding.screenContainer.getId(), new HomeFragment(), "SELECTPAGE");
                    } else {
                        transaction.show(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                        hideProgress();
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
                        transaction.remove(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                        transaction.remove(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                    }
                    transaction.commitAllowingStateLoss();
                    if (is_refresh)
                        moveTabRefresh();
                    mbinding.tabLayout.setVisibility(View.VISIBLE);
                    mbinding.toolbar.setVisibility(View.VISIBLE);
                }
                break;
            }
            case FAVPAGE: {
                tabStatus(false);
                CONFIG.isRecent = true;
                if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") == null) {
                    transaction.add(mbinding.screenContainer.getId(), new FavoriteFragment(), "FAVPAGE");
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
                    transaction.remove(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                }
                if (getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                }
                transaction.commitAllowingStateLoss();
                is_refresh = true;
                mbinding.tabLayout.setVisibility(View.GONE);
                mbinding.toolbar.setVisibility(View.GONE);
                break;
            }
            case RESERVPAGE: {
                tabStatus(false);
                if (getSupportFragmentManager().findFragmentByTag("RESERVPAGE") == null) {
                    transaction.add(mbinding.screenContainer.getId(), new ReservationFragment(), "RESERVPAGE");
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
                if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                    transaction.remove(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                }
                if (getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                }
                transaction.commitAllowingStateLoss();
                is_refresh = true;
                mbinding.tabLayout.setVisibility(View.GONE);
                mbinding.toolbar.setVisibility(View.GONE);

                break;
            }
            case MYPAGE: {
                TuneWrap.Event("myinfo");
                tabStatus(false);
                if (isMove) {
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    setHide();
                                    mbinding.navigation.setCurrentItem(3);
                                }
                            }, 100);
                }
                if (getSupportFragmentManager().findFragmentByTag("MYPAGE") == null) {
                    transaction.add(mbinding.screenContainer.getId(), new MypageFragment(), "MYPAGE");
                } else {
                    transaction.show(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                    hideProgress();
                }

                if (getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                }
                if (getSupportFragmentManager().findFragmentByTag("HOTELPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                }
                if (getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
                    transaction.remove(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                }
                if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                    transaction.remove(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
                }
                if (getSupportFragmentManager().findFragmentByTag("SELECTPAGE") != null) {
                    transaction.hide(getSupportFragmentManager().findFragmentByTag("SELECTPAGE"));
                }
                transaction.commitAllowingStateLoss();
                is_refresh = true;
                mbinding.tabLayout.setVisibility(View.GONE);
                mbinding.toolbar.setVisibility(View.GONE);

                break;
            }
            case HOTELPAGE: {
                TuneWrap.Event("stay");
                if (isMove) {
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
                } else {
                    if (getSupportFragmentManager().findFragmentByTag("HOTELPAGE") == null) {
                        transaction.add(mbinding.screenContainer.getId(), new HotelFragment(), "HOTELPAGE");
                    } else {
                        transaction.show(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                        hideProgress();
                    }

                    if (getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("MYPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
                        transaction.remove(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                        transaction.remove(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
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
            case LEISUREPAGE: {
                TuneWrap.Event("activity");
                if (isMove) {
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
                } else {
                    if (getSupportFragmentManager().findFragmentByTag("LEISUREPAGE") == null) {
                        transaction.add(mbinding.screenContainer.getId(), new LeisureFragment(), "LEISUREPAGE");
                    } else {
                        transaction.show(getSupportFragmentManager().findFragmentByTag("LEISUREPAGE"));
                        hideProgress();
                    }

                    if (getSupportFragmentManager().findFragmentByTag("HOTELPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("HOTELPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("MYPAGE") != null) {
                        transaction.hide(getSupportFragmentManager().findFragmentByTag("MYPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("RESERVPAGE") != null) {
                        transaction.remove(getSupportFragmentManager().findFragmentByTag("RESERVPAGE"));
                    }
                    if (getSupportFragmentManager().findFragmentByTag("FAVPAGE") != null) {
                        transaction.remove(getSupportFragmentManager().findFragmentByTag("FAVPAGE"));
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

    private void setHide() {
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

    public void setTitle() {
        String t_name = "";
        try {
            if (_preferences.getString("username", null) != null && AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")).length() > 8) {
                t_name = AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")).substring(0, 8) + "...";
            } else {
                t_name = AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", ""));
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        if (TextUtils.isEmpty(t_name)) {
            mbinding.layoutSearch.txtSearch.setText("어떤 여행을 찾고 있나요?");
        } else {
            mbinding.layoutSearch.txtSearch.setText(t_name + "님, 어떤 여행을 찾고 있나요?");
        }
    }

    public void setTapdelete(String tag) {
        transaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null && !fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
            transaction.commitAllowingStateLoss();
        }
    }

    public void showToast(String msg) {
        mbinding.toastLayout.setVisibility(View.VISIBLE);
        mbinding.tvToast.setText(msg);
        mbinding.icoFavorite.setVisibility(View.GONE);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mbinding.toastLayout.setVisibility(View.GONE);
                    }
                }, 1000);

        moveTabRefresh2();
        moveTabRefresh3();
    }

    public void showIconToast(String msg, boolean is_fav) {
        mbinding.toastLayout.setVisibility(View.VISIBLE);
        mbinding.tvToast.setText(msg);

        if (is_fav) { // 성공
            mbinding.icoFavorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        } else { // 취소
            mbinding.icoFavorite.setBackgroundResource(R.drawable.ico_titbar_favorite);
        }
        mbinding.icoFavorite.setVisibility(View.VISIBLE);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mbinding.toastLayout.setVisibility(View.GONE);
                    }
                }, 1500);

        moveTabRefresh2();
        moveTabRefresh3();
    }

    public void moveTabReservation() {
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        setHide();
                        mbinding.navigation.setCurrentItem(2);
                    }
                }, 100);
    }

    public void moveTabRefresh() {
        HomeFragment fm = (HomeFragment) getSupportFragmentManager().findFragmentByTag("SELECTPAGE");

        if (fm != null) {
            if (dbHelper.selectAllRecentItem("10").size() > 0 && !CONFIG.isRecent) {
                fm.getRecentData(true);
                CONFIG.isRecent = true;
            } else {
                fm.getRecentData(false);
                fm.setLikeRefresh(false);
            }
        }
    }

    public void HomePopup() {
        HomeFragment fm = (HomeFragment) getSupportFragmentManager().findFragmentByTag("SELECTPAGE");
        if (fm != null) {
            fm.setPopup();
        }
    }

    public void moveTabRefresh2() {
        HotelFragment fm = (HotelFragment) getSupportFragmentManager().findFragmentByTag("HOTELPAGE");
        if (fm != null) {
            fm.allRefresh();
        }
    }

    public void moveTabRefresh3() {
        LeisureFragment fm = (LeisureFragment) getSupportFragmentManager().findFragmentByTag("LEISUREPAGE");
        if (fm != null) {
            fm.allRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            Util.clearSearch();
            super.onBackPressed();
        } else {
            hideProgress();
            Toast.makeText(getApplicationContext(), getString(R.string.back_button), Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (myPosition == 0) {
            if (CONFIG.isRecent) {
                moveTabRefresh();
            }
        } else if (myPosition == 1) {
            moveTabRefresh2();
        } else if (myPosition == 2) {
            moveTabRefresh3();
        }

        if (CONFIG.Mypage_Search) {
            moveTabReservation();
        }

        if(_preferences.getString("userid", null) != null && CONFIG.MYLOGIN == true){
            setTitle();
            setTapdelete("MYPAGE");
            CONFIG.MYLOGIN = false;
        }

        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void tabStatus(boolean enable) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mbinding.toolbar.getLayoutParams();
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) mbinding.appbar.getLayoutParams();
        if (enable) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
            mbinding.appbar.setLayoutParams(appBarLayoutParams);
        } else {
            params.setScrollFlags(0);
            appBarLayoutParams.setBehavior(null);
            mbinding.appbar.setLayoutParams(appBarLayoutParams);
        }
    }

    public void wrapTabIndicatorToTitle(TabLayout tabLayout, int externalMargin, int internalMargin) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            int childCount = ((ViewGroup) tabStrip).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View tabView = tabStripGroup.getChildAt(i);
                //set minimum width to 0 for instead for small texts, indicator is not wrapped as expected
                tabView.setMinimumWidth(0);
                // set padding to 0 for wrapping indicator as title
                tabView.setPadding(0, tabView.getPaddingTop(), 0, tabView.getPaddingBottom());
                // setting custom margin between tabs
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    if (i == 0) {
                        // left
                        settingMargin(layoutParams, externalMargin, internalMargin);
                    } else if (i == childCount - 1) {
                        // right
                        settingMargin(layoutParams, internalMargin, externalMargin);
                    } else {
                        // internal
                        settingMargin(layoutParams, internalMargin, internalMargin);
                    }
                }
            }

            tabLayout.requestLayout();
        }
    }

    private void settingMargin(ViewGroup.MarginLayoutParams layoutParams, int start, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(start);
            layoutParams.setMarginEnd(end);
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        } else {
            layoutParams.leftMargin = start;
            layoutParams.rightMargin = end;
        }
    }
}
