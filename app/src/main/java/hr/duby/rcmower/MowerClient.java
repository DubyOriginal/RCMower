package hr.duby.rcmower;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import hr.duby.rcmower.network.AsyncHttpClient;
import hr.duby.rcmower.network.AsyncHttpListener;
import hr.duby.rcmower.network.HttpRequestMower;
import hr.duby.rcmower.util.BasicParsing;
import hr.duby.rcmower.util.BasicUtils;

/**
 * Created by Duby on 22.10.2016..
 */
public class MowerClient {

    private static MowerClient _this;
    private static Object lock = new Object();

    public long startTime;

    private String IPADDRESS;
    private String PORT;
    private String BASE_URL;


    private void ____________INTERFACES____________() {}
    //*****************************************************************************************************************************************
    //*****************************************************************************************************************************************

    public interface OnResponse_GIO0 {
        void onResponse_GIO0();
    }

    public interface OnResponse_HCSR04 {
        void onResponse_HCSR04Done(JSONObject result);
    }

    //**********************************************************************************************
    public static MowerClient getInstance() {
        synchronized (lock) {
            if (_this == null) {
                _this = new MowerClient();
            }
        }
        return _this;
    }

    private void _____________REST_REQUEST_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    //**********************************************************************************************
    public void request_HCSR04(Context context, final OnResponse_HCSR04 listener) {
        DLog("Sending request_HCSR04....");
        startTime = System.currentTimeMillis();

        String reqURL = getBASE_URL(context) + Const.SENSOR_HCSR04;
        DLog("reqURL: " + reqURL);

        //new HttpRequestMower(reqURL).execute();

        new AsyncHttpClient().get(reqURL, new AsyncHttpListener() {
            @Override
            public void onGetDone(JSONObject result) {
                String stopTime = BasicParsing.getResponseTimeForStartTime(startTime);
                DLog("RESPONSE (in: " + stopTime + ") -> request_HCSR04: " + result.toString());
                if (listener != null) {
                    listener.onResponse_HCSR04Done(result);
                }
            }

            @Override
            public void onPostDone(JSONObject object) {}

            @Override
            public void onError(Exception e) {
                String stopTime = BasicParsing.getResponseTimeForStartTime(startTime);
                DLog("RESPONSE (in: " + stopTime + ") -> request_HCSR04: ERROR");
                DLog("RESPONSE -> request_HCSR04: ERROR -> " + e);
                if (listener != null) {
                    listener.onResponse_HCSR04Done(null);
                }
            }
        });
    }

    //**********************************************************************************************
    public void request_GIO0(D digital, final OnResponse_GIO0 listener) {
        DLog("Sending request for STATUS request_GIO0....");
        startTime = System.currentTimeMillis();

        String reqURL = "";  //(digital == D.LOW) ? Const.CMD_PIN0_L : Const.CMD_PIN0_H;
        DLog("reqURL: " + reqURL);

        new AsyncHttpClient().get(reqURL, new AsyncHttpListener() {
            @Override
            public void onGetDone(JSONObject object) {
                String stopTime = BasicParsing.getResponseTimeForStartTime(startTime);
                DLog("RESPONSE (in: " + stopTime + ") -> request_GIO0: " + object.toString());
                if (listener != null) {
                    listener.onResponse_GIO0();
                }
            }

            @Override
            public void onPostDone(JSONObject object) {}

            @Override
            public void onError(Exception e) {
                String stopTime = BasicParsing.getResponseTimeForStartTime(startTime);
                DLog("RESPONSE (in: " + stopTime + ") -> request_GIO0: ERROR");
                DLog("RESPONSE -> request_GIO0: ERROR");
                if (listener != null) {
                    listener.onResponse_GIO0();
                }
            }
        });
    }

    private void ____________OTHER____________() {}
    //*****************************************************************************************************************************************
    //*****************************************************************************************************************************************

    //**********************************************************************************************
    public String getBASE_URL(Context context){
        IPADDRESS = BasicUtils.getVALUEFromSharedPrefs(context, Const.PREF_IP, "192.168.1.24");
        PORT = BasicUtils.getVALUEFromSharedPrefs(context, Const.PREF_PORT, "80");
        BASE_URL = "http://"  + IPADDRESS + ":" + PORT;
        return BASE_URL;
    }

    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }
}
