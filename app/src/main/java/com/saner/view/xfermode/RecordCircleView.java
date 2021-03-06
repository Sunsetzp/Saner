package com.saner.view.xfermode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.saner.util.LogUtil;

/**
 * Created by sunset on 2018/5/11.
 */

public class RecordCircleView extends View {


    private Paint mForeignPaint;
    private Paint mInnerPaint;
    private Paint mArcPaint;
    //外圆
    private int mForeignRadius = 0;
    //内圆
    private int mInnerRadius = 0;

    private final int RATE = 40;
    private int mRate = 0;

    private double mSweepAngle = 0;
    private int centerX, centerY;
    private RectF mArcRectF;
    private int mArcStrokeWidth = 15;
    //外圆的区域
    private Region mForeignRegion;


    public RecordCircleView(Context context) {
        this(context, null);
    }

    public RecordCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    private void init(Context context) {

        mForeignPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mForeignPaint.setStyle(Paint.Style.FILL);
        mForeignPaint.setColor(Color.DKGRAY);

        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setColor(Color.WHITE);

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcStrokeWidth);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeJoin(Paint.Join.ROUND);
        mArcPaint.setColor(Color.GREEN);

        mArcRectF = new RectF();
        mForeignRegion = new Region();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        mForeignRadius = (w - RATE * 2) / 2;
        mInnerRadius = mForeignRadius / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = mForeignRadius + mRate;

        canvas.drawCircle(centerX, centerY, radius, mForeignPaint);
        canvas.drawCircle(centerX, centerY, mInnerRadius - mRate, mInnerPaint);


        mArcRectF.left = centerX - radius + mArcStrokeWidth / 2;
        mArcRectF.top = centerY - radius + mArcStrokeWidth / 2;
        mArcRectF.right = radius * 2 + (centerX - radius - mArcStrokeWidth / 2);
        mArcRectF.bottom = radius * 2 + (centerY - radius - mArcStrokeWidth / 2);
        canvas.drawArc(mArcRectF, 0, (float) (360 * mSweepAngle), false, mArcPaint);


        mForeignRegion.set((int) mArcRectF.left, (int) mArcRectF.top, (int) mArcRectF.right, (int) mArcRectF.bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width=0;
//        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
//        int widthModel=MeasureSpec.getMode(widthMeasureSpec);
//        if (widthModel==MeasureSpec.UNSPECIFIED){
//            LogUtil.logd("UNSPECIFIED");
//            width=mForeignRadius+RATE;
//        }else if (widthModel==MeasureSpec.EXACTLY){
//            LogUtil.logd("EXACTLY");
//            width=widthSize;
//        }else if (widthModel==MeasureSpec.AT_MOST){
//            LogUtil.logd("AT_MOST");
//            width=Math.min(widthSize,mForeignRadius+RATE);
//        }
        //测量时以宽度为标准
        setMeasuredDimension(resolveSize(mForeignRadius + RATE, widthMeasureSpec), resolveSize(mForeignRadius + RATE, widthMeasureSpec));

    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mForeignRegion.contains((int) event.getX(),(int)event.getY())){
                    mRate = RATE;
                    handler.sendMessage(handler.obtainMessage(0));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mRate!=0){
                    mRate = 0;
                    mSweepAngle = 0;
                    handler.removeMessages(0);
                    invalidate();

                }
                break;
        }
        return true;
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mSweepAngle <= 1) {
                        mSweepAngle = mSweepAngle + 0.01;
                        postInvalidate();
                        handler.sendMessageDelayed(handler.obtainMessage(0), 100);
//                        LogUtil.logd("mSweepAngle = "+((int) (mSweepAngle * 100)));
                    }
                    break;
            }
            return false;
        }

    });


}
