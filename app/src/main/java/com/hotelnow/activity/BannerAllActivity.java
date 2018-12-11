package com.hotelnow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.BannerAllAdapter;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class BannerAllActivity extends Activity {

    private ListView listview;
    private ArrayList<BannerItem> mItems = new ArrayList<>();
    private BannerAllAdapter mAdapter;
    private DbOpenHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_banner);

        dbHelper = new DbOpenHelper(this);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(80);
                finish();
            }
        });

        listview = (ListView) findViewById(R.id.listview);
        mAdapter = new BannerAllAdapter(this, 0, mItems, dbHelper, "Home");
        listview.setAdapter(mAdapter);

        getData();
    }

    private void getData(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        Api.get(CONFIG.banner_list+"?category=promotion", new Api.HttpCallback() {
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
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }

                    if(obj.has("banners")){
                        if(obj.getJSONArray("banners").length()>0) {
                            JSONArray banners = new JSONArray(obj.getJSONArray("banners").toString());
                            for(int i =0; i<banners.length(); i++){
                                JSONObject entry = banners.getJSONObject(i);
                                mItems.add(new BannerItem(
                                        entry.getString("id"),
                                        entry.getString("order"),
                                        entry.getString("category"),
                                        entry.getString("image"),
                                        entry.getString("keyword"),
                                        entry.getString("type"),
                                        entry.getString("evt_type"),
                                        entry.getString("event_id"),
                                        entry.getString("link"),
                                        entry.getString("title"),
                                        entry.getString("sub_title")
                                ));
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(80);
        finish();
        super.onBackPressed();
    }
}
