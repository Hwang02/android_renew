package com.hotelnow.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.FacebookWrap;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;
import com.tune.TuneEventItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by susia on 16. 1. 8..
 */
public class PaymentActivity extends Activity {

    // Main WebView
    WebView webView;
    private final Handler handler = new Handler();
    private String uid, bid, pid, un, up;
    private String selected_card = "";
    private String cancelMsg = "";
    private String hotel_name = "";
    private String coupon_name = "";
    private String city = "";
    private String ptune = "";
    private String coupon_id = "";

    DialogAlert dialogAlert;
    DialogConfirm dialogConfirm;
    private boolean is_q = false;
    private boolean is_payco = false;

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_payment_webview);

        Util.setStatusColor(this);

//        Tracker t = ((HotelnowApplication)getApplication()).getTracker(HotelnowApplication.TrackerName.APP_TRACKER);
//        t.setScreenName("Payment");
//        t.send(new HitBuilders.AppViewBuilder().build());
//
//        TuneWrap.ScreenName("Payment");
//        FacebookWrap.logViewedContentEvent(this, "Payment");

        Intent intent = getIntent();
        int paytype = intent.getIntExtra("paytype", 1);
        hotel_name = intent.getStringExtra("hotel_name");
        bid = intent.getStringExtra("bid");
        pid = intent.getStringExtra("pid");
        un = intent.getStringExtra("uname");
        up = intent.getStringExtra("uphone");
        selected_card = intent.getStringExtra("selected_card");
        coupon_name = intent.getStringExtra("coupon_name");
        coupon_id = intent.getStringExtra("coupon_id");
        is_q = intent.getBooleanExtra("is_q", false);
        is_payco = intent.getBooleanExtra("is_payco", false);
        city = intent.getStringExtra("city");
        ptune = intent.getStringExtra("product_id");

        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            uid = _preferences.getString("userid", null) == null ? null : AES256Chiper.AES_Decode(_preferences.getString("userid", null).replace("HN|", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (uid == null) {
            uid = "0";
        }

        // Main WebView 생성
        webView = (WebView) findViewById(R.id.webview);

        // Main WebView 설정
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + " HOTELNOW_APP_ANDROID");
        webView.setWebViewClient(new MyViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new billWebInterface(), "billWebInterface");    // bill 취소 또는 성공

        String url = "";
        if (paytype == 1) {
//            url = CONFIG.billIndex+"?bid="+bid+"&uid="+uid;	// Main WebView에 스마트폰결제 URL 로딩
            url = CONFIG.KcpCardDomain + "?hn_pay_method=CARD_KCP" + "&bid=" + bid + "&uid=" + uid;    // kcp 카드결제 URL 로딩
        } else if (paytype == 2) {
            url = CONFIG.phonePayIndex + "?bid=" + bid + "&uid=" + uid;    // Main WebView에 모빌리언스 URL 로딩
        } else if (paytype == 3) {
            url = CONFIG.arsIndex + "?bid=" + bid + "&uid=" + uid;    // Main WebView에 ARS URL 로딩
        } else if (paytype == 4) {
            url = CONFIG.cardAddUrl + "?bid=" + bid + "&uid=" + uid + "&cid=" + selected_card;    // Main WebView에 간편결제 URL 로딩
        } else if (paytype == 5) {
            url = CONFIG.KcpCardDomain + "?hn_pay_method=VBANK_KCP" + "&bid=" + bid + "&uid=" + uid;    // kcp 카드결제 URL 로딩
        } else if (paytype == 6) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            url = CONFIG.KcpCardDomain + "?hn_pay_method=CARD_KCP" + "&bid=" + bid + "&uid=" + uid;    // 페이코
        }

        if (is_q) {
            url += "&is_q=Y";
        }

        if (is_payco) {
            url += "&is_payco=Y";
        }

