package com.hotelnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.EventActivity;
import com.hotelnow.activity.PrivateDaelAllActivity;
import com.hotelnow.activity.ThemeSpecialActivityActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.activity.WebviewActivity;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.fragment.home.HomeFragment;
import com.hotelnow.model.BannerItem;
import com.hotelnow.model.SubBannerItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SubBannerPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<SubBannerItem> data;
    private String frontType;
    private String frontImg;
    private String frontMethod;
    private String frontEvtId;
    private String mId;
    private String method;
    private String url;
    private CallbackManager callbackManager;
    private String frontTitle;
    private static DialogAlert dialogAlert;
    private HomeFragment mHf;
    private String mTitle;

    public SubBannerPagerAdapter(Context context, ArrayList<SubBannerItem> data, HomeFragment mHf) {
        this.context = context;
        this.data = data;
        this.mHf = mHf;
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {

        //뷰페이지 슬라이딩 할 레이아웃 인플레이션
        int realPos = position % data.size();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subbanner_item, parent, false);
        ImageView image_container = (ImageView) v.findViewById(R.id.image_container);
        image_container.setTag(realPos);
        Ion.with(image_container).load(data.get(realPos).getImage());
        image_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mId = data.get((int) v.getTag()).getEvent_id();
                mTitle = data.get((int) v.getTag()).getTitle();
                if (!TextUtils.isEmpty(data.get((int) v.getTag()).getImage())) {
                    frontType = data.get((int) v.getTag()).getEvt_type();
                    frontImg = data.get((int) v.getTag()).getImage();
                    String[] arr = data.get((int) v.getTag()).getLink().split("hotelnowevent://");
                    if (arr.length > 1) {
                        frontMethod = arr[1];
                        frontMethod = Util.stringToHTMLString(frontMethod);
                        frontTitle = data.get((int) v.getTag()).getTitle() != "" ? data.get((int) v.getTag()).getTitle() : "무료 숙박 이벤트";
                    }
                    if (!frontType.equals("a")) {
                        frontEvtId = mId;
                    } else {
                        frontEvtId = Util.getFrontThemeId(data.get((int) v.getTag()).getLink());
                    }
                }
                if (data.get((int) v.getTag()).getEvt_type().equals("a") && !data.get((int) v.getTag()).getEvt_type().equals("")) {
                    try {
                        JSONObject obj = new JSONObject(frontMethod);
                        method = obj.getString("method");
                        if(obj.has("param")) {
                            url = obj.getString("param");
                        }

                        if (method.equals("move_near")) {
                            int fDayLimit = mHf._preferences.getInt("future_day_limit", 180);
                            String checkurl = CONFIG.checkinDateUrl + "/" + url + "/" + fDayLimit;

                            Api.get(checkurl, new Api.HttpCallback() {
                                @Override
                                public void onFailure(Response response, Exception e) {
                                    Toast.makeText(context, context.getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                @Override
                                public void onSuccess(Map<String, String> headers, String body) {
                                    try {
                                        JSONObject obj = new JSONObject(body);
                                        JSONArray aobj = obj.getJSONArray("data");

                                        if (aobj.length() == 0) {
                                            dialogAlert = new DialogAlert(
                                                    context.getString(R.string.alert_notice),
                                                    "해당 숙소는 현재 예약 가능한 객실이 없습니다.",
                                                    context,
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

                                        Intent intent = new Intent(context, DetailHotelActivity.class);
                                        intent.putExtra("hid", url);
                                        intent.putExtra("evt", "N");
                                        intent.putExtra("sdate", checkin);
                                        intent.putExtra("edate", checkout);

                                        mHf.startActivityForResult(intent, 80);

                                    } catch (Exception e) {
                                        // Log.e(CONFIG.TAG, e.toString());
                                        Toast.makeText(context, context.getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                }
                            });

                        } else if (method.equals("move_theme")) {
                            Intent intent = new Intent(context, ThemeSpecialHotelActivity.class);
                            intent.putExtra("tid", url);

                            mHf.startActivityForResult(intent, 80);
                        } else if (method.equals("move_theme_ticket")) {
                            Intent intent = new Intent(context, ThemeSpecialActivityActivity.class);
                            intent.putExtra("tid", url);

                            mHf.startActivityForResult(intent, 80);
                        } else if (method.equals("move_ticket_detail")) {
                            Intent intent = new Intent(context, DetailActivityActivity.class);
                            intent.putExtra("tid", url);

                            mHf.startActivityForResult(intent, 80);
                        } else if (method.equals("outer_link")) {
                            if (url.contains("hotelnow")) {
                                frontTitle = mTitle != "" ? mTitle : "무료 숙박 이벤트";
                                Intent intent = new Intent(context, WebviewActivity.class);
                                intent.putExtra("url", url);
                                intent.putExtra("title", frontTitle);
                                mHf.startActivityForResult(intent, 80);

                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                mHf.startActivity(intent);
                            }
                        } else if (method.equals("move_privatedeal_all")){
                            Intent intent = new Intent(context, PrivateDaelAllActivity.class);
                            mHf.startActivityForResult(intent, 200);
                        }
                    } catch (Exception e) {
                    }
                } else {
                    frontTitle = mTitle != "" ? mTitle : "무료 숙박 이벤트";
                    Intent intentEvt = new Intent(context, EventActivity.class);
                    intentEvt.putExtra("idx", Integer.valueOf(frontEvtId));
                    intentEvt.putExtra("title", mTitle);
                    mHf.startActivityForResult(intentEvt, 200);
                }
            }
        });
        parent.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);

    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
