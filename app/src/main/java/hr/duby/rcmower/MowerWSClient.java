package hr.duby.rcmower;

import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Duby on 22.10.2016..
 */
public class MowerWSClient {

    enum WSStatus {OPEN, CLOSED}

    private static MowerWSClient _this;
    private static Object lock = new Object();

    private WebSocketClient mWebSocketClient = null;
    private WSStatus webSockedStatus = WSStatus.CLOSED;

    public long startTime;


    //**********************************************************************************************
    public static MowerWSClient getInstance() {
        synchronized (lock) {
            if (_this == null) {
                _this = new MowerWSClient();
            }
        }
        return _this;
    }

    private void ____________INTERFACES____________() {
    }
    //*****************************************************************************************************************************************
    //*****************************************************************************************************************************************

    public interface OnWebSocketEvent {
        void onWebSocketEvent(String eventCode, String eventMsg);
    }

    public interface OnResponse_GIO0 {
        void onResponse_GIO0();
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
        if (mWebSocketClient == null) {
            createNewWebSocketClient(uri, listener);
            mWebSocketClient.connect();

        } else {
            if (webSockedStatus == WSStatus.CLOSED) {
                createNewWebSocketClient(uri, listener);
                mWebSocketClient.connect();

            } else {
                DLog("mWebSocketClient is already opened");
                if (listener != null) {
                    listener.onWebSocketEvent("ws_event", "already opened");
                }
            }
        }
    }

    private void createNewWebSocketClient(URI uri, final OnWebSocketEvent listener) {
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
                    listener.onWebSocketEvent("ws_event", "onMessage: " + message);
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

            } catch (Throwable tErr) {
                DLog("Unable to send message! \n" + tErr);
                if (listener != null) {
                    listener.onWebSocketEvent("ws_event", "sendMessage FAILED");
                }
            }
        } else {
            DLog("webSocketClient -> NULL");
            if (listener != null) {
                listener.onWebSocketEvent("ws_event", "WebSocket NOT Connected!");
            }
        }
    }

    private void ____________OTHER____________() {
    }
    //*****************************************************************************************************************************************
    //*****************************************************************************************************************************************

    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }
}
