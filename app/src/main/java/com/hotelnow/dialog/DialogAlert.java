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
public class DialogAlert extends Dialog {
    private String mTitle;
    private String mMessage;
    private View.OnClickListener mOkClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_alert);

        TextView tvTitle = (TextView) findViewById(R.id.title);
        TextView tvMessage = (TextView) findViewById(R.id.message);
        Button mOkButton = (Button) findViewById(R.id.ok);
        mOkButton.setOnClickListener(mOkClickListener);

        tvTitle.setText(mTitle);
        tvMessage.setText(mMessage);
    }

    public DialogAlert(String title, String message, Context context, View.OnClickListener ok) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);

        mTitle = title;
        mMessage = message;
        this.mOkClickListener = ok;
    }
}
