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
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class LocationAgreeActivity extends Activity {

    private TextView tv_webgo;
    private CheckBox accept_location;
    private SharedPreferences _preferences;
    private String cookie = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agree);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        cookie = _preferences.getString("userid", null);
        tv_webgo = (TextView) findViewById(R.id.tv_webgo);
        accept_location = (CheckBox) findViewById(R.id.accept_location);
        Spannable span = Spannable.Factory.getInstance().newSpannable("위치 서비스 이용을 동의하시면 편리하게 호텔나우 앱을 이용하실 수 있어요!");
        String text = span.toString();

        int start = text.indexOf("위치 서비스 이용");
        int end = start + "위치 서비스 이용".length();

        span.setSpan(new UnderlineSpan(), 0, end, 0);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LocationAgreeActivity.this, WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree3);
                intent.putExtra("title", getString(R.string.term_txt3));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = 0xff666666;
                super.updateDrawState(ds);
            }
        }, start, end, 0);
        tv_webgo.setText(span);
        tv_webgo.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.btn_authority).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // 앱 권한 설정 나중에 필요
                Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(appDetail);
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(91);
                finish();
            }
        });

        setUserBenefit();
    }

    private void setUserBenefit(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.maketing_agree;
        String uuid = Util.getAndroidId(this);

        if(uuid != null && !TextUtils.isEmpty(uuid)){
            url += "?uuid="+uuid;
        }
        url +="&location";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                findViewById(R.id.wrapper).setVisibility(View.GONE);
                Toast.makeText(LocationAgreeActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(LocationAgreeActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    if(obj.has("location")){
                        accept_location.setChecked(obj.getJSONObject("location").getString("agreed_yn").equals("Y") ? true : false);
                    }

                    accept_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            setMaketing(isChecked);
                        }
                    });

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
            paramObj.put("location", (flag == true) ? "Y" : "N");

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
}
