package com.hotelnow.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FindDebugger;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.BottomCropImageView;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.Map;

public class ActLoading extends Activity {

    private DbOpenHelper dbHelper;
    private SharedPreferences _preferences;
    private DialogAlert dialogAlert;
    private DialogConfirm dialogConfirm;
    private String push_type = "";
    private String bid;
    private String hid;
    private String isevt;
    private int evtidx;
    private String evttag = "";
    private String sdate = null;
    private String edate = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    private BottomCropImageView image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading); 

        Util.setStatusColor(this);

        dbHelper = new DbOpenHelper(this);
        dbHelper.open();
        dbHelper.close();

        //사람 이미지
        image2 = (BottomCropImageView) findViewById(R.id.image2);
        image2.setScaleType(ImageView.ScaleType.MATRIX);

        // preference 할당
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!BuildConfig.DEBUG && existRootingFile()) {
            dialogAlert = new DialogAlert("알림", "루팅된 단말입니다. 앱을 종료 합니다.", this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAlert.dismiss();
                    finish();
                }
            });
            dialogAlert.show();
            dialogAlert.setCancelable(false);
            return;
        }

        if (!BuildConfig.DEBUG && isDebugged()) {
            dialogAlert = new DialogAlert("알림", "디버깅 탐지로 앱을 종료 합니다.", ActLoading.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAlert.dismiss();
                    finish();
                }
            });
            dialogAlert.show();
            dialogAlert.setCancelable(false);
            return;
        }

        if (BuildConfig.DEBUG) {
            if (!Util.getHashKey(this, BuildConfig.test_jobkey)) {
                dialogAlert = new DialogAlert("알림", "위변조된 앱으로 종료 합니다.", ActLoading.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogAlert.dismiss();
                        finish();
                    }
                });
                dialogAlert.show();
                dialogAlert.setCancelable(false);
                return;
            }
        } else {
            if (!Util.getHashKey(this, BuildConfig.jobkey)) {
                dialogAlert = new DialogAlert("알림", "위변조된 앱으로 종료 합니다.", ActLoading.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogAlert.dismiss();
                        finish();
                    }
                });
                dialogAlert.show();
                dialogAlert.setCancelable(false);
                return;
            }
        }

