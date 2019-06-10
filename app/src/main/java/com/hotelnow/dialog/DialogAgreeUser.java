package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hotelnow.R;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.OnSingleClickListener;
import java.util.Random;

public class DialogAgreeUser extends Dialog {

    private View.OnClickListener mOkClickListener;
    private View.OnClickListener mCancelClickListener;
    private CompoundButton.OnCheckedChangeListener mCheckedListener;
    private WebView webview;
    private boolean agreed_yn;

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
        CheckBox agree_checkbox1 = (CheckBox) findViewById(R.id.agree_checkbox1);

        agree_checkbox1.setChecked(agreed_yn);

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

        mOkButton.setOnClickListener(mOkClickListener);
        mCancelButton.setOnClickListener(mCancelClickListener);
        agree_checkbox1.setOnCheckedChangeListener(mCheckedListener);
    }

    public DialogAgreeUser( Context context, View.OnClickListener ok, View.OnClickListener cancel, String agreed_yn, CompoundButton.OnCheckedChangeListener check) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.mOkClickListener = ok;
        this.mCancelClickListener = cancel;
        this.mCheckedListener = check;
        this.agreed_yn = agreed_yn.equals("Y") ? true :false;
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
