package com.hotelnow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAgreeUser;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class SettingActivity extends Activity {

    private SharedPreferences _preferences;
    private String cookie = "";
    private DialogAgreeUser dialogAgreeUser;
    private boolean Sel_check = false;
    private ImageView my_banner;
    private DialogAlert dialogAlert;
    private String url_link = "";
    private TextView tv_alarm_setting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cookie = _preferences.getString("userid", null);
        my_banner = (ImageView) findViewById(R.id.my_banner);
        tv_alarm_setting = (TextView) findViewById(R.id.tv_alarm_setting);
        if(cookie == null){
            tv_alarm_setting.setText("할인 혜택 알림 받기(Push)");
            findViewById(R.id.btn_retire).setVisibility(View.GONE);
            findViewById(R.id.retire_line).setVisibility(View.GONE);
        }
        else{
            tv_alarm_setting.setText("할인 혜택 알림 받기(SMS,E-mail,Push)");
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
                setUserBenefit(true);
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

        setUserBenefit(false);
    }

    private void setUserBenefit(final boolean flag){
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

                    if(flag) {
                        setAgreedPopup(agreed_yn);
                    }else{
                        if(obj.has("event_banners")){
                            findViewById(R.id.layout_banner).setVisibility(View.VISIBLE);
                            Ion.with(my_banner).load(obj.getJSONArray("event_banners").getJSONObject(0).getString("image"));
                            final String id = obj.getJSONArray("event_banners").getJSONObject(0).getString("event_id");
                            final String evt_type = obj.getJSONArray("event_banners").getJSONObject(0).getString("evt_type");
                            final String link = obj.getJSONArray("event_banners").getJSONObject(0).getString("link");
                            final String title = obj.getJSONArray("event_banners").getJSONObject(0).getString("title");
                            findViewById(R.id.layout_banner).setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    setEventCheck(id, evt_type, link, title);
                                }
                            });
                        }
                        else{
                            findViewById(R.id.layout_banner).setVisibility(View.GONE);
                        }
                    }
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
        }, cookie == null ? false : true);
        dialogAgreeUser.setCancelable(false);
        dialogAgreeUser.show();
    }

    private void setEventCheck(String id, String type, String link, String title){

        String[] arr = link.split("hotelnowevent://");
        String frontMethod = "";
        String frontTitle = "";
        String frontEvtId = "";
        String method = "";

        if (arr.length > 1) {
            frontMethod = arr[1];
            frontMethod = Util.stringToHTMLString(frontMethod);
            frontTitle = title != "" ? title : "무료 숙박 이벤트";
        }
        if (!type.equals("a")) {
            frontEvtId = id;
        } else {
            frontEvtId = Util.getFrontThemeId(link);
        }

        if (type.equals("a") && !type.equals("")) {
            try {
                JSONObject obj = new JSONObject(frontMethod);
                method = obj.getString("method");
                if(obj.has("param")) {
                    url_link = obj.getString("param");
                }

                if (method.equals("move_near")) {
                    int fDayLimit = _preferences.getInt("future_day_limit", 180);
                    String checkurl = CONFIG.checkinDateUrl + "/" + url_link + "/" + fDayLimit;

                    Api.get(checkurl, new Api.HttpCallback() {
                        @Override
                        public void onFailure(Response response, Exception e) {
                            Toast.makeText(SettingActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        @Override
                        public void onSuccess(Map<String, String> headers, String body) {
                            try {
                                JSONObject obj = new JSONObject(body);
                                JSONArray aobj = obj.getJSONArray("data");

                                if (aobj.length() == 0) {
                                    dialogAlert = new DialogAlert(
                                            getString(R.string.alert_notice),
                                            "해당 숙소는 현재 예약 가능한 객실이 없습니다.",
                                            SettingActivity.this,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialogAlert.dismiss();
                                                }
                                            });
                                    dialogAlert.setCancelable(false);
                                    dialogAlert.show();
                                    return;
                                }

                                String checkin = aobj.getString(0);
                                String checkout = Util.getNextDateStr(checkin);

                                Intent intent = new Intent(SettingActivity.this, DetailHotelActivity.class);
                                intent.putExtra("hid", url_link);
                                intent.putExtra("evt", "N");
                                intent.putExtra("sdate", checkin);
                                intent.putExtra("edate", checkout);

                                startActivity(intent);

                            } catch (Exception e) {
                                // Log.e(CONFIG.TAG, e.toString());
                                Toast.makeText(SettingActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }
                    });
                } else if (method.equals("move_theme")) {
                    Intent intent = new Intent(SettingActivity.this, ThemeSpecialHotelActivity.class);
                    intent.putExtra("tid", url_link);

                    startActivity(intent);

                } else if (method.equals("move_theme_ticket")) {
                    Intent intent = new Intent(SettingActivity.this, ThemeSpecialActivityActivity.class);
                    intent.putExtra("tid", url_link);

                    startActivity(intent);

                } else if (method.equals("move_ticket_detail")) {
                    Intent intent = new Intent(SettingActivity.this, DetailActivityActivity.class);
                    intent.putExtra("tid", url_link);

                    startActivity(intent);

                } else if (method.equals("outer_link")) {
                    if (url_link.contains("hotelnow")) {
                        frontTitle = title != "" ? title : "무료 숙박 이벤트";
                        Intent intent = new Intent(SettingActivity.this, WebviewActivity.class);
                        intent.putExtra("url", url_link);
                        intent.putExtra("title", frontTitle);
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_link));
                        startActivity(intent);
                    }
                } else if (method.equals("move_privatedeal_all")){
                    Intent intent = new Intent(SettingActivity.this, PrivateDaelAllActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
            }
        } else {
            frontTitle = title != "" ? title : "무료 숙박 이벤트";
            Intent intentEvt = new Intent(SettingActivity.this, EventActivity.class);
            intentEvt.putExtra("idx", Integer.valueOf(frontEvtId));
            intentEvt.putExtra("title", frontTitle);
            startActivity(intentEvt);
        }
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
