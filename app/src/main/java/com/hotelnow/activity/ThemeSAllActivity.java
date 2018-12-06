package com.hotelnow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.ThemeSAllAdapter;
import com.hotelnow.fragment.model.ThemeSpecialItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
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
public class ThemeSAllActivity extends Activity {
    private ListView listview;
    public ArrayList<ThemeSpecialItem> mThemeSItem = new ArrayList<>();
    private ThemeSAllAdapter mAdapter;
    private DbOpenHelper dbHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stheme);

        Util.setStatusColor(this);

        dbHelper = new DbOpenHelper(this);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(80);
                finish();
            }
        });

        listview = (ListView) findViewById(R.id.listview);
        mAdapter = new ThemeSAllAdapter(this, 0, mThemeSItem, dbHelper);
        listview.setAdapter(mAdapter);

        getData();
    }

    private void getData() {

        Api.get(CONFIG.special_theme_list, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.has("result") || !obj.getString("result").equals("success")) {
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                    if(obj.has("theme_lists")){
                        JSONArray mThemeS = new JSONArray(obj.getJSONArray("theme_lists").toString());
                        mThemeSItem.clear();
                        if(mThemeS.length()>0) {
                            for (int i = 0; i < mThemeS.length(); i++) {
                                mThemeSItem.add(new ThemeSpecialItem(
                                        mThemeS.getJSONObject(i).getString("id"),
                                        mThemeS.getJSONObject(i).getString("title"),
                                        mThemeS.getJSONObject(i).getString("sub_title"),
                                        mThemeS.getJSONObject(i).getString("img_main_top"),
                                        mThemeS.getJSONObject(i).getString("img_main_list"),
                                        mThemeS.getJSONObject(i).getString("theme_flag"),
                                        mThemeS.getJSONObject(i).getString("subject"),
                                        mThemeS.getJSONObject(i).getString("detail"),
                                        mThemeS.getJSONObject(i).getString("notice"),
                                        mThemeS.getJSONObject(i).getString("img_background")
                                ));
                            }
                          mAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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