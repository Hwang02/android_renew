package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.utils.OnSingleClickListener;


public class LocationAgreeActivity extends Activity {

    private TextView tv_webgo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agree);

        tv_webgo = (TextView) findViewById(R.id.tv_webgo);
        Spannable span = Spannable.Factory.getInstance().newSpannable("위치 서비스 이용을 동의하시면 편리하게 호텔나우 앱을 이용하실 수 있어요!");
        String text = span.toString();

        int start = text.indexOf("위치 서비스 이용");
        int end = start + "위치 서비스 이용".length();

        span.setSpan(new UnderlineSpan(), 0, end, 0);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = 0xff666666;
                super.updateDrawState(ds);
            }
        }, start, end, 0);
        tv_webgo.setText(span);

        findViewById(R.id.btn_authority).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // 앱 권한 설정 나중에 필요
                Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(appDetail);
            }
        });
    }
}
