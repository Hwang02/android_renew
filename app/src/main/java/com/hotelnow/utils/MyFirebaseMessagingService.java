package com.hotelnow.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hotelnow.R;
import com.hotelnow.activity.ActLoading;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by idhwang on 2018. 9. 4..
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private String pushType = "";
    private String bid;
    private String hid;
    private int evtidx;
    private String evttag;
    private String isevt = "N";
    private String check_in = "";
    private String check_out = "";
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = -11;
    public static final String CHANNEL_ID = "com.hotelnow";
    public static final String CHANNEL_NAME = "hotelnow";
    private JSONObject jsonObj;
    private SharedPreferences _preferences;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!(TextUtils.isEmpty(token))) {
            LogUtil.e(TAG, "newToken : " + token);

            Util.setPreferenceValues(_preferences, "gcm_registration_id", token);
            if (!_preferences.getBoolean("flag_first_executed", false)) {
                String regId = _preferences.getString("gcm_registration_id", null);
                String userId = _preferences.getString("userid", null);
                Util.setGcmToken(this, regId, userId, null);
            }
        }
    }

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> map = remoteMessage.getData();

        for (Map.Entry<String, String> ent : map.entrySet()) {
            LogUtil.i(TAG, "entry " + ent.getKey() + " : " + ent.getValue());
            if (ent.getKey() != null) {
                String key = ent.getKey();
                if (key.equalsIgnoreCase("af-uinstall-tracking") || key.equalsIgnoreCase("af-uninstall-tracking")) {
                    LogUtil.w(TAG, "appsFlyer tracking message. try to ignore");
                    return;
                }
            }
        }

        //추가한것
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        try {
            Map<String, String> map = remoteMessage.getData();
            if (map != null) {
                String message = map.get("message");
                String custom = map.get("custom");
                jsonObj = new JSONObject(custom);
                String tmpVal = jsonObj.getString("collapse_key");
                String[] tmpArr = tmpVal.split("\\|");
                pushType = tmpArr[0];

                if (pushType.equals("1") || pushType.equals("4")) {
                    bid = tmpArr[1];
                } else if (pushType.equals("3")) {
                    hid = tmpArr[1];
                    isevt = (tmpArr.length == 3 && tmpArr[2].equals("Y")) ? "Y" : "N";
                    check_in = (tmpArr.length == 5 && !TextUtils.isEmpty(tmpArr[3])) ? tmpArr[3] : "";
                    check_out = (tmpArr.length == 5 && !TextUtils.isEmpty(tmpArr[4])) ? tmpArr[4] : "";
                } else if (pushType.equals("2") || pushType.equals("6") || pushType.equals("8")) {
                    if (pushType.equals("6")) {
                        evtidx = (tmpArr.length == 3) ? Integer.valueOf(tmpArr[1]) : 0;
                        evttag = (tmpArr.length == 3) ? tmpArr[2] : "H";
                    } else {
                        evtidx = (tmpArr.length == 2) ? Integer.valueOf(tmpArr[1]) : 0;
                        if(pushType.equals("2")) {
                            if(jsonObj.has("tab_no")) {
                                String subType = jsonObj.getString("tab_no"); // 1: 추천 2: 숙소 3: 액티비티
                                String[] subTmpArr = subType.split("\\|");
                                if (subTmpArr[0].equals("1")) {
                                    pushType = "2";
                                } else if (subTmpArr[0].equals("2")) {
                                    pushType = "10";
                                } else if (subTmpArr[0].equals("3")) {
                                    pushType = "11";
                                }
                            }
                        }
                    }
                }

                LogUtil.e("message : ", message);
                LogUtil.e("custom : ", custom);
                Intent intent = new Intent(getApplication(), ActLoading.class);
                PendingIntent contentIntent;
                int dummyInt = (int) (Math.random() * 1000);

                // 1 : 예약 상세, 2 : 인덱스 페이지 이동, 3: 상품 상세, 4: 리뷰 activity, 5: 적립금 페이지
                intent.putExtra("push_type", pushType);
                intent.putExtra("bid", bid);
                intent.putExtra("hid", hid);
                intent.putExtra("isevt", isevt);
                intent.putExtra("evtidx", evtidx);
                intent.putExtra("evttag", evttag);
                intent.putExtra("sdate", check_in);
                intent.putExtra("edate", check_out);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                contentIntent = PendingIntent.getActivity(getApplication(), dummyInt, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                mNotificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    mNotificationManager.createNotificationChannel(mChannel);
                    Notification.Builder mBuilder = new Notification.Builder(getApplication(), mChannel.getId())
                            .setSmallIcon(R.drawable.ico_push)
                            .setTicker(message)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(message)
                            .setContentIntent(contentIntent)
                            .setStyle(new Notification.BigTextStyle().bigText(message))
                            .setAutoCancel(true);
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                } else if (Build.VERSION.SDK_INT > 16 && Build.VERSION.SDK_INT < 26) {
                    Notification.Builder mBuilder = new Notification.Builder(this)
                            .setSmallIcon(R.drawable.ico_push)
                            .setTicker(message)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(message)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setContentIntent(contentIntent)
                            .setStyle(new Notification.BigTextStyle().bigText(message))
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_MAX);
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                } else {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(message)
                            .setSmallIcon(R.drawable.ico_push)
                            .setAutoCancel(true)
                            .setTicker(message)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentIntent(contentIntent);
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
