package com.hotelnow.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.hotelnow.R;
import com.hotelnow.adapter.BannerPagerAdapter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class ViewPagerCustom extends ViewPager {

    boolean canSwipe = true;

//    public ViewPagerCustom(Context context) {
//        super(context);
//    }
//
//    public ViewPagerCustom(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }

    private static final String TAG = "AutoScrollViewPager";

    // 默认滚动间隔时间
    private static final int DEFAULT_SLIDE_INTERVAL = 3000;
    // 自动滚动间隔时间
    private int slideInterval = DEFAULT_SLIDE_INTERVAL;

    private static final int DEFAULT_SLIDE_DURATION = 800;
    // 滑动时间
    private int slideDuration = DEFAULT_SLIDE_DURATION;

    // 滑动方向向右
    public static final int DIRECTION_RIGHT = 1;
    // 向左滑动
    public static final int DIRECTION_LEFT = 0;
    // 滑动方向
    private int direction = DIRECTION_RIGHT;
    // 触摸时停止滚动
    private boolean stopWhenTouch = true;

    // 滚动消息
    private static final int MSG_SCROLL = 1;

    // 是否循环（只针对普通ViewPager）
    private boolean cycle;

    // 是否是自动滚动
    private boolean isAutoScroll;
    // 是否已经触摸停止滚动
    private boolean isStopedWhenTouch;

    private MyHandler mHandler;
    private CustomDurationScroller mScroller;

    public ViewPagerCustom(Context context) {
        super(context);
        init(context, null);
    }

    public ViewPagerCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化方法
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        mHandler = new MyHandler(this);
        setScroller();
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable
                    .AutoScrollViewPager, 0, 0);
            try {
                slideInterval = array.getInt(R.styleable.AutoScrollViewPager_slideInterval,
                        DEFAULT_SLIDE_INTERVAL);
                direction = array.getInt(R.styleable.AutoScrollViewPager_slideDirection,
                        DIRECTION_RIGHT);
                stopWhenTouch = array.getBoolean(R.styleable.AutoScrollViewPager_stopWhenTouch,
                        true);
                cycle = array.getBoolean(R.styleable.AutoScrollViewPager_cycle, false);
                slideDuration = array.getInt(R.styleable.AutoScrollViewPager_slideDirection,
                        DEFAULT_SLIDE_DURATION);
            } finally {
                array.recycle();
            }
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null && adapter instanceof BannerPagerAdapter && adapter.getCount() > 1) {
            // 设置ViewPager初始位置在List的中间位置
            int midPos = ((adapter.getCount() - 2) / 2) - (adapter.getCount() - 2) / 2 % (
                    (BannerPagerAdapter) adapter).getItemCount() + 1;
            setCurrentItem(midPos);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnPageChangeListener(mOnPageChangeListener);
    }

    /**
     * ViewPager自动滚动方法
     */
    private void scroll() {
        PagerAdapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        int totalCount;
        if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
            // 如果adapter为空或者item数量小于等于1则不做任何操作
            return;
        }
        int nextItem = (direction == DIRECTION_RIGHT) ? ++currentItem : --currentItem;
        if (!(getAdapter() instanceof BannerPagerAdapter) && cycle) {
            // 如果是普通adapter并且是循环滚动则在第一个和最后一个特殊处理
            if (nextItem < 0) {
                setCurrentItem(totalCount - 1, true);
            } else if (nextItem == totalCount) {
                setCurrentItem(0, true);
            } else {
                setCurrentItem(nextItem, true);
            }
        } else {
            setCurrentItem(nextItem, true);
        }
    }

    /**
     * 设置自定义scroller控制ViewPager速度
     */
    private void setScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            mScroller = new CustomDurationScroller(getContext(), (Interpolator) interpolatorField
                    .get(null));
            mScroller.setDuration(slideDuration);
            scrollerField.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始自动滚动
     */
    public void startAutoScroll() {
        if (getAdapter().getCount() <= 1) {
            return;
        }
        isAutoScroll = true;
        sendScrollMsg(slideInterval);
    }

    /**
     * 开启自动滚动
     *
     * @param delayTime 滚动间隔时间，会覆盖之前设置的间隔时间。
     */
    public void startAutoScroll(int delayTime) {
        isAutoScroll = true;
        slideInterval = delayTime;
        setScroller();
        sendScrollMsg(delayTime);
    }

    /**
     * 停止自动滚动
     */
    public void stopAutoScroll() {
        isAutoScroll = false;
        mHandler.removeMessages(MSG_SCROLL);
    }

    /**
     * 发送滚动消息
     */
    private void sendScrollMsg(long delayTime) {
        mHandler.removeMessages(MSG_SCROLL);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLL, delayTime);
    }

    /**
     * 控制自动滚动的handler
     */
    private static class MyHandler extends Handler {
        private WeakReference<ViewPagerCustom> hostWeakReference;

        public MyHandler(ViewPagerCustom host) {
            hostWeakReference = new WeakReference<ViewPagerCustom>(host);
        }

        @Override
        public void handleMessage(Message msg) {
            ViewPagerCustom host = hostWeakReference.get();
            if (host != null) {
                host.scroll();
                host.sendScrollMsg(host.slideInterval);
            }
        }
    }

    /**
     * ViewPager滑动监听
     */
    private SimpleOnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (state == SCROLL_STATE_IDLE) {
                if (getAdapter() != null && getAdapter() instanceof BannerPagerAdapter) {
                    int cur = getCurrentItem();
                    // 获取最后一个真实的item
                    int lastReal = getAdapter().getCount() - 2;
                    if (cur == 0) {
                        setCurrentItem(lastReal, false);
                    } else if (cur > lastReal) {
                        setCurrentItem(1, false);
                    }
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (stopWhenTouch) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (isAutoScroll) {
                        isStopedWhenTouch = true;
                        stopAutoScroll();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_OUTSIDE:
                    if (isStopedWhenTouch) {
                        startAutoScroll();
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnPageChangeListener(mOnPageChangeListener);
    }

    public int getSlideInterval() {
        return slideInterval;
    }

    /**
     * 设置滑动间隔时间
     *
     * @param slideInterval
     */
    public void setSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;
        setScroller();
    }

    public int getDirection() {
        return direction;
    }

    /**
     * 设置滑动方向
     *
     * @param direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isStopWhenTouch() {
        return stopWhenTouch;
    }

    /**
     * 触摸时停止滑动
     *
     * @param stopWhenTouch
     */
    public void setStopWhenTouch(boolean stopWhenTouch) {
        this.stopWhenTouch = stopWhenTouch;
    }

    public boolean isCycle() {
        return cycle;
    }

    /**
     * 设置是否循环滚动（只对普通adapter设置）
     *
     * @param cycle
     */
    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    /**
     * 设置滑动时间
     *
     * @param slideDuration
     */
    public void setSlideDuration(int slideDuration) {
        this.slideDuration = slideDuration;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec)
                == MeasureSpec.AT_MOST;

        if (wrapHeight) {
            /**
             * The first super.onMeasure call made the pager take up all the
             * available height. Since we really wanted to wrap it, we need
             * to remeasure it. Luckily, after that call the first child is
             * now available. So, we take the height from it.
             */

            int width = getMeasuredWidth(), height = getMeasuredHeight();

            // Use the previously measured width but simplify the calculations
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

            /* If the pager actually has any children, take the first child's
             * height and call that our own */
            if (getChildCount() > 0) {
                View firstChild = getChildAt(0);

                /* The child was previously measured with exactly the full height.
                 * Allow it to wrap this time around. */
                firstChild.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));

                height = firstChild.getMeasuredHeight();
            }

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void canSwipe(boolean canSwipe) {
        this.canSwipe = canSwipe;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.canSwipe) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.canSwipe) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }
}
