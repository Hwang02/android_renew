package com.hotelnow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAgreeUser;
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
    private DialogAgreeUser dialogAgreeUser;
    private boolean Sel_check = false;

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
        // 알림 받기
        findViewById(R.id.btn_push).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingAlarmActivity.class);
                startActivity(intent);
            }
        });

        // 개인정보 수집
        findViewById(R.id.btn_agree1).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                setUserBenefit();
            }
        });

        // 위치서비스
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

    private void setUserBenefit(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.maketing_agree;
        String uuid = Util.getAndroidId(this);

        if(uuid != null && !TextUtils.isEmpty(uuid)){
            url += "?uuid="+uuid;
        }
        url +="&marketing_use";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                findViewById(R.id.wrapper).setVisibility(View.GONE);
                Toast.makeText(SettingActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(SettingActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }
                    String agreed_yn = "N";
                    if(obj.has("marketing_use")){
                        agreed_yn = obj.getJSONObject("marketing_use").getString("agreed_yn");
                    }

                    setAgreedPopup(agreed_yn);

                    findViewById(R.id.wrapper).setVisibility(View.GONE);

                } catch (Exception e) {
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

    private void setMaketing(boolean ischeck) {
        // 푸시 수신 상태값 저장
        String regId = _preferences.getString("gcm_registration_id", null);

        LogUtil.e("xxxxx", regId);
        if (regId != null) {
            setMaketingSend(this, regId, ischeck);
        }
    }

    // GCM TOKEN
    public void setMaketingSend(final Context context, String regId, final Boolean flag) {
        String androidId = Util.getAndroidId(context);

        JSONObject paramObj = new JSONObject();

        try {
            paramObj.put("os", "a");
            paramObj.put("uuid", androidId);
            paramObj.put("push_token", regId);
            paramObj.put("ver", Util.getAppVersionName(context));
            paramObj.put("marketing_use", (flag == true) ? "Y" : "N");

        } catch (JSONException e) {; }

        Api.post(CONFIG.maketing_agree_change, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {

            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception e) {
                }
            }
        });
    }

    private void setAgreedPopup(String value){
        dialogAgreeUser = new DialogAgreeUser(SettingActivity.this,
                new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        // api 호출
                        setMaketing(Sel_check);
                        dialogAgreeUser.dismiss();
                    }
                },
                new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        dialogAgreeUser.dismiss();
                    }
                }, value, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Sel_check = isChecked;
            }
        });
        dialogAgreeUser.setCancelable(false);
        dialogAgreeUser.show();
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
