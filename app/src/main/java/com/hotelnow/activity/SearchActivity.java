package com.hotelnow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelnow.R;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FlowLayout;
import com.hotelnow.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends Activity{
    private EditText et_search;
    private TextView tv_search_word, tv_popular_title, search_cancel;
    private LinearLayout lv_location, recent_clear, recent_list, ll_popular, hq_list;
    private List<KeyWordItem> mSearchList;
    private DbOpenHelper dbHelper;
    private View recent_clear_line;
    private FlowLayout popular_keyword;
    List<String> tmpCat = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        et_search = (EditText) findViewById(R.id.et_search);
        tv_search_word = (TextView) findViewById(R.id.tv_search_word);
        tv_popular_title = (TextView) findViewById(R.id.tv_popular_title);
        lv_location = (LinearLayout) findViewById(R.id.lv_location);
        recent_clear = (LinearLayout) findViewById(R.id.recent_clear);
        recent_list = (LinearLayout) findViewById(R.id.recent_list);
        recent_clear_line = (View) findViewById(R.id.recent_clear_line);
        ll_popular = (LinearLayout) findViewById(R.id.ll_popular);
        search_cancel = (TextView) findViewById(R.id.search_cancel);
        popular_keyword = (FlowLayout) findViewById(R.id.popular_keyword);
        hq_list = (LinearLayout) findViewById(R.id.hq_list);

        dbHelper = new DbOpenHelper(this);
        getRecentData();

        recent_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteKeyword("0",true);
                mSearchList.clear();
                getRecentData();
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    tv_search_word.setText("");
                    SpannableStringBuilder builder = new SpannableStringBuilder("'"+et_search.getText()+"'"+" 로 키워드 검색하기 〉");
                    builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple)), 1, et_search.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_search_word.append(builder);
                    tv_search_word.setVisibility(View.VISIBLE);
                    lv_location.setVisibility(View.GONE);
                    ll_popular.setVisibility(View.GONE);
                    tv_popular_title.setVisibility(View.GONE);
                }
                else if(s.length()==0){
                    tv_search_word.setVisibility(View.GONE);
                    lv_location.setVisibility(View.VISIBLE);
                    ll_popular.setVisibility(View.VISIBLE);
                    tv_popular_title.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (et_search.getRight() - et_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        et_search.setText("");
                        tv_search_word.setVisibility(View.GONE);
                        lv_location.setVisibility(View.VISIBLE);
                        ll_popular.setVisibility(View.VISIBLE);
                        tv_popular_title.setVisibility(View.VISIBLE);
                        LogUtil.e("xxxx", "xxxx삭제");
                        return true;
                    }
                }
                return false;
            }
        });

        tv_search_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.insertKeywordArea(et_search.getText().toString());
                mSearchList.clear();
                getRecentData();
            }
        });

        search_cancel.postDelayed(new Runnable() {
            @Override
            public void run() {
                search_cancel.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_slide_in_left);
                search_cancel.startAnimation(animation);
            }
        }, 400);

        search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityFinish();
            }
        });

        //인기 여행지 하드코딩 데이터
        tmpCat.add("인기여행지");
        tmpCat.add("여의도 불꽅놀이");
        tmpCat.add("경복궁 한복투어");
        tmpCat.add("전주 식도락여행");
        tmpCat.add("가족여행");
        tmpCat.add("포항해맞이");
        setCategory();
    }

    private void setCategory(){
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_pressed}, new int[]{-android.R.attr.state_pressed}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<tmpCat.size();i++){
            TextView tv = new TextView(this);
            tv.setId(i);
            tv.setTag(i);
            tv.setText(tmpCat.get(i));
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setTextColor(getResources().getColor(R.color.termtext));
            tv.setGravity(Gravity.LEFT);
            tv.setBackgroundResource(R.drawable.style_checkbox_keyword);
//            tv.setButtonDrawable(android.R.color.transparent);
            tv.setTextColor(myColorStateList);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // keyword 검색으로 검색 리스트로 이동
                    dbHelper.insertKeywordArea(tmpCat.get((int)v.getTag()));
                    mSearchList.clear();
                    getRecentData();
                }
            });
//            tv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
//                    // keyword 검색으로 검색 리스트로 이동
//                    if(isChecked == true){
//                        dbHelper.insertKeywordArea(tmpCat.get((int)v.getTag()));
//                        mSearchList.clear();
//                        getRecentData();
////                        LogUtil.e("xxxxx",v.getTag().toString());
//                    } else {
////                        LogUtil.e("xxxxx",v.getTag().toString());
//                    }
//                }
//            });

            popular_keyword.addView(tv);
        }
    }

    @Override
    public void onBackPressed() {
        activityFinish();
        super.onBackPressed();
    }

    public void activityFinish(){
        // 키보드 숨김
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);

        finish();
    }

    public void getRecentData(){
        mSearchList = dbHelper.selectAllKeyword();

        if(mSearchList.size() > 0) {
            recent_clear.setVisibility(View.VISIBLE);
            recent_clear_line.setVisibility(View.VISIBLE);
            recent_list.removeAllViews();
            for (int i = 0; i < mSearchList.size(); i++) {
                View view_recent = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_search_recent_item, null);
                ImageView item_del = (ImageView) view_recent.findViewById(R.id.item_del);
                TextView tv_recent_txt = (TextView) view_recent.findViewById(R.id.tv_recent_txt);
                TextView tv_recent_id = (TextView) view_recent.findViewById(R.id.tv_recent_id);

                tv_recent_txt.setText(mSearchList.get(i).getKeyword());
                tv_recent_id.setText(mSearchList.get(i).getKeyword_id()+"");
                tv_recent_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtil.e("xxxxx", v.getTag()+"");
//                        et_search.setText(mSearchList.get((int)v.getTag()).getKeyword());
//                        mSearchList.clear();
//                        getRecentData();
                    }
                });

                item_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper.deleteKeyword(mSearchList.get((int)v.getTag()).getKeyword_id()+"", false);
                        mSearchList.clear();
                        getRecentData();
                    }
                });
                item_del.setTag(i);
                tv_recent_txt.setTag(i);
                recent_list.addView(view_recent);
            }
        }
        else {
            // 없을때
            recent_clear.setVisibility(View.GONE);
            recent_clear_line.setVisibility(View.GONE);
            recent_list.removeAllViews();

            View view_recent = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_search_recent_no, null);
            TextView tv_recent_txt = (TextView) view_recent.findViewById(R.id.tv_recent_txt);
            tv_recent_txt.setText(getResources().getText(R.string.not_recent_search));

            recent_list.addView(view_recent);

            LogUtil.e("xxxxx", "없다");
        }
    }

    public void getHotelActivity(){
        hq_list.removeAllViews();
        View view_ha = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_hotel_activity_item, null);
        TextView item_star_txt = (TextView) findViewById(R.id.item_star_txt);
        TextView tv_popular_txt = (TextView) findViewById(R.id.tv_popular_txt);
        TextView tv_select_id = (TextView) findViewById(R.id.tv_select_id);
        ImageView ico_popular = (ImageView) findViewById(R.id.ico_popular);

        //ico_search_hotel, ico_search_activity 둘중하나로...

        hq_list.addView(view_ha);
    }
}
