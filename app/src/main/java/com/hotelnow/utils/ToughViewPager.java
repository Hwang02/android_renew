package com.hotelnow.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by susia on 16. 1. 5..
 */
public class ToughViewPager extends ViewPager {

    public ToughViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ToughViewPager(final Context context) {
        super(context);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && v instanceof ViewPager) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}