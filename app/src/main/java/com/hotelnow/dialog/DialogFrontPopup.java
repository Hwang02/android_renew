package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hotelnow.R;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * Created by susia on 16. 1. 10..
 */
public class DialogFrontPopup extends Dialog {

    private CheckBox mLeftButton;
    private Button mRightButton;
    private RoundedImageView popup_img;

    private String mImgUrl;
    private String mEvtId;
    private ImageView imgView;
    private RelativeLayout popup_bg;

    private View.OnClickListener mImgClickListener;
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    public DialogFrontPopup(String imgUrl, String evtId, Context context, View.OnClickListener banner, View.OnClickListener left , View.OnClickListener right) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.mImgUrl = imgUrl;
        this.mEvtId = evtId;
        this.mImgClickListener = banner;
        this.mLeftClickListener = left;
        this.mRightClickListener = right;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_frontpopup);

        ImageView popup_img = (RoundedImageView) findViewById(R.id.popup_img);
        mLeftButton = (CheckBox) findViewById(R.id.left);
        mRightButton = (Button) findViewById(R.id.right);
        popup_bg = (RelativeLayout) findViewById(R.id.popup_bg);

        Ion.with(popup_img).load(mImgUrl);

        popup_bg.setOnClickListener(mImgClickListener);
        mLeftButton.setOnClickListener(mLeftClickListener);
        mRightButton.setOnClickListener(mRightClickListener);

    }

}

