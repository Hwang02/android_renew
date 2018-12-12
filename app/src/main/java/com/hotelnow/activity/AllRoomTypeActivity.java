package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
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
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.HtmlTagHandler;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private boolean is_date = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_room);

        room_list = (LinearLayout) findViewById(R.id.room_list);
        bt_calendar = (RelativeLayout) findViewById(R.id.bt_calendar);

        Intent intent  = getIntent();
        ec_date = intent.getStringExtra("sdate");
        ee_date = intent.getStringExtra("edate");
        hid = intent.getStringExtra("hid");
        pid = intent.getStringExtra("pid");
        evt = intent.getStringExtra("evt");

        _preferences = PreferenceManager.getDefaultSharedPreferences(AllRoomTypeActivity.this);
        cookie = _preferences.getString("userid", null);

        bt_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllRoomTypeActivity.this, CalendarActivity.class);
                intent.putExtra("ec_date", ec_date);
                intent.putExtra("ee_date", ee_date);
                intent.putExtra("selectList", selectList);
                intent.putExtra("lodge_type", lodge_type);
                startActivityForResult(intent, 80);
            }
        });
        date = (TextView) findViewById(R.id.date);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_date){
                    Intent intent = new Intent();
                    intent.putExtra("ec_date", ec_date);
                    intent.putExtra("ee_date", ee_date);
                    intent.putExtra("is_date", true);
                    setResult(80, intent);
                }
                finish();
            }
        });

        setDetailView();
    }

    private void setDetailView(){
        String url = CONFIG.hotel_detail + "/" + hid + "?pid=" + pid + "&evt=" + evt+ "&user_id="+cookie;
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
                    date.setText(Util.formatchange5(ec_date)+ " - "+Util.formatchange5(ee_date));
                    room_list.removeAllViews();
                    final JSONObject hotel_data = obj.getJSONObject("hotel");
                    final JSONArray rdata = obj.getJSONArray("room_types");
                    final JSONArray avail_dates = obj.getJSONArray("avail_dates");
                    hotel_name = hotel_data.getString("name");
                    city = hotel_data.getString("city_name");
                    selectList = new String[avail_dates.length()];
                    for(int i =0; i<avail_dates.length(); i++){
                        selectList[i] = avail_dates.get(i).toString();
                    }

                    privatedeal_status = obj.getInt("privatedeal_booking_status");
                    lodge_type = hotel_data.getString("lodge_type");

                    for(int i =0; i<rdata.length(); i++){
                        View view_room = LayoutInflater.from(AllRoomTypeActivity.this).inflate(R.layout.layout_detail_hotel_room_item, null);
                        final TextView tv_room_title = (TextView)view_room.findViewById(R.id.tv_room_title);
                        TextView tv_room_sub_title = (TextView)view_room.findViewById(R.id.tv_room_sub_title);
                        TextView tv_detail1 = (TextView)view_room.findViewById(R.id.tv_detail1);
                        TextView tv_detail2 = (TextView)view_room.findViewById(R.id.tv_detail2);
                        TextView tv_detail3 = (TextView)view_room.findViewById(R.id.tv_detail3);
                        final ImageView img_room = (ImageView)view_room.findViewById(R.id.img_room);
                        TextView tv_room_detail_price = (TextView)view_room.findViewById(R.id.tv_room_detail_price);
                        RelativeLayout btn_more = (RelativeLayout)view_room.findViewById(R.id.btn_more);
                        AutoLinkTextView tv_room_info = (AutoLinkTextView)view_room.findViewById(R.id.tv_room_info);
                        LinearLayout btn_more_close = (LinearLayout)view_room.findViewById(R.id.btn_more_close);
                        LinearLayout more_img_list = (LinearLayout)view_room.findViewById(R.id.more_img_list);
                        TextView btn_private =(TextView)view_room.findViewById(R.id.btn_private);
                        TextView btn_reservation =(TextView)view_room.findViewById(R.id.btn_reservation);
                        TextView tv_detail_per = (TextView)view_room.findViewById(R.id.tv_detail_per);
                        final TextView pid = (TextView)view_room.findViewById(R.id.pid);
                        final TextView rid = (TextView)view_room.findViewById(R.id.rid);
                        final String p_default = rdata.getJSONObject(i).getString("default_pn");
                        final String p_max = rdata.getJSONObject(i).getString("max_pn");
                        final int sale_price = rdata.getJSONObject(i).getInt("sale_price");
                        final int normal_price = rdata.getJSONObject(i).getInt("normal_price");
                        HorizontalScrollView hscroll_img = (HorizontalScrollView) view_room.findViewById(R.id.hscroll_img);

                        pid.setText(rdata.getJSONObject(i).getString("product_id"));
                        rid.setText(rdata.getJSONObject(i).getString("room_id"));
                        tv_room_title.setText(rdata.getJSONObject(i).getString("room_name"));

                        if(!TextUtils.isEmpty(rdata.getJSONObject(i).getString("title"))) {
                            tv_room_sub_title.setVisibility(View.VISIBLE);
                            tv_room_sub_title.setText(rdata.getJSONObject(i).getString("title"));
                        }
                        else {
                            tv_room_sub_title.setVisibility(View.GONE);
                        }

                        if(rdata.getJSONObject(i).getJSONArray("img").length() != 0) {
                            image_arr = new String[rdata.getJSONObject(i).getJSONArray("img").length()];
                            for(int j = 0; j<image_arr.length; j++){
                                image_arr[j] = rdata.getJSONObject(i).getJSONArray("img").getJSONObject(j).getString("room_img");
                            }
                            Ion.with(img_room).load(image_arr[0]);
                            if (image_arr.length == 1){
                                hscroll_img.setVisibility(View.GONE);
                            }
                            else {
                                hscroll_img.setVisibility(View.VISIBLE);
                            }
                        }

                        more_img_list.removeAllViews();
                        for(int j=0; j<image_arr.length;j++){
                            View view_img = LayoutInflater.from(AllRoomTypeActivity.this).inflate(R.layout.layout_detail_room_img_item, null);
                            ImageView image_container = view_img.findViewById(R.id.image_container);
                            Ion.with(image_container).load(image_arr[j]);
                            more_img_list.addView(view_img);
                        }

                        tv_detail2.setText("기준 "+rdata.getJSONObject(i).getString("default_pn")+"인,"+"최대 "+rdata.getJSONObject(i).getString("max_pn")+"");
                        tv_detail3.setText("체크인 "+rdata.getJSONObject(i).getString("checkin_time")+" 체크아웃 "+rdata.getJSONObject(i).getString("checkout_time"));
                        tv_detail_per.setText(rdata.getJSONObject(i).getInt("sale_rate")+"%↓");
                        tv_room_detail_price.setText(Util.numberFormat(rdata.getJSONObject(i).getInt("sale_price")));
                        String info_html = rdata.getJSONObject(i).getString("room_content").replace("\n","<br>").replace("•","ㆍ ");

                        Spanned text;
                        if (Build.VERSION.SDK_INT >= 24) {
                            text = Html.fromHtml(info_html, Html.FROM_HTML_MODE_LEGACY, null, new HtmlTagHandler());
                        } else {
                            text = Html.fromHtml(info_html, null, new HtmlTagHandler());
                        }

                        tv_room_info.setText(text);

                        if(rdata.getJSONObject(i).getString("privateDealYN").equals("Y") && rdata.getJSONObject(i).getInt("privatedeal_inven_count") != -999){
                            btn_private.setVisibility(View.VISIBLE);
                        }
                        if(!rdata.getJSONObject(i).has("privatedeal_proposal_yn") || rdata.getJSONObject(i).getString("privatedeal_proposal_yn").equals("Y")){
                            btn_private.setVisibility(View.GONE);
                        }
                        if(rdata.getJSONObject(i).getInt("privatedeal_inven_count") <= 0){
                            btn_private.setVisibility(View.GONE);
                        }
                        else {
                            btn_private.setVisibility(View.VISIBLE);
                        }

                        img_room.setTag(image_arr[0]);
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
                                if (privatedeal_status == 1 || privatedeal_status == -1) {
                                    String mUrl = CONFIG.PrivateUrl + "?hotel_id=" + hid + "&hotel_name=" + hotel_name + "&room_id=" + rid.getText() + "&room_name=" + tv_room_title.getText() + "&room_img=" + (String)img_room.getTag()
                                            + "&product_id=" + pid.getText() + "&product_name=" + tv_room_title.getText() + "&default_pn=" + p_default + "&max_pn=" + p_max
                                            + "&normal_price=" + normal_price + "&price=" + sale_price;

                                    if (cookie == null) {
                                        Intent intent = new Intent(AllRoomTypeActivity.this, LoginActivity.class);
                                        intent.putExtra("page", "Private");
                                        intent.putExtra("sdate", ec_date);
                                        intent.putExtra("edate", ee_date);
                                        startActivityForResult(intent, 90);
                                    } else {
                                        setPrivateDeal(mUrl, hid, rid.getText().toString(), pid.getText().toString());
                                    }
                                }
                                else{
                                    ShowPrivateDealDialog("프라이빗딜은 1일 1회 예약 가능합니다.\n내일 다시 시도해주세요.");
                                    return;
                                }
                            }
                        });

                        btn_reservation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (cookie == null) {
                                    Intent intent = new Intent(AllRoomTypeActivity.this, LoginActivity.class);
                                    intent.putExtra("ec_date", ec_date);
                                    intent.putExtra("ee_date", ee_date);
                                    intent.putExtra("pid", pid.getText());
                                    intent.putExtra("page", "detailH");
                                    startActivityForResult(intent,90);
                                    return;
                                }

                                Intent intent = new Intent(AllRoomTypeActivity.this, ReservationActivity.class);
                                intent.putExtra("ec_date", ec_date);
                                intent.putExtra("ee_date", ee_date);
                                intent.putExtra("pid", pid.getText());
                                intent.putExtra("page", "detailH");
                                startActivityForResult(intent,80);
                            }
                        });

                        btn_more_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(room_list.getChildAt((int)v.getTag()).findViewById(R.id.more_view).getVisibility() == View.VISIBLE){
                                    room_list.getChildAt((int)v.getTag()).findViewById(R.id.more_view).setVisibility(View.GONE);
                                    ((TextView)room_list.getChildAt((int)v.getTag()).findViewById(R.id.room_detail_close)).setText(R.string.btn_more2);
                                    ((ImageView)room_list.getChildAt((int)v.getTag()).findViewById(R.id.icon_more)).setBackgroundResource(R.drawable.btn_detail_open);
                                }
                                else{
                                    room_list.getChildAt((int)v.getTag()).findViewById(R.id.more_view).setVisibility(View.VISIBLE);
                                    ((TextView)room_list.getChildAt((int)v.getTag()).findViewById(R.id.room_detail_close)).setText(R.string.btn_more);
                                    ((ImageView)room_list.getChildAt((int)v.getTag()).findViewById(R.id.icon_more)).setBackgroundResource(R.drawable.btn_detail_close);
                                }
                            }
                        });

                        btn_more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(room_list.getChildAt((int)v.getTag()).findViewById(R.id.more_view).getVisibility() == View.VISIBLE){
                                    room_list.getChildAt((int)v.getTag()).findViewById(R.id.more_view).setVisibility(View.GONE);
                                    ((TextView)room_list.getChildAt((int)v.getTag()).findViewById(R.id.room_detail_close)).setText(R.string.btn_more2);
                                    ((ImageView)room_list.getChildAt((int)v.getTag()).findViewById(R.id.icon_more)).setBackgroundResource(R.drawable.btn_detail_open);
                                }
                                else{
                                    room_list.getChildAt((int)v.getTag()).findViewById(R.id.more_view).setVisibility(View.VISIBLE);
                                    ((TextView)room_list.getChildAt((int)v.getTag()).findViewById(R.id.room_detail_close)).setText(R.string.btn_more);
                                    ((ImageView)room_list.getChildAt((int)v.getTag()).findViewById(R.id.icon_more)).setBackgroundResource(R.drawable.btn_detail_close);
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
    private void setPrivateDeal(final String linkUrl, String Hotel_id, final String Room_id, final String mProduct_Id){
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("hotel_id", Hotel_id);
            paramObj.put("room_id", Room_id);
            paramObj.put("ec_date", ec_date);
            paramObj.put("ee_date", ee_date);
        } catch(Exception e){
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

                    String fullLinkUrl =linkUrl+"&bid_id="+obj.getJSONObject("data").getString("id")+"&refKey="+obj.getJSONObject("data").getString("refKey");
                    Intent intent = new Intent(AllRoomTypeActivity.this, PrivateDealActivity.class);
                    intent.putExtra("pid", mProduct_Id);
                    intent.putExtra("url", fullLinkUrl);
                    intent.putExtra("bid_id", obj.getJSONObject("data").getString("id"));
                    intent.putExtra("bid", Room_id);
                    intent.putExtra("ec_date", ec_date);
                    intent.putExtra("ee_date", ee_date);
                    intent.putExtra("city", city);
                    intent.putExtra("hotel_name", hotel_name);
                    startActivityForResult(intent, 80);

                } catch (Exception e) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ShowPrivateDealDialog(String msg){
        if(dialogAlert != null && dialogAlert.isShowing()){
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
        if(is_date){
            Intent intent = new Intent();
            intent.putExtra("ec_date", ec_date);
            intent.putExtra("ee_date", ee_date);
            intent.putExtra("is_date", true);
            setResult(80, intent);
        }

        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 80 && requestCode == 80){
            ec_date = data.getStringExtra("ec_date");
            ee_date = data.getStringExtra("ee_date");
            is_date = true;
            setDetailView();
        }
        else if(resultCode == 90 && requestCode == 90) {
            Intent intent = new Intent();
            intent.putExtra("ec_date", ec_date);
            intent.putExtra("ee_date", ee_date);
            setResult(110, intent);
            finish();
        }
        else if(resultCode == 100 && requestCode == 80) {
            setResult(100);
            finish();
        }
    }
}
