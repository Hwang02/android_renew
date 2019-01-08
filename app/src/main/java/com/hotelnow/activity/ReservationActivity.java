package com.hotelnow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
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
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.ReservationPagerAdapter;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogBillingAlert;
import com.hotelnow.dialog.DialogBookingCaution;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.dialog.DialogFee;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.CustomLinkMovementMethod;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    private String pid, ec_date, ee_date, cookie, pcode = "0", hid;
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
    private TextView auth_count, remain_count;
    private CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 180 * 1000; //총 시간 (300초 = 5분)
    final int COUNT_DOWN_INTERVAL = 1000; //onTick 메소드를 호출할 간격 (1초)
    private CheckBox other_checkbox;
    private TextView show_policy;
    private LinearLayout layout_orther_user;
    private boolean is_other_user = false;
    private Button btn_go_payment, btn_point_ok;
    private Boolean sale_available = false;
    private Boolean flag_btn_clicked = false;
    private EditText useremail;
    private DialogAlert dialogAlert;
    private int paytype = 0;
    private int reserve_money = 0, sale_price;
    private LinearLayout ll_point;
    private TextView btn_point_total, tv_point_title, tv_coupon_title;
    private NumberFormat nf = NumberFormat.getNumberInstance();
    private Spinner coupon_spinner;
    private Map<Integer, String> coupon_arr;
    private ArrayList<Integer> coupon_price;
    private ArrayList<String> coupon_avail;
    private TextView tv_discount_price, tv_real_price, tv_total_price, tv_total_discount_point, tv_title_bar;
    private ViewPager pay_pager;
    private ArrayList<String> banner_arr;
    private ReservationPagerAdapter pay_adapter;
    Runnable Update = null;
    Handler handler = new Handler();
    Timer swipeTimer = null;
    int nowPosition = 0;
    TableLayout sub_products;
    int save_price = 0;
    private LinearLayout ll_private;
    String mPage = ""; // nomal, private
    private LinearLayout paytype1_background, paytype2_background, paytype3_background, paytype4_background, paytype5_background, paytype3_info, booking_save_point, btn_phone;
    private LinearLayout paytype0_list;
    private TextView tv_paytype1, tv_paytype2, tv_paytype3, tv_paytype4, tv_paytype5, paytype3_txt;
    private ImageView img_paytype1, img_paytype2, img_paytype3, img_paytype4, img_paytype5;
    private HashMap<Integer, JSONObject> billmap;
    private CheckBox agree_policy;
    private DialogBookingCaution bdialog;
    private DialogBillingAlert dialogBillingAlert;
    private String bmpStr = "";
    String selected_card = "", bid_id="";
    String reserve_value = "";
    private int private_money = 0, real_price=0, coupon_value = 0, accepted_price = 0;
    private String hotel_name;
    private boolean isReserve = false, isCoupon = false, is_sel_point = false, is_sel_coupon = false;
    private String all_coupon_id = "",cancel_fee_str="";
    private DialogFee dialogFee;
    private DialogConfirm dialogConfirm;


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
        ll_private = (LinearLayout) findViewById(R.id.ll_private);
        useremail = (EditText) findViewById(R.id.useremail);
        agree_policy = (CheckBox) findViewById(R.id.agree_policy);
        booking_save_point = (LinearLayout) findViewById(R.id.booking_save_point);
        tv_total_discount_point = (TextView) findViewById(R.id.tv_total_discount_point);
        btn_point_ok = (Button) findViewById(R.id.btn_point_ok);
        tv_title_bar = (TextView) findViewById(R.id.tv_title_bar);
        btn_phone = (LinearLayout) findViewById(R.id.btn_phone);
        paytype3_txt = (TextView) findViewById(R.id.paytype3_txt);

        cookie = _preferences.getString("userid", null);

        if(cookie == null){
            tv_title_bar.setText(getResources().getString(R.string.login_not_user_title));
            findViewById(R.id.ll_coupon_title).setVisibility(View.GONE);
        }
        else{
            tv_title_bar.setText(getResources().getString(R.string.reservation_title));
        }

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

        btn_point_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(point_discount.getText().toString()) && point_discount.getText().toString().length() <= 8 && Integer.parseInt(point_discount.getText().toString()) >= 1000 ) {
                    if(reserve_money < Integer.parseInt(point_discount.getText().toString())){
                        point_discount.setText(reserve_money+"");
//                        Toast.makeText(ReservationActivity.this, "적립금을 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                        return;
                    }
                    if(sale_price < Integer.parseInt(point_discount.getText().toString())){
                        point_discount.setText(sale_price+"");
//                        Toast.makeText(ReservationActivity.this, "적립금을 확인해 주세요.", Toast.LENGTH_SHORT).show();
//                        return;
                    }

                    if(!is_sel_point) {
                        save_price = save_price + Integer.parseInt(point_discount.getText().toString());
                        int m_total = sale_price - save_price;
                        tv_discount_price.setText("-" + nf.format(save_price) + "원");
                        tv_total_price.setText(nf.format(m_total) + "원");
                        is_sel_point = true;
                        is_sel_coupon = false;
                        setSavePoint("", point_discount.getText().toString());
                        point_discount.clearFocus();
                    }
                }
                else{
                    if(!TextUtils.isEmpty(point_discount.getText().toString()) && point_discount.getText().toString().length() <= 8 && Integer.parseInt(point_discount.getText().toString()) >= 1000 ) {
                        Toast.makeText(ReservationActivity.this, "최소 1,000원부터 사용 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ReservationActivity.this, "적립금을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

                // 키보드 숨김
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(point_discount.getWindowToken(), 0);

                point_discount.clearFocus();
            }
        });

        btn_point_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reserve_money > sale_price){
                    point_discount.setText(sale_price-private_money+"");
                    return;
                }else if(reserve_money < sale_price && reserve_money>=1000){
                    point_discount.setText(reserve_money-private_money+"");
                    return;
                }
                point_discount.setText(reserve_money-private_money+"");
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
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP && point_discount.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                    if(event.getRawX() >= (point_discount.getRight() - point_discount.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        point_discount.setText("");
                        save_price = private_money;
                        tv_discount_price.setText("-"+nf.format(save_price) +"원");
                        tv_total_price.setText(nf.format(sale_price - save_price)+"원");
                        is_sel_point = false;
                        is_sel_coupon = false;
                        setSavePoint("","");
                        point_discount.clearFocus();
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
        mPage = intent.getStringExtra("page") == null ? "" : intent.getStringExtra("page");
        if(mPage.equals("Private")){
            ll_private.setVisibility(View.VISIBLE);
            accepted_price = intent.getIntExtra("accepted_price",0);
            bid_id = intent.getStringExtra("bid_id");
        }

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SpannableStringBuilder builder = new SpannableStringBuilder(getResources().getText(R.string.paytype3_info_message));
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple)), 16, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        paytype3_txt.setText(builder);

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
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url;

        if (ec_date != null && ee_date != null)
            url = CONFIG.reserveUrl + "/" + pid + "?ec_date=" + ec_date + "&ee_date=" + ee_date + "&consecutive=Y";
        else
            url = CONFIG.reserveUrl + "/" + pid;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    boolean isPrivate = false;
                    JSONObject obj = new JSONObject(body);
                    if (!obj.getString("result").equals("success")) {
                        dialogAlert = new DialogAlert(
                                getString(R.string.alert_notice),
                                obj.getString("msg"),
                                ReservationActivity.this,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogAlert.dismiss();
                                        finish();
                                    }
                                });
                        dialogAlert.setCancelable(false);
                        dialogAlert.show();

                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }
                    JSONObject data = obj.getJSONObject("data");
                    hotel_name = data.getString("hotel_name");
                    // 룸정보
                    tv_hotel_name.setText(data.getString("hotel_name"));
                    Ion.with(img_room).load(data.getString("room_img"));
                    tv_room_detail_price.setText(Util.numberFormat(data.getInt("sale_price")));
                    tv_detail3.setText(data.getString("room_name"));

                    if(data.getString("sale_rate").equals("0")){
                        tv_detail_per.setVisibility(View.GONE);
                    }
                    else{
                        tv_detail_per.setVisibility(View.VISIBLE);
                    }

                    tv_detail_per.setText(data.getString("sale_rate")+"%↓");
                    checkin_date = data.getString("checkin_date");
                    checkout_date = data.getString("checkout_date");
                    sale_available = data.getBoolean("sale_available");
                    sale_price = data.getInt("sale_price");
                    hid = data.getString("hotel_id");
                    real_price = data.getInt("normal_price");
                    if(data.has("reserve_money"))
                        reserve_money = data.getInt("reserve_money");

                    TextView private_discount = (TextView) findViewById(R.id.private_discount);
                    if(mPage.equals("Private")) {
                        ll_private.setVisibility(View.VISIBLE);
                        //프라이빗 할인금액
                        isPrivate = true;
                        int private_sale_price = sale_price - accepted_price;
                        private_money = private_sale_price;
                        private_discount.setText("-"+Util.numberFormat(private_money) + "원");
                        save_price = private_money;
                    }
                    else {
                        ll_private.setVisibility(View.GONE);
                        save_price = 0;
                    }

                    if(reserve_money >=1000){
                        btn_point_total.setVisibility(View.VISIBLE);
                    }
                    else{
                        btn_point_total.setVisibility(View.GONE);
                    }

                    btn_phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm = new DialogConfirm(
                                    getString(R.string.alert_notice),
                                    getString(R.string.wanna_call_hotelnow)+"\n운영시간 : "+CONFIG.operation_time,
                                    getString(R.string.alert_no),
                                    getString(R.string.alert_connect),
                                    ReservationActivity.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogConfirm.dismiss();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ReservationActivity.this.startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + CONFIG.csPhoneNum)));
                                            dialogConfirm.dismiss();
                                        }
                                    });
                            dialogConfirm.setCancelable(false);
                            dialogConfirm.show();
                        }
                    });

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

                                        // 번호 비활성화
                                        setUseableEditText(pnum2, false);
                                        setUseableEditText(pnum3, false);
                                        setUseableSpinner(pnum1, false);

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

                    // 금액
                    tv_discount_price.setText("-"+nf.format(save_price)+"원");
                    tv_real_price.setText(nf.format(sale_price)+"원");
                    tv_total_price.setText(nf.format(sale_price - private_money)+"원");

                    // 적립금 포인트 지급관련
                    LinearLayout ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);
                    if(cookie != null) {
                        // 쿠폰
                        if(data.has("promotion_code")) {
                            JSONArray pcodes = data.getJSONArray("promotion_code");

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
                                    if(i == 0) {
                                        all_coupon_id = tobj.getString("id");
                                    }
                                    else{
                                        all_coupon_id += ","+tobj.getString("id");
                                    }
                                    coupon_txt_arr.add(tobj.getString("promotion_name") +" (" + tobj.getString("promotion_price") + "원)");
                                    coupon_price.add(Integer.valueOf(tobj.getString("promotion_price")));
                                }
                                setSavePoint("", "");
                            }
                            else{
                                ll_coupon.setVisibility(View.GONE);
                                setSavePoint("","");
                            }
                        }
                        else {
                            setSavePoint("","");
                        }
                    }
                    else{
                        booking_save_point.setVisibility(View.GONE);
                        ll_coupon.setVisibility(View.GONE);
//                        setSavePoint("","");
                    }

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
                        pay_pager.setCurrentItem(0);
                        pay_pager.setOffscreenPageLimit(3);
                        pay_pager.setPageMargin(3);

