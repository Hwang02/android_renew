package com.hotelnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hotelnow.R;

/**
 * Created by susia on 16. 6. 17..
 */
public class DialogBillingAlert extends Dialog {
    private View.OnClickListener mOkClickListener;
    private View.OnClickListener mCloseClickListener;
    private Context mContext;
    public RelativeLayout paint_area;
    public Boolean hasSign = false;
    public Canvas sign;
    public MyPaint mp;
    public String selected_card = "";

    public DialogBillingAlert(Context context, View.OnClickListener ok, View.OnClickListener close) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.mContext = context;
        this.mOkClickListener = ok;
        this.mCloseClickListener = close;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_billing_alert);

        Button mOkButton = (Button) findViewById(R.id.ok);
        mOkButton.setOnClickListener(mOkClickListener);

        if(selected_card.equals("")) {
            Toast.makeText(mContext, "서명 후 카드 등록을 완료하시면 결제가 완료됩니다.", Toast.LENGTH_LONG).show();
            mOkButton.setText("결제하기");
        } else {
            mOkButton.setText("결제완료");
        }

        Button mCloseButton = (Button) findViewById(R.id.close);
        mCloseButton.setOnClickListener(mCloseClickListener);

        paint_area = (RelativeLayout)findViewById(R.id.paint_area);
        mp = new MyPaint(mContext);

        paint_area.addView(mp);
    }

    public Bitmap getBitmap() {
        return mp.getBitmap();
    }

    class Point {
        float x;
        float y;
        boolean isDraw;
        public Point(float x, float y, boolean isDraw) {
            this.x = x;
            this.y = y;
            this.isDraw = isDraw;
        }
    }

    class MyPaint extends View {
        public Bitmap mBitmap;
        public Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private Paint mPaint;

        public MyPaint(Context context) {
            super(context);
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(10);
        }


        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            mCanvas.drawPath(mPath, mPaint);
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();

                    if(hasSign == false) {
                        hasSign = true;
                    }

                    break;
            }
            return true;
        }

        public Bitmap getBitmap()
        {
            this.setDrawingCacheEnabled(true);
            this.buildDrawingCache();
            Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
            this.setDrawingCacheEnabled(false);

            return bmp;
        }

        public void clear(){
            mBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            System.gc();
        }

    }
}
