package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_themehotel);

        Util.setStatusColor(this);

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
        mAdapter = new ThemeSpecialHotelAdapter(ThemeSpecialHotelActivity.this, 0, mThemeItem);
        hotelListview.setAdapter(mAdapter);

        hotelListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                TextView hid = (TextView) v.findViewById(R.id.hid);
                TextView pid = (TextView) v.findViewById(R.id.pid);
                // 내일 확인
//                TextView sdate = (TextView) v.findViewById(R.id.sdate);
//                TextView edate = (TextView) v.findViewById(R.id.edate);
                TextView hname = (TextView) v.findViewById(R.id.hotel_name);

                if(!pid.getText().toString().equals("-1")) {
//                    t.send(new HitBuilders.EventBuilder().setCategory("PRODUCT_THEME").setAction(tid).setLabel(hname.getText().toString()).build());
//                    TuneWrap.Event("PRODUCT_THEME", tid, hname.getText().toString());

                    Intent intent = new Intent(ThemeSpecialHotelActivity.this, DetailHotelActivity.class);
                    intent.putExtra("hid", hid.getText().toString());
                    intent.putExtra("evt", "N");
//                    intent.putExtra("sdate", sdate.getText().toString());
//                    intent.putExtra("edate", edate.getText().toString());
                    startActivity(intent);
                }
            }
        });

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipeView.setColorSchemeColors(R.color.purple, R.color.purple_cc, R.color.green);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRefresh = true;
                        swipeView.setRefreshing(false);
                        getHotelList();
                    }
                }, 200);
            }
        });

        getHotelList();
    }

    public void getHotelList() {
        String url = CONFIG.special_theme_list+"/"+tid+"/H";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ThemeSpecialHotelActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("theme")) {
                        Toast.makeText(ThemeSpecialHotelActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject entry = null;
                    JSONArray rows = obj.getJSONArray("lists");

//                    getActionBar().setTitle(head.getString("title"));

                    if(isRefresh){
                        isRefresh = false;
                        mThemeItem.clear();
                    }
                    //0번째에 head 이미지가 있으면 array에 넣고 없으면 리스트만 담는다.
                    //array name 무시 필요한것만 담음.
                    if(obj.has("theme")) {
                        JSONObject head = obj.getJSONObject("theme");
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
                                head.getString("img_background"))
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
                                entry.getString("comment")
                        ));
                    }

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 200);

                } catch (Exception e) {
                    Toast.makeText(ThemeSpecialHotelActivity.this, getString(R.string.error_theme_info), Toast.LENGTH_SHORT).show();
                }

            }

        });
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

}