//                        setTimer();
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
                                if(i == 0)
                                {
                                    View t = new View(ReservationActivity.this);
                                    t.setBackgroundResource(R.color.bg_background);
                                    t.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                                    sub_products.addView(t);
                                }
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

                    // 결제 정보
//                    paytype0_background = (LinearLayout) findViewById(R.id.paytype0_background);
                    paytype1_background = (LinearLayout) findViewById(R.id.paytype1_background);
                    paytype5_background = (LinearLayout) findViewById(R.id.paytype5_background);
                    paytype4_background = (LinearLayout) findViewById(R.id.paytype4_background);
                    paytype2_background = (LinearLayout) findViewById(R.id.paytype2_background);
                    paytype3_background = (LinearLayout) findViewById(R.id.paytype3_background);
//                    tv_paytype0= (TextView) findViewById(R.id.tv_paytype0);
                    tv_paytype1= (TextView) findViewById(R.id.tv_paytype1);
                    tv_paytype2= (TextView) findViewById(R.id.tv_paytype2);
                    tv_paytype3= (TextView) findViewById(R.id.tv_paytype3);
                    tv_paytype4= (TextView) findViewById(R.id.tv_paytype4);
                    tv_paytype5= (TextView) findViewById(R.id.tv_paytype5);
//                    img_paytype0= (ImageView) findViewById(R.id.img_paytype0);
                    img_paytype1= (ImageView) findViewById(R.id.img_paytype1);
                    img_paytype2= (ImageView) findViewById(R.id.img_paytype2);
                    img_paytype3= (ImageView) findViewById(R.id.img_paytype3);
                    img_paytype4= (ImageView) findViewById(R.id.img_paytype4);
                    img_paytype5= (ImageView) findViewById(R.id.img_paytype5);

                    paytype3_info = (LinearLayout) findViewById(R.id.paytype3_info);
                    paytype0_list = (LinearLayout) findViewById(R.id.paytype0_list);
