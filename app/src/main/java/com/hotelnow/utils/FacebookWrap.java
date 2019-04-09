package com.hotelnow.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by idhwang on 2018. 3. 5..
 */

public class FacebookWrap {

    private static final String currency = "KRW";

    /*
    ViewedContent Event
    contentType : 화면
    */
    public static void logViewedContentEvent(Context mContext, String contentType, String contentID) {
        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        if(!TextUtils.isEmpty(contentID)) {
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentID);
        }
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params);
    }

    /*
   ViewedContent Event
   contentType : 화면
   */
    public static void logAddCartEvent(Context mContext, String contentType, String contentID) {
        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        if(!TextUtils.isEmpty(contentID)) {
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentID);
        }
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, params);
    }

    /*
    ViewedContent Event
    contentType : 화면
    */
    public static void logViewedContentEvent2(Context mContext, String contentType) {
        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);
        logger.logEvent(contentType);
    }

    /*
    Purchased Event
    */
    public static void logPurchasedEvent(Context mContext, String id, int price, String type) {
        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, id);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, type);

        logger.logPurchase(BigDecimal.valueOf(price), Currency.getInstance(currency), params);
    }
}
