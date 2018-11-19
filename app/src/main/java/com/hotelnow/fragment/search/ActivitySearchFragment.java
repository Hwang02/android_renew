package com.hotelnow.fragment.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.BuildConfig;
import com.hotelnow.R;
import com.hotelnow.activity.AreaHotelActivity;
import com.hotelnow.activity.CalendarActivity;
import com.hotelnow.activity.FilterHotelActivity;
import com.hotelnow.activity.MapAcvitityActivity;
import com.hotelnow.activity.MapHotelActivity;
import com.hotelnow.activity.SearchResultActivity;
import com.hotelnow.adapter.SearchResultActivityAdapter;
import com.hotelnow.adapter.SearchResultStayAdapter;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ActivitySearchFragment  extends Fragment {

    private SharedPreferences _preferences;
    private ListView mlist;
    private View EmptyView;
    private View HeaderView;
    private ImageView map_img;
    private TextView tv_review_count;
    private RelativeLayout btn_location, btn_date;
    private ArrayList<SearchResultItem> mItems = new ArrayList<>();
    private SearchResultActivityAdapter adapter;
    private String banner_id, search_txt;
    private LinearLayout btn_filter;
    private int Page = 1;
    private int total_count;
    private String s_position = "";
    private DbOpenHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a_result, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        dbHelper = new DbOpenHelper(getActivity());

        search_txt = getArguments().getString("search_txt");
        banner_id = getArguments().getString("banner_id");

        mlist = (ListView) getView().findViewById(R.id.h_list);
        HeaderView = getLayoutInflater().inflate(R.layout.layout_search_map_filter_header, null, false);
        btn_location = (RelativeLayout) HeaderView.findViewById(R.id.btn_location);
        btn_date = (RelativeLayout)HeaderView.findViewById(R.id.btn_date);
        tv_review_count = (TextView) HeaderView.findViewById(R.id.tv_review_count);
        map_img = (ImageView) HeaderView.findViewById(R.id.map_img);
        btn_filter = (LinearLayout) getView().findViewById(R.id.btn_filter);

        mlist.addHeaderView(HeaderView);
        adapter = new SearchResultActivityAdapter(getActivity(), 0, mItems, ActivitySearchFragment.this, dbHelper);
        mlist.setAdapter(adapter);

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AreaHotelActivity.class);
                startActivity(intent);
            }
        });
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
            }
        });
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FilterHotelActivity.class);
                startActivity(intent);
            }
        });

        getSearch();
    }

    public void getSearch(){
        String url = CONFIG.search_activity_list;
//        search_txt = "서울";
        if(!TextUtils.isEmpty(search_txt)){
            url +="&search_text="+search_txt;
        }
        if(!TextUtils.isEmpty(banner_id)){
            url +="&banner_id="+banner_id;
        }

        url +="&per_page=20";

        if(Page < 2 || total_count != mItems.size()) {
            Api.get(url + "&page=" + Page, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final JSONArray list = obj.getJSONArray("lists");
                        JSONObject entry = null;

                        final String total_cnt = "총 " + obj.getString("total_count") + "개의 객실이 있습니다";
                        SpannableStringBuilder builder = new SpannableStringBuilder(total_cnt);
                        builder.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.purple)), 2, 2 + obj.getString("total_count").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_review_count.setText(builder);

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
                                    i == 0 ? true : false
                            ));
                            if (Page == 1)
                                s_position += "&markers=icon:http://hotelnow.s3.amazonaws.com/etc/20181114_150606_DfU0o2DCag.png%7C" + entry.getString("latitude") + "%2C" + entry.getString("longitude");
                        }

                        String mapStr = "https://maps.googleapis.com/maps/api/staticmap?" +
                                s_position +
                                "&scale=2&sensor=false&language=ko&size=360x130" + "&key=" + BuildConfig.google_map_key2;
                        Ion.with(map_img).load(mapStr);

                        map_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), MapAcvitityActivity.class);
                                intent.putExtra("search_data", mItems);
                                intent.putExtra("Page", Page);
                                intent.putExtra("total_count", total_count);
                                startActivityForResult(intent, 90);
                            }
                        });

                        total_count = obj.getInt("total_count");
                        adapter.notifyDataSetChanged();
                        Page++;

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void setLike(final int position, final boolean islike){
        final String sel_id = mItems.get(position).getId();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type", "activity");
            paramObj.put("id", sel_id);
        } catch(Exception e){
            Log.e(CONFIG.TAG, e.toString());
        }
        if(islike){// 취소
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
                            ((SearchResultActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false,  sel_id,"A");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((SearchResultActivity)getActivity()).showIconToast("관심 상품 담기 취소", false);
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
        else{// 성공
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
                            ((SearchResultActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id,"A");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((SearchResultActivity)getActivity()).showIconToast("관심 상품 담기 성공", true);
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if(responseCode == 90) {
            if(data.getBooleanExtra("search_data", false)) {
                mItems = (ArrayList<SearchResultItem>)data.getSerializableExtra("search_data");;
                if(adapter != null)
                    adapter.notifyDataSetChanged();
            }
        }
    }
}
