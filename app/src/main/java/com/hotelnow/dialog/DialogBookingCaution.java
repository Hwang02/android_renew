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
 * Created by susia on 16. 1. 10..
 */
public class DialogBookingCaution extends Dialog {
    private Context mContext;
    private TextView caution_txt;
    private Button mLeftButton;
    private Button mRightButton;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_booking_caution);

        caution_txt = (TextView) findViewById(R.id.caution_txt);
        mLeftButton = (Button) findViewById(R.id.btn_skip);
        mRightButton = (Button) findViewById(R.id.btn_ok);

        mLeftButton.setOnClickListener(mLeftClickListener);
        mRightButton.setOnClickListener(mRightClickListener);

    }

    public DialogBookingCaution(Context context, View.OnClickListener left , View.OnClickListener right) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);

        mContext = context;
        this.mLeftClickListener = left;
        this.mRightClickListener = right;
    }

}

