package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.ThemeSpecialActivityAdapter;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by susia on 16. 5. 8..
 */
public class ThemeSpecialActivityActivity extends Activity {
    RelativeLayout wrapper;

    ListView hotelListview;
    public ArrayList<SearchResultItem> mThemeItem = new ArrayList<>();
    ThemeSpecialActivityAdapter mAdapter;

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

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new DbOpenHelper(this);

        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        from = intent.getStringExtra("from") != null ? intent.getStringExtra("from") : "";

        hotelListview = (ListView) findViewById(R.id.listview);
        mAdapter = new ThemeSpecialActivityAdapter(ThemeSpecialActivityActivity.this, 0, mThemeItem, dbHelper);
        hotelListview.setAdapter(mAdapter);

        tv_toast = (TextView) findViewById(R.id.tv_toast);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);

        hotelListview.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View v, int position, long id) {

                TextView hid = (TextView) v.findViewById(R.id.hid);
                TextView pid = (TextView) v.findViewById(R.id.pid);
                // 내일 확인
                TextView hname = (TextView) v.findViewById(R.id.hotel_name);

                if (!pid.getText().toString().equals("-1")) {
                    Intent intent = new Intent(ThemeSpecialActivityActivity.this, DetailActivityActivity.class);
                    intent.putExtra("tid", hid.getText().toString());
                    intent.putExtra("evt", "N");
                    intent.putExtra("save", true);
                    startActivityForResult(intent, 80);
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
        String url = CONFIG.special_theme_list + "/" + tid + "/A";

        TuneWrap.Event("theme", "activity", tid);

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ThemeSpecialActivityActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("theme")) {
                        Toast.makeText(ThemeSpecialActivityActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    JSONObject entry = null;
                    JSONArray rows = obj.getJSONArray("lists");

//                    getActionBar().setTitle(head.getString("title"));

                    if (isRefresh) {
                        isRefresh = false;
                        mThemeItem.clear();
                    }
                    //0번째에 head 이미지가 있으면 array에 넣고 없으면 리스트만 담는다.
                    //array name 무시 필요한것만 담음.
                    if (obj.has("theme")) {
                        JSONObject head = obj.getJSONObject("theme");
                        ((TextView) findViewById(R.id.title_text)).setText(head.getString("title"));
                        mThemeItem.add(new SearchResultItem(
                                head.getString("id"),
                                "",
                                head.getString("subject"),
                                "",
                                "-1",
                                head.getString("detail"),
                                TextUtils.isEmpty(head.getString("notice")) ? "" : head.getString("notice"),
                                0,
                                0,
                                "N",
                                head.getString("img_main_list"),
                                "",
                                "",
                                "",
                                0,
                                "",
                                "0",
                                "",
                                "",
                                "0",
                                "",
                                "0",
                                "",
                                "N",
                                "",
                                "",
                                0,
                                false,
                                0
                        ));
                    }

                    for (int i = 0; i < rows.length(); i++) {
                        entry = rows.getJSONObject(i);
                        mThemeItem.add(new SearchResultItem(
                                entry.getString("id"),
                                entry.getString("deal_id"),
                                entry.getString("name"),
                                "",
                                entry.getString("category"),
                                "",
                                "",
                                entry.getDouble("latitude"),
                                entry.getDouble("longitude"),
                                "N",
                                entry.getString("landscape"),
                                entry.getString("sale_price"),
                                entry.getString("normal_price"),
                                entry.getString("sale_rate"),
                                0,
                                entry.getString("benefit_text"),
                                "0",
                                entry.getString("grade_score"),
                                entry.getString("real_grade_score"),
                                "0",
                                entry.getString("distance_real"),
                                "0",
                                entry.getString("location"),
                                "N",
                                entry.getString("is_hot_deal"),
                                entry.getString("is_add_reserve"),
                                entry.getInt("coupon_count"),
                                false,
                                0
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
                    Toast.makeText(ThemeSpecialActivityActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

            }

        });
    }

    private void showToast(String msg) {
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);
        findViewById(R.id.ico_favorite).setVisibility(View.GONE);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 1500);
    }

    private void showIconToast(String msg, boolean is_fav) {
        toast_layout.setVisibility(View.VISIBLE);
        tv_toast.setText(msg);

        if (is_fav) { // 성공
            findViewById(R.id.ico_favorite).setBackgroundResource(R.drawable.ico_titbar_favorite_active);
        } else { // 취소
            findViewById(R.id.ico_favorite).setBackgroundResource(R.drawable.ico_titbar_favorite);
        }
        findViewById(R.id.ico_favorite).setVisibility(View.VISIBLE);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        toast_layout.setVisibility(View.GONE);
                    }
                }, 1500);
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

    public void setLike(final int position, final boolean islike) {
        final String sel_id = mThemeItem.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }
        if (islike) {// 취소
            Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(ThemeSpecialActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            return;
                        }
                        dbHelper.deleteFavoriteItem(false, sel_id, "A");
                        LogUtil.e("xxxx", "찜하기 취소");
                        showIconToast("관심 상품 담기 취소", false);
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        } else {// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(ThemeSpecialActivityActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            return;
                        }
                        TuneWrap.Event("favorite_activity", sel_id);

                        dbHelper.insertFavoriteItem(sel_id, "A");
                        LogUtil.e("xxxx", "찜하기 성공");
                        showIconToast("관심 상품 담기 성공", true);
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 80 && resultCode == 80) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
