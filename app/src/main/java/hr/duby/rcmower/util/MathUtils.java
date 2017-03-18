package hr.duby.rcmower.util;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by dvrbancic on 22/08/16.
 */
public class MathUtils {

    // CONSTANTS
    //**********************************************************************************************
    // cosiFi = sin45 = cos45 = 0.70710678
    public static final float cosiFi = 0.70710678f;

    // CALCULATIONS
    //**********************************************************************************************
    public static Point getPointFromRadiusAndAngle(int radius, int angle){
        //formula: x = x0 + r*cos(t),  y = y0 + r*sin(t)
        int x =  (int) (radius * Math.cos(angle));
        int y =  (int) (radius * Math.sin(angle));
        return new Point(x,y);
    }

    //rotate point T(x,y) for angleDeg to T'(x, y)
    public static Point rotateAxesForDegAngle(int relX, int relY, int angleDeg){
        // axes rotation 45°
        // cosiFi = sin45 = cos45 = 0.70710678

        double angleRad = Math.toRadians(angleDeg);
        float cosAngle = (float) Math.cos(angleRad);
        float sinAngle = (float) Math.sin(angleRad);

        int rotatedX = Math.round(relX*cosAngle - relY*sinAngle);
        int rotatedY = Math.round(-1*relX*sinAngle + relY*cosAngle);
        //Log.d("DTag", "MathUtils: angle: " + angleDeg +", BEFORE [" + relX + ", " + relY +"], AFTER [" + rotatedX + ", " + rotatedY + "]");
        return new Point(rotatedX, rotatedY);
    }

    //convert [+180,-180] to [0,360]
    public static int degreMaping180To360(int angleDeg){
        if(angleDeg < 0){
            angleDeg += 360;
        }
        return angleDeg;
    }



}
