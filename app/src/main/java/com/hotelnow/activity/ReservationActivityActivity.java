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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.ReservationPagerAdapter;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogBillingAlert;
import com.hotelnow.dialog.DialogBookingCaution;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.dialog.DialogFee;
import com.hotelnow.fragment.model.TicketSelEntry;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
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
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReservationActivityActivity extends Activity {

    private EditText point_discount, usernameInput, pnum2, pnum3, other_pnum2, other_pnum3, auth_string, other_username;
    private String pid, accepted_price, cookie, pcode = "0", hid;
    private String t_id = "", t_name="", option_id="", option_cnt ="";
    private SharedPreferences _preferences;
    private LinearLayout layout_useremail;
    private TextView tv_hotel_name,auth_ok, tv_auth_change, btn_auth;
    private AutoLinkTextView fee_text;
    private ImageView img_room;
    private String[] hphone;
    private Spinner pnum1, other_pnum1;
    private String is_auth = "N";
    private TextView auth_count, remain_count;
    private CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 180 * 1000; //총 시간 (300초 = 5분)
    final int COUNT_DOWN_INTERVAL = 1000; //onTick 메소드를 호출할 간격 (1초)
    private CheckBox other_checkbox;
    private LinearLayout layout_orther_user;
    private boolean is_other_user = false;
    private Button btn_go_payment;
    private Boolean sale_available = false;
    private Boolean flag_btn_clicked = false;
    private EditText useremail;
    private DialogAlert dialogAlert;
    private int paytype = 0;
    private int sale_price;
    private LinearLayout ll_point;
    private TextView tv_coupon_title;
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
    int nowPosition = 0, coupon_value=0, selectdCouponIdx = 0;
    TableLayout sub_products;
    private LinearLayout ll_private;
    String mPage = ""; // nomal, private
    private LinearLayout paytype1_background, paytype3_background, paytype5_background, paytype3_info, booking_save_point, btn_phone;
    private LinearLayout paytype0_list;
    private TextView tv_paytype1, tv_paytype3, tv_paytype5, tv_title_bar, paytype3_txt;
    private ImageView img_paytype1, img_paytype3, img_paytype5;
    private HashMap<Integer, JSONObject> billmap;
    private CheckBox agree_policy;
    private DialogBookingCaution bdialog;
    private DialogBillingAlert dialogBillingAlert;
    private String bmpStr = "";
    String selected_card = "", bid_id="";
    int reserve_value = 0;
    private String hotel_name, city;
    private boolean is_point_ok = false;
    private LinearLayout layout_info;
    private ArrayList<TicketSelEntry> sel_items;
    private DialogFee dialogFee;
    private String cancel_fee_str="";
    private Spanned cancel_fee;
    private LinearLayout rl_all_point;
    private LinearLayout ll_coupon;
    private int save_price = 0;
    private DialogConfirm dialogConfirm;
    private TextView real_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_activity);

        TuneWrap.Event("reservation_activity");

        _preferences = PreferenceManager.getDefaultSharedPreferences(ReservationActivityActivity.this);
        point_discount = (EditText) findViewById(R.id.point_discount);
        usernameInput = (EditText) findViewById(R.id.username);
        pnum2 = (EditText) findViewById(R.id.pnum2);
        pnum3 = (EditText) findViewById(R.id.pnum3);
        other_pnum2 = (EditText) findViewById(R.id.other_pnum2);
        other_pnum3 = (EditText) findViewById(R.id.other_pnum3);
        layout_useremail = (LinearLayout) findViewById(R.id.layout_useremail);
        tv_hotel_name = (TextView) findViewById(R.id.tv_hotel_name);
        tv_auth_change = (TextView) findViewById(R.id.tv_auth_change);
        auth_ok = (TextView) findViewById(R.id.auth_ok);
        btn_auth = (TextView) findViewById(R.id.btn_auth);
        auth_string = (EditText) findViewById(R.id.auth_string);
        auth_count = (TextView) findViewById(R.id.auth_count);
        remain_count = (TextView) findViewById(R.id.remain_count);
        other_checkbox = (CheckBox) findViewById(R.id.other_checkbox);
        fee_text = (AutoLinkTextView) findViewById(R.id.fee_text);
        layout_orther_user = (LinearLayout) findViewById(R.id.layout_orther_user);
        btn_go_payment = (Button) findViewById(R.id.btn_go_payment);
        other_username = (EditText) findViewById(R.id.other_username);
        img_room = (ImageView) findViewById(R.id.img_room);
        pnum1 = (Spinner)findViewById(R.id.pnum1);
        other_pnum1 = (Spinner)findViewById(R.id.other_pnum1);
        ll_point = (LinearLayout)findViewById(R.id.ll_point);
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
        layout_info = (LinearLayout) findViewById(R.id.layout_info);
        rl_all_point = (LinearLayout) findViewById(R.id.rl_all_point);
        ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);
        tv_title_bar = (TextView) findViewById(R.id.tv_title_bar);
        btn_phone = (LinearLayout) findViewById(R.id.btn_phone);
        real_title = (TextView) findViewById(R.id.real_title);
        paytype3_txt = (TextView) findViewById(R.id.paytype3_txt);

        real_title.setText("상품가");

        cookie = _preferences.getString("userid", null);

        if(cookie == null){
            tv_title_bar.setText(getResources().getString(R.string.login_not_user_title));
            findViewById(R.id.ll_coupon_title).setVisibility(View.GONE);
        }
        else{
            tv_title_bar.setText(getResources().getString(R.string.reservation_title));
        }

        ll_private.setVisibility(View.GONE);
        ll_point.setVisibility(View.GONE);
        rl_all_point.setVisibility(View.GONE);
        booking_save_point.setVisibility(View.GONE);
        findViewById(R.id.activity_info).setVisibility(View.VISIBLE);

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
                    int m_save = save_price + 0;
                    int m_total = sale_price - m_save;
                    tv_discount_price.setText("-"+nf.format(m_save) +"원");
                    tv_total_price.setText(nf.format(m_total)+"원");
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
                    auth_ok.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.purple));
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
                        is_point_ok = false;

                        return true;
                    }
                }
                return false;
            }
        });


        Intent intent = getIntent();
        sel_items = (ArrayList<TicketSelEntry>)intent.getSerializableExtra("sel_list");
        t_id = intent.getStringExtra("tid");
        t_name = intent.getStringExtra("tname");
        if(sel_items == null){
            Toast.makeText(getApplicationContext(), "상품을 다시 선택해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

        for(int i=0; i<sel_items.size(); i++){
            if(sel_items.get(i).getmCnt() != 0 ){
                if(option_id.equals("")) {
                    option_id = sel_items.get(i).getmId();
                    option_cnt = sel_items.get(i).getmCnt()+"";
                }
                else{
                    option_id += ","+sel_items.get(i).getmId();
                    option_cnt += ","+sel_items.get(i).getmCnt();
                }
            }
        }

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.show_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationActivityActivity.this, WebviewActivity.class);
                intent.putExtra("url", CONFIG.info_provide1);
                intent.putExtra("title", getString(R.string.caution_agreement_checkbox));
                startActivity(intent);
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
            paramObj.put("ui", AES256Chiper.AES_Decode(_preferences.getString("userid", null).replace("HN|","")));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
            paramObj.put("ver", Util.getAppVersionName(ReservationActivityActivity.this));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivityActivity.this, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
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
        String url="";
        url = CONFIG.ticketreserveUrl+"?deal_ids="+t_id+"&option_ids="+option_id+"&option_counts="+option_cnt;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    boolean isReserve = false, isCoupon = false, isPrivate = false;
                    JSONObject obj = new JSONObject(body);
                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReservationActivityActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }
                    JSONObject booking_data = obj.getJSONObject("booking_data");
                    JSONArray deals = booking_data.getJSONArray("deals");

                    btn_phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogConfirm = new DialogConfirm(
                                    getString(R.string.alert_notice),
                                    getString(R.string.wanna_call_hotelnow)+"\n운영시간 : "+CONFIG.operation_time,
                                    getString(R.string.alert_no),
                                    getString(R.string.alert_connect),
                                    ReservationActivityActivity.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogConfirm.dismiss();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ReservationActivityActivity.this.startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + CONFIG.csPhoneNum)));
                                            dialogConfirm.dismiss();
                                        }
                                    });
                            dialogConfirm.setCancelable(false);
                            dialogConfirm.show();
                        }
                    });

                    city = booking_data.getString("city_name");

                    if(deals.length() > 0){
                        JSONArray options = deals.getJSONObject(0).getJSONArray("options");
                        String img_url = deals.getJSONObject(0).getString("img_url");

                        hotel_name = t_name;
                        // 룸정보
                        tv_hotel_name.setText(t_name);

                        Ion.with(img_room).load(img_url);
                        layout_info.removeAllViews();
                        for(int i=0; i<options.length(); i++){
                            View ticket_view = LayoutInflater.from(ReservationActivityActivity.this).inflate(R.layout.layout_ticket_reserve, null);
                            TextView txt_cnt = (TextView) ticket_view.findViewById(R.id.txt_cnt);
                            TextView txt_title = (TextView) ticket_view.findViewById(R.id.txt_title);

                            txt_cnt.setText(options.getJSONObject(i).getString("selected_cnt") +"장");
                            txt_title.setText(options.getJSONObject(i).getString("name"));
                            layout_info.addView(ticket_view);
                        }

                        sale_available = booking_data.getBoolean("sale_available");

                        if (sale_available == false) {
                            btn_go_payment.setBackgroundColor(Color.parseColor("#aaaaaa"));
                            btn_go_payment.setText(getString(R.string.sell_complete));
                        }

                        int real_price = booking_data.getInt("total_normal_price");
                        sale_price = booking_data.getInt("total_sale_price");
//                        save_price = real_price - sale_price;

                        TextView fee_text = (TextView)findViewById(R.id.fee_text);
                        Spanned fee_tmp = Html.fromHtml(booking_data.getString("fee").replaceAll("\n", "<br>"));
                        fee_text.setText(fee_tmp);

                        // 예약폼 폰 번호 1
                        String[] phonePrefixs = getResources().getStringArray(R.array.phone_prefix);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationActivityActivity.this, android.R.layout.simple_spinner_dropdown_item, phonePrefixs);
                        pnum1.setAdapter(adapter);
                        other_pnum1.setAdapter(adapter);

                        // 이름, 전번
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
                            findViewById(R.id.other_line).setVisibility(View.GONE);
                            other_checkbox.setVisibility(View.GONE);
                            tv_auth_change.setVisibility(View.GONE);
                            setUseableEditText(usernameInput, true);
                            setUseableEditText(pnum2, true);
                            setUseableEditText(pnum3, true);
                            setUseableSpinner(pnum1, true);
                        }
                        else {// 회원
                            //인증
                            is_auth = booking_data.getString("phone_auth");
                            setUseableEditText(usernameInput, true);
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
                            findViewById(R.id.other_checkbox).setVisibility(View.VISIBLE);
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

//                                if (cookie != null)
//                                    setUseableEditText(usernameInput, false);

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
                                        Toast.makeText(ReservationActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(Map<String, String> headers, String body) {
                                        try {
                                            JSONObject obj = new JSONObject(body);

                                            if (!obj.getString("result").equals("success")) {
                                                Toast.makeText(ReservationActivityActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // 타이머가 종료되었을 때 출력되는 메시지
                                            btn_auth.setText("인증번호 발송");

                                            //인증번호 발급버튼 비활성화
                                            btn_auth.setBackgroundResource(R.drawable.cal_unactive_round);
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
                                            Toast.makeText(ReservationActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
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

                        agree_policy = (CheckBox) findViewById(R.id.agree_policy);
                        agree_policy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {

                                    dialogFee = new DialogFee(
                                            getString(R.string.cancel_rule),
                                            cancel_fee_str.replaceAll("\n", "<br>"),
                                            getString(R.string.not_agree),
                                            getString(R.string.agree),
                                            ReservationActivityActivity.this,
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

                        cancel_fee_str = booking_data.getString("fee");
                        cancel_fee = Html.fromHtml(cancel_fee_str.replaceAll("\n", "<br>"));

                        // 결제관련 이미지
                        if(booking_data.has("pay_promotion_img") && booking_data.getJSONArray("pay_promotion_img").length() > 0) {
                            banner_arr = new ArrayList<String>();
                            pay_adapter = new ReservationPagerAdapter(ReservationActivityActivity.this, banner_arr);
                            pay_pager.setAdapter(pay_adapter);
                            pay_pager.setVisibility(View.VISIBLE);
                            for(int i=0; i<booking_data.getJSONArray("pay_promotion_img").length(); i++) {
                                banner_arr.add(booking_data.getJSONArray("pay_promotion_img").get(i).toString());
                            }
                            pay_adapter.notifyDataSetChanged();
                            pay_pager.setClipToPadding(false);
                            pay_pager.setCurrentItem(0);
                            pay_pager.setOffscreenPageLimit(3);
                            pay_pager.setPageMargin(3);

//                            setTimer();
                        }
                        else{
                            pay_pager.setVisibility(View.GONE);
                        }
                        // 쿠
                        if(booking_data.has("promotion_code")) {
                            JSONArray pcodes = booking_data.getJSONArray("promotion_code");
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

                                ArrayAdapter<String> coupon_adapter = new ArrayAdapter<String>(ReservationActivityActivity.this, R.layout.layout_spinner_item, coupon_txt_arr);
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
                                        selectdCouponIdx = position;

                                        int final_price = sale_price - coupon_value;

                                        if (final_price < 0) {
                                            tv_total_price.setText(getString(R.string.price, nf.format(sale_price - final_price)));
                                        } else {
                                            tv_total_price.setText(getString(R.string.price, nf.format(final_price)));
                                            if(coupon_value == 0) {
                                                if(parentView.getChildAt(0) !=null)
                                                    ((TextView) parentView.getChildAt(0)).setText("사용안함");
                                            } else {
                                                if(parentView.getChildAt(0) !=null)
                                                    ((TextView) parentView.getChildAt(0)).setText(getString(R.string.price, nf.format(coupon_value)));
                                            }
                                        }

                                        tv_discount_price.setText("-"+Util.numberFormat(save_price + coupon_value) + "원");
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                        // your code here
                                    }
                                });
                            }
                            else{
                                ll_coupon.setVisibility(View.GONE);
                                findViewById(R.id.ll_coupon_title).setVisibility(View.GONE);
                            }
                        }
                        else{
                            ll_coupon.setVisibility(View.GONE);
                            findViewById(R.id.ll_coupon_title).setVisibility(View.GONE);
                        }

                        // 금액
                        tv_discount_price.setText("-0원");
                        tv_real_price.setText(nf.format(sale_price)+"원");
                        tv_total_price.setText(nf.format(sale_price)+"원");

                        // 결제 정보
//                        paytype0_background = (LinearLayout) findViewById(R.id.paytype0_background);
                        paytype1_background = (LinearLayout) findViewById(R.id.paytype1_background);
                        paytype5_background = (LinearLayout) findViewById(R.id.paytype5_background);
                        paytype3_background = (LinearLayout) findViewById(R.id.paytype3_background);
//                        tv_paytype0= (TextView) findViewById(R.id.tv_paytype0);
                        tv_paytype1= (TextView) findViewById(R.id.tv_paytype1);
                        tv_paytype3= (TextView) findViewById(R.id.tv_paytype3);
                        tv_paytype5= (TextView) findViewById(R.id.tv_paytype5);
//                        img_paytype0= (ImageView) findViewById(R.id.img_paytype0);
                        img_paytype1= (ImageView) findViewById(R.id.img_paytype1);
                        img_paytype3= (ImageView) findViewById(R.id.img_paytype3);
                        img_paytype5= (ImageView) findViewById(R.id.img_paytype5);

                        paytype3_info = (LinearLayout) findViewById(R.id.paytype3_info);
                        paytype0_list = (LinearLayout) findViewById(R.id.paytype0_list);
//                        paytype0_count = (TextView) findViewById(R.id.paytype0_count);

                        if(_preferences.getString("userid", null) != null && booking_data.has("billkeys") && !booking_data.getString("billkeys").equals("null")) {
                            JSONArray billkeys = booking_data.getJSONArray("billkeys");
                            if (billkeys.length() > 0) {
//                                paytype0_count.setText(billkeys.length()+"");
                                billmap = new HashMap<Integer, JSONObject>();
                                for (int i = 0; i < billkeys.length(); i++) {
                                    JSONObject tmp = billkeys.getJSONObject(i);
                                    billmap.put(i, tmp);
                                }
                            }
//                            paytype0_background.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setPayItem(4);
//                                }
//                            });
                        }
                        else {
//                            paytype0_background.setBackgroundResource(R.drawable.style_edittext_gray_border_gray_bg);
//                            tv_paytype0.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.graytxt));
//                            img_paytype0.setBackgroundResource(R.drawable.ico_pay_direct_no);
//                            paytype0_count.setVisibility(View.GONE);
//
//                            paytype0_background.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    setPayItem(4);
//                                }
//                            });
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

                        paytype3_background.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPayItem(3);
                            }
                        });


                        // 결제 하기
                        if (sale_available == false) {
                            btn_go_payment.setBackgroundColor(Color.parseColor("#aaaaaa"));
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
                                            ReservationActivityActivity.this,
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
                                            "결제방식을 선택하여 주십시오",
                                            ReservationActivityActivity.this,
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
                                            ReservationActivityActivity.this,
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

                                if (paytype == 4) {
                                    showSignPop();
                                } else {
                                    goPayment();
                                }
                            }
                        });
                    }
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    LogUtil.e("xxxx", e.getMessage());
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
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
                    Toast.makeText(ReservationActivityActivity.this, "서명을 하셔야 결제를 진행 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ReservationActivityActivity.this, "서명을 하셔야 결제를 진행 할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showSignPop() {
        dialogBillingAlert = new DialogBillingAlert(ReservationActivityActivity.this, signCompleteListner, skipSignClickListener);
        dialogBillingAlert.selected_card = selected_card;
        dialogBillingAlert.setCancelable(false);
        dialogBillingAlert.show();
    }

    private void setPayItem(int mPayType){
        if(mPayType == 1){
            paytype1_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
            tv_paytype1.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.purple));;
            img_paytype1.setBackgroundResource(R.drawable.ico_pay_card_selected);
        }else{
            paytype1_background.setBackgroundResource(R.drawable.style_edittext_gray_2border);
            tv_paytype1.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.blacktxt));;
            img_paytype1.setBackgroundResource(R.drawable.ico_pay_card);
        }
        if (mPayType == 3) {
            paytype3_info.setVisibility(View.VISIBLE);
            paytype3_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
            tv_paytype3.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.purple));;
            img_paytype3.setBackgroundResource(R.drawable.ico_pay_ars_selected);
        }else{
            paytype3_info.setVisibility(View.GONE);
            paytype3_background.setBackgroundResource(R.drawable.style_edittext_gray_2border);
            tv_paytype3.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.blacktxt));;
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
                        rb.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.termtext));
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
//                tv_paytype0.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.purple));;
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
//                tv_paytype0.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.blacktxt));
//                img_paytype0.setBackgroundResource(R.drawable.ico_pay_direct);
//                paytype0_count.setVisibility(View.VISIBLE);
            }
            else {
//                paytype0_background.setBackgroundResource(R.drawable.style_edittext_gray_border_gray_bg);
//                tv_paytype0.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.graytxt));
//                img_paytype0.setBackgroundResource(R.drawable.ico_pay_direct_no);
//                paytype0_count.setVisibility(View.GONE);
            }
            paytype0_list.setVisibility(View.GONE);

        }
        if (mPayType == 6) {
            paytype5_background.setBackgroundResource(R.drawable.style_edittext_purple_border);
            tv_paytype5.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.purple));;
            img_paytype5.setBackgroundResource(R.drawable.ico_pay_payco_selected);
        }else{
            paytype5_background.setBackgroundResource(R.drawable.style_edittext_gray_2border);
            tv_paytype5.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.blacktxt));;
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
            if(cookie != null) {
                try {
                    paramObj.put("user_id", AES256Chiper.AES_Decode(cookie.replace("HN|","")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                paramObj.put("user_id", cookie);
            }
        }
        catch (JSONException e) {}

        Api.post(CONFIG.phone_auth_check, paramObj.toString(), new Api.HttpCallback() {

            @Override
            public void onFailure(Response response, Exception e) {
                Log.e(CONFIG.TAG, "expection is ", e);
                Toast.makeText(ReservationActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                authReset();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try{
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReservationActivityActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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

//                    if(cookie != null) {
//                        setUseableEditText(usernameInput, false);
//                    }
//                    else{
//                        setUseableEditText(usernameInput, true);
//                    }

                    auth_ok.setVisibility(View.GONE);
                    findViewById(R.id.layout_auth).setVisibility(View.GONE);
                    setUseableEditText(pnum2, false);
                    setUseableEditText(pnum3, false);
                    setUseableSpinner(pnum1, false);

                    tv_auth_change.setText(Html.fromHtml("<u>" + getResources().getString(R.string.phone_change) + "</u>"));

                    // 인증하기 비활성화
                    auth_ok.setBackgroundResource(R.drawable.style_edittext_gray_border);
                    auth_ok.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.coupon_dis));

                    if(countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    is_auth = "Y";
                    Toast.makeText(ReservationActivityActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Log.e(CONFIG.TAG, "expection is ", e);
                    Toast.makeText(ReservationActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
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
                auth_ok.setTextColor(ContextCompat.getColor(ReservationActivityActivity.this, R.color.coupon_dis));
                auth_ok.setClickable(false);

                //인증번호 발급버튼 활성화
                btn_auth.setBackgroundResource(R.drawable.purple_2round);
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
        btn_auth.setBackgroundResource(R.drawable.purple_2round);
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

        setUseableEditText(pnum2, true);
        setUseableEditText(pnum3, true);
        setUseableSpinner(pnum1, true);
    }

    private void setUseableEditText(EditText et, boolean useable) {
        if(et != null) {
            et.setClickable(useable);
            et.setEnabled(useable);
            et.setFocusable(useable);
            et.setFocusableInTouchMode(useable);
            if (useable) {
                et.setBackgroundResource(R.drawable.style_edittext_gray_2border);
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
                sp.setBackgroundResource(R.drawable.style_edittext_gray_2border);
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
                            Util.doRestart(ReservationActivityActivity.this);
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
                Util.doRestart(ReservationActivityActivity.this);
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
        if(is_point_ok) {
            reserve_value = Integer.parseInt(point_discount.getText().toString());
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
            params.put("deal_ids", t_id);
            params.put("un", Base64.encodeToString(user_name.getBytes(), Base64.NO_WRAP));
            params.put("up", Base64.encodeToString(phone_number.getBytes(),Base64.NO_WRAP));
            params.put("option_counts", option_cnt);
            params.put("option_ids", option_id);
            params.put("reserve_money", reserve_value);
            params.put("pcode", pcode);
            params.put("imgstr", bmpStr);
            params.put("em", useremail.getText());
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

            params.put("uuid", Util.getAndroidId(ReservationActivityActivity.this));
            params.put("lat", locLat);
            params.put("lng", locLng);
            params.put("version", Util.getAppVersionName(ReservationActivityActivity.this));
            params.put("os", "a");

        } catch (JSONException e) {}

        Api.post(CONFIG.bookingQReserveUrl, params.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReservationActivityActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        flag_btn_clicked = false;
                        return;
                    }

                    // 카드면 카드웹뷰로
                    flag_btn_clicked = false;
                    Intent intent = new Intent(ReservationActivityActivity.this, PaymentActivity.class);
                    intent.putExtra("paytype", paytype);
                    intent.putExtra("hotel_name", t_name);
                    intent.putExtra("bid", obj.getString("bid"));
                    intent.putExtra("selected_card", selected_card);
                    intent.putExtra("uname", usernameInput.getText().toString());
                    intent.putExtra("uphone", pnum1.getSelectedItem()+"-"+pnum2.getText().toString()+"-"+pnum3.getText().toString());
                    intent.putExtra("is_q", true);
                    intent.putExtra("city", city);
                    intent.putExtra("product_id", t_id);

                    if(paytype == 6) {
                        intent.putExtra("is_payco", true);
                    }
                    if(coupon_spinner != null && coupon_spinner.getSelectedItem() != null && !TextUtils.isEmpty(coupon_spinner.getSelectedItem().toString()) && !coupon_spinner.getSelectedItem().toString().equals("사용안함")) {
                        intent.putExtra("coupon_name", coupon_spinner.getSelectedItem().toString());
                        intent.putExtra("coupon_id", pcode);
                    }

                    float revenue = obj.has("revenue")? (float)obj.getInt("revenue"):0;
                    int quantity = obj.has("quantity")? obj.getInt("quantity"):0;

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
