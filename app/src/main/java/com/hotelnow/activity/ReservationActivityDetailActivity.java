package com.hotelnow.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
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
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
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

public class ReservationActivityDetailActivity extends Activity {

    String bid, accnum, hotel_id,mAddress, lat, lon, h_name;
    TextView hotel_name, hotel_room_name, booking_status, tv_username, tv_usertel,
            tv_real_price, tv_private_price, tv_reserve_price,tv_coupon_price, tv_total_price,tv_pay_type,tv_pay_bank_nm, tv_pay_bank_num,tv_pay_bank_user_nm,
            tv_pay_bank_user_day,tv_pay_num,tv_pay_tel_com,tv_pay_card_com,tv_save_point,tv_address, tv_pay_income_day;
    ImageView iv_img;
    Boolean showSnsDialog = true;
    DialogAlert dialogAlert;
    Button btn_review;
    LinearLayout ll_coupon, ticket_list;
    private SharedPreferences _preferences;
    DialogConfirm dialogConfirm;
    String call_message;
    String hotel_phone_number = "";
    WebView info_view;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservation_activity_detail);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();
        if(intent != null){
            bid = intent.getStringExtra("bid");
        }

        setData();
    }

    private void setData(){
        String url = CONFIG.ticketbookingDetailUrl+"/"+bid;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivityDetailActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReservationActivityDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject booking = obj.getJSONObject("detail");
                    JSONObject info = booking.getJSONObject("info");
                    JSONObject booking_info = booking.getJSONObject("booking_info");
                    JSONArray option_info = booking.getJSONArray("option_info");
                    JSONObject price_info = booking.getJSONObject("price_info");
                    JSONObject payment_info = booking.getJSONObject("payment_info");
                    JSONObject deal_info = booking.getJSONObject("deal_info");


                    hotel_name = (TextView) findViewById(R.id.hotel_name);
                    hotel_room_name = (TextView) findViewById(R.id.hotel_room_name);
                    iv_img = (ImageView) findViewById(R.id.iv_img);
                    booking_status = (TextView) findViewById(R.id.booking_status);
                    btn_review = (Button) findViewById(R.id.btn_review);
                    tv_username = (TextView) findViewById(R.id.tv_username);
                    tv_usertel = (TextView) findViewById(R.id.tv_usertel);
                    tv_real_price = (TextView) findViewById(R.id.tv_real_price);
                    ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);
                    tv_coupon_price = (TextView) findViewById(R.id.tv_coupon_price);
                    tv_total_price = (TextView) findViewById(R.id.tv_total_price);
                    tv_pay_type = (TextView) findViewById(R.id.tv_pay_type);
                    tv_pay_bank_nm = (TextView) findViewById(R.id.tv_pay_bank_nm);
                    tv_pay_bank_num = (TextView) findViewById(R.id.tv_pay_bank_num);
                    tv_pay_bank_user_nm = (TextView) findViewById(R.id.tv_pay_bank_user_nm);
                    tv_pay_bank_user_day = (TextView) findViewById(R.id.tv_pay_bank_user_day);
                    tv_pay_num = (TextView) findViewById(R.id.tv_pay_num);
                    tv_pay_tel_com = (TextView) findViewById(R.id.tv_pay_tel_com);
                    tv_pay_card_com = (TextView) findViewById(R.id.tv_pay_card_com);
                    tv_save_point = (TextView) findViewById(R.id.tv_save_point);
                    tv_address = (TextView) findViewById(R.id.tv_address);
                    tv_pay_income_day = (TextView) findViewById(R.id.tv_pay_income_day);
                    ticket_list = (LinearLayout) findViewById(R.id.ticket_list);

                    hotel_name.setText(info.getString("deal_name"));
                    hotel_room_name.setText(info.getString("total_ticket_count_display"));
                    Ion.with(iv_img).load(info.getString("img_url"));
                    tv_username.setText(booking_info.getString("user_name"));
                    tv_usertel.setText(booking_info.getString("user_phone"));

                    hotel_id = info.getString("deal_id");
                    lat = info.getString("latitude");
                    lon = info.getString("longitude");
                    h_name = info.getString("deal_name");
                    tv_real_price.setText(Util.numberFormat(price_info.getInt("sale_price")) + "원");

                    if(_preferences.getString("userid", null) != null) {
                        if (info.getString("is_review_writable").equals("Y") && info.getInt("review_count") == 0) {
                            btn_review.setText("리뷰 작성하기");
                            btn_review.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                        } else if (info.getInt("review_count") > 0) {
                            btn_review.setText("리뷰 보기");
                            btn_review.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                        } else {
                            btn_review.setVisibility(View.GONE);
                        }
                    }
                    else{
                        btn_review.setVisibility(View.GONE);
                        findViewById(R.id.not_user_reserid).setVisibility(View.VISIBLE);
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

                    if(price_info.getInt("promotion_money") <= 0){
                        ll_coupon.setVisibility(View.GONE);
                    }
                    else{
                        ll_coupon.setVisibility(View.VISIBLE);
                        tv_coupon_price.setText("-"+Util.numberFormat(price_info.getInt("promotion_money"))+"원");
                    }

                    tv_total_price.setText(Util.numberFormat(price_info.getInt("pay_price"))+"원");

                    if (option_info.length() > 0) {
                        ticket_list.removeAllViews();
                        ticket_list.setVisibility(View.VISIBLE);

                        //	sub_products 아래에 할당할 상품 정보들
                        for (int i = 0; i < option_info.length(); i++) {
                            JSONObject tobj = option_info.getJSONObject(i);

                            View v = getLayoutInflater().inflate(R.layout.layout_reservation_ticket_item, null);
                            TextView tv_option_title = (TextView) v.findViewById(R.id.tv_option_title);
                            TextView tv_option_info = (TextView) v.findViewById(R.id.tv_option_info);

                            tv_option_title.setText(tobj.getString("name"));
                            String tv_option = tobj.getString("available_date");
                            if(!TextUtils.isEmpty(tobj.getString("status_display"))){
                                tv_option +=  " / "+tobj.getString("status_display");
                            }

                            SpannableStringBuilder builder = new SpannableStringBuilder(tv_option);
                            builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.termtext)), tv_option.length() - tobj.getString("available_date").length(), tv_option.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_option_info.setText(tv_option);

                            if(i == option_info.length()-1){
                                v.findViewById(R.id.item_line).setVisibility(View.GONE);
                            }
                            else{
                                v.findViewById(R.id.item_line).setVisibility(View.VISIBLE);
                            }

                            ticket_list.addView(v);
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

                   info_view = (WebView) findViewById(R.id.info_view);

                    String webData ="";
                    Spannable sp = null;
                    String html = "";
                    if(deal_info.has("deal_introduce") && !TextUtils.isEmpty(deal_info.getString("deal_introduce"))) {
                        webData = deal_info.getString("deal_introduce").replace("\n", "<br>");
                        sp = new SpannableString(Html.fromHtml(webData));
                        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
                        html = "<div style='font-size:14px;color:#222222'>상품 소개</div><div style='font-size:12px;color:#666666'>"+Html.toHtml(sp)+"</div>";
                    }
                    if(deal_info.has("deal_info") && !TextUtils.isEmpty(deal_info.getString("deal_info"))){
                        webData = deal_info.getString("deal_info").replace("\n", "<br>");
                        sp = new SpannableString(Html.fromHtml(webData));
                        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
                        html += "<div style='font-size:14px;color:#222222''>상품 정보</div><div style='font-size:12px;color:#666666'>"+Html.toHtml(sp)+"</div>";
                    }
                    if(deal_info.has("refund_info") && !TextUtils.isEmpty(deal_info.getString("refund_info"))){
                        webData = deal_info.getString("refund_info").replace("\n", "<br>");
                        sp = new SpannableString(Html.fromHtml(webData));
                        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
                        html += "<div style='font-size:14px;color:#222222''>환불 정보</div><div style='font-size:12px;color:#666666'>"+Html.toHtml(sp)+"</div>";
                    }
                    if(deal_info.has("usage_info") && !TextUtils.isEmpty(deal_info.getString("usage_info"))){
                        webData = deal_info.getString("usage_info").replace("\n", "<br>");
                        sp = new SpannableString(Html.fromHtml(webData));
                        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
                        html += "<div style='font-size:14px;color:#222222''>사용 정보</div><div style='font-size:12px;color:#666666'>"+Html.toHtml(sp)+"</div>";
                    }
                    if(deal_info.has("store_info") && !TextUtils.isEmpty(deal_info.getString("store_info"))){
                        webData = deal_info.getString("store_info").replace("\n", "<br>");
                        sp = new SpannableString(Html.fromHtml(webData));
                        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
                        html += "<div style='font-size:14px;color:#222222''>시설사 정보</div><div style='font-size:12px;color:#666666'>"+Html.toHtml(sp)+"</div>";
                    }
                    if(deal_info.has("notice_info") && !TextUtils.isEmpty(deal_info.getString("notice_info"))){
                        webData = deal_info.getString("notice_info").replace("\n", "<br>");
                        sp = new SpannableString(Html.fromHtml(webData));
                        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
                        html += "<div style='font-size:14px;color:#222222''>공지 정보</div><div style='font-size:12px;color:#666666'>"+Html.toHtml(sp)+"</div>";
                    }
                    if(deal_info.has("cs_info") && !TextUtils.isEmpty(deal_info.getString("cs_info"))){
                        webData = deal_info.getString("cs_info").replace("\n", "<br>");
                        sp = new SpannableString(Html.fromHtml(webData));
                        Linkify.addLinks(sp, Linkify.PHONE_NUMBERS);
                        html += "<div style='font-size:14px;color:#222222''>고객센터 정보</div><div style='font-size:12px;color:#666666'>"+Html.toHtml(sp)+"</div>";
                    }

                    if(android.os.Build.VERSION.SDK_INT < 16) {
                        info_view.loadData(html, "text/html", "UTF-8"); // Android 4.0 이하 버전
                    }else {
                        info_view.loadData(html, "text/html; charset=UTF-8", null); // Android 4.1 이상 버전
                    }

                    // 지도 상세보기 정보 설정
                    String mapStr = "http://maps.googleapis.com/maps/api/staticmap?center="+info.getString("latitude")+"%2C"+info.getString("longitude")+
                            "&markers=icon:http://d2gxin9b07oiov.cloudfront.net/web/hotel_pin.png%7C"+info.getString("latitude")+"%2C"+info.getString("longitude")+
                            "&scale=2&sensor=false&language=ko&size=360x220&zoom=13"+"&key="+ BuildConfig.google_map_key2;
                    ImageView mapImg = (ImageView)findViewById(R.id.map_img);
                    Ion.with(mapImg).load(mapStr);
                    mAddress = info.getString("address");
                    tv_address.setText(mAddress);

                    // 지도 클릭 이벤트 설정
                    findViewById(R.id.btn_address_copy).setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) HotelnowApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", mAddress);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(ReservationActivityDetailActivity.this, "주소가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ((TextView)findViewById(R.id.btn_near)).setText("주변호텔 보기");
                    findViewById(R.id.btn_kimkisa).setVisibility(View.VISIBLE);

                    if(info.isNull("company_tel")){
                        hotel_phone_number = null;
                    } else {
                        findViewById(R.id.company_call).setVisibility(View.VISIBLE);
                        hotel_phone_number = info.getString("company_tel");
                        call_message = h_name + "\n" + hotel_phone_number + "\n\n" + "[확인] 버튼을 누르면 시설사와 바로 연결됩니다.";
                        findViewById(R.id.company_call).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogConfirm = new DialogConfirm(
                                        getString(R.string.alert_notice),
                                        call_message,
                                        getString(R.string.alert_close),
                                        getString(R.string.alert_confrim),
                                        ReservationActivityDetailActivity.this,
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
                                    ReservationActivityDetailActivity.this,
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
                            Intent intent = new Intent(ReservationActivityDetailActivity.this, MapActivity.class);
                            intent.putExtra("isTicket", true);
                            intent.putExtra("deal_name", h_name);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lng", lon);
                            intent.putExtra("from", "pdetail");
                            startActivity(intent);
                        }
                    });

                    if(payment_info.getString("account_available").equals("Y") && payment_info.getString("pay_type").equals("VBANK_KCP")) {
                        showSnsDialog = false; // sns 띄우지마
                        String timeLimit = payment_info.getString("limit_time").substring(5, 16).replace(" ", "일 ").replace("-", "월 ");

                        dialogAlert = new DialogAlert(
                                getString(R.string.alert_notice),
                                getString(R.string.alert_account_msg, payment_info.getString("income_account_nm"), booking_info.getString("user_name"), timeLimit),
                                ReservationActivityDetailActivity.this,
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
                                ReservationActivityDetailActivity.this,
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
                                    getString(R.string.alert_no),
                                    getString(R.string.alert_yes),
                                    ReservationActivityDetailActivity.this,
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

                                            Api.post(CONFIG.bookingticketHidelUrl, paramObj.toString(), new Api.HttpCallback() {
                                                @Override
                                                public void onFailure(Response response, Exception e) {
                                                    Toast.makeText(ReservationActivityDetailActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onSuccess(Map<String, String> headers, String body) {
                                                    try {
                                                        JSONObject obj = new JSONObject(body);

                                                        if (!obj.getString("result").equals("success")) {
                                                            Toast.makeText(ReservationActivityDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        Toast.makeText(ReservationActivityDetailActivity.this, getString(R.string.booking_hide_success), Toast.LENGTH_SHORT).show();

                                                        Intent returnIntent = new Intent();
                                                        setResult(88, returnIntent);
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                                                    } catch (Exception e) {
                                                        Toast.makeText(ReservationActivityDetailActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
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
                                    ReservationActivityDetailActivity.this,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogConfirm.dismiss();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String url = CONFIG.bookingticketReceiptlUrl+"/"+bid;

                                            Api.get(url, new Api.HttpCallback() {
                                                @Override
                                                public void onFailure(Response response, Exception e) {
                                                    Toast.makeText(ReservationActivityDetailActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
//                                                    wrapper.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onSuccess(Map<String, String> headers, String body) {
                                                    try {
                                                        JSONObject obj = new JSONObject(body);

                                                        if (!obj.getString("result").equals("success")) {
                                                            Toast.makeText(ReservationActivityDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        String receiptUrl = obj.getString("url");
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( receiptUrl ));
                                                        startActivity(intent);

                                                    } catch (Exception e) {
                                                        Toast.makeText(ReservationActivityDetailActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
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

                } catch (Exception e) {
                    Toast.makeText(ReservationActivityDetailActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(info_view != null)
            info_view.destroy();
    }
}