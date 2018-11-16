package com.hotelnow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.fragment.model.SearchAutoitem;
import com.hotelnow.fragment.model.SearchKeyWordItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FlowLayout;
import com.hotelnow.utils.LogUtil;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SearchActivity extends Activity{
    private EditText et_search;
    private TextView tv_search_word, tv_popular_title, search_cancel;
    private LinearLayout lv_location, recent_clear, recent_list, ll_popular, hq_list, hotel_list, activity_list;
    private List<SearchKeyWordItem> mSearchList;
    private DbOpenHelper dbHelper;
    private View recent_clear_line, underline3;
    private FlowLayout popular_keyword;
    private List<KeyWordItem> mKeywordList = new ArrayList<>();
    private List<KeyWordItem> mHotelActivity = new ArrayList<>();
    private List<SearchAutoitem> mHotelAuto = new ArrayList<>();
    private List<SearchAutoitem> mActivityAuto = new ArrayList<>();

    private RelativeLayout ll_before, ll_result;

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
        //결과 화면
        ll_before = (RelativeLayout) findViewById(R.id.ll_before);
        ll_result = (RelativeLayout) findViewById(R.id.ll_result);
        hotel_list = (LinearLayout) findViewById(R.id.hotel_list);
        activity_list = (LinearLayout) findViewById(R.id.activity_list);

        //결과 화면
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

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
              @Override
              public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                  if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                      dbHelper.insertKeyword(et_search.getText().toString(), "x");
                      mSearchList.clear();
                      getRecentData();

                      // 리스트 화면 이동
                      Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                      intent.putExtra("search", et_search.getText().toString());
                      startActivity(intent);
                  }
                  return false;
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
                    ll_result.setVisibility(View.VISIBLE);
                    lv_location.setVisibility(View.GONE);
                    ll_popular.setVisibility(View.GONE);
                    tv_popular_title.setVisibility(View.GONE);
                    ll_before.setVisibility(View.GONE);

                    setResultData();

                }
                else if(s.length()==0){
                    tv_search_word.setVisibility(View.GONE);
                    ll_result.setVisibility(View.GONE);
                    lv_location.setVisibility(View.VISIBLE);
                    ll_popular.setVisibility(View.VISIBLE);
                    tv_popular_title.setVisibility(View.VISIBLE);
                    ll_before.setVisibility(View.VISIBLE);
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
                dbHelper.insertKeyword(et_search.getText().toString(),"x");
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
        setData();
//        setCategory();
    }

    private void setResultData(){
        String url = CONFIG.search_auto+et_search.getText().toString();
        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(SearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);
                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(SearchActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final JSONObject search_results = obj.getJSONObject("search_results");
                    final JSONArray hotel = search_results.getJSONArray("hotel");
                    final JSONArray activity = search_results.getJSONArray("activity");

                    mHotelAuto.clear();
                    mActivityAuto.clear();

                    //호텔 리스트
                    for(int i=0; i<hotel.length(); i++){
                        mHotelAuto.add(new SearchAutoitem(
                                hotel.getJSONObject(i).getString("flag"),
                                hotel.getJSONObject(i).getString("id"),
                                hotel.getJSONObject(i).getString("name"),
                                hotel.getJSONObject(i).getString("city"),
                                hotel.getJSONObject(i).getString("sub_city")
                        ));
                    }

                    //액티비티 리스트
                    for(int i=0; i<activity.length(); i++){
                        mActivityAuto.add(new SearchAutoitem(
                                activity.getJSONObject(i).getString("flag"),
                                activity.getJSONObject(i).getString("id"),
                                activity.getJSONObject(i).getString("name"),
                                activity.getJSONObject(i).getString("city"),
                                activity.getJSONObject(i).getString("sub_city")
                        ));
                    }

                    setAutoH();
                    setAutoA();

                } catch (Exception e) {
                    Toast.makeText(SearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setAutoH(){
        hotel_list.removeAllViews();
        for(int i =0; i<mHotelAuto.size(); i++) {
            View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_search_auto_item, null);
            final TextView tv_recent_txt = (TextView) view.findViewById(R.id.tv_recent_txt);
            ImageView ico_item = (ImageView) view.findViewById(R.id.ico_item);

            if(mHotelAuto.get(i).getFlag().equals("region_hotel")){
                ico_item.setBackgroundResource(R.drawable.ico_search_location);
            }
            else{
                ico_item.setBackgroundResource(R.drawable.ico_search_hotel);
            }

            tv_recent_txt.setTag(i);
            tv_recent_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("tab", 0);
                    intent.putExtra("search", tv_recent_txt.getText().toString());
                    startActivity(intent);
                }
            });

            tv_recent_txt.setText(mHotelAuto.get(i).getName());
            hotel_list.addView(view);
        }
    }

    private void setAutoA(){
        activity_list.removeAllViews();
        for(int i =0; i<mActivityAuto.size(); i++) {
            View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_search_auto_item, null);
            final TextView tv_recent_txt = (TextView) view.findViewById(R.id.tv_recent_txt);
            ImageView ico_item = (ImageView) view.findViewById(R.id.ico_item);

            if(mActivityAuto.get(i).getFlag().equals("region_activity")){
                ico_item.setBackgroundResource(R.drawable.ico_search_location);
            }
            else{
                ico_item.setBackgroundResource(R.drawable.ico_search_activity);
            }

            tv_recent_txt.setTag(i);
            tv_recent_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("tab", 1);
                    intent.putExtra("search", tv_recent_txt.getText().toString());
                    startActivity(intent);
                }
            });

            tv_recent_txt.setText(mActivityAuto.get(i).getName());
            activity_list.addView(view);
        }
    }

    private void setData(){
        Api.get(CONFIG.search_before, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(SearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(SearchActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final JSONArray popular_keywords = obj.getJSONArray("popular_keywords");
                    final JSONArray popular_products = obj.getJSONArray("popular_products");

                    for(int i = 0; i < popular_keywords.length(); i++){
                        mKeywordList.add(new KeyWordItem(
                                popular_keywords.getJSONObject(i).getString("id"),
                                popular_keywords.getJSONObject(i).getString("order"),
                                popular_keywords.getJSONObject(i).getString("category"),
                                popular_keywords.getJSONObject(i).getString("image"),
                                popular_keywords.getJSONObject(i).getString("keyword"),
                                popular_keywords.getJSONObject(i).getString("type"),
                                popular_keywords.getJSONObject(i).getString("evt_type"),
                                popular_keywords.getJSONObject(i).getString("event_id"),
                                popular_keywords.getJSONObject(i).has("link") ? popular_keywords.getJSONObject(i).getString("link") : ""
                        ));
                    }

                    for(int j = 0; j < popular_products.length(); j++){
                        mHotelActivity.add(new KeyWordItem(
                                popular_products.getJSONObject(j).getString("id"),
                                popular_products.getJSONObject(j).getString("order"),
                                popular_products.getJSONObject(j).getString("category"),
                                popular_products.getJSONObject(j).getString("image"),
                                popular_products.getJSONObject(j).getString("keyword"),
                                popular_products.getJSONObject(j).getString("type"),
                                popular_products.getJSONObject(j).getString("evt_type"),
                                popular_products.getJSONObject(j).getString("event_id"),
                                popular_products.getJSONObject(j).has("link") ? popular_products.getJSONObject(j).getString("link") : ""
                        ));
                    }
                    // 키워드
                    setCategory();
                    // 추천
                    getHotelActivity();
                } catch (Exception e) {
                    e.getStackTrace();
                    Toast.makeText(SearchActivity.this, getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setCategory(){
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_pressed}, new int[]{-android.R.attr.state_pressed}},
                new int[] { getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext) } );

        for(int i=0;i<mKeywordList.size();i++){
            TextView tv = new TextView(this);
            tv.setId(i);
            tv.setTag(i);
            tv.setText(mKeywordList.get(i).getLink());
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
                    dbHelper.insertKeyword(mKeywordList.get((int)v.getTag()).getKeyword(), mKeywordList.get((int)v.getTag()).getId());
                    mSearchList.clear();
                    getRecentData();

                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("banner_id", mKeywordList.get((int)v.getTag()).getId());
                    startActivity(intent);

                }
            });

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
        for (int i=0; i<mHotelActivity.size(); i++) {
            View view_ha = LayoutInflater.from(SearchActivity.this).inflate(R.layout.layout_hotel_activity_item, null);
            TextView item_star_txt = (TextView) view_ha.findViewById(R.id.item_star_txt);
            TextView tv_popular_txt = (TextView) view_ha.findViewById(R.id.tv_popular_txt);
            TextView tv_select_id = (TextView) view_ha.findViewById(R.id.tv_select_id);
            ImageView ico_popular = (ImageView) view_ha.findViewById(R.id.ico_popular);

            //ico_search_hotel, ico_search_activity 둘중하나로...

            if(mHotelActivity.get(i).getCategory().equals("popular_product_stay")){
                ico_popular.setBackgroundResource(R.drawable.ico_search_hotel);
            }
            else{
                ico_popular.setBackgroundResource(R.drawable.ico_search_activity);
            }

            item_star_txt.setText("");
            tv_popular_txt.setText(mHotelActivity.get(i).getKeyword());
            hq_list.addView(view_ha);
        }
    }
}
