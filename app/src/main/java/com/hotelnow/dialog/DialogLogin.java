package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.hotelnow.R;
import com.hotelnow.utils.Util;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogLogin extends Dialog {
    private View.OnClickListener mOkClickListener;
    private Context mContext;
    public SharedPreferences _preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_login);

        final CheckBox left = (CheckBox) findViewById(R.id.left);
        TextView right = (TextView) findViewById(R.id.right);
        _preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(left.isChecked()){
                    // 오늘 하루 닫기
                    Calendar calendar = Calendar.getInstance();
                    Date currentTime = new Date();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 7);

                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String checkdate = mSimpleDateFormat.format(calendar.getTime());
                    if(_preferences != null) {
                        Util.setPreferenceValues(_preferences, "user_app_login_date", checkdate);
                    }
                    dismiss();
                }
                else {
                    // 닫기
                    dismiss();
                    Util.setPreferenceValues(_preferences, "user_app_login", true);
                }
            }
        });

    }

    public DialogLogin(Context context, View.OnClickListener ok) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        this.mOkClickListener = ok;
    }
}