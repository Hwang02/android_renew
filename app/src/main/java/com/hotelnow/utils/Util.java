package com.hotelnow.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.hotelnow.R;
import com.hotelnow.activity.ActLoading;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by susia on 15. 9. 21..
 */
public class Util {
    private enum LocationAwareness { LOCATION_AWARENESS_NORMAL, LOCATION_AWARENESS_TRUNCATED, LOCATION_AWARENESS_DISABLED }
    private static LocationAwareness mLocationAwareness = LocationAwareness.LOCATION_AWARENESS_NORMAL;
    private static int mLocationPrecision = 6;	//	lat, lng 소수점
    public static Location userLocation = null;
    private static Calendar startCal = Calendar.getInstance();
    private static Calendar endCal = Calendar.getInstance();

    // unique Android-id
    public static String getAndroidId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    // app version :: 2.0
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }

        return versionName;
    }

    // 안드로이드 user agent 문자열 생성
    public static String getUserAgent(Context context){
        String returnStr = "Android ";

        returnStr += Build.VERSION.RELEASE+"(SDK-"+ String.valueOf(Build.VERSION.SDK_INT)+");";
        returnStr += context.getResources().getConfiguration().locale+";";
        returnStr += Build.MODEL+";"+ Build.MANUFACTURER+";"+ Build.PRODUCT;

        return returnStr;
    }

    // 리스트 이미지 높이 확인
    public static int getListHeight(Context context){
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(_preferences.getFloat("list_height", 0) <= 0){
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int imgHeight = (int) Math.round(screenWidth/16*9);

            return imgHeight;
        } else {
            return (int) _preferences.getFloat("list_height", 0);
        }
    }

    public static String getEncode(String mSearch){
        String ALLOWED_URI_CHARS = "@#&=*-_.,:!()/~";
        String encodedUri = Uri.encode(mSearch, ALLOWED_URI_CHARS);

        return encodedUri;
    }

    public static int dptopixel(Context context, int dp){
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static int getFullHeight(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        return height;
    }

    // 위치정보 사용 설정 확인
    public static boolean chkLocationService(Context context) {
        boolean gps_enabled = false, network_enabled = false;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }

        return (gps_enabled || network_enabled);
    }

    // 추천 팝업을 위한 날짜 차이 확인
    public static boolean getDateGap(Context context){
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(context);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyyMMdd");
        int curDate = Integer.valueOf(CurDateFormat.format(date));
        int last_date = _preferences.getInt("cookie_recommendation_date", 0);

        if(curDate-last_date >= 3){
            return true;
        } else {
            return false;
        }
    }

    // 추천 팝업 안띄우려 값 설정
    public static void setDateGap(Context context){
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(context);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyyMMdd");
        int curDate = Integer.valueOf(CurDateFormat.format(date));

        SharedPreferences.Editor prefEditor = _preferences.edit();
        prefEditor.putInt("cookie_recommendation_date", curDate);
        prefEditor.commit();
    }

    // captcha image string
    public static void captchaImgUrl(final Context context, final ImageView captcha){
        Api.get(CONFIG.captchaUrl, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(HotelnowApplication.getAppContext(), "보안문자를 받아오지 못했습니다. 잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    JSONArray data = obj.getJSONArray("data");
                    String str_captcha = data.getJSONObject(0).getString("captcha");

                    byte[] decodedString = Base64.decode(str_captcha, 0);
                    Bitmap bitmapObj = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    captcha.setImageBitmap(bitmapObj);
                } catch (Exception e) {
                    Toast.makeText(HotelnowApplication.getAppContext(), "보안문자를 받아오지 못했습니다. 잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 월 일 에 0 붙임
    public static String leftZero(String num){
        if(num.length() == 1)
            return "0"+num;
        else
            return num;
    }

    // 소팅에서 사용
    public static String rightZero(String num){
        if(num.length() == 1)
            return num+"0";
        else
            return num;
    }

    // unescape html string
    public static String stringToHTMLString(String string) {
        return string.replace("&lt;", "<").replace("&gt;", "<").replace("&amp;", "<").replace("&quot;", "\"").replace("&nbsp;", " ");
    }


    // GCM TOKEN
    public static void setGcmToken(Context context, String regId, String userId, Boolean flag){
        String androidId = Util.getAndroidId(context);

        JSONObject paramObj = new JSONObject();

        try{
            paramObj.put("os", "a");
            paramObj.put("uuid", androidId);
            paramObj.put("push_token", regId);
            paramObj.put("ver", Util.getAppVersionName(context));

            if(flag != null) {
                paramObj.put("use_yn", ((flag == true)? "Y":"N"));
            }
            if(userId != null) paramObj.put("user_id", userId);
        } catch (JSONException e) {}

        Api.post(CONFIG.notiSettingUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                } catch (Exception e) {
                }
            }
        });
    }

    // 앱 리스타트
    public static void doRestart(Context c) {
        try {
            if (c != null) {
                PackageManager pm = c.getPackageManager();
                Intent i = new Intent(c.getApplicationContext(), ActLoading.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(i);
                Toast.makeText(c, "오류로 인해 앱을 재시작 합니다.", Toast.LENGTH_SHORT).show();
            } else {
				Log.e("doRestart", "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
			Log.e("doRestart", "Was not able to restart application");
        }
    }

    // 추천인코드 만들기
    public static String getRecommCode(String s) {
        String tmp = (new StringBuffer(s) ).reverse().toString();
        return String.format("%-6s", tmp).replace(' ', '0');
    }

    // 넘버포멧
    public static String numberFormat(int num) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return nf.format(num);
    }

    public static String todayFormat(){
        String mDay = "";
        try {
            Date today;
            Calendar todayCal = Calendar.getInstance();
            todayCal.setTime(new Date());
            today = todayCal.getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            mDay = df.format(today);
        } catch (Exception e){}

        return mDay;
    }

    public static String todayFormat2(){
        String mDay = "";
        try {
            Date today;
            Calendar todayCal = Calendar.getInstance();
            todayCal.setTime(new Date());
            today = todayCal.getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy년MM월dd일");
            mDay = df.format(today);
        } catch (Exception e){}

        return mDay;
    }

    // 요일 날짜포멧
    public static String weekdayFormat(String dt) {
        String wdt = "";

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN);
            Date to = formatter.parse(dt);
            wdt = transFormat.format(to);
        } catch (Exception e){}

        return wdt;
    }

    // 요일 날짜포멧
    public static String weekdayFormatMd(String dt) {
        String wdt = "";

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat transFormat = new SimpleDateFormat("MM/dd", Locale.KOREAN);
            Date to = formatter.parse(dt);
            wdt = transFormat.format(to);
        } catch (Exception e){}

        return wdt;
    }


    // 디바이스 너비
    public static int checkDeviceWidth(){
        DisplayMetrics dm = HotelnowApplication.getAppContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    // 날짜 차 계산
    public static long diffOfDate(String begin, String end) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd",Locale.KOREAN);

        Date beginDate;
        Date endDate;
        long diff;
        long diffDays = 0;

        try {
            beginDate = formatter.parse(begin);
            endDate = formatter.parse(end);
            diff = endDate.getTime() - beginDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diffDays;
    }

    // 팝업 노출 여부
    public static Boolean showFrontPopup(String pref) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date;

        try {
            Date currentTime = new Date();
            date = format.parse(pref);

            if(currentTime.before(date)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String getFrontThemeId(String thumb_link_action) {
        String[] arr = thumb_link_action.split("otelnowevent://");
        String evtObj = arr[1];
        evtObj = Util.stringToHTMLString(evtObj);

        try {
            JSONObject obj = new JSONObject(evtObj);
            return obj.getString("param");
        } catch (Throwable t) {
            return "";
        }
    }

    // Preference 값 설정
    public static void setPreferenceValues(SharedPreferences preferences, String prefKey, boolean prefVal){
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean(prefKey, prefVal);
        prefEditor.commit();
    }

    public static void setPreferenceValues(SharedPreferences preferences, String prefKey, int prefVal){
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putInt(prefKey, prefVal);
        prefEditor.commit();
    }

    public static void setPreferenceValues(SharedPreferences preferences, String prefKey, String prefVal){
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(prefKey, prefVal);
        prefEditor.commit();
    }

    public static void setPreferenceValues(SharedPreferences preferences, String prefKey, Float prefVal){
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putFloat(prefKey, prefVal);
        prefEditor.commit();
    }

    public static void showKakaoLink(Activity activity){
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String cookie = _preferences.getString("userid", null);

        if (cookie != null) {
            try {
                KakaoLink kakaoLink = KakaoLink.getKakaoLink(activity);
                KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

                kakaoTalkLinkMessageBuilder.addText("[호텔나우]\n"
                        + _preferences.getString("username", null)
                        + "님이 호텔나우 "+Util.numberFormat(_preferences.getInt("reserve_money", CONFIG.default_reserve_money))+
                        "원 적립금을 드립니다!\n추천인코드 입력하고 "+Util.numberFormat(_preferences.getInt("reserve_money", CONFIG.default_reserve_money))+"원을 바로 받아보세요!\n추천인코드:"
                        + Util.getRecommCode(_preferences.getString("userid", null)));
                kakaoTalkLinkMessageBuilder.addAppButton("앱으로 이동");
                kakaoTalkLinkMessageBuilder.addImage(CONFIG.kakaotalkimg, 300, 300);
                kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, activity);
            } catch (KakaoParameterException e) {
//				Log.e("KakaoParameterException", e.toString());
            }
        } else {
            Toast.makeText(activity, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareFacebookFeed(final Activity activity, CallbackManager callbackManager){
        final SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        LoginManager manager;
        Profile profile = Profile.getCurrentProfile().getCurrentProfile();

        String cookie = _preferences.getString("userid", null);

        if (cookie != null) {
            if(profile != null) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()

                        .setContentTitle(_preferences.getString("username", null) + "님이 호텔나우 적립금 " + Util.numberFormat(_preferences.getInt("reserve_money", CONFIG.default_reserve_money)) + "원을 드립니다."+"(추천인코드:"+ Util.getRecommCode(_preferences.getString("userid", null))+")")
                        .setContentDescription(_preferences.getString("username", null) + "님이 호텔나우 적립금 "+ Util.numberFormat(_preferences.getInt("reserve_money", CONFIG.default_reserve_money)) +"원을 드립니다. " +
                                "추천인코드 : "+ Util.getRecommCode(_preferences.getString("userid", null))+ " 입력하고 "+ Util.numberFormat(_preferences.getInt("reserve_money", CONFIG.default_reserve_money))+"원을 바로 받아보세요!" )
                        .setContentUrl(Uri.parse(CONFIG.marketUrl))
                        .setImageUrl(Uri.parse("http://d2gxin9b07oiov.cloudfront.net/web/favicon_152.png"))
                        .build();

                ShareDialog shareDialog;
                shareDialog = new ShareDialog(activity);
                shareDialog.show(linkContent);
            } else {
                manager = LoginManager.getInstance();
                List<String> permissionNeeds = Arrays.asList("publish_actions");
                manager.logInWithPublishPermissions(activity, permissionNeeds);
                manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(_preferences.getString("username", null) + "님이 호텔나우 적립금 " + Util.numberFormat(_preferences.getInt("reserve_money", CONFIG.default_reserve_money)) + "원을 드립니다."+"(추천인코드:"+ Util.getRecommCode(_preferences.getString("userid", null))+")")
                                .setContentDescription(_preferences.getString("username", null) + "님이 호텔나우 적립금 "+ Util.numberFormat(_preferences.getInt("reserve_money", CONFIG.default_reserve_money)) +"원을 드립니다. " +
                                        "추천인코드 : "+ Util.getRecommCode(_preferences.getString("userid", null))+ " 입력하고 "+ Util.numberFormat(_preferences.getInt("reserve_money", CONFIG.default_reserve_money))+"원을 바로 받아보세요!" )
                                .setContentUrl(Uri.parse(CONFIG.marketUrl))
                                .setImageUrl(Uri.parse("http://d2gxin9b07oiov.cloudfront.net/web/favicon_152.png"))
                                .build();

                        ShareDialog shareDialog;
                        shareDialog = new ShareDialog(activity);
                        shareDialog.show(linkContent);
                    }

                    @Override
                    public void onCancel() {
                        Log.e(CONFIG.TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e(CONFIG.TAG, "onError");
                    }
                });
            }
        } else {
            Toast.makeText(activity, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 옐로우아이디
    public static void kakaoYelloId(Activity activity) {
        try {
            String tmp = "kakaoplus://plusfriend/friend/@%ED%98%B8%ED%85%94%EB%82%98%EC%9A%B0";

            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tmp));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);

        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kakao.talk"));
                activity.startActivity(intent);
            } catch (android.content.ActivityNotFoundException anfe) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.kakao.talk"));
                activity.startActivity(intent);
            }
        }
    }

    // 옐로우아이디
    public static void kakaoYelloId2(Activity activity) {
        try {
            String tmp = "http://pf.kakao.com/_CxgQxjl/chat";

            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tmp));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);

        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kakao.talk"));
                activity.startActivity(intent);
            } catch (android.content.ActivityNotFoundException anfe) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.kakao.talk"));
                activity.startActivity(intent);
            }
        }
    }

    public static boolean in_array(String[] haystack, String needle) {
        for(int i=0;i<haystack.length;i++) {
            if(haystack[i].startsWith("4:") && needle.equals("4")) {
                return true;
            } else {
                if(haystack[i].equals(needle)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getGradeText(String val) {
        Double value = Double.valueOf(val);

        if(value >= 4.6 && value <= 5){
            return "강력추천";
        } else if(value >= 3.6 && value <= 4.5){
            return "추천함";
        } else if(value >= 2.6 && value <= 3.5){
            return "만족스러움";
        } else if(value >= 1.6 && value <= 2.5){
            return "보통";
        } else if(value >= 1.0 && value <= 1.5){
            return "개선이 필요함";
        }

        return "";
    }

    public static String getCategory(Context mContext, String val) {
        String[] categorycodearr = mContext.getResources().getStringArray(R.array.category_code);
        String[] categorytextarr = mContext.getResources().getStringArray(R.array.category_text);
        int position = 0;
        for(int i=0; i<categorycodearr.length; i++){
            if(categorycodearr[i].equals(val)){
                position = i;
                break;
            }
        }
        return categorytextarr[position];
    }

    // 검색조건 초기화
//    public static boolean hasSearch(){
//        String mCheckinout[] = null;
//        if(getDefaultCheckinout().contains(",")) {
//            mCheckinout = getDefaultCheckinout().split(",");
//        }
//        if( (CONFIG.sel_keyword != null && !CONFIG.sel_keyword.equals(""))  ||
//            (CONFIG.sel_area != null && !CONFIG.sel_area.equals("all"))    ||
//            CONFIG.sel_subarea != null && !CONFIG.sel_subarea.equals("") ||
//            CONFIG.sel_category != null ||
//            CONFIG.sel_facility != null ||
//            mCheckinout !=null && mCheckinout.length>0 &&
//            ((CONFIG.sel_checkin != null && !CONFIG.sel_checkin.equals(mCheckinout[0])) ||
//            (CONFIG.sel_checkout != null && !CONFIG.sel_checkout.equals(mCheckinout[1])))){
//            return true;
//        } else {
//            return false;
//        }
//    }

    // 검색조건 초기화
//    public static boolean fixedCheck(){
//        String mCheckinout[] = null;
//        if(getDefaultCheckinout().contains(",")) {
//            mCheckinout = getDefaultCheckinout().split(",");
//        }
//        if( CONFIG.sel_keyword == null &&
//            CONFIG.sel_category == null &&
//            CONFIG.sel_facility == null &&
//            mCheckinout !=null &&
//            mCheckinout.length>0 &&
//            (CONFIG.sel_checkin != null && CONFIG.sel_checkin.equals(mCheckinout[0])) &&
//            (CONFIG.sel_checkout != null && CONFIG.sel_checkout.equals(mCheckinout[1]))){
//            return true;
//        } else {
//            return false;
//        }
//    }

    // 필터검색 유무
//    public static boolean FilterCheck(){
//        boolean IsselPrice = false;
//        if(CONFIG.sel_maxPrice.equals("200000") && CONFIG.sel_minPrice.equals("0")){
//            IsselPrice = false;
//        }
//        else if(CONFIG.sel_maxPrice.equals("") && CONFIG.sel_minPrice.equals("")){
//            IsselPrice = false;
//        }
//        else{
//            IsselPrice = true;
//        }
//
//        if( (CONFIG.sel_category != null && !CONFIG.sel_category.equals(""))
//                || (CONFIG.sel_facility != null && !CONFIG.sel_facility.equals(""))
//                || IsselPrice )
//        {
//            return true;
//        } else {
//            return false;
//        }
//    }

    // 다음날
    public static String getNextDateStr(String curDate) {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date;

        try {
            date = format.parse(curDate);
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } catch (Exception e) {}

        return format.format(calendar.getTime());
    }

    // 다음날
    public static Date getNextDate(String curDate) {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date;

        try {
            date = format.parse(curDate);
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } catch (Exception e) {}

        return calendar.getTime();
    }

    // 이전날
    public static String getPreDate(String curDate) {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date;

        try {
            date = format.parse(curDate);
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        } catch (Exception e) {}

        return format.format(calendar.getTime());
    }

//    public static void clearSearch(){
//        CONFIG.sel_sort = "";													        // 필터
//        CONFIG.sel_keyword = null;														// 검색문자
//        CONFIG.sel_area = "all";															// 검색도시
//        CONFIG.sel_subarea = null;														// 검색구
//        CONFIG.sel_category = null;														// 검색가테고리
//        CONFIG.sel_facility = null;														// 검색부대시설
//        CONFIG.sel_checkin = null;														// 검색체크인
//        CONFIG.sel_checkout = null;														// 검색체크아웃
//        CONFIG.sel_minPrice = "";												        // 검색가격대
//        CONFIG.sel_maxPrice = "";                                                       // 검색가격대
//        CONFIG.isAdd = true;                                                            // item 추가 여부
//        CONFIG.mPage = 1;                                                               //mainpage 호출된 페이지
//        CONFIG.sel_sort_price="";                                                       //필터 가격순
//        CONFIG.sel_ticket_area = null;
//        CONFIG.sel_ticket_category = null;
//        CONFIG.sel_areaticketname = null;
//        CONFIG.sel_categoryticketname = null;
//        CONFIG.sel_ticket_keyword = null;
//        CONFIG.sel_booking_tab = true;
//        CONFIG.ticket_isAdd = true;
//        CONFIG.mticket_Page = 1;
//    }

//    private static String getDefaultCheckinout(){
//        Date dateObj = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat formatterdt = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
//
//        // 서버타임있는지 확인하고 없으면 설정
//        if(CONFIG.svr_date == null) {
//            long time = System.currentTimeMillis();
//            CONFIG.svr_date = new Date(time);
//        }
//
//        // 현재 시간 object 설정
//        try {
//            dateObj = CONFIG.svr_date;
//        } catch(Exception e){}
//
//        startCal.setTime(dateObj);
//        endCal.setTime(dateObj);
//        endCal.add(Calendar.DATE, 1);
//
//        if(CurHourFormat.format(dateObj).equals("00") || CurHourFormat.format(dateObj).equals("01")){
//            startCal.add(Calendar.DATE, -1);
//            endCal.add(Calendar.DATE, -1);
//        }
//
//        return formatterdt.format(startCal.getTime())+","+formatterdt.format(endCal.getTime());
//    }

    public static void setStatusColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.header_dark));
        }
    }

    public static void setStatusTransColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        }
    }

    //하단 버튼 유무
    public static boolean hasNavBar(Context context) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            return resources.getBoolean(id);
        } else {    // Check for keys
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !hasMenuKey && !hasBackKey;
        }
    }

    //하단 버튼 높이
    public static int getSoftMenuHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int deviceHeight = 0;

        if (resourceId > 0) {
            deviceHeight = resources.getDimensionPixelSize(resourceId);
        }
        return deviceHeight;
    }

    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidName(String name) {
        boolean err = false;
        String regex = "^[a-zA-Z0-9ㄱ-ㅎ가-힇]+$";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        if(m.matches()) {
            err = true;
        }
        return err;
    }
}
