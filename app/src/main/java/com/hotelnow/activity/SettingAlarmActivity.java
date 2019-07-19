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
import com.hotelnow.dialog.DialogAgreeUser;
import com.hotelnow.dialog.DialogAlert;
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
    private final int val_p=1,val_m=2, val_e=3, val_market=4;
    private String val_marketing="";
    private DialogAgreeUser dialogAgreeUser;
    private boolean Sel_check = false;
    private DialogAlert dialogAlert;

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

        setUserBenefit(false);

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
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);
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

    private void setUserBenefit(final boolean isMarketing){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.maketing_agree;
        String uuid = Util.getAndroidId(this);

        if(uuid != null && !TextUtils.isEmpty(uuid)){
            url += "?uuid="+uuid;
        }
        url +="&marketing_receive_push&marketing_receive_sms&marketing_receive_email&marketing_use";

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
                    if(!isMarketing) {
                        if (obj.has("marketing_receive")) {
                            // push
                            if (obj.getJSONObject("marketing_receive").has("push")) {

//                            if(!NotificationManagerCompat.from(SettingAlarmActivity.this).areNotificationsEnabled()){
//                                cb_push.setChecked(false);
//                                cb_push.setEnabled(false);
//                            }
//                            else {
                                cb_push.setEnabled(true);
                                if (obj.getJSONObject("marketing_receive").getJSONObject("push").getString("agreed_yn").equals("Y")) {
                                    cb_push.setChecked(true);
                                } else {
                                    cb_push.setChecked(false);
                                }
//                            }
                            }

                            // push

                            // sms
                            if (obj.getJSONObject("marketing_receive").has("sms")) {
                                if (cookie == null) {
                                    cb_sms.setChecked(false);
                                    tv_sms.setSelected(false);
                                } else if (obj.getJSONObject("marketing_receive").getJSONObject("sms").getString("agreed_yn").equals("Y")) {
                                    cb_sms.setChecked(true);
                                    tv_sms.setSelected(true);
                                } else {
                                    cb_sms.setChecked(false);
                                    tv_sms.setSelected(true);
                                }
                            }
                            // sms

                            // email
                            if (obj.getJSONObject("marketing_receive").has("email")) {
                                if (cookie == null) {
                                    cb_email.setChecked(false);
                                    tv_email.setSelected(false);
                                } else if (obj.getJSONObject("marketing_receive").getJSONObject("email").getString("agreed_yn").equals("Y")) {
                                    cb_email.setChecked(true);
                                    tv_email.setSelected(true);
                                } else {
                                    cb_email.setChecked(false);
                                    tv_email.setSelected(true);

                                }
                            }
                            // email

                            // marketing_use
                            if (obj.has("marketing_use")) {
                                val_marketing = obj.getJSONObject("marketing_use").getString("agreed_yn");
                            }
                        }

                        cb_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked && !NotificationManagerCompat.from(SettingAlarmActivity.this).areNotificationsEnabled()){
                                    dialogAlert = new DialogAlert("알림", "앱의 알림이 차단된 상태입니다.\n사용중으로 설정하러가기", SettingAlarmActivity.this, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                            intent.putExtra("app_package", getPackageName());
                                            intent.putExtra("app_uid", getApplicationInfo().uid);
                                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                            startActivityForResult(intent, 100);
                                            dialogAlert.dismiss();
                                        }
                                    });
                                    dialogAlert.show();
                                    cb_push.setChecked(false);
                                }
                                else {
                                    setMaketing(val_p, cb_push.isChecked());
                                }
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
                    }
                    else{
                        if (obj.has("marketing_receive")) {
                            // marketing_use
                            if (obj.has("marketing_use")) {
                                val_marketing = obj.getJSONObject("marketing_use").getString("agreed_yn");
                                if(val_marketing.equals("N")) {
                                    setAgreedPopup(val_marketing);
                                }
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

    private void setMaketing(int type, boolean ischeck) {
        // 푸시 수신 상태값 저장
        String regId = _preferences.getString("gcm_registration_id", null);

        if (regId != null) {
            LogUtil.e("xxxxx", regId);
            setMaketingSend(this, regId, ischeck, type);
        }else {
            setMaketingSend(this, "", false, type);
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

            if(type == val_p) {
                paramObj.put("marketing_receive_push", ((flag == true) ? "Y" : "N"));
            }
            else if(type == val_e){
                paramObj.put("marketing_receive_email", ((flag == true) ? "Y" : "N"));
            }
            else if(type == val_m){
                paramObj.put("marketing_receive_sms", ((flag == true) ? "Y" : "N"));
            }
            else {
                paramObj.put("marketing_use", (flag == true) ? "Y" : "N");
            }

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
                                            "Push (동의)",
                                            "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("push").getString("agreed_at").substring(0, 16),
                                            "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                            "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                            SettingAlarmActivity.this,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    discountAlert.dismiss();
                                                    if(val_marketing.equals("N")){
                                                        setUserBenefit(true);
                                                    }
                                                }
                                            }
                                    );

                                } else {
                                    discountAlert = new DialogDiscountAlert(
                                            "할인 혜택 알림 수신 미동의 안내",
                                            "Push (미동의)",
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
                                        "E-mail (동의)",
                                        "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("email").getString("agreed_at").substring(0, 16),
                                        "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                        "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                        SettingAlarmActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                discountAlert.dismiss();
                                                if(val_marketing.equals("N")){
                                                    setUserBenefit(true);
                                                }
                                            }
                                        }
                                    );
                                } else {
                                    discountAlert = new DialogDiscountAlert(
                                        "할인 혜택 알림 수신 미동의 안내",
                                        "E-mail (미동의)",
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
                        else if(type == val_m){
                            if (obj.getJSONObject("marketing_receive").has("sms")) {
                                if (cb_sms.isChecked() && obj.getJSONObject("marketing_receive").getJSONObject("sms").getString("agreed_yn").equals("Y")) {
                                    discountAlert = new DialogDiscountAlert(
                                            "할인 혜택 알림 수신 동의 안내",
                                            "SMS (동의)",
                                            "수신 동의 일시 : " + obj.getJSONObject("marketing_receive").getJSONObject("sms").getString("agreed_at").substring(0, 16),
                                            "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                            "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                            SettingAlarmActivity.this,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    discountAlert.dismiss();
                                                    if(val_marketing.equals("N")){
                                                        setUserBenefit(true);
                                                    }
                                                }
                                            }
                                    );
                                } else {
                                    discountAlert = new DialogDiscountAlert(
                                            "할인 혜택 알림 수신 미동의 안내",
                                            "SMS (미동의)",
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

    private void setAgreedPopup(String value){
        dialogAgreeUser = new DialogAgreeUser(SettingAlarmActivity.this,
                new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        // api 호출
                        setMaketing(val_market, Sel_check);
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
        }, cookie == null ? false : true);
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

//        if(requestCode == 100)
//        {
//            if(!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
//                cb_push.setChecked(false);
//                cb_push.setEnabled(false);
//            }
//            else{
//                cb_push.setEnabled(true);
//            }
//        }
    }
}
