package com.hotelnow.utils;

import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;

public abstract class OnSingleItemClickListener implements AdapterView.OnItemClickListener {
    /**
     * 最短click事件的时间间隔
     */
    private static final long MIN_CLICK_INTERVAL=600;
    /**
     * 上次click的时间
     */
    private long mLastClickTime;

    public abstract void onSingleClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    public final void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentClickTime= SystemClock.uptimeMillis();
        long elapsedTime=currentClickTime-mLastClickTime;
        //有可能2次连击，也有可能3连击，保证mLastClickTime记录的总是上次click的时间
        mLastClickTime=currentClickTime;

        if(elapsedTime<=MIN_CLICK_INTERVAL)
            return;

        onSingleClick(parent, view, position, id);
    }

}