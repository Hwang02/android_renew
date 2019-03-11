package com.hotelnow.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.kakao.auth.KakaoSDK;
import com.tune.Tune;

import java.net.CookieHandler;
import java.net.CookiePolicy;

import android.support.multidex.MultiDexApplication;

import io.fabric.sdk.android.Fabric;

/**
 * Created by susia on 15. 12. 8..
 */

public class HotelnowApplication extends MultiDexApplication{
    private static final String PROPERTY_ID = "UA-40726275-4";
//    private Tracker mTracker;
    private static Context context;
    private SharedPreferences _preferences;
    private static volatile HotelnowApplication obj = null;
    private static volatile Activity currentActivity = null;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

//    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());

        // enable cookies
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        // TUNE
        Tune.init(this, "192489", "22923c1467a436aad7b62d3ccbad4f52");

        //  구글플레이로 배포되므로 생략해도 되는부분
        HotelnowApplication.context = getApplicationContext();
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (_preferences.getBoolean("flag_first_executed", false) != false) {
            Tune.getInstance().setExistingUser(true);
        }
    }

    public static Context getAppContext() {
        return HotelnowApplication.context;
    }

    public HotelnowApplication() {
        super();
    }

//    synchronized public Tracker getTracker(TrackerName trackerId) {
//        if (!mTrackers.containsKey(trackerId)) {
//
//            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
//                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
//                    : analytics.newTracker(R.xml.ecommerce_tracker);
//            mTrackers.put(trackerId, t);
//
//        }
//        return mTrackers.get(trackerId);
//    }

    public static HotelnowApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        HotelnowApplication.currentActivity = currentActivity;
    }
}
