package com.hotelnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.CallbackManager;
import com.hotelnow.R;
import com.hotelnow.activity.DetailHotelActivity;
import com.hotelnow.activity.EventActivity;
import com.hotelnow.activity.MainActivity;
import com.hotelnow.activity.ThemeSpecialHotelActivity;
import com.hotelnow.activity.WebviewActivity;
import com.hotelnow.fragment.model.BannerItem;
import com.hotelnow.utils.Util;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;

public class BannerPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<BannerItem> data;
    private String frontType;
    private String frontImg;
    private String frontMethod;
    private String frontEvtId;
    private String mId;
    private String method;
    private String url;
    private CallbackManager callbackManager;

    public BannerPagerAdapter(Context context, ArrayList<BannerItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {

        //뷰페이지 슬라이딩 할 레이아웃 인플레이션
        int realPos = position % data.size();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_banner_item, parent, false);
        RoundedImageView image_container = (RoundedImageView) v.findViewById(R.id.image_container);
        Ion.with(image_container).load(data.get(realPos).getImage());
        image_container.setTag(realPos);
        image_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mId = data.get((int)v.getTag()).getId();
                if (!TextUtils.isEmpty(data.get((int)v.getTag()).getImage())) {
                    frontType = data.get((int)v.getTag()).getEvt_type();
                    frontImg = data.get((int)v.getTag()).getImage();
                    String[] arr = data.get((int)v.getTag()).getLink().split("hotelnowevent://");
                    if (arr.length > 1) {
                        frontMethod = arr[1];
                        frontMethod = Util.stringToHTMLString(frontMethod);
                    }
                    if (!frontType.equals("a")) {
                        frontEvtId = mId;
                    } else {
                        frontEvtId = Util.getFrontThemeId(data.get((int)v.getTag()).getLink());
                    }
                }
                if(data.get((int)v.getTag()).getEvt_type().equals("a") && !data.get((int)v.getTag()).getEvt_type().equals("")) {
                    try {
                        JSONObject obj = new JSONObject(frontMethod);
                        method = obj.getString("method");
                        url = obj.getString("param");
                        if (method.equals("social_open")) {
                            if (url.equals("kakao")) {
                                Util.showKakaoLink((MainActivity)context);
//                                t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("popup").build());
//                                t.send(new HitBuilders.EventBuilder().setCategory("RECOMMENDATION").setAction("KAKAO").setLabel("EVENT_ALONE").build());
//                                TuneWrap.Event("EVENT", frontEvtId);
//                                TuneWrap.Event("RECOMMENDATION", "KAKAO", "EVENT_ALONE");
                            } else if (url.equals("facebook")) {
                                callbackManager = CallbackManager.Factory.create();
                                Util.shareFacebookFeed((MainActivity)context, callbackManager);

//                                t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("popup").build());
//                                t.send(new HitBuilders.EventBuilder().setCategory("RECOMMENDATION").setAction("FACEBOOK").setLabel("EVENT_ALONE").build());
//                                TuneWrap.Event("EVENT", frontEvtId);
//                                TuneWrap.Event("RECOMMENDATION", "FACEBOOK", "EVENT_ALONE");
                            }
                        } else if (method.equals("move_near")) {
                            Intent intent = new Intent(context, DetailHotelActivity.class);
                            intent.putExtra("hid", url);
                            intent.putExtra("evt", "N");
                            intent.putExtra("sdate", Util.setCheckinout().get(0));
                            intent.putExtra("edate", Util.setCheckinout().get(1));
                            context.startActivity(intent);
                        }
                        else if (method.equals("outer_link")) {
                            if(url.contains("hotelnow")) {
                                Intent intent = new Intent(context, WebviewActivity.class);
                                intent.putExtra("url", url);
                                intent.putExtra("title", "");

                                context.startActivity(intent);

                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                context.startActivity(intent);
                            }

//                            t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("popup").build());
//                            TuneWrap.Event("EVENT", frontEvtId);
                        }
                        else if (method.equals("move_theme")) {
                            Intent intent = new Intent(context, ThemeSpecialHotelActivity.class);
                            intent.putExtra("tid", url);

                            context.startActivity(intent);

//                            t.send(new HitBuilders.EventBuilder().setCategory("EVENT").setAction(frontEvtId).setLabel("popup").build());
//                            TuneWrap.Event("EVENT", frontEvtId);
                        }
                    }
                    catch (Exception e) {}
                }
                else {
                    Intent intentEvt = new Intent(context, EventActivity.class);
                    intentEvt.putExtra("idx", Integer.valueOf(frontEvtId));
                    context.startActivity(intentEvt);
                }
            }
        });
        parent.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View)object);

    }

    public int getItemCount() {
        return data == null ? 0 : data.size();
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
