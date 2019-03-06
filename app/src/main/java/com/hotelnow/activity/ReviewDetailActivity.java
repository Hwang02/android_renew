package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.adapter.DetailReviewAdapter;
import com.hotelnow.fragment.model.ReviewItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ReviewDetailActivity extends Activity{

    int mPage = 1;
    final int mPer_page = 20;
    String hid;
    TextView tv_review_count, tv_title_hotel;
    ListView lv_list;
    Button bt_scroll;
    DetailReviewAdapter mListAdapter;
    private ArrayList<ReviewItem> reviewEntries = new ArrayList<ReviewItem>();
    private boolean isAdd = true;
    private boolean is_q = false;
    private TextView tv_checktitle;
    private View HeaderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_hotel);

        Intent intent = getIntent();

        HeaderView = getLayoutInflater().inflate(R.layout.reivew_header_view, null, false);
        tv_title_hotel = (TextView) findViewById(R.id.tv_title_hotel);
        bt_scroll = (Button) findViewById(R.id.bt_scroll);
        lv_list = (ListView) findViewById(R.id.lv_list);

        tv_review_count = (TextView) HeaderView.findViewById(R.id.tv_review_count);
        tv_checktitle = (TextView) HeaderView.findViewById(R.id.tv_checktitle);
        TextView tv_review_rate = (TextView) HeaderView.findViewById(R.id.tv_review_rate);
        TextView tv_review_rate_message = (TextView) HeaderView.findViewById(R.id.tv_review_rate_message);
        //서비스
        ImageView sc_star1 = (ImageView) HeaderView.findViewById(R.id.sc_star1);
        ImageView sc_star2 = (ImageView) HeaderView.findViewById(R.id.sc_star2);
        ImageView sc_star3 = (ImageView) HeaderView.findViewById(R.id.sc_star3);
        ImageView sc_star4 = (ImageView) HeaderView.findViewById(R.id.sc_star4);
        ImageView sc_star5 = (ImageView) HeaderView.findViewById(R.id.sc_star5);
        //교통
        ImageView ko_star1 = (ImageView) HeaderView.findViewById(R.id.ko_star1);
        ImageView ko_star2 = (ImageView) HeaderView.findViewById(R.id.ko_star2);
        ImageView ko_star3 = (ImageView) HeaderView.findViewById(R.id.ko_star3);
        ImageView ko_star4 = (ImageView) HeaderView.findViewById(R.id.ko_star4);
        ImageView ko_star5 = (ImageView) HeaderView.findViewById(R.id.ko_star5);
        //청결
        ImageView c_star1 = (ImageView) HeaderView.findViewById(R.id.c_star1);
        ImageView c_star2 = (ImageView) HeaderView.findViewById(R.id.c_star2);
        ImageView c_star3 = (ImageView) HeaderView.findViewById(R.id.c_star3);
        ImageView c_star4 = (ImageView) HeaderView.findViewById(R.id.c_star4);
        ImageView c_star5 = (ImageView) HeaderView.findViewById(R.id.c_star5);
        //시설
        ImageView sp_star1 = (ImageView) HeaderView.findViewById(R.id.sp_star1);
        ImageView sp_star2 = (ImageView) HeaderView.findViewById(R.id.sp_star2);
        ImageView sp_star3 = (ImageView) HeaderView.findViewById(R.id.sp_star3);
        ImageView sp_star4 = (ImageView) HeaderView.findViewById(R.id.sp_star4);
        ImageView sp_star5 = (ImageView) HeaderView.findViewById(R.id.sp_star5);

        lv_list.addHeaderView(HeaderView);

        Double mAvg = intent.getDoubleExtra("avg",0);
        if(intent.getDoubleExtra("avg",0)>4) {
            tv_review_rate_message.setText("최고에요!");
        }
        else if(intent.getDoubleExtra("avg",0)>=3.5) {
            tv_review_rate_message.setText("아주 좋아요");
        }
        else if(intent.getDoubleExtra("avg",0)>=3) {
            tv_review_rate_message.setText("좋아요");
        }
        else if(intent.getDoubleExtra("avg",0)>=2) {
            tv_review_rate_message.setText("보통입니다");
        }
        else {
            tv_review_rate_message.setText("별로에요");
        }

        tv_title_hotel.setText(intent.getStringExtra("title"));

        tv_review_rate.setText(mAvg+"");

        hid = intent.getStringExtra("hid");
        is_q = intent.getBooleanExtra("is_q", false);

        if(is_q){
            tv_checktitle.setText("안전성");
        }
        else {
            tv_checktitle.setText("청결도");
        }

        setStar(intent.getDoubleExtra("r1",0), sc_star1, sc_star2, sc_star3, sc_star4, sc_star5);
        setStar(intent.getDoubleExtra("r2",0), ko_star1, ko_star2, ko_star3, ko_star4, ko_star5);
        setStar(intent.getDoubleExtra("r3",0), c_star1, c_star2, c_star3, c_star4, c_star5);
        setStar(intent.getDoubleExtra("r4",0), sp_star1, sp_star2, sp_star3, sp_star4, sp_star5);

        bt_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lv_list.smoothScrollToPosition(0);
            }
        });

        mListAdapter = new DetailReviewAdapter( this, 0, reviewEntries, is_q);
        lv_list.setAdapter(mListAdapter);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

    public void getReviewList(){
        findViewById(R.id.wrapper).setVisibility(View.VISIBLE);
        String url;
        if(is_q){
            url = CONFIG.qreviewListUrl + "/" + mPage + "/" + hid;
            TuneWrap.Event("activity_review", hid);
        }
        else {
            url = CONFIG.reviewListUrl + "/" + mPage + "/" + mPer_page + "/" + hid;
            TuneWrap.Event("stay_review", hid);
        }
        if(isAdd) {
            Api.get(url, new Api.HttpCallback() {

                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(ReviewDetailActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.wrapper).setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {

                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getJSONObject("reviews").getString("result").equals("success")) {
                            Toast.makeText(ReviewDetailActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                            return;
                        }
                        if (mPage == 1) {
                            String total_cnt = "총 " + Util.numberFormat(obj.getJSONObject("info").getJSONObject("data").getInt("cnt")) + "개의 리뷰";
                            SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
                            builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple)), 2, 2 + obj.getJSONObject("info").getJSONObject("data").getString("cnt").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2 + obj.getJSONObject("info").getJSONObject("data").getString("cnt").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_review_count.append(builder);
                        }
                        if (obj.getJSONObject("reviews").has("data")) {
//                        String hotel_name, String masked_name, String total_rating, String view_yn, String comment, String owner_comment, String room_name, String stay_cnt, String created_at
                            JSONArray r_list = obj.getJSONObject("reviews").getJSONArray("data");
                            if (r_list.length() > 0) {
                                isAdd = true;
                                mPage++;
                                JSONObject entry = null;
                                for (int i = 0; i < r_list.length(); i++) {
                                    entry = r_list.getJSONObject(i);
                                    reviewEntries.add(new ReviewItem(
                                            entry.has("hotel_name") ? entry.getString("hotel_name") : "",
                                            entry.getString("masked_name"),
                                            entry.getString("total_rating"),
                                            entry.getString("view_yn"),
                                            entry.getString("comment"),
                                            entry.getString("owner_comment"),
                                            entry.has("room_name") ? entry.getString("room_name") : "",
                                            entry.has("stay_cnt") ? entry.getString("stay_cnt") : "",
                                            entry.getString("created_at"),
                                            entry.getString("updated_at")
                                    ));
                                }
                                mListAdapter.notifyDataSetChanged();
                            } else {
                                isAdd = false;
                            }
                            findViewById(R.id.wrapper).setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(ReviewDetailActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        findViewById(R.id.wrapper).setVisibility(View.GONE);
                    }
                }
            });
        }
        else{
            findViewById(R.id.wrapper).setVisibility(View.GONE);
        }
    }
}
