package com.hotelnow.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.dialog.DialogMarket;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.EndEventScrollView;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class ReservationHotelDetailActivity extends Activity {

    String bid, accnum, hotel_id,mAddress, lat, lon, h_name;
    TextView hotel_name, hotel_room_name, booking_status, tv_checkin_day, tv_checkin_time,tv_checkout_day, tv_checkout_time, tv_username, tv_usertel,
            tv_real_price, tv_private_price, tv_reserve_price,tv_coupon_price, tv_total_price,tv_pay_type,tv_pay_bank_nm, tv_pay_bank_num,tv_pay_bank_user_nm,
            tv_pay_bank_user_day,tv_pay_income_day,tv_pay_num,tv_pay_tel_com,tv_pay_card_com,tv_save_point,tv_address;
    ImageView iv_img;
    Boolean showSnsDialog = true;
    DialogAlert dialogAlert;
    Button btn_review;
    LinearLayout ll_private, ll_reservation, ll_coupon;
    TableLayout sub_products;
    private SharedPreferences _preferences;
    DialogConfirm dialogConfirm;
    String call_message;
    String hotel_phone_number = "";
    WebView info_view;
    boolean isReservation = false;
    String cookie="", user_name ="", user_phone="", r_name="", r_id;
    TextView tv_title_bar;
    boolean is_review = false;
    int dayCount = 0;
    boolean isEndScroll = false;
    DialogMarket dialogMarket;
    EndEventScrollView scrollview;

    private Handler mEndHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0 && !isEndScroll) {
                isEndScroll = true;
                if (!_preferences.getBoolean("isReMarket", false)) {
                    if (_preferences.getString("ReMarketDate", null) == null) {
                        showMarketPopup();
                    }
                    else{
                        if( (int) Util.diffOfDate(_preferences.getString("ReMarketDate", null), Util.todayFormat()) >= 180){
                            showMarketPopup();
                        }
                    }
                }
            }
        }
    };

    private void showMarketPopup(){
        final String mDay = Util.todayFormat();

//        누른 날짜 적용
        dialogMarket = new DialogMarket(
                getString(R.string.alert_notice),
                "호텔나우 서비스에 만족하셨나요?\n별점과 리뷰로 응원해주세요!",
                "다음에요",
                "응원해요! 호텔나우",
                ReservationHotelDetailActivity.this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("ReMarketDate", mDay);
                        prefEditor.commit();
                        dialogMarket.dismiss();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putBoolean("isReMarket", true);
                        prefEditor.commit();

                        String appPackageName = getPackageName();
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                            startActivity(intent);
                        } catch (ActivityNotFoundException anfe) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName));
                            startActivity(intent);
                        }
                        dialogMarket.dismiss();

                    }
                });
        dialogMarket.setCancelable(false);
        dialogMarket.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservation_hotel_detail);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cookie = _preferences.getString("userid", null);
        tv_title_bar = (TextView) findViewById(R.id.tv_title_bar);
        scrollview = (EndEventScrollView) findViewById(R.id.scrollview);
        scrollview.setHandler(mEndHandler);

        Intent intent = getIntent();
        if(intent != null){
            bid = intent.getStringExtra("bid");
            isReservation = intent.getBooleanExtra("reservation", false);
            if(cookie == null){
                user_name = intent.getStringExtra("user_name");
                user_phone = intent.getStringExtra("user_phone");
                tv_title_bar.setText(intent.getStringExtra("title"));
            }
        }
        if(isReservation) {
            CONFIG.sel_reserv = 0;
        }


        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setData();
    }

    private void setData(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.bookingDetailUrl+"/"+bid;
        if(cookie == null){
            url +="?user_name="+user_name+"&user_phone="+user_phone;
        }

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationHotelDetailActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReservationHotelDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    JSONObject booking = obj.getJSONObject("detail");
                    JSONObject info = booking.getJSONObject("info");
                    JSONObject booking_info = booking.getJSONObject("booking_info");
                    JSONObject price_info = booking.getJSONObject("price_info");
                    JSONObject payment_info = booking.getJSONObject("payment_info");
                    JSONObject buy_reward = booking.getJSONObject("buy_reward");
                    JSONObject confirm_info = booking.getJSONObject("confirm_info");


                    hotel_name = (TextView) findViewById(R.id.hotel_name);
                    hotel_room_name = (TextView) findViewById(R.id.hotel_room_name);
                    iv_img = (ImageView) findViewById(R.id.iv_img);
                    booking_status = (TextView) findViewById(R.id.booking_status);
                    tv_checkin_day = (TextView) findViewById(R.id.tv_checkin_day);
                    tv_checkin_time = (TextView) findViewById(R.id.tv_checkin_time);
                    tv_checkout_day = (TextView) findViewById(R.id.tv_checkout_day);
                    tv_checkout_time = (TextView) findViewById(R.id.tv_checkout_time);
                    btn_review = (Button) findViewById(R.id.btn_review);
                    tv_username = (TextView) findViewById(R.id.tv_username);
                    tv_usertel = (TextView) findViewById(R.id.tv_usertel);
                    tv_real_price = (TextView) findViewById(R.id.tv_real_price);
                    ll_private = (LinearLayout) findViewById(R.id.ll_private);
                    tv_private_price = (TextView) findViewById(R.id.tv_private_price);
                    ll_reservation = (LinearLayout) findViewById(R.id.ll_reservation);
                    tv_reserve_price = (TextView) findViewById(R.id.tv_reserve_price);
                    ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);
                    tv_coupon_price = (TextView) findViewById(R.id.tv_coupon_price);
                    tv_total_price = (TextView) findViewById(R.id.tv_total_price);
                    sub_products = (TableLayout) findViewById(R.id.sub_products);
                    tv_pay_type = (TextView) findViewById(R.id.tv_pay_type);
                    tv_pay_bank_nm = (TextView) findViewById(R.id.tv_pay_bank_nm);
                    tv_pay_bank_num = (TextView) findViewById(R.id.tv_pay_bank_num);
                    tv_pay_bank_user_nm = (TextView) findViewById(R.id.tv_pay_bank_user_nm);
                    tv_pay_bank_user_day = (TextView) findViewById(R.id.tv_pay_bank_user_day);
                    tv_pay_income_day = (TextView) findViewById(R.id.tv_pay_income_day);
                    tv_pay_num = (TextView) findViewById(R.id.tv_pay_num);
                    tv_pay_tel_com = (TextView) findViewById(R.id.tv_pay_tel_com);
                    tv_pay_card_com = (TextView) findViewById(R.id.tv_pay_card_com);
                    tv_save_point = (TextView) findViewById(R.id.tv_save_point);
                    tv_address = (TextView) findViewById(R.id.tv_address);

                    hotel_name.setText(info.getString("hotel_name"));
                    hotel_room_name.setText(info.getString("room_name"));
                    Ion.with(iv_img).load(info.getString("room_img"));
                    tv_checkin_day.setText(info.getString("checkin_date_format"));
                    tv_checkin_time.setText(info.getString("checkin_time_format"));
                    tv_checkout_day.setText(info.getString("checkout_date_format"));
                    tv_checkout_time.setText(info.getString("checkout_time_format"));
                    tv_username.setText(booking_info.getString("user_name"));
                    tv_usertel.setText(booking_info.getString("user_phone"));

                    hotel_id = info.getString("hotel_id");
                    lat = info.getString("latitude");
                    lon = info.getString("longuitude");
                    h_name = info.getString("hotel_name");
                    r_id = info.getString("room_id");
                    r_name = info.getString("room_name");
                    tv_real_price.setText(Util.numberFormat(price_info.getInt("price")) + "원");

                    if(cookie != null) {
                        if (info.getString("is_review_writable").equals("Y") && info.getInt("review_count") == 0) {
                            btn_review.setVisibility(View.VISIBLE);
                            btn_review.setText("리뷰 작성하기");
                            btn_review.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ReservationHotelDetailActivity.this, ReviewHotelWriteActivity.class);
                                    intent.putExtra("booking_id", bid);
                                    intent.putExtra("hotel_id", hotel_id);
                                    intent.putExtra("room_id", r_id);
                                    intent.putExtra("userid", cookie);
                                    intent.putExtra("hotel_name", h_name);
                                    intent.putExtra("room_name", r_name);
                                    startActivityForResult(intent, 80);
                                }
                            });
                        } else if (info.getInt("review_count") > 0) {
                            btn_review.setVisibility(View.VISIBLE);
                            btn_review.setText("리뷰 보기");
                            btn_review.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ReservationHotelDetailActivity.this, ReviewShowActivity.class);
                                    intent.putExtra("page", "stay");
                                    intent.putExtra("booking_id", bid);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            btn_review.setVisibility(View.GONE);
                        }
                    }
                    else{
                        btn_review.setVisibility(View.GONE);
                        findViewById(R.id.not_user_reserid).setVisibility(View.VISIBLE);
                        findViewById(R.id.not_user_info).setVisibility(View.VISIBLE);
                        Spannable spannable = new SpannableString("예약번호 "+info.getString("booking_id"));
                        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 5, info.getString("booking_id").length()+5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ((TextView)findViewById(R.id.not_user_reserid)).setText(spannable);
                    }

                    if(info.getString("status_detail").equals("inprogress")){
                        // 대기
                        booking_status.setBackgroundResource(R.drawable.bg_round_status_inpro);
                    }
                    else if(info.getString("status_detail").equals("used") || info.getString("status_detail").equals("cancel") || info.getString("status_detail").equals("expiration")){
                        // 사용완료
                        booking_status.setBackgroundResource(R.drawable.bg_round_status_can_comple);
                    }
                    else {
                        //결제만료
                        booking_status.setBackgroundResource(R.drawable.bg_round_status_book);
                    }

                    booking_status.setText(info.getString("status_display"));

                    if(price_info.getInt("privatedeal_money") <= 0){
                        ll_private.setVisibility(View.GONE);
                    }
                    else{
                        ll_private.setVisibility(View.VISIBLE);
                        tv_private_price.setText("-"+Util.numberFormat(price_info.getInt("privatedeal_money"))+"원");
                    }

                    if(price_info.getInt("reserve_money") <= 0){
                        ll_reservation.setVisibility(View.GONE);
                    }
                    else{
                        ll_reservation.setVisibility(View.VISIBLE);
                        tv_reserve_price.setText("-"+Util.numberFormat(price_info.getInt("reserve_money"))+"원");
                    }

                    if(price_info.getInt("promotion_money") <= 0){
                        ll_coupon.setVisibility(View.GONE);
                    }
                    else{
                        ll_coupon.setVisibility(View.VISIBLE);
                        tv_coupon_price.setText("-"+Util.numberFormat(price_info.getInt("promotion_money"))+"원");
                    }

                    tv_total_price.setText(Util.numberFormat(price_info.getInt("total_price"))+"원");

                    if (price_info.has("sub_bookings") == true) {
                        SimpleDateFormat p_DateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat p_CurDateFormat = new SimpleDateFormat(getString(R.string.year_month_day_weekday_format3), Locale.KOREAN);
                        JSONArray products = price_info.getJSONArray("sub_bookings");

                        if(products.length() > 1) {
                            sub_products.setVisibility(View.VISIBLE);

                            //	sub_products 아래에 할당할 상품 정보들
                            for (int i = 0; i < products.length(); i++) {
                                if(i == 0)
                                {
                                    View t = new View(ReservationHotelDetailActivity.this);
                                    t.setBackgroundResource(R.color.bg_background);
                                    t.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                                    sub_products.addView(t);
                                }
                                JSONObject tobj = products.getJSONObject(i);

                                View v = (TableRow) getLayoutInflater().inflate(R.layout.row_consecutive, null);
                                TextView sub_date = (TextView) v.findViewById(R.id.date);
                                TextView sub_price = (TextView) v.findViewById(R.id.price);
                                TextView sub_breakfast = (TextView) v.findViewById(R.id.breakfast);

                                String bf = tobj.getString("breakfast");

                                if(bf.equals("0")) bf = "불포함";
                                else bf = "포함";

                                sub_date.setText(tobj.getString("checkin_date_format"));
                                sub_price.setText(Util.numberFormat(tobj.getInt("total_price")) + "원");
                                sub_breakfast.setText(bf);

                                sub_products.addView(v);
                            }
                        }
                    }

                    // 수단별 결제정보
                    if(payment_info.getString("pay_type").equals("VBANK_KCP")){
                        findViewById(R.id.ll_pay_bank_nm).setVisibility(View.VISIBLE);
                        findViewById(R.id.ll_pay_bank_num).setVisibility(View.VISIBLE);
                        findViewById(R.id.ll_pay_bank_user_nm).setVisibility(View.VISIBLE);
                        accnum = payment_info.getString("account_no");

                        tv_pay_type.setText("가상계좌");
                        tv_pay_bank_nm.setText(payment_info.getString("bank_name"));
                        tv_pay_bank_num.setText(payment_info.getString("account_no")+"\n(터치하시면 복사됩니다.)");
                        tv_pay_bank_user_nm.setText(payment_info.getString("income_account_nm"));

                        tv_pay_bank_num.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", accnum);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(ReservationHotelDetailActivity.this, "계좌번호가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if(info.getString("status").equals("inprogress")) {
                            findViewById(R.id.ll_pay_bank_user_day).setVisibility(View.VISIBLE);
                            String limitTime = payment_info.getString("limit_time").substring(5, 16).replace(" ", "일 ").replace("-", "월 ");
                            tv_pay_bank_user_day.setText(limitTime);
                        }
                    }
                    else if(payment_info.getString("pay_type").equals("ARS")){
                        tv_pay_type.setText("ARS");

                        if(info.getString("status").equals("inprogress")) {
                            findViewById(R.id.ll_pay_income_day).setVisibility(View.VISIBLE);
                            String limitTime = payment_info.getString("limit_time").substring(5, 16).replace(" ", "일 ").replace("-", "월 ");
                            tv_pay_income_day.setText(limitTime);
                        }
                    }
                    else if(payment_info.getString("pay_type").equals("ABANK")){
                        findViewById(R.id.ll_pay_bank_nm).setVisibility(View.VISIBLE);
                        tv_pay_type.setText("계좌이체");
                        tv_pay_bank_nm.setText(payment_info.getString("bank_name"));
                    }
                    else if(payment_info.getString("pay_type").equals("RESERVE")){
                        tv_pay_type.setText("적립금");
                    }
                    else if(payment_info.getString("pay_type").equals("FREE")){
                        tv_pay_type.setText("이벤트");
                    }
                    else if(payment_info.getString("pay_type").equals("PHONE")){
                        tv_pay_type.setText("휴대폰");

                        if(payment_info.has("app_no")) {
                            findViewById(R.id.ll_pay_num).setVisibility(View.VISIBLE);
                            tv_pay_num.setText(payment_info.getString("app_no"));
                        }
                        if(payment_info.has("card_name")) {
                            findViewById(R.id.ll_pay_tel_com).setVisibility(View.VISIBLE);
                            tv_pay_tel_com.setText(payment_info.getString("card_name"));
                        }
                    }
                    else if(payment_info.getString("pay_type").equals("BCARD")) {
                        tv_pay_type.setText("간편결제");

                        if(payment_info.has("app_no")) {
                            findViewById(R.id.ll_pay_num).setVisibility(View.VISIBLE);
                            tv_pay_num.setText(payment_info.getString("app_no"));
                        }
                        if(payment_info.has("card_name")) {
                            findViewById(R.id.ll_pay_card_com).setVisibility(View.VISIBLE);
                            tv_pay_card_com.setText(payment_info.getString("card_name"));
                        }
                    }
                    else {
                        tv_pay_type.setText("신용카드");

                        if(payment_info.has("app_no")) {
                            findViewById(R.id.ll_pay_num).setVisibility(View.VISIBLE);
                            tv_pay_num.setText(payment_info.getString("app_no"));
                        }
                        if(payment_info.has("card_name")) {
                            findViewById(R.id.ll_pay_card_com).setVisibility(View.VISIBLE);
                            tv_pay_card_com.setText(payment_info.getString("card_name"));
                        }
                    }

                    if(_preferences.getString("userid", null) != null){
                        findViewById(R.id.ll_save_point).setVisibility(View.VISIBLE);
                        if(buy_reward.getInt("buy_reserve_monay") != 0) {
                            tv_save_point.setText(Util.numberFormat(buy_reward.getInt("buy_reserve_monay")) + "원");
                        }
                        else{
                            findViewById(R.id.ll_save_point).setVisibility(View.GONE);
                        }
                    }
                    else{
                        findViewById(R.id.ll_save_point).setVisibility(View.GONE);
                    }

                    info_view = (WebView) findViewById(R.id.info_view);

                    String webData =
                            confirm_info.getString("confirm_check").replace("<ul>", "").replace("</ul>", "").replace("<li>", "<div>• ").replace("</li>", "</div>")
                            +confirm_info.getString("cancel_fee").replace("\n","<br>");

                    Spannable sp = new SpannableString(Html.fromHtml(webData));
