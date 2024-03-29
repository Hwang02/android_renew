package com.hotelnow.fragment.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.EventActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.PrivateDaelAllActivity;
import com.hotelnow.activity.ThemeSpecialActivityActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.activity.WebviewActivity;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


/**
 * Created by susia on 16. 1. 7..
 */
public class PagerMainFragment extends Fragment {

    private String mId;
    private String mTitle;
    private String mThumb_img;
    private String mThumb_link_action;
    private String mPopup_img;
    private String mResponse_wh;
    private String mPopup_map;
    private String mEvt_type;
    private String mFront_img;
    private String cookie;
    private RelativeLayout popup_bg;
    private static String method;
    private static String url;
    private String frontType;
    private String frontImg;
    private String frontMethod;
    private String frontTitle;
    private String frontEvtId;
    //    static Tracker t;
    private static DialogAlert dialogAlert;
    private static MainActivity mPf;

    public static PagerMainFragment create(MainActivity pf, String id, String title, String thumb_img, String thumb_link_action, String popup_img, String response_wh, String popup_map, String evt_type, String front_img) {
        PagerMainFragment fragment = new PagerMainFragment();
        Bundle args = new Bundle();

        args.putString("id", id);
        args.putString("title", title);
        args.putString("thumb_img", thumb_img);
        args.putString("thumb_link_action", thumb_link_action);
        args.putString("popup_img", popup_img);
        args.putString("response_wh", response_wh);
        args.putString("popup_map", popup_map);
        args.putString("evt_type", evt_type);
        args.putString("front_img", front_img);
        mPf = pf;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mId = getArguments().getString("id");
        mTitle = getArguments().getString("title");
        mThumb_img = getArguments().getString("thumb_img");
        mThumb_link_action = getArguments().getString("thumb_link_action");
        mPopup_img = getArguments().getString("popup_img");
        mResponse_wh = getArguments().getString("response_wh");
        mPopup_map = getArguments().getString("popup_map");
        mEvt_type = getArguments().getString("evt_type");
        mFront_img = getArguments().getString("front_img");
        getArguments().clear();
        if (!TextUtils.isEmpty(mFront_img)) {
            frontType = mEvt_type;
            frontImg = mFront_img;
            String[] arr = mThumb_link_action.split("hotelnowevent://");
            if (arr.length > 1) {
                frontMethod = arr[1];
                frontMethod = Util.stringToHTMLString(frontMethod);
            }
            if (!frontType.equals("a")) {
                frontEvtId = mId;
            } else {
                frontEvtId = Util.getFrontThemeId(mThumb_link_action);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.dialog_frontpopup, container, false);

        ImageView popup_img = (ImageView) rootView.findViewById(R.id.popup_img);
        Ion.with(popup_img).load(mFront_img);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        popup_bg = (RelativeLayout) getView().findViewById(R.id.popup_bg);
        popup_bg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (frontType.equals("a") && !frontType.equals("")) {
                    try {
                        JSONObject obj = new JSONObject(frontMethod);
                        method = obj.getString("method");
                        if (obj.has("param")) {
                            url = obj.getString("param");
                        }
                        if (method.equals("move_near")) {
                            int fDayLimit = mPf._preferences.getInt("future_day_limit", 180);
                            String checkurl = CONFIG.checkinDateUrl + "/" + url + "/" + fDayLimit;

                            Api.get(checkurl, new Api.HttpCallback() {
                                @Override
                                public void onFailure(Response response, Exception e) {
                                    Toast.makeText(mPf, mPf.getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                @Override
                                public void onSuccess(Map<String, String> headers, String body) {
                                    try {
                                        JSONObject obj = new JSONObject(body);
                                        JSONArray aobj = obj.getJSONArray("data");

                                        if (aobj.length() == 0) {
                                            dialogAlert = new DialogAlert(
                                                    getActivity().getString(R.string.alert_notice),
                                                    "해당 숙소는 현재 예약 가능한 객실이 없습니다.",
                                                    mPf,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialogAlert.dismiss();
                                                        }
                                                    });
                                            dialogAlert.setCancelable(false);
                                            dialogAlert.show();
                                            return;
                                        }

                                        String checkin = aobj.getString(0);
                                        String checkout = Util.getNextDateStr(checkin);

                                        Intent intent = new Intent(mPf, DetailHotelActivity.class);
                                        intent.putExtra("hid", url);
                                        intent.putExtra("evt", "N");
                                        intent.putExtra("save", true);
                                        intent.putExtra("sdate", checkin);
                                        intent.putExtra("edate", checkout);

                                        mPf.startActivityForResult(intent, 80);

                                    } catch (Exception e) {
                                        // Log.e(CONFIG.TAG, e.toString());
                                        Toast.makeText(mPf, mPf.getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                }
                            });

                        } else if (method.equals("move_theme")) {
                            Intent intent = new Intent(mPf, ThemeSpecialHotelActivity.class);
                            intent.putExtra("tid", url);

                            mPf.startActivityForResult(intent, 80);
                        } else if (method.equals("move_theme_ticket")) {
                            Intent intent = new Intent(mPf, ThemeSpecialActivityActivity.class);
                            intent.putExtra("tid", url);

                            mPf.startActivityForResult(intent, 80);
                        } else if (method.equals("move_ticket_detail")) {
                            Intent intent = new Intent(mPf, DetailActivityActivity.class);
                            intent.putExtra("tid", url);

                            mPf.startActivityForResult(intent, 80);
                        } else if (method.equals("outer_link")) {
                            if (url.contains("hotelnow")) {
                                frontTitle = mTitle != "" ? mTitle : "무료 숙박 이벤트";
                                Intent intent = new Intent(mPf, WebviewActivity.class);
                                intent.putExtra("url", url);
                                intent.putExtra("title", frontTitle);
                                mPf.startActivityForResult(intent, 80);

                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                mPf.startActivity(intent);
                            }
                        } else if (method.equals("move_privatedeal_all")){
                            Intent intent = new Intent(mPf, PrivateDaelAllActivity.class);
                            mPf.startActivityForResult(intent, 200);
                        }
                    } catch (Throwable t) {
                        Toast.makeText(mPf, "올바른 형식의 주소가 아닙니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    frontTitle = mTitle != "" ? mTitle : "무료 숙박 이벤트";
                    Intent intentEvt = new Intent(mPf, EventActivity.class);
                    intentEvt.putExtra("idx", Integer.valueOf(frontEvtId));
                    intentEvt.putExtra("title", frontTitle);
                    mPf.startActivityForResult(intentEvt, 80);
                }

                if (mPf != null && mPf.frgpopup != null) {
                    Calendar calendar = Calendar.getInstance();
                    Date currentTime = new Date();
                    calendar.setTime(currentTime);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);

                    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String checkdate = mSimpleDateFormat.format(calendar.getTime());

                    Util.setPreferenceValues(mPf._preferences, "front_popup_date", checkdate);

                    mPf.frgpopup.dismiss();
                }
            }
        });
    }
}