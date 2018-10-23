package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.dialog.DialogRecommend;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SignupActivity extends Activity {

    private SharedPreferences _preferences;
    private Spinner phone_first;
    private String[] phone_prefixs;
    private TextView auth_ok, auth_count, remain_count;
    private Button btn_auth;
    private EditText auth_string, email, passwd, username, phone_num_2, phone_num_3;
    private CheckBox all_checkbox, agree_checkbox0, agree_checkbox1, agree_checkbox2, agree_checkbox3;
    private CountDownTimer countDownTimer;
    private final int MILLISINFUTURE = 180 * 1000; //총 시간 (300초 = 5분)
    private final int COUNT_DOWN_INTERVAL = 1000; //onTick 메소드를 호출할 간격 (1초)
    private String is_auth = "N";
    private String phone_number, snsid, utype, emailval;
    private EditText codeInput;
    private TextView codeResult;
    private LinearLayout auth_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        Util.setStatusColor(this);
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // 휴대폰 앞번호
        phone_prefixs= getResources().getStringArray(R.array.phone_prefix);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignupActivity.this, android.R.layout.simple_spinner_dropdown_item, phone_prefixs);
        phone_first = (Spinner)findViewById(R.id.phone_num_1);
        phone_first.setAdapter(adapter);

        // 인증번호 버튼, 확인버튼, 입력
        btn_auth = (Button)findViewById(R.id.btn_auth);
        auth_ok = (TextView)findViewById(R.id.auth_ok);
        auth_string = (EditText)findViewById(R.id.auth_string);
        auth_count = (TextView)findViewById(R.id.auth_count);
        remain_count = (TextView)findViewById(R.id.remain_count);
        auth_layout = (LinearLayout)findViewById(R.id.auth_layout);

        // 인증번호 버튼, 확인버튼, 입력
        email = (EditText)findViewById(R.id.email);
        passwd = (EditText)findViewById(R.id.passwd);
        username = (EditText)findViewById(R.id.username);
        phone_num_2 = (EditText)findViewById(R.id.phone_num_2);
        phone_num_3 = (EditText)findViewById(R.id.phone_num_3);
        all_checkbox = (CheckBox)findViewById(R.id.all_checkbox);
        agree_checkbox0 = (CheckBox)findViewById(R.id.agree_checkbox0);
        agree_checkbox1 = (CheckBox)findViewById(R.id.agree_checkbox1);
        agree_checkbox2 = (CheckBox)findViewById(R.id.agree_checkbox2);
        agree_checkbox3 = (CheckBox)findViewById(R.id.agree_checkbox3);

        Intent intent = getIntent();
        emailval = intent.getStringExtra("email") != null? intent.getStringExtra("email"):"";
        snsid = intent.getStringExtra("snsid") != null? intent.getStringExtra("snsid"):"";
        utype = intent.getStringExtra("utype") != null? intent.getStringExtra("utype"):"";

        if(!emailval.equals("")) {
            email.setText(emailval);
        }

        all_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                all_checkbox.setChecked(isChecked);
                agree_checkbox0.setChecked(isChecked);
                agree_checkbox1.setChecked(isChecked);
                agree_checkbox2.setChecked(isChecked);
                agree_checkbox3.setChecked(isChecked);
            }
        });

        btn_auth.setClickable(false);
        auth_ok.setClickable(false);

        auth_string.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    auth_ok.setClickable(true);
                    auth_ok.setBackgroundResource(R.drawable.purple_and_white_round);
                    auth_ok.setTextColor(ContextCompat.getColor(SignupActivity.this, R.color.purple));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //번호 입력만 되도 인증 버튼 활성화
        phone_num_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    btn_auth.setClickable(true);
                    btn_auth.setBackgroundResource(R.color.purple);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone_num_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    btn_auth.setClickable(true);
                    btn_auth.setBackgroundResource(R.color.purple);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //인증번호 요청
        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_num_2.getText().toString().trim().length() < 3) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                    phone_num_2.requestFocus();
                    return;
                }

                if (phone_num_3.getText().toString().trim().length() < 4) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                    phone_num_3.requestFocus();
                    return;
                }

                String phone_num_1 = (String) (phone_first.getSelectedItem() != null ? phone_first.getSelectedItem() : phone_prefixs[0]);
                String phone_number_ful = phone_num_1 + "-" + phone_num_2.getText().toString() + "-" + phone_num_3.getText().toString();

                JSONObject paramObj = new JSONObject();
                try{
                    paramObj.put("phone_number", phone_number_ful);
                } catch (JSONException e) {}

                Api.post(CONFIG.phone_auth, paramObj.toString(), new Api.HttpCallback() {

                    @Override
                    public void onFailure(Response response, Exception e) {
                        Log.e(CONFIG.TAG, "expection is ", e);
                        Toast.makeText(SignupActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);

                            if (!obj.getString("result").equals("success")) {
                                Toast.makeText(SignupActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 타이머가 종료되었을 때 출력되는 메시지,
                            btn_auth.setText("인증번호 발송");

                            auth_layout.setVisibility(View.VISIBLE);

                            // 인증하기 버튼 활성화
//                            auth_ok.setBackgroundColor(Color.parseColor("#4f2680"));
//                            auth_ok.setClickable(true);

                            //인증번호 발급버튼 비활성화
                            btn_auth.setBackgroundResource(R.color.board_line);
                            btn_auth.setClickable(false);

                            //카운트, 타이머 show
                            auth_count.setVisibility(View.VISIBLE);
                            remain_count.setText("인증번호가 발송되었습니다 ( 남은횟수 "+obj.getString("remain_count")+"회 )");
                            remain_count.setVisibility(View.VISIBLE);
                            AuthCodeTimmer();

                        } catch (JSONException e) {
                            Log.e(CONFIG.TAG, "expection is ", e);
                            Toast.makeText(SignupActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //인증번호 확인
        auth_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phone_num_2.getText().toString().trim().length() < 3) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                    phone_num_2.requestFocus();
                    return;
                }

                if (phone_num_3.getText().toString().trim().length() < 4) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                    phone_num_3.requestFocus();
                    return;
                }

                if(auth_string.getText().toString().trim().length() < 1){
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_auth), Toast.LENGTH_SHORT).show();
                    phone_num_3.requestFocus();
                    return;
                }

                final String phone_num_1 = (String) (phone_first.getSelectedItem() != null ? phone_first.getSelectedItem() : phone_prefixs[0]);
                String phone_number_ful = phone_num_1 + "-" + phone_num_2.getText().toString() + "-" + phone_num_3.getText().toString();

                JSONObject paramObj = new JSONObject();
                try{
                    paramObj.put("phone_number", phone_number_ful);
                    paramObj.put("phone_auth_code", auth_string.getText().toString());
                } catch (JSONException e) {}

                Api.post(CONFIG.phone_auth_check, paramObj.toString(), new Api.HttpCallback() {

                    @Override
                    public void onFailure(Response response, Exception e) {
                        Log.e(CONFIG.TAG, "expection is ", e);
                        Toast.makeText(SignupActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        authReset();
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try{
                            JSONObject obj = new JSONObject(body);

                            if (!obj.getString("result").equals("success")) {
                                Toast.makeText(SignupActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // 인증하기 버튼 비활성화
                            auth_ok.setBackgroundResource(R.drawable.style_edittext_gray_border);
                            auth_ok.setTextColor(ContextCompat.getColor(SignupActivity.this, R.color.coupon_dis));
                            auth_ok.setClickable(false);
                            auth_ok.setText("인증완료");

                            //카운트, 타이머 show
                            btn_auth.setBackgroundResource(R.color.board_line);
                            btn_auth.setClickable(false);
                            auth_count.setVisibility(View.GONE);
                            remain_count.setVisibility(View.GONE);
                            auth_string.setVisibility(View.GONE);

                            setUseableSpinner(phone_first, false);
                            setUseableEditText(phone_num_2, false);
                            setUseableEditText(phone_num_3, false);

                            if(countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            auth_layout.setVisibility(View.GONE);

                            is_auth = "Y";

                        } catch (JSONException e) {
                            Log.e(CONFIG.TAG, "expection is ", e);
                            Toast.makeText(SignupActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // 서비스 이용약관 동의
        TextView show_agreement1 = (TextView)findViewById(R.id.show_agreement1);
        show_agreement1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree1);
                intent.putExtra("title", getString(R.string.term_txt1));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });

        // 개인정보 취급방침 동의
        TextView show_agreement2 = (TextView)findViewById(R.id.show_agreement2);
        show_agreement2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree2);
                intent.putExtra("title", getString(R.string.term_txt2));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });

        // 위치기반서비스 이용약관 동의
        TextView show_agreement3 = (TextView)findViewById(R.id.show_agreement3);
        show_agreement3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, WebviewActivity.class);
                intent.putExtra("url", CONFIG.setting_agree3);
                intent.putExtra("title", getString(R.string.term_txt3));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });


        // 가입 하기
        Button btn_signin = (Button)findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().length() <= 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_email), Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }

                if (email.getText().toString().contains("@") != true || email.getText().toString().contains(".") != true || !Util.isValidEmail(email.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_email_invalid), Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }

                if (passwd.getText().toString().trim().length() < 4 || passwd.getText().toString().trim().length() > 20) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_password_length), Toast.LENGTH_SHORT).show();
                    passwd.requestFocus();
                    return;
                }

                if (username.getText().toString().trim().length() < 2) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_name_length), Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                    return;
                }

                if(!Util.isValidName(username.getText().toString())){
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_name_emoji), Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                    return;
                }

                if (phone_num_2.getText().toString().trim().length() < 3) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                    phone_num_2.requestFocus();
                    return;
                }

                if (phone_num_3.getText().toString().trim().length() < 4) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                    phone_num_3.requestFocus();
                    return;
                }

                if (is_auth.equals("N")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_tel_agreement), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (agree_checkbox0.isChecked() != true) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_age_agreement), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (agree_checkbox1.isChecked() != true) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_service_agreement), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (agree_checkbox2.isChecked() != true) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_userinfo_agreement), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (agree_checkbox3.isChecked() != true) {
                    Toast.makeText(getApplicationContext(), getString(R.string.validator_location_agreement), Toast.LENGTH_SHORT).show();
                    return;
                }

                String phone_num_1 = (String) (phone_first.getSelectedItem() != null ? phone_first.getSelectedItem() : phone_prefixs[0]);
                phone_number = phone_num_1 + "-" + phone_num_2.getText().toString() + "-" + phone_num_3.getText().toString();

                JSONObject paramObj = new JSONObject();
                try{
                    paramObj.put("email", email.getText().toString());
                    paramObj.put("utype", utype);
                    paramObj.put("snsid", snsid);
                    paramObj.put("password", passwd.getText().toString());
                    paramObj.put("name", username.getText().toString());
                    paramObj.put("phone", phone_number);
                    paramObj.put("ver", Util.getAppVersionName(SignupActivity.this));
                    paramObj.put("useragent", Util.getUserAgent(SignupActivity.this));
                    paramObj.put("uuid", Util.getAndroidId(SignupActivity.this));
                    paramObj.put("phone_auth", is_auth);
                } catch (JSONException e) {}

                Api.post(CONFIG.signupUrl, paramObj.toString(), new Api.HttpCallback() {
                    @Override
                    public void onFailure(Response response, Exception e) {
                        Log.e(CONFIG.TAG, "expection is ", e);
                        Toast.makeText(SignupActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);

                            if (!obj.getString("result").equals("success")) {
                                Toast.makeText(SignupActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject info = obj.getJSONObject("user_info");

                            String username = new String(Base64.decode(info.getString("name"), 0));
                            String phone = new String(Base64.decode(info.getString("phone"), 0));
                            String userid = info.getString("id");

                            int reserve_money = info.getInt("reserve_money");

                            SharedPreferences.Editor prefEditor = _preferences.edit();
                            prefEditor.putString("email", email.getText().toString().trim());
                            prefEditor.putString("username", username);
                            prefEditor.putString("phone", phone);
                            prefEditor.putString("userid", userid);
                            prefEditor.commit();

                            // 디바이스 정보 서버에 있는지 체크 후 적립금 띄우든가 말든가
                            if (obj.getString("device_exist").equals("Y")) {
                                Toast.makeText(getApplicationContext(), getString(R.string.signup_success), Toast.LENGTH_SHORT).show();
                                finishSignup();
                            } else {
                                DialogRecommend dialog = new DialogRecommend(SignupActivity.this, skipClickListener, okClickListener);
                                dialog.setCancelable(false);
                                dialog.show();

                                codeInput = (EditText) dialog.findViewById(R.id.recommend_code);
                                codeResult = (TextView) dialog.findViewById(R.id.recommend_result);
                            }

                            // Tune
                            TuneWrap.Registration();
                            TuneWrap.Login();

                        } catch (Exception e) {
                            Log.e(CONFIG.TAG, "expection is ", e);
                            Toast.makeText(SignupActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    private void setUseableSpinner(Spinner sp, boolean useable) {
        if(sp != null){
            sp.setClickable(useable);
            sp.setEnabled(useable);
            sp.setFocusable(useable);
            sp.setFocusableInTouchMode(useable);
            if(useable) {
                sp.setBackgroundResource(R.drawable.selectbox2);
            }
            else{
                sp.setBackgroundResource(R.drawable.selectbox2_dis);
            }
        }
    }

    private void setUseableEditText(EditText et, boolean useable) {
        if(et != null) {
            et.setClickable(useable);
            et.setEnabled(useable);
            et.setFocusable(useable);
            et.setFocusableInTouchMode(useable);
            if (useable) {
                et.setBackgroundResource(R.color.white);
            } else {
                et.setBackgroundResource(R.color.edit_back);
            }
        }
    }

    private void authReset(){
        // 인증하기 버튼 활성화
        auth_ok.setBackgroundResource(R.drawable.purple_and_white_round);
        auth_ok.setTextColor(ContextCompat.getColor(this, R.color.white));
        auth_ok.setClickable(true);

        //인증번호 발급버튼 비활성화
        btn_auth.setBackgroundResource(R.color.board_line);
        btn_auth.setClickable(true);

        //카운트, 타이머 show
        auth_count.setVisibility(View.GONE);

        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // 인증번호 타이머
    public void AuthCodeTimmer()
    {
        // 타이머 설정
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL)
        {
            @Override
            public void onFinish()
            {
                // 인증하기 버튼 비활성화
                auth_ok.setBackgroundResource(R.drawable.style_edittext_gray_border);
                auth_ok.setTextColor(ContextCompat.getColor(SignupActivity.this, R.color.coupon_dis));
                auth_ok.setClickable(false);

                //인증번호 발급버튼 활성화
                btn_auth.setBackgroundResource(R.color.purple);
                btn_auth.setClickable(true);
            }
            @Override
            public void onTick(long millisUntilFinished)
            {
                long AuthCount = millisUntilFinished / 1000;

                if ((AuthCount - ((AuthCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                    auth_count.setText("("+(AuthCount / 60) + " : " + (AuthCount - ((AuthCount / 60) * 60))+")");
                } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                    auth_count.setText("("+(AuthCount / 60) + " : 0" + (AuthCount - ((AuthCount / 60) * 60))+")");
                }
            }
        };
        // 타이머 시작
        countDownTimer.start();
    }

    // 추천인 코드 dialog cancel
    private View.OnClickListener skipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), getString(R.string.signup_success), Toast.LENGTH_SHORT).show();
            finishSignup();
        }
    };

    // 추천인 코드 dialog 서버전송
    private View.OnClickListener okClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject paramObj = new JSONObject();
            try{
                paramObj.put("code", String.valueOf(codeInput.getText()).trim());
                paramObj.put("target_id", _preferences.getString("userid", null));
            } catch (JSONException e) {}

            Api.post(CONFIG.recommendSaveUrl, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    Log.e(CONFIG.TAG, "expection is ", e);
                    Toast.makeText(SignupActivity.this, getString(R.string.validator_recommend_fail), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(SignupActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.signup_recommend_success), Toast.LENGTH_SHORT).show();
                            finishSignup();
                        }
                    } catch (Exception e) {
                        Log.e(CONFIG.TAG, "expection is ", e);
                        Toast.makeText(SignupActivity.this, getString(R.string.validator_recommend_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    // 회원가입 종료
    private void finishSignup(){
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        Intent intent = new Intent();
        setResult(90, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishSignup();
        super.onBackPressed();
    }
}