//                    paytype0_count = (TextView) findViewById(R.id.paytype0_count);
                    if(_preferences.getString("userid", null) != null && data.has("billkeys") && !data.getString("billkeys").equals("null")) {
                        JSONArray billkeys = data.getJSONArray("billkeys");
                        if (billkeys.length() > 0) {
//                            paytype0_count.setText(billkeys.length()+"");
                            billmap = new HashMap<Integer, JSONObject>();
                            for (int i = 0; i < billkeys.length(); i++) {
                                JSONObject tmp = billkeys.getJSONObject(i);
                                billmap.put(i, tmp);
                            }
                        }
//                        paytype0_background.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                setPayItem(4);
//                            }
//                        });
                    }
                    else {
//                        paytype0_background.setBackgroundResource(R.drawable.style_edittext_gray_border_gray_bg);
//                        tv_paytype0.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.graytxt));
//                        img_paytype0.setBackgroundResource(R.drawable.ico_pay_direct_no);
//                        paytype0_count.setVisibility(View.GONE);
//
//                        paytype0_background.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                setPayItem(4);
//                            }
//                        });
                    }

                    paytype1_background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPayItem(1);
                        }
                    });

                    paytype5_background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPayItem(6);
                        }
                    });

                    paytype4_background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPayItem(5);
                        }
                    });

                    paytype2_background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPayItem(2);
                        }
                    });

                    paytype3_background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPayItem(3);
                        }
                    });


                    // 결제 하기
                    if (sale_available == false) {
                        btn_go_payment.setBackgroundColor(Color.parseColor("aaaaaa"));
                        btn_go_payment.setText(getString(R.string.sell_complete));
                        btn_go_payment.setClickable(false);
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

                            if (cookie == null && useremail.getText().toString().trim().length() <= 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.validator_email), Toast.LENGTH_SHORT).show();
                                useremail.requestFocus();
                                return;
                            }

                            if (cookie == null && (useremail.getText().toString().contains("@") != true || useremail.getText().toString().contains(".") != true || !Util.isValidEmail(useremail.getText().toString()))) {
                                Toast.makeText(getApplicationContext(), getString(R.string.validator_email_invalid), Toast.LENGTH_SHORT).show();
                                useremail.requestFocus();
                                return;
                            }

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

                            if(_preferences.getString("userid", null) == null && TextUtils.isEmpty(useremail.getText())){
                                Toast.makeText(getApplicationContext(), getString(R.string.validator_email), Toast.LENGTH_SHORT).show();
                                useremail.requestFocus();
                                flag_btn_clicked = false;
                                return;
                            }

                            if (agree_policy.isChecked() != true) {

                                dialogAlert = new DialogAlert(
                                        getString(R.string.alert_notice),
                                        getString(R.string.need_caution_agree),
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

                            Calendar cal = Calendar.getInstance();
                            Calendar calDawn = Calendar.getInstance();
                            cal.add(Calendar.DATE, -1);
                            Date dt = cal.getTime();
                            Date dtDawn = calDawn.getTime();
                            SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
                            SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                            if ((CurHourFormat.format(dt).equals("00") || CurHourFormat.format(dt).equals("01")) && checkin_date.startsWith(CurDateFormat.format(dt))) {
                                String msg = "현재 선택하신 상품은 지금 바로 체크인을 하셔야 하는 상품입니다.<br/>내일 밤(" + CurDateFormat.format(dtDawn) + ")부터 투숙을 원하실 경우 날짜를 다시 선택해주세요. 현재 선택하신 상품을 결제하시겠습니까?";

                                bdialog = new DialogBookingCaution(ReservationActivity.this, okClickListener, skipClickListener);
                                bdialog.setCancelable(false);
                                bdialog.show();
                                TextView caution_txt = (TextView) bdialog.findViewById(R.id.caution_txt);
                                caution_txt.setText(Html.fromHtml(msg));

                            } else if ((CurHourFormat.format(dt).equals("02") ||
                                    CurHourFormat.format(dt).equals("03") ||
                                    CurHourFormat.format(dt).equals("04") ||
                                    CurHourFormat.format(dt).equals("05")) && checkin_date.startsWith(CurDateFormat.format(dtDawn))) {

                                String msg = "현재 선택하신 상품은 당일 오후에 체크인하는 상품입니다. 예약을 진행하시겠습니까?";

                                bdialog = new DialogBookingCaution(ReservationActivity.this, okClickListener, skipClickListener);
                                bdialog.setCancelable(false);
                                bdialog.show();
                                TextView caution_txt = (TextView) bdialog.findViewById(R.id.caution_txt);
                                caution_txt.setText(Html.fromHtml(msg));

                            } else {
                                if (paytype == 4) {
                                    showSignPop();
                                } else {
                                    goPayment();
                                }
                            }
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
                    Spannable fee_tmp = new SpannableString(Html.fromHtml(data.getString("fee").replaceAll("\n", "<br>").replaceAll("-", "ㆍ")));
                    Linkify.addLinks(fee_tmp, Patterns.PHONE, "tel:", Util.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);
                    fee_text.setMovementMethod(CustomLinkMovementMethod.getInstance());
                    fee_text.setText(fee_tmp);
                    cancel_fee_str = data.getString("fee");
                    cancel_fee_str = cancel_fee_str + "\n\n- <font color=#ff0000>체크아웃</font> : " + Util.formatchange(checkout_date.substring(0,10));

                    agree_policy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {

                                dialogFee = new DialogFee(
                                        getString(R.string.cancel_rule),
                                        cancel_fee_str.replaceAll("\n", "<br>"),
                                        getString(R.string.not_agree),
                                        getString(R.string.agree),
                                        ReservationActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                agree_policy.setChecked(false);
                                                dialogFee.dismiss();
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                agree_policy.setChecked(true);
                                                dialogFee.dismiss();
                                            }
                                        },
                                        true);
                                dialogFee.setCancelable(false);
                                dialogFee.show();
                            }
                        }
                    });

                    if(ll_private.getVisibility() == View.VISIBLE || ll_point.getVisibility() == View.VISIBLE || ll_coupon.getVisibility() == View.VISIBLE) {
                        findViewById(R.id.include_reservation_discount).setVisibility(View.VISIBLE);
                    }
                    else {
                        findViewById(R.id.include_reservation_discount).setVisibility(View.GONE);
                    }

                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    LogUtil.e("xxxx", e.getMessage());
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

    private void setSavePoint(String apply_coupon_id, final String apply_reserve_money){
        Api.get(CONFIG.save_point+"/"+hid+"?sale_price="+sale_price+"&coupon_ids="+all_coupon_id+"&apply_coupon_id="+apply_coupon_id+"&checkin_date="+ec_date+"&checkout_date="+ee_date+"&privatedeal_money="+private_money+"&apply_reserve_money="+apply_reserve_money, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                booking_save_point.setVisibility(View.GONE);
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

                    booking_save_point.setVisibility(View.VISIBLE);
                    String t_save_point = nf.format(obj.getInt("buy_reserve_money")) + "원 적립 예정(체크아웃 이후 지급)";
                    SpannableStringBuilder builder = new SpannableStringBuilder(t_save_point);
                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.redtext)), 0, nf.format(obj.getInt("buy_reserve_money")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_total_discount_point.setText(builder);


                    // 쿠폰
                    if(obj.has("coupons")) {
                        JSONArray pcodes = obj.getJSONArray("coupons");
                        LinearLayout ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);
                        if (pcodes.length() > 0) {
                            ll_coupon.setVisibility(View.VISIBLE);

                            tv_coupon_title = (TextView) findViewById(R.id.tv_coupon_title);

                            if(!is_sel_coupon) {
                                coupon_arr = new HashMap<Integer, String>();
                                coupon_price = new ArrayList<Integer>();
                                coupon_avail = new ArrayList<String>();
                                final ArrayList<String> coupon_txt_arr = new ArrayList<String>();

                                coupon_arr.put(0, "0");
                                coupon_txt_arr.add("사용안함");
                                coupon_price.add(0);
                                coupon_avail.add("Y");
                                int use_coupon = 0;
                                for (int i = 0; i < pcodes.length(); i++) {
                                    JSONObject tobj = pcodes.getJSONObject(i);
                                    if(tobj.getString("avail").equals("Y")) {
                                        use_coupon++;
                                        coupon_arr.put(i + 1, tobj.getString("id"));
                                        coupon_txt_arr.add(tobj.getString("promotion_name") + " (" + tobj.getString("promotion_price") + "원)");
                                        coupon_price.add(Integer.valueOf(tobj.getString("promotion_price")));
                                        coupon_avail.add(tobj.getString("avail"));
                                    }

                                    if (i == 0) {
                                        all_coupon_id = tobj.getString("id");
                                    } else {
                                        all_coupon_id += "," + tobj.getString("id");
                                    }
                                }

                                String s_coupon = getResources().getString(R.string.reservation_coupon) + "(" + use_coupon + " 장)";
                                SpannableStringBuilder builder1 = new SpannableStringBuilder(s_coupon);
                                builder1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.redtext)), 6, s_coupon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                tv_coupon_title.setText(builder1);
                                if(use_coupon == 0){
                                    coupon_txt_arr.clear();
                                    coupon_txt_arr.add("사용 가능한 쿠폰이 없습니다.");
                                    coupon_spinner.setEnabled(false);
                                }
                                else{
                                    coupon_spinner.setEnabled(true);
                                }

                                ArrayAdapter<String> coupon_adapter = new ArrayAdapter<String>(ReservationActivity.this, R.layout.layout_spinner_item, coupon_txt_arr){
                                    @Override
                                    public boolean isEnabled(int position) {
                                        if(coupon_avail.get(position).equals("Y")){

                                            return true;
                                        }
                                        else {
                                            return false;
                                        }
                                    }

                                    @Override
                                    public View getDropDownView(int position, View convertView,
                                                                ViewGroup parent) {
                                        View view = super.getDropDownView(position, convertView, parent);
                                        TextView tv = (TextView) view;
                                        if(coupon_avail.get(position).equals("N")){
                                            // Set the disable item text color
                                            tv.setBackgroundResource(R.color.footerview);
                                            tv.setTextColor(getResources().getColor(R.color.graytxt));
                                        }
                                        else {
                                            tv.setBackgroundResource(R.color.white);
                                            tv.setTextColor(getResources().getColor(R.color.blacktxt));
                                        }
                                        return view;
                                    }
                                };

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
                                        coupon_value = coupon_price.get(position);
                                        pcode = coupon_arr.get(position);

                                        int final_price = 0;
                                        int final_save = 0;

                                        final_save = (save_price + coupon_value);

                                        final_price = sale_price - final_save;

                                        if (coupon_value == 0) {
                                            if (parentView.getChildAt(0) != null)
                                                ((TextView) parentView.getChildAt(0)).setText(coupon_txt_arr.get(0));
                                        } else {
                                            if (parentView.getChildAt(0) != null)
                                                ((TextView) parentView.getChildAt(0)).setText(getString(R.string.price, nf.format(coupon_value)));
                                        }

                                        tv_discount_price.setText("-" + nf.format(final_save) + "원");
                                        tv_total_price.setText(nf.format(final_price) + "원");
                                        is_sel_coupon = true;
                                        setSavePoint(pcode, apply_reserve_money);

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }
                                });
                            }
                        }
                        else{
                            ll_coupon.setVisibility(View.GONE);
                        }
                    }

                }
                catch (JSONException e){
                    booking_save_point.setVisibility(View.GONE);
                    Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private View.OnClickListener skipSignClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            flag_btn_clicked = false;
            dialogBillingAlert.dismiss();
        }
    };

    private View.OnClickListener skipClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            flag_btn_clicked = false;
            bdialog.dismiss();
        }
    };

    private View.OnClickListener okClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(paytype == 4) {
                showSignPop();
            } else {
                goPayment();
            }

            bdialog.dismiss();
        }
    };

    private View.OnClickListener signCompleteListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(dialogBillingAlert.hasSign == true) {
                Bitmap bm = dialogBillingAlert.getBitmap();

                if (bm != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, 120, 80, false);
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);

                    byte [] b = baos.toByteArray();
                    bmpStr = "data:image/png;base64,"+ Base64.encodeToString(b, Base64.DEFAULT);

                    goPayment();

                    dialogBillingAlert.dismiss();
                } else {
                    Toast.makeText(ReservationActivity.this, "서명을 하셔야 결제를 진행 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ReservationActivity.this, "서명을 하셔야 결제를 진행 할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showSignPop() {
        dialogBillingAlert = new DialogBillingAlert(ReservationActivity.this, signCompleteListner, skipSignClickListener);
        dialogBillingAlert.selected_card = selected_card;
        dialogBillingAlert.setCancelable(false);
        dialogBillingAlert.show();
    }

    private void setPayItem(int mPayType){
        if(mPayType == 1){
            paytype1_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
            tv_paytype1.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.purple));;
            img_paytype1.setBackgroundResource(R.drawable.ico_pay_card_selected);
        }else{
            paytype1_background.setBackgroundResource(R.drawable.style_edittext_gray_border);
            tv_paytype1.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.blacktxt));;
            img_paytype1.setBackgroundResource(R.drawable.ico_pay_card);
        }
        if (mPayType == 2) {
            paytype2_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
            tv_paytype2.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.purple));;
            img_paytype2.setBackgroundResource(R.drawable.ico_pay_phone_selected);
        }else{
            paytype2_background.setBackgroundResource(R.drawable.style_edittext_gray_border);
            tv_paytype2.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.blacktxt));;
            img_paytype2.setBackgroundResource(R.drawable.ico_pay_phone);
        }
        if (mPayType == 3) {
            paytype3_info.setVisibility(View.VISIBLE);
            paytype3_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
            tv_paytype3.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.purple));;
            img_paytype3.setBackgroundResource(R.drawable.ico_pay_ars_selected);
        }else{
            paytype3_info.setVisibility(View.GONE);
            paytype3_background.setBackgroundResource(R.drawable.style_edittext_gray_border);
            tv_paytype3.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.blacktxt));;
            img_paytype3.setBackgroundResource(R.drawable.ico_pay_ars);
        }
        if (mPayType == 4) {
            paytype0_list.removeAllViews();
            paytype0_list.setVisibility(View.VISIBLE);
            RadioGroup rg = new RadioGroup(this);
            rg.setOrientation(LinearLayout.VERTICAL);
            if(billmap != null && billmap.size() > 0) {
                for(int i = 0; i<billmap.size(); i++){
                    try {
                        JSONObject selobj = billmap.get(i);
                        final RadioButton rb = new RadioButton(this);
                        rb.setText(selobj.getString("cardnm") +"    "+ selobj.getString("cardno"));
                        rb.setTextSize(12);
                        rb.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.termtext));
                        rb.setPadding(32, 20, 0, 20);
                        rb.setButtonDrawable(R.drawable.radio_icon);
                        rb.setId(selobj.getInt("id"));
                        rg.setTag(selobj.getString("id"));
                        if (i == 0) {
                            selected_card = selobj.getString("id");
                        }
                        rg.addView(rb);
                        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                LogUtil.e("xxxxx", checkedId+"");
                                selected_card = checkedId+"";
                            }
                        });
                    } catch (Exception e) {
                    }
                }
                paytype0_list.addView(rg);