//                    Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
                    Linkify.addLinks(sp, Patterns.PHONE, "tel:", Linkify.sPhoneNumberMatchFilter,
                            Linkify.sPhoneNumberTransformFilter);
                    final String html = Html.toHtml(sp);

                    if(android.os.Build.VERSION.SDK_INT < 16) {
                        info_view.loadData("<div style='font-size:12px'>"+html+"</div>", "text/html", "UTF-8"); // Android 4.0 이하 버전
                    }else {
                        info_view.loadData("<div style='font-size:12px'>"+html+"</div>", "text/html; charset=UTF-8", null); // Android 4.1 이상 버전
                    }

                    // 지도 상세보기 정보 설정
                    String mapStr = "https://maps.googleapis.com/maps/api/staticmap?center="+info.getString("latitude")+"%2C"+info.getString("longuitude")+
                            "&markers=icon:http://hotelnow.s3.amazonaws.com/etc/20181012_180827_hozDzSdI4I.png%7C"+info.getString("latitude")+"%2C"+info.getString("longuitude")+
                            "&scale=2&sensor=false&language=ko&size=360x220&zoom="+info.getString("map_zoom")+"&key="+ BuildConfig.google_map_key2;
                    ImageView mapImg = (ImageView)findViewById(R.id.map_img);
                    Ion.with(mapImg).load(mapStr);
                    mAddress = info.getString("hotel_address");
                    tv_address.setText(mAddress);

                    // 지도 클릭 이벤트 설정
                    findViewById(R.id.btn_address_copy).setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) HotelnowApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", mAddress);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(ReservationHotelDetailActivity.this, "주소가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ((TextView)findViewById(R.id.btn_near)).setText("주변액티비티 보기");
                    findViewById(R.id.btn_kimkisa).setVisibility(View.VISIBLE);

                    if(info.isNull("hotel_phone")){
                        hotel_phone_number = null;
                    } else {
                        findViewById(R.id.company_call).setVisibility(View.VISIBLE);
                        hotel_phone_number = info.getString("hotel_phone");
                        call_message = h_name + "\n" + hotel_phone_number + "\n\n" + "[확인] 버튼을 누르면 시설사와 바로 연결됩니다.";
                        findViewById(R.id.company_call).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogConfirm = new DialogConfirm(
                                        getString(R.string.alert_notice),
                                        call_message,
                                        getString(R.string.alert_close),
                                        getString(R.string.alert_confrim),
                                        ReservationHotelDetailActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogConfirm.dismiss();
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + hotel_phone_number)));
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                                dialogConfirm.dismiss();
                                            }
                                        });
                                dialogConfirm.setCancelable(false);
                                dialogConfirm.show();
                            }
                        });
                    }

                    findViewById(R.id.btn_kimkisa).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            dialogConfirm = new DialogConfirm(
                                    getString(R.string.alert_notice),
                                    getString(R.string.booking_kimkisa_ask),
                                    getString(R.string.alert_no),
                                    getString(R.string.alert_yes),
                                    ReservationHotelDetailActivity.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogConfirm.dismiss();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String url = "kimgisa://navigate?name="+h_name+"&coord_type=wgs84&pos_x="+lon+"&pos_y="+lat+"&return_uri=com.hotelnow.activities.ActLoading&key="+CONFIG.kimgisaKey;
                                            String strAppPackage = "com.locnall.KimGiSa";
                                            PackageManager pm = getPackageManager();

                                            try {
                                                Intent intent = new Intent();
                                                intent.setClassName(strAppPackage, "com.locnall.KimGiSa.Engine.SMS.CremoteActivity");
                                                intent.setData(Uri.parse(url));
                                                startActivity(intent);

                                                Toast.makeText(getApplicationContext(), "카카오내비를 구동합니다.", Toast.LENGTH_SHORT).show();
//                                                t.send(new HitBuilders.EventBuilder().setCategory("BOOKING").setAction("KIMGISA").setLabel("BOOKING").build());
//                                                TuneWrap.Event("BOOKING", "KIMGISA", "BOOKING");
                                            } catch (Exception e) {
                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + strAppPackage));
                                                    startActivity(intent);
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + strAppPackage));
                                                    startActivity(intent);
                                                }
