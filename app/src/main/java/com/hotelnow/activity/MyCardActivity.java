package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.MyCardAdapter;
import com.hotelnow.base.BaseActivity;
import com.hotelnow.fragment.model.CardEntry;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MyCardActivity extends BaseActivity{

    private SharedPreferences _preferences;
    private String cookie;
    private View header, footer;
    private ListView mMainListView;
    private ArrayList<CardEntry> cardEntries = new ArrayList<CardEntry>();
    private MyCardAdapter mAdapter;
    private RelativeLayout card_add;
    public boolean mRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycard);

        Util.setStatusColor(this);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cookie = _preferences.getString("userid", null);
        if(cookie == null){
            Toast.makeText(MyCardActivity.this, getString(R.string.error_need_login), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        header = getLayoutInflater().inflate(R.layout.layout_mycard_header, null, false);
        footer = getLayoutInflater().inflate(R.layout.layout_mycard_footer, null, false);

        mMainListView = (ListView)findViewById(R.id.lv_card);
        mMainListView.addHeaderView(header);
        mMainListView.addFooterView(footer);

        mAdapter = new MyCardAdapter(MyCardActivity.this, 0, cardEntries);
        mMainListView.setAdapter(mAdapter);

        card_add = (RelativeLayout) header.findViewById(R.id.card_add);
        card_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCardActivity.this, CardAddActivity.class);
                startActivityForResult(intent, 91);
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getCardList();
    }

    public void getCardList() {
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        Api.get(CONFIG.cardManageUrl, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(MyCardActivity.this, "카드 정보를 확인할 수 없습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(MyCardActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cardEntries.clear();
                    JSONArray feed = obj.getJSONArray("data");
                    JSONObject entry = null;

                    ((TextView)header.findViewById(R.id.card_count)).setText(feed.length()+"");

                    for (int i = 0; i < feed.length(); i++) {
                        entry = feed.getJSONObject(i);

                        cardEntries.add(new CardEntry(
                                entry.getString("cardno"),
                                entry.getString("cardtype"),
                                entry.getString("cardcd"),
                                entry.getString("id"),
                                entry.getString("cardnm")
                        ));
                    }

                    if(cardEntries.size() == 0){
                        cardEntries.add(new CardEntry(
                                "",
                                "",
                                "",
                                "empty",
                                ""
                        ));
                    }

                    mAdapter.notifyDataSetChanged();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    Toast.makeText(MyCardActivity.this, "카드 정보를 확인할 수 없습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 91 && resultCode == 91){
            getCardList();
            mRefresh = true;
        }
    }

    public void getEmptyHeight(View empty_item){

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int height = dm.heightPixels;

        header.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        footer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        (findViewById(R.id.toolbar)).measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int heightTop = header.getHeight();
        int heightBottom = footer.getHeight();
        int heightBar = (findViewById(R.id.toolbar)).getHeight();

        int realHeight = height - heightTop;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) empty_item.getLayoutParams();
        lp.height = realHeight;
        empty_item.setLayoutParams(lp);
    }

    @Override
    public void onBackPressed() {
        finishSignup();
        super.onBackPressed();
    }

    // 회원가입 종료
    private void finishSignup(){
        if(mRefresh) {
            Intent intent = new Intent();
            setResult(91, intent);
        }
        finish();
    }
}
