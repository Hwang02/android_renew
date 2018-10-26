package com.hotelnow.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.hotelnow.R;
import com.hotelnow.utils.CONFIG;
import com.hotelnow.utils.HotelnowApplication;
import com.hotelnow.utils.Util;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

/**
 * Created by susia on 16. 1. 11..
 */
public class DialogShare extends Dialog {
    Context mContext;
    String hid;
    String main_photo;
    String hotel_name;
    String checkin;
    String checkout;
    Button closeButton;
    View.OnClickListener mClickListener;
    SharedPreferences _preferences;
    String linkUrl;
    ResponseCallback<KakaoLinkResponse> callback;
    String webUrl;
    Double value;

    public DialogShare(Context context, String hid, String main_photo, String hotel_name, String checkin, String checkout, Double value, View.OnClickListener close) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);

        mContext = context;
        this.hid = hid;
        this.main_photo = main_photo;
        this.hotel_name = hotel_name;
        this.checkin = checkin;
        this.checkout = checkout;
        this.value = value;
        this.mClickListener = close;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = (Activity)mContext;

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_share);

        // 문구....
        LinearLayout kakao_btn = (LinearLayout)findViewById(R.id.kakao_btn);
        LinearLayout sms_btn = (LinearLayout)findViewById(R.id.sms_btn);
        LinearLayout share_btn = (LinearLayout)findViewById(R.id.share_btn);

        _preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        linkUrl = CONFIG.domain+"/?hotel_id="+hid+"&date="+checkin+"&e_date="+checkout+"&is_event=N";
        webUrl = CONFIG.homeWebUrl+"/product/"+hid+"/N?"+"ci="+checkin+"&co="+checkout;

        callback = new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Toast.makeText(mContext, errorResult.getErrorMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
//                 Toast.makeText(mContext, "Successfully sent KakaoLink v2 message.", Toast.LENGTH_LONG).show();
            }
        };

        kakao_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    String msg = "";
                    if(value >0) {
                        msg = "투숙객 평점 " + value + "점 강력추천\n언제어디서나 호텔나우만 믿고 떠나세요!";
                    }
                    else{
                        msg = "언제어디서나 호텔나우만 믿고 떠나세요!";
                    }
                    FeedTemplate params = FeedTemplate
                            .newBuilder(ContentObject.newBuilder("["+hotel_name+"]",
                                    main_photo,
                                    LinkObject.newBuilder().setWebUrl(linkUrl)
                                            .setMobileWebUrl(linkUrl).build())
                                    .setDescrption(msg)
                                    .build())
                            .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                    .setWebUrl(webUrl)
                                    .setMobileWebUrl(linkUrl)
                                    .setAndroidExecutionParams(linkUrl)
                                    .setIosExecutionParams("hotelnow://hnevent={\"method\":\"move_detail\", \"param\":{\"hotel_id\":\""+hid+"\", \"date\":\""+checkin+"\", \"e_date\":\""+checkout+"\", \"is_event\":\"N\"}}")
                                    .build()))
                            .addButton(new ButtonObject("웹에서 보기", LinkObject.newBuilder().setWebUrl(webUrl).setMobileWebUrl(webUrl).build()))
                            .build();

                    KakaoLinkService.getInstance().sendDefault(mContext, params, callback);


                    dismiss();
            }
        });

        sms_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String smsBody = "[호텔나우]\n"
                        +"친구가 추천하는 호텔!\n"
                        + "[" + hotel_name + "]\n"
                        + "- 체크인 : " + Util.weekdayFormat(checkin) + "\n"
                        + "- 체크아웃 : " + Util.weekdayFormat(checkout) + "\n\n"
                        + "자세히보기 >> "+linkUrl;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
                {
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(mContext); //Need to change the build to API 19

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);

                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, smsBody);

                    if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
                    {
                        sendIntent.setPackage(defaultSmsPackageName);
                    }
                    mContext.startActivity(sendIntent);

                }
                else //For early versions, do what worked for you before.
                {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:"));
                    sendIntent.putExtra("sms_body", smsBody);
                    sendIntent.putExtra("address", ""); // 받는사람 번호
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    mContext.startActivity(sendIntent);
                }


                dismiss();
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) HotelnowApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", linkUrl);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "링크 메시지가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();

                dismiss();
            }
        });

        closeButton = (Button) findViewById(R.id.btn_ok);
        closeButton.setOnClickListener(mClickListener);
    }
}
