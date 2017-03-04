package hr.duby.rcmower;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import hr.duby.rcmower.network.http.AsyncHttpClient;
import hr.duby.rcmower.network.http.AsyncHttpListener;
import hr.duby.rcmower.util.BasicParsing;
import hr.duby.rcmower.util.BasicUtils;

/**
 * Created by Duby on 22.10.2016..
 */
public class MowerWSClient {

    private static MowerWSClient _this;
    private static Object lock = new Object();

    private WebSocketClient mWebSocketClient;

    public long startTime;

    private String IPADDRESS;
    private String PORT;
    private String BASE_URL;

    //**********************************************************************************************
    public static MowerWSClient getInstance() {
        synchronized (lock) {
            if (_this == null) {
                _this = new MowerWSClient();
            }
        }
        return _this;
    }

    private void ____________INTERFACES____________() {}
    //*****************************************************************************************************************************************
    //*****************************************************************************************************************************************

    public interface OnWebSocketEvent {
        void onWebSocketEvent(String eventCode, String eventMsg);
    }

    public interface OnResponse_GIO0 {
        void onResponse_GIO0();
    }

    public interface OnResponse_HCSR04 {
        void onResponse_HCSR04Done(String resTime, JSONObject result);
    }

    //***********************************************************************************************************************************
    public void connectWebSocket(final OnWebSocketEvent listener) {
        URI uri;
        try {
            uri = new URI(Const.WS_URI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onWebSocketEvent("err", "invalid uri");
            }
            return;
        }



        //*************************************************************
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                if (listener != null) {
                    listener.onWebSocketEvent("ws_event", "open");
                }
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);

            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                if (listener != null) {
                    listener.onWebSocketEvent("ws_event", "onMessage");
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                DLog("Websocket Closed: " + s);
                if (listener != null) {
                    listener.onWebSocketEvent("ws_event", "onClose");
                }
            }

            @Override
            public void onError(Exception e) {
                DLog("Websocket - Error " + e.getMessage());
                if (listener != null) {
                    listener.onWebSocketEvent("ws_event", "onError");
                }
            }
        };
        mWebSocketClient.connect();
    }

    //***********************************************************************************************************************************
    public void sendMessage(String msg, final OnWebSocketEvent listener) {
        DLog("webSocketClient -> sendMessage: " + msg);

        if (mWebSocketClient != null) {
            try {
                mWebSocketClient.send(msg);
                if (listener != null) {
                    listener.onWebSocketEvent("ws_event", "sendMessage SUCCESS");
                }

            }catch (Throwable tErr){
                DLog("Unable to send message! \n" + tErr);
                if (listener != null) {
                    listener.onWebSocketEvent("ws_event", "sendMessage FAILED");
                }
            }
        } else {

        }
    }


    private void _____________REST_REQUEST_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************



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
        //BASE_URL = "http://"  + IPADDRESS + ":" + PORT;
        BASE_URL = "http://"  + IPADDRESS;
        return BASE_URL;
    }

    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }
}
