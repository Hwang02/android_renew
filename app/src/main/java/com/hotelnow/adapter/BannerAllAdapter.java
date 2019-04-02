package com.hotelnow.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hotelnow.R;
import com.hotelnow.activity.DetailActivityActivity;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.EventActivity;
import com.hotelnow.activity.ThemeSpecialActivityActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.activity.WebviewActivity;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.utils.Api;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.DbOpenHelper;
import com.hotelnow.utils.LogUtil;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class BannerAllAdapter extends ArrayAdapter<BannerItem> {

    List<BannerItem> data;
    DbOpenHelper dbHelper;
    Context mContext;
    String page;
    private String frontType;
    private String frontImg;
    private String frontMethod;
    private String frontEvtId;
    private String mId;
    private String method;
    private String url;
    private String frontTitle;
    private static DialogAlert dialogAlert;
    private String mTitle;
    private SharedPreferences _preferences;
    private Activity mActivity;

    public BannerAllAdapter(Context context, int textViewResourceId, List<BannerItem> objects, DbOpenHelper dbHelper, String page) {
        super(context, textViewResourceId, objects);

        mContext = context;
        data = objects;
        this.dbHelper = dbHelper;
        this.page = page;
        _preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mActivity = (Activity) context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_banner_item, null);
        }

        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }

        BannerItem entry = getItem(position);
        RelativeLayout.LayoutParams param;
        if (page == "Home") {
            param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param.height = mContext.getResources().getDisplayMetrics().widthPixels / 2;
            LogUtil.e("xxxxx", mContext.getResources().getDisplayMetrics().widthPixels / 2 + "");
        } else {
            param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param.height = mContext.getResources().getDisplayMetrics().widthPixels / 3;
            LogUtil.e("xxxxx", mContext.getResources().getDisplayMetrics().widthPixels / 3 + "");
        }

        if (position == 0) {
            param.topMargin = Util.dptopixel(mContext, 14);
            param.leftMargin = Util.dptopixel(mContext, 16);
            param.rightMargin = Util.dptopixel(mContext, 16);
            holder.iv_img.setLayoutParams(param);
        } else {
            param.topMargin = Util.dptopixel(mContext, 10);
            param.leftMargin = Util.dptopixel(mContext, 16);
            param.rightMargin = Util.dptopixel(mContext, 16);
            if (data.size() - 1 == position)
                param.bottomMargin = Util.dptopixel(mContext, 10);
            holder.iv_img.setLayoutParams(param);
        }
        Ion.with(holder.iv_img).animateIn(R.anim.fadein).load(entry.getImage());
        holder.iv_img.setTag(position);
        holder.iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mId = data.get((int) v.getTag()).getId();
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
                        url = obj.getString("param");

                        if (method.equals("move_near")) {
                            int fDayLimit = _preferences.getInt("future_day_limit", 180);
                            String checkurl = CONFIG.checkinDateUrl + "/" + url + "/" + fDayLimit;

                            Api.get(checkurl, new Api.HttpCallback() {
                                @Override
                                public void onFailure(Response response, Exception e) {
                                    Toast.makeText(mContext, mContext.getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                @Override
                                public void onSuccess(Map<String, String> headers, String body) {
                                    try {
                                        JSONObject obj = new JSONObject(body);
                                        JSONArray aobj = obj.getJSONArray("data");

                                        if (aobj.length() == 0) {
                                            dialogAlert = new DialogAlert(
                                                    mContext.getString(R.string.alert_notice),
                                                    "해당 숙소는 현재 예약 가능한 객실이 없습니다.",
                                                    mContext,
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

                                        Intent intent = new Intent(mContext, DetailHotelActivity.class);
                                        intent.putExtra("hid", url);
                                        intent.putExtra("evt", "N");
                                        intent.putExtra("sdate", checkin);
                                        intent.putExtra("edate", checkout);

                                        mActivity.startActivityForResult(intent, 80);

                                    } catch (Exception e) {
                                        // Log.e(CONFIG.TAG, e.toString());
                                        Toast.makeText(mContext, mContext.getString(R.string.error_connect_problem), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                }
                            });

//                                t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("popup").build());
//                                TuneWrap.Event("EVENT", frontEvtId);
                        } else if (method.equals("move_theme")) {
                            Intent intent = new Intent(mContext, ThemeSpecialHotelActivity.class);
                            intent.putExtra("tid", url);

                            mActivity.startActivityForResult(intent, 80);

//                                t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("popup").build());
//                                TuneWrap.Event("EVENT", frontEvtId);
                        } else if (method.equals("move_theme_ticket")) {
                            Intent intent = new Intent(mContext, ThemeSpecialActivityActivity.class);
                            intent.putExtra("tid", url);

                            mActivity.startActivityForResult(intent, 80);

//                                t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("banner").build());
//                                TuneWrap.Event("EVENT", frontEvtId);
                        } else if (method.equals("move_ticket_detail")) {
                            Intent intent = new Intent(mContext, DetailActivityActivity.class);
                            intent.putExtra("tid", url);

                            mActivity.startActivityForResult(intent, 80);

//                                t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("banner").build());
//                                TuneWrap.Event("EVENT", frontEvtId);
                        } else if (method.equals("outer_link")) {
                            if (url.contains("hotelnow")) {
                                frontTitle = mTitle != "" ? mTitle : "무료 숙박 이벤트";
                                Intent intent = new Intent(mContext, WebviewActivity.class);
                                intent.putExtra("url", url);
                                intent.putExtra("title", frontTitle);
                                mActivity.startActivityForResult(intent, 80);

                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                mActivity.startActivity(intent);
                            }

//                                t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("popup").build());
//                                TuneWrap.Event("EVENT", frontEvtId);
                        }
                    } catch (Exception e) {
                    }
                } else {
                    Intent intentEvt = new Intent(mContext, EventActivity.class);
                    intentEvt.putExtra("idx", Integer.valueOf(frontEvtId));
                    intentEvt.putExtra("title", frontTitle);
                    mContext.startActivity(intentEvt);
                }
            }
        });

        return v;
    }

    private class ViewHolder {

        RoundedImageView iv_img;

        public ViewHolder(View v) {
            iv_img = (RoundedImageView) v.findViewById(R.id.image_container);

            v.setTag(R.id.id_holder);
        }

    }

}
