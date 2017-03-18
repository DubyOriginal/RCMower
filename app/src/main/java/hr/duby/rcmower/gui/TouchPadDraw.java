package hr.duby.rcmower.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import hr.duby.rcmower.data.MPoint;
import hr.duby.rcmower.util.DVector;
import hr.duby.rcmower.util.MathUtils;


/**
 * Created by Duby on 14.11.2016..
 *
 * DESCRIPTION:
 *  - green touch area circle is RelativeLayout background Shape (xml)
 *  -
 *
 */



public class TouchPadDraw extends View {

    //VARS
    private Paint mCursorPaint;
    private Paint textPaint;
    private CursorP mCursor;
    private int canvasW, canvasH;
    private float rangeFactor = (1023f / 600f);    //1.7

    //CONST
    private final int mCursorRadius = 70;

    //FLAGS
    boolean flag = false;


    public TouchPadDraw(final Context ct) {
        super(ct);
        init();
        //DLog("llTouchPad -> canvasW = " + this.getWidth() + ", canvasH = " + this.getHeight());
    }

    public MPoint getRelativePoint() {
        return new MPoint(mCursor.refX, mCursor.refY);
    }

    public String getTouchedPointAsCMD(){
        return "" + mCursor.refX + "," + mCursor.refY;
    }


    //**********************************************************************************************
    private void init() {
        //vars
        mCursor = new CursorP();

        //canvas params
        mCursorPaint = new Paint();
        mCursorPaint.setColor(Color.argb(150,200,60,150));   //150,200,60,150
        mCursorPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        canvasW = View.MeasureSpec.getSize(widthMeasureSpec);
        canvasH = View.MeasureSpec.getSize(heightMeasureSpec);
        DLog("onMeasure -> canvasW: " + canvasW + ", canvasH: " + canvasH);
        setMeasuredDimension(canvasW, canvasH);

        mCursor.setCursorBound(canvasW, canvasH);
    }

    @Override
    //**********************************************************************************************
    public void onDraw(final Canvas canvas) {
        //DLog("onDraw");

        //DLog("llTouchPad -> canvasW = " + canvasW + ", canvasH = " + canvasH);

        if (mCursor != null){
            canvas.drawCircle(mCursor.pX, mCursor.pY, mCursorRadius, mCursorPaint);

            String posStr = "X: " + mCursor.refX + "  Y: " + mCursor.refY + ", " + mCursor.dVector.toString();
            canvas.drawText(posStr, canvasW - 900, canvasH -50, textPaint);
        }
    }

    @Override
    //**********************************************************************************************
    public boolean onTouchEvent(@Nullable final MotionEvent event) {
        boolean handled = false;
        int xTouch;
        int yTouch;

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //DLog("MotionEvent.ACTION_DOWN");
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);
                mCursor.setCursorPosition(xTouch, yTouch);
                //DLog("X: " + xTouch + ", Y: " + yTouch);
                invalidate();
                handled = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //DLog("MotionEvent.ACTION_MOVE");
                xTouch = (int) event.getX();
                yTouch = (int) event.getY();

                if (mCursor != null) {
                    mCursor.setCursorPosition(xTouch, yTouch);
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

    private void setCenterPoint() {
        mCursor.pX = mCursor.centerX;
        mCursor.pY = mCursor.centerY;

        mCursor.refX = 0;
        mCursor.refY = 0;
    }

    //**********************************************************************************************

    private class CursorP {
        private int centDistance;
        private int maxDistance = 600;  //default value
        private int pX;
        private int pY;
        private int centerX;
        private int centerY;
        private int refX;
        private int refY;
        private DVector dVector;

        private CursorP() {
            dVector = new DVector();  //initialize cursor vector
        }

        private CursorP(int pX, int pY, int centDistance) {
            this.centDistance = centDistance;
            this.pX = pX;
            this.pY = pY;
        }

        public void setCursorPosition(int posX, int posY){
            //DLog("setCursorPosition -> X: " + posX + ", Y: " + posY);

            refX = posX - centerX;
            refY = posY - centerY;
            int distance;   //distance of touch point from center
            distance = (int)Math.sqrt(Math.pow(refX,2) + Math.pow(refY, 2));
            if (distance <= maxDistance) {
                pX = posX;
                pY = posY;

            }else {
                DVector bigVector = new DVector().createVectorFromPoint(refX, refY);
                int angle = bigVector.getDirection();
                Point point = MathUtils.getPointFromRadiusAndAngle(maxDistance, angle);
                pX = point.x;
                pY = point.y;

            }
            dVector.createVectorFromPoint(refX, refY);

            DLog(this.toStringFull());

        }

        //total area iv wich cursor can move / appear
        private void setCursorBound(int canvasW, int canvasH){
            int smallerDim;
            if (canvasW < canvasH){
                smallerDim = canvasW;
            }else{
                smallerDim = canvasH;
            }
            centerX = canvasW / 2;
            centerY = canvasH / 2;
            maxDistance = smallerDim / 2;
        }

        @Override
        public String toString() {
            return "CursorP[" + pX + ", " + pY + ", " + centDistance + "]";
        }

        public String toStringFull() {
            return "CursorP: pX: " + pX + ", pY: "  + pY + ", " + dVector.toString() + ", refX: " + refX + ", refY: " + refY;
        }

    }


    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }
}