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

import com.hotelnow.R;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogPush extends Dialog {
    private View.OnClickListener mOkClickListener;
    private Context mContext;
    public SharedPreferences _preferences;

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
                //

                Util.setPreferenceValues(_preferences, "user_push", true);
                dismiss();
            }
        });

    }

    public DialogPush(Context context, View.OnClickListener ok) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        this.mOkClickListener = ok;
    }
}