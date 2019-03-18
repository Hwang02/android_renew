package com.hotelnow.fragment.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.activity.ActivityFilterActivity;
import com.hotelnow.activity.AreaActivityActivity;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.MapAcvitityActivity;
import com.hotelnow.activity.SearchResultActivity;
import com.hotelnow.adapter.SearchResultActivityAdapter;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.FlowLayout;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivitySearchFragment extends Fragment {

    private SharedPreferences _preferences;
    private ListView mlist;
    private View EmptyView;
    private View HeaderView;
    private ImageView map_img;
    private TextView tv_review_count, tv_category, tv_location, tv_location2, tv_category2;
    private RelativeLayout btn_location, btn_category, btn_location2, btn_category2;
    private ArrayList<SearchResultItem> mItems = new ArrayList<>();
    private SearchResultActivityAdapter adapter;
    private String banner_id, search_txt, order_kind;
    private int Page = 1;
    private int total_count;
    private String s_position = "", theme_id = "", city = "", title_text;
    private DbOpenHelper dbHelper;
    private Button bt_scroll;
    private FlowLayout popular_keyword;
    private List<KeyWordItem> mKeywordList = new ArrayList<>();
    private boolean _hasLoadedOnce = false; // your boolean field
    private LinearLayout empty_image, layout_popular;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a_result, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void getSearch() {
        ((SearchResultActivity) getActivity()).showprogress();
        String url = CONFIG.search_activity_list;
//        search_txt = "서울";
        if (!TextUtils.isEmpty(search_txt)) {
            url += "&search_text=" + search_txt;
        }
        if (!TextUtils.isEmpty(banner_id)) {
            url += "&banner_id=" + banner_id;
        }
        if (!TextUtils.isEmpty(theme_id)) {
            url += "&category=" + theme_id;
        }
        if (!TextUtils.isEmpty(city)) {
            url += "&city=" + city;
        }
        if (!TextUtils.isEmpty(order_kind)) {
            url += "&order_kind=" + order_kind;
//            if(order_kind.equals("distance")){
            url += "&lat=" + CONFIG.lat + "&lng=" + CONFIG.lng;
//            }
        }
        if (!TextUtils.isEmpty(title_text) && title_text.equals("내 주변 바로보기")) {
            url += "&location_go=Y";
        }

        url += "&per_page=20";

        if (Page < 2 || total_count != mItems.size()) {
            Api.get(url + "&page=" + Page, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    if (isAdded()) {
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        ((SearchResultActivity) getActivity()).hideprogress();
                    }
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            ((SearchResultActivity) getActivity()).hideprogress();
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final JSONArray list = obj.getJSONArray("lists");
                        JSONObject entry = null;

                        final String total_cnt = "총 " + Util.numberFormat(obj.getInt("total_count")) + "개의 상품이 검색되었습니다";
                        SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
                        builder.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.purple)), 2, 2 + Util.numberFormat(obj.getInt("total_count")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 2, 2 + Util.numberFormat(obj.getInt("total_count")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_review_count.setText(builder);

                        final JSONArray popular_keywords = obj.getJSONArray("popular_keywords");
                        mKeywordList.clear();
                        for (int i = 0; i < popular_keywords.length(); i++) {
                            mKeywordList.add(new KeyWordItem(
                                    popular_keywords.getJSONObject(i).getString("id"),
                                    popular_keywords.getJSONObject(i).getString("order"),
                                    popular_keywords.getJSONObject(i).getString("category"),
                                    popular_keywords.getJSONObject(i).getString("image"),
                                    popular_keywords.getJSONObject(i).getString("keyword"),
                                    popular_keywords.getJSONObject(i).getString("type"),
                                    popular_keywords.getJSONObject(i).getString("evt_type"),
                                    popular_keywords.getJSONObject(i).getString("event_id"),
                                    popular_keywords.getJSONObject(i).has("link") ? popular_keywords.getJSONObject(i).getString("link") : "",
                                    popular_keywords.getJSONObject(i).getString("bannerable_id")
                            ));
                        }

                        setPopular();

                        for (int i = 0; i < list.length(); i++) {
                            entry = list.getJSONObject(i);
                            mItems.add(new SearchResultItem(
                                    entry.getString("id"),
                                    entry.getString("deal_id"),
                                    entry.getString("name"),
                                    "",
                                    entry.getString("category"),
                                    "",
                                    "",
                                    entry.getDouble("latitude"),
                                    entry.getDouble("longitude"),
                                    "N",
                                    entry.getString("landscape"),
                                    entry.getString("sale_price"),
                                    entry.getString("normal_price"),
                                    entry.getString("sale_rate"),
                                    0,
                                    entry.getString("benefit_text"),
                                    "0",
                                    entry.getString("grade_score"),
                                    entry.getString("real_grade_score"),
                                    "0",
                                    entry.getString("distance_real"),
                                    "0",
                                    entry.getString("location"),
                                    "N",
                                    entry.getString("is_hot_deal"),
                                    entry.getString("is_add_reserve"),
                                    entry.getInt("coupon_count"),
                                    i == 0 ? true : false,
                                    0
                            ));

                            if (Page == 1)
                                s_position += "&markers=icon:http://hotelnow.s3.amazonaws.com/etc/20181114_150606_DfU0o2DCag.png%7C" + entry.getString("latitude") + "%2C" + entry.getString("longitude");
                        }

                        if (mItems.size() > 0) {
                            bt_scroll.setVisibility(View.VISIBLE);
                            empty_image.setVisibility(View.GONE);
                        } else {
                            bt_scroll.setVisibility(View.GONE);
                            empty_image.setVisibility(View.VISIBLE);
                        }

                        String mapStr = "https://maps.googleapis.com/maps/api/staticmap?" +
                                s_position +
                                "&scale=2&sensor=false&language=ko&size=360x130" + "&key=" + BuildConfig.google_map_key2;
                        Ion.with(map_img).load(mapStr);

                        map_img.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                Intent intent = new Intent(getActivity(), MapAcvitityActivity.class);
                                intent.putExtra("search_data", mItems);
                                intent.putExtra("Page", Page);
                                intent.putExtra("total_count", total_count);
                                if (order_kind.equals("distance")) {
                                    intent.putExtra("lat", CONFIG.lat);
                                    intent.putExtra("lng", CONFIG.lng);
                                }
                                intent.putExtra("banner_id", banner_id);
                                intent.putExtra("theme_id", theme_id);
                                intent.putExtra("city", city);
                                intent.putExtra("title_text", title_text);
                                intent.putExtra("location", tv_location.getText().toString());
                                intent.putExtra("category", tv_category.getText().toString());
                                startActivityForResult(intent, 90);
                            }
                        });

                        total_count = obj.getInt("total_count");
                        adapter.notifyDataSetChanged();
                        Page++;

                        ((SearchResultActivity) getActivity()).hideprogress();
                    } catch (Exception e) {
                        if (isAdded()) {
                            Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                            ((SearchResultActivity) getActivity()).hideprogress();
                        }
                    }
                }
            });
        } else {
            ((SearchResultActivity) getActivity()).hideprogress();
        }
    }

    private void setPopular() {
        popular_keyword.removeAllViews();
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_pressed}, new int[]{-android.R.attr.state_pressed}},
                new int[]{getResources().getColor(R.color.purple), getResources().getColor(R.color.termtext)});

        if (mKeywordList.size() > 0) {
            layout_popular.setVisibility(View.VISIBLE);
            for (int i = 0; i < mKeywordList.size(); i++) {
                TextView tv = new TextView(getActivity());
                tv.setId(i);
                tv.setTag(i);
                tv.setText(mKeywordList.get(i).getKeyword());
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tv.setTextColor(getResources().getColor(R.color.termtext));
                tv.setGravity(Gravity.LEFT);
                tv.setBackgroundResource(R.drawable.style_checkbox_keyword);
//            tv.setButtonDrawable(android.R.color.transparent);
                tv.setTextColor(myColorStateList);
                tv.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                        intent.putExtra("banner_id", mKeywordList.get((int) v.getTag()).getId());
                        intent.putExtra("banner_name", mKeywordList.get((int) v.getTag()).getLink());
                        intent.putExtra("tab", 1);
                        startActivityForResult(intent, 80);
                        getActivity().finish();
                    }
                });

                popular_keyword.addView(tv);
            }
        } else {
            layout_popular.setVisibility(View.GONE);
        }
    }

    public void setClear() {
        Page = 1;
        total_count = 0;
        city = "";
        tv_location.setText("지역선택");
        search_txt = "";
        theme_id = "";
        tv_category.setText("테마선택");
        tv_category2.setText("테마선택");
        tv_location2.setText("지역선택");

        mItems.clear();
        getSearch();
    }

    public void setLike(final int position, final boolean islike) {
        final String sel_id = mItems.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch (Exception e) {
            Log.e(CONFIG.TAG, e.toString());
        }
        if (islike) {// 취소
            Api.post(CONFIG.like_unlike, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((SearchResultActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false, sel_id, "A");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((SearchResultActivity) getActivity()).showIconToast("관심 상품 담기 취소", false);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        } else {// 성공
            TuneWrap.Event("list_activity_favorite", sel_id);
            Api.post(CONFIG.like_like, paramObj.toString(), new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception throwable) {
                    Toast.makeText(getActivity(), getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);
                        if (!obj.has("result") || !obj.getString("result").equals("success")) {
                            ((SearchResultActivity) getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id, "A");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((SearchResultActivity) getActivity()).showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {

                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (responseCode == 90) {
            if (data.getBooleanExtra("search_data", false)) {
                mItems = (ArrayList<SearchResultItem>) data.getSerializableExtra("search_data");
                ;
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        } else if (requestCode == 80 && responseCode == 80) {
            city = data.getStringExtra("id");
            tv_location.setText(data.getStringExtra("name"));
            tv_location2.setText(data.getStringExtra("name"));
            Page = 1;
            total_count = 0;
            mItems.clear();
            adapter.notifyDataSetChanged();
            empty_image.setVisibility(View.GONE);
            layout_popular.setVisibility(View.GONE);
            getSearch();
        } else if (requestCode == 70 && responseCode == 80) {
            theme_id = data.getStringExtra("id");
            tv_category.setText(data.getStringExtra("name"));
            tv_category2.setText(data.getStringExtra("name"));
            Page = 1;
            total_count = 0;
            mItems.clear();
            adapter.notifyDataSetChanged();
            empty_image.setVisibility(View.GONE);
            layout_popular.setVisibility(View.GONE);
            getSearch();
        } else if (requestCode == 50 && responseCode == 80) {
            adapter.notifyDataSetChanged();
        } else if (requestCode == 90 && responseCode == 3000) {
            adapter.notifyDataSetChanged();
        } else if (requestCode == 90 && responseCode == 4000) {
            getActivity().finish();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);
        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_ && !_hasLoadedOnce) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TuneWrap.Event("search_list_activity");
                        init();
                    }
                }, 500);

                _hasLoadedOnce = true;
            }
        }
    }

    private void init() {
        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

        search_txt = getArguments().getString("search_txt");
        banner_id = getArguments().getString("banner_id");
        order_kind = getArguments().getString("order_kind");
        title_text = getArguments().getString("title_text");

        mlist = (ListView) getView().findViewById(R.id.h_list);
        HeaderView = getLayoutInflater().inflate(R.layout.layout_search_map_filter_header2, null, false);
        btn_location = (RelativeLayout) HeaderView.findViewById(R.id.btn_location);
        btn_category = (RelativeLayout) HeaderView.findViewById(R.id.btn_category);
        tv_review_count = (TextView) HeaderView.findViewById(R.id.tv_review_count);
        map_img = (ImageView) HeaderView.findViewById(R.id.map_img);
        tv_category = (TextView) HeaderView.findViewById(R.id.tv_category);
        tv_location = (TextView) HeaderView.findViewById(R.id.tv_location);
        bt_scroll = (Button) getView().findViewById(R.id.bt_scroll);

        View empty = getLayoutInflater().inflate(R.layout.layout_search_empty2, null, false);
        popular_keyword = (FlowLayout) empty.findViewById(R.id.filter1);

        tv_location2 = empty.findViewById(R.id.tv_location);
        tv_category2 = empty.findViewById(R.id.tv_category);
        btn_location2 = (RelativeLayout) empty.findViewById(R.id.btn_location);
        btn_category2 = (RelativeLayout) empty.findViewById(R.id.btn_category);
        empty_image = (LinearLayout) empty.findViewById(R.id.empty_image);
        layout_popular = (LinearLayout) empty.findViewById(R.id.popular_keyword);

        ((ViewGroup) mlist.getParent()).addView(empty);
        mlist.setEmptyView(empty);

        mlist.addHeaderView(HeaderView);
        adapter = new SearchResultActivityAdapter(getActivity(), 0, mItems, ActivitySearchFragment.this, dbHelper);
        mlist.setAdapter(adapter);
        empty_image.setVisibility(View.GONE);
        layout_popular.setVisibility(View.GONE);


        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView hid = (TextView) view.findViewById(R.id.hid);
                Intent intent = new Intent(getActivity(), DetailActivityActivity.class);
                intent.putExtra("tid", hid.getText().toString());
                intent.putExtra("save", true);
                startActivityForResult(intent, 50);
            }
        });

        btn_location2.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), AreaActivityActivity.class);
                startActivityForResult(intent, 80);
            }
        });
        btn_category2.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityFilterActivity.class);
                intent.putExtra("tv_category", tv_category.getText().toString());
                startActivityForResult(intent, 70);
            }
        });

        btn_location.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), AreaActivityActivity.class);
                startActivityForResult(intent, 80);
            }
        });
        btn_category.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityFilterActivity.class);
                intent.putExtra("tv_category", tv_category.getText().toString());
                startActivityForResult(intent, 70);
            }
        });
        bt_scroll.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mlist.setSelection(0);
            }
        });

        getSearch();
    }
}
