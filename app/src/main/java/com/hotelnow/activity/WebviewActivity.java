package com.hotelnow.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.utils.Util;

import java.util.Random;

/**
 * Created by susia on 15. 12. 7..
 */
public class WebviewActivity extends AppCompatActivity {
    private WebView webview;
    private String linkUrl;
    private String linkTitle;
    private boolean reservation;
    private final Handler handler = new Handler();
    private TextView title_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Util.setStatusColor(this);

        webview = (WebView)findViewById(R.id.webview);
        title_text = (TextView) findViewById(R.id.title_text);

        Intent intent = getIntent();
        if(intent != null){
            linkUrl = intent.getStringExtra("url");
            linkTitle = intent.getStringExtra("title");
            reservation = intent.getBooleanExtra("reservation", false);
        }

        Random oRandom = new Random();
        int rand = oRandom.nextInt(999999) + 1;

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString(webview.getSettings().getUserAgentString() + " / HOTELNOW_APP_ANDROID / " + String.valueOf(rand));
        webview.setWebViewClient(new webViewClient());
        webview.addJavascriptInterface(new DetailInterface(), "DetailInterface");
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(linkUrl);

        title_text.setText(linkTitle);

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
                Util.kakaoYelloId(WebviewActivity.this);
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

            } else if(url.contains("www.hotelnow.co.kr")){
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
    private class DetailInterface
    {
        @JavascriptInterface
        public void reoladDetail(){
//            handler.post(new Runnable() {
//                public void run() {
                    webview.loadUrl(linkUrl);
//                }
//            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
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
