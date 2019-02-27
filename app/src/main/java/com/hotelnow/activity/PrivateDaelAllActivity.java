package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.adapter.PrivateDealAllAdapter;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;

public class PrivateDaelAllActivity extends Activity{

    private DbOpenHelper dbHelper;
    private SharedPreferences _preferences;
    private ArrayList<SearchResultItem> mHotelItem = new ArrayList<>();
    private ListView mlist;
    private PrivateDealAllAdapter adapter;
    private RelativeLayout toast_layout;
    private ImageView ico_favorite;
    private TextView tv_toast;
    private String cookie;
    private boolean isLogin= false;
    private View header;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_privatedael);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new DbOpenHelper(this);

        header = getLayoutInflater().inflate(R.layout.layout_private_header, null, false);
        toast_layout = (RelativeLayout) findViewById(R.id.toast_layout);
        ico_favorite = (ImageView) findViewById(R.id.ico_favorite);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        mlist = (ListView) findViewById(R.id.listview);
        mlist.addHeaderView(header);
        adapter = new PrivateDealAllAdapter(this, 0, mHotelItem, dbHelper);
        mlist.setAdapter(adapter);
        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView)view.findViewById(R.id.hid);
                Intent intent = new Intent(PrivateDaelAllActivity.this, DetailHotelActivity.class);
                intent.putExtra("hid", hid.getText().toString());
                intent.putExtra("save", true);
                startActivityForResult(intent, 50);
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                finished();
            }
        });
        getData();

    }

    @Override
    public void onBackPressed() {
        finished();
        super.onBackPressed();
    }

    public void finished(){
        if(isLogin){
            setResult(110);
        }
        finish();
    }

    private void getData(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        Api.get(CONFIG.hotdeal_list + "/private_deals", new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(PrivateDaelAllActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(obj.has("private_deals")){
                        JSONArray mStay = new JSONArray(obj.getJSONArray("private_deals").toString());
                        mHotelItem.clear();
                        if(mStay.length()>0) {
                            for (int i = 0; i < mStay.length(); i++) {
                                mHotelItem.add(new SearchResultItem(
                                        mStay.getJSONObject(i).getString("id"),
                                        "",
                                        mStay.getJSONObject(i).getString("name"),
                                        "",
                                        mStay.getJSONObject(i).getString("category"),
                                        mStay.getJSONObject(i).has("street1") ? mStay.getJSONObject(i).getString("street1") : "",
                                        mStay.getJSONObject(i).has("street2") ? mStay.getJSONObject(i).getString("street2") : "",
                                        0,
                                        0,
                                        "N",
                                        mStay.getJSONObject(i).getString("landscape"),
                                        mStay.getJSONObject(i).getString("sale_price"),
                                        mStay.getJSONObject(i).getString("normal_price"),
                                        mStay.getJSONObject(i).getString("sale_rate"),
                                        mStay.getJSONObject(i).getInt("items_quantity"),
                                        mStay.getJSONObject(i).getString("special_msg"),
                                        mStay.getJSONObject(i).getString("review_score"),
                                        mStay.getJSONObject(i).getString("grade_score"),
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        mStay.getJSONObject(i).getString("is_private_deal"),
                                        mStay.getJSONObject(i).getString("is_hot_deal"),
                                        mStay.getJSONObject(i).getString("is_add_reserve"),
                                        mStay.getJSONObject(i).getInt("coupon_count"),
                                        i == 0 ? true : false,
                                        0
                                ));
                            }
                            adapter.notifyDataSetChanged();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                        }
                    }
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

    public void setLike(final int position, final boolean islike){
        final String sel_id = mHotelItem.get(position).getId();
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
                    Toast.makeText(PrivateDaelAllActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
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
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
        else{// 성공
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(PrivateDaelAllActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
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
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){

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
                }, 1500);
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
                }, 1500);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if(requestCode == 50 && responseCode == 90) {
            setResult(80);
            finish();
        }
        else if(requestCode == 50 && responseCode == 80){
            adapter.notifyDataSetChanged();
        }
        else{
            cookie = _preferences.getString("userid", null);
            if(cookie != null){
                isLogin = true;
            }
        }
    }
}
