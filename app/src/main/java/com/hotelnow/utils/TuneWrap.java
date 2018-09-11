package com.hotelnow.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tune.Tune;
import com.tune.TuneEvent;
import com.tune.TuneEventItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by susia on 16. 7. 8..
 */
public class TuneWrap {
    private static final String currency = "KRW";

    //  Screen Name
    public static void ScreenName(String screen_name) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        TuneEventItem eventItem = new TuneEventItem(screen_name);
        ArrayList<TuneEventItem> events = new ArrayList<TuneEventItem>();
        events.add(eventItem);

        Tune tune = Tune.getInstance();
        tune.setUserId(_preferences.getString("userid",""));
        tune.setUserEmail(_preferences.getString("email", ""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(TuneEvent.CONTENT_VIEW).withEventItems(events).withCurrencyCode(currency));
    }

    //  Screen Name
    public static void ScreenNameDetail(String screen_name, String depth1, String depth2) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        TuneEventItem eventItem = new TuneEventItem(screen_name).withAttribute1(depth1).withAttribute2(depth2);
        ArrayList<TuneEventItem> events = new ArrayList<TuneEventItem>();
        events.add(eventItem);

        Tune tune = Tune.getInstance();
        tune.setUserId(_preferences.getString("userid",""));
        tune.setUserEmail(_preferences.getString("email", ""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(TuneEvent.CONTENT_VIEW).withEventItems(events).withCurrencyCode(currency));
    }

    //  Event1
    public static void Event(String evt_name, String depth1) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        tune.setUserId(_preferences.getString("userid",""));
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1));
    }

    //  Event2
    public static void Event(String evt_name, String depth1, String depth2) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        tune.setUserId(_preferences.getString("userid",""));
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2));
    }

    //  Event3
    public static void Event(String evt_name, String depth1, String depth2, String depth3) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        tune.setUserId(_preferences.getString("userid",""));
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2).withAttribute3(depth3));
    }

    //  Login
    public static void Login(){
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username", ""));
        tune.setUserId(_preferences.getString("userid", ""));
        tune.measureEvent(TuneEvent.LOGIN);
    }

    //  Registration
    public static void Registration(){
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username",""));
        tune.setUserId(_preferences.getString("userid",""));
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
        tune.setUserId(_preferences.getString("userid",""));
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
    public static void Purchase(TuneEventItem eventItem, float revenue, String booking_id) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        ArrayList<TuneEventItem> events = new ArrayList<TuneEventItem>();
        events.add(eventItem);

        Tune tune = Tune.getInstance();
        tune.setUserId(_preferences.getString("userid", ""));
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username",""));
        tune.measureEvent(new TuneEvent(TuneEvent.PURCHASE)
                .withEventItems(events)
                .withRevenue(revenue)
                .withCurrencyCode(currency)
                .withAdvertiserRefId(booking_id));
    }

    // Search
    public static void Search(String keyword) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        tune.setUserId(_preferences.getString("userid", ""));
        tune.setUserEmail(_preferences.getString("email",""));
        tune.setUserName(_preferences.getString("username",""));
        tune.measureEvent(new TuneEvent(TuneEvent.SEARCH).withSearchString(keyword));
    }

}
