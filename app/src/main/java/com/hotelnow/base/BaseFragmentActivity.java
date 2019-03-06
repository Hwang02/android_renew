package com.hotelnow.base;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.hotelnow.BuildConfig;
import com.hotelnow.dialog.DialogAlert;
import com.hotelnow.utils.FindDebugger;
import com.hotelnow.utils.LogUtil;

public abstract class BaseFragmentActivity extends FragmentActivity {

    private DialogAlert dialogAlert;

    @Override
    protected void onResume() {
        super.onResume();

        if(!BuildConfig.DEBUG && isDebugged()){
            dialogAlert = new DialogAlert("알림", "디버깅 탐지로 앱을 종료 합니다.", BaseFragmentActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAlert.dismiss();
                    moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            dialogAlert.show();
            dialogAlert.setCancelable(false);
            return;
        }
    }

    public boolean isDebugged() {
        LogUtil.e("ActLoading","Checking for debuggers...");

        boolean tracer = false;
        try {
            tracer = FindDebugger.hasTracerPid();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (FindDebugger.isBeingDebugged() || tracer) {
            LogUtil.e("ActLoading","Debugger was detected");
            return true;
        } else {
            LogUtil.e("ActLoading","No debugger was detected.");
            return false;
        }
    }
}
