package com.hotelnow.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogCoupon;
import com.hotelnow.dialog.DialogShare;
import com.hotelnow.dialog.DialogTicketShare;
import com.hotelnow.fragment.detail.ActivityImageFragment;
import com.hotelnow.fragment.model.RecentItem;
import com.hotelnow.fragment.model.TicketInfoEntry;
import com.hotelnow.fragment.model.TicketSelEntry;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.CustomLinkMovementMethod;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.ToughViewPager;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DetailActivityActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout app_bar;
    private DialogShare dialogShare;
    private String tid, cookie, tname, mCity;
    private SharedPreferences _preferences;
    private String maplat, maplon;
    private TextView tv_special_title, m_countView, m_img_title;
    private String PagerImgs[];
    private String InfoImgs[];
    private int pager_cnt=0;
    private int info_cnt=0;
    public static int PAGES = 0;
    public static int LOOPS = 1000;
    public static int FIRST_PAGE = 0;
    public static int nowPosition = 0;
    public static int markNowPosition = 0;
    public static int markPrevPosition = 0;
    private TicketPagerAdapter mPagerAdapter;
    private ToughViewPager mViewPager;
    private LinearLayout room_list, coupon_list, btn_more_detail, info_list, btn_more_review;
    private int ticket_count = 0;
    private List<TicketSelEntry> sel_list = new ArrayList<TicketSelEntry>();
    private int isAcoupon[];
    private String mCouponId[];
    private TextView tv_more, tv_product_info, tv_address;
    private ImageView img_more;
    private WebView webview;
    private ImageView map_img;
    private String mAddress;
    private TextView tv_category, tv_hotelname,
            tv_minprice, tv_maxprice, tv_per, tv_review_rate, tv_review_count,review_message;
    private ImageView sc_star1, sc_star2, sc_star3, sc_star4, sc_star5;
    private Button btn_reservation;
    private DialogAlert dialogAlert;
    private DialogCoupon dialogCoupon;
    private boolean isSave = false;
    private DbOpenHelper dbHelper;
    private String[] FavoriteActivityList;
    public List<RecentItem> mFavoriteActivityItem = new ArrayList<>();
    private boolean islike = false;
    private ImageView icon_zzim;
    private boolean islikechange = false;
    private DialogTicketShare dialogTicketShare;
    private Double mAvg = 0.0;
    private RelativeLayout toast_layout;
    private ImageView ico_favorite;
    private TextView tv_toast;
    private boolean isLogin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activity_detail);

        Util.setStatusTransColor(this);
        _preferences = PreferenceManager.getDefaultSharedPreferences(DetailActivityActivity.this);
        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        cookie = _preferences.getString("userid", null);
        isSave = intent.getBooleanExtra("save", false);
        dbHelper = new DbOpenHelper(this);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);

        mFavoriteActivityItem = dbHelper.selectAllFavoriteActivityItem();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        icon_zzim = (ImageView) toolbar.findViewById(R.id.icon_zzim);

        findViewById(R.id.btn_kakao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.kakaoYelloId2(DetailActivityActivity.this);
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
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
                            ((TextView) toolbar.findViewById(R.id.tv_title_bar)).setText(tname);
                            ((TextView) toolbar.findViewById(R.id.tv_title_bar)).setTextColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.purple));
                            findViewById(R.id.btn_share).setVisibility(View.GONE);
                            // 찜상품이면 빨강색으로 변경
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

        icon_zzim.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                JSONObject paramObj = new JSONObject();
                try {
                    paramObj.put("type", "activity");
                    paramObj.put("id", tid);
                } catch(Exception e){
                    Log.e(CONFIG.TAG, e.toString());
                }
                if(islike){// 취소
                    Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                        @Override
                        public void onFailure(Response response, Exception throwable) {
                            Toast.makeText(DetailActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
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
                                dbHelper.deleteFavoriteItem(false,  tid,"A");
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
                            Toast.makeText(DetailActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
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
                                dbHelper.insertFavoriteItem(tid,"A");
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

        findViewById(R.id.btn_share).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialogTicketShare = new DialogTicketShare(DetailActivityActivity.this, tid, PagerImgs[0], tname, mAvg, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogTicketShare.dismiss();
                    }
                });
                dialogTicketShare.show();
            }
        });

        setDetailData();
    }

    private void setDetailData(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.ticketdetailUrl_v2+"/"+ tid;

        Api.get(url, new Api.HttpCallback() {

            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(DetailActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        MainActivity.hideProgress();
                        Toast.makeText(DetailActivityActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    if(isSave) {
                        dbHelper.insertRecentItem(tid, "A");
                    }

                    if(cookie != null) {
                        if (mFavoriteActivityItem.size() > 0) {
                            FavoriteActivityList = new String[mFavoriteActivityItem.size()];
                            for (int i = 0; i < mFavoriteActivityItem.size(); i++) {
                                FavoriteActivityList[i] = mFavoriteActivityItem.get(i).getSel_id();
                            }
                            if (Arrays.asList(FavoriteActivityList).contains(tid)) {
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

                    final JSONObject ticket_data = obj.getJSONObject("deal");
                    final JSONArray photos = obj.getJSONArray("deal_photo");
                    final JSONArray options = obj.getJSONArray("deal_option");
                    final JSONObject review_data = obj.getJSONObject("review_info");
                    final JSONArray deal_option_group = obj.getJSONArray("deal_option_group");
                    final JSONArray instant_coupons = obj.getJSONArray("instant_coupons");

                    tv_special_title = (TextView) findViewById(R.id.tv_special_title);
                    mViewPager = (ToughViewPager) findViewById(R.id.img_pager);
                    m_countView = (TextView) findViewById(R.id.page);
                    m_img_title = (TextView) findViewById(R.id.img_title);
                    room_list = (LinearLayout) findViewById(R.id.room_list);
                    coupon_list = (LinearLayout) findViewById(R.id.coupon_list);
                    img_more = (ImageView) findViewById(R.id.img_more);
                    tv_more = (TextView) findViewById(R.id.tv_more);
                    btn_more_detail = (LinearLayout) findViewById(R.id.btn_more_detail);
                    webview = (WebView) findViewById(R.id.webview);
                    tv_product_info = (TextView) findViewById(R.id.tv_product_info);
                    map_img = (ImageView) findViewById(R.id.map_img);
                    tv_address = (TextView) findViewById(R.id.tv_address);
                    info_list = (LinearLayout) findViewById(R.id.info_list);
                    tv_category = (TextView) findViewById(R.id.tv_category);
                    tv_hotelname = (TextView) findViewById(R.id.tv_hotelname);
                    tv_minprice = (TextView) findViewById(R.id.tv_minprice);
                    tv_maxprice = (TextView) findViewById(R.id.tv_maxprice);
                    tv_per = (TextView) findViewById(R.id.tv_per);
                    tv_review_rate = (TextView) findViewById(R.id.tv_review_rate);
                    tv_review_count = (TextView) findViewById(R.id.tv_review_count);
                    btn_reservation = (Button) findViewById(R.id.btn_reservation);
                    btn_more_review = (LinearLayout) findViewById(R.id.btn_more_review);

                    if(ticket_data.getString("is_hot_deal").equals("Y")){
                        findViewById(R.id.ico_hotdeal).setVisibility(View.VISIBLE);
                    }
                    else{
                        findViewById(R.id.ico_hotdeal).setVisibility(View.GONE);
                    }
                    if(ticket_data.getString("is_add_reserve").equals("Y")) {
                        findViewById(R.id.ico_addpoint).setVisibility(View.VISIBLE);
                    }
                    else{
                        findViewById(R.id.ico_addpoint).setVisibility(View.GONE);
                    }

//                    if(instant_coupons.length() >0){
//                        findViewById(R.id.soon_discount).setVisibility(View.VISIBLE);
//                    }else {
//                        findViewById(R.id.soon_discount).setVisibility(View.GONE);
//                    }

                    //티켓 메인 명
                    //title 명
                    tname = ticket_data.getString("name");
                    maplat = ticket_data.getString("latitude");
                    maplon = ticket_data.getString("longitude");
                    mCity = ticket_data.getString("city");
                    tv_hotelname.setText(tname);
                    tv_category.setText(ticket_data.getString("categor_name"));
                    tv_minprice.setText(Util.numberFormat(ticket_data.getInt("sale_price")));
                    tv_maxprice.setText(Util.numberFormat(ticket_data.getInt("normal_price"))+"원");
                    tv_maxprice.setPaintFlags(tv_maxprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tv_per.setText(ticket_data.getString("sale_rate"));
                    mAvg = review_data.getDouble("avg");
                    tv_review_rate.setText(mAvg+"");
                    setReviewRate(review_data.getDouble("avg"));
                    tv_review_count.setText(review_data.getInt("cnt")+"");
                    if(review_data.getInt("cnt") == 0){
                        findViewById(R.id.review0).setVisibility(View.GONE);
                    }
                    else{
                        findViewById(R.id.review0).setVisibility(View.VISIBLE);
                    }
                    final Double r1, r2, r3, r4, avg;
                    r1 = review_data.getDouble("r1");
                    r2 = review_data.getDouble("r2");
                    r3 = review_data.getDouble("r3");
                    r4 = review_data.getDouble("r4");
                    avg = review_data.getDouble("avg");

                    //viewpager
                    PagerImgs = new String[photos.length()];
                    InfoImgs = new String[photos.length()];

                    for (int i = 0; i < photos.length(); i++) {
                        if(photos.getJSONObject(i).getString("view_yn").equals("Y") && photos.getJSONObject(i).getString("img_type").equals("A")) {
                            PagerImgs[pager_cnt] = photos.getJSONObject(i).getString("url");
                            pager_cnt++;
                        }
                        else if(photos.getJSONObject(i).getString("view_yn").equals("Y") && photos.getJSONObject(i).getString("img_type").equals("B")){
                            InfoImgs[info_cnt] = photos.getJSONObject(i).getString("url");
                            info_cnt++;
                        }
                    }

                    showPager(pager_cnt);

                    //event massage
                    if(ticket_data.has("benefit_text")){
                        if(!TextUtils.isEmpty(ticket_data.getString("benefit_text")) && !ticket_data.getString("benefit_text").equals("null")) {
                            findViewById(R.id.view_detail_special).setVisibility(View.VISIBLE);
                            tv_special_title.setText(ticket_data.getString("benefit_text"));
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
                        setCoupon(instant_coupons);
                    }

                    //가격정보
                    //그룹일경우
                    if(deal_option_group.length() > 0){
                        View view_product = null;
                        room_list.removeAllViews();
                        for(int j =0; j<deal_option_group.length(); j++) {
                            if (options.length() > 0) {
                                ticket_count = options.length();
                                for (int i = 0; i < options.length(); i++) {
                                    view_product = LayoutInflater.from(DetailActivityActivity.this).inflate(R.layout.layout_ticket_item, null);
                                    TextView item_name = (TextView) view_product.findViewById(R.id.item_name);
                                    TextView item_price = (TextView) view_product.findViewById(R.id.item_price);
                                    final TextView item_cnt = (TextView) view_product.findViewById(R.id.item_cnt);
                                    TextView item_description = (TextView) view_product.findViewById(R.id.item_description);
                                    final RelativeLayout layout_background = (RelativeLayout) view_product.findViewById(R.id.layout_background);
                                    final TextView group_title = (TextView) view_product.findViewById(R.id.group_title);
                                    final TextView item_id = (TextView) view_product.findViewById(R.id.item_id);
                                    final TextView group_id = (TextView) view_product.findViewById(R.id.group_id);
                                    final ImageView minus = (ImageView) view_product.findViewById(R.id.minus);
                                    final ImageView plus = (ImageView) view_product.findViewById(R.id.plus);
                                    LinearLayout group_view = (LinearLayout) view_product.findViewById(R.id.group_view);
                                    TextView group_info = (TextView) view_product.findViewById(R.id.group_info);
                                    if (!options.getJSONObject(i).getString("group_id").equals("null")) {
                                        if (deal_option_group.getJSONObject(j).getInt("id") == options.getJSONObject(i).getInt("group_id")) {
                                            minus.setTag(i);
                                            minus.setOnClickListener(new OnSingleClickListener() {
                                                @Override
                                                public void onSingleClick(View v) {
                                                    if (sel_list.get((int)v.getTag()).getmCnt() < 11) {
                                                        int cnt = sel_list.get((int) v.getTag()).getmCnt() - 1;
                                                        if(cnt == 0){
                                                            item_cnt.setText(0 + "장");
                                                            sel_list.get((int)v.getTag()).setmCnt(0);
                                                            v.setBackgroundResource(R.drawable.numeric_m_disabled);
                                                        }else if(cnt > 0) {
                                                            item_cnt.setText(cnt + "장");
                                                            sel_list.get((int) v.getTag()).setmCnt(cnt);
                                                            plus.setBackgroundResource(R.drawable.numeric_p_enabled);
                                                        }
                                                    }
                                                }
                                            });
                                            plus.setTag(i);
                                            plus.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (sel_list.get((int)v.getTag()).getmCnt() >= 0 && sel_list.get((int)v.getTag()).getmCnt() < 11) {
                                                        int cnt = sel_list.get((int) v.getTag()).getmCnt() + 1;
                                                        if(cnt == 10){
                                                            item_cnt.setText(10 + "장");
                                                            sel_list.get((int)v.getTag()).setmCnt(10);
                                                            v.setBackgroundResource(R.drawable.numeric_p_disabled);
                                                        }
                                                        else if(cnt < 11){
                                                            item_cnt.setText(cnt + "장");
                                                            sel_list.get((int) v.getTag()).setmCnt(cnt);
                                                            minus.setBackgroundResource(R.drawable.numeric_m_enabled);
                                                        }
                                                    }
                                                }
                                            });

                                            NumberFormat nf = NumberFormat.getNumberInstance();
                                            item_name.setText(options.getJSONObject(i).getString("name"));
                                            item_price.setText(nf.format(Integer.valueOf(options.getJSONObject(i).getString("sale_price"))) + "원 ");
                                            item_id.setText(options.getJSONObject(i).getString("id"));
                                            group_id.setText(options.getJSONObject(i).getString("group_id"));
                                            if (options.getJSONObject(i).getString("description") == null || options.getJSONObject(i).getString("description").equals("null") || options.getJSONObject(i).getString("description").equals(null) || TextUtils.isEmpty(options.getJSONObject(i).getString("description"))) {
                                                item_description.setVisibility(View.GONE);
                                            } else {
                                                item_description.setVisibility(View.VISIBLE);
                                                item_description.setText(options.getJSONObject(i).getString("description"));
                                            }

                                            TicketSelEntry item = new TicketSelEntry();
                                            item.setmId(item_id.getText().toString());
                                            item.setmCnt(0);

                                            group_view.setVisibility(View.VISIBLE);
                                            if(i != 0){
                                                view_product.findViewById(R.id.end_line).setVisibility(View.VISIBLE);
                                            }

                                            group_title.setText(deal_option_group.getJSONObject(j).getString("name"));
                                            if (!deal_option_group.getJSONObject(j).getString("description").equals("null")) {
                                                group_info.setText("" + deal_option_group.getJSONObject(j).getString("description").replace("• ","ㆍ").replace("<br> ", "\n").replace("<br />", "\n").replace("<br>", "\n").replace(" ", "\u00A0"));
                                            } else {
                                                group_info.setText("" + deal_option_group.getJSONObject(j).getString("name"));
                                            }

                                            if (sel_list.size() > 0) {
                                                TextView prev_gid = room_list.getChildAt(sel_list.size() - 1).findViewById(R.id.group_id);
                                                if (prev_gid.getText().toString().equals(deal_option_group.getJSONObject(j).getInt("id") + "")) {
                                                    group_view.setVisibility(View.GONE);
                                                }
                                            }

                                            view_product.findViewById(R.id.group_line).setVisibility(View.VISIBLE);

                                            sel_list.add(item);
                                            room_list.addView(view_product);
                                        }
                                    }
                                }
                            }
                            else{
                                room_list.setVisibility(View.GONE);
                            }
                        }
                    }
                    else { // 그룹이 아닐경우
                        if (options.length() > 0) {
                            ticket_count = options.length();
                            View view_product = null;
                            room_list.removeAllViews();

                            for (int i = 0; i < options.length(); i++) {
                                view_product = LayoutInflater.from(DetailActivityActivity.this).inflate(R.layout.layout_ticket_item, null);
                                TextView item_name = (TextView) view_product.findViewById(R.id.item_name);
                                TextView item_price = (TextView) view_product.findViewById(R.id.item_price);
                                final TextView item_cnt = (TextView) view_product.findViewById(R.id.item_cnt);
                                final RelativeLayout layout_background = (RelativeLayout) view_product.findViewById(R.id.layout_background);
                                final TextView group_title = (TextView) view_product.findViewById(R.id.group_title);
                                final TextView item_id = (TextView) view_product.findViewById(R.id.item_id);
                                TextView item_description = (TextView) view_product.findViewById(R.id.item_description);
                                ImageView minus = (ImageView) view_product.findViewById(R.id.minus);
                                ImageView plus = (ImageView) view_product.findViewById(R.id.plus);

                                minus.setTag(i);
                                minus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (sel_list.get((int)v.getTag()).getmCnt() < 11) {
                                            if(sel_list.get((int)v.getTag()).getmCnt() == 0){
                                                item_cnt.setText(0 + "장");
                                                sel_list.get((int)v.getTag()).setmCnt(0);
                                            }else {
                                                int cnt = sel_list.get((int) v.getTag()).getmCnt() - 1;
                                                item_cnt.setText(cnt + "장");
                                                sel_list.get((int) v.getTag()).setmCnt(cnt);
                                            }
                                        }
                                    }
                                });
                                plus.setTag(i);
                                plus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (sel_list.get((int)v.getTag()).getmCnt() >= 0 && sel_list.get((int)v.getTag()).getmCnt() < 11) {
                                            if(sel_list.get((int)v.getTag()).getmCnt() == 10){
                                                item_cnt.setText(10 + "장");
                                                sel_list.get((int)v.getTag()).setmCnt(10);
                                            }
                                            else {
                                                int cnt = sel_list.get((int) v.getTag()).getmCnt() + 1;
                                                item_cnt.setText(cnt + "장");
                                                sel_list.get((int) v.getTag()).setmCnt(cnt);
                                            }
                                        }
                                    }
                                });

                                NumberFormat nf = NumberFormat.getNumberInstance();
                                item_name.setText(options.getJSONObject(i).getString("name"));
                                item_price.setText(nf.format(Integer.valueOf(options.getJSONObject(i).getString("sale_price"))) + "원 ");
                                item_id.setText(options.getJSONObject(i).getString("id"));
                                if(options.getJSONObject(i).getString("description") == null || options.getJSONObject(i).getString("description").equals(null) || options.getJSONObject(i).getString("description").equals(null)) {
                                    item_description.setVisibility(View.GONE);
                                }else{
                                    item_description.setVisibility(View.VISIBLE);
                                    item_description.setText(options.getJSONObject(i).getString("description"));
                                }

                                if(i<options.length()-1){
                                    view_product.findViewById(R.id.group_line).setVisibility(View.VISIBLE);
                                }
                                else{
                                    view_product.findViewById(R.id.end_line).setVisibility(View.VISIBLE);
                                }

                                TicketSelEntry item = new TicketSelEntry();
                                item.setmId(item_id.getText().toString());
                                item.setmCnt(0);
                                sel_list.add(item);
                                room_list.addView(view_product);
                            }
                        }
                        else{
                            ticket_count = 0;
                            room_list.setVisibility(View.GONE);
                        }
                    }
                    //가격정보

                    //이미지 정보
                    if(info_cnt >0){
                        findViewById(R.id.img_detail).setVisibility(View.VISIBLE);
                        String img_tag = "";
                        img_tag = "<div style='font-size:30px;text-align:center'><strong>"+"하기 이미지는 상품의 이해를 돕기 위한 내용으로,<br> 시설사의 상황에 따라 상품 상세 정보가 다를 수 있습니다."+"</strong></div><br>";
                        for(int j=0; j<InfoImgs.length; j++){
                            img_tag +=  "<img width='100%' src ="+InfoImgs[j]+"><br>";
                        }
                        if(android.os.Build.VERSION.SDK_INT < 16) {
                            webview.loadData(img_tag, "text/html", "UTF-8"); // Android 4.0 이하 버전
                        }else {
                            webview.loadData(img_tag, "text/html; charset=UTF-8", null); // Android 4.1 이상 버전
                        }
                        webview.getSettings().setLoadWithOverviewMode(true);
                        webview.getSettings().setUseWideViewPort(true);
                        btn_more_detail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LinearLayout.LayoutParams lp = null;
                                if(tv_more.getText().equals("더보기")){
                                    tv_more.setText(getResources().getString(R.string.more_close_title));
                                    img_more.setBackgroundResource(R.drawable.btn_detail_close);
                                    lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                                else{
                                    tv_more.setText(getResources().getString(R.string.more_title));
                                    img_more.setBackgroundResource(R.drawable.btn_detail_open);
                                    lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dptopixel(DetailActivityActivity.this,180));
                                }
                                webview.setLayoutParams(lp);
                                webview.reload();
                            }
                        });
                    }
                    else {
                        findViewById(R.id.img_detail).setVisibility(View.GONE);
                    }
                    //이미지 정보

                    //상품소개
                    if(ticket_data.has("deal_introduce")&&!ticket_data.getString("deal_introduce").equals("null")) {
                        findViewById(R.id.product_info).setVisibility(View.VISIBLE);
                        tv_product_info.setText(ticket_data.getString("deal_introduce"));
                    }
                    else{
                        findViewById(R.id.product_info).setVisibility(View.GONE);
                    }

                    // 지도
                    // 주소
                    String mapStr = "https://maps.googleapis.com/maps/api/staticmap?center="+ticket_data.getString("latitude")+"%2C"+ticket_data.getString("longitude")+
                            "&markers=icon:http://hotelnow.s3.amazonaws.com/etc/20181012_180827_hozDzSdI4I.png%7C"+ticket_data.getString("latitude")+"%2C"+ticket_data.getString("longitude")+
                            "&scale=1&sensor=false&language=ko&size="+700+"x"+700+"&zoom=15"+"&key="+ BuildConfig.google_map_key2;
                    Ion.with(map_img).load(mapStr);
                    mAddress = ticket_data.getString("address");
                    tv_address.setText(mAddress);

                    findViewById(R.id.btn_address_copy).setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) HotelnowApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", mAddress);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(DetailActivityActivity.this, "주소가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    findViewById(R.id.btn_address_near).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 주변보기
                            Intent intent = new Intent(DetailActivityActivity.this, MapActivity.class);
                            intent.putExtra("from", "pdetail");
                            intent.putExtra("isTicket", true);
                            intent.putExtra("tid", tid);
                            intent.putExtra("lat", maplat);
                            intent.putExtra("lng", maplon);
                            intent.putExtra("deal_name", tname);
                            startActivityForResult(intent, 81); // 81 지도보기
                        }
                    });

                    map_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailActivityActivity.this, MapActivity.class);
                            intent.putExtra("from", "pdetail");
                            intent.putExtra("isTicket", true);
                            intent.putExtra("tid", tid);
                            intent.putExtra("lat", maplat);
                            intent.putExtra("lng", maplon);
                            intent.putExtra("deal_name", tname);
                            startActivityForResult(intent, 81); // 81 지도보기
                        }
                    });

                    //이용 정보
                    ArrayList<TicketInfoEntry> infolist = new ArrayList<>();

                    if(ticket_data.has("deal_info") && !ticket_data.getString("deal_info").equals("null")) {
                        infolist.add(new TicketInfoEntry("상품 정보", ticket_data.getString("deal_info")));
                    }
                    if(ticket_data.has("refund_info") &&!ticket_data.getString("refund_info").equals("null")){
                        infolist.add(new TicketInfoEntry("환불 정보", ticket_data.getString("refund_info")));
                    }

                    if(ticket_data.has("usage_info")&&!ticket_data.getString("usage_info").equals("null")){
                        infolist.add(new TicketInfoEntry("사용 정보", ticket_data.getString("usage_info")));
                    }

                    if(ticket_data.has("store_info")&&!ticket_data.getString("store_info").equals("null")){
                        infolist.add(new TicketInfoEntry("시설사 정보", ticket_data.getString("store_info")));
                    }

                    if(ticket_data.has("notice_info")&&!ticket_data.getString("notice_info").equals("null")){
                        infolist.add(new TicketInfoEntry("공지 정보", ticket_data.getString("notice_info")));
                    }

                    if(ticket_data.has("cs_info")&&!ticket_data.getString("cs_info").equals("null")){
                        infolist.add(new TicketInfoEntry("제공 정보", ticket_data.getString("cs_info")));
                    }
                    if(infolist.size()>0) {
                        info_list.removeAllViews();
                        for (int i = 0; i < infolist.size(); i++) {
                            View info_view = LayoutInflater.from(DetailActivityActivity.this).inflate(R.layout.layout_ticket_info, null);
                            AutoLinkTextView title_sub = (AutoLinkTextView) info_view.findViewById(R.id.title_sub);
                            TextView title = (TextView) info_view.findViewById(R.id.title);
                            title_sub.addAutoLinkMode(
                                    AutoLinkMode.MODE_PHONE,
                                    AutoLinkMode.MODE_URL);
                            title_sub.setPhoneModeColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.purple));
                            title_sub.setUrlModeColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.private_discount));
                            Spannable sp = new SpannableString(infolist.get(i).getmMessage().replace("• ", "ㆍ"));
                            Linkify.addLinks(sp, Patterns.PHONE, "tel:", Util.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);
                            title_sub.setMovementMethod(CustomLinkMovementMethod.getInstance());
                            title_sub.setText(sp);
                            title.setText(infolist.get(i).getmTitle());

                            info_list.addView(info_view);
                        }
                    }
                    //이용 정보

                    btn_reservation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean is_sel = false;
                            for(int i=0; i<sel_list.size(); i++){
                                if(sel_list.get(i).getmCnt() != 0){
                                    is_sel = true;
                                    break;
                                }
                            }
                            if(is_sel) {
                                cookie = _preferences.getString("userid", null);
                                if(cookie != null) {
                                    Intent intent = new Intent(DetailActivityActivity.this, ReservationActivityActivity.class);
                                    intent.putExtra("sel_list", (Serializable) sel_list);
                                    intent.putExtra("tid", tid);
                                    intent.putExtra("tname", tname);
                                    startActivityForResult(intent,80);
                                }
                                else{
                                    Intent intent = new Intent(DetailActivityActivity.this, LoginActivity.class);
                                    intent.putExtra("from", "ticket_reservation");
                                    intent.putExtra("sel_list", (Serializable) sel_list);
                                    intent.putExtra("pid", tid);
                                    intent.putExtra("tname", tname);
                                    intent.putExtra("page", "detailA");
                                    startActivityForResult(intent,90);
                                }
                            }
                            else{
                                dialogAlert = new DialogAlert(
                                        getString(R.string.alert_notice),
                                        "옵션 수량을 1개 이상 선택해주세요.",
                                        DetailActivityActivity.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialogAlert.dismiss();
                                            }
                                        });
                                dialogAlert.setCancelable(false);
                                dialogAlert.show();
                            }
                        }
                    });

                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
                catch (Exception e) {
                    e.getStackTrace();
                    Toast.makeText(DetailActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

    private void setCoupon(final JSONArray cdata) {
        try{
            coupon_list.removeAllViews();
            isAcoupon = new int[cdata.length()];
            mCouponId = new String[cdata.length()];

            for (int i = 0; i < cdata.length(); i++) {
                View view_coupon = LayoutInflater.from(DetailActivityActivity.this).inflate(R.layout.layout_detail_coupon_item, null);
                TextView tv_coupon_price = (TextView) view_coupon.findViewById(R.id.tv_coupon_price);
                TextView tv_coupon_title = (TextView) view_coupon.findViewById(R.id.tv_coupon_title);
                ImageView icon_coupon = (ImageView) view_coupon.findViewById(R.id.icon_coupon);
                ImageView icon_download = (ImageView) view_coupon.findViewById(R.id.icon_download);
                tv_coupon_title.setText(cdata.getJSONObject(i).getString("name"));
                tv_coupon_price.setText(cdata.getJSONObject(i).getString("coupon_price"));
                if(cdata.getJSONObject(i).getInt("mycoupon_cnt") == 0){
                    //사용가능
                    tv_coupon_price.setTextColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.redtext));
                    tv_coupon_title.setTextColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.blacktxt));
                    icon_coupon.setBackgroundResource(R.drawable.ico_coupon);
                    icon_download.setBackgroundResource(R.drawable.ico_download);
                }
                else {
                    //불가능
                    tv_coupon_price.setTextColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.coupon_dis));
                    tv_coupon_title.setTextColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.coupon_dis));
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
                view_coupon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dptopixel(DetailActivityActivity.this,54)));
                coupon_list.addView(view_coupon);
            }
        }
        catch (Exception e) {
            e.getStackTrace();
            Toast.makeText(DetailActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            findViewById(R.id.wrapper).setVisibility(View.GONE);
        }
    }

    private void setCouponDown(final int position, final JSONArray cdata){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        Api.get(CONFIG.ticketcouponUrl2+"/"+mCouponId[position], new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(DetailActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(DetailActivityActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    isAcoupon[position] = 1;
                    TextView tv_coupon_price = (TextView) coupon_list.getChildAt(position).findViewById(R.id.tv_coupon_price);
                    TextView tv_coupon_title = (TextView) coupon_list.getChildAt(position).findViewById(R.id.tv_coupon_title);
                    ImageView icon_coupon = (ImageView) coupon_list.getChildAt(position).findViewById(R.id.icon_coupon);
                    ImageView icon_download = (ImageView) coupon_list.getChildAt(position).findViewById(R.id.icon_download);

                    tv_coupon_price.setTextColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.coupon_dis));
                    tv_coupon_title.setTextColor(ContextCompat.getColor(DetailActivityActivity.this, R.color.coupon_dis));
                    icon_coupon.setBackgroundResource(R.drawable.ico_coupon_dis);
                    icon_download.setBackgroundResource(R.drawable.ico_download_dis);

                    showCouponDialog(obj.getString("msg"));
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    Toast.makeText(DetailActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

    //쿠폰 다이얼로그
    private void showCouponDialog(String message){
        dialogCoupon = new DialogCoupon(
                DetailActivityActivity.this,
                getString(R.string.coupon_title2),
                tname+"\n"+message +"\n\n지금 바로예약하세요",
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

    private void showPager(final int pager_cnt) {

        PAGES = pager_cnt;
        FIRST_PAGE = PAGES * LOOPS / 2;

        m_countView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivityActivity.this, FullImageViewActivityActivity.class);
                intent.putExtra("tid", tid);
                intent.putExtra("idx", markNowPosition);
                intent.putExtra("imgs", PagerImgs);
                intent.putExtra("name", tname);
                intent.putExtra("total", pager_cnt);
                startActivity(intent);
            }
        });

        try {
            m_img_title.setVisibility(View.GONE);
            mPagerAdapter = new TicketPagerAdapter(getSupportFragmentManager(), PagerImgs);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.addOnPageChangeListener(mPagerAdapter);
            mViewPager.setCurrentItem(FIRST_PAGE, true);
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    try {
                        nowPosition = position;
                        markNowPosition = position % PAGES;

                        m_countView.setText(markNowPosition+1+"/"+PAGES+" + ");
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

        m_countView.setText(1+"/"+PAGES+" + ");

        markPrevPosition = markNowPosition;
    }

    private class TicketPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private String landscapeImgs[];

        public TicketPagerAdapter(FragmentManager frgm, String[] imgs) {
            super(frgm);
            landscapeImgs = imgs;
        }

        @Override
        public Fragment getItem(int position) {
            if (PAGES != 0)
                position = position % PAGES;

            return ActivityImageFragment.newInstance(DetailActivityActivity.this, position, landscapeImgs[position], tid, PagerImgs, pager_cnt);
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

    public void showToast(String msg){
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);
        ico_favorite.setVisibility(View.GONE);
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
    protected void onDestroy() {
        super.onDestroy();
        if(webview != null){
            webview.destroy();
        }
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

        if(resultCode == 90 && requestCode == 90) {
            isLogin = true;
            cookie = _preferences.getString("userid", null);
        }
        else if(resultCode == 100 && requestCode == 80) {
            setResult(100);
            finish();
        }
        else if(resultCode == 81 && requestCode == 81){
            Intent intent = new Intent(this, DetailHotelActivity.class);
            intent.putExtra("hid", data.getStringExtra("hid"));
            intent.putExtra("save", true);
            startActivityForResult(intent, 100);
            finish();
        }
    }
}
