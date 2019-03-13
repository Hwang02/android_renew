package com.hotelnow.utils;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by idhwang on 2018. 1. 17..
 */

public class EndEventScrollView extends ScrollView {

    private Handler handler = null;
    private Rect rect;

    public EndEventScrollView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        checkIsLocatedAtFooter();
    }

    private void checkIsLocatedAtFooter() {
        if (rect == null) {
            rect = new Rect();
            getLocalVisibleRect(rect);
            return;
        }
        int oldBottom = rect.bottom;
        getLocalVisibleRect(rect);
        int height = getMeasuredHeight();
        View v = getChildAt(0);
        if (oldBottom > 0 && height > 0) {
            if (oldBottom != rect.bottom && rect.bottom == v.getMeasuredHeight()) {
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            } else if (oldBottom != rect.bottom && oldBottom < v.getMeasuredHeight() / 1.05) {
                if (handler != null) {
                    handler.sendEmptyMessage(1);
                }
            }
        }

    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}