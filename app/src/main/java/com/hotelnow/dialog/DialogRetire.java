package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.utils.Util;

/**
 * Created by susia on 15. 11. 26..
 */
public class DialogRetire extends Dialog {
    private String mTitle;
    private String mMessage;
    private String mLeftText;
    private String mRightText;
    private Boolean misHtml = false;
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;
    private Context mContext;

    public DialogRetire(String title, String message, String leftText, String rightText, Context context, View.OnClickListener left, View.OnClickListener right) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        mTitle = title;
        mMessage = message;
        mLeftText = leftText;
        mRightText = rightText;
        this.mLeftClickListener = left;
        this.mRightClickListener = right;
        mContext = context;
    }

    public DialogRetire(String title, String message, String leftText, String rightText, Context context, View.OnClickListener left, View.OnClickListener right, Boolean isHtml) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        mTitle = title;
        mMessage = message;
        mLeftText = leftText;
        mRightText = rightText;
        this.mLeftClickListener = left;
        this.mRightClickListener = right;
        misHtml = isHtml;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_retire);

        TextView tvTitle = (TextView) findViewById(R.id.title);
        TextView tvMessage = (TextView) findViewById(R.id.message);
        Button mLeftButton = (Button) findViewById(R.id.left);
        Button mRightButton = (Button) findViewById(R.id.right);

        tvTitle.setText(mTitle);

        if (misHtml == true) {
            tvMessage.setGravity(Gravity.LEFT);
            tvMessage.setText(Html.fromHtml(mMessage));
        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder(mMessage);
            builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.purple)), 33, mMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvMessage.setText(builder);
        }

        mLeftButton.setOnClickListener(mLeftClickListener);
        mRightButton.setOnClickListener(mRightClickListener);
        mLeftButton.setText(mLeftText);
        mRightButton.setText(mRightText);
    }
}
