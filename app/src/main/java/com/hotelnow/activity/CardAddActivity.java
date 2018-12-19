package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;

/**
 * Created by susia on 16. 6. 2..
 */
public class CardAddActivity extends Activity {
    private WebView webview;
    private final Handler handler = new Handler();
    private DialogAlert dialogAlert;
    private DialogConfirm dialogConfirm;
    private String uid = "";

//    @SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Util.setStatusColor(this);

        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        uid = _preferences.getString("userid", null);

        if(uid.equals("") || uid == null) {
            Toast.makeText(CardAddActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        ((TextView) findViewById(R.id.title_text)).setText("신용카드등록");

        webview = (WebView)findViewById(R.id.webview);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString(webview.getSettings().getUserAgentString()+" HOTELNOW_APP_ANDROID");
        webview.setWebViewClient(new webViewClient());
//        webview.addJavascriptInterface(new billWebInterface(), "billWebInterface");
        webview.setWebChromeClient(new WebChromeClient());

        webview.loadUrl(CONFIG.cardAddUrl+"?uid="+uid);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class webViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.loadUrl("file:///android_asset/404.html");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains("plusfriend")){
                // 옐로우 아이디 처리
                Util.kakaoYelloId(CardAddActivity.this);

                finish();
                return true;

            } else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);

                return true;

            } else if (url.startsWith("mailto:")) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(i);

                return true;

            } else if(url.contains("www")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

                return true;
            } else {
                view.loadUrl(url);
                return false;
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

    // 404 새로고침
//    private class billWebInterface
//    {
//        @JavascriptInterface
//        public void finishAlert( final String str )
//        {
//            handler.post( new Runnable() {
//                public void run()
//                {
//
//                    dialogAlert = new DialogAlert(
//                            getString(R.string.alert_notice),
//                            (str.length() <= 0)? "카드 등록을 취소하였습니다." : str,
//                            CardAddActivity.this,
//                            new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    dialogAlert.dismiss();
//
//                                    finish();
//                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
//                                }
//                            });
//                    dialogAlert.setCancelable(false);
//                    dialogAlert.show();
//                }
//            });
//        }
//
//        @JavascriptInterface
//        public void paymentSuccess(final String id) {
//            handler.post(new Runnable() {
//                public void run() {
//                    Toast.makeText(CardAddActivity.this, "카드를 등록하였습니다.", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    setResult(91, intent);
//                    finish();
//                }
//            });
//        }
//
//        @JavascriptInterface
//        public void reoladDetail(){
//            webview.loadUrl(CONFIG.cardAddUrl);
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            dialogConfirm = new DialogConfirm(
                    getString(R.string.alert_notice),
                    "카드 등록을 취소하시겠습니까?",
                    getString(R.string.alert_no),
                    getString(R.string.alert_yes),
                    CardAddActivity.this,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
