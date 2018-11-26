package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.Map;

public class ReviewShowActivity extends Activity{

    private ImageView sc_star1, sc_star2, sc_star3, sc_star4, sc_star5;
    private ImageView ko_star1, ko_star2, ko_star3, ko_star4, ko_star5;
    private ImageView c_star1, c_star2, c_star3, c_star4, c_star5;
    private ImageView sp_star1, sp_star2, sp_star3, sp_star4, sp_star5;
    private int sc_count = 5, c_count = 5, ko_count = 5, sp_count = 5;
    private TextView review_edittext, title_ch;
    private TextView hotel_name, user_room_info;
    private String bid, page;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_show);

        Intent intent = getIntent();

        bid = intent.getStringExtra("booking_id");
        page = intent.getStringExtra("page");

        hotel_name = (TextView) findViewById(R.id.hotel_name);
        user_room_info = (TextView) findViewById(R.id.user_room_info);
        title_ch = (TextView) findViewById(R.id.title_ch);
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
        hotel_name = (TextView) findViewById(R.id.hotel_name);
        user_room_info = (TextView) findViewById(R.id.user_room_info);


        if(page.equals("stay")){
            title_ch.setText("ㆍ 청결도");
        }
        else {
            title_ch.setText("ㆍ 안전성");
        }

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData();

    }

    private void getData(){
        String url = CONFIG.review_show;
        if(page.equals("stay")) {
            url += "/stay/" + bid;
        }
        else {
            url += "/activity/" + bid;
        }
        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(ReviewShowActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReviewShowActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject item = obj.getJSONObject("review");
                    hotel_name.setText(item.getString("hotel_name"));
                    user_room_info.setText(item.getString("room_name"));
                    sc_count = item.getInt("rating_1");
                    c_count = item.getInt("rating_2");
                    ko_count = item.getInt("rating_3");
                    sp_count = item.getInt("rating_4");
                    review_edittext.setText(item.getString("comment"));


                    setStar(sc_count, sc_star1, sc_star2, sc_star3, sc_star4, sc_star5);
                    setStar(c_count, c_star1, c_star2, c_star3, c_star4, c_star5);
                    setStar(ko_count, ko_star1, ko_star2, ko_star3, ko_star4, ko_star5);
                    setStar(sp_count, sp_star1, sp_star2, sp_star3, sp_star4, sp_star5);
                 }
                 catch (Exception e) {
                    Toast.makeText(ReviewShowActivity.this, getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setStar(double mScore, ImageView imgStar1, ImageView imgStar2, ImageView imgStar3, ImageView imgStar4, ImageView imgStar5){
        if (mScore == 1){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
        }
        else if (mScore == 2){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
        }
        else if(mScore == 3){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
        }
        else if(mScore == 4){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_20_blank);
        }
        else if(mScore == 5){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_20);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_20);
        }
    }
}
