package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hotelnow.R;

/**
 * Created by susia on 15. 12. 10..
 */
public class DialogRecommend extends Dialog {
    private EditText codeInput;
    private TextView codeResult;
    private Button mLeftButton;
    private Button mRightButton;
    private SharedPreferences _preferences;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    public DialogRecommend(Context context, View.OnClickListener left, View.OnClickListener right) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.mLeftClickListener = left;
        this.mRightClickListener = right;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_recommend);

        codeInput = (EditText) findViewById(R.id.recommend_code);
        codeResult = (TextView) findViewById(R.id.recommend_result);
        mLeftButton = (Button) findViewById(R.id.left);
        mRightButton = (Button) findViewById(R.id.right);

        mLeftButton.setOnClickListener(mLeftClickListener);
        mRightButton.setOnClickListener(mRightClickListener);
    }
}
