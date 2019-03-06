package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.MyCouponAdapter;
import com.hotelnow.base.BaseActivity;
import com.hotelnow.fragment.model.CouponEntry;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

public class MyCouponActivity extends BaseActivity{

    private SharedPreferences _preferences;
    private String cookie;
    private View header, footer;
    private ListView mMainListView;
    private ArrayList<CouponEntry> cpnEntries = new ArrayList<CouponEntry>();
    private MyCouponAdapter mAdapter;
    private Button btn_ok;
    private EditText code;
    private boolean mRefresh = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycoupon);

        Util.setStatusColor(this);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cookie = _preferences.getString("userid", null);
        if(cookie == null){
            Toast.makeText(MyCouponActivity.this, getString(R.string.error_need_login), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        header = getLayoutInflater().inflate(R.layout.layout_mycoupon_header, null, false);
        footer = getLayoutInflater().inflate(R.layout.layout_mycoupon_footer, null, false);

        btn_ok = (Button) header.findViewById(R.id.btn_ok);
        code = (EditText) header.findViewById(R.id.code);

        mMainListView = (ListView)findViewById(R.id.lv_coupon);
        mMainListView.addHeaderView(header);
        mMainListView.addFooterView(footer);

        mAdapter = new MyCouponAdapter(MyCouponActivity.this, 0, cpnEntries);
        mMainListView.setAdapter(mAdapter);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponApply();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        authCheck();
    }

    public void authCheck() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", Util.decode(_preferences.getString("userid", null).replace("HN|","")));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
            paramObj.put("ver", Util.getAppVersionName(MyCouponActivity.this));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                return;
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (obj.getString("result").equals("0")) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("email", null);
                        prefEditor.putString("username", null);
                        prefEditor.putString("phone", null);
                        prefEditor.putString("userid", null);
                        prefEditor.commit();
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_need_login), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    getCouponList();

                } catch (Exception e) {

                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void couponApply(){
        String inputCode = code.getText().toString().trim();

        if(inputCode.equals("") || inputCode.equals(null)){
            Toast.makeText(MyCouponActivity.this, "쿠폰명을 입력하여 주십시요", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("pcode", inputCode);
            paramObj.put("uuid", Util.getAndroidId(MyCouponActivity.this));
        } catch(Exception e){ }
        int dummyInt = (int) (Math.random() * 1000);
        Api.post(CONFIG.promotionUrl+"?q="+dummyInt, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {

            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(MyCouponActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    code.setText("");
                    Toast.makeText(MyCouponActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getCouponList();
                            mRefresh = true;
                        }
                    },500);

                } catch (Exception e) { }
            }
        });
    }

    private void getCouponList(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        Api.get(CONFIG.promotionListUrl, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(MyCouponActivity.this, getString(R.string.error_coupon_money), Toast.LENGTH_SHORT).show();
                findViewById(R.id.wrapper).setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(MyCouponActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    cpnEntries.clear();
                    JSONArray feed = obj.getJSONArray("data");
                    JSONObject entry = null;

                    NumberFormat nf = NumberFormat.getNumberInstance();
                    ((TextView) header.findViewById(R.id.disable_coupon)).setText(nf.format(obj.getInt("expire_coupon_cnt"))+"장");
                    ((TextView) header.findViewById(R.id.coupon_count)).setText(nf.format(feed.length()));

                    for (int i = 0; i < feed.length(); i++) {
                        entry = feed.getJSONObject(i);
                        String target_lists[] = null;
                        String option_item[] = null;

                        if(entry.has("option") && entry.getJSONArray("option") != null) {
                            JSONArray item = entry.getJSONArray("option");
                            option_item = new String[item.length()];
                            for(int j=0; j<item.length(); j++){
                                option_item[j] = item.get(j).toString();
                            }
                        }

                        if(entry.has("target_lists") && entry.getJSONArray("target_lists") != null) {
                            JSONArray item = entry.getJSONArray("target_lists");
                            target_lists = new String[item.length()];
                            for(int j=0; j<item.length(); j++){
                                target_lists[j] = item.get(j).toString();
                            }
                        }

                        cpnEntries.add(new CouponEntry(
                                        entry.getString("product"),
                                        entry.getString("title"),
                                        entry.getString("isValid"),
                                        entry.getString("name"),
                                        entry.getString("discount_value"),
                                        entry.getString("expiration_date"),
                                        option_item,
                                        entry.getString("min_price"),
                                        entry.getString("target_text"),
                                        target_lists,
                                        entry.getString("expire_text")
                        ));
                    }

                    String[] ex= new String[1];
                    ex[0]="xxxx";
                    if(cpnEntries.size() == 0){
                        cpnEntries.add(new CouponEntry(
                                "empty",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ex,
                                "",
                                "",
                                ex,
                                ""
                        ));
                    }

                    mAdapter.notifyDataSetChanged();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);

                } catch (Exception e) {
                    Toast.makeText(MyCouponActivity.this, getString(R.string.error_coupon_money), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
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
        int heightBar = (findViewById(R.id.toolbar)).getHeight();

        int realHeight = height - heightTop - heightBottom - heightBar;
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
