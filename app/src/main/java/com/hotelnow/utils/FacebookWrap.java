package com.hotelnow.utils;

import android.content.Context;
import android.os.Bundle;

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
    public static void logViewedContentEvent (Context mContext, String contentType) {
        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, params);
    }


    /*
    Search
    searchString : 검색어
    */
    public static void Search(Context mContext, String searchString, boolean success){
        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, searchString);
        params.putInt(AppEventsConstants.EVENT_PARAM_SUCCESS, success ? 1 : 0);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params);
    }

    /*
    Purchased Event
    */
    public static void logPurchasedEvent (Context mContext, int price) {
        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);
        logger.logPurchase(BigDecimal.valueOf(price), Currency.getInstance(currency));
    }
}
