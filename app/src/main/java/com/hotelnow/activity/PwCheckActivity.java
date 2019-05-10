package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.OnSingleClickListener;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class PwCheckActivity extends Activity {

    private EditText et_pw;
    private DialogAlert dialogAlert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pw_check);

        et_pw = (EditText) findViewById(R.id.et_pw);

        findViewById(R.id.btn_check).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (et_pw.length() > 0) {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("password", et_pw.getText().toString());
                    }
                    catch (JSONException e){
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        Toast.makeText(PwCheckActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        et_pw.setText("");
                    }
                    Api.post(CONFIG.retirepwUrl, params.toString(), new Api.HttpCallback() {
                        @Override
                        public void onFailure(Response response, Exception throwable) {
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            Toast.makeText(PwCheckActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                            et_pw.setText("");
                        }

                        @Override
                        public void onSuccess(Map<String, String> headers, String body) {
                            try {
                                JSONObject obj = new JSONObject(body);

                                if (!obj.getString("result").equals("success")) {
                                    dialogAlert = new DialogAlert("알림", obj.getString("msg"), PwCheckActivity.this, new OnSingleClickListener() {
                                        @Override
                                        public void onSingleClick(View v) {
                                            dialogAlert.dismiss();
                                            et_pw.setText("");
                                        }
                                    });
                                    dialogAlert.show();
                                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                                    return;
                                }

                                Intent intent = new Intent(PwCheckActivity.this, RetireActivity.class);
                                startActivityForResult(intent, 999);
                                findViewById(R.id.wrapper).setVisibility(View.GONE);
                                et_pw.setText("");

                            } catch (Exception e) {
                                findViewById(R.id.wrapper).setVisibility(View.GONE);
                                Toast.makeText(PwCheckActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                et_pw.setText("");
                            }
                        }
                    });
                }
            }
        });

        findViewById(R.id.title_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //셋팅화면으로 이동
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 999)
        {
            if(resultCode == 888){
                finish();
            }
            else if(resultCode == 999){
                setResult(999);
                finish();
            }
        }
    }
}
