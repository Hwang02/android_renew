package com.hotelnow.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.tune.Tune;
import com.tune.TuneEvent;
import com.tune.TuneEventItem;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by susia on 16. 7. 8..
 */
public class TuneWrap {
    private static final String currency = "KRW";

    //  Event1
    public static void Event(String evt_name) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tune.measureEvent(new TuneEvent(evt_name));
        }
    }

    //  Event1
    public static void Event(String evt_name, String depth1) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1));
        }
    }

    //  Event2
    public static void Event(String evt_name, String depth1, String depth2) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2));
        }
    }

    //  Event3
    public static void Event(String evt_name, String depth1, String depth2, String depth3) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2).withAttribute3(depth3));
        }
    }

    //  Event4
    public static void Event(String evt_name, String depth1, String depth2, String depth3, String depth4) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2).withAttribute3(depth3).withAttribute4(depth4));
        }
    }

    //  Event4
    public static void Event(String evt_name, String depth1, String depth2, String depth3, String depth4, String depth5) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());
        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tune.measureEvent(new TuneEvent(evt_name).withAttribute1(depth1).withAttribute2(depth2).withAttribute3(depth3).withAttribute4(depth4).withAttribute5(depth5));
        }
    }

    //  Login
    public static void Login() {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            tune.measureEvent(TuneEvent.LOGIN);
        }
    }

    //  Registration
    public static void Registration() {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tune.measureEvent(TuneEvent.REGISTRATION);
        }
    }

    // Reservation
    public static void Reservation(float revenue, int quantity, String booking_id, String checkin, String checkout) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Date ci = new Date();
        Date co = new Date();

        try {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ci = transFormat.parse(checkin);
            co = transFormat.parse(checkout);
        } catch (Exception e) {
        }

        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            tune.measureEvent(new TuneEvent(TuneEvent.RESERVATION)
//                .withRevenue(revenue)
                    .withCurrencyCode(currency)
                    .withDate1(ci)
                    .withDate2(co)
//                .withAdvertiserRefId(booking_id)
                    .withQuantity(quantity));
        }

    }

    // Purchase
    public static void Purchase(TuneEventItem eventItem, float revenue, String booking_id, boolean is_q) {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        try {
            String user_id = AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", ""));
            if (TextUtils.isEmpty(user_id)) {
                user_id = Util.getAndroidId(HotelnowApplication.getAppContext());
            }
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
            tune.setUserId(user_id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            String sel_type = "";
            if (is_q) {
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
            } else {
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

    }

    // Search
    public static void Search() {
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(HotelnowApplication.getAppContext());

        Tune tune = Tune.getInstance();
        try {
            tune.setUserId(AES256Chiper.AES_Decode(_preferences.getString("userid", "").replace("HN|", "")));
            tune.setUserEmail(AES256Chiper.AES_Decode(_preferences.getString("email", "").replace("HN|", "")));
            tune.setUserName(AES256Chiper.AES_Decode(_preferences.getString("username", "").replace("HN|", "")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tune.measureEvent(new TuneEvent("searchmain"));
        }

    }

}
