package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DialogPush extends Dialog {
    private View.OnClickListener mOkClickListener;
    private Context mContext;
    public SharedPreferences _preferences;
    private DialogDiscountAlert dialogDiscountAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.3f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_push);

        final CheckBox left = (CheckBox) findViewById(R.id.left);
        TextView right = (TextView) findViewById(R.id.right);
        TextView tv_info = (TextView) findViewById(R.id.tv_info);
        Button ok = (Button) findViewById(R.id.ok);

        SpannableStringBuilder builder2 = new SpannableStringBuilder(tv_info.getText().toString());
        builder2.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.purple)), 7, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_info.setText(builder2);

        _preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if(left.isChecked()){
                    // 오늘 하루 닫기
                    Date currentTime = new Date();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 14);
                }
                else {
                    Date currentTime = new Date();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }

                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String checkdate = mSimpleDateFormat.format(calendar.getTime());
                if(_preferences != null) {
                    Util.setPreferenceValues(_preferences, "user_push_date", checkdate);
                }
                dismiss();

                ((MainActivity)mContext).HomePopup();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPush();
                Util.setPreferenceValues(_preferences, "user_push", true);
                dismiss();
            }
        });

    }

    private void setPush(){
        // 푸시 수신 상태값 저장
        String regId = _preferences.getString("gcm_registration_id", null);
        String userId = _preferences.getString("userid", null);

        LogUtil.e("xxxxx", regId);
        if(regId != null)
            setGcmToken(getContext(), regId, userId, true);
    }

    // GCM TOKEN
    public void setGcmToken(final Context context, String regId, String userId, final Boolean flag){
        String androidId = Util.getAndroidId(context);

        JSONObject paramObj = new JSONObject();

        try{
            paramObj.put("os", "a");
            paramObj.put("uuid", androidId);
            paramObj.put("push_token", regId);
            paramObj.put("ver", Util.getAppVersionName(context));

            if(flag != null) {
                paramObj.put("use_yn", ((flag == true)? "Y":"N"));
            }
            if(userId != null) paramObj.put("user_id", userId);
        } catch (JSONException e) {}

        Api.post(CONFIG.notiSettingUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(obj.has("use_yn")) {
//                        Toast.makeText(HotelnowApplication.getAppContext(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
//                        tv_push.setSelected((obj.getString("use_yn").equals("Y")));
//                        if (cb_push.isChecked()) {
                        dialogDiscountAlert = new DialogDiscountAlert(
                                    "할인 혜택 알림 수신 동의 안내",
                                    "앱 PUSH (동의)",
                                    "수신 동의 일시 : " + obj.getString("updated_at").substring(0, 16),
                                    "위의 내용으로 호텔나우 혜택 알림 수신에 동의 하셨습니다.",
                                    "특가 정보, 이벤트, 할인쿠폰 소식 받고 즐거운 여행을 떠나세요!",
                                    getContext(),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogDiscountAlert.dismiss();
                                        }
                                    }
                            );
                        dialogDiscountAlert.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(HotelnowApplication.getAppContext(), getContext().getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public DialogPush(Context context, View.OnClickListener ok) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        this.mOkClickListener = ok;
    }
}