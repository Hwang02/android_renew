package com.hotelnow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SettingAlarmActivity extends Activity {

    private CheckBox cb_email, cb_sms, cb_push;
    private TextView tv_email, tv_sms, tv_push;
    private SharedPreferences _preferences;
    private String cookie = "";
    private DialogDiscountAlert discountAlert;
    private final int val_p=1,val_m=2, val_e=3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cookie = _preferences.getString("userid", null);

        cb_email = (CheckBox) findViewById(R.id.accept_email);
        cb_sms = (CheckBox) findViewById(R.id.accept_sms);
        cb_push = (CheckBox) findViewById(R.id.accept_push);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_sms = (TextView) findViewById(R.id.tv_sms);
        tv_push = (TextView) findViewById(R.id.tv_push);
        tv_push.setSelected(true);

        setUserBenefit();

//        findViewById(R.id.ll_sms).setVisibility(View.GONE);
//        findViewById(R.id.v_sms).setVisibility(View.GONE);
//        findViewById(R.id.v_email).setVisibility(View.GONE);
//        findViewById(R.id.ll_email).setVisibility(View.GONE);

//        Intent intent = getIntent();
//
//        cb_push.setChecked(intent.getBooleanExtra("isPush", false));
//        tv_push.setSelected(intent.getBooleanExtra("isPush", false));
//
//        cb_email.setChecked(intent.getStringExtra("isEmail").equals("Y"));
//        tv_email.setSelected(intent.getStringExtra("isEmail").equals("Y"));
//
//        cb_sms.setChecked(intent.getStringExtra("isSms").equals("Y"));
//        tv_sms.setSelected(intent.getStringExtra("isSms").equals("Y"));

        if(cookie == null) {
            cb_sms.setEnabled(false);
            cb_email.setEnabled(false);
            tv_sms.setSelected(false);
            tv_email.setSelected(false);
        }
        else {
            cb_sms.setEnabled(true);
            cb_email.setEnabled(true);
            tv_sms.setSelected(true);
            tv_email.setSelected(true);
        }

        findViewById(R.id.btn_push).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivityForResult(intent, 100);

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
        url +="&types[]=marketing_receive";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                findViewById(R.id.wrapper).setVisibility(View.GONE);
                Toast.makeText(SettingAlarmActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(SettingAlarmActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    if(obj.has("marketing_receive")){
                        // push
                        if(obj.getJSONObject("marketing_receive").has("push")){

                            if(!NotificationManagerCompat.from(SettingAlarmActivity.this).areNotificationsEnabled()){
                                cb_push.setChecked(false);
                                cb_push.setEnabled(false);
                            }
                            else {
                                cb_push.setEnabled(true);
                                if(obj.getJSONObject("marketing_receive").getJSONObject("push").getString("agreed_yn").equals("Y")) {
                                    cb_push.setChecked(true);
                                }
                                else{
                                    cb_push.setChecked(false);
                                }
                            }
                        }

                        // push

                        // sms
                        if(obj.getJSONObject("marketing_receive").has("sms")){
                            if(obj.getJSONObject("marketing_receive").getJSONObject("sms").getString("agreed_yn").equals("Y")) {
                                cb_sms.setChecked(true);
                                tv_sms.setSelected(true);
                            }
                            else{
                                cb_sms.setChecked(false);
                                tv_sms.setSelected(true);
                            }
                        }
                        // sms

                        // email
                        if(obj.getJSONObject("marketing_receive").has("email")){
                            if(obj.getJSONObject("marketing_receive").getJSONObject("email").getString("agreed_yn").equals("Y")) {
                                cb_email.setChecked(true);
                                tv_email.setSelected(true);
                            }
                            else{
                                cb_email.setChecked(false);
                                tv_email.setSelected(true);
                            }
                        }
                        // email
                    }

                    cb_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            setMaketing(val_p, cb_push.isChecked());
                        }
                    });

                    cb_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            setMaketing(val_e, isChecked);
                        }
                    });

                    cb_sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            setMaketing(val_m, isChecked);
                        }
                    });

                    findViewById(R.id.wrapper).setVisibility(View.GONE);

                } catch (Exception e) {
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

//    private void setCheckMaketing(final String type, String flag) {
//        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
//        JSONObject paramObj = new JSONObject();
//
//        try {
//            paramObj.put("yn", flag);
//
//        } catch (JSONException e) {
//            findViewById(R.id.wrapper).setVisibility(View.GONE);
//        }
//
//        Api.post(CONFIG.maketing_check + "/" + type, paramObj.toString(), new Api.HttpCallback() {
//            @Override
//            public void onFailure(Response response, Exception throwable) {
//                findViewById(R.id.wrapper).setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onSuccess(Map<String, String> headers, String body) {
//
//                try {
//                    JSONObject obj = new JSONObject(body);
//
//                    if (!obj.getString("result").equals("success")) {
//                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
//                        findViewById(R.id.wrapper).setVisibility(View.GONE);
//                        return;
//                    }
//
//                    if (obj.has("marketing_info")) {
//                        if (discountAlert != null && discountAlert.isShowing()) {
//                            discountAlert.dismiss();
//                        }
//                        if (obj.getJSONObject("marketing_info").getString("category").equals("email")) {
//                            tv_email.setSelected((obj.getJSONObject("marketing_info").getString("yn").equals("Y")));
//                            if (tv_email.isSelected()) {
//                                discountAlert = new DialogDiscountAlert(
//                                        "할인 혜택 알림 수신 동의 안내",
//                                        "이메일 (동의)",
//                                        "수신 동의 일시 : " + obj.getJSONObject("marketing_info").getString("datetime").substring(0, 16),
//                                        "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
//                                        "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
//                                        SettingAlarmActivity.this,
//                                        new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                discountAlert.dismiss();
//                                            }
//                                        }
//                                );
//
//                                discountAlert.show();
//                            } else {
//                                discountAlert = new DialogDiscountAlert(
//                                        "할인 혜택 알림 수신 미동의 안내",
//                                        "이메일 (미동의)",
//                                        "수신 동의 일시 : " + obj.getJSONObject("marketing_info").getString("datetime").substring(0, 16),
//                                        "위의 내용으로 호텔나우 혜택 알림 수신에 미동의 하셨습니다.",
//                                        "특가 정보, 이벤트, 할인 쿠폰 소식이 궁금하다면 언제든지 푸시 알림을 허용해주세요!",
//                                        SettingAlarmActivity.this,
//                                        new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                discountAlert.dismiss();
//                                            }
//                                        }
//                                );
//
//                                discountAlert.show();
//                            }
//                        } else {
//                            tv_sms.setSelected((obj.getJSONObject("marketing_info").getString("yn").equals("Y")));
//                            if (tv_sms.isSelected()) {
//                                discountAlert = new DialogDiscountAlert(
//                                        "할인 혜택 알림 수신 동의 안내",
//                                        "SMS / MMS (동의)",
//                                        "수신 동의 일시 : " + obj.getJSONObject("marketing_info").getString("datetime").substring(0, 16),
//                                        "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
//                                        "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
//                                        SettingAlarmActivity.this,
//                                        new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                discountAlert.dismiss();
//                                            }
//                                        }
//                                );
//
//                                discountAlert.show();
//                            } else {
//                                discountAlert = new DialogDiscountAlert(
//                                        "할인 혜택 알림 수신 미동의 안내",
//                                        "SMS / MMS (미동의)",
//                                        "수신 동의 일시 : " + obj.getJSONObject("marketing_info").getString("datetime").substring(0, 16),
//                                        "위의 내용으로 호텔나우 혜택 알림 수신에 미동의 하셨습니다.",
//                                        "특가 정보, 이벤트, 할인 쿠폰 소식이 궁금하다면 언제든지 푸시 알림을 허용해주세요!",
//                                        SettingAlarmActivity.this,
//                                        new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                discountAlert.dismiss();
//                                            }
//                                        }
//                                );
//
//                                discountAlert.show();
//                            }
//                        }
//                    }
//                    findViewById(R.id.wrapper).setVisibility(View.GONE);
//
//                } catch (Exception e) {
//                    findViewById(R.id.wrapper).setVisibility(View.GONE);
//                }
//            }
//        });
//
//    }

    private void setMaketing(int type, boolean ischeck) {
        // 푸시 수신 상태값 저장
        String regId = _preferences.getString("gcm_registration_id", null);

        LogUtil.e("xxxxx", regId);
        if (regId != null) {
            setMaketingSend(this, regId, ischeck, type);
        }else {
            cb_push.setChecked(false);
        }
    }

    // GCM TOKEN
    public void setMaketingSend(final Context context, String regId, final Boolean flag, final int type) {
        String androidId = Util.getAndroidId(context);

        JSONObject paramObj = new JSONObject();

        try {
            paramObj.put("os", "a");
            paramObj.put("uuid", androidId);
            paramObj.put("push_token", regId);
            paramObj.put("ver", Util.getAppVersionName(context));
            JSONObject obj=new JSONObject();
            JSONObject obj2=new JSONObject();
            if(type == val_p) {
                obj.put("marketing_receive", obj2.put("push", ((flag == true) ? "Y" : "N")));
            }
            else if(type == val_e){
                obj.put("marketing_receive", obj2.put("email", ((flag == true) ? "Y" : "N")));
            }
            else{
                obj.put("marketing_receive", obj2.put("sms", ((flag == true) ? "Y" : "N")));
            }

            paramObj.put("types", obj);

        } catch (JSONException e) {; }

        Api.post(CONFIG.maketing_agree_change, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                cb_push.setChecked(false);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        cb_push.setChecked(false);
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (obj.has("marketing_receive")) {
                        if(type == val_p) {
                            if (obj.getJSONObject("marketing_receive").has("push")) {
                                if (cb_push.isChecked() && obj.getJSONObject("marketing_receive").getJSONObject("push").getString("agreed_yn").equals("Y")) {
                                    discountAlert = new DialogDiscountAlert(
                                            "할인 혜택 알림 수신 동의 안내",
                                            "앱 PUSH (동의)",
                                            "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("push").getString("agreed_at").substring(0, 16),
                                            "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                            "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                            SettingAlarmActivity.this,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    discountAlert.dismiss();
                                                }
                                            }
                                    );


                                } else {
                                    discountAlert = new DialogDiscountAlert(
                                            "할인 혜택 알림 수신 미동의 안내",
                                            "앱 PUSH (미동의)",
                                            "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("push").getString("agreed_at").substring(0, 16),
                                            "위의 내용으로 호텔나우 혜택 알림 수신에 미동의 하셨습니다.",
                                            "특가 정보, 이벤트, 할인 쿠폰 소식이 궁금하다면 언제든지 푸시 알림을 허용해주세요!",
                                            SettingAlarmActivity.this,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    discountAlert.dismiss();
                                                }
                                            }
                                    );
                                }
                                discountAlert.show();
                            }
                        }
                        else if(type == val_e){
                            if (obj.getJSONObject("marketing_receive").has("email")) {
                                if (cb_email.isChecked() && obj.getJSONObject("marketing_receive").getJSONObject("email").getString("agreed_yn").equals("Y")) {
                                    discountAlert = new DialogDiscountAlert(
                                        "할인 혜택 알림 수신 동의 안내",
                                        "이메일 (동의)",
                                        "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("email").getString("agreed_at").substring(0, 16),
                                        "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                        "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                        SettingAlarmActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                discountAlert.dismiss();
                                            }
                                        }
                                    );
                                } else {
                                    discountAlert = new DialogDiscountAlert(
                                        "할인 혜택 알림 수신 미동의 안내",
                                        "이메일 (미동의)",
                                        "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("email").getString("agreed_at").substring(0, 16),
                                        "위의 내용으로 호텔나우 혜택 알림 수신에 미동의 하셨습니다.",
                                        "특가 정보, 이벤트, 할인 쿠폰 소식이 궁금하다면 언제든지 푸시 알림을 허용해주세요!",
                                        SettingAlarmActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                discountAlert.dismiss();
                                            }
                                        }
                                    );
                                }
                                discountAlert.show();
                            }
                        }
                        else{
                            if (obj.getJSONObject("marketing_receive").has("sms")) {
                                if (cb_sms.isChecked() && obj.getJSONObject("marketing_receive").getJSONObject("sms").getString("agreed_yn").equals("Y")) {
                                    discountAlert = new DialogDiscountAlert(
                                            "할인 혜택 알림 수신 동의 안내",
                                            "SMS / MMS (동의)",
                                            "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("sms").getString("agreed_at").substring(0, 16),
                                            "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                            "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                            SettingAlarmActivity.this,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    discountAlert.dismiss();
                                                }
                                            }
                                    );
                                } else {
                                    discountAlert = new DialogDiscountAlert(
                                            "할인 혜택 알림 수신 미동의 안내",
                                            "SMS / MMS (미동의)",
                                            "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("sms").getString("agreed_at").substring(0, 16),
                                            "위의 내용으로 호텔나우 혜택 알림 수신에 미동의 하셨습니다.",
                                            "특가 정보, 이벤트, 할인 쿠폰 소식이 궁금하다면 언제든지 푸시 알림을 허용해주세요!",
                                            SettingAlarmActivity.this,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    discountAlert.dismiss();
                                                }
                                            }
                                    );
                                }
                                discountAlert.show();
                            }
                        }
                    }
                } catch (Exception e) {
                    cb_push.setChecked(false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100)
        {
            if(!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                cb_push.setChecked(false);
            }
        }
    }
}
