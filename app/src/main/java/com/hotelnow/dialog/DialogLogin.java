package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
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
    private View.OnClickListener mViewClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_login);

        final CheckBox left = (CheckBox) findViewById(R.id.left);
        TextView right = (TextView) findViewById(R.id.right);
        _preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        popup_img = (ImageView) findViewById(R.id.popup_img);
        Ion.with(popup_img).load(img_url);
        popup_img.setOnClickListener(mViewClickListener);
        right.setOnClickListener(mOkClickListener);
    }

    public DialogLogin(Context context, View.OnClickListener ok, View.OnClickListener view, String img_url, String img_link, String page) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        this.mOkClickListener = ok;
        this.mViewClickListener = view;
        this.img_url = img_url;
        this.img_link = img_link;
        this.page = page;
    }
}