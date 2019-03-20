package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.HtmlTagHandler;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

public class AllRoomTypeActivity extends Activity {


    private String ec_date, ee_date, hid, hotel_name, city, pid, evt;
    private LinearLayout room_list;
    private RelativeLayout bt_calendar;
    private String image_arr[];
    private int privatedeal_status = -1;
    private String cookie;
    private SharedPreferences _preferences;
    private DialogAlert dialogAlert = null;
    private String[] selectList;
    private String lodge_type;
    private TextView date;
    private boolean is_date = false, isLogin = false;
    private TextView tv_review_count;
    private DialogConfirm dialogConfirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_room);

        room_list = (LinearLayout) findViewById(R.id.room_list);
        bt_calendar = (RelativeLayout) findViewById(R.id.bt_calendar);
        tv_review_count = (TextView) findViewById(R.id.tv_review_count);

        Intent intent = getIntent();
        ec_date = intent.getStringExtra("sdate");
        ee_date = intent.getStringExtra("edate");
        hid = intent.getStringExtra("hid");
        pid = intent.getStringExtra("pid");
        evt = intent.getStringExtra("evt");

        TuneWrap.Event("productdetail_stay_more", hid);

        _preferences = PreferenceManager.getDefaultSharedPreferences(AllRoomTypeActivity.this);
        cookie = _preferences.getString("userid", null);

        bt_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api.get(CONFIG.server_time, new Api.HttpCallback() {
                    @Override
                    public void onFailure(Response response, Exception throwable) {
                        Toast.makeText(AllRoomTypeActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);
                            if (!TextUtils.isEmpty(obj.getString("server_time"))) {
                                long time = obj.getInt("server_time") * (long) 1000;
                                CONFIG.svr_date = new Date(time);
                                Intent intent = new Intent(AllRoomTypeActivity.this, CalendarActivity.class);
                                intent.putExtra("ec_date", ec_date);
                                intent.putExtra("ee_date", ee_date);
                                intent.putExtra("selectList", selectList);
                                intent.putExtra("lodge_type", lodge_type);
                                startActivityForResult(intent, 80);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        date = (TextView) findViewById(R.id.date);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_date) {
                    Intent intent = new Intent();
                    intent.putExtra("ec_date", ec_date);
                    intent.putExtra("ee_date", ee_date);
                    intent.putExtra("is_date", true);
                    intent.putExtra("page", true);
                    setResult(80, intent);
                }
                finish();
            }
        });

        setDetailView();
    }

    private void setDetailView() {
        String url = CONFIG.hotel_detail + "/" + hid + "?pid=" + pid + "&evt=" + evt;

        try {
            if (cookie != null) {
                url += "&user_id=" + AES256Chiper.AES_Decode(cookie.replace("HN|", ""));
            } else {
                url += "&user_id=" + cookie;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ec_date != null && ee_date != null) {
            url += "&ec_date=" + ec_date + "&ee_date=" + ee_date + "&consecutive=Y";
        }

        Api.get(url, new Api.HttpCallback() {

            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(AllRoomTypeActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(AllRoomTypeActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    room_list.removeAllViews();
                    final JSONObject hotel_data = obj.getJSONObject("hotel");
                    final JSONArray rdata = obj.getJSONArray("room_types");
                    final JSONArray avail_dates = obj.getJSONArray("avail_dates");

                    if (ec_date == null && ee_date == null) {
                        ec_date = avail_dates.get(0).toString();
                        ee_date = Util.getNextDateStr(avail_dates.get(0).toString());
                    }
                    date.setText(Util.formatchange5(ec_date) + " - " + Util.formatchange5(ee_date));
                    hotel_name = hotel_data.getString("name");
                    city = hotel_data.getString("city_name");
                    selectList = new String[avail_dates.length()];
                    for (int i = 0; i < avail_dates.length(); i++) {
                        selectList[i] = avail_dates.get(i).toString();
                    }

                    final String total_cnt = "총 " + Util.numberFormat(rdata.length()) + "개의 객실이 있습니다";
                    SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple)), 2, 2 + Util.numberFormat(rdata.length()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2 + Util.numberFormat(rdata.length()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_review_count.setText(builder);

                    privatedeal_status = obj.getInt("privatedeal_booking_status");
                    lodge_type = hotel_data.getString("lodge_type");

                    for (int i = 0; i < rdata.length(); i++) {
                        int r_soldout = 0;
                        final View view_room = LayoutInflater.from(AllRoomTypeActivity.this).inflate(R.layout.layout_detail_hotel_room_item, null);
                        final TextView tv_room_title = (TextView) view_room.findViewById(R.id.tv_room_title);
                        TextView tv_room_sub_title = (TextView) view_room.findViewById(R.id.tv_room_sub_title);
                        TextView tv_detail1 = (TextView) view_room.findViewById(R.id.tv_detail1);
                        TextView tv_detail2 = (TextView) view_room.findViewById(R.id.tv_detail2);
                        TextView tv_detail3 = (TextView) view_room.findViewById(R.id.tv_detail3);
                        final RoundedImageView img_room = (RoundedImageView) view_room.findViewById(R.id.img_room);
                        TextView tv_room_detail_price = (TextView) view_room.findViewById(R.id.tv_room_detail_price);
                        RelativeLayout btn_more = (RelativeLayout) view_room.findViewById(R.id.btn_more);
                        AutoLinkTextView tv_room_info = (AutoLinkTextView) view_room.findViewById(R.id.tv_room_info);
                        LinearLayout btn_more_close = (LinearLayout) view_room.findViewById(R.id.btn_more_close);
                        LinearLayout more_img_list = (LinearLayout) view_room.findViewById(R.id.more_img_list);
                        final TextView btn_private = (TextView) view_room.findViewById(R.id.btn_private);
                        TextView btn_reservation = (TextView) view_room.findViewById(R.id.btn_reservation);
                        TextView tv_detail_per = (TextView) view_room.findViewById(R.id.tv_detail_per);
                        TextView tv_room_days = (TextView) view_room.findViewById(R.id.tv_room_days);
                        final TextView pid = (TextView) view_room.findViewById(R.id.pid);
                        final TextView rid = (TextView) view_room.findViewById(R.id.rid);
                        final String p_default = rdata.getJSONObject(i).getString("default_pn");
                        final String p_max = rdata.getJSONObject(i).getString("max_pn");
                        final int sale_price = rdata.getJSONObject(i).getInt("sale_price");
                        final int normal_price = rdata.getJSONObject(i).getInt("normal_price");
                        HorizontalScrollView hscroll_img = (HorizontalScrollView) view_room.findViewById(R.id.hscroll_img);

                        pid.setText(rdata.getJSONObject(i).getString("product_id"));
                        rid.setText(rdata.getJSONObject(i).getString("room_id"));
                        tv_room_title.setText(rdata.getJSONObject(i).getString("room_name"));

                        long daydiff = Util.diffOfDate(ec_date.replace("-", ""), ee_date.replace("-", ""));
                        if (daydiff > 1)
                            tv_room_days.setText(daydiff + "박 / ");

                        if (!TextUtils.isEmpty(rdata.getJSONObject(i).getString("benefit_text"))) {
                            tv_room_sub_title.setVisibility(View.VISIBLE);
                            tv_room_sub_title.setText(rdata.getJSONObject(i).getString("benefit_text"));
                        } else {
                            tv_room_sub_title.setVisibility(View.GONE);
                        }

                        if (rdata.getJSONObject(i).getJSONArray("img").length() != 0) {
                            image_arr = new String[rdata.getJSONObject(i).getJSONArray("img").length()];
                            for (int j = 0; j < image_arr.length; j++) {
                                image_arr[j] = rdata.getJSONObject(i).getJSONArray("img").getJSONObject(j).getString("room_img");
                            }
                            Ion.with(img_room).load(image_arr[0]);
                            if (image_arr.length == 1) {
                                hscroll_img.setVisibility(View.GONE);
                            } else {
                                hscroll_img.setVisibility(View.VISIBLE);
                            }

                            more_img_list.removeAllViews();
                            for (int j = 0; j < image_arr.length; j++) {
                                View view_img = LayoutInflater.from(AllRoomTypeActivity.this).inflate(R.layout.layout_detail_room_img_item, null);
                                RoundedImageView image_container = view_img.findViewById(R.id.image_container);
                                Ion.with(image_container).load(image_arr[j]);
                                more_img_list.addView(view_img);
                            }
                            img_room.setTag(image_arr[0]);
                        }

                        tv_detail2.setText("기준 " + rdata.getJSONObject(i).getString("default_pn") + "인," + "최대 " + rdata.getJSONObject(i).getString("max_pn") + "인");
                        tv_detail3.setText("체크인 " + rdata.getJSONObject(i).getString("checkin_time") + " 체크아웃 " + rdata.getJSONObject(i).getString("checkout_time"));
                        if (rdata.getJSONObject(i).getInt("sale_rate") == 0) {
                            tv_detail_per.setVisibility(View.GONE);
                        } else {
                            tv_detail_per.setVisibility(View.VISIBLE);
                        }
                        tv_detail_per.setText(rdata.getJSONObject(i).getInt("sale_rate") + "%↓");
                        tv_room_detail_price.setText(Util.numberFormat(rdata.getJSONObject(i).getInt("sale_price")) + "원");
                        String info_html = rdata.getJSONObject(i).getString("room_content").replace("\n", "<br>").replace("•", "ㆍ ");

                        Spanned text;
                        if (Build.VERSION.SDK_INT >= 24) {
                            text = Html.fromHtml(info_html, Html.FROM_HTML_MODE_LEGACY, null, new HtmlTagHandler());
                        } else {
                            text = Html.fromHtml(info_html, null, new HtmlTagHandler());
                        }

                        tv_room_info.setText(text);


                        if (rdata.getJSONObject(i).getInt("privatedeal_inven_count") == -999) {
                            view_room.findViewById(R.id.img_room_private).setVisibility(View.GONE);
                            btn_private.setVisibility(View.GONE);
                            view_room.findViewById(R.id.line_private).setVisibility(View.GONE);
                        } else if (rdata.getJSONObject(i).has("privatedeal_proposal_yn") && rdata.getJSONObject(i).getString("privatedeal_proposal_yn").equals("Y")) {
                            view_room.findViewById(R.id.img_room_private).setVisibility(View.GONE);
                            btn_private.setText("제안완료");
                            btn_private.setBackgroundResource(R.drawable.gray_round);
                            view_room.findViewById(R.id.line_private).setVisibility(View.INVISIBLE);
                        } else if (rdata.getJSONObject(i).getInt("privatedeal_inven_count") <= 0) {
                            view_room.findViewById(R.id.img_room_private).setVisibility(View.GONE);
                            btn_private.setVisibility(View.VISIBLE);
                            btn_private.setText("프라이빗딜 종료");
                            btn_private.setBackgroundResource(R.drawable.gray_round);
                            view_room.findViewById(R.id.line_private).setVisibility(View.INVISIBLE);
                            r_soldout = r_soldout + 1;
                        } else {
                            view_room.findViewById(R.id.img_room_private).setVisibility(View.GONE);
                            btn_private.setVisibility(View.VISIBLE);
                            btn_private.setBackgroundResource(R.drawable.reservation_private_round);
                            btn_private.setText("프라이빗딜");
                            view_room.findViewById(R.id.line_private).setVisibility(View.INVISIBLE);
                        }

                        if (rdata.getJSONObject(i).getInt("inven_count") <= 0) {
                            btn_reservation.setText("판매완료");
                            btn_reservation.setBackgroundResource(R.drawable.gray_round);
                            btn_reservation.setEnabled(false);
                            r_soldout = r_soldout + 1;
                        } else {
                            btn_reservation.setText("예약하기");
                            btn_reservation.setBackgroundResource(R.drawable.reservation_round);
                            btn_reservation.setEnabled(true);
                        }

                        if (r_soldout == 2) {
                            view_room.findViewById(R.id.img_room_private).setVisibility(View.GONE);
                            btn_private.setVisibility(View.GONE);
                            view_room.findViewById(R.id.line_private).setVisibility(View.GONE);
                        }

                        tv_room_title.setTag(i);
                        rid.setTag(i);
                        pid.setTag(i);
                        view_room.setTag(i);
                        btn_more.setTag(i);
                        btn_more_close.setTag(i);
                        btn_private.setTag(i);
                        btn_reservation.setTag(i);

                        btn_private.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TuneWrap.Event("productdetail_stay_privatebutton", hid);
                                if(btn_private.getText().equals("제안완료")){
                                    Toast.makeText(getApplicationContext(), "프라이빗딜은 객실 타입 당 1일 3회 제안이 가능합니다. 다른 객실로 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (privatedeal_status == 1 || privatedeal_status == -1 || btn_private.getText().equals("예약하기")) {

                                    String mUrl = CONFIG.PrivateUrl + "?hotel_id=" + hid + "&hotel_name=" + hotel_name + "&room_id=" + rid.getText() + "&room_name=" + tv_room_title.getText() + "&room_img=" + (String) img_room.getTag()
                                            + "&product_id=" + pid.getText() + "&product_name=" + tv_room_title.getText() + "&default_pn=" + p_default + "&max_pn=" + p_max
                                            + "&normal_price=" + normal_price + "&price=" + sale_price;

                                    if (cookie == null) {
                                        dialogConfirm = new DialogConfirm("알림", "로그인 후 사용 할 수 있습니다.", "취소", "확인", AllRoomTypeActivity.this,
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialogConfirm.dismiss();
                                                    }
                                                },
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(AllRoomTypeActivity.this, LoginActivity.class);
                                                        intent.putExtra("page", "Private");
                                                        intent.putExtra("sdate", ec_date);
                                                        intent.putExtra("edate", ee_date);
                                                        startActivityForResult(intent, 90);
                                                        dialogConfirm.dismiss();
                                                    }
                                                });
                                        dialogConfirm.show();
                                    } else {
                                        setPrivateDeal(mUrl, hid, rid.getText().toString(), pid.getText().toString());
                                    }
                                } else {
                                    ShowPrivateDealDialog("프라이빗딜은 1일 1회 예약 가능합니다.\n내일 다시 시도해주세요.");
                                    return;
                                }
                            }
                        });

                        btn_reservation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TuneWrap.Event("productdetail_stay_reservationbutton", hid);
                                if (cookie == null) {
                                    Intent intent = new Intent(AllRoomTypeActivity.this, LoginActivity.class);
                                    intent.putExtra("ec_date", ec_date);
                                    intent.putExtra("ee_date", ee_date);
                                    intent.putExtra("pid", pid.getText());
                                    intent.putExtra("page", "detailH");
                                    startActivityForResult(intent, 90);
                                    return;
                                }

                                Intent intent = new Intent(AllRoomTypeActivity.this, ReservationActivity.class);
                                intent.putExtra("ec_date", ec_date);
                                intent.putExtra("ee_date", ee_date);
                                intent.putExtra("pid", pid.getText());
                                intent.putExtra("page", "detailH");
                                startActivityForResult(intent, 80);
                            }
                        });

                        btn_more_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (room_list.getChildAt((int) v.getTag()).findViewById(R.id.more_view).getVisibility() == View.VISIBLE) {
                                    TuneWrap.Event("productdetail_stay_roomtype");
                                    room_list.getChildAt((int) v.getTag()).findViewById(R.id.more_view).setVisibility(View.GONE);
                                    view_room.findViewById(R.id.line).setVisibility(View.GONE);
                                    ((TextView) room_list.getChildAt((int) v.getTag()).findViewById(R.id.room_detail_close)).setText(R.string.btn_more2);
                                    ((ImageView) room_list.getChildAt((int) v.getTag()).findViewById(R.id.icon_more)).setBackgroundResource(R.drawable.btn_detail_open);
                                } else {
                                    room_list.getChildAt((int) v.getTag()).findViewById(R.id.more_view).setVisibility(View.VISIBLE);
                                    view_room.findViewById(R.id.line).setVisibility(View.VISIBLE);
                                    ((TextView) room_list.getChildAt((int) v.getTag()).findViewById(R.id.room_detail_close)).setText(R.string.btn_more);
                                    ((ImageView) room_list.getChildAt((int) v.getTag()).findViewById(R.id.icon_more)).setBackgroundResource(R.drawable.btn_detail_close);
                                }
                            }
                        });

                        btn_more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (room_list.getChildAt((int) v.getTag()).findViewById(R.id.more_view).getVisibility() == View.VISIBLE) {
                                    room_list.getChildAt((int) v.getTag()).findViewById(R.id.more_view).setVisibility(View.GONE);
                                    view_room.findViewById(R.id.line).setVisibility(View.GONE);
                                    ((TextView) room_list.getChildAt((int) v.getTag()).findViewById(R.id.room_detail_close)).setText(R.string.btn_more2);
                                    ((ImageView) room_list.getChildAt((int) v.getTag()).findViewById(R.id.icon_more)).setBackgroundResource(R.drawable.btn_detail_open);
                                } else {
                                    room_list.getChildAt((int) v.getTag()).findViewById(R.id.more_view).setVisibility(View.VISIBLE);
                                    view_room.findViewById(R.id.line).setVisibility(View.VISIBLE);
                                    ((TextView) room_list.getChildAt((int) v.getTag()).findViewById(R.id.room_detail_close)).setText(R.string.btn_more);
                                    ((ImageView) room_list.getChildAt((int) v.getTag()).findViewById(R.id.icon_more)).setBackgroundResource(R.drawable.btn_detail_close);
                                }
                            }
                        });

                        room_list.addView(view_room);
                    }

                } catch (Exception e) {
                    e.getStackTrace();
                    LogUtil.e("xxxxx", e.getMessage());
                    Toast.makeText(AllRoomTypeActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 프라이빗 딜
    private void setPrivateDeal(final String linkUrl, String Hotel_id, final String Room_id, final String mProduct_Id) {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("hotel_id", Hotel_id);
            paramObj.put("room_id", Room_id);
            paramObj.put("ec_date", ec_date);
            paramObj.put("ee_date", ee_date);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }
        Api.post(CONFIG.privateDeaUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.getString("result").equals("success")) {
                        return;
                    }
                    TuneWrap.Event("stay_private_detail", hid);
                    String fullLinkUrl = linkUrl + "&bid_id=" + obj.getJSONObject("data").getString("id") + "&refKey=" + obj.getJSONObject("data").getString("refKey");
                    Intent intent = new Intent(AllRoomTypeActivity.this, PrivateDealActivity.class);
                    intent.putExtra("pid", mProduct_Id);
                    intent.putExtra("url", fullLinkUrl);
                    intent.putExtra("bid_id", obj.getJSONObject("data").getString("id"));
                    intent.putExtra("bid", Room_id);
                    intent.putExtra("ec_date", ec_date);
                    intent.putExtra("ee_date", ee_date);
                    intent.putExtra("city", city);
                    intent.putExtra("hotel_name", hotel_name);
                    intent.putExtra("hid", hid);
                    startActivityForResult(intent, 80);

                } catch (Exception e) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ShowPrivateDealDialog(String msg) {
        if (dialogAlert != null && dialogAlert.isShowing()) {
            dialogAlert.dismiss();
        }
        dialogAlert = new DialogAlert(
                getString(R.string.alert_notice),
                msg,
                AllRoomTypeActivity.this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogAlert.dismiss();
                    }
                });
        dialogAlert.setCancelable(false);
        dialogAlert.show();
    }

    @Override
    public void onBackPressed() {
        if (is_date) {
            Intent intent = new Intent();
            intent.putExtra("ec_date", ec_date);
            intent.putExtra("ee_date", ee_date);
            intent.putExtra("is_date", true);
            intent.putExtra("page", true);
            setResult(80, intent);
        } else if (isLogin) {
            Intent intent = new Intent();
            setResult(80, intent);
        }

        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 80 && requestCode == 80) {

            ec_date = data.getStringExtra("ec_date");
            ee_date = data.getStringExtra("ee_date");

            if (data.getBooleanExtra("is_date", false)) {
                is_date = true;
            }
            cookie = _preferences.getString("userid", null);
            if (cookie != null) {
                isLogin = true;
            }
            setDetailView();
        } else if (resultCode == 90 && requestCode == 90) {
            Intent intent = new Intent();
            intent.putExtra("ec_date", ec_date);
            intent.putExtra("ee_date", ee_date);
            setResult(110, intent);
            finish();
        } else if (resultCode == 100 && requestCode == 80) {
            setResult(100);
            finish();
        } else if (resultCode == 80 && requestCode == 90) {
            isLogin = true;
            cookie = _preferences.getString("userid", null);
        }
    }
}
