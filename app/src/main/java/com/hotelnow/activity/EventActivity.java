package com.hotelnow.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * Created by susia on 16. 1. 11..
 */
public class EventActivity extends AppCompatActivity {
    WebView webView;
    String linkUrl = "";
    JSONObject evtObj = null;
    final Handler handler = new Handler();
    SharedPreferences _preferences;
    String cookie;
    int idx;
    String obj;
    String uid="";
    DialogAlert dialogAlert;
    String selhid;
    boolean fromPush =false;
    //  fb
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Util.setStatusColor(this);

        // facebook
        try {
            AppEventsLogger.activateApp(getApplication(), getResources().getString(R.string.facebook_app_id));
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }
        callbackManager = CallbackManager.Factory.create();

        Intent intent = getIntent();
        idx = intent.getIntExtra("idx", 0);
        obj = intent.getStringExtra("obj");

        if(intent != null) {
            fromPush = intent.getBooleanExtra("from_push", false);
        }
        
        _preferences = PreferenceManager.getDefaultSharedPreferences(EventActivity.this);
        cookie = _preferences.getString("userid", null);
        uid = _preferences.getString("userid", "");

        webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webSettings.setUserAgentString(webSettings.getUserAgentString() + " HOTELNOW_APP_ANDROID");
        webView.addJavascriptInterface(new DetailInterface(), "DetailInterface");	// 디테일 페이지 인터페이스
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/404.html");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(cookie != null){
                    webView.loadUrl("javascript:func_from_native('" + cookie + "')");
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("hotelnowevent://")){
                    String[] arr = url.split("otelnowevent://");
                    String evtObj = arr[1];

                    try {
                        JSONObject obj = new JSONObject(evtObj);

                        // social_open
                        if(obj.getString("method").equals("social_open")){
                            if(obj.getString("param").equals("kakao")){
                                Util.showKakaoLink(EventActivity.this);

                            } else if(obj.getString("param").equals("facebook")){
                                Util.shareFacebookFeed(EventActivity.this, callbackManager);

                            }
                        }

                        // move_detail
                        if(obj.getString("method").equals("move_detail")){

                            JSONObject info = new JSONObject(obj.getString("param"));

                            String hid = info.getString("hotel_id");
                            String sdate = info.getString("date");
                            String edate = info.getString("e_date");
                            String is_event = info.getString("is_event");

                            Intent intent = new Intent(EventActivity.this, DetailHotelActivity.class);
                            intent.putExtra("hid", hid);
                            intent.putExtra("evt", is_event);
                            intent.putExtra("sdate", sdate);
                            intent.putExtra("edate", edate);
                            startActivity(intent);
                        }

                        // move_near
                        if(obj.getString("method").equals("move_near")){
                            selhid = obj.getString("param");
                            int fDayLimit = _preferences.getInt("future_day_limit", 180);
                            String checkurl = CONFIG.checkinDateUrl+"/"+selhid+"/"+ String.valueOf(fDayLimit);

                            Api.get(checkurl, new Api.HttpCallback() {
                                @Override
                                public void onFailure(Response response, Exception e) {
                                    Toast.makeText(EventActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                @Override
                                public void onSuccess(Map<String, String> headers, String body) {
                                    try {
                                        JSONObject obj = new JSONObject(body);

                                        if (!obj.getString("result").equals("success")) {
                                            Toast.makeText(EventActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        JSONArray aobj = obj.getJSONArray("data");

                                        if (aobj.length() == 0) {
                                            dialogAlert = new DialogAlert(
                                                    getString(R.string.alert_notice),
                                                    "해당 숙소는 현재 예약 가능한 객실이 없습니다.",
                                                    EventActivity.this,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialogAlert.dismiss();
                                                        }
                                                    });
                                            dialogAlert.setCancelable(false);
                                            dialogAlert.show();

                                            return;
                                        }

                                        String checkin = aobj.getString(0);
                                        String checkout = Util.getNextDateStr(checkin);

                                        Intent intent = new Intent(EventActivity.this, DetailHotelActivity.class);
                                        intent.putExtra("hid", selhid);
                                        intent.putExtra("evt", "N");
                                        intent.putExtra("sdate", checkin);
                                        intent.putExtra("edate", checkout);
                                        startActivity(intent);
                                    } catch (Exception e) {
//                                        Log.e(CONFIG.TAG, e.toString());
                                        Toast.makeText(EventActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                }
                            });
                        }

                        // move_hotel
                        if(obj.getString("method").equals("move_hotel")){
                            String hid = obj.getString("param");

                            Intent intent = new Intent(EventActivity.this, DetailHotelActivity.class);
                            intent.putExtra("hid", hid);
                            intent.putExtra("evt", "Y");
                            startActivity(intent);
                        }

                        // move_theme_ticket
                        if(obj.getString("method").equals("move_theme_ticket")){
                            String tid = obj.getString("param");

                            Intent intent = new Intent(EventActivity.this, DetailHotelActivity.class);
                            intent.putExtra("tid", tid);
                            startActivity(intent);
                        }

                        // move_ticket_detail
                        if(obj.getString("method").equals("move_ticket_detail")){
                            String tid = obj.getString("param");

                            Intent intent = new Intent(EventActivity.this, DetailHotelActivity.class);
                            intent.putExtra("tid", tid);
                            intent.putExtra("evt", "Y");
                            startActivity(intent);
                        }

//                        // move_theme
//                        if(obj.getString("method").equals("move_theme")){
//                            String tid = obj.getString("param");
//
//                            Intent intent = new Intent(EventActivity.this, ThemeHotelActivity.class);
//                            intent.putExtra("tid", tid);
//                            startActivity(intent);
//                        }

//                        // move_page
//                        if(obj.getString("method").equals("move_page")){
//                            String type = obj.getString("param");
//
//                            if(type.equals("account")){
//                                Intent intent = new Intent(EventActivity.this, IndexActivity.class);
//                                startActivity(intent);
//                                intent.putExtra("reserve", true);
//                                startActivity(intent);
//
//                            } else if(type.equals("booking")) {
//                                Intent intent = new Intent(EventActivity.this, IndexActivity.class);
//                                startActivity(intent);
//                                intent.putExtra("booking", true);
//                                startActivity(intent);
//                            }
//                        }

                        if(obj.getString("method").equals("outer_link") && obj.getString("param").contains("hotelnow")){
                            view.loadUrl(obj.getString("param"));
                        } else if(obj.getString("method").equals("outer_link") && obj.getString("param").contains("hotelnow") == false){
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(obj.getString("param")));
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "올바른 형식의 주소가 아닙니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(url.contains("plusfriend")){
                        Util.kakaoYelloId(EventActivity.this);

                    } else {
                        view.loadUrl(url);
                    }
                }

                return true;
            }
        });

        if(intent != null){
            try {
                if(idx > 0){
                    linkUrl = CONFIG.eventWebUrl+"/"+ String.valueOf(idx)+"?uid="+uid;
                } else {
                    evtObj = new JSONObject(obj);
                    linkUrl = CONFIG.eventWebUrl+"/"+evtObj.getString("id")+"?uid="+uid;
                }
            } catch (JSONException e) {
                webView.loadUrl("file:///android_asset/404.html");
                e.printStackTrace();
            }
        }

        webView.loadUrl(linkUrl);
    }

    private class DetailInterface
    {
        @JavascriptInterface
        public void reoladDetail(){
            handler.post( new Runnable() {
                public void run()
                {
                    webView.loadUrl(linkUrl+"?uid="+uid);
                }
            });
        }
        @JavascriptInterface
        public void selectEnter(){
            handler.post(new Runnable() {
                public void run() {
                    Intent intent = new Intent(EventActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 90);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 90 && resultCode == 90){
            cookie = _preferences.getString("userid", null);
            if(cookie != null){
                webView.loadUrl("javascript:func_from_native('" + cookie + "')");
            }
        }else {
            try {
                super.onActivityResult(requestCode, resultCode, data);
                callbackManager.onActivityResult(requestCode, resultCode, data);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}