package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ReviewHotelActivity extends Activity{

    int mPage = 0;
    final int mPer_page = 20;
    String hid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_hotel);

        Intent intent = getIntent();

        TextView tv_review_rate = (TextView) findViewById(R.id.tv_review_rate);
        TextView tv_review_rate_message = (TextView) findViewById(R.id.tv_review_rate_message);
        //서비스
        ImageView sc_star1 = (ImageView) findViewById(R.id.sc_star1);
        ImageView sc_star2 = (ImageView) findViewById(R.id.sc_star2);
        ImageView sc_star3 = (ImageView) findViewById(R.id.sc_star3);
        ImageView sc_star4 = (ImageView) findViewById(R.id.sc_star4);
        ImageView sc_star5 = (ImageView) findViewById(R.id.sc_star5);
        //교통
        ImageView ko_star1 = (ImageView) findViewById(R.id.ko_star1);
        ImageView ko_star2 = (ImageView) findViewById(R.id.ko_star2);
        ImageView ko_star3 = (ImageView) findViewById(R.id.ko_star3);
        ImageView ko_star4 = (ImageView) findViewById(R.id.ko_star4);
        ImageView ko_star5 = (ImageView) findViewById(R.id.ko_star5);
        //청결
        ImageView c_star1 = (ImageView) findViewById(R.id.c_star1);
        ImageView c_star2 = (ImageView) findViewById(R.id.c_star2);
        ImageView c_star3 = (ImageView) findViewById(R.id.c_star3);
        ImageView c_star4 = (ImageView) findViewById(R.id.c_star4);
        ImageView c_star5 = (ImageView) findViewById(R.id.c_star5);
        //시설
        ImageView sp_star1 = (ImageView) findViewById(R.id.sp_star1);
        ImageView sp_star2 = (ImageView) findViewById(R.id.sp_star2);
        ImageView sp_star3 = (ImageView) findViewById(R.id.sp_star3);
        ImageView sp_star4 = (ImageView) findViewById(R.id.sp_star4);
        ImageView sp_star5 = (ImageView) findViewById(R.id.sp_star5);

        Double mAvg = intent.getDoubleExtra("avg",0);
        if(intent.getDoubleExtra("avg",0)>4) {
            tv_review_rate_message.setText("최고에요! 강추!");
        }
        else if(intent.getDoubleExtra("avg",0)>=3.5) {
            tv_review_rate_message.setText("좋았어요! 추천해요!");
        }
        else if(intent.getDoubleExtra("avg",0)>=3) {
            tv_review_rate_message.setText("좋았어요!");
        }
        else if(intent.getDoubleExtra("avg",0)>=2) {
            tv_review_rate_message.setText("보통이에요.");
        }
        else {
            tv_review_rate_message.setText("그럭저럭");
        }

        tv_review_rate.setText(mAvg+"");

        hid = intent.getStringExtra("hid");

        setStar(intent.getDoubleExtra("r1",0), sc_star1, sc_star2, sc_star3, sc_star4, sc_star5);
        setStar(intent.getDoubleExtra("r2",0), ko_star1, ko_star2, ko_star3, ko_star4, ko_star5);
        setStar(intent.getDoubleExtra("r3",0), c_star1, c_star2, c_star3, c_star4, c_star5);
        setStar(intent.getDoubleExtra("r4",0), sp_star1, sp_star2, sp_star3, sp_star4, sp_star5);
        getReviewList();
    }

    private void setStar(double mScore, ImageView imgStar1, ImageView imgStar2, ImageView imgStar3, ImageView imgStar4, ImageView imgStar5){
        if (mScore < 1){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_half);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if (mScore == 1){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if (mScore < 2){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_half);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if (mScore == 2){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore < 3){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_half);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore == 3){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_blank);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore < 4){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_half);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore == 4){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_blank);
        }
        else if(mScore < 5){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_half);
        }
        else if(mScore == 5){
            imgStar1.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar2.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar3.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar4.setBackgroundResource(R.drawable.ico_starpoint_press);
            imgStar5.setBackgroundResource(R.drawable.ico_starpoint_press);
        }
    }

    private void getReviewList(){
        String url = CONFIG.reviewListUrl + "/" + mPage++ + "/" + mPer_page + "/" + hid;
        Api.get(url, new Api.HttpCallback() {

            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(ReviewHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {

                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getJSONObject("reviews").getString("result").equals("success")) {
                        Toast.makeText(ReviewHotelActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (obj.getJSONObject("reviews").has("data")) {
                        JSONArray r_list = obj.getJSONObject("reviews").getJSONArray("data");


                    }

                } catch (JSONException e) {
                    Toast.makeText(ReviewHotelActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
