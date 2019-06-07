package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.SignupActivity;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;

import java.util.Random;

public class DialogAgreeUser extends Dialog {

    private View.OnClickListener mOkClickListener;
    private View.OnClickListener mCancelClickListener;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_agree_user);

        Random oRandom = new Random();
        int rand = oRandom.nextInt(999999) + 1;

        Button mCancelButton = (Button) findViewById(R.id.left);
        Button mOkButton = (Button) findViewById(R.id.right);
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString(webview.getSettings().getUserAgentString() + " / HOTELNOW_APP_ANDROID / " + String.valueOf(rand));
        webview.setWebViewClient(new webViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(CONFIG.setting_agree2);

        findViewById(R.id.tv_title).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(webview.getVisibility() == View.GONE) {
                    webview.setVisibility(View.VISIBLE);
                    findViewById(R.id.title_icon).setBackgroundResource(R.drawable.ico_more_close);
                } else {
                    webview.setVisibility(View.GONE);
                    findViewById(R.id.title_icon).setBackgroundResource(R.drawable.ico_more_open);
                }
            }
        });

        mOkButton.setOnClickListener(mCancelClickListener);
        mCancelButton.setOnClickListener(mOkClickListener);

    }

    public DialogAgreeUser( Context context, View.OnClickListener ok, View.OnClickListener cancel) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.mOkClickListener = ok;
        this.mCancelClickListener = cancel;
    }

    private class webViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.loadUrl("file:///android_asset/404.html");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }
}
