package hr.duby.rcmower;

import android.app.Application;
import android.util.Log;

import hr.duby.rcmower.broadcast_receivers.WifiReceiver;

/**
 * Created by Duby on 6.11.2016..
 */

public class MowerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DTag", "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        Log.d("DTag", "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        Log.d("DTag", "STATUS: VERSION_NAME: " + BuildConfig.VERSION_NAME + ", DEBUG: " + BuildConfig.DEBUG);


    }
}


