package com.hotelnow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogDiscountAlert;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class SettingActivity extends Activity {

    private SharedPreferences _preferences;
    private String cookie = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cookie = _preferences.getString("userid", null);

        if(cookie == null){
            findViewById(R.id.btn_retire).setVisibility(View.GONE);
            findViewById(R.id.retire_line).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.btn_retire).setVisibility(View.VISIBLE);
            findViewById(R.id.retire_line).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_push).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingAlarmActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_agree2).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LocationAgreeActivity.class);
                startActivity(intent);
            }
        });

        // 탈퇴하기
        findViewById(R.id.btn_retire).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(SettingActivity.this, PwCheckActivity.class);
                startActivityForResult(intent, 999);
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(91);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(91);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 999)
        {
            if(resultCode == 888){
                finish();
            }
            else if(resultCode == 999){
                setResult(999);
                finish();
            }
        }
    }
}
