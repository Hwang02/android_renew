package com.hotelnow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.hotelnow.R;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by idhwang on 2017. 8. 18..
 */

public class ReviewHotelWriteActivity extends Activity implements View.OnClickListener{

    private ImageView sc_star1, sc_star2, sc_star3, sc_star4, sc_star5;
    private ImageView ko_star1, ko_star2, ko_star3, ko_star4, ko_star5;
    private ImageView c_star1, c_star2, c_star3, c_star4, c_star5;
    private ImageView sp_star1, sp_star2, sp_star3, sp_star4, sp_star5;
    private int sc_count = 5, c_count = 5, ko_count = 5, sp_count = 5;
    private Button right;
    private EditText review_edittext;
    private String bid, hotel_id, room_id, userid, h_name, r_name;
    private boolean review_sent = true;
    private TextView hotel_name, user_room_info, info, info1;
    private ScrollView scroll;
    private DialogAlert dialogConfirm;
//    private DialogSocial dialogSocial;
    private CallbackManager callbackManager;
//    private Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_write_review);


//        t = ((HotelnowApplication)getApplication()).getTracker(HotelnowApplication.TrackerName.APP_TRACKER);

        Intent intent = getIntent();

        bid = intent.getStringExtra("booking_id");
        hotel_id = intent.getStringExtra("hotel_id");
        room_id = intent.getStringExtra("room_id");
        userid = intent.getStringExtra("userid");
        h_name = intent.getStringExtra("hotel_name");
        r_name = intent.getStringExtra("room_name");


        hotel_name = (TextView) findViewById(R.id.hotel_name);
        user_room_info = (TextView) findViewById(R.id.user_room_info);
        hotel_name.setText(h_name);
        user_room_info.setText(r_name);

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
        review_edittext = (EditText) findViewById(R.id.review_edittext);
        info = (TextView)findViewById(R.id.info);
        info1 = (TextView)findViewById(R.id.info1);

