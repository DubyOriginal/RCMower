package hr.duby.rcmower.util;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by Duby on 18.3.2017..
 */

public class DVector{
    private int length = 0;
    private int direction = 0;

    public DVector (){}
    public DVector (int length, int direction){}

    public DVector createVectorFromPoint(int pX, int pY){
        length = (int)Math.sqrt(Math.pow(pX, 2) + Math.pow(pY, 2));
        Point rotatedPoint = MathUtils.rotateAxesForDegAngle(pX, pY, 180);
        direction = (int) Math.toDegrees(Math.atan2(rotatedPoint.y, rotatedPoint.x));
        //DVector dvect = new DVector(length, direction);
        //Log.d("DTag", "dvect: " + dvect.toString() + ", len: " + length + ", dir: " + direction);
        return this;
    }

    //canvas 1200,1200 -> refX=refY=600;
    public DVector createVectorFromReferencePoint(int refX, int refY, int pX, int pY){
        int dX = (pX - refX);
        int dY = (pY - refY);
        length = (int)Math.sqrt(Math.pow(dX,2) + Math.pow(dY,2));
        //Point rotatedPoint = MathUtils.rotateAxesForDegAngle(dX, dY, 180);
        //direction = (int)Math.toDegrees(Math.atan2(rotatedPoint.x, rotatedPoint.y));
        direction = (int)Math.toDegrees(Math.atan2(dY, dX));
        return this;
    }



    //GETTERs & SETTERs
    //**********************************************************************************************
    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    //OTHER
    //**********************************************************************************************
    @Override
    public String toString() {
        return "DVector[" + length + ", " + direction + "]";
    }
}