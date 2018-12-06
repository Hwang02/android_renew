package com.hotelnow.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.MySaveAdapter;
import com.hotelnow.fragment.model.MySaveMoneyItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

public class MySaveActivity extends Activity{

    private SharedPreferences _preferences;
    private static final int PAGE_SIZE = 10000;
    private ListView mMainListView;
    private View header, footer;
    private MySaveAdapter mAdapter;
    private ArrayList<MySaveMoneyItem> mEntries = new ArrayList<MySaveMoneyItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysave);

        Util.setStatusColor(this);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        header = getLayoutInflater().inflate(R.layout.layout_mysave_header, null, false);
        footer = getLayoutInflater().inflate(R.layout.layout_mysave_footer, null, false);

        mMainListView = (ListView)findViewById(R.id.lv_save);
        mMainListView.addHeaderView(header);
        mMainListView.addFooterView(footer);

        mAdapter = new MySaveAdapter(MySaveActivity.this, 0, mEntries);
        mMainListView.setAdapter(mAdapter);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        getlist();
    }

    private void getlist(){
        String url = CONFIG.reservemoneyUrl+"/"+1+"/"+PAGE_SIZE;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(MySaveActivity.this, getString(R.string.error_reserve_money), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(MySaveActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject info = obj.getJSONObject("info");
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    ((TextView) header.findViewById(R.id.disable_money)).setText(nf.format(info.getInt("expire_amount"))+"원");
                    ((TextView) header.findViewById(R.id.my_money)).setText(nf.format(info.getInt("amount")));

                    JSONArray feed = obj.getJSONArray("data");
                    JSONObject entry;

                    for (int i = 0; i < feed.length(); i++) {
                        entry = feed.getJSONObject(i);
                        mEntries.add(new MySaveMoneyItem(
                                entry.getString("recommendee_name"),
                                entry.getInt("id"),
                                entry.getString("type"),
                                entry.getInt("income"),
                                entry.getInt("spent"),
                                entry.getInt("remain"),
                                entry.getInt("amount"),
                                entry.getString("use_yn"),
                                entry.getString("created_at"),
                                entry.getString("end_date"),
                                entry.getString("type_dp"),
                                entry.getString("change_dp")
                                )
                        );
                    }

                    if(mEntries.size() == 0){
                        mEntries.add(new MySaveMoneyItem(
                                "",
                                0,
                                "",
                                0,
                                0,
                                0,
                                0,
                                "",
                                "",
                                "",
                                "",
                                "")
                        );
                    }

                    mAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e(CONFIG.TAG, "expection is ", e);
                    Toast.makeText(MySaveActivity.this, getString(R.string.error_reserve_money), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getEmptyHeight(View empty_item){

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int height = dm.heightPixels;


        header.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        footer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        (findViewById(R.id.toolbar)).measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int heightTop = header.getHeight();
        int heightBottom = footer.getHeight();
        int heightBar =  (findViewById(R.id.toolbar)).getHeight();

        int realHeight = height - heightTop - heightBottom - heightBar - Util.dptopixel(this, 26);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) empty_item.getLayoutParams();
        lp.height = realHeight;
        empty_item.setLayoutParams(lp);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
