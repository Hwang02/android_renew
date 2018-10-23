package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.ReservationPagerAdapter;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReservationActivity extends Activity {

    private EditText point_discount, usernameInput, pnum2, pnum3, other_pnum2, other_pnum3, auth_string, other_username;
    private String pid, accepted_price, ec_date, ee_date, cookie;
    private SharedPreferences _preferences;
    private LinearLayout layout_useremail;
    private TextView tv_hotel_name, tv_room_detail_price, tv_detail3, tv_checkin_day, tv_checkin_time, tv_checkout_day, tv_checkout_time,auth_ok, tv_auth_change, btn_auth, tv_detail_per;
    private AutoLinkTextView fee_text;
    private ImageView img_room;
    private String[] hphone;
    private Spinner pnum1, other_pnum1;
    private String is_auth = "N";
    private String checkin_date;
    private String checkout_date;
    private TextView sub_total_price2, auth_count, remain_count;
    private CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 180 * 1000; //총 시간 (300초 = 5분)
    final int COUNT_DOWN_INTERVAL = 1000; //onTick 메소드를 호출할 간격 (1초)
    private CheckBox other_checkbox;
    private TextView show_policy;
    private LinearLayout layout_orther_user;
    private boolean is_other_user = false;
    private Button btn_go_payment;
    private Boolean sale_available = false;
    private Boolean flag_btn_clicked = false;
    private DialogAlert dialogAlert;
    private int paytype = 0;
    private int reserve_money = 0, sale_price;
    private LinearLayout ll_point;
    private TextView btn_point_total, tv_point_title, tv_coupon_title;
    private NumberFormat nf = NumberFormat.getNumberInstance();
    private Spinner coupon_spinner;
    private Map<Integer, String> coupon_arr;
    private ArrayList<Integer> coupon_price;
    private TextView tv_discount_price, tv_real_price, tv_total_price;
    private ViewPager pay_pager;
    private ArrayList<String> banner_arr;
    private ReservationPagerAdapter pay_adapter;
    Runnable Update = null;
    Handler handler = new Handler();
    Timer swipeTimer = null;
    int nowPosition = 0;
    TableLayout sub_products;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        _preferences = PreferenceManager.getDefaultSharedPreferences(ReservationActivity.this);
        point_discount = (EditText) findViewById(R.id.point_discount);
        usernameInput = (EditText) findViewById(R.id.username);
        pnum2 = (EditText) findViewById(R.id.pnum2);
        pnum3 = (EditText) findViewById(R.id.pnum3);
        other_pnum2 = (EditText) findViewById(R.id.other_pnum2);
        other_pnum3 = (EditText) findViewById(R.id.other_pnum3);
        layout_useremail = (LinearLayout) findViewById(R.id.layout_useremail);
        tv_hotel_name = (TextView) findViewById(R.id.tv_hotel_name);
        tv_room_detail_price = (TextView) findViewById(R.id.tv_room_detail_price);
        tv_detail_per = (TextView) findViewById(R.id.tv_detail_per);
        tv_detail3 = (TextView) findViewById(R.id.tv_detail3);
        tv_checkin_day = (TextView) findViewById(R.id.tv_checkin_day);
        tv_checkin_time = (TextView) findViewById(R.id.tv_checkin_time);
        tv_checkout_day = (TextView) findViewById(R.id.tv_checkout_day);
        tv_checkout_time = (TextView) findViewById(R.id.tv_checkout_time);
        tv_auth_change = (TextView) findViewById(R.id.tv_auth_change);
        auth_ok = (TextView) findViewById(R.id.auth_ok);
        btn_auth = (TextView) findViewById(R.id.btn_auth);
        auth_string = (EditText) findViewById(R.id.auth_string);
        auth_count = (TextView) findViewById(R.id.auth_count);
        remain_count = (TextView) findViewById(R.id.remain_count);
        other_checkbox = (CheckBox) findViewById(R.id.other_checkbox);
        fee_text = (AutoLinkTextView) findViewById(R.id.fee_text);
        show_policy = (TextView) findViewById(R.id.show_policy);
        layout_orther_user = (LinearLayout) findViewById(R.id.layout_orther_user);
        btn_go_payment = (Button) findViewById(R.id.btn_go_payment);
        other_username = (EditText) findViewById(R.id.other_username);
        img_room = (ImageView) findViewById(R.id.img_room);
        pnum1 = (Spinner)findViewById(R.id.pnum1);
        other_pnum1 = (Spinner)findViewById(R.id.other_pnum1);
        ll_point = (LinearLayout)findViewById(R.id.ll_point);
        btn_point_total = (TextView)findViewById(R.id.btn_point_total);
        coupon_spinner = (Spinner) findViewById(R.id.coupon_use);
        tv_discount_price = (TextView)findViewById(R.id.tv_discount_price);
        tv_real_price = (TextView)findViewById(R.id.tv_real_price);
        tv_total_price = (TextView)findViewById(R.id.tv_total_price);
        pay_pager = (ViewPager) findViewById(R.id.pay_pager);
        sub_products = (TableLayout)findViewById(R.id.sub_products);

        cookie = _preferences.getString("userid", null);

        point_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    point_discount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_input_delete, 0);
                }
                else if(s.length()==0){
                    point_discount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        auth_string.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    auth_ok.setClickable(true);
                    auth_ok.setBackgroundResource(R.drawable.purple_and_white_round);
                    auth_ok.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.purple));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        point_discount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP && point_discount.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                    if(event.getRawX() >= (point_discount.getRight() - point_discount.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        point_discount.setText("");
                        LogUtil.e("xxxx", "xxxx삭제");
                        return true;
                    }
                }
                return false;
            }
        });

        Intent intent = getIntent();
        ec_date = intent.getStringExtra("ec_date");
        ee_date = intent.getStringExtra("ee_date");
        pid = intent.getStringExtra("pid");

        authCheck();
    }

    public void authCheck() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", _preferences.getString("userid", null));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivity.this, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    layout_useremail.setVisibility(View.GONE);
                    if (obj.getString("result").equals("0")) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("email", null);
                        prefEditor.putString("username", null);
                        prefEditor.putString("phone", null);
                        prefEditor.putString("userid", null);
                        prefEditor.commit();
                        layout_useremail.setVisibility(View.VISIBLE);
                    }
                    getDealInfo();
                } catch (Exception e) {

                }
            }
        });
    }

    private void getDealInfo() {
        String url;

        if (ec_date != null && ee_date != null)
            url = CONFIG.reserveUrl + "/" + pid + "?ec_date=" + ec_date + "&ee_date=" + ee_date + "&consecutive=Y";
        else
            url = CONFIG.reserveUrl + "/" + pid;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    boolean isReserve = false, isCoupon = false, isPrivate = false;
                    JSONObject obj = new JSONObject(body);
                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReservationActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject data = obj.getJSONObject("data");
                    // 룸정보
                    tv_hotel_name.setText(data.getString("hotel_name"));
                    Ion.with(img_room).load(data.getString("room_img"));
                    tv_room_detail_price.setText(Util.numberFormat(data.getInt("sale_price")));
                    tv_detail3.setText(data.getString("room_name"));
                    tv_detail_per.setText(data.getString("sale_rate")+"%↓");
                    checkin_date = data.getString("checkin_date");
                    checkout_date = data.getString("checkout_date");
                    sale_available = data.getBoolean("sale_available");
                    sale_price = data.getInt("sale_price");
                    int real_price = data.getInt("normal_price");
                    if(data.has("reserve_money"))
                        reserve_money = data.getInt("reserve_money");
                    int save_price = real_price - sale_price;

                    SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd(EEE)", Locale.KOREAN);
                    SimpleDateFormat DateFormat2 = new SimpleDateFormat("HH:mm", Locale.KOREAN);
                    SimpleDateFormat DateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN);

                    Date to_day = DateFormat3.parse(checkin_date);
                    Date to_day1 = DateFormat3.parse(checkout_date);

                    tv_checkin_day.setText(DateFormat.format(to_day));
                    tv_checkin_time.setText(DateFormat2.format(to_day));
                    tv_checkout_day.setText(DateFormat.format(to_day1));
                    tv_checkout_time.setText(DateFormat2.format(to_day1));

                    // 예약자 정보
                    String[] phonePrefixs = getResources().getStringArray(R.array.phone_prefix);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationActivity.this, android.R.layout.simple_spinner_dropdown_item, phonePrefixs);
                    pnum1.setAdapter(adapter);
                    other_pnum1.setAdapter(adapter);

                    String username = _preferences.getString("username", null);
                    String hphoneTmp = _preferences.getString("phone", null);
                    if (username != null && hphoneTmp != null) {
                        hphone = hphoneTmp.split("-");

                        if (username != null) {
                            usernameInput.setText(username);
                        }

                        pnum2.setText(hphone[1]);
                        pnum3.setText(hphone[2]);

                        for (int i = 0; i < phonePrefixs.length; i++) {
                            if (hphone[0].equals(phonePrefixs[i])) {
                                pnum1.setSelection(i);
                                break;
                            }
                        }
                    }

                    //번호인증
                    if(cookie == null){ // 비회원
                        findViewById(R.id.layout_auth).setVisibility(View.VISIBLE);
                        other_checkbox.setVisibility(View.GONE);
                        tv_auth_change.setVisibility(View.GONE);
                        setUseableEditText(usernameInput, true);
                        setUseableEditText(pnum2, true);
                        setUseableEditText(pnum3, true);
                        setUseableSpinner(pnum1, true);
                    }
                    else {// 회원
                        //인증
                        is_auth = data.getString("phone_auth");
                        setUseableEditText(usernameInput, false);
                        if(is_auth.equals("Y")) {
                            tv_auth_change.setVisibility(View.VISIBLE);
                            findViewById(R.id.layout_auth).setVisibility(View.GONE);
                            setUseableEditText(pnum2, false);
                            setUseableEditText(pnum3, false);
                            setUseableSpinner(pnum1, false);
                        }
                        else {
                            tv_auth_change.setVisibility(View.GONE);
                            findViewById(R.id.layout_auth).setVisibility(View.VISIBLE);
                            setUseableEditText(pnum2, true);
                            setUseableEditText(pnum3, true);
                            setUseableSpinner(pnum1, true);

                        }
                        other_checkbox.setVisibility(View.VISIBLE);
                    }

                    // 변경하기
                    tv_auth_change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean is_change = false;
                            if(tv_auth_change.getText().toString().equals("변경하기")) {
                                tv_auth_change.setText(Html.fromHtml("<u>" + getResources().getString(R.string.phone_cancel) + "</u>"));
                                findViewById(R.id.layout_auth).setVisibility(View.VISIBLE);
                                btn_auth.setVisibility(View.VISIBLE);
                                auth_ok.setVisibility(View.VISIBLE);
                                is_change = true;
                            }
                            else{
                                tv_auth_change.setText(Html.fromHtml("<u>" + getResources().getString(R.string.phone_change) + "</u>"));
                                findViewById(R.id.layout_auth).setVisibility(View.GONE);
                                btn_auth.setVisibility(View.GONE);
                                auth_ok.setVisibility(View.GONE);
                                is_change = false;

                            }

                            authReset();

                            if (cookie != null)
                                setUseableEditText(usernameInput, false);

                            setUseableEditText(pnum2, is_change);
                            setUseableEditText(pnum3, is_change);
                            setUseableSpinner(pnum1, is_change);
                        }
                    });

                    //인증번호 요청
                    btn_auth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (pnum2 == null || pnum2.getText().toString().trim().length() < 3) {
                                Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                                pnum2.requestFocus();
                                return;
                            }

                            if (pnum3 == null|| pnum3.getText().toString().trim().length() < 4) {
                                Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                                pnum3.requestFocus();
                                return;
                            }

                            String phone_num_1 = (String) (pnum1.getSelectedItem() != null ? pnum1.getSelectedItem() : hphone[0]);
                            String phone_number_ful = phone_num_1 + "-" + pnum2.getText().toString() + "-" + pnum3.getText().toString();

                            JSONObject paramObj = new JSONObject();
                            try{
                                paramObj.put("phone_number", phone_number_ful);
                            } catch (JSONException e) {}

                            Api.post(CONFIG.phone_auth, paramObj.toString(), new Api.HttpCallback() {

                                @Override
                                public void onFailure(Response response, Exception e) {
                                    Log.e(CONFIG.TAG, "expection is ", e);
                                    Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(Map<String, String> headers, String body) {
                                    try {
                                        JSONObject obj = new JSONObject(body);

                                        if (!obj.getString("result").equals("success")) {
                                            Toast.makeText(ReservationActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // 타이머가 종료되었을 때 출력되는 메시지
                                        btn_auth.setText("인증번호 발송");

                                        //인증번호 발급버튼 비활성화
                                        btn_auth.setBackgroundColor(Color.parseColor("#e1e1e1"));
                                        btn_auth.setClickable(false);

                                        //카운트, 타이머 show
                                        auth_count.setVisibility(View.VISIBLE);
                                        remain_count.setText("인증번호가 발송되었습니다 ( 남은횟수 "+obj.getString("remain_count")+"회 )");
                                        remain_count.setVisibility(View.VISIBLE);
                                        AuthCodeTimmer();

                                    } catch (JSONException e) {
                                        Log.e(CONFIG.TAG, "expection is ", e);
                                        Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                    // 인증하기
                    auth_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            authSendData();
                        }
                    });

                    //다른 사람이 방문 할 경우
                    other_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                layout_orther_user.setVisibility(View.VISIBLE);
                            }
                            else{
                                layout_orther_user.setVisibility(View.GONE);
                            }
                            is_other_user = isChecked;
                        }
                    });

                    // 적립금
                    if (data.has("reserve_money") && reserve_money > 0) {
                        isReserve = true;
                        ll_point.setVisibility(View.VISIBLE);
                        btn_point_total.setVisibility(View.VISIBLE);

                        tv_point_title = (TextView) findViewById(R.id.tv_point_title);
                        String s_save_money = getResources().getString(R.string.reservation_point) + "(" + nf.format(reserve_money) + " 원)";
                        SpannableStringBuilder builder = new SpannableStringBuilder(s_save_money);
                        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.redtext)), 3, s_save_money.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_point_title.append(builder);
                    }
                    else{
                        ll_point.setVisibility(View.GONE);
                        btn_point_total.setVisibility(View.GONE);
                    }

                    // 쿠폰
                    if(data.has("promotion_code")) {
                        JSONArray pcodes = data.getJSONArray("promotion_code");
                        LinearLayout ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);
                        if (pcodes.length() > 0) {
                            isCoupon = true;

                            ll_coupon.setVisibility(View.VISIBLE);

                            tv_coupon_title = (TextView) findViewById(R.id.tv_coupon_title);
                            String s_coupon = getResources().getString(R.string.reservation_coupon) + "(" + nf.format(pcodes.length()) + " 장)";
                            SpannableStringBuilder builder = new SpannableStringBuilder(s_coupon);
                            builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.redtext)), 4, s_coupon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_coupon_title.append(builder);

                            coupon_arr = new HashMap<Integer, String>();
                            coupon_price = new ArrayList<Integer>();
                            ArrayList<String> coupon_txt_arr = new ArrayList<String>();

                            coupon_arr.put(0, "0");
                            coupon_txt_arr.add("사용안함");
                            coupon_price.add(0);

                            for (int i = 0; i < pcodes.length(); i++) {
                                JSONObject tobj = pcodes.getJSONObject(i);

                                coupon_arr.put(i + 1, tobj.getString("id"));
                                coupon_txt_arr.add(tobj.getString("promotion_name") +" (" + tobj.getString("promotion_price") + "원)");
                                coupon_price.add(Integer.valueOf(tobj.getString("promotion_price")));
                            }

                            ArrayAdapter<String> coupon_adapter = new ArrayAdapter<String>(ReservationActivity.this, R.layout.layout_spinner_item, coupon_txt_arr);
                            coupon_spinner.setAdapter(coupon_adapter);
                            coupon_spinner.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });

                            coupon_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                                    coupon_value = coupon_price.get(position);