//                                                t.send(new HitBuilders.EventBuilder().setCategory("BOOKING").setAction("KIMGISA").setLabel("INSTALL").build());
//                                                TuneWrap.Event("BOOKING", "KIMGISA", "INSTALL");
                                            }
                                            dialogConfirm.dismiss();
                                        }
                                    });
                            dialogConfirm.setCancelable(false);
                            dialogConfirm.show();

                        }
                    });

                    findViewById(R.id.btn_address_near).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ReservationHotelDetailActivity.this, ActivityMapActivity.class);
                            intent.putExtra("hid", hotel_id);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lng", lon);
                            intent.putExtra("deal_name", h_name);
                            startActivity(intent);
                        }
                    });

                    if(payment_info.getString("account_available").equals("Y") && payment_info.getString("pay_type").equals("VBANK_KCP")) {
                        showSnsDialog = false; // sns 띄우지마
                        String timeLimit = payment_info.getString("limit_time").substring(5, 16).replace(" ", "일 ").replace("-", "월 ");

                        dialogAlert = new DialogAlert(
                                getString(R.string.alert_notice),
                                getString(R.string.alert_account_msg, payment_info.getString("income_account_nm"), booking_info.getString("user_name"), timeLimit),
                                ReservationHotelDetailActivity.this,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogAlert.dismiss();
                                    }
                                });
                        dialogAlert.setCancelable(false);
                        dialogAlert.show();
                    }  else if(payment_info.getString("account_available").equals("Y") && payment_info.getString("pay_type").equals("ARS")) {
                        showSnsDialog = false; // sns 띄우지마

                        dialogAlert = new DialogAlert(
                                getString(R.string.alert_notice),
                                "발송된 문자를 확인하신 후 통화 버튼을 눌러 ARS 결제를 진행해 주세요.\nARS 결제가 완료되면 수분내에 예약이 완료됩니다.",
                                ReservationHotelDetailActivity.this,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogAlert.dismiss();
                                    }
                                });
                        dialogAlert.setCancelable(false);
                        dialogAlert.show();
                    }

                    findViewById(R.id.booking_delete).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {

                            dialogConfirm = new DialogConfirm(
                                    getString(R.string.alert_notice),
                                    getString(R.string.booking_hide_ask),
                                    getString(R.string.booking_hide_cancel),
                                    getString(R.string.booking_hide),
                                    ReservationHotelDetailActivity.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogConfirm.dismiss();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            JSONObject paramObj = new JSONObject();
                                            try{
                                                paramObj.put("bid", bid);
                                            } catch (JSONException e) {}

                                            Api.post(CONFIG.bookingHidelUrl, paramObj.toString(), new Api.HttpCallback() {
                                                @Override
                                                public void onFailure(Response response, Exception e) {
                                                    Toast.makeText(ReservationHotelDetailActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onSuccess(Map<String, String> headers, String body) {
                                                    try {
                                                        JSONObject obj = new JSONObject(body);

                                                        if (!obj.getString("result").equals("success")) {
                                                            Toast.makeText(ReservationHotelDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        Toast.makeText(ReservationHotelDetailActivity.this, getString(R.string.booking_hide_success), Toast.LENGTH_SHORT).show();

                                                        Intent returnIntent = new Intent();
                                                        setResult(88, returnIntent);
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                                                    } catch (Exception e) {
                                                        Toast.makeText(ReservationHotelDetailActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

//                                            t.send(new HitBuilders.EventBuilder().setCategory("BOOKING").setAction("HIDE").build());
//                                            TuneWrap.Event("BOOKING", "HIDE");

                                            dialogConfirm.dismiss();
                                        }
                                    });
                            dialogConfirm.setCancelable(false);
                            dialogConfirm.show();
                        }
                    });

                    findViewById(R.id.booking_receipt).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {

                            dialogConfirm = new DialogConfirm(
                                    getString(R.string.alert_notice),
                                    getString(R.string.booking_receipt_ask),
                                    getString(R.string.alert_no),
                                    getString(R.string.alert_yes),
                                    ReservationHotelDetailActivity.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogConfirm.dismiss();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String url = CONFIG.bookingReceiptlUrl+"/"+bid;

                                            Api.get(url, new Api.HttpCallback() {
                                                @Override
                                                public void onFailure(Response response, Exception e) {
                                                    Toast.makeText(ReservationHotelDetailActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
//                                                    wrapper.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onSuccess(Map<String, String> headers, String body) {
                                                    try {
                                                        JSONObject obj = new JSONObject(body);

                                                        if (!obj.getString("result").equals("success")) {
                                                            Toast.makeText(ReservationHotelDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        String receiptUrl = obj.getString("url");
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( receiptUrl ));
                                                        startActivity(intent);

                                                    } catch (Exception e) {
                                                        Toast.makeText(ReservationHotelDetailActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
                                                    }

//                                                    wrapper.setVisibility(View.GONE);
                                                }
                                            });

//                                            t.send(new HitBuilders.EventBuilder().setCategory("BOOKING").setAction("RECEIPT").build());
//                                            TuneWrap.Event("BOOKING", "RECEIPT");

                                            dialogConfirm.dismiss();
                                        }
                                    });
                            dialogConfirm.setCancelable(false);
                            dialogConfirm.show();
                        }
                    });
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    Toast.makeText(ReservationHotelDetailActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 80 && resultCode == RESULT_OK){
            is_review = true;
            setData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(info_view != null)
            info_view.destroy();

        if(is_review){
            setResult(0);
        }
    }
}
