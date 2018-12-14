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
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SettingActivity extends Activity{

    private CheckBox cb_email, cb_sms, cb_push;
    private TextView tv_email, tv_sms, tv_push;
    private SharedPreferences _preferences;
    private String cookie = "";
    private DialogDiscountAlert discountAlert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cookie = _preferences.getString("userid", null);

        cb_email = (CheckBox) findViewById(R.id.accept_email);
        cb_sms = (CheckBox) findViewById(R.id.accept_sms);
        cb_push = (CheckBox) findViewById(R.id.accept_push);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_sms = (TextView) findViewById(R.id.tv_sms);
        tv_push = (TextView) findViewById(R.id.tv_push);
        if(cookie == null){
            findViewById(R.id.ll_sms).setVisibility(View.GONE);
            findViewById(R.id.v_sms).setVisibility(View.GONE);
            findViewById(R.id.v_email).setVisibility(View.GONE);
            findViewById(R.id.ll_email).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.ll_sms).setVisibility(View.VISIBLE);
            findViewById(R.id.v_sms).setVisibility(View.VISIBLE);
            findViewById(R.id.v_email).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_email).setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();

        cb_push.setChecked(intent.getBooleanExtra("isPush", false));
        tv_push.setSelected(intent.getBooleanExtra("isPush", false));

        cb_email.setChecked(intent.getStringExtra("isEmail").equals("Y"));
        tv_email.setSelected(intent.getStringExtra("isEmail").equals("Y"));

        cb_sms.setChecked(intent.getStringExtra("isSms").equals("Y"));
        tv_sms.setSelected(intent.getStringExtra("isSms").equals("Y"));

        cb_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckMaketing("email", (isChecked == true)? "Y":"N");

            }
        });

        cb_sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckMaketing("sms", (isChecked == true)? "Y":"N");

            }
        });

        cb_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPush();
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

    private void setCheckMaketing(final String type, String flag){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        JSONObject paramObj = new JSONObject();

        try{
            paramObj.put("yn", flag);

        } catch (JSONException e) {
            findViewById(R.id.wrapper).setVisibility(View.GONE);
        }

        Api.post(CONFIG.maketing_check + "/" + type, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {

                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    if(obj.has("marketing_info")){
                        if(discountAlert != null && discountAlert.isShowing()){
                            discountAlert.dismiss();
                        }
                        if(obj.getJSONObject("marketing_info").getString("category").equals("email")){
                            tv_email.setSelected((obj.getJSONObject("marketing_info").getString("yn").equals("Y")));
                            if(tv_email.isSelected()){
                                discountAlert = new DialogDiscountAlert(
                                        "할인 혜택 알림 수신 동의 안내",
                                        "이메일 (동의)",
                                        "수신 동의 일시 : "+obj.getJSONObject("marketing_info").getString("datetime").substring(0, 16),
                                        "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                        "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                        SettingActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                discountAlert.dismiss();
                                            }
                                        }
                                );

                                discountAlert.show();
                            }
                            else{
                                discountAlert = new DialogDiscountAlert(
                                        "할인 혜택 알림 수신 미동의 안내",
                                        "이메일 (미동의)",
                                        "수신 동의 일시 : "+obj.getJSONObject("marketing_info").getString("datetime").substring(0, 16),
                                        "위의 내용으로 호텔나우 혜택 알림 수신에 미동의 하셨습니다.",
                                        "특가 정보, 이벤트, 할인 쿠폰 소식이 궁금하다면 언제든지 푸시 알림을 허용해주세요!",
                                        SettingActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                discountAlert.dismiss();
                                            }
                                        }
                                );

                                discountAlert.show();
                            }
                        }
                        else{
                            tv_sms.setSelected((obj.getJSONObject("marketing_info").getString("yn").equals("Y")));
                            if(tv_sms.isSelected()){
                                discountAlert = new DialogDiscountAlert(
                                        "할인 혜택 알림 수신 동의 안내",
                                        "SMS / MMS (동의)",
                                        "수신 동의 일시 : "+obj.getJSONObject("marketing_info").getString("datetime").substring(0, 16),
                                        "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                        "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                        SettingActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                discountAlert.dismiss();
                                            }
                                        }
                                );

                                discountAlert.show();
                            }
                            else{
                                discountAlert = new DialogDiscountAlert(
                                        "할인 혜택 알림 수신 미동의 안내",
                                        "SMS / MMS (미동의)",
                                        "수신 동의 일시 : "+obj.getJSONObject("marketing_info").getString("datetime").substring(0, 16),
                                        "위의 내용으로 호텔나우 혜택 알림 수신에 미동의 하셨습니다.",
                                        "특가 정보, 이벤트, 할인 쿠폰 소식이 궁금하다면 언제든지 푸시 알림을 허용해주세요!",
                                        SettingActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                discountAlert.dismiss();
                                            }
                                        }
                                );

                                discountAlert.show();
                            }
                        }
                    }
                    findViewById(R.id.wrapper).setVisibility(View.GONE);

                } catch (Exception e) {
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });

    }

    private void setPush(){
        // 푸시 수신 상태값 저장
        String regId = _preferences.getString("gcm_registration_id", null);
        String userId = _preferences.getString("userid", null);

        LogUtil.e("xxxxx", regId);
        if(regId != null)
            setGcmToken(this, regId, userId, cb_push.isChecked());
        else
            cb_push.setChecked(false);
    }

    // GCM TOKEN
    public void setGcmToken(final Context context, String regId, String userId, final Boolean flag){
        String androidId = Util.getAndroidId(context);

        JSONObject paramObj = new JSONObject();

        try{
            paramObj.put("os", "a");
            paramObj.put("uuid", androidId);
            paramObj.put("push_token", regId);
            paramObj.put("ver", Util.getAppVersionName(context));

            if(flag != null) {
                paramObj.put("use_yn", ((flag == true)? "Y":"N"));
            }
            if(userId != null) paramObj.put("user_id", userId);
        } catch (JSONException e) {}

        Api.post(CONFIG.notiSettingUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                cb_push.setChecked(false);
                tv_push.setSelected(false);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        cb_push.setChecked(false);
                        tv_push.setSelected(false);
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(obj.has("use_yn")) {
                        tv_push.setSelected((obj.getString("use_yn").equals("Y")));
                        if (cb_push.isChecked()) {
                            discountAlert = new DialogDiscountAlert(
                                    "할인 혜택 알림 수신 동의 안내",
                                    "앱 PUSH (동의)",
                                    "수신 동의 일시 : " + obj.getString("updated_at").substring(0, 16),
                                    "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                    "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                    SettingActivity.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            discountAlert.dismiss();
                                            tv_push.setSelected(true);
                                        }
                                    }
                            );

                            discountAlert.show();
                        } else {
                            discountAlert = new DialogDiscountAlert(
                                    "할인 혜택 알림 수신 미동의 안내",
                                    "앱 PUSH (미동의)",
                                    "수신 동의 일시 : " + obj.getString("updated_at").substring(0, 16),
                                    "위의 내용으로 호텔나우 혜택 알림 수신에 미동의 하셨습니다.",
                                    "특가 정보, 이벤트, 할인 쿠폰 소식이 궁금하다면 언제든지 푸시 알림을 허용해주세요!",
                                    SettingActivity.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            discountAlert.dismiss();
                                        }
                                    }
                            );

                            discountAlert.show();
                        }
                    }
                } catch (Exception e) {
                    cb_push.setChecked(false);
                    tv_push.setSelected(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(91);
        finish();
        super.onBackPressed();
    }
}