//                                    pcode = coupon_arr.get(position);
//                                    selectdCouponIdx = position;
//
//                                    int final_price = sale_price - reserve_value - coupon_value - private_money;
//
//                                    if (final_price < 0) {
//                                        int minusReserve = (int) Math.ceil(Math.abs(final_price) / (double) 1000);
//
//                                        reserve_money_spinner.setSelection(selectdReserveIdx - minusReserve);
//                                        txt_sale_price.setText(getString(R.string.price, nf.format(sale_price - final_price)));
//                                    } else {
//                                        txt_sale_price.setText(getString(R.string.price, nf.format(final_price)));
//                                        if(coupon_value == 0) {
//                                            if(parentView.getChildAt(0) !=null)
//                                                ((TextView) parentView.getChildAt(0)).setText("사용안함");
//                                        } else {
//                                            if(parentView.getChildAt(0) !=null)
//                                                ((TextView) parentView.getChildAt(0)).setText(getString(R.string.price, nf.format(coupon_value)));
//                                        }
//                                    }
//
//                                    sub_total_price2.setText("-"+Util.numberFormat(reserve_value + coupon_value + private_money) + "원");
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }
                            });
                        }
                        else{
                            ll_coupon.setVisibility(View.GONE);
                        }
                    }

                    // 금액
                    tv_discount_price.setText("-"+save_price+"원");
                    tv_real_price.setText(real_price+"원");
                    tv_total_price.setText(sale_price+"원");

                    // 적립금


                    // 결제관련 이미지
                    if(data.has("pay_promotion_img") && data.getJSONArray("pay_promotion_img").length() > 0) {
                        banner_arr = new ArrayList<String>();
                        pay_adapter = new ReservationPagerAdapter(ReservationActivity.this, banner_arr);
                        pay_pager.setAdapter(pay_adapter);
                        pay_pager.setVisibility(View.VISIBLE);
                        for(int i=0; i<data.getJSONArray("pay_promotion_img").length(); i++) {
                            banner_arr.add(data.getJSONArray("pay_promotion_img").get(i).toString());
                        }
                        pay_adapter.notifyDataSetChanged();
                        pay_pager.setClipToPadding(false);
                        pay_pager.setCurrentItem(banner_arr.size() * 10);
                        pay_pager.setOffscreenPageLimit(3);
                        pay_pager.setPageMargin(3);

                        setTimer();
                    }
                    else{
                        pay_pager.setVisibility(View.GONE);
                    }

                    // 연박일때
                    if (data.has("products") == true) {
                        SimpleDateFormat p_DateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat p_CurDateFormat = new SimpleDateFormat(getString(R.string.year_month_day_weekday_format3), Locale.KOREAN);
                        JSONArray products = data.getJSONArray("products");

                        if(products.length() > 1) {
                            sub_products.setVisibility(View.VISIBLE);

                            //	sub_products 아래에 할당할 상품 정보들
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject tobj = products.getJSONObject(i);

                                Date subDate = p_DateFormat.parse(tobj.getString("checkin_date"));
                                String subcheckin = p_CurDateFormat.format(subDate);

                                View v = (TableRow) getLayoutInflater().inflate(R.layout.row_consecutive, null);
                                TextView sub_date = (TextView) v.findViewById(R.id.date);
                                TextView sub_price = (TextView) v.findViewById(R.id.price);
                                TextView sub_breakfast = (TextView) v.findViewById(R.id.breakfast);

                                String bf = tobj.getString("breakfast");

                                if(bf.equals("0")) bf = "불포함";
                                else bf = "포함";

                                sub_date.setText(subcheckin);
                                sub_price.setText(Util.numberFormat(tobj.getInt("sale_price")) + "원");
                                sub_breakfast.setText(bf);

                                sub_products.addView(v);
                            }
                        }
                    }

                    // 결제 하기
                    if (sale_available == false) {
                        btn_go_payment.setBackgroundColor(Color.parseColor("aaaaaa"));
                        btn_go_payment.setText(getString(R.string.sell_complete));
                    }

                    // 결제 하기
                    btn_go_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (flag_btn_clicked == true)
                                return;
                            else
                                flag_btn_clicked = true;

                            // 입금 전 폼 체크
                            if (usernameInput.getText().toString().trim().length() < 2) {
                                Toast.makeText(getApplicationContext(), getString(R.string.validator_name_length), Toast.LENGTH_SHORT).show();
                                usernameInput.requestFocus();
                                flag_btn_clicked = false;
                                return;
                            }

                            if (pnum2.getText().toString().trim().length() < 3) {
                                Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                                pnum2.requestFocus();
                                flag_btn_clicked = false;
                                return;
                            }

                            if (pnum3.getText().toString().trim().length() < 4) {
                                Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                                pnum3.requestFocus();
                                flag_btn_clicked = false;
                                return;
                            }

                            if(is_other_user){
                                if (other_username.getText().toString().trim().length() < 2) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.validator_name_length), Toast.LENGTH_SHORT).show();
                                    usernameInput.requestFocus();
                                    flag_btn_clicked = false;
                                    return;
                                }

                                if (other_pnum2.getText().toString().trim().length() < 3) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                                    pnum2.requestFocus();
                                    flag_btn_clicked = false;
                                    return;
                                }

                                if (other_pnum3.getText().toString().trim().length() < 4) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
                                    pnum3.requestFocus();
                                    flag_btn_clicked = false;
                                    return;
                                }
                            }

                            if(is_auth.equals("N")) { // 비회원이고 인증 유무
                                dialogAlert = new DialogAlert(
                                        getString(R.string.alert_notice),
                                        getString(R.string.validator_auth),
                                        ReservationActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogAlert.dismiss();
                                            }
                                        });
                                dialogAlert.setCancelable(false);
                                dialogAlert.show();

                                flag_btn_clicked = false;
                                return;
                            }

                            if (paytype <= 0) {
                                dialogAlert = new DialogAlert(
                                        getString(R.string.alert_notice),
                                        "결제방식을 선택하세요.",
                                        ReservationActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogAlert.dismiss();
                                            }
                                        });
                                dialogAlert.setCancelable(false);
                                dialogAlert.show();

                                flag_btn_clicked = false;
                                return;
                            }

