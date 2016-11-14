package hr.duby.rcmower.data;

/**
 * Created by Duby on 14.11.2016..
 */

public class MPoint {
    private int X;
    private int Y;

    public MPoint() {}

    public MPoint(int x, int y) {
        X = x;
        Y = y;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }
}
