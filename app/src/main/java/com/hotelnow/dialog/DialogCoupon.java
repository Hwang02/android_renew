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
 * Created by susia on 15. 12. 10..
 */
public class DialogCoupon extends Dialog {
    private TextView message;
    private TextView title;
    private Button mLeftButton;
    private String coupon_title, coupon_message;

    private View.OnClickListener mLeftClickListener;

    public DialogCoupon(Context context, String mTitle, String mMessage, View.OnClickListener left) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        coupon_title = mTitle;
        coupon_message = mMessage;
        this.mLeftClickListener = left;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_coupon);

        message = (TextView) findViewById(R.id.coupon_message);
        mLeftButton = (Button) findViewById(R.id.left);
        title = (TextView) findViewById(R.id.title);
        message.setText(coupon_message);
        title.setText(coupon_title);
        mLeftButton.setOnClickListener(mLeftClickListener);
    }
}