//        Log.e(CONFIG.TAG, url);

        webView.loadUrl(url);
    }

    // 결제 성공, 실패, 취소시 웹페이지와 통신하는 javascript interface
    private class billWebInterface {
        @JavascriptInterface
        public void finishAlert(final String str) {
            handler.post(new Runnable() {
                public void run() {
                    if (!is_q) {
                        setBookingCancel("");
                    }
                    if (!isFinishing()) {
                        dialogAlert = new DialogAlert(
                                getString(R.string.alert_notice),
                                (str.length() <= 0) ? "결제를 취소하였습니다." : str,
                                PaymentActivity.this,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogAlert.dismiss();

                                        uid = "";
                                        finish();
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                    }
                                });
                        dialogAlert.setCancelable(false);
                        dialogAlert.show();
                    }
                }
            });
        }

        @JavascriptInterface
        public void paymentSuccess(final String payid) {
            handler.post(new Runnable() {
                public void run() {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("payid", payid);
                    } catch (JSONException e) {
                    }

                    if (is_q) {
                        String q_id = payid.replace("q_", "");
                        Api.post(CONFIG.ticketbookingSuccessUrl + "/" + q_id, params.toString(), new Api.HttpCallback() {
                            @Override
                            public void onFailure(Response response, Exception e) {
                                Toast.makeText(PaymentActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Map<String, String> headers, String body) {
                                try {
                                    JSONObject obj = new JSONObject(body);

                                    if (!obj.getString("result").equals("success")) {

                                        dialogAlert = new DialogAlert(
                                                getString(R.string.alert_notice),
                                                obj.getString("msg"),
                                                PaymentActivity.this,
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialogAlert.dismiss();
                                                        uid = "";
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                                    }
                                                });
                                        dialogAlert.setCancelable(false);
                                        dialogAlert.show();

                                        return;
                                    }

                                    try {
                                        if (obj.has("sale_price") && obj.has("category")) {

                                            float revenue = obj.has("revenue") ? (float) obj.getInt("revenue") : 0;
                                            int quantity = obj.has("quantity") ? obj.getInt("quantity") : 0;

                                            // Tune
                                            TuneEventItem eventItem = new TuneEventItem(hotel_name)
                                                    .withQuantity(quantity)
                                                    .withUnitPrice(obj.getInt("sale_price"))
                                                    .withRevenue(revenue)
                                                    .withAttribute1(city)
                                                    .withAttribute2(ptune)
                                                    .withAttribute3(obj.getString("category"))
                                                    .withAttribute4(coupon_name)
                                                    .withAttribute5(coupon_id);
                                            TuneWrap.Purchase(eventItem, (float) obj.getInt("sale_price"), obj.getString("bid"), is_q);

                                            FacebookWrap.logPurchasedEvent(PaymentActivity.this, obj.getString("bid"), obj.getInt("sale_price"), "activity");

                                        }
                                    } catch (Exception e) {
//                        					Log.e("HOTELNOW_LOG : error ????",e.toString());
                                    }
                                    //예약 상세 페이지
                                    if (!uid.equals("0")) {
                                        uid = "";
                                        Intent intent = new Intent(PaymentActivity.this, ReservationActivityDetailActivity.class);
                                        intent.putExtra("reservation", true);
                                        intent.putExtra("tid", bid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else { // 링크 변경
                                        uid = "";
                                        Intent intent = new Intent(PaymentActivity.this, ReservationActivityDetailActivity.class);
                                        intent.putExtra("reservation", true);
                                        intent.putExtra("user_name", un);
                                        intent.putExtra("user_phone", up);
                                        intent.putExtra("tid", bid);
                                        intent.putExtra("title", "비회원 예약조회");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                    if (!obj.has("pay_type")) {
                                        Toast.makeText(getApplicationContext(), "결제 및 예약이 완료되었습니다.", Toast.LENGTH_SHORT).show();

//                                    Util.clearSearch();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "결제를 성공했지만 예약 처리가 되지 않았습니다. 호텔 나우로 연락 부탁드립니다.", Toast.LENGTH_LONG).show();
                                    uid = "";
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                }
                            }
                        });
                    } else {
                        Api.post(CONFIG.bookingSuccessUrl, params.toString(), new Api.HttpCallback() {
                            @Override
                            public void onFailure(Response response, Exception e) {
                                Toast.makeText(PaymentActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Map<String, String> headers, String body) {
                                try {
                                    JSONObject obj = new JSONObject(body);

                                    if (!obj.getString("result").equals("success")) {

                                        dialogAlert = new DialogAlert(
                                                getString(R.string.alert_notice),
                                                obj.getString("msg"),
                                                PaymentActivity.this,
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialogAlert.dismiss();

                                                        finish();
                                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                                    }
                                                });
                                        dialogAlert.setCancelable(false);
                                        dialogAlert.show();

                                        return;
                                    }

                                    try {
                                        if (obj.has("hotel_id") && obj.has("room_id") && obj.has("sale_price") && obj.has("category")) {
                                            float revenue = obj.has("revenue") ? (float) obj.getInt("revenue") : 0;
                                            int quantity = obj.has("quantity") ? obj.getInt("quantity") : 0;

                                            // Tune
                                            TuneEventItem eventItem = new TuneEventItem(hotel_name)
                                                    .withQuantity(quantity)
                                                    .withUnitPrice(obj.getInt("sale_price"))
                                                    .withRevenue(revenue)
                                                    .withAttribute1(city)
                                                    .withAttribute2(ptune)
                                                    .withAttribute3(obj.getString("category"))
                                                    .withAttribute4(coupon_name)
                                                    .withAttribute5(coupon_id);
                                            TuneWrap.Purchase(eventItem, (float) obj.getInt("sale_price"), obj.getString("bid"), is_q);

                                            FacebookWrap.logPurchasedEvent(PaymentActivity.this, obj.getString("bid"), obj.getInt("sale_price"), "stay");
                                        }
                                    } catch (Exception e) {
//                        					Log.e("HOTELNOW_LOG : error ????",e.toString());
                                    }
                                    if (!uid.equals("0")) {
                                        uid = "";
                                        Intent intent = new Intent(PaymentActivity.this, ReservationHotelDetailActivity.class);
                                        intent.putExtra("reservation", true);
                                        intent.putExtra("bid", bid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        uid = "";
                                        Intent intent = new Intent(PaymentActivity.this, ReservationHotelDetailActivity.class);
                                        intent.putExtra("reservation", true);
                                        intent.putExtra("user_name", un);
                                        intent.putExtra("user_phone", up);
                                        intent.putExtra("bid", bid);
                                        intent.putExtra("title", "비회원 예약조회");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    if (!obj.has("pay_type")) {
                                        Toast.makeText(getApplicationContext(), "결제 및 예약이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "결제를 성공했지만 예약 처리가 되지 않았습니다. 호텔 나우로 연락 부탁드립니다.", Toast.LENGTH_LONG).show();
                                    uid = "";
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 사용자정의 WebViewClient
     */
    private class MyViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//			Toast.makeText(getApplicationContext(), "결제 서버 오류로 인해 결제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), String.valueOf(errorCode) + " / " + description, Toast.LENGTH_SHORT).show();
        }

        /**
         * URL 호출 이벤트를 처리하기 위해 오버라이딩
         */
        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);

                return true;
            }

            // ISP 인증 APP을 호출
            if (url != null && (url.contains("ispmobile://"))) {

                // ISP 인증 APP이 설치 되어 있는지 확인하고 설치 되지 않았을 경우 안드로이드 마켓으로 연결
                try {
                    getPackageManager().getPackageInfo("kvp.jjy.MispAndroid320", 0);
                } catch (PackageManager.NameNotFoundException ne) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"));
                    startActivity(intent);
                    return true;
                }

                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    return true;
                }
                // 3D 인증창에서 백신 모듈 설치 또는 호출을 위한 URL Scheme 호출시
                // 안드로이드 마켓으로 연결 또는 해당 백신 APP 호출
            } else if (url != null
                    && (url.contains("http://market.android.com") || url.contains("vguard") || url.contains("droidxantivirus")
                    || url.contains("smhyundaiansimclick://") || url.contains("smshinhancardusim://") || url.contains("smshinhancardusim://")
                    || url.contains("market://") || url.contains("v3mobile") || url.endsWith(".apk") || url.contains("ansimclick")
                    || url.contains("http://m.ahnlab.com/kr/site/download")
                    || url.contains("http://mobile.vpay.co.kr/jsp/MISP/andown.jsp")
                    || url.contains("lottesmartpay://") || url.contains("hanaansim://") || url.contains("mvaccine") || url.contains("cpy") || url.contains("kftc-bankpay://")
                    || url.contains("ispmobile") || url.contains("com.lotte.lottesmartpay") || url.contains("com.lcacApp") || url.contains("cloudpay")
                    || url.contains("pay") || url.contains("lottecard") || url.contains("kakaopay")
                    || url.contains("nh.smart.nhallonepay"))) {

                //payco 예외처리
                if (url.contains("payco.com") || url.contains("https://rsmpay.kcp.co.kr/pay/card/paycoGeneralResult.kcp")) {
                    view.loadUrl(url);
                    return false;
                }

                try {
                    Intent intent = null;
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException ex) {
                    }

                    if (url.startsWith("intent") && url.contains("com.ahnlab.v3mobileplus")) {
                        view.getContext().startActivity(Intent.parseUri(url, 0));
                    } else if (url.startsWith("intent")) { // chrome 버젼 방식
                        // 앱설치 체크를 합니다.
                        if (getPackageManager().resolveActivity(intent, 0) == null) {
                            String packagename = intent.getPackage();
                            if (packagename != null) {
                                Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                return true;
                            }
                        }

                        Uri uri = Uri.parse(intent.getDataString());
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } else {
                        Uri uri = Uri.parse(url);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                } catch (ActivityNotFoundException e) {
                    return true;
                } catch (URISyntaxException e) {
                    return true;
                }
            } else {
                view.loadUrl(url);
                return false;
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialogConfirm = new DialogConfirm(
                    getString(R.string.alert_notice),
                    "결제가 진행중입니다.\n정말로 취소하시겠습니까?",
                    getString(R.string.alert_no),
                    getString(R.string.alert_yes),
                    PaymentActivity.this,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setBookingCancel("");
                            dialogConfirm.dismiss();

                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                            dialogConfirm.dismiss();
                        }
                    });
            dialogConfirm.setCancelable(false);
            dialogConfirm.show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setBookingCancel(String msg) {
        cancelMsg = (msg == "") ? "결제를 취소하였습니다." : msg;

        JSONObject params = new JSONObject();
        try {
            params.put("bid", bid);
        } catch (JSONException e) {
        }

        Api.post(CONFIG.bookingCancelUrl, params.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(PaymentActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                Toast.makeText(getApplicationContext(), cancelMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uid = "";
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
//        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

}
