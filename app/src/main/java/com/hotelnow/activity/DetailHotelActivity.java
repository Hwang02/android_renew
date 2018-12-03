package com.hotelnow.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogConfirm;
import com.hotelnow.dialog.DialogCoupon;
import com.hotelnow.dialog.DialogShare;
import com.hotelnow.fragment.detail.HotelImageFragment;
import com.hotelnow.fragment.model.FacilitySelItem;
import com.hotelnow.fragment.model.RecentItem;
import com.hotelnow.fragment.model.TicketInfoEntry;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FlowLayout;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.HtmlTagHandler;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.ToughViewPager;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DetailHotelActivity extends AppCompatActivity {

    private ToughViewPager mViewPager;
    private TextView m_countView, m_img_title,tv_category, tv_hotelname,
            tv_minprice, tv_maxprice, tv_per, tv_review_rate, review_message,
            tv_review_count, tv_special_title, tv_checkin, tv_checkout, tv_total_count, tv_address;
    private String ec_date =null, ee_date =null, main_photo = "", hid, pid, evt;
    private ImageView sc_star1, sc_star2, sc_star3, sc_star4, sc_star5, map_img;
    private LinearLayout btn_more_review, coupon_list, room_list, btn_show_room,
            btn_more_view, bt_checkinout;
    private RelativeLayout rl_tv;
    private int isAcoupon[];
    private String mCouponId[];
    private String landscapeImgs[];
    private String captions[];
    private AutoLinkTextView tv_recommend;
    private String[] facility;
    private String image_arr[];
    private FlowLayout filter;
    private String mAddress, hotel_name, city;
    private Toolbar toolbar;
    private AppBarLayout app_bar;
    private static int PAGES = 0;
    private static int LOOPS = 1000;
    private static int FIRST_PAGE = 0;
    private static int nowPosition = 0;
    public static int markNowPosition = 0;
    private static int markPrevPosition = 0;
    private MyPagerAdapter mPagerAdapter;
    private SharedPreferences _preferences;
    private String cookie;
    private String[] selectList;
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    private String lodge_type;
    private int privatedeal_status = -1;
    private DialogAlert dialogAlert = null;
    private DialogShare dialogShare;
    private Double mAvg = 0.0;
    private DialogCoupon dialogCoupon;
    private ImageView icon_zzim;
    private boolean islikechange = false;
    private DbOpenHelper dbHelper;
    private String sdate, edate;
    private boolean isSave = false;
    public List<RecentItem> mFavoriteStayItem = new ArrayList<>();
    private String[] FavoriteStayList;
    private boolean islike = false;
    private RelativeLayout toast_layout;
    private ImageView ico_favorite;
    private TextView tv_toast;
    private boolean isLogin = false;
    private NestedScrollView scroll;
    private LinearLayout hotel_check_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        Util.setStatusTransColor(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        icon_zzim = (ImageView) toolbar.findViewById(R.id.icon_zzim);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        scroll = (NestedScrollView) findViewById(R.id.scroll);

        dbHelper = new DbOpenHelper(DetailHotelActivity.this);

        _preferences = PreferenceManager.getDefaultSharedPreferences(DetailHotelActivity.this);

        Intent intent = getIntent();
        hid = intent.getStringExtra("hid");
        pid = intent.getStringExtra("pid");
        evt = intent.getStringExtra("evt");

        isSave = intent.getBooleanExtra("save", false);

        // 찜인지 아닌지 확인
        mFavoriteStayItem = dbHelper.selectAllFavoriteStayItem();

        cookie = _preferences.getString("userid", null);

        sdate = intent.getStringExtra("sdate");
        edate = intent.getStringExtra("edate");

        if(sdate==null && edate==null) {
            ec_date = Util.setCheckinout().get(0);
            ee_date = Util.setCheckinout().get(1);
        }
        else{
            ec_date = sdate;
            ee_date = edate;
        }

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finished();
            }
        });
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
           boolean isShow = true;
           int scrollRange = -1;
           @Override
           public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
               app_bar.post(new Runnable() {
                   @Override
                   public void run() {
                       if (scrollRange == -1) {
                           scrollRange = appBarLayout.getTotalScrollRange();
                       }
                       if (scrollRange + verticalOffset == 0) {
                           ((TextView) toolbar.findViewById(R.id.tv_title_bar)).setText(hotel_name);
                           ((TextView) toolbar.findViewById(R.id.tv_title_bar)).setTextColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.purple));
                           findViewById(R.id.btn_share).setVisibility(View.GONE);
                           // 찜상품이면 빨강색으로 변경
                           if(islike) {
                               icon_zzim.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                           }
                           else{
                               icon_zzim.setBackgroundResource(R.drawable.ico_titbar_favorite);
                           }
                           findViewById(R.id.icon_back).setBackgroundResource(R.drawable.ico_titbar_back);
                           toolbar.setBackgroundResource(R.color.white);
                           isShow = true;
                       } else if (isShow) {
                           ((TextView) toolbar.findViewById(R.id.tv_title_bar)).setText("");
                           findViewById(R.id.btn_share).setVisibility(View.VISIBLE);
                           // 찜상품이면 빨강색으로 변경
                           if(islike) {
                               icon_zzim.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                           }
                           else{
                               icon_zzim.setBackgroundResource(R.drawable.ico_titbarw_favorite);
                           }
                           findViewById(R.id.icon_back).setBackgroundResource(R.drawable.ico_titbarw_back);
                           toolbar.setBackgroundResource(android.R.color.transparent);
                           isShow = false;
                       }
                   }
               });
           }
       });

        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShare = new DialogShare(DetailHotelActivity.this, hid, main_photo, hotel_name, ec_date, ee_date, mAvg, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogShare.dismiss();
                    }
                });
                dialogShare.show();
            }
        });

        icon_zzim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject paramObj = new JSONObject();
                try {
                    paramObj.put("type", "stay");
                    paramObj.put("id", hid);
                } catch(Exception e){
                    Log.e(CONFIG.TAG, e.toString());
                }
                if(islike){// 취소
                    Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                        @Override
                        public void onFailure(Response response, Exception throwable) {
                            Toast.makeText(DetailHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Map<String, String> headers, String body) {
                            try {
                                JSONObject obj = new JSONObject(body);
                                if (!obj.has("result") || !obj.getString("result").equals("success")) {
                                    showToast("로그인 후 이용해주세요");
                                    return;
                                }
                                islike = false;
                                dbHelper.deleteFavoriteItem(false,  hid,"H");
                                icon_zzim.setBackgroundResource(R.drawable.ico_titbarw_favorite);
                                LogUtil.e("xxxx", "찜하기 취소");
                                showIconToast("관심 상품 담기 취소", true);
                                islikechange = true;
                            }catch (JSONException e){

                            }
                        }
                    });
                }
                else{// 성공
                    Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                        @Override
                        public void onFailure(Response response, Exception throwable) {
                            Toast.makeText(DetailHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Map<String, String> headers, String body) {
                            try {
                                JSONObject obj = new JSONObject(body);
                                if (!obj.has("result") || !obj.getString("result").equals("success")) {
                                    showToast("로그인 후 이용해주세요");
                                    return;
                                }
                                islike = true;
                                dbHelper.insertFavoriteItem(hid,"H");
                                icon_zzim.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                                LogUtil.e("xxxx", "찜하기 성공");
                                showIconToast("관심 상품 담기 성공", true);
                                islikechange = true;
                            }catch (JSONException e){

                            }
                        }
                    });
                }
            }
        });

        setDetailView();
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
                Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(getApplicationContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String fullLinkUrl =linkUrl+"&bid_id="+obj.getJSONObject("data").getString("id")+"&refKey="+obj.getJSONObject("data").getString("refKey");
                    Intent intent = new Intent(DetailHotelActivity.this, PrivateDealActivity.class);
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
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDetailView(){
        String url = CONFIG.hotel_detail + "/" + hid + "?pid=" + pid + "&evt=" + evt+ "&user_id="+cookie;
        if (ec_date != null && ee_date != null) {
            url += "&ec_date=" + ec_date + "&ee_date=" + ee_date + "&consecutive=Y";
        }

        Api.get(url, new Api.HttpCallback() {

            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(DetailHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(DetailHotelActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(isSave) {
                        dbHelper.insertRecentItem(hid, "H");
                    }

                    final JSONObject hotel_data = obj.getJSONObject("hotel");
                    final JSONArray room_data = obj.getJSONArray("room_types");
                    final JSONArray photos = hotel_data.getJSONArray("photos");
                    final JSONArray notes_array = hotel_data.getJSONArray("notes_array");
                    final JSONObject review_data = obj.getJSONObject("review_info");
                    final JSONArray avail_dates = obj.getJSONArray("avail_dates");
                    final JSONArray instant_coupons = obj.getJSONArray("instant_coupons");

                    if(cookie != null) {
                        if (mFavoriteStayItem.size() > 0) {
                            FavoriteStayList = new String[mFavoriteStayItem.size()];
                            for (int i = 0; i < mFavoriteStayItem.size(); i++) {
                                FavoriteStayList[i] = mFavoriteStayItem.get(i).getSel_id();
                            }

                            if (Arrays.asList(FavoriteStayList).contains(hid)) {
                                islike = true;
                            } else {
                                islike = false;
                            }
                        } else {
                            islike = false;
                        }
                    }

                    if(islike){
                        icon_zzim.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
                    }

                    tv_category = (TextView) findViewById(R.id.tv_category);
                    tv_hotelname = (TextView) findViewById(R.id.tv_hotelname);
                    tv_minprice = (TextView) findViewById(R.id.tv_minprice);
                    tv_maxprice = (TextView) findViewById(R.id.tv_maxprice);
                    tv_per = (TextView) findViewById(R.id.tv_per);
                    tv_review_rate = (TextView) findViewById(R.id.tv_review_rate);
                    btn_more_review = (LinearLayout) findViewById(R.id.btn_more_review);
                    tv_review_count = (TextView) findViewById(R.id.tv_review_count);
                    tv_special_title = (TextView) findViewById(R.id.tv_special_title);
                    coupon_list = (LinearLayout) findViewById(R.id.coupon_list);
                    tv_checkin = (TextView) findViewById(R.id.tv_checkin);
                    tv_checkout = (TextView) findViewById(R.id.tv_checkout);
                    room_list = (LinearLayout) findViewById(R.id.room_list);
                    btn_show_room = (LinearLayout) findViewById(R.id.btn_show_room);
                    tv_total_count = (TextView) findViewById(R.id.tv_total_count);
                    tv_recommend = (AutoLinkTextView) findViewById(R.id.tv_recommend);
                    btn_more_view = (LinearLayout) findViewById(R.id.btn_more_view);
                    rl_tv = (RelativeLayout) findViewById(R.id.rl_tv);
                    filter = (FlowLayout) findViewById(R.id.filter);
                    map_img = (ImageView) findViewById(R.id.map_img);
                    tv_address = (TextView) findViewById(R.id.tv_address);
                    mViewPager = (ToughViewPager) findViewById(R.id.img_pager);
                    m_countView = (TextView) findViewById(R.id.page);
                    m_img_title = (TextView) findViewById(R.id.img_title);
                    bt_checkinout = (LinearLayout) findViewById(R.id.bt_checkinout);
                    hotel_check_list = (LinearLayout) findViewById(R.id.hotel_check_list);

                    if(hotel_data.getString("is_private_deal").equals("Y")){
                        findViewById(R.id.ico_private).setVisibility(View.VISIBLE);
                    }
                    else{
                        findViewById(R.id.ico_private).setVisibility(View.GONE);
                    }
                    if(hotel_data.getString("is_hot_deal").equals("Y")){
                        findViewById(R.id.ico_hotdeal).setVisibility(View.VISIBLE);
                    }
                    else{
                        findViewById(R.id.ico_hotdeal).setVisibility(View.GONE);
                    }
                    if(hotel_data.getString("is_add_reserve").equals("Y")) {
                        findViewById(R.id.ico_addpoint).setVisibility(View.VISIBLE);
                    }
                    else{
                        findViewById(R.id.ico_addpoint).setVisibility(View.GONE);
                    }

                    // 선택 될 날짜
                    if(ec_date == null && ee_date==null) {
                        ec_date = avail_dates.get(0).toString();
                        ee_date = avail_dates.get(1).toString();
                    }
                    selectList = new String[avail_dates.length()];
                    for(int i =0; i<avail_dates.length(); i++){
                        selectList[i] = avail_dates.get(i).toString();
                    }

                    privatedeal_status = obj.getInt("privatedeal_booking_status");
                    lodge_type = hotel_data.getString("lodge_type");

                    // 호텔 이미지
                    landscapeImgs = new String[photos.length()];
                    captions = new String[photos.length()];

                    for (int i = 0; i < photos.length(); i++) {
                        landscapeImgs[i] = photos.getJSONObject(i).getString("landscape");
                        captions[i] = photos.getJSONObject(i).has("caption1") ? photos.getJSONObject(i).getString("caption1") : "";
                        if(photos.getJSONObject(i).getString("img_type").equals("m")) {
                            main_photo = photos.getJSONObject(i).has("landscape") ? photos.getJSONObject(i).getString("landscape") : "";
                        }
                    }

                    showPager();

                    // 카테고리
                    tv_category.setText(Util.getCategory(DetailHotelActivity.this, hotel_data.getString("category")));

                    hotel_name = hotel_data.getString("name");
                    city = hotel_data.getString("city_name");
                    tv_hotelname.setText(hotel_name);
                    tv_minprice.setText(Util.numberFormat(hotel_data.getInt("sale_price")));

                    //원 금액
                    tv_maxprice.setText(Util.numberFormat(hotel_data.getInt("normal_price"))+"원");
                    tv_maxprice.setPaintFlags(tv_maxprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    tv_per.setText(hotel_data.getString("sale_rate"));

                    //평점
                    mAvg = review_data.getDouble("avg");
                    tv_review_rate.setText(review_data.getDouble("avg")+"");
                    setReviewRate(review_data.getDouble("avg"));
                    tv_review_count.setText(review_data.getInt("cnt")+"");
                    if(review_data.getInt("cnt") == 0){
                        btn_more_review.setVisibility(View.GONE);
                    }
                    else{
                        btn_more_review.setVisibility(View.VISIBLE);
                    }
                    final Double r1, r2, r3, r4, avg;
                    r1 = review_data.getDouble("r1");
                    r2 = review_data.getDouble("r2");
                    r3 = review_data.getDouble("r3");
                    r4 = review_data.getDouble("r4");
                    avg = review_data.getDouble("avg");


                    btn_more_review.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailHotelActivity.this, ReviewHotelActivity.class);
                            intent.putExtra("avg", avg);
                            intent.putExtra("r1", r1);
                            intent.putExtra("r2", r2);
                            intent.putExtra("r3", r3);
                            intent.putExtra("r4", r4);
                            intent.putExtra("hid", hid);
                            intent.putExtra("title", tv_hotelname.getText());
                            startActivity(intent);
                        }
                    });


                    //스페셜 메시지
                    if(hotel_data.has("special_msg")){
                        if(!TextUtils.isEmpty(hotel_data.getString("special_msg")) && !hotel_data.getString("special_msg").equals("null")) {
                            findViewById(R.id.view_detail_special).setVisibility(View.VISIBLE);
                            tv_special_title.setText(hotel_data.getString("special_msg"));
                        }
                        else{
                            findViewById(R.id.view_detail_special).setVisibility(View.GONE);
                        }
                    }
                    else{
                        findViewById(R.id.view_detail_special).setVisibility(View.GONE);
                    }

                    // 쿠폰
                    if(instant_coupons.length()>0) {
                        coupon_list.setVisibility(View.VISIBLE);
                        setCoupon(instant_coupons);
                    }
                    else{
                        coupon_list.setVisibility(View.GONE);
                    }

                    // 체크인 체크아웃
                    tv_checkin.setText(Util.formatchange(ec_date));
                    tv_checkout.setText(Util.formatchange(ee_date));
                    bt_checkinout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailHotelActivity.this, CalendarActivity.class);
                            intent.putExtra("ec_date", ec_date);
                            intent.putExtra("ee_date", ee_date);
                            intent.putExtra("selectList", selectList);
                            intent.putExtra("lodge_type", lodge_type);
                            startActivityForResult(intent, 80);
                        }
                    });

                    // 룸정보
                    setRoom(room_data);

                    // 추천이유
                    if(hotel_data.has("notes_array")){
                        for(int i=0; i<hotel_data.getJSONArray("notes_array").length(); i++){
                            if(hotel_data.getJSONArray("notes_array").getJSONObject(i).getString("title").equals("추천이유")) {
                                String s_html = hotel_data.getJSONArray("notes_array").getJSONObject(i).getString("content")
                                        .replace("\r\n","")
                                        .replace("</span>","");
                                tv_recommend.setText(Html.fromHtml(s_html));
                                break;
                            }
                        }

                        btn_more_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(findViewById(R.id.blur_view).getVisibility() == View.VISIBLE) {
                                    findViewById(R.id.blur_view).setVisibility(View.GONE);
                                    rl_tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    ((TextView) findViewById(R.id.tv_recommend_title)).setText(R.string.more_close_title);
                                    ((ImageView) findViewById(R.id.icon_recommend)).setBackgroundResource(R.drawable.btn_detail_close);
                                }
                                else{
                                    findViewById(R.id.blur_view).setVisibility(View.VISIBLE);
                                    rl_tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dptopixel(DetailHotelActivity.this,162)));
                                    ((TextView) findViewById(R.id.tv_recommend_title)).setText(R.string.more_title);
                                    ((ImageView) findViewById(R.id.icon_recommend)).setBackgroundResource(R.drawable.btn_detail_open);
                                }
                            }
                        });
                    }

                    // 편의 시설 및 서비스
                    int[] mFaArray = {R.drawable.facility_parking, R.drawable.facility_wifi, R.drawable.facility_desk, R.drawable.facility_baggage, R.drawable.facility_breakfast, R.drawable.facility_hall, R.drawable.facility_amenity, R.drawable.facility_business, R.drawable.facility_fitness, R.drawable.facility_massage, R.drawable.facility_sauna, R.drawable.facility_swim, R.drawable.facility_swim1, R.drawable.facility_restaurant1, R.drawable.facility_restaurant, R.drawable.facility_bar, R.drawable.facility_coffee, R.drawable.facility_washfree, R.drawable.facility_wash, R.drawable.facility_notebook, R.drawable.facility_smoke, R.drawable.facility_smoke1, R.drawable.facility_terrace, R.drawable.facility_shuttle, R.drawable.facility_kitchen, R.drawable.facility_fork, R.drawable.facility_animal, R.drawable.facility_song, R.drawable.facility_bicycle, R.drawable.facility_kitchen1, R.drawable.facility_livingroom, R.drawable.facility_movement, R.drawable.facility_pc, R.drawable.facility_selfbar, R.drawable.facility_ocean};
                    facility = hotel_data.getString("facility").split(",");

                    String[] facilityarr = getResources().getStringArray(R.array.facility_text);
                    List<FacilitySelItem> sel_FacilityList = new ArrayList<>();

                    for (int i = 0; i < mFaArray.length; i++) {
                        if (Util.in_array(facility, String.valueOf(i))) {
                            sel_FacilityList.add(new FacilitySelItem(mFaArray[i], facilityarr[i]));
                        }
                    }

                    setFacility(sel_FacilityList, false);

                    // 주소
                    String mapStr = "https://maps.googleapis.com/maps/api/staticmap?center="+hotel_data.getString("latitude")+"%2C"+hotel_data.getString("longuitude")+
                            "&markers=icon:http://hotelnow.s3.amazonaws.com/etc/20181012_180827_hozDzSdI4I.png%7C"+hotel_data.getString("latitude")+"%2C"+hotel_data.getString("longuitude")+
                            "&scale=2&sensor=false&language=ko&size=360x220&zoom=15"+"&key="+ BuildConfig.google_map_key2;
                    Ion.with(map_img).load(mapStr);
                    mAddress = hotel_data.getString("address_street");
                    tv_address.setText(mAddress);

                    findViewById(R.id.btn_address_copy).setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) HotelnowApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", mAddress);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(DetailHotelActivity.this, "주소가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    findViewById(R.id.btn_address_near).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 주변보기
                            Intent intent = new Intent(DetailHotelActivity.this, MapActivity.class);
                            intent.putExtra("from", "pdetail");
                            intent.putExtra("title", hotel_name);
                            intent.putExtra("hid", hid);
                            intent.putExtra("ec_date", ec_date);
                            intent.putExtra("ee_date", ee_date);
                            startActivityForResult(intent, 81); // 81 지도보기
                        }
                    });

                    findViewById(R.id.btn_kakao).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.kakaoYelloId2(DetailHotelActivity.this);
                        }
                    });

                    findViewById(R.id.room_select).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailHotelActivity.this, AllRoomTypeActivity.class);
                            intent.putExtra("sdate", ec_date);
                            intent.putExtra("edate", ee_date);
                            intent.putExtra("hid", hid);
                            intent.putExtra("pid", pid);
                            intent.putExtra("evt", evt);
                            startActivityForResult(intent, 80);
                        }
                    });

                    //이용 정보
                    ArrayList<TicketInfoEntry> infolist = new ArrayList<>();

                    if(notes_array.length() > 0) {
                        for (int i = 0; i < notes_array.length(); i++) {
                            infolist.add(new TicketInfoEntry(notes_array.getJSONObject(i).getString("title"), notes_array.getJSONObject(i).getString("content")));
                        }

                        if (infolist.size() > 0) {
                            hotel_check_list.removeAllViews();
                            for (int i = 0; i < infolist.size(); i++) {
                                View info_view = LayoutInflater.from(DetailHotelActivity.this).inflate(R.layout.layout_ticket_info, null);
                                AutoLinkTextView title_sub = (AutoLinkTextView) info_view.findViewById(R.id.title_sub);
                                TextView title = (TextView) info_view.findViewById(R.id.title);
                                title_sub.addAutoLinkMode(
                                        AutoLinkMode.MODE_PHONE,
                                        AutoLinkMode.MODE_URL);
                                title_sub.setPhoneModeColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.purple));
                                title_sub.setUrlModeColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.private_discount));
                                title_sub.setText(Html.fromHtml(infolist.get(i).getmMessage().replace("&nbsp", "").replace("• ", "ㆍ").replace("\n\n","")));
                                title.setText(infolist.get(i).getmTitle());

                                hotel_check_list.addView(info_view);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.getStackTrace();
                    LogUtil.e("xxxxx", e.getMessage());
                    Toast.makeText(DetailHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showPager() {

        PAGES = landscapeImgs.length;
        FIRST_PAGE = PAGES * LOOPS / 2;

        m_countView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailHotelActivity.this, FullImageViewActivity.class);
                intent.putExtra("hid", hid);
                intent.putExtra("idx", markNowPosition);
                intent.putExtra("name", hotel_name);
                startActivity(intent);
            }
        });

        try {
            if(captions.length>0) {
                if (TextUtils.isEmpty(captions[0])) {
                    m_img_title.setVisibility(View.GONE);
                } else {
                    m_img_title.setVisibility(View.VISIBLE);
                }
            }
            mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), landscapeImgs, captions);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.addOnPageChangeListener(mPagerAdapter);
            mViewPager.setCurrentItem(FIRST_PAGE, true);
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    try {
                        nowPosition = position;
                        markNowPosition = position % PAGES;

                        m_countView.setText(markNowPosition+1+"/"+landscapeImgs.length+" + ");
                        m_img_title.setText(captions[markNowPosition]);
                        if (TextUtils.isEmpty(captions[markNowPosition])){
                            m_img_title.setVisibility(View.GONE);
                        }
                        else{
                            m_img_title.setVisibility(View.VISIBLE);
                        }
                        markPrevPosition = markNowPosition;
                    } catch (Exception e) {
                        Util.doRestart(getApplicationContext());
                    }
                }

                @Override
                public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
                    if (PAGES != 0)
                        markNowPosition = position % PAGES;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } catch (Exception e) {
            e.getStackTrace();
            Util.doRestart(getApplicationContext());
        }

        nowPosition = FIRST_PAGE;
        initPageMark();
    }

    private void initPageMark() {

        m_countView.setText(1+"/"+landscapeImgs.length+" + ");

        markPrevPosition = markNowPosition;
    }

    private void setFacility(final List<FacilitySelItem> sel_FacilityList, boolean isOpen){
        filter.removeAllViews();
        for(int i=0;i<sel_FacilityList.size();i++){
            View child = getLayoutInflater().inflate(R.layout.layout_detail_facility_item, null);
            child.setId(i);
            child.setTag(i);
            TextView facility_title = (TextView)child.findViewById(R.id.facility_title);
            ImageView facility_icon = (ImageView)child.findViewById(R.id.facility_icon);
            facility_title.setText(sel_FacilityList.get(i).getSel_title());
            facility_icon.setImageResource(sel_FacilityList.get(i).getSel_img());
            if(!isOpen) {
                if(sel_FacilityList.size() <= 10) {
                    filter.addView(child);
                }
                else if (sel_FacilityList.size() > 10){
                    if (i < 9) {
                        filter.addView(child);
                    }else if(i == 9){
                        View btn_child = getLayoutInflater().inflate(R.layout.layout_detail_facility_item, null);
                        btn_child.setTag(36);
                        TextView facility_title2 = (TextView)btn_child.findViewById(R.id.facility_title);
                        ImageView facility_icon2 = (ImageView)btn_child.findViewById(R.id.facility_icon);
                        facility_title2.setText(R.string.more_title);
                        facility_icon2.setBackgroundResource(R.drawable.ico_f_more);
                        btn_child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setFacility(sel_FacilityList,true);
                            }
                        });
                        filter.addView(btn_child);
                        break;
                    }
                }
            }
            else{
                filter.addView(child);
                if(i == sel_FacilityList.size()-1){
                    View btn_child = getLayoutInflater().inflate(R.layout.layout_detail_facility_item, null);
                    btn_child.setTag(37);
                    TextView facility_title2 = (TextView)btn_child.findViewById(R.id.facility_title);
                    ImageView facility_icon2 = (ImageView)btn_child.findViewById(R.id.facility_icon);
                    facility_title2.setText(R.string.more_close_title);
                    facility_icon2.setBackgroundResource(R.drawable.ico_f_close);
                    btn_child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setFacility(sel_FacilityList,false);
                        }
                    });
                    filter.addView(btn_child);
                }
            }
        }
    }

    private void setRoom(final JSONArray rdata) {
        try{
            SpannableStringBuilder sb = new SpannableStringBuilder();
            String total_cnt = "총 "+rdata.length()+"개의 객실 전체보기";
            int cnt_len = 0;
            int for_cnt = 0;
            if(rdata.length() < 10){
                cnt_len = 1;
            }
            else if(rdata.length() > 9 && rdata.length() < 100){
                cnt_len = 2;
            }
            else if(rdata.length() > 99 && rdata.length() < 999){
                cnt_len = 3;
            }

            sb.append(total_cnt);
            sb.setSpan(new StyleSpan(Typeface.BOLD),2, cnt_len+3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_total_count.setText(sb);

            room_list.removeAllViews();

            if(rdata.length() <= 4){
                for_cnt = rdata.length();
                btn_show_room.setVisibility(View.GONE);
            }
            else {
                for_cnt = 4;
                btn_show_room.setVisibility(View.VISIBLE);
                btn_show_room.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailHotelActivity.this, AllRoomTypeActivity.class);
                        intent.putExtra("sdate", ec_date);
                        intent.putExtra("edate", ee_date);
                        intent.putExtra("hid", hid);
                        intent.putExtra("pid", pid);
                        intent.putExtra("evt", evt);

                        startActivityForResult(intent, 80);
                    }
                });
            }

            for (int i = 0; i < for_cnt; i++) {
                View view_room = LayoutInflater.from(DetailHotelActivity.this).inflate(R.layout.layout_detail_hotel_room_item, null);
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
                }

                more_img_list.removeAllViews();
                for(int j=0; j<image_arr.length;j++){
                    View view_img = LayoutInflater.from(DetailHotelActivity.this).inflate(R.layout.layout_detail_room_img_item, null);
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
                                Intent intent = new Intent(DetailHotelActivity.this, LoginActivity.class);
                                intent.putExtra("page", "Private");
                                intent.putExtra("sdate", ec_date);
                                intent.putExtra("edate", ee_date);
                                startActivityForResult(intent, 80);
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
                            Intent intent = new Intent(DetailHotelActivity.this, LoginActivity.class);
                            intent.putExtra("ec_date", ec_date);
                            intent.putExtra("ee_date", ee_date);
                            intent.putExtra("pid", pid.getText());
                            intent.putExtra("page", "detailH");
                            startActivityForResult(intent,90);
                            return;
                        }

                        Intent intent = new Intent(DetailHotelActivity.this, ReservationActivity.class);
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
        }
        catch (Exception e) {
            e.getStackTrace();
            LogUtil.e("xxxxx", e.getMessage());
            Toast.makeText(DetailHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
        }

    }

    private void ShowPrivateDealDialog(String msg){
        if(dialogAlert != null && dialogAlert.isShowing()){
            dialogAlert.dismiss();
        }
        dialogAlert = new DialogAlert(
                getString(R.string.alert_notice),
                msg,
                DetailHotelActivity.this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogAlert.dismiss();
                    }
                });
        dialogAlert.setCancelable(false);
        dialogAlert.show();
    }

    private void setCoupon(final JSONArray cdata) {
        try{
            coupon_list.removeAllViews();
            isAcoupon = new int[cdata.length()];
            mCouponId = new String[cdata.length()];

            for (int i = 0; i < cdata.length(); i++) {
                View view_coupon = LayoutInflater.from(DetailHotelActivity.this).inflate(R.layout.layout_detail_coupon_item, null);
                TextView tv_coupon_price = (TextView) view_coupon.findViewById(R.id.tv_coupon_price);
                TextView tv_coupon_title = (TextView) view_coupon.findViewById(R.id.tv_coupon_title);
                ImageView icon_coupon = (ImageView) view_coupon.findViewById(R.id.icon_coupon);
                ImageView icon_download = (ImageView) view_coupon.findViewById(R.id.icon_download);
                tv_coupon_title.setText(cdata.getJSONObject(i).getString("name"));
                tv_coupon_price.setText(cdata.getJSONObject(i).getString("coupon_price"));
                if(cdata.getJSONObject(i).getInt("mycoupon_cnt") == 0){
                    //사용가능
                    tv_coupon_price.setTextColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.redtext));
                    tv_coupon_title.setTextColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.blacktxt));
                    icon_coupon.setBackgroundResource(R.drawable.ico_coupon);
                    icon_download.setBackgroundResource(R.drawable.ico_download);
                }
                else {
                    //불가능
                    tv_coupon_price.setTextColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.coupon_dis));
                    tv_coupon_title.setTextColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.coupon_dis));
                    icon_coupon.setBackgroundResource(R.drawable.ico_coupon_dis);
                    icon_download.setBackgroundResource(R.drawable.ico_download_dis);
                }
                view_coupon.setTag(i);
                isAcoupon[i] = cdata.getJSONObject(i).getInt("mycoupon_cnt");
                mCouponId[i] = cdata.getJSONObject(i).getString("id");
                view_coupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtil.e("xxxx", v.getTag()+"");
                        setCouponDown((int)v.getTag(), cdata);
                    }
                });
                view_coupon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dptopixel(DetailHotelActivity.this,54)));
                coupon_list.addView(view_coupon);
            }
        }
        catch (Exception e) {
            e.getStackTrace();
            Toast.makeText(DetailHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
        }
    }

    private void setCouponDown(final int position, final JSONArray cdata){
        Api.get(CONFIG.promotionUrl3+"/"+mCouponId[position], new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(DetailHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(DetailHotelActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    isAcoupon[position] = 1;
                    TextView tv_coupon_price = (TextView) coupon_list.getChildAt(position).findViewById(R.id.tv_coupon_price);
                    TextView tv_coupon_title = (TextView) coupon_list.getChildAt(position).findViewById(R.id.tv_coupon_title);
                    ImageView icon_coupon = (ImageView) coupon_list.getChildAt(position).findViewById(R.id.icon_coupon);
                    ImageView icon_download = (ImageView) coupon_list.getChildAt(position).findViewById(R.id.icon_download);

                    tv_coupon_price.setTextColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.coupon_dis));
                    tv_coupon_title.setTextColor(ContextCompat.getColor(DetailHotelActivity.this, R.color.coupon_dis));
                    icon_coupon.setBackgroundResource(R.drawable.ico_coupon_dis);
                    icon_download.setBackgroundResource(R.drawable.ico_download_dis);

                    showCouponDialog(obj.getString("msg"));
                } catch (Exception e) {
                    Toast.makeText(DetailHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //쿠폰 다이얼로그
    private void showCouponDialog(String message){
        dialogCoupon = new DialogCoupon(
                DetailHotelActivity.this,
                getString(R.string.coupon_title2),
                hotel_name+"\n"+message +"\n\n지금 바로예약하세요",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogCoupon.dismiss();
                    }
                }
        );
        dialogCoupon.setCancelable(false);
        dialogCoupon.show();
    }

    private void setReviewRate(Double rate){
        review_message = (TextView) findViewById(R.id.review_message);
        sc_star1 = (ImageView) findViewById(R.id.sc_star1);
        sc_star2 = (ImageView) findViewById(R.id.sc_star2);
        sc_star3 = (ImageView) findViewById(R.id.sc_star3);
        sc_star4 = (ImageView) findViewById(R.id.sc_star4);
        sc_star5 = (ImageView) findViewById(R.id.sc_star5);

        if(rate>4) {
            review_message.setText("최고에요! 강추!");
        }
        else if(rate>=3.5) {
            review_message.setText("좋았어요! 추천해요!");
        }
        else if(rate>=3) {
            review_message.setText("좋았어요!");
        }
        else if(rate>=2) {
            review_message.setText("보통이에요.");
        }
        else {
            review_message.setText("그럭저럭");
        }
        setStar(rate, sc_star1, sc_star2,sc_star3 ,sc_star4,sc_star5);
    }

    private void setStar(double mScore, ImageView imgStar1, ImageView imgStar2, ImageView imgStar3, ImageView imgStar4, ImageView imgStar5){
        if (mScore < 1){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_half);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if (mScore == 1){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if (mScore < 2){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_half);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if (mScore == 2){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore < 3){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_half);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore == 3){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore < 4){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_half);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore == 4){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore < 5){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_half);
        }
        else if(mScore == 5){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_press);
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private String landscapeImgs[];
        private String captionList[];

        public MyPagerAdapter(FragmentManager frgm, String[] imgs, String[] captions) {
            super(frgm);
            landscapeImgs = imgs;
            captionList = captions;
        }

        @Override
        public Fragment getItem(int position) {
            if (PAGES != 0)
                position = position % PAGES;

            return HotelImageFragment.newInstance(DetailHotelActivity.this, position, landscapeImgs[position], hid, captionList[position]);
        }

        @Override
        public int getCount() {
            return PAGES * LOOPS;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public void showToast(String msg){
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);
        findViewById(R.id.ico_favorite).setVisibility(View.VISIBLE);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 2000);
    }

    public void showIconToast(String msg, boolean is_fav){
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);

        if(is_fav) { // 성공
            ico_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        }
        else{ // 취소
            ico_favorite.setBackgroundResource(R.drawable.ico_titbar_favorite);
        }
        ico_favorite.setVisibility(View.VISIBLE);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 2000);
    }

    @Override
    public void onBackPressed() {
        finished();
        super.onBackPressed();
    }

    public void finished(){
        if(islikechange) {
            setResult(80);
        }
        else if(isLogin){
            setResult(110);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 80 && requestCode == 80){
            ec_date = data.getStringExtra("ec_date");
            ee_date = data.getStringExtra("ee_date");
            setDetailView();
        }
        else if(resultCode == 81 && requestCode == 81){
            ec_date = data.getStringExtra("ec_date");
            ee_date = data.getStringExtra("ee_date");
            hid = data.getStringExtra("hid");
            isSave = true;
            scroll.fling(0);
            scroll.smoothScrollTo(0, 0);
            setDetailView();
        }
        else if(resultCode == 90 && requestCode == 90) {
            isLogin = true;
            cookie = _preferences.getString("userid", null);
        }
        else if(resultCode == 100 && requestCode == 80) {
            setResult(100);
            finish();
        }
        else if(resultCode == 110 && requestCode == 80){
            isLogin = true;
            cookie = _preferences.getString("userid", null);
            ec_date = data.getStringExtra("ec_date");
            ee_date = data.getStringExtra("ee_date");
            setDetailView();
        }
    }
}