//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        Bundle params = new Bundle();
//        params.putString("PAGE", "ActLoading-Start");
//        params.putString("NAME", "HWANG");
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, params);
        checkSeverInfo();
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            AppEventsLogger.activateApp(getApplication(), getResources().getString(R.string.facebook_app_id));
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }


    }

    public boolean isDebugged() {
        LogUtil.e("ActLoading", "Checking for debuggers...");

        boolean tracer = false;
        try {
            tracer = FindDebugger.hasTracerPid();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (FindDebugger.isBeingDebugged() || tracer) {
            LogUtil.e("ActLoading", "Debugger was detected");
            return true;
        } else {
            LogUtil.e("ActLoading", "No debugger was detected.");
            return false;
        }
    }

    private boolean existRootingFile() {
        boolean found = false;
        if (!found) {
            String[] places = {"/sbin/", "/system/bin/", "/system/xbin/",
                    "/data/local/xbin/", "/data/local/bin/",
                    "/system/sd/xbin/", "/system/bin/failsafe/"};
            for (String where : places) {
                if (new File(where + "su").exists()) {
                    found = true;
                    break;
                } else if (new File(where + "lu").exists()) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    private void checkSeverInfo() {

        String url = CONFIG.loadingUrl+"?uuid="+Util.getAndroidId(ActLoading.this)+"&os=a"+"&ver="+Util.getAppVersionName(ActLoading.this)+"&push_token="+_preferences.getString("gcm_registration_id", "");
        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject data = obj.getJSONObject("msg");
                    if (data.getString("inspection").toUpperCase().equals("Y")) {
                        dialogAlert = new DialogAlert(
                                getString(R.string.alert_notice),
                                data.getString("inspection_msg"),
                                ActLoading.this,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                        dialogAlert.setCancelable(false);
                        dialogAlert.show();
                        return;
                    }

                    //지역
                    if (data.has("city")) {
                        dbHelper.deleteHotelCity();
                        for (int i = 0; i < data.getJSONArray("city").length(); i++) {
                            if (!data.getJSONArray("city").getJSONArray(i).get(1).toString().equals("all")) {
                                dbHelper.insertHotelCity(
                                        data.getJSONArray("city").getJSONArray(i).get(0).toString(),
                                        data.getJSONArray("city").getJSONArray(i).get(1).toString()
                                );
                            } else {
                                dbHelper.insertHotelCity(
                                        "최근 지역",
                                        "0"
                                );
                            }
                        }
                    }

                    //sub 지역
                    if (data.has("sub_city")) {
                        dbHelper.deleteHotelSubCity();
                        for (int i = 0; i < data.getJSONArray("sub_city").length(); i++) {
                            dbHelper.insertHotelsubCity(
                                    data.getJSONArray("sub_city").getJSONArray(i).get(0).toString(),
                                    data.getJSONArray("sub_city").getJSONArray(i).get(1).toString(),
                                    data.getJSONArray("sub_city").getJSONArray(i).get(2).toString()
                            );
                        }
                    }

                    //티켓 카테고리
                    if (data.has("q_category")) {
                        dbHelper.deleteActivityTheme();
                        for (int i = 0; i < data.getJSONArray("q_category").length(); i++) {
                            dbHelper.insertActivityTheme(
                                    data.getJSONArray("q_category").getJSONObject(i).getString("name"),
                                    data.getJSONArray("q_category").getJSONObject(i).getString("id")
                            );
                        }
                    }

                    //티켓 지역
                    if (data.has("q_city")) {
                        dbHelper.deleteActivityCity();
                        for (int i = 0; i < data.getJSONArray("q_city").length(); i++) {
                            if (!data.getJSONArray("q_city").getJSONObject(i).getString("id").equals("0")) {
                                dbHelper.insertActivityCity(
                                        data.getJSONArray("q_city").getJSONObject(i).getString("name"),
                                        data.getJSONArray("q_city").getJSONObject(i).getString("id")
                                );
                            } else {
                                dbHelper.insertActivityCity(
                                        "최근지역",
                                        "0"
                                );
                            }
                        }
                    }

                    //프라이빗딜 url
                    if (data.has("privatedeal_web_url")) {
                        CONFIG.PrivateUrl = data.getString("privatedeal_web_url");
                    }

                    // 서버타임
                    if (data.has("server_time")) {
                        long time = data.getInt("server_time") * (long) 1000;
                        CONFIG.svr_date = new Date(time);
                    }

                    if (data.has("signup_promotion")) {
                        CONFIG.sign_pro_img = data.getJSONObject("signup_promotion").getString("front_img");
                    }

                    // 적립금
                    if (data.has("savemoney")) {
                        CONFIG.default_reserve_money = data.getInt("savemoney");
                        Util.setPreferenceValues(_preferences, "savemoney", data.getInt("savemoney"));
                    } else {
                        Util.setPreferenceValues(_preferences, "savemoney", CONFIG.default_reserve_money);
                    }

                    // 미래예약 설정일
                    int fDayLimit = (data.has("flimit")) ? data.getInt("flimit") + 1 : 181;
                    CONFIG.maxDate = fDayLimit;

                    // 상품 판매 시작시간
                    if (data.has("open_time"))
                        CONFIG.open_sell_time = data.getString("open_time");

                    // 고객센터 운영시간
                    if (data.has("operation_time"))
                        CONFIG.operation_time = data.getString("operation_time");

                    // 단발성 이벤트 문구
                    if (data.has("special_text"))
                        CONFIG.special_text = data.getString("special_text");

                    // 단발성 이벤트 테마아이디
                    if (data.has("special_theme_id"))
                        CONFIG.special_theme_id = data.getString("special_theme_id");

                    //단발성 이벤트 검색 힌트문구
                    if (data.has("search_bg_text"))
                        CONFIG.search_bg_text = data.getString("search_bg_text");

                    if (data.has("signup_promotion"))
                        CONFIG.signupimgURL = data.getJSONObject("signup_promotion").getString("front_img");

                    if (data.has("special_text_q")) // 단발성 티켓 이벤트 문구
                        CONFIG.special_text_q = data.getString("special_text_q");

                    if (data.has("special_theme_id_q")) //단발성 이벤트 티켓 테마아이디
                        CONFIG.special_theme_id_q = data.getString("special_theme_id_q");

                    if (data.has("search_bg_text_q")) //단발성 이벤트 티켓 검색 힌트문구
                        CONFIG.search_bg_text_q = data.getString("search_bg_text_q");

                    if (obj.has("marketing_use")) { // 개인정보 수집 (선택)
//                        CONFIG.maketing_agree_use = obj.getJSONObject("marketing_use").getString("agreed_yn");
                        Util.setPreferenceValues(_preferences, "maketing_agree_use", obj.getJSONObject("marketing_use").getString("agreed_yn"));
                    }
                    // 업데이트 메시지
                    String updateMsg = data.has("update_msg") ? data.getString("update_msg") : "";
                    String mustUpdate = data.has("must_update") ? data.getString("must_update") : "";

                    // 버전 비교 후 마켓 가기
                    if (Double.valueOf(Util.getAppVersionName(ActLoading.this)) < data.getDouble("version")) {
                        if (mustUpdate.equals("Y")) {
                            dialogAlert = new DialogAlert(
                                    getString(R.string.alert_notice_update),
                                    getString(R.string.ask_new_version3) + updateMsg,
                                    ActLoading.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String appPackageName = getPackageName();

                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                                                startActivity(intent);
                                            } catch (ActivityNotFoundException anfe) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName));
                                                startActivity(intent);
                                            }
                                        }
                                    });
                            dialogAlert.setCancelable(false);
                            dialogAlert.show();

                        } else {
                            dialogConfirm = new DialogConfirm(
                                    getString(R.string.alert_notice_update),
                                    getString(R.string.ask_new_version3) + updateMsg,
                                    getString(R.string.alert_close),
                                    getString(R.string.alert_confrim),
                                    ActLoading.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startHandler();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String appPackageName = getPackageName();
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                                                startActivity(intent);
                                            } catch (ActivityNotFoundException anfe) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName));
                                                startActivity(intent);
                                            }
                                        }
                                    });
                            dialogConfirm.setCancelable(false);
                            dialogConfirm.show();
                        }
                    } else {

                        String uid = _preferences.getString("userid", null);
                        String uname = _preferences.getString("username", null);
                        String uemail = _preferences.getString("email", null);
                        String uphone = _preferences.getString("phone", null);

                        if (uid != null && !uid.contains("HN|")) {
                            SharedPreferences.Editor prefEditor = _preferences.edit();
                            prefEditor.putString("userid", "HN|" + AES256Chiper.AES_Encode(uid));
                            prefEditor.putString("email", "HN|" + AES256Chiper.AES_Encode(uemail));
                            prefEditor.putString("username", "HN|" + AES256Chiper.AES_Encode(uname));
                            prefEditor.putString("phone", "HN|" + AES256Chiper.AES_Encode(uphone));
                            prefEditor.commit();
                        }

                        if (uid != null) {
                            authCheck();
                        } else {
                            SharedPreferences.Editor prefEditor = _preferences.edit();
                            prefEditor.putString("email", null);
                            prefEditor.putString("username", null);
                            prefEditor.putString("phone", null);
                            prefEditor.putString("userid", null);
                            prefEditor.commit();
                            startHandler();
                        }
//                        Intent intent = new Intent(ActLoading.this, MainActivity.class);
//                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void authCheck() {
        JSONObject paramObj = new JSONObject();
        try {
//            paramObj.put("ui", AES256Chiper.AES_Decode(_preferences.getString("userid", null).replace("HN|", "")));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
            paramObj.put("ver", Util.getAppVersionName(ActLoading.this));
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (obj.getString("result").equals("0")) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("email", null);
                        prefEditor.putString("username", null);
                        prefEditor.putString("phone", null);
                        prefEditor.putString("userid", null);
                        prefEditor.commit();
                        startHandler();
                    } else {
                        getFavorite();
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFavorite() {

        String url = CONFIG.like_list;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ActLoading.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ActLoading.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbHelper.deleteFavoriteItem(true, "", "");
                    if (obj.getJSONArray("stay").length() > 0) {
                        for (int i = 0; i < obj.getJSONArray("stay").length(); i++) {
                            dbHelper.insertFavoriteItem(obj.getJSONArray("stay").getString(i), "H");
                        }
                    }

                    if (obj.getJSONArray("activity").length() > 0) {
                        for (int i = 0; i < obj.getJSONArray("activity").length(); i++) {
                            dbHelper.insertFavoriteItem(obj.getJSONArray("activity").getString(i), "A");
                        }
                    }
                    startHandler();
                } catch (Exception e) {
                    Toast.makeText(ActLoading.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startHandler() {

        if (!_preferences.getBoolean("flag_first_executed", false)) {
            Util.setPreferenceValues(_preferences, "flag_first_executed", true);
        }

        // 메인 페이지
        MovePage();

    }

    private void MovePage() {
        if (checkPlayServices()) {
            Intent intentLink = getIntent();
            push_type = intentLink.getStringExtra("push_type");
            bid = intentLink.getStringExtra("bid");
            hid = intentLink.getStringExtra("hid");
            isevt = intentLink.getStringExtra("isevt");
            evtidx = intentLink.getIntExtra("evtidx", 0);
            sdate = intentLink.getStringExtra("sdate");
            edate = intentLink.getStringExtra("edate");
            evttag = intentLink.getStringExtra("evttag");
            String action = intentLink.getAction();
            String data = intentLink.getDataString();
            Intent intent = null;
            if(_preferences.getBoolean("user_first_app", true)) {
                intent = new Intent(this, DialogFullActivity.class);
            }
            else {
                intent = new Intent(ActLoading.this, MainActivity.class);
            }
            intent.putExtra("action", action);
            intent.putExtra("data", data);
            intent.putExtra("push_type", push_type);
            intent.putExtra("bid", bid);
            intent.putExtra("hid", hid);
            intent.putExtra("isevt", isevt);
            intent.putExtra("evtidx", evtidx);
            intent.putExtra("evttag", evttag);
            intent.putExtra("sdate", sdate);
            intent.putExtra("edate", edate);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }

    }

    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            }).show();
            return false;
        }
    }

}
