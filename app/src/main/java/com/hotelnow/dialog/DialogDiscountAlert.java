package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hotelnow.R;

/**
 * Created by susia on 15. 11. 26..
 */
public class DialogDiscountAlert extends Dialog {
    private String mTitle, mMessage, check_date, check_status, main_title;
    private View.OnClickListener mOkClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_discount_alert);

        TextView tvTitle = (TextView) findViewById(R.id.title);
        TextView tvMessage = (TextView) findViewById(R.id.message);
        TextView tvcheck_date = (TextView) findViewById(R.id.check_date);
        TextView tvcheck_status = (TextView) findViewById(R.id.check_status);
        TextView tvmain_title = (TextView) findViewById(R.id.main_title);
        Button mOkButton = (Button) findViewById(R.id.ok);
        mOkButton.setOnClickListener(mOkClickListener);

        tvTitle.setText(mTitle);
        tvMessage.setText(mMessage);
        tvcheck_date.setText(check_date);
        tvcheck_status.setText(check_status);
        tvmain_title.setText(main_title);
    }

    public DialogDiscountAlert(String main_title, String title, String check_date, String check_status, String message, Context context, View.OnClickListener ok) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        mTitle = title;
        mMessage = message;
        this.check_date = check_date;
        this.check_status = check_status;
        this.main_title = main_title;
        this.mOkClickListener = ok;
    }
}
