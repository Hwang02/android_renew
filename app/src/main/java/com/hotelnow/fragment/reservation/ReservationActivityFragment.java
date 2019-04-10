
package com.hotelnow.fragment.reservation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.LoginActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.ReservationActivityDetailActivity;
import com.hotelnow.adapter.ReservationActivityAdapter;
import com.hotelnow.fragment.model.BookingQEntry;
import com.hotelnow.utils.AES256Chiper;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.NonScrollListView;
import com.hotelnow.utils.OnSingleClickListener;
import com.hotelnow.utils.OnSingleItemClickListener;
import com.hotelnow.utils.TuneWrap;
import com.hotelnow.utils.Util;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ReservationActivityFragment extends Fragment {

    //    private EndlessScrollListener endlessScrollListener;
    private ArrayList<BookingQEntry> mEntries = new ArrayList<BookingQEntry>();
    private SharedPreferences _preferences;
    private NonScrollListView mlist;
    private ReservationActivityAdapter adapter;
    private Button btn_go_login, u_send;
    private RelativeLayout main_view;
    private TextView btn_go_reservation;
    private ImageView back;
    private EditText u_name, u_tel, u_num;
    private int currentPage = 1;
    private int total_count = 0;
    private boolean isAdd = true;
    private boolean _hasLoadedOnce = false; // your boolean field
    boolean firstDragFlag = true;
    boolean dragFlag = false;   //현재 터치가 드래그 인지 확인
    float startYPosition = 0, endYPosition = 0;       //터치이벤트의 시작점의 Y(세로)위치
    private NestedScrollView scroll;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation_a, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        init();
    }

    public void authCheck() {
        MainActivity.showProgress();
        JSONObject paramObj = new JSONObject();
        try {
//            paramObj.put("ui", AES256Chiper.AES_Decode(_preferences.getString("userid", null).replace("HN|", "")));
            paramObj.put("umi", _preferences.getString("moreinfo", null));
            paramObj.put("ver", Util.getAppVersionName(getActivity()));
        } catch (Exception e) {
        }

        Api.post(CONFIG.authcheckUrl, paramObj.toString(), new Api.HttpCallback() {
            @Override
            public void onFailure(Response response, Exception e) {
                MainActivity.hideProgress();
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

                        mlist.setEmptyView(getView().findViewById(R.id.login_view));
                        getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.white);
                        btn_go_login.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivityForResult(intent, 80);
                            }
                        });
                        btn_go_reservation.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                                getView().findViewById(R.id.reserv_view).setVisibility(View.VISIBLE);

                            }
                        });
                        u_send.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                if (!TextUtils.isEmpty(u_name.getText().toString()) && !TextUtils.isEmpty(u_tel.getText().toString()) && !TextUtils.isEmpty(u_num.getText().toString())) {
                                    Intent intent = new Intent(getActivity(), ReservationActivityDetailActivity.class);
                                    intent.putExtra("user_name", u_name.getText().toString());
                                    intent.putExtra("user_phone", u_tel.getText().toString());
                                    intent.putExtra("tid", u_num.getText().toString());
                                    intent.putExtra("title", "비회원 예약조회");
                                    startActivityForResult(intent, 80);
                                }
                            }
                        });
                        back.setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                mlist.setEmptyView(getView().findViewById(R.id.login_view));
                                getView().findViewById(R.id.empty_view).setVisibility(View.GONE);
                                getView().findViewById(R.id.reserv_view).setVisibility(View.GONE);
                            }
                        });
                        MainActivity.hideProgress();
                        setInfobar();
                    } else {
                        mlist.setEmptyView(getView().findViewById(R.id.empty_view));
                        mlist.getEmptyView().findViewById(R.id.tv_info1).setVisibility(View.GONE);
                        getView().findViewById(R.id.login_view).setVisibility(View.GONE);
                        main_view.setBackgroundResource(R.color.footerview);
                        getBookingList();
                    }
                } catch (Exception e) {
                    if (isAdded())
                        Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    MainActivity.hideProgress();
                }
            }
        });
    }

    public void getBookingList() {

        String url = CONFIG.ticket_booking_Url + "?page=" + currentPage + "&per_page=10000"; //endlessScrollListener.getCurrentPage();
        if (isAdd) {
            Api.get(url, new Api.HttpCallback() {
                @Override
                public void onFailure(Response response, Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                    MainActivity.hideProgress();
                }

                @Override
                public void onSuccess(Map<String, String> headers, String body) {
                    try {
                        JSONObject obj = new JSONObject(body);

                        if (!obj.getString("result").equals("success")) {
                            Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                            MainActivity.hideProgress();
                            return;
                        }

                        total_count = obj.getInt("total_count");

                        JSONArray feed = obj.getJSONArray("lists");
                        JSONObject entry;
                        if (feed.length() > 0) {
                            isAdd = true;
                            currentPage++;
                            for (int i = 0; i < feed.length(); i++) {
                                entry = feed.getJSONObject(i);
                                mEntries.add(new BookingQEntry(
                                        entry.getString("id"),
                                        entry.getString("status"),
                                        entry.getString("deal_name"),
                                        entry.getString("created_at_format"),
                                        entry.getString("img_url"),
                                        entry.getString("is_review_writable"),
                                        entry.getInt("total_ticket_count"),
                                        entry.getString("not_used_ticket_count"),
                                        entry.getString("used_ticket_count"),
                                        entry.getString("cancel_ticket_count"),
                                        entry.getString("status_display"),
                                        entry.getString("review_writable_words_1"),
                                        entry.getString("review_writable_words_2"),
                                        entry.getString("status_detail"),
                                        entry.getString("deal_id"))
                                );
                            }

                            adapter.notifyDataSetChanged();
                            setInfobar();
                        } else {
                            mlist.getEmptyView().findViewById(R.id.tv_info1).setVisibility(View.VISIBLE);
                            isAdd = false;
                        }

                        if (total_count == mEntries.size()) {
                            isAdd = false;
                        }

                        MainActivity.hideProgress();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), getString(R.string.error_try_again), Toast.LENGTH_SHORT).show();
                        MainActivity.hideProgress();
                    }
                }
            });
        } else {
            MainActivity.hideProgress();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 80) {
            ((ReservationFragment)getParentFragment()).setChildDelete(1);
            authCheck();
            ((MainActivity) getActivity()).setTitle();
            ((MainActivity) getActivity()).setTapdelete("MYPAGE");
            CONFIG.TabLogin = true;
            setInfobar();
        } else if (requestCode == 90 && resultCode == 0) {
            mEntries.clear();
            adapter.notifyDataSetChanged();
            currentPage = 1;
            isAdd = true;
            MainActivity.showProgress();
            getBookingList();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setInfobar() {
        if (_preferences.getString("userid", null) != null) {
            getView().findViewById(R.id.layout_info).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.layout_info).setVisibility(View.GONE);
        }
    }

    private void init() {
        // preference
        mlist = (NonScrollListView) getView().findViewById(R.id.h_list);
        scroll = (NestedScrollView) getView().findViewById(R.id.scroll);
        adapter = new ReservationActivityAdapter(getActivity(), 0, mEntries, _preferences.getString("userid", ""), ReservationActivityFragment.this);
        mlist.setAdapter(adapter);
        btn_go_login = (Button) getView().findViewById(R.id.btn_go_login);
        main_view = (RelativeLayout) getView().findViewById(R.id.main_view);
        btn_go_reservation = (TextView) getView().findViewById(R.id.btn_go_reservation);
        u_send = (Button) getView().findViewById(R.id.u_send);
        back = (ImageView) getView().findViewById(R.id.back);
        u_name = (EditText) getView().findViewById(R.id.u_name);
        u_tel = (EditText) getView().findViewById(R.id.u_tel);
        u_num = (EditText) getView().findViewById(R.id.u_num);

        mlist.setOnItemClickListener(new OnSingleItemClickListener() {
            @Override
            public void onSingleClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.aid);
                Intent intent = new Intent(getActivity(), ReservationActivityDetailActivity.class);
                intent.putExtra("tid", tv.getText().toString());
                startActivityForResult(intent, 90);
            }
        });
        CONFIG.sel_fav = 0;
        authCheck();
    }
}
