package com.hotelnow.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hotelnow.R;
import com.hotelnow.fragment.model.TicketSelEntry;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class LoginActivity extends Activity {
    private SessionCallback callback = new SessionCallback();
    private CallbackManager callbackManager;
    private Session mKakaoSession;
    private EditText email, passwd;
    private String pid = "", page = "";
    private LinearLayout btn_facebook, btn_kakao;
    private String ec_date;
    private String ee_date;
    private String tname;
    private boolean isHotel;
    private ProgressDialog dialog;
    private SharedPreferences _preferences;
    private TextView btn_nocookie;
    private DbOpenHelper dbHelper;
    private ArrayList<TicketSelEntry> sel_items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);

        Util.setStatusColor(this);

        dbHelper = new DbOpenHelper(this);

        mKakaoSession = Session.getCurrentSession();
        mKakaoSession.addCallback(callback);

        email = (EditText) findViewById(R.id.email);
        passwd = (EditText) findViewById(R.id.password);
        TextView btn_nocookie = (TextView) findViewById(R.id.btn_nocookie);

        Intent intent = getIntent();
        pid = intent.getStringExtra("pid") != null ? intent.getStringExtra("pid") : "";
        page = intent.getStringExtra("page") != null ? intent.getStringExtra("page") : "";


        btn_facebook = (LinearLayout) findViewById(R.id.btn_facebook);
        btn_kakao = (LinearLayout) findViewById(R.id.btn_kakao);
        btn_nocookie = (TextView) findViewById(R.id.btn_nocookie);

        ec_date = intent.getStringExtra("ec_date");
        ee_date = intent.getStringExtra("ee_date");
        tname = intent.getStringExtra("tname");
        sel_items = (ArrayList<TicketSelEntry>) intent.getSerializableExtra("sel_list");

        // 회원가입
        Button btn_join = (Button) findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 키보드 숨김
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null)
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                intent.putExtra("page", page);
                intent.putExtra("ec_date", ec_date);
                intent.putExtra("ee_date", ee_date);
                intent.putExtra("pid", pid);
                startActivityForResult(intent, 90);
            }
        });

        // 비번 재설정
        TextView btn_resetpass = (TextView) findViewById(R.id.btn_resetpass);
        btn_resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 키보드 숨김
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null)
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                Intent intent = new Intent(LoginActivity.this, WebviewActivity.class);
                intent.putExtra("url", CONFIG.passresetUrl);
                intent.putExtra("title", getString(R.string.login_find_pass2));
                startActivity(intent);
            }
        });

        // 로그인
        Button bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().length() <= 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_email), Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }

                if (email.getText().toString().contains("@") != true) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_email_invalid), Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }

                if (passwd.getText().toString().trim().length() <= 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_password), Toast.LENGTH_SHORT).show();
                    passwd.requestFocus();
                    return;
                }

                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage(getString(R.string.login_loading));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                dialog.show();

                JSONObject params = new JSONObject();
                try {
                    params.put("email", email.getText().toString());
                    params.put("password", passwd.getText().toString());
                    params.put("ver", Util.getAppVersionName(LoginActivity.this));
                    params.put("useragent", Util.getUserAgent(LoginActivity.this));
                    String androidId = Util.getAndroidId(LoginActivity.this);
                    params.put("uuid", androidId);
                } catch (JSONException e) {
                }

                Api.post(CONFIG.loginUrl, params.toString(), new Api.HttpCallback() {
                    @Override
                    public void onFailure(Response response, Exception e) {
                        Toast.makeText(LoginActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            dialog.dismiss();
                            JSONObject obj = new JSONObject(body);

                            if (!obj.getString("result").equals("success")) {
                                Toast.makeText(LoginActivity.this, getString(R.string.validator_user_not_match), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject info = obj.getJSONObject("info");

                            String username = new String(Base64.decode(info.getString("name"), 0));
                            String phone = new String(Base64.decode(info.getString("phone"), 0));
                            String userid = info.getString("id");
                            String moreinfo = (info.has("more_info") == true) ? info.getString("more_info") : "";

                            _preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor prefEditor = _preferences.edit();
                            prefEditor.putString("email", "HN|" + AES256Chiper.AES_Encode(email.getText().toString()));
                            prefEditor.putString("username", "HN|" + AES256Chiper.AES_Encode(username));
                            prefEditor.putString("phone", "HN|" + AES256Chiper.AES_Encode(phone));
                            prefEditor.putString("userid", "HN|" + AES256Chiper.AES_Encode(userid));
                            prefEditor.putString("moreinfo", moreinfo);
                            prefEditor.putString("marketing_email_yn", info.getString("marketing_email_yn"));
                            prefEditor.putString("marketing_sms_yn", info.getString("marketing_sms_yn"));
                            prefEditor.commit();

                            // Tune
                            TuneWrap.Login();

                            Toast.makeText(getApplicationContext(), getString(R.string.login_complete), Toast.LENGTH_SHORT).show();
                            passwd.setText("");

                            // 키보드 숨김
                            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                            CONFIG.MYLOGIN = true;
                            getFavorite();
                            obj = null;

                        } catch (Exception e) {
                            e.getStackTrace();
                            Toast.makeText(LoginActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                params = null;
            }
        });

        // kakao
        btn_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
                if (mKakaoSession.isClosed()) {
                } else {
                    callback = new SessionCallback();
                    Session.getCurrentSession().addCallback(callback);
                }
            }
        });

        // facebook
        callbackManager = CallbackManager.Factory.create();
        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {
                                            String user_id = object.getString("id");
                                            String snsemail = object.getString("email");

                                            if (!user_id.equals(0) && !user_id.equals("") && user_id != null) {
                                                checkUserInfo("facebook", user_id, snsemail);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "페이스북 계정을 읽을 수 없습니다. 다른 수단을 사용해 주세요.(1)", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "페이스북 계정을 읽을 수 없습니다. 다른 수단을 사용해 주세요.(2)", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,email,gender,birthday");
                                graphRequest.setParameters(parameters);
                                graphRequest.executeAsync();
                            }

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Toast.makeText(getApplicationContext(), "페이스북 계정을 읽을 수 없습니다. 다른 수단을 사용해 주세요.(3)", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        if (page.equals("Booking") || page.equals("detailH")) { // 비회원 예약
            btn_nocookie.setText(getResources().getText(R.string.login_not_user));

            btn_nocookie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Intent intent = new Intent(LoginActivity.this, ReservationActivity.class);
                    intent.putExtra("pid", pid);
                    intent.putExtra("ec_date", ec_date);
                    intent.putExtra("ee_date", ee_date);
                    startActivity(intent);
                    finish();
                }
            });
        } else if (page.equals("detailA")) { // 비회원 예약
            btn_nocookie.setText(getResources().getText(R.string.login_not_user));

            btn_nocookie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Intent intent = new Intent(LoginActivity.this, ReservationActivityActivity.class);
                    intent.putExtra("sel_list", (Serializable) sel_items);
                    intent.putExtra("tid", pid);
                    intent.putExtra("tname", tname);
                    startActivity(intent);
                    finish();
                }
            });
        } else { // 예약 조회
            btn_nocookie.setText(getResources().getText(R.string.login_user_search2));

            btn_nocookie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    CONFIG.Mypage_Search = true;
                    setResult(90, new Intent());
                    finish();
                }
            });
        }

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        if (requestCode == 90 && resultCode == 90) {
            Intent intent = new Intent();
            setResult(90, intent);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Session.getCurrentSession().clearCallbacks();
                    if (result.getId() != 0) {
                        checkUserInfo("kakao", String.valueOf(result.getId()), "");

                    } else {
                        Toast.makeText(getApplicationContext(), "카카오톡 계정을 읽을 수 없습니다. 다른 수단을 사용해 주세요.(2)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
//            Toast.makeText(getApplicationContext(), "카카오톡 계정을 읽을 수 없습니다. 다른 수단을 사용해 주세요.3", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserInfo(final String utype, final String snsid, final String snsemail) {
        JSONObject params = new JSONObject();
        try {
            params.put("utype", utype);
            params.put("snsid", snsid);
            params.put("ver", Util.getAppVersionName(LoginActivity.this));
            params.put("useragent", Util.getUserAgent(LoginActivity.this));
            String androidId = Util.getAndroidId(LoginActivity.this);
            params.put("uuid", androidId);
        } catch (JSONException e) {
        }

        Api.post(CONFIG.loginUrl, params.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(LoginActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    // 가입안된 사용자
                    if (obj.getString("result").equals("nouser")) {
                        Session.getCurrentSession().close();
                        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                        intent.putExtra("page", page);
                        intent.putExtra("pid", pid);
                        intent.putExtra("snsid", snsid);
                        intent.putExtra("utype", utype);
                        intent.putExtra("email", snsemail);
                        startActivityForResult(intent, 90);

                        return;
                    }

                    // 실패
                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(LoginActivity.this, getString(R.string.validator_user_not_match), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 가입된 사용자면 바로 로그인 후 처리
                    JSONObject info = obj.getJSONObject("info");

                    String username = new String(Base64.decode(info.getString("name"), 0));
                    String phone = new String(Base64.decode(info.getString("phone"), 0));
                    String userid = info.getString("id");
                    String emailval = new String(Base64.decode(info.getString("email"), 0));
                    String moreinfo = (info.has("more_info") == true) ? info.getString("more_info") : "";

                    _preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor prefEditor = _preferences.edit();
                    prefEditor.putString("email", "HN|" + AES256Chiper.AES_Encode(emailval));
                    prefEditor.putString("username", "HN|" + AES256Chiper.AES_Encode(username));
                    prefEditor.putString("phone", "HN|" + AES256Chiper.AES_Encode(phone));
                    prefEditor.putString("userid", "HN|" + AES256Chiper.AES_Encode(userid));
                    prefEditor.putString("moreinfo", moreinfo);
                    prefEditor.putString("utype", utype);
                    prefEditor.putString("marketing_email_yn", info.getString("marketing_email_yn"));
                    prefEditor.putString("marketing_sms_yn", info.getString("marketing_sms_yn"));
                    prefEditor.commit();

                    // Tune
                    TuneWrap.Login();

                    Toast.makeText(getApplicationContext(), getString(R.string.login_complete), Toast.LENGTH_SHORT).show();

                    // 키보드 숨김
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    CONFIG.MYLOGIN = true;
                    getFavorite();

                    body = "";
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });

        params = new JSONObject();
    }

    private void getFavorite() {

        String url = CONFIG.like_list;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(LoginActivity.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(LoginActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

                    if (page.equals("detailH")) {
                        setResult(90);
                        finish();
                    } else if (page.equals("Private")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("sdate", ec_date);
                        returnIntent.putExtra("edate", ee_date);
                        setResult(80, returnIntent);
                        finish();
                    } else {
                        setResult(90, new Intent());
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        // 키보드 숨김
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        finish();

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
        Session.getCurrentSession().close();

//        Runtime.getRuntime().gc();
    }
}
