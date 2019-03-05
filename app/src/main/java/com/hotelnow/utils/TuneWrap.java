package com.hotelnow.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.tune.Tune;
import com.tune.TuneEvent;
import com.tune.TuneEventItem;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by susia on 16. 7. 8..
 */
public class TuneWrap {
    private static final String currency = "KRW";

    //  Screen Name
//    public static void ScreenName(String screen_name) {
//        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
//
//        TuneEventItem eventItem = new TuneEventItem(screen_name);
//        ArrayList<TuneEventItem> events = new ArrayList<TuneEventItem>();
//        events.add(eventItem);
//
//        Tune tune = Tune.getInstance();
//        tune.setUserId(_preferences.getString("userid",""));
//        tune.setUserEmail(_preferences.getString("email", ""));
//        tune.setUserName(_preferences.getString("username", ""));
//        tune.measureEvent(new TuneEvent(TuneEvent.CONTENT_VIEW).withEventItems(events).withCurrencyCode(currency));
//    }
//
//    //  Screen Name
//    public static void ScreenNameDetail(String screen_name, String depth1, String depth2) {
//        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
//
//        TuneEventItem eventItem = new TuneEventItem(screen_name).withAttribute1(depth1).withAttribute2(depth2);
//        ArrayList<TuneEventItem> events = new ArrayList<TuneEventItem>();
//        events.add(eventItem);
//
//        Tune tune = Tune.getInstance();
//        tune.setUserId(_preferences.getString("userid",""));
//        tune.setUserEmail(_preferences.getString("email", ""));
//        tune.setUserName(_preferences.getString("username", ""));
//        tune.measureEvent(new TuneEvent(TuneEvent.CONTENT_VIEW).withEventItems(events).withCurrencyCode(currency));
//    }

    //  Event1
    public static void Event(String evt_name) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name));
    }

    //  Event1
    public static void Event(String evt_name, String depth1) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1));
    }

    //  Event2
    public static void Event(String evt_name, String depth1, String depth2) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2));
    }

    //  Event3
    public static void Event(String evt_name, String depth1, String depth2, String depth3) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2).withAttribute3(depth3));
    }

    //  Event4
    public static void Event(String evt_name, String depth1, String depth2, String depth3, String depth4) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2).withAttribute3(depth3).withAttribute4(depth4));
    }

    //  Event4
    public static void Event(String evt_name, String depth1, String depth2, String depth3, String depth4, String depth5) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2).withAttribute3(depth3).withAttribute4(depth4).withAttribute5(depth5));
    }

    //  Login
    public static void Login(){
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.measureEvent(TuneEvent.LOGIN);
    }

    //  Registration
    public static void Registration(){
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username",""));
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.measureEvent(TuneEvent.REGISTRATION);
    }

    // Reservation
    public static void Reservation(float revenue, int quantity, String booking_id, String checkin, String checkout) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Date ci = new Date();
        Date co = new Date();
        int date_term = (int)Util.diffOfDate(checkin, checkout);

        try {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ci = transFormat.parse(checkin);
            co = transFormat.parse(checkout);
        } catch (Exception e) {}

        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username",""));
        tune.measureEvent(new TuneEvent(TuneEvent.RESERVATION)
//                .withRevenue(revenue)
                .withCurrencyCode(currency)
                .withDate1(ci)
                .withDate2(co)
//                .withAdvertiserRefId(booking_id)
                .withQuantity(quantity));
    }

    // Purchase
    public static void Purchase( TuneEventItem eventItem, float revenue, String booking_id, boolean is_q) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        try {
            String user_id = Util.decode(_preferences.getString("userid", "").replace("HN|",""));
            if(TextUtils.isEmpty(user_id)){
                user_id = Util.getAndroidId(HotelnowApplication.getAppContext());
            }
            tune.setUserId(user_id);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username",""));
        String sel_type = "";
        if(is_q) {
            tune.measureEvent(new TuneEvent("purchase_activity")
                    .withRevenue(revenue)
                    .withCurrencyCode(currency)
                    .withAttribute1(eventItem.attribute1) // 시티
                    .withAttribute2(eventItem.attribute2) // 상품id
                    .withAttribute3(eventItem.attribute3) // 카테고리
                    .withAttribute4(eventItem.attribute4) // 쿠폰명
                    .withAttribute5(eventItem.attribute5) // 쿠폰아이디
                    .withAdvertiserRefId(booking_id));
            sel_type = "activity";
        }
        else{
            tune.measureEvent(new TuneEvent("purchase_stay")
                    .withRevenue(revenue)
                    .withCurrencyCode(currency)
                    .withAttribute1(eventItem.attribute1) // 시티
                    .withAttribute2(eventItem.attribute2) // 상품id
                    .withAttribute3(Util.TuneCategory(HotelnowApplication.getAppContext(), eventItem.attribute3)) // 숙소등급
                    .withAttribute4(eventItem.attribute4) // 쿠폰명
                    .withAttribute5(eventItem.attribute5) // 쿠폰아이디
                    .withAdvertiserRefId(booking_id));
            sel_type = "stay";
        }

        tune.measureEvent(new TuneEvent(TuneEvent.PURCHASE)
                .withRevenue(revenue)
                .withCurrencyCode(currency)
                .withAttribute1(eventItem.attribute1) // 시티
                .withAttribute2(sel_type) // 타입
                .withAttribute3(eventItem.attribute2) // 상품id
                .withAttribute4(eventItem.attribute4) // 쿠폰명
                .withAttribute5(eventItem.attribute5) // 쿠폰id
                .withAdvertiserRefId(booking_id));
    }

    // Search
    public static void Search() {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(Util.decode(_preferences.getString("userid", "").replace("HN|","")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username",""));
        tune.measureEvent(new TuneEvent(TuneEvent.SEARCH));
    }

}
