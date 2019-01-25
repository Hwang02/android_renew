package com.hotelnow.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class NonSwipeableViewPager extends ViewPager {

    private boolean mEnabled;
    boolean firstDragFlag = true;
    boolean dragFlag = false;   //현재 터치가 드래그 인지 확인
    boolean motionFlag = false;
    float startYPosition = 0, endYPosition = 0;       //터치이벤트의 시작점의 Y(세로)위치

    public NonSwipeableViewPager(Context context) {
        super(context);
        mEnabled = false;
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mEnabled ? super.onTouchEvent(event) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mEnabled ? super.onTouchEvent(ev) : false;
    }

    public void setPagingEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isPagingEnabled() {
        return mEnabled;
    }
}