//                            if(_preferences.getString("userid", null) == null && TextUtils.isEmpty(useremailInput.getText())){
//                                Toast.makeText(getApplicationContext(), getString(R.string.validator_email), Toast.LENGTH_SHORT).show();
//                                useremailInput.requestFocus();
//                                flag_btn_clicked = false;
//                                return;
//                            }
//
//                            if (agree_policy.isChecked() != true) {
//
//                                dialogAlert = new DialogAlert(
//                                        getString(R.string.alert_notice),
//                                        getString(R.string.need_caution_agree),
//                                        ReservationActivity.this,
//                                        new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                dialogAlert.dismiss();
//                                            }
//                                        });
//                                dialogAlert.setCancelable(false);
//                                dialogAlert.show();
//
//                                flag_btn_clicked = false;
//                                return;
//                            }
//
//                            Calendar cal = Calendar.getInstance();
//                            Calendar calDawn = Calendar.getInstance();
//                            cal.add(Calendar.DATE, -1);
//                            Date dt = cal.getTime();
//                            Date dtDawn = calDawn.getTime();
//                            SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
//                            SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//                            if ((CurHourFormat.format(dt).equals("00") || CurHourFormat.format(dt).equals("01")) && checkin_date.startsWith(CurDateFormat.format(dt))) {
//                                String msg = "현재 선택하신 상품은 지금 바로 체크인을 하셔야 하는 상품입니다.<br/>내일 밤(" + CurDateFormat.format(dtDawn) + ")부터 투숙을 원하실 경우 날짜를 다시 선택해주세요. 현재 선택하신 상품을 결제하시겠습니까?";
//
//                                bdialog = new DialogBookingCaution(ReservationActivity.this, okClickListener, skipClickListener);
//                                bdialog.setCancelable(false);
//                                bdialog.show();
//                                caution_txt = (TextView) bdialog.findViewById(R.id.caution_txt);
//                                caution_txt.setText(Html.fromHtml(msg));
//
//                            } else if ((CurHourFormat.format(dt).equals("02") ||
//                                    CurHourFormat.format(dt).equals("03") ||
//                                    CurHourFormat.format(dt).equals("04") ||
//                                    CurHourFormat.format(dt).equals("05")) && checkin_date.startsWith(CurDateFormat.format(dtDawn))) {
//
//                                String msg = "현재 선택하신 상품은 당일 오후에 체크인하는 상품입니다. 예약을 진행하시겠습니까?";
//
//                                bdialog = new DialogBookingCaution(ReservationActivity.this, okClickListener, skipClickListener);
//                                bdialog.setCancelable(false);
//                                bdialog.show();
//                                caution_txt = (TextView) bdialog.findViewById(R.id.caution_txt);
//                                caution_txt.setText(Html.fromHtml(msg));
//
//                            } else {
//                                if (paytype == 4) {
//                                    showSignPop();
//                                } else {
//                                    goPayment();
//                                }
//                            }
                        }
                    });


                    // 정보제공 내용 보기
                    show_policy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ReservationActivity.this, WebviewActivity.class);
                            intent.putExtra("url", CONFIG.info_provide1);
                            intent.putExtra("title", getString(R.string.caution_agreement_checkbox));
                            startActivity(intent);
                        }
                    });

                    // 규정
                    Spanned fee_tmp = Html.fromHtml(data.getString("fee").replaceAll("\n", "<br>").replaceAll("-", "ㆍ"));
                    fee_text.setText(fee_tmp);

                } catch (Exception e) {
                    LogUtil.e("xxxx", e.getMessage());
                }
            }
        });
    }

    public void authSendData(){
        if (pnum2.getText().toString().trim().length() < 3) {
            Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
            pnum2.requestFocus();
            return;
        }

        if (pnum3.getText().toString().trim().length() < 4) {
            Toast.makeText(getApplicationContext(), getString(R.string.validator_pnum), Toast.LENGTH_SHORT).show();
            pnum3.requestFocus();
            return;
        }

        if(auth_string.getText().toString().trim().length() < 1){
            Toast.makeText(getApplicationContext(), getString(R.string.validator_auth), Toast.LENGTH_SHORT).show();
            auth_string.requestFocus();
            return;
        }

        String phone_num_1 = (String) (pnum1.getSelectedItem() != null ? pnum1.getSelectedItem() : hphone[0]);
        final String phone_number_ful = phone_num_1 + "-" + pnum2.getText().toString() + "-" + pnum3.getText().toString();

        JSONObject paramObj = new JSONObject();
        try{
            paramObj.put("phone_number", phone_number_ful);
            paramObj.put("phone_auth_code", auth_string.getText().toString());
            paramObj.put("user_id", cookie);
            paramObj.put("uuid", Util.getAndroidId(ReservationActivity.this));
        } catch (JSONException e) {}

        Api.post(CONFIG.phone_auth_check, paramObj.toString(), new Api.HttpCallback() {

            @Override
            public void onFailure(Response response, Exception e) {
                Log.e(CONFIG.TAG, "expection is ", e);
                Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                authReset();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try{
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReservationActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(cookie != null) {
                        //번호 저장
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("phone", phone_number_ful);
                        prefEditor.commit();
                    }

                    if(cookie != null) {
                        setUseableEditText(usernameInput, false);
                    }
                    else{
                        setUseableEditText(usernameInput, true);
                    }

                    auth_ok.setVisibility(View.GONE);
                    findViewById(R.id.layout_auth).setVisibility(View.GONE);
                    setUseableEditText(pnum2, false);
                    setUseableEditText(pnum3, false);
                    setUseableSpinner(pnum1, false);

                    tv_auth_change.setText(Html.fromHtml("<u>" + getResources().getString(R.string.phone_change) + "</u>"));

                    // 인증하기 비활성화
                    auth_ok.setBackgroundResource(R.drawable.style_edittext_gray_border);
                    auth_ok.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.coupon_dis));

                    if(countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    is_auth = "Y";

                } catch (JSONException e) {
                    Log.e(CONFIG.TAG, "expection is ", e);
                    Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                auth_ok.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.coupon_dis));
                auth_ok.setClickable(false);

                //인증번호 발급버튼 활성화
                btn_auth.setBackgroundResource(R.color.purple);
                btn_auth.setClickable(true);

                auth_count.setText("시간 초과");
                remain_count.setText("입력시간을 초과했습니다 다시 시도해주세요.");
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

    private void authReset(){
        // 인증하기 버튼 비활성화
        auth_ok.setClickable(false);

        // 인증번호 발급버튼 활성화
        btn_auth.setBackgroundColor(Color.parseColor("#4f2680"));
        btn_auth.setClickable(true);

        btn_auth.setText("인증번호 발송");
        auth_string.setText("");
        auth_count.setText("");

        //카운트, 타이머 show
        auth_count.setVisibility(View.GONE);
        remain_count.setVisibility(View.GONE);

        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void setUseableEditText(EditText et, boolean useable) {
        if(et != null) {
            et.setClickable(useable);
            et.setEnabled(useable);
            et.setFocusable(useable);
            et.setFocusableInTouchMode(useable);
            if (useable) {
                et.setBackgroundResource(R.drawable.style_edittext_gray_border);
            } else {
                et.setBackgroundColor(Color.parseColor("#f6f6f6"));
            }
        }
    }

    private void setUseableSpinner(Spinner sp, boolean useable) {
        if(sp != null){
            sp.setClickable(useable);
            sp.setEnabled(useable);
            sp.setFocusable(useable);
            sp.setFocusableInTouchMode(useable);
            if(useable) {
                sp.setBackgroundResource(R.drawable.style_edittext_gray_border);
            }
            else{
                sp.setBackgroundColor(Color.parseColor("#f6f6f6"));
            }
        }
    }

    public void setTimer(){
        if(pay_pager != null) {
            try {
                if (Update != null || swipeTimer != null) return;
                Update = new Runnable() {
                    public void run() {
                        try {
                            if(nowPosition == pay_adapter.getCount()){
                                nowPosition = 0;
                            }

                            pay_pager.setCurrentItem(nowPosition++, true);
                        } catch (Exception e) {
                            Util.doRestart(ReservationActivity.this);
                        }
                    }
                };
                swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, 0, 2000);
            } catch (Exception e) {
                Util.doRestart(ReservationActivity.this);
            }
        }
    }

    public void stopTimer(){
        if(Update != null || swipeTimer != null) {
            swipeTimer.cancel();
            swipeTimer.purge();
            swipeTimer = null;
            Update = null;
        }
    }

    @Override
    public void onBackPressed () {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        stopTimer();

        super.onBackPressed();
    }
}