//                paytype0_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
//                tv_paytype0.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.purple));;
//                img_paytype0.setBackgroundResource(R.drawable.ico_pay_direct_selected);
            }
            else{
                View child = getLayoutInflater().inflate(R.layout.layout_paytype0_info, null);
                paytype0_list.addView(child);
            }
//            paytype0_count.setVisibility(View.GONE);
        }else{
            if(billmap != null && billmap.size() > 0) {
//                paytype0_background.setBackgroundResource(R.drawable.style_edittext_gray_border);
//                tv_paytype0.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.blacktxt));
//                img_paytype0.setBackgroundResource(R.drawable.ico_pay_direct);
//                paytype0_count.setVisibility(View.VISIBLE);
            }
            else {
//                paytype0_background.setBackgroundResource(R.drawable.style_edittext_gray_border_gray_bg);
//                tv_paytype0.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.graytxt));
//                img_paytype0.setBackgroundResource(R.drawable.ico_pay_direct_no);
//                paytype0_count.setVisibility(View.GONE);
            }
            paytype0_list.setVisibility(View.GONE);

        }
        if (mPayType == 5) {
            paytype4_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
            tv_paytype4.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.purple));;
            img_paytype4.setBackgroundResource(R.drawable.ico_pay_account_selected);
        }else{
            paytype4_background.setBackgroundResource(R.drawable.style_edittext_gray_border);
            tv_paytype4.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.blacktxt));;
            img_paytype4.setBackgroundResource(R.drawable.ico_pay_account);
        }
        if (mPayType == 6) {
            paytype5_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
            tv_paytype5.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.purple));;
            img_paytype5.setBackgroundResource(R.drawable.ico_pay_payco_selected);
        }else{
            paytype5_background.setBackgroundResource(R.drawable.style_edittext_gray_border);
            tv_paytype5.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.blacktxt));;
            img_paytype5.setBackgroundResource(R.drawable.ico_pay_payco);
        }

        paytype = mPayType;
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
                        setUseableEditText(pnum2, true);
                        setUseableEditText(pnum3, true);
                        setUseableSpinner(pnum1, true);
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
                    Toast.makeText(ReservationActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

                setUseableEditText(pnum2, true);
                setUseableEditText(pnum3, true);
                setUseableSpinner(pnum1, true);
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

    private void goPayment(){
        String phone_number = "";
        String user_name = "";
        if(is_sel_point) {
            reserve_value = point_discount.getText().toString();
        }
        if(is_other_user) { // 다른유저
            phone_number = other_pnum1.getSelectedItem() + "-" + other_pnum2.getText().toString() + "-" + other_pnum3.getText().toString();
            user_name = other_username.getText().toString();
        }
        else{
            phone_number = pnum1.getSelectedItem() + "-" + pnum2.getText().toString() + "-" + pnum3.getText().toString();
            user_name = usernameInput.getText().toString();
        }

        JSONObject params = new JSONObject();
        try{
            String locLat = "";
            String locLng = "";
            params.put("pid", pid);
            params.put("un", Base64.encodeToString(user_name.getBytes(), Base64.NO_WRAP));
            params.put("up", Base64.encodeToString(phone_number.getBytes(),Base64.NO_WRAP));
            params.put("pcode", pcode);
            params.put("reserve_money", reserve_value);
            params.put("imgstr", bmpStr);
            params.put("em", useremail.getText());
            params.put("privatedeal_bid", bid_id);
            params.put("privatedeal_money", private_money);
            params.put("phone_auth", is_auth);
            String other_booker = is_other_user ? "Y":"N";
            params.put("other_booker", other_booker);

            if(Util.userLocation != null) {
                locLat = String.valueOf(Util.userLocation.getLatitude());
                locLng = String.valueOf(Util.userLocation.getLongitude());
            } else {
                locLat = "37.506292";
                locLng = "127.053612";
            }

            params.put("uuid", Util.getAndroidId(ReservationActivity.this));
            params.put("lat", locLat);
            params.put("lng", locLng);
            params.put("version", Util.getAppVersionName(ReservationActivity.this));
            params.put("os", "a");

            if(ec_date != null && ee_date != null){
                params.put("ec_date", ec_date);
                params.put("ee_date", ee_date);
                params.put("consecutive", "Y");
            }
        } catch (JSONException e) {}

        Api.post(CONFIG.bookingReserveUrl, params.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReservationActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        flag_btn_clicked = false;
                        return;
                    }

                    // 카드면 카드웹뷰로
                    flag_btn_clicked = false;
                    Intent intent = new Intent(ReservationActivity.this, PaymentActivity.class);
                    intent.putExtra("paytype", paytype);
                    intent.putExtra("bid", obj.getString("bid"));
                    intent.putExtra("hotel_name", hotel_name);
                    intent.putExtra("pid", pid);
                    intent.putExtra("selected_card", selected_card);
                    intent.putExtra("uname", usernameInput.getText().toString());
                    intent.putExtra("uphone", pnum1.getSelectedItem()+"-"+pnum2.getText().toString()+"-"+pnum3.getText().toString());
                    if(paytype == 6) {
                        intent.putExtra("is_payco", true);
                    }
                    if(coupon_spinner != null && coupon_spinner.getSelectedItem() != null && !TextUtils.isEmpty(coupon_spinner.getSelectedItem().toString()) && !coupon_spinner.getSelectedItem().toString().equals("사용안함"))
                        intent.putExtra("coupon_name", coupon_spinner.getSelectedItem().toString());

                    float revenue = obj.has("revenue")? (float)obj.getInt("revenue"):0;
                    int quantity = obj.has("quantity")? obj.getInt("quantity"):0;

                    // Tune
                    TuneWrap.Reservation(revenue, quantity, obj.getString("bid"), checkin_date, checkout_date);

                    startActivityForResult(intent,80);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                } catch (JSONException e) {
                    Log.e(CONFIG.TAG, e.toString());

                    Toast.makeText(getApplicationContext(), getString(R.string.error_booking_fail), Toast.LENGTH_SHORT).show();
                    flag_btn_clicked = false;
                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 80 && resultCode == 80){
            setResult(100);
            finish();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            Rect outRect = new Rect();
            if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                if(v != null) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        }
        return super.dispatchTouchEvent(event);
    }
}
