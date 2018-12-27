package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.adapter.SearchLeisureAdapter;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ActivitySearchActivity extends Activity {

    private SharedPreferences _preferences;
    private ListView mlist;
    private View EmptyView;
    private View HeaderView;
    private ImageView map_img;
    private TextView tv_review_count, tv_category, tv_location;
    private RelativeLayout btn_location, btn_category;
    private ArrayList<SearchResultItem> mItems = new ArrayList<>();
    private SearchLeisureAdapter adapter;
    private String banner_id, search_txt;
    private int Page = 1;
    private int total_count;
    private String s_position = "", theme_id="", city="";
    private DbOpenHelper dbHelper;
    private Button bt_scroll;
    RelativeLayout toast_layout, title_search;
    ImageView ico_favorite;
    TextView tv_toast, title_text, tv_ecategory,tv_elocation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ha_search);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new DbOpenHelper(this);
        findViewById(R.id.btn_filter).setVisibility(View.GONE);

        Intent intent = getIntent();

        city = intent.getStringExtra("location_id");
        theme_id = intent.getStringExtra("theme_id");

        mlist = (ListView) findViewById(R.id.h_list);
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("액티비티");
        HeaderView = getLayoutInflater().inflate(R.layout.layout_search_map_filter_header2, null, false);
        btn_location = (RelativeLayout) HeaderView.findViewById(R.id.btn_location);
        btn_category = (RelativeLayout)HeaderView.findViewById(R.id.btn_category);
        tv_review_count = (TextView) HeaderView.findViewById(R.id.tv_review_count);
        map_img = (ImageView) HeaderView.findViewById(R.id.map_img);
        tv_category = (TextView) HeaderView.findViewById(R.id.tv_category);
        tv_location = (TextView) HeaderView.findViewById(R.id.tv_location);
        bt_scroll = (Button)findViewById(R.id.bt_scroll);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        title_search = (RelativeLayout) findViewById(R.id.title_search);
        HeaderView.findViewById(R.id.tv_review_count).setVisibility(View.GONE);

        mlist.addHeaderView(HeaderView);
        adapter = new SearchLeisureAdapter(ActivitySearchActivity.this, 0, mItems, dbHelper);
        mlist.setAdapter(adapter);

        View empty = getLayoutInflater().inflate(R.layout.layout_a_search_empty, null, false);
        ((TextView)empty.findViewById(R.id.sub)).setText("다른 지역이나 테마로 검색해보세요");
        tv_ecategory = (TextView) empty.findViewById(R.id.tv_category);
        tv_elocation = (TextView) empty.findViewById(R.id.tv_location);
        ((ViewGroup)mlist.getParent()).addView(empty);
        mlist.setEmptyView(empty);

        tv_location.setText(intent.getStringExtra("location"));
        tv_category.setText(intent.getStringExtra("theme"));
        tv_ecategory.setText(intent.getStringExtra("theme"));
        tv_elocation.setText(intent.getStringExtra("location"));
        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView) view.findViewById(R.id.hid);
                Intent intent = new Intent(ActivitySearchActivity.this, DetailActivityActivity.class);
                intent.putExtra("tid", hid.getText().toString());
                intent.putExtra("save", true);
                startActivityForResult(intent, 50);
            }
        });
        tv_ecategory.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(ActivitySearchActivity.this, ActivityFilterActivity.class);
                startActivityForResult(intent, 70);
            }
        });

        tv_elocation.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(ActivitySearchActivity.this, AreaActivityActivity.class);
                startActivityForResult(intent, 80);
            }
        });

        btn_location.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(ActivitySearchActivity.this, AreaActivityActivity.class);
                startActivityForResult(intent, 80);
            }
        });
        btn_category.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(ActivitySearchActivity.this, ActivityFilterActivity.class);
                startActivityForResult(intent, 70);
            }
        });
        bt_scroll.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mlist.smoothScrollToPosition(0);
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                setResult(70);
                finish();
            }
        });

        title_search.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(ActivitySearchActivity.this, SearchActivity.class);
                startActivityForResult(intent, 50);
                finish();
            }
        });

        getSearch();
    }

    public void getSearch(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url = CONFIG.search_activity_list;
//        search_txt = "서울";
        if(!TextUtils.isEmpty(search_txt)){
            url +="&search_text="+search_txt;
        }
        if(!TextUtils.isEmpty(banner_id)){
            url +="&banner_id="+banner_id;
        }
        if(!TextUtils.isEmpty(theme_id)){
            url += "&category=" + theme_id;
        }
        if(!TextUtils.isEmpty(city)){
            url += "&city=" + city;
        }

        url +="&per_page=20";

        if(Page < 2 || total_count != mItems.size()) {
            Api.get(url + "&page=" + Page, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(ActivitySearchActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }

                        final JSONArray list = obj.getJSONArray("lists");
                        JSONObject entry = null;
                        if (Page == 1)
                            s_position="";

                        final String total_cnt = "총 " + Util.numberFormat(obj.getInt("total_count")) + "개의 객실이 있습니다";
                        SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
                        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple)), 2, 2 + obj.getString("total_count").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_review_count.setText(builder);

                        for (int i = 0; i < list.length(); i++) {
                            entry = list.getJSONObject(i);
                            mItems.add(new SearchResultItem(
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
                                    i == 0 ? true : false
                            ));
                            if (Page == 1)
                                s_position += "&markers=icon:http://hotelnow.s3.amazonaws.com/etc/20181114_150606_DfU0o2DCag.png%7C" + entry.getString("latitude") + "%2C" + entry.getString("longitude");
                        }

                        if(mItems.size()>0){
                            bt_scroll.setVisibility(View.VISIBLE);
                        }
                        else {
                            bt_scroll.setVisibility(View.GONE);
                        }

                        String mapStr = "https://maps.googleapis.com/maps/api/staticmap?" +
                                s_position +
                                "&scale=2&sensor=false&language=ko&size=700x260" + "&key=" + BuildConfig.google_map_key2;
                        Ion.with(map_img).load(mapStr);

                        map_img.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                Intent intent = new Intent(ActivitySearchActivity.this, MapAcvitityActivity.class);
                                intent.putExtra("search_data", mItems);
                                intent.putExtra("Page", Page);
                                intent.putExtra("total_count", total_count);
                                startActivityForResult(intent, 90);
                            }
                        });

                        total_count = obj.getInt("total_count");
                        adapter.notifyDataSetChanged();
                        Page++;
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                }
            });
        }
        else {
            findViewById(R.id.wrapper).setVisibility(View.GONE);
        }
    }

    public void setLike(final int position, final boolean islike){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        final String sel_id = mItems.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch(Exception e){
            Log.e(CONFIG.TAG, e.toString());
            findViewById(R.id.wrapper).setVisibility(View.GONE);
        }
        if(islike){// 취소
            Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(ActivitySearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false,  sel_id,"A");
                        LogUtil.e("xxxx", "찜하기 취소");
                        showIconToast("관심 상품 담기 취소", false);
                        adapter.notifyDataSetChanged();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }catch (JSONException e){
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                }
            });
        }
        else{// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(ActivitySearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            showToast("로그인 후 이용해주세요");
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id,"A");
                        LogUtil.e("xxxx", "찜하기 성공");
                        showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }catch (JSONException e){
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void showToast(String msg){
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
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if(responseCode == 90) {
            if(data.getBooleanExtra("search_data", false)) {
                mItems = (ArrayList<SearchResultItem>)data.getSerializableExtra("search_data");;
                if(adapter != null)
                    adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == 80 && responseCode == 80) {
            city = data.getStringExtra("id");
            tv_location.setText(data.getStringExtra("name"));
            tv_elocation.setText(data.getStringExtra("name"));
            Page = 1;
            total_count = 0;
            mItems.clear();
            getSearch();
        }
        else if(requestCode == 70 && responseCode == 80) {
            theme_id = data.getStringExtra("id");
            tv_category.setText(data.getStringExtra("name"));
            tv_ecategory.setText(data.getStringExtra("name"));
            Page = 1;
            total_count = 0;
            mItems.clear();
            getSearch();
        }
        else if(requestCode == 50 && responseCode == 80) {
            adapter.notifyDataSetChanged();
        }
        else if(responseCode == 100){
            setResult(100);
            finish();
        }
    }
}
