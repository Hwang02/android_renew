package com.hotelnow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hotelnow.R;

/**
 * Created by idhwang on 2018. 9. 4..
 */

public class PushActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

    }
}