        Spannable spannable = new SpannableString(info.getText());
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 30, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple)), 30, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        info.setText(spannable);

        spannable = new SpannableString(info1.getText());
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.termtext)), 63, 80, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        info1.setText(spannable);

        right = (Button) findViewById(R.id.right);
        right.setOnClickListener(this);

        sc_star1.setOnClickListener(this);
        sc_star2.setOnClickListener(this);
        sc_star3.setOnClickListener(this);
        sc_star4.setOnClickListener(this);
        sc_star5.setOnClickListener(this);

        ko_star1.setOnClickListener(this);
        ko_star2.setOnClickListener(this);
        ko_star3.setOnClickListener(this);
        ko_star4.setOnClickListener(this);
        ko_star5.setOnClickListener(this);

        c_star1.setOnClickListener(this);
        c_star2.setOnClickListener(this);
        c_star3.setOnClickListener(this);
        c_star4.setOnClickListener(this);
        c_star5.setOnClickListener(this);

        sp_star1.setOnClickListener(this);
        sp_star2.setOnClickListener(this);
        sp_star3.setOnClickListener(this);
        sp_star4.setOnClickListener(this);
        sp_star5.setOnClickListener(this);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setReview(){
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("hotel_id", hotel_id);
            paramObj.put("room_id", room_id);
            paramObj.put("user_id", userid);
            paramObj.put("comment", review_edittext.getText());
            paramObj.put("booking_id", bid);
            paramObj.put("rating_1", sc_count);
            paramObj.put("rating_2", c_count);
            paramObj.put("rating_3", ko_count);
            paramObj.put("rating_4", sp_count);
            paramObj.put("is_write", "Y");
        } catch (Exception e) {
        }
        review_sent = true;
        Api.post(CONFIG.reviewCreateUrl_v2, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                review_sent = false;
                Toast.makeText(ReviewHotelWriteActivity.this, getString(R.string.error_review_regist), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(ReviewHotelWriteActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    succReviewDialog();
                }catch (Exception e) {
                    Toast.makeText(ReviewHotelWriteActivity.this, getString(R.string.error_review_regist), Toast.LENGTH_SHORT).show();
                }
                review_sent = false;
            }
        });

    }

    private void succReviewDialog(){
        dialogConfirm = new DialogAlert(
                getString(R.string.alert_review_title),
                getString(R.string.alert_review_message),
                ReviewHotelWriteActivity.this,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirm.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                }
        );
        dialogConfirm.setCancelable(false);
        dialogConfirm.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right :{

                if (review_edittext.getText().length() == 0) {
                    Toast.makeText(ReviewHotelWriteActivity.this, getText(R.string.need_review_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(review_edittext.getText().length() < 10){
                    Toast.makeText(ReviewHotelWriteActivity.this, getText(R.string.need_review_msg2), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sc_count == 0 || c_count == 0 || ko_count == 0 || sp_count == 0){
                    Toast.makeText(ReviewHotelWriteActivity.this, getText(R.string.need_review_msg3), Toast.LENGTH_SHORT).show();
                    return;
                }

                setReview();
            }
            break;
            case R.id.sc_star1 :{
                sc_count = 1;
                setStar(sc_count, sc_star1, sc_star2, sc_star3, sc_star4, sc_star5);
            }
            break;
            case R.id.sc_star2 :{
                sc_count = 2;
                setStar(sc_count, sc_star1, sc_star2, sc_star3, sc_star4, sc_star5);
            }
            break;
            case R.id.sc_star3 :{
                sc_count = 3;
                setStar(sc_count, sc_star1, sc_star2, sc_star3, sc_star4, sc_star5);
            }
            break;
            case R.id.sc_star4 :{
                sc_count = 4;
                setStar(sc_count, sc_star1, sc_star2, sc_star3, sc_star4, sc_star5);
            }
            break;
            case R.id.sc_star5 :{
                sc_count = 5;
                setStar(sc_count, sc_star1, sc_star2, sc_star3, sc_star4, sc_star5);
            }
            break;
            case R.id.c_star1 :{
                c_count = 1;
                setStar(c_count, c_star1, c_star2, c_star3, c_star4, c_star5);
            }
            break;
            case R.id.c_star2 :{
                c_count = 2;
                setStar(c_count, c_star1, c_star2, c_star3, c_star4, c_star5);
            }
            break;
            case R.id.c_star3 :{
                c_count = 3;
                setStar(c_count, c_star1, c_star2, c_star3, c_star4, c_star5);
            }
            break;
            case R.id.c_star4 :{
                c_count = 4;
                setStar(c_count, c_star1, c_star2, c_star3, c_star4, c_star5);
            }
            break;
            case R.id.c_star5 :{
                c_count = 5;
                setStar(c_count, c_star1, c_star2, c_star3, c_star4, c_star5);
            }
            break;
            case R.id.ko_star1 :{
                ko_count = 1;
                setStar(ko_count, ko_star1, ko_star2, ko_star3, ko_star4, ko_star5);
            }
            break;
            case R.id.ko_star2 :{
                ko_count = 2;
                setStar(ko_count, ko_star1, ko_star2, ko_star3, ko_star4, ko_star5);
            }
            break;
            case R.id.ko_star3 :{
                ko_count = 3;
                setStar(ko_count, ko_star1, ko_star2, ko_star3, ko_star4, ko_star5);
            }
            break;
            case R.id.ko_star4 :{
                ko_count = 4;
                setStar(ko_count, ko_star1, ko_star2, ko_star3, ko_star4, ko_star5);
            }
            break;
            case R.id.ko_star5 :{
                ko_count = 5;
                setStar(ko_count, ko_star1, ko_star2, ko_star3, ko_star4, ko_star5);
            }
            break;
            case R.id.sp_star1 :{
                sp_count = 1;
                setStar(sp_count, sp_star1, sp_star2, sp_star3, sp_star4, sp_star5);
            }
            break;
            case R.id.sp_star2 :{
                sp_count = 2;
                setStar(sp_count, sp_star1, sp_star2, sp_star3, sp_star4, sp_star5);
            }
            break;
            case R.id.sp_star3 :{
                sp_count = 3;
                setStar(sp_count, sp_star1, sp_star2, sp_star3, sp_star4, sp_star5);
            }
            break;
            case R.id.sp_star4 :{
                sp_count = 4;
                setStar(sp_count, sp_star1, sp_star2, sp_star3, sp_star4, sp_star5);
            }
            break;
            case R.id.sp_star5 :{
                sp_count = 5;
                setStar(sp_count, sp_star1, sp_star2, sp_star3, sp_star4, sp_star5);
            }
            break;
        }
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

    @Override
    public void onBackPressed() {

        finish();

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
}
