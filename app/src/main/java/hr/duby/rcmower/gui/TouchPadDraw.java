package hr.duby.rcmower.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import hr.duby.rcmower.data.MPoint;


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
    private Paint textPaint;
    private CircleArea mCircleArea;
    private int tpW, tpH, padRadius;
    private int relativeX;
    private int relativeY;
    private float rangeFactor = (1023f / 600f);    //1.7

    //FLAGS
    boolean flag = false;
    private int pRadius = 60;

    public TouchPadDraw(final Context ct) {
        super(ct);
        init(ct);
        //DLog("llTouchPad -> tpW = " + this.getWidth() + ", tpH = " + this.getHeight());
    }

    public MPoint getRelativePoint() {
        return new MPoint(relativeX, relativeY);
    }

    public String getTouchedPointAsCMD(){
        return "" + relativeX + "," + relativeY;
    }


    //**********************************************************************************************
    private void init(final Context ct) {
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.argb(150,200,60,150));
        mCirclePaint.setStrokeWidth(40);
        mCirclePaint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint();
        strokePaint.setColor(Color.argb(100,0,100,0));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
    }

    @Override
    //**********************************************************************************************
    public void onDraw(final Canvas canvas) {
        //DLog("onDraw");

        if (canvas != null){
            tpW = canvas.getWidth();
            tpH = canvas.getHeight();
            padRadius = tpH / 2;
        }
        //DLog("llTouchPad -> tpW = " + tpW + ", tpH = " + tpH);

        if (mCircleArea != null){
            int pPosX = mCircleArea.centerX;
            int pPosY = mCircleArea.centerY;
            int radius = mCircleArea.radius;

            canvas.drawCircle(pPosX, pPosY, pRadius, mCirclePaint);
            canvas.drawCircle(pPosX, pPosY, pRadius, strokePaint);

            int relX = Math.round((float)(pPosX-padRadius) * rangeFactor);
            int relY = Math.round(((float)(pPosY-padRadius)*-1f) * rangeFactor);
            // axes rotation 45°
            // cosiFi = sin45 = cos45 = 0.70710678
            final float cosiFi = 0.70710678f;
            relativeX = Math.round(relX*cosiFi + relY*cosiFi);
            relativeY = Math.round(-1*relX*cosiFi + relY*cosiFi);
            String posStr = "X: " + relativeX + "  Y: " + relativeY;
            canvas.drawText(posStr, tpW-280, tpH-50, textPaint);
        }
    }

    @Override
    //**********************************************************************************************
    public boolean onTouchEvent(@Nullable final MotionEvent event) {
        boolean handled = false;
        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        //int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //DLog("MotionEvent.ACTION_DOWN");
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
                //DLog("MotionEvent.ACTION_MOVE");

                xTouch = (int) event.getX();
                yTouch = (int) event.getY();
                int radius = (int) Math.sqrt(Math.pow((xTouch-600), 2) + Math.pow((yTouch-600), 2));

                if (mCircleArea != null) {
                    mCircleArea.centerX = xTouch;
                    mCircleArea.centerY = yTouch;
                    mCircleArea.radius = radius;
                }

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                setCenterPoint();
                invalidate();
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
    private void setCenterPoint(){
        mCircleArea.centerX = tpW / 2;
        mCircleArea.centerY = tpH / 2;
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
            touchedCircle = new CircleArea(xTouch, yTouch, pRadius);

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