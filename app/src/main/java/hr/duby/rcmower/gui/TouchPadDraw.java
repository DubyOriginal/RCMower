package hr.duby.rcmower.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;

/**
 * Created by Duby on 14.11.2016..
 */

public class TouchPadDraw extends View {
    /** Stores data about single circle */
    private class CircleArea {
        int radius;
        int centerX;
        int centerY;

        CircleArea(int centerX, int centerY, int radius) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }

    //VARS
    private Paint mCirclePaint;
    private Paint strokePaint;
    private CircleArea mCircleArea;

    //FLAGS
    boolean flag = false;

    /** All available circles */
    //private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    //private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);

    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public TouchPadDraw(final Context ct) {
        super(ct);
        init(ct);
    }

    private void init(final Context ct) {
        // Generate bitmap used for background
        //mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.abc_ic_menu_cut_mtrl_alpha);
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.argb(100,200,60,150));
        mCirclePaint.setStrokeWidth(40);
        mCirclePaint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint();
        strokePaint.setColor(Color.argb(100,0,100,0));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3);
    }

    @Override
    public void onDraw(final Canvas canv) {
        if (mCircleArea != null){
            canv.drawCircle(mCircleArea.centerX, mCircleArea.centerY, mCircleArea.radius, mCirclePaint);
            canv.drawCircle(mCircleArea.centerX, mCircleArea.centerY, mCircleArea.radius, strokePaint);
        }
    }

    @Override
    public boolean onTouchEvent(@Nullable final MotionEvent event) {
        boolean handled = false;
        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                DLog("MotionEvent.ACTION_DOWN");

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCircleArea = touchedCircle;

                invalidate();
                handled = true;
                break;
            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCircleArea;  //mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                DLog("MotionEvent.ACTION_UP");
                //invalidate();
                returnPointerToCenter();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                DLog("MotionEvent.ACTION_POINTER_UP");

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                DLog("MotionEvent.ACTION_CANCEL");
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    //**********************************************************************************************
    private void returnPointerToCenter(){
        int centerX = 100;
        int centerY = 100;

        mCircleArea.centerX = centerX;
        mCircleArea.centerY = centerY;

        invalidate();
    }

    /**
     * Search and creates new (if needed) circle based on touch area
     *
     * @param xTouch int x of touch
     * @param yTouch int y of touch
     *
     * @return obtained {@link CircleArea}
     */
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null == touchedCircle) {
            touchedCircle = new CircleArea(xTouch, yTouch, 50/*mRadiusGenerator.nextInt(RADIUS_LIMIT) + RADIUS_LIMIT*/);

            if (flag == false) {
                mCircleArea = touchedCircle;
                flag = true;
            }
        }

        return touchedCircle;
    }

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     *
     * @return {@link CircleArea} touched circle or null if no circle has been touched
     */
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;
        if (mCircleArea != null) {
            if ((mCircleArea.centerX - xTouch) * (mCircleArea.centerX - xTouch) + (mCircleArea.centerY - yTouch) * (mCircleArea.centerY - yTouch) <= mCircleArea.radius * mCircleArea.radius) {
                touched = mCircleArea;
            }
        }

        return touched;
    }


    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }
}