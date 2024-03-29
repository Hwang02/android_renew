package com.hotelnow.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

public class HrSpan extends ReplacementSpan {
    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start,
                       @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
        return 0;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start,
                     @IntRange(from = 0) int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        // Draws an 8px tall rectangle the same
        // width as the TextView
        canvas.drawRect(0, top, 10000, top + 3, paint);
    }
}