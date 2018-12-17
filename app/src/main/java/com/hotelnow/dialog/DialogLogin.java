package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hotelnow.R;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.WebviewActivity;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogLogin extends Dialog {
    private View.OnClickListener mOkClickListener;
    private Context mContext;
    public SharedPreferences _preferences;
    private String img_url, img_link, page;
    private ImageView popup_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_login);

        final CheckBox left = (CheckBox) findViewById(R.id.left);
        TextView right = (TextView) findViewById(R.id.right);
        _preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        popup_img = (ImageView) findViewById(R.id.popup_img);
        Ion.with(popup_img).load(img_url);

        if(page.equals("info")) {

            findViewById(R.id.popup_bg).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(mContext, WebviewActivity.class);
                    intent.putExtra("url", img_link);
                    intent.putExtra("title", "공지");
                    mContext.startActivity(intent);

                    Calendar calendar = Calendar.getInstance();
                    Date currentTime = new Date();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String checkdate = mSimpleDateFormat.format(calendar.getTime());
                    if (_preferences != null) {
                        Util.setPreferenceValues(_preferences, "info_date", checkdate);
                    }
                    dismiss();
                    ((MainActivity)mContext).HomePopup();
                }
            });

            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    if (left.isChecked()) {
                        // 오늘 하루 닫기
                        Date currentTime = new Date();
                        calendar.setTime(currentTime);
                        calendar.add(Calendar.DAY_OF_YEAR, 14);
                    } else {
                        // 닫기
                        Date currentTime = new Date();
                        calendar.setTime(currentTime);
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String checkdate = mSimpleDateFormat.format(calendar.getTime());
                    if (_preferences != null) {
                        Util.setPreferenceValues(_preferences, "info_date", checkdate);
                    }
                    dismiss();
                    ((MainActivity)mContext).HomePopup();
                }
            });
        }
        else{

            findViewById(R.id.popup_bg).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    ((MainActivity)mContext).setTapMove(3, true);

                    Calendar calendar = Calendar.getInstance();
                    Date currentTime = new Date();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String checkdate = mSimpleDateFormat.format(calendar.getTime());
                    if (_preferences != null) {
                        Util.setPreferenceValues(_preferences, "user_app_login_date", checkdate);
                    }
                    dismiss();
                    ((MainActivity)mContext).HomePopup();
                }
            });

            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    if (left.isChecked()) {
                        // 오늘 하루 닫기
                        Date currentTime = new Date();
                        calendar.setTime(currentTime);
                        calendar.add(Calendar.DAY_OF_YEAR, 14);
                    } else {
                        // 닫기
                        Date currentTime = new Date();
                        calendar.setTime(currentTime);
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }

                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String checkdate = mSimpleDateFormat.format(calendar.getTime());
                    if (_preferences != null) {
                        Util.setPreferenceValues(_preferences, "user_app_login_date", checkdate);
                    }
                    dismiss();
                    ((MainActivity)mContext).HomePopup();
                }
            });
        }

    }

    public DialogLogin(Context context, View.OnClickListener ok, String img_url, String img_link, String page) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        this.mOkClickListener = ok;
        this.img_url = img_url;
        this.img_link = img_link;
        this.page = page;
    }
}