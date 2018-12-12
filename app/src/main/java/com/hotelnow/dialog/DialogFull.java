package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hotelnow.R;

/**
 * Created by susia on 15. 11. 26..
 */
public class DialogFull extends Dialog {
    private View.OnClickListener mOkClickListener;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_full);
        TextView title = (TextView) findViewById(R.id.title);

        Spannable spannable = new SpannableString(title.getText().toString());
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);

        Button mOkButton = (Button) findViewById(R.id.ok);
        mOkButton.setOnClickListener(mOkClickListener);

    }

    public DialogFull(Context context, View.OnClickListener ok) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
        this.mOkClickListener = ok;
    }
}
