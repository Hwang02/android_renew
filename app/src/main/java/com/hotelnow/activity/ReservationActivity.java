package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.LogUtil;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.Map;

public class ReservationActivity extends Activity {

    private EditText point_discount;
    private String pid, accepted_price, ec_date, ee_date, cookie;
    private SharedPreferences _preferences;
    private LinearLayout layout_useremail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        _preferences = PreferenceManager.getDefaultSharedPreferences(ReservationActivity.this);
        point_discount = (EditText) findViewById(R.id.point_discount);
        layout_useremail = (LinearLayout) findViewById(R.id.layout_useremail);
        cookie = _preferences.getString("userid", null);

        point_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    point_discount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_input_delete, 0);
                }
                else if(s.length()==0){
                    point_discount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        point_discount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP && point_discount.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                    if(event.getRawX() >= (point_discount.getRight() - point_discount.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        point_discount.setText("");
                        LogUtil.e("xxxx", "xxxx삭제");
                        return true;
                    }
                }
                return false;
            }
        });

        Intent intent = getIntent();
        intent.getStringExtra("ec_date");
        intent.getStringExtra("ee_date");


        authCheck();
    }

    public void authCheck() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", _preferences.getString("userid", null));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivity.this, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    layout_useremail.setVisibility(View.GONE);
                    if (obj.getString("result").equals("0")) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("email", null);
                        prefEditor.putString("username", null);
                        prefEditor.putString("phone", null);
                        prefEditor.putString("userid", null);
                        prefEditor.commit();
                        layout_useremail.setVisibility(View.VISIBLE);
                    }
                    getDealInfo();
                } catch (Exception e) {

                }
            }
        });
    }

    private void getDealInfo() {
        String url;

        if (ec_date != null && ee_date != null)
            url = CONFIG.reserveUrl + "/" + pid + "?ec_date=" + ec_date + "&ee_date=" + ee_date + "&consecutive=Y";
        else
            url = CONFIG.reserveUrl + "/" + pid;

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReservationActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {

                } catch (Exception e) {

                }
            }
        });
    }
}
