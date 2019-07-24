package com.hotelnow.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.utils.Util;

/**
 * Created by susia on 15. 11. 26..
 */
public class DialogFullActivity extends Activity {
    private SharedPreferences _preferences;
    private String push_type = "";
    private String bid;
    private String hid;
    private String isevt;
    private int evtidx;
    private String evttag = "";
    private String sdate = null;
    private String edate = null;
    private String action = "";
    private String data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_full);
        // preference 할당
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intentLink = getIntent();
        push_type = intentLink.getStringExtra("push_type");
        bid = intentLink.getStringExtra("bid");
        hid = intentLink.getStringExtra("hid");
        isevt = intentLink.getStringExtra("isevt");
        evtidx = intentLink.getIntExtra("evtidx", 0);
        sdate = intentLink.getStringExtra("sdate");
        edate = intentLink.getStringExtra("edate");
        evttag = intentLink.getStringExtra("evttag");
        action = intentLink.getAction();
        data = intentLink.getDataString();

        TextView title = (TextView) findViewById(R.id.title);

        Spannable spannable = new SpannableString(title.getText().toString());
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);

        Button mOkButton = (Button) findViewById(R.id.ok);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.setPreferenceValues(_preferences, "user_first_app", false);
                Intent intent = new Intent(DialogFullActivity.this, MainActivity.class);
                intent.putExtra("action", action);
                intent.putExtra("data", data);
                intent.putExtra("push_type", push_type);
                intent.putExtra("bid", bid);
                intent.putExtra("hid", hid);
                intent.putExtra("isevt", isevt);
                intent.putExtra("evtidx", evtidx);
                intent.putExtra("evttag", evttag);
                intent.putExtra("sdate", sdate);
                intent.putExtra("edate", edate);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
