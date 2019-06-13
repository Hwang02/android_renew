package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.activity.SignupActivity;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;

import java.util.Random;

/**
 * Created by susia on 15. 11. 26..
 */
public class DialogAgreeAll extends Dialog {
    private View.OnClickListener mOkClickListener;
    private Context mContext;
    private CheckBox all_checkbox, agree_checkbox1, agree_checkbox2, agree_checkbox3, agree_checkbox4;
    private TextView agree_text1, agree_text2, agree_text3, agree_text4;
    private ImageView agree_img1, agree_img2, agree_img3, agree_img4;
    private WebView agree_web1, agree_web2, agree_web3, agree_web4;
    private Button mOkButton;
    private boolean isuser = false;
    private boolean isOneCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_agree_all);
        TextView title = (TextView) findViewById(R.id.title);

        Spannable spannable = new SpannableString(title.getText().toString());
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);

        if(!isuser) {
            findViewById(R.id.sub_title).setVisibility(View.INVISIBLE);
        }
        else{
            findViewById(R.id.sub_title).setVisibility(View.VISIBLE);
        }
        all_checkbox = (CheckBox) findViewById(R.id.all_checkbox);
        agree_checkbox1 = (CheckBox) findViewById(R.id.agree_checkbox1);
        agree_checkbox2 = (CheckBox) findViewById(R.id.agree_checkbox2);
        agree_checkbox3 = (CheckBox) findViewById(R.id.agree_checkbox3);
        agree_checkbox4 = (CheckBox) findViewById(R.id.agree_checkbox4);
        agree_text1 = (TextView) findViewById(R.id.agree_txt1);
        agree_text2 = (TextView) findViewById(R.id.agree_txt2);
        agree_text3 = (TextView) findViewById(R.id.agree_txt3);
        agree_text4 = (TextView) findViewById(R.id.agree_txt4);
        agree_img1 = (ImageView) findViewById(R.id.agree_img1);
        agree_img2 = (ImageView) findViewById(R.id.agree_img2);
        agree_img3 = (ImageView) findViewById(R.id.agree_img3);
        agree_img4 = (ImageView) findViewById(R.id.agree_img4);

        SpannableStringBuilder builder = new SpannableStringBuilder("서비스 이용약관 동의 (필수)");
        builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.purple)), 11, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        agree_text1.setText(builder);

        builder = new SpannableStringBuilder("개인 정보 수집 이용 동의 (필수)");
        builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.purple)), 14, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        agree_text2.setText(builder);

        builder = new SpannableStringBuilder("개인 정보 수집 이용 동의 (선택)");
        builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.graytxt)), 14, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        agree_text3.setText(builder);

        builder = new SpannableStringBuilder("위치 정보 서비스 이용 약관 (선택)");
        builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.graytxt)), 16, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        agree_text4.setText(builder);

        all_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    isOneCheck = true;
                }
                all_checkbox.setChecked(isChecked);
                if(isOneCheck) {
                    agree_checkbox1.setChecked(isChecked);
                    agree_checkbox2.setChecked(isChecked);
                    agree_checkbox3.setChecked(isChecked);
                    agree_checkbox4.setChecked(isChecked);
                    isOneCheck = true;
                }
                else{
                    isOneCheck = true;
                }
            }
        });

        agree_checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    isOneCheck = isChecked;
                    all_checkbox.setChecked(isChecked);
                }
            }
        });

        agree_checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    isOneCheck = isChecked;
                    all_checkbox.setChecked(isChecked);
                }
            }
        });

        agree_checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    isOneCheck = isChecked;
                    all_checkbox.setChecked(isChecked);
                }
            }
        });

        agree_checkbox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    isOneCheck = isChecked;
                    all_checkbox.setChecked(isChecked);
                }
            }
        });

        // 수정 필요함 url
        // 서비스 이용약관 동의(필수)

        Random oRandom = new Random();
        int rand = oRandom.nextInt(999999) + 1;

        agree_web1 = (WebView) findViewById(R.id.agree_web1);
        agree_web1.getSettings().setJavaScriptEnabled(true);
        agree_web1.getSettings().setUserAgentString(agree_web1.getSettings().getUserAgentString() + " / HOTELNOW_APP_ANDROID / " + String.valueOf(rand));
        agree_web1.setWebViewClient(new webViewClient());
        agree_web1.setWebChromeClient(new WebChromeClient());
        agree_web1.loadUrl(CONFIG.setting_agree1+"?remove_tab=Y");
        agree_web1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                agree_web1.requestDisallowInterceptTouchEvent(true);
                return onTouchEvent(event);
            }
        });

        // 개인정보 취급방침 동의(필수)
        agree_web2 = (WebView) findViewById(R.id.agree_web2);
        agree_web2.getSettings().setJavaScriptEnabled(true);
        agree_web2.getSettings().setUserAgentString(agree_web2.getSettings().getUserAgentString() + " / HOTELNOW_APP_ANDROID / " + String.valueOf(rand));
        agree_web2.setWebViewClient(new webViewClient());
        agree_web2.setWebChromeClient(new WebChromeClient());
        agree_web2.loadUrl(CONFIG.setting_agree2);
        agree_web2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                agree_web2.requestDisallowInterceptTouchEvent(true);
                return onTouchEvent(event);
            }
        });

        // 개인정보 취급방침 동의(선택)
        agree_web3 = (WebView) findViewById(R.id.agree_web3);
        agree_web3.getSettings().setJavaScriptEnabled(true);
        agree_web3.getSettings().setUserAgentString(agree_web3.getSettings().getUserAgentString() + " / HOTELNOW_APP_ANDROID / " + String.valueOf(rand));
        agree_web3.setWebViewClient(new webViewClient());
        agree_web3.setWebChromeClient(new WebChromeClient());
        agree_web3.loadUrl(CONFIG.setting_agree2);
        agree_web3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                agree_web3.requestDisallowInterceptTouchEvent(true);
                return onTouchEvent(event);
            }
        });

        // 위치기반서비스 이용약관 동의
        agree_web4 = (WebView) findViewById(R.id.agree_web4);
        agree_web4.getSettings().setJavaScriptEnabled(true);
        agree_web4.getSettings().setUserAgentString(agree_web4.getSettings().getUserAgentString() + " / HOTELNOW_APP_ANDROID / " + String.valueOf(rand));
        agree_web4.setWebViewClient(new webViewClient());
        agree_web4.setWebChromeClient(new WebChromeClient());
        agree_web4.loadUrl(CONFIG.setting_agree3);
        agree_web4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                agree_web4.requestDisallowInterceptTouchEvent(true);
                return onTouchEvent(event);
            }
        });

        findViewById(R.id.agree_layout1).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(findViewById(R.id.layout_agree1).getVisibility() == View.GONE) {
                    setWebviewClose();
                    findViewById(R.id.layout_agree1).setVisibility(View.VISIBLE);
                    agree_img1.setBackgroundResource(R.drawable.ico_viewmore_close);
                }
                else{
                    findViewById(R.id.layout_agree1).setVisibility(View.GONE);
                    agree_img1.setBackgroundResource(R.drawable.ico_viewmore_open);
                }
            }
        });
        findViewById(R.id.agree_layout2).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(findViewById(R.id.layout_agree2).getVisibility() == View.GONE) {
                    setWebviewClose();
                    findViewById(R.id.layout_agree2).setVisibility(View.VISIBLE);
                    agree_img2.setBackgroundResource(R.drawable.ico_viewmore_close);
                }
                else{
                    findViewById(R.id.layout_agree2).setVisibility(View.GONE);
                    agree_img2.setBackgroundResource(R.drawable.ico_viewmore_open);
                }
            }
        });
        findViewById(R.id.agree_layout3).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(findViewById(R.id.layout_agree3).getVisibility() == View.GONE) {
                    setWebviewClose();
                    findViewById(R.id.layout_agree3).setVisibility(View.VISIBLE);
                    agree_img3.setBackgroundResource(R.drawable.ico_viewmore_close);
                }
                else{
                    findViewById(R.id.layout_agree3).setVisibility(View.GONE);
                    agree_img3.setBackgroundResource(R.drawable.ico_viewmore_open);
                }
            }
        });
        findViewById(R.id.agree_layout4).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(findViewById(R.id.layout_agree4).getVisibility() == View.GONE) {
                    setWebviewClose();
                    findViewById(R.id.layout_agree4).setVisibility(View.VISIBLE);
                    agree_img4.setBackgroundResource(R.drawable.ico_viewmore_close);
                }
                else{
                    findViewById(R.id.layout_agree4).setVisibility(View.GONE);
                    agree_img4.setBackgroundResource(R.drawable.ico_viewmore_open);
                }
            }
        });

        agree_checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selCheck();
            }
        });
        agree_checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selCheck();
            }
        });


        mOkButton = (Button) findViewById(R.id.ok);
        mOkButton.setOnClickListener(mOkClickListener);
    }

    private void selCheck(){
        mOkButton.setBackgroundResource(R.drawable.gray_round);
        if(agree_checkbox1.isChecked() && agree_checkbox2.isChecked()){
            mOkButton.setBackgroundResource(R.drawable.purple_2round);
        }
    }

    private void setWebviewClose(){
        findViewById(R.id.layout_agree1).setVisibility(View.GONE);
        agree_img1.setBackgroundResource(R.drawable.ico_viewmore_open);
        findViewById(R.id.layout_agree2).setVisibility(View.GONE);
        agree_img2.setBackgroundResource(R.drawable.ico_viewmore_open);
        findViewById(R.id.layout_agree3).setVisibility(View.GONE);
        agree_img3.setBackgroundResource(R.drawable.ico_viewmore_open);
        findViewById(R.id.layout_agree4).setVisibility(View.GONE);
        agree_img4.setBackgroundResource(R.drawable.ico_viewmore_open);
    }

    public DialogAgreeAll(Context context, View.OnClickListener ok, boolean isuser) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        this.mOkClickListener = ok;
        this.isuser = isuser;
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
