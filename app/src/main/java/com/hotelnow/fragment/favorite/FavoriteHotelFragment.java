package com.hotelnow.fragment.favorite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.adapter.FavoriteHotelAdapter;
import com.hotelnow.fragment.model.FavoriteStayItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.NonScrollListView;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;

public class FavoriteHotelFragment extends Fragment {

    private SharedPreferences _preferences;
    private NonScrollListView mlist;
    private View EmptyView;
    private View HeaderView;
    private ImageView map_img;
    private TextView tv_review_count;
    private RelativeLayout btn_location, btn_date;
    private ArrayList<FavoriteStayItem> mItems = new ArrayList<>();
    private String banner_id, search_txt;
    private LinearLayout btn_filter;
    private FavoriteHotelAdapter adapter;
    private View empty;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_h, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        search_txt = getArguments().getString("search_txt");
        banner_id = getArguments().getString("banner_id");

        mlist = (NonScrollListView) getView().findViewById(R.id.h_list);
        adapter = new FavoriteHotelAdapter(getActivity(), 0, mItems);
        mlist.setAdapter(adapter);

        mlist.setEmptyView(getView().findViewById(R.id.empty_view));

        authCheck();
    }

    public void authCheck() {
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", _preferences.getString("userid", null));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (obj.getString("result").equals("0")) {
                        SharedPreferences.Editor prefEditor = _preferences.edit();
                        prefEditor.putString("email", null);
                        prefEditor.putString("username", null);
                        prefEditor.putString("phone", null);
                        prefEditor.putString("userid", null);
                        prefEditor.commit();

                    } else {
                        getSearch();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getSearch(){
        String url = CONFIG.like_list+"?only_id=N&type=stay";

        Api.get(url, new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Map<String, String> headers, String body) {
                try {
                    JSONObject obj = new JSONObject(body);

                    if (!obj.getString("result").equals("success")) {
                        Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(obj.getJSONArray("stay").length() >0) {
                        final JSONArray list = obj.getJSONArray("stay");
                        JSONObject entry = null;
                        for (int i = 0; i < list.length(); i++) {
                            entry = list.getJSONObject(i);
                            mItems.add(new FavoriteStayItem(
                                    entry.getString("id"),
                                    entry.getString("name"),
                                    entry.getString("category"),
                                    entry.getString("street1"),
                                    entry.getString("street2"),
                                    entry.getString("landscape"),
                                    entry.getString("sale_price"),
                                    entry.getString("sale_rate"),
                                    entry.getInt("items_quantity"),
                                    entry.getString("special_msg"),
                                    entry.getString("grade_score"),
                                    entry.getString("real_grade_score"),
                                    entry.getString("is_private_deal"),
                                    entry.getString("is_hot_deal"),
                                    entry.getString("is_add_reserve")
                            ));
                        }
                    }
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
