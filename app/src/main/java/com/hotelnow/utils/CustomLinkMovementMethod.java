package com.hotelnow.utils;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

public class CustomLinkMovementMethod extends LinkMovementMethod {

    private static CustomLinkMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new CustomLinkMovementMethod();
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(final TextView widget, final Spannable buffer, final MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) { // if you want only ACTION_DOWN,remove the ACTION_UP

            final int x = (int) event.getX() - widget.getTotalPaddingLeft() + widget.getScrollX();
            final int y = (int) event.getY() - widget.getTotalPaddingTop() + widget.getScrollY();
            final Layout layout = widget.getLayout();
            final int line = layout.getLineForVertical(y);
            final int off = layout.getOffsetForHorizontal(line, x);
            final ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                // Do any thing, link calls come here for both up and down</p>
                // return true/false, if you want to block external url redirection.

            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }
}