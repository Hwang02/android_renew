package com.hotelnow.fragment.reservation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.ReservationHotelDetailActivity;
import com.hotelnow.adapter.FavoriteHotelAdapter;
import com.hotelnow.adapter.ReservationHotelAdapter;
import com.hotelnow.fragment.model.BookingEntry;
import com.hotelnow.fragment.model.FavoriteStayItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.EndlessScrollListener;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.NonScrollListView;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ReservationHotelFragment extends Fragment {

    private SharedPreferences _preferences;
    private NonScrollListView mlist;
    private ReservationHotelAdapter adapter;
    private Button btn_go_login, u_send;
    private ImageView back;
    private RelativeLayout main_view;
    private TextView btn_go_reservation;
    private EndlessScrollListener endlessScrollListener;
    private ArrayList<BookingEntry> mEntries = new ArrayList<BookingEntry>();
    private EditText u_name, u_tel, u_num;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation_h, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // preference
        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        endlessScrollListener = new EndlessScrollListener();
        mlist = (NonScrollListView) getView().findViewById(R.id.h_list);
        adapter = new ReservationHotelAdapter(getActivity(), 0, mEntries, _preferences.getString("userid", ""), ReservationHotelFragment.this);
        mlist.setAdapter(adapter);
        btn_go_login = (Button) getView().findViewById(R.id.btn_go_login);
        main_view = (RelativeLayout) getView().findViewById(R.id.main_view);
        btn_go_reservation = (TextView) getView().findViewById(R.id.btn_go_reservation);
        u_send = (Button) getView().findViewById(R.id.u_send);
        back = (ImageView) getView().findViewById(R.id.back);
        u_name = (EditText) getView().findViewById(R.id.u_name);
        u_tel = (EditText) getView().findViewById(R.id.u_tel);
        u_num = (EditText) getView().findViewById(R.id.u_num);

        mlist.setOnScrollListener(endlessScrollListener);

        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)view.findViewById(R.id.hid);
                Intent intent = new Intent(getActivity(), ReservationHotelDetailActivity.class);
                intent.putExtra("bid", tv.getText().toString());
                startActivityForResult(intent, 90);
            }
        });
        authCheck();
    }

    public void authCheck() {
        LogUtil.e("xxxxxxxx", "11001010101001");
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("ui", _preferences.getString("userid", null));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
        } catch(Exception e){ }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
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
                        if(!CONFIG.Mypage_Search) {
                            mlist.setEmptyView(getView().findViewById(R.id.login_view));
                            getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                            getView().findViewById(R.id.reserv_view).setVisibility(View.GONE);
                        }
                        else{
                            mlist.setEmptyView(getView().findViewById(R.id.reserv_view));
                            getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                            getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                        }
                        main_view.setBackgroundResource(R.color.white);
                        btn_go_login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivityForResult(intent, 80);
                            }
                        });
                        btn_go_reservation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                                getView().findViewById(R.id.reserv_view).setVisibility(View.VISIBLE);

                            }
                        });
                        u_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(u_name.getText().toString()) && !TextUtils.isEmpty(u_tel.getText().toString()) && !TextUtils.isEmpty(u_num.getText().toString())){
                                    Intent intent = new Intent(getActivity(), ReservationHotelDetailActivity.class);
                                    intent.putExtra("reservation", true);
                                    intent.putExtra("user_name", u_name.getText().toString());
                                    intent.putExtra("user_phone", u_tel.getText().toString());
                                    intent.putExtra("bid", u_num.getText().toString());
                                    intent.putExtra("title", "비회원 예약조회");
                                    startActivityForResult(intent, 80);
                                }
                                else {
                                    Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_booking_info), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mlist.setEmptyView(getView().findViewById(R.id.login_view));
                                getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                                getView().findViewById(R.id.reserv_view).setVisibility(View.GONE);
                            }
                        });

                    } else {
                        mlist.setEmptyView(getView().findViewById(R.id.empty_view));
                        getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                        getView().findViewById(R.id.reserv_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.footerview);
                        getBookingList();
                    }
                } catch (Exception e) {
                    if(isAdded())
                        Toast.makeText(HotelnowApplication.getAppContext(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getBookingList(){

        String url = CONFIG.bookingListUrl+"?page="+endlessScrollListener.getCurrentPage();

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

                    JSONArray feed = obj.getJSONArray("lists");
                    JSONObject entry;

                    for (int i = 0; i < feed.length(); i++) {
                        entry = feed.getJSONObject(i);
                        mEntries.add(new BookingEntry(
                                entry.getString("id"),
                                entry.getString("status"),
                                entry.getString("hotel_name"),
                                entry.getString("room_name"),
                                entry.getString("room_img"),
                                entry.getString("checkin_date"),
                                entry.getString("checkout_date"),
                                entry.getString("room_id"),
                                entry.getString("hotel_id"),
                                entry.getInt("myreview_cnt"),
                                entry.getString("is_review_writable"),
                                entry.getString("status_display"),
                                entry.getString("review_writable_words_1"),
                                entry.getString("review_writable_words_2"),
                                entry.getString("status_detail"))
                        );
                    }

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 80){

            authCheck();
            ((MainActivity)getActivity()).setTitle();
            ((MainActivity)getActivity()).setTapdelete("MYPAGE");
            CONFIG.TabLogin=true;
        }
        else if(requestCode == 90 && resultCode == 0)
        {
            mEntries.clear();
            endlessScrollListener.initialize();
            getBookingList();
        }
    }

    private class EndlessScrollListener implements AbsListView.OnScrollListener {
        // how many entries earlier to start loading next page
        private int visibleThreshold = 20;
        private int currentPage = 1;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                getBookingList();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView arg0, int arg1) {}

        public int getCurrentPage() {
            return currentPage;
        }

        public void initialize() {
            currentPage = 1;
            previousTotal = 0;
        }
    }
}
