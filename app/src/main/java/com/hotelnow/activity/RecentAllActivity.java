package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.RecentAllAdapter;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.fragment.model.RecentItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
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
import java.util.List;
import java.util.Map;

/**
 * Created by susia on 16. 5. 8..
 */
public class RecentAllActivity extends Activity {
    RelativeLayout wrapper;

    ListView Listview;
    public ArrayList<SearchResultItem> mItems = new ArrayList<>();
    public List<RecentItem> mRecentItem = new ArrayList<>();
    RecentAllAdapter mAdapter;

    SharedPreferences _preferences;
    String tid;
    String from = "";

    DialogAlert dialogAlert;
//    Tracker t;
    DbOpenHelper dbHelper;
    RelativeLayout toast_layout;
    TextView tv_toast;
    boolean is_fav_sel = false;
    View header;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recent);

        Util.setStatusColor(this);

        dbHelper = new DbOpenHelper(this);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRecentItem = dbHelper.selectAllRecentItem("30");

        header = getLayoutInflater().inflate(R.layout.layout_recent_header, null, false);

        Listview = (ListView)findViewById(R.id.listview);
        mAdapter = new RecentAllAdapter(RecentAllActivity.this, 0, mItems, dbHelper);
        Listview.addHeaderView(header);
        Listview.setAdapter(mAdapter);

        tv_toast = (TextView) findViewById(R.id.tv_toast);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);

        Listview.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View v, int position, long id) {

                TextView hid = (TextView) v.findViewById(R.id.hid);
                TextView pid = (TextView) v.findViewById(R.id.pid);
                TextView sdate = (TextView) v.findViewById(R.id.sdate);
                TextView edate = (TextView) v.findViewById(R.id.edate);
                TextView hname = (TextView) v.findViewById(R.id.hotel_name);

                if(mItems.get(position-1).getHotel_id().equals("stay")) {
                    TuneWrap.Event("RecentlySee", "stay", hid.getText().toString());
                    Intent intent = new Intent(RecentAllActivity.this, DetailHotelActivity.class);
                    intent.putExtra("hid", hid.getText().toString());
                    intent.putExtra("evt", "N");
                    intent.putExtra("save", true);
                    intent.putExtra("sdate", sdate.getText().toString());
                    intent.putExtra("edate", edate.getText().toString());
                    startActivityForResult(intent,80);
                }
                else{
                    TuneWrap.Event("RecentlySee", "stay", hid.getText().toString());
                    Intent intent = new Intent(RecentAllActivity.this, DetailActivityActivity.class);
                    intent.putExtra("tid", hid.getText().toString());
                    intent.putExtra("evt", "N");
                    intent.putExtra("save", true);
                    startActivityForResult(intent,80);
                }

            }
        });

        findViewById(R.id.bt_scroll).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Listview.smoothScrollToPosition(0);
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_fav_sel){
                    setResult(80);
                }
                finish();
            }
        });

        getRecentData();
    }

    public void getRecentData(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.mainRecent;
        if (mRecentItem.size() > 0) {
            try {
                JSONArray jArray = new JSONArray();//배열
                for (int i = 0; i < mRecentItem.size(); i++) {
                    String option = "1";
                    if (mRecentItem.get(i).getSel_option().equals("H")) {
                        option = "1";
                    } else {
                        option = "2";
                    }
                    JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                    sObject.put("flag", option);
                    sObject.put("id", mRecentItem.get(i).getSel_id());
                    jArray.put(sObject);
                }

                LogUtil.e("JSON Test", jArray.toString());
                JSONObject params = new JSONObject();
                params.put("recent_items", jArray.toString());

                Api.post(url, params.toString(), new Api.HttpCallback() {

                    @Override
                    public void onFailure(Response response, Exception throwable) {
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, String body) {
                        try {
                            JSONObject obj = new JSONObject(body);
                            if (!obj.has("result") || !obj.getString("result").equals("success")) {

                            }
                            else if (obj.has("lists")) {
                                if(obj.getJSONArray("lists").length()>0) {
                                    JSONArray p_recent = new JSONArray(obj.getJSONArray("lists").toString());
                                    for (int i = 0; i < p_recent.length(); i++) {
                                        JSONObject entry = p_recent.getJSONObject(i);
                                        if (entry.getString("type").equals("stay")) {
                                            mItems.add(new SearchResultItem(
                                                    entry.getString("id"),
                                                    entry.getString("type"),
                                                    entry.getString("name"),
                                                    "",
                                                    entry.getString("category"),
                                                    entry.getString("street1"),
                                                    entry.getString("street2"),
                                                    0,
                                                    0,
                                                    "",
                                                    entry.getString("landscape"),
                                                    entry.getString("sale_price"),
                                                    entry.getString("normal_price"),
                                                    entry.getString("sale_rate"),
                                                    entry.getInt("items_quantity"),
                                                    entry.getString("special_msg"),
                                                    "",
                                                    entry.getString("grade_score"),
                                                    entry.getString("real_grade_score"),
                                                    entry.getString("start_date"),
                                                    entry.getString("end_date"),
                                                    "",
                                                    "",
                                                    entry.getString("is_private_deal"),
                                                    entry.getString("is_hot_deal"),
                                                    entry.getString("is_add_reserve"),
                                                    entry.getInt("coupon_count"),
                                                    i == 0 ? true : false,
                                                    0
                                            ));
                                        } else {
                                            mItems.add(new SearchResultItem(
                                                    entry.getString("id"),
                                                    entry.getString("type"),
                                                    entry.getString("name"),
                                                    "",
                                                    entry.getString("category"),
                                                    entry.getString("location"),
                                                    "",
                                                    entry.getDouble("latitude"),
                                                    entry.getDouble("longitude"),
                                                    "",
                                                    entry.getString("landscape"),
                                                    entry.getString("sale_price"),
                                                    entry.getString("normal_price"),
                                                    entry.getString("sale_rate"),
                                                    0,
                                                    entry.getString("benefit_text"),
                                                    "",
                                                    entry.getString("grade_score"),
                                                    entry.getString("real_grade_score"),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "N",
                                                    entry.getString("is_hot_deal"),
                                                    entry.getString("is_add_reserve"),
                                                    entry.getInt("coupon_count"),
                                                    i == 0 ? true : false,
                                                    0
                                            ));
                                        }
                                    }
                                    findViewById(R.id.bt_scroll).setVisibility(View.VISIBLE);
                                }
                                else{
                                    findViewById(R.id.bt_scroll).setVisibility(View.GONE);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                        }
                    }
                });
            } catch(Exception e){
                e.printStackTrace();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }
        }
    }

    public void setLike(final int position, final boolean islike){
        final String sel_id = mItems.get(position).getId();
        final String sel_type = mItems.get(position).getHotel_id();
        JSONObject paramObj = new JSONObject();
        try {
            if(sel_type.equals("stay"))
                paramObj.put("type", "stay");
            else
                paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch(Exception e){
            Log.e(CONFIG.TAG, e.toString());
        }
        if(islike){// 취소
            Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(RecentAllActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            return;
                        }
                        if (sel_type.equals("stay")) {
                            TuneWrap.Event("favorite_stay_del", sel_id);
                            dbHelper.deleteFavoriteItem(false, sel_id, "H");
                        } else {
                            TuneWrap.Event("favorite_activity_del", sel_id);
                            dbHelper.deleteFavoriteItem(false, sel_id, "A");
                        }
                        LogUtil.e("xxxx", "찜하기 취소");
                        showIconToast("관심 상품 담기 취소", false);
                        mAdapter.notifyDataSetChanged();
                        is_fav_sel = true;
                    }catch (JSONException e){

                    }
                }
            });
        }
        else{// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(RecentAllActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                           showToast("로그인 후 이용해주세요");
                            return;
                        }

                        if (sel_type.equals("stay")) {
                            TuneWrap.Event("favorite_stay", sel_id);
                            dbHelper.insertFavoriteItem(sel_id, "H");
                        } else {
                            TuneWrap.Event("favorite_activity", sel_id);
                            dbHelper.insertFavoriteItem(sel_id, "A");
                        }
                        LogUtil.e("xxxx", "찜하기 성공");
                        showIconToast("관심 상품 담기 성공", true);
                        mAdapter.notifyDataSetChanged();
                        is_fav_sel = true;
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
        if(is_fav_sel){
            setResult(80);
        }
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
