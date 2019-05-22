package com.hotelnow.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.dialog.DialogRetire;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class RetireActivity extends Activity {


    private Spinner retire_spinner;
    private ArrayList<String> list = new ArrayList<>();
    private String sel_message = "";
    private EditText ed_etc;
    private SpannableStringBuilder sb = new SpannableStringBuilder();
    private TextView retire_message_4;
    private ArrayList<String> _avail;
    private DialogAlert dialogAlert;
    private CheckBox agree_policy;
    private DialogRetire dialogRetire;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_retire);

        retire_spinner = (Spinner) findViewById(R.id.sel_retire);
        ed_etc = (EditText)findViewById(R.id.ed_etc);
        retire_message_4 = (TextView)findViewById(R.id.retire_message_4);
        agree_policy = (CheckBox) findViewById(R.id.agree_policy);
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        setRetireInfo();

        sb.append(retire_message_4.getText().toString());
        sb.setSpan(new StyleSpan(Typeface.BOLD), 108, retire_message_4.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        retire_message_4.setText(sb);

        findViewById(R.id.title_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //셋팅화면으로 이동
                setResult(888);
                finish();
            }
        });

        findViewById(R.id.btn_retire).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
                final JSONObject params = new JSONObject();
                try {
                    if (sel_message.equals("기타")) {
                        if (ed_etc.getText().toString().length() > 10) {
                            params.put("retired_reason", ed_etc.getText().toString());
                        } else {
                            dialogAlert = new DialogAlert("알림", "10글자 이상 입력해주셔야 합니다.", RetireActivity.this, new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    dialogAlert.dismiss();
                                }
                            });
                            dialogAlert.show();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }
                    } else {
                        if(!TextUtils.isEmpty(sel_message)) {
                            params.put("retired_reason", sel_message);
                        }
                        else{
                            dialogAlert = new DialogAlert("알림", "탈퇴 사유를 선택해주셔야 합니다.", RetireActivity.this, new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    dialogAlert.dismiss();
                                }
                            });
                            dialogAlert.show();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }
                    }

                    if(agree_policy.isChecked() != true){
                        dialogAlert = new DialogAlert("알림", "호텔나우 회원탈퇴 안내에 대한 확인 및 동의 되지 않았습니다", RetireActivity.this, new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                dialogAlert.dismiss();
                            }
                        });
                        dialogAlert.show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }
                    else{
                        dialogRetire = new DialogRetire("회원탈퇴", "확인을 누르시면 최종 호텔나우\n 서비스에 탈퇴 처리 됩니다.\n정말로 회원탈퇴를 진행하시겠습니까?", "취소", "확인", RetireActivity.this, new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                dialogRetire.dismiss();
                                findViewById(R.id.wrapper).setVisibility(View.GONE);
                            }
                        }, new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                dialogRetire.dismiss();
                                Api.post(CONFIG.retireUrl, params.toString(), new Api.HttpCallback() {
                                    @Override
                                    public void onFailure(Response response, Exception throwable) {
                                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                                        Toast.makeText(RetireActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess(Map<String, String> headers, String body) {
                                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                                        setResult(999);
                                        finish();

                                    }
                                });
                            }
                        });
                        dialogRetire.show();
                    }
                }catch(JSONException e){
                    Toast.makeText(RetireActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //셋팅화면으로 이동
        setResult(888);
        finish();
        super.onBackPressed();
    }

    private void setRetireInfo(){
        JSONObject params = new JSONObject();

        Api.post(CONFIG.retireinfoUrl, params.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                findViewById(R.id.wrapper).setVisibility(View.GONE);
                Toast.makeText(RetireActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                        return;
                    }

                    JSONObject info = obj.getJSONObject("retire_info");

                    TextView tv_reserve = (TextView) findViewById(R.id.tv_reserve);
                    TextView tv_promotion = (TextView) findViewById(R.id.tv_promotion);
                    TextView tv_booking = (TextView) findViewById(R.id.tv_booking);
                    TextView tv_qbooking = (TextView) findViewById(R.id.tv_qbooking);

                    tv_reserve.setText(": "+ Util.numberFormat(info.getInt("reserve_money"))+"p");
                    tv_promotion.setText(": "+ info.getString("promotion_cnt")+"장");
                    tv_booking.setText(": "+ info.getString("booking_count")+"건");
                    tv_qbooking.setText(": "+ info.getString("q_booking_count")+"건");

                    list.clear();
                    _avail = new ArrayList<String>();
                    list.add("사유를 선택해주세요");
                    _avail.add("N");
                    if(info.getJSONArray("retired_reasons").length() > 0) {
                        for (int i = 0; i < info.getJSONArray("retired_reasons").length(); i++) {
                            list.add(info.getJSONArray("retired_reasons").get(i).toString());
                            LogUtil.e("xxxx",list.get(i));
                            _avail.add("Y");
                        }
                    }
                    list.add("기타");
                    _avail.add("Y");

                    //using ArrayAdapter
//                    ArrayAdapter spinnerAdapter;
//                    spinnerAdapter = new ArrayAdapter(RetireActivity.this, R.layout.layout_spinner_item, list);
//                    retire_spinner.setAdapter(spinnerAdapter);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RetireActivity.this, R.layout.layout_spinner_item, list) {
                        @Override
                        public boolean isEnabled(int position) {
                            if (_avail.get(position).equals("Y")) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        @Override
                        public View getDropDownView(int position, View convertView,
                                                    ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            if (_avail.get(position).equals("N")) {
                                // Set the disable item text color
                                tv.setBackgroundResource(R.color.white);
                                tv.setTextColor(getResources().getColor(R.color.graytxt));
                            } else {
                                tv.setBackgroundResource(R.color.white);
                                tv.setTextColor(getResources().getColor(R.color.blacktxt));
                            }
                            return view;
                        }
                    };
                    retire_spinner.setAdapter(adapter);
                    //event listener
                    retire_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            Toast.makeText(RetireActivity.this,"선택된 아이템 : "+retire_spinner.getItemAtPosition(position),Toast.LENGTH_SHORT).show();

                            sel_message = retire_spinner.getItemAtPosition(position)+"";
                            if(sel_message.equals("기타")){
                                ed_etc.setVisibility(View.VISIBLE);
                            }
                            else{
                                ed_etc.setVisibility(View.GONE);
                            }

                            if(position == 0){
                                TextView tv = (TextView) view;
                                sel_message ="";
                                tv.setTextColor(getResources().getColor(R.color.graytxt));
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                } catch (Exception e) {
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                    Toast.makeText(RetireActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
