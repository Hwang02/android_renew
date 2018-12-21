package com.hotelnow.fragment.hotdeal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.HotDealActivity;
import com.hotelnow.adapter.HotdaelActivityAdapter;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.squareup.okhttp.Response;
import com.thebrownarrow.model.SearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HotDealActivityFragment extends Fragment {

    private SharedPreferences _preferences;
    private DbOpenHelper dbHelper;
    private ListView mlist;
    private HotdaelActivityAdapter adapter;
    private ArrayList<SearchResultItem> mActivityItem = new ArrayList<>();

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

        mlist = (ListView) getView().findViewById(R.id.h_list);
        adapter = new HotdaelActivityAdapter(getActivity(), 0, mActivityItem, HotDealActivityFragment.this, dbHelper);
        mlist.setAdapter(adapter);

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

        getView().findViewById(R.id.bt_scroll).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mlist.smoothScrollToPosition(0);
            }
        });

        getData();
    }

    private void getData(){
        Api.get(CONFIG.hotdeal_list + "/activity_hot_deals", new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception throwable) {
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isAdded()) {
                        if(obj.has("activity_hot_deals")){
                            JSONArray mActivity = new JSONArray(obj.getJSONObject("activity_hot_deals").getJSONArray("deals").toString());
                            mActivityItem.clear();
                            if(mActivity.length()>0) {
                                for (int i = 0; i < mActivity.length(); i++) {
                                    mActivityItem.add(new SearchResultItem(
                                            mActivity.getJSONObject(i).getString("id"),
                                            "",
                                            mActivity.getJSONObject(i).getString("name"),
                                            "",
                                            mActivity.getJSONObject(i).getString("category"),
                                            mActivity.getJSONObject(i).has("location") ? mActivity.getJSONObject(i).getString("location") : "",
                                            "",
                                            0,
                                            0,
                                            "N",
                                            mActivity.getJSONObject(i).getString("img_url"),
                                            mActivity.getJSONObject(i).getString("sale_price"),
                                           "",
                                            mActivity.getJSONObject(i).getString("sale_rate"),
                                            0,
                                            mActivity.getJSONObject(i).getString("benefit_text"),
                                            mActivity.getJSONObject(i).getString("review_score"),
                                            mActivity.getJSONObject(i).getString("grade_score"),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "N",
                                            mActivity.getJSONObject(i).getString("is_hot_deal"),
                                            mActivity.getJSONObject(i).getString("is_add_reserve"),
                                            mActivity.getJSONObject(i).getInt("coupon_count"),
                                            i == 0 ? true : false
                                    ));
                                }
                                getView().findViewById(R.id.bt_scroll).setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }
                            else{
                                getView().findViewById(R.id.bt_scroll).setVisibility(View.GONE);
                            }
                        }

                    }
                }
                catch (Exception e){
                    if(isAdded()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void setLike(final int position, final boolean islike){
        final String sel_id = mActivityItem.get(position).getId();
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
                            ((HotDealActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.deleteFavoriteItem(false,  sel_id,"A");
                        LogUtil.e("xxxx", "찜하기 취소");
                        ((HotDealActivity)getActivity()).showIconToast("관심 상품 담기 취소", false);
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
                            ((HotDealActivity)getActivity()).showToast("로그인 후 이용해주세요");
                            return;
                        }

                        dbHelper.insertFavoriteItem(sel_id,"A");
                        LogUtil.e("xxxx", "찜하기 성공");
                        ((HotDealActivity)getActivity()).showIconToast("관심 상품 담기 성공", true);
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
        if(requestCode == 50 && responseCode == 90) {
            getActivity().setResult(80);
            getActivity().finish();
        }
        else if(requestCode == 50 && responseCode == 80){
            adapter.notifyDataSetChanged();
        }
    }
}
