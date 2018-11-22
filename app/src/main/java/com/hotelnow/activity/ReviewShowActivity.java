package com.hotelnow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelnow.R;

public class ReviewShowActivity extends Activity{

    private ImageView sc_star1, sc_star2, sc_star3, sc_star4, sc_star5;
    private ImageView ko_star1, ko_star2, ko_star3, ko_star4, ko_star5;
    private ImageView c_star1, c_star2, c_star3, c_star4, c_star5;
    private ImageView sp_star1, sp_star2, sp_star3, sp_star4, sp_star5;
    private int sc_count = 5, c_count = 5, ko_count = 5, sp_count = 5;
    private TextView review_edittext;
    private TextView hotel_name, user_room_info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_show);

        hotel_name = (TextView) findViewById(R.id.hotel_name);
        user_room_info = (TextView) findViewById(R.id.user_room_info);
//        hotel_name.setText(h_name);
//        user_room_info.setText(r_name);

        //서비스
        sc_star1 = (ImageView) findViewById(R.id.sc_star1);
        sc_star2 = (ImageView) findViewById(R.id.sc_star2);
        sc_star3 = (ImageView) findViewById(R.id.sc_star3);
        sc_star4 = (ImageView) findViewById(R.id.sc_star4);
        sc_star5 = (ImageView) findViewById(R.id.sc_star5);
        //교통
        ko_star1 = (ImageView) findViewById(R.id.ko_star1);
        ko_star2 = (ImageView) findViewById(R.id.ko_star2);
        ko_star3 = (ImageView) findViewById(R.id.ko_star3);
        ko_star4 = (ImageView) findViewById(R.id.ko_star4);
        ko_star5 = (ImageView) findViewById(R.id.ko_star5);
        //청결
        c_star1 = (ImageView) findViewById(R.id.c_star1);
        c_star2 = (ImageView) findViewById(R.id.c_star2);
        c_star3 = (ImageView) findViewById(R.id.c_star3);
        c_star4 = (ImageView) findViewById(R.id.c_star4);
        c_star5 = (ImageView) findViewById(R.id.c_star5);
        //시설
        sp_star1 = (ImageView) findViewById(R.id.sp_star1);
        sp_star2 = (ImageView) findViewById(R.id.sp_star2);
        sp_star3 = (ImageView) findViewById(R.id.sp_star3);
        sp_star4 = (ImageView) findViewById(R.id.sp_star4);
        sp_star5 = (ImageView) findViewById(R.id.sp_star5);
        review_edittext = (TextView) findViewById(R.id.review_edittext);

    }
}
