package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.adapter.ThemeSpecialHotelAdapter;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.fragment.model.ThemeSItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by susia on 16. 5. 8..
 */
public class ThemeSpecialHotelActivity extends Activity {
    RelativeLayout wrapper;

    ListView hotelListview;
    public ArrayList<ThemeSItem> mThemeItem = new ArrayList<>();
    ThemeSpecialHotelAdapter mAdapter;

    SharedPreferences _preferences;
    String tid;
    String from = "";

    DialogAlert dialogAlert;
//    Tracker t;
    boolean isRefresh = false;
    DbOpenHelper dbHelper;
    RelativeLayout toast_layout;
    TextView tv_toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_themehotel);

        Util.setStatusColor(this);

        dbHelper = new DbOpenHelper(this);

//        t = ((HotelnowApplication)getApplication()).getTracker(HotelnowApplication.TrackerName.APP_TRACKER);
//        t.setScreenName("ThemeHotelList");
//        t.send(new HitBuilders.AppViewBuilder().build());

//        TuneWrap.ScreenName("ThemeHotelList");
//        FacebookWrap.logViewedContentEvent(this, "ThemeHotelList");

//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
//        getActionBar().setTitle("테마");

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        wrapper = (RelativeLayout)findViewById(R.id.wrapper);
//        wrapper.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        from = intent.getStringExtra("from") != null? intent.getStringExtra("from"):"";

        hotelListview = (ListView)findViewById(R.id.listview);
        mAdapter = new ThemeSpecialHotelAdapter(ThemeSpecialHotelActivity.this, 0, mThemeItem, dbHelper);
        hotelListview.setAdapter(mAdapter);

        tv_toast = (TextView) findViewById(R.id.tv_toast);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);

        hotelListview.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View v, int position, long id) {

                TextView hid = (TextView) v.findViewById(R.id.hid);
                TextView pid = (TextView) v.findViewById(R.id.pid);
                // 내일 확인
                TextView sdate = (TextView) v.findViewById(R.id.sdate);
                TextView edate = (TextView) v.findViewById(R.id.edate);
                TextView hname = (TextView) v.findViewById(R.id.hotel_name);

                if(!pid.getText().toString().equals("-1")) {
//                    t.send(new HitBuilders.EventBuilder().setCategory("PRODUCT_THEME").setAction(tid).setLabel(hname.getText().toString()).build());
//                    TuneWrap.Event("PRODUCT_THEME", tid, hname.getText().toString());

                    Intent intent = new Intent(ThemeSpecialHotelActivity.this, DetailHotelActivity.class);
                    intent.putExtra("hid", hid.getText().toString());
                    intent.putExtra("evt", "N");
                    intent.putExtra("sdate", sdate.getText().toString());
                    intent.putExtra("edate", edate.getText().toString());
                    intent.putExtra("save", true);
                    startActivityForResult(intent,80);
                }
            }
        });

        findViewById(R.id.bt_scroll).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                hotelListview.smoothScrollToPosition(0);
            }
        });

//        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout)findViewById(R.id.swipe);
//        swipeView.setColorSchemeColors(R.color.purple, R.color.purple_cc, R.color.green);
//        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeView.setRefreshing(true);
//                (new Handler()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        isRefresh = true;
//                        swipeView.setRefreshing(false);
//                        getHotelList();
//                    }
//                }, 200);
//            }
//        });

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getHotelList();
    }

    public void getHotelList() {
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.special_theme_list+"/"+tid+"/H";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ThemeSpecialHotelActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("theme")) {
                        Toast.makeText(ThemeSpecialHotelActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    JSONObject entry = null;
                    JSONArray rows = obj.getJSONArray("lists");

                    if(isRefresh){
                        isRefresh = false;
                        mThemeItem.clear();
                    }
                    //0번째에 head 이미지가 있으면 array에 넣고 없으면 리스트만 담는다.
                    //array name 무시 필요한것만 담음.
                    if(obj.has("theme")) {
                        JSONObject head = obj.getJSONObject("theme");
                        ((TextView)findViewById(R.id.title_text)).setText(head.getString("title"));
                        mThemeItem.add(new ThemeSItem(
                                head.getString("id"),
                                head.getString("title"),
                                "-1",
                                head.getString("sub_title"),
                                "null",
                                "null",
                                "null",
                                "null",
                                "null",
                                0,
                                "null",
                                "null",
                                "null",
                                "",
                                "",
                                "null",
                                "",
                                head.getString("img_main_list"),
                                "",
                                "",
                                0)
                        );
                    }

                    for (int i = 0; i < rows.length(); i++) {
                        entry = rows.getJSONObject(i);
                        mThemeItem.add(new ThemeSItem(
                                entry.getString("id"),
                                entry.getString("name"),
                                entry.getString("category"),
                                entry.getString("street1"),
                                entry.getString("street2"),
                                entry.getString("special_msg"),
                                entry.getString("sale_price"),
                                entry.getString("normal_price"),
                                entry.getString("sale_rate"),
                                entry.getInt("items_quantity"),
                                entry.getString("landscape"),
                                entry.getString("grade_score"),
                                entry.getString("real_grade_score"),
                                entry.getString("is_private_deal"),
                                entry.getString("is_hot_deal"),
                                entry.getString("is_add_reserve"),
                                entry.getString("theme_listing_order"),
                                entry.getString("comment"),
                                entry.getString("start_date"),
                                entry.getString("end_date"),
                                entry.getInt("coupon_count")
                        ));
                    }

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 200);
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    Toast.makeText(ThemeSpecialHotelActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

            }

        });
    }

    public void setLike(final int position, final boolean islike){
        final String sel_id = mThemeItem.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "stay");
            paramObj.put("id", sel_id);
        } catch(Exception e){
            Log.e(CONFIG.TAG, e.toString());
        }
        if(islike){// 취소
            Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(ThemeSpecialHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                           showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false,  sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 취소");
                        showIconToast("관심 상품 담기 취소", false);
                        mAdapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
        else{// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(ThemeSpecialHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                           showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id,"H");
                        LogUtil.e("xxxx", "찜하기 성공");
                        showIconToast("관심 상품 담기 성공", true);
                        mAdapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
    }

    private void showToast(String msg){
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);
        findViewById(R.id.ico_favorite).setVisibility(View.GONE);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 2000);
    }

    private void showIconToast(String msg, boolean is_fav){
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);

        if(is_fav) { // 성공
            findViewById(R.id.ico_favorite).setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        }
        else{ // 취소
            findViewById(R.id.ico_favorite).setBackgroundResource(R.drawable.ico_titbar_favorite);
        }
        findViewById(R.id.ico_favorite).setVisibility(View.VISIBLE);

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
       finish();
       super.onBackPressed();
    }


    @Override
    public void onStart() {
        super.onStart();
//        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
//        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 80 && resultCode == 80){
            mAdapter.notifyDataSetChanged();
        }
    }
}
