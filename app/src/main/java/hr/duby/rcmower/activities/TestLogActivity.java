package hr.duby.rcmower.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.R;
import hr.duby.rcmower.broadcast_receivers.WifiReceiver;
import hr.duby.rcmower.util.BasicUtils;


public class TestLogActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //VARS
    private ArrayAdapter<String> m_adapter;
    private ArrayList<String> msgCodeList;
    private boolean isRegisteredWifiReceiver = false;

    //WIDGETS
    private ListView m_list_view;
    private TextView tvConnectionStatus;
    private EditText etInputMsg;
    private Button btnWiFiConnect, btnWebSocketConnect, btnSendMsg, btnClrScr_sta, btnPumpOff;
    private Spinner spinnerCMDList;

    private WebSocketClient mWebSocketClient;

    private final WifiReceiver wifiReceiver = new WifiReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);

            if (intent.getAction().equals("ACTION_WIFI_CONNECTED")) {
                String[] netInfo = intent.getStringArrayExtra("NETINFO");
                updateMsgList("");
                updateMsgList(netInfo[0]);
                updateMsgList(netInfo[1]);
                updateMsgList(netInfo[2]);
                DLog("ACTION_WIFI_CONNECTED -> netInfo: " + netInfo);
            }
        }
    };


    @Override
    //**********************************************************************************************
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_log);

        initWidgets();

        spinnerCMDList = (Spinner) findViewById(R.id.spinnerCMDList);
        spinnerCMDList.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        msgCodeList = new ArrayList<String>();

        m_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgCodeList);
        m_list_view = (ListView) findViewById(R.id.id_list);
        m_list_view.setAdapter(m_adapter);
        m_list_view.setOnItemClickListener(this);

        //WifiReceiver wifiReceiver = new WifiReceiver();

        //WifiReceiver wifiReceiver;

        //registerWiFiBroadcastReceiver();
        //connectWiFi();

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_WIFI_CONNECTED");
        registerReceiver(wifiReceiver, filter);
        isRegisteredWifiReceiver = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isRegisteredWifiReceiver){
            unregisterReceiver(wifiReceiver);
            isRegisteredWifiReceiver = false;
        }
    }

    @Override
    protected void onStop() {
        DLog("onStop");

        super.onStop();
    }

    private void initWidgets(){
        tvConnectionStatus = (TextView) findViewById(R.id.tvConnectionStatus);
        tvConnectionStatus.setText("NOT Connected!");

        etInputMsg = (EditText) findViewById(R.id.etInputMsg_sta);

        spinnerCMDList = (Spinner) findViewById(R.id.spinnerCMDList);

        btnWiFiConnect = (Button) findViewById(R.id.btnWiFiConnect);
        btnWebSocketConnect = (Button) findViewById(R.id.btnWebSocketConnect);
        btnSendMsg = (Button) findViewById(R.id.btnSendMsg_sta);
        btnClrScr_sta = (Button) findViewById(R.id.btnClrScr_sta);
        btnPumpOff = (Button) findViewById(R.id.btnPumpOff_sta);

        btnWiFiConnect.setOnClickListener(this);
        btnWebSocketConnect.setOnClickListener(this);
        btnSendMsg.setOnClickListener(this);
        btnClrScr_sta.setOnClickListener(this);
        btnPumpOff.setOnClickListener(this);
    }

    private void updateMsgList(final String msg_code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgCodeList.add(msg_code);
                m_adapter.notifyDataSetChanged();
            }
        });
    }

    private void clearList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgCodeList.clear();
                m_adapter.notifyDataSetChanged();
            }
        });
    }

    private void registerWiFiBroadcastReceiver(){
        /*
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        */
    }

    //***********************************************************************************************************************************
    private void connectWiFi() {
        DLog("connectWiFi");

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", Const.WIFI_SSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", Const.WIFI_PASS);

        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

    }

    //***********************************************************************************************************************************
    private void connectWebSocket() {
        DLog("connectWebSocket");

        String WSUrl = BasicUtils.getVALUEFromSharedPrefs(this, Const.PREF_WS, "ws://192.168.4.1:81");
        updateMsgList("connecting to: " + WSUrl);
        URI uri;
        try {
            uri = new URI(WSUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        //***********************************************************************************************************************************
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                DLog("Websocket Opened");
                updateMsgList("Websocket Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);

                //DLog("mWebSocketClient.getDraft: " + mWebSocketClient.getDraft());
                //DLog("mWebSocketClient.getURI: " + mWebSocketClient.getURI());
                //DLog("mWebSocketClient.getConnection: " + mWebSocketClient.getConnection());
            }

            @Override
            public void onMessage(String s) {
                DLog("onMessage -> " + s);
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //m_tv_log.setText(m_tv_log.getText() + "\n" + message);
                        updateMsgList(">> " + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                DLog("Websocket Closed: " + s);
                updateMsgList("Websocket Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                DLog("Websocket - Error " + e.getMessage());
                updateMsgList("Websocket - Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage(String msg) {
        DLog("webSocketClient -> sendMessage: " + msg);

        if (mWebSocketClient != null) {
            try {
                mWebSocketClient.send(msg);
                //etInputMsg.setText("");

            }catch (Throwable tErr){
                DLog("Unable to send message! \n" + tErr);
            }
        } else {
            DLog("mWebSocketClient is NULL");
        }
    }


    //***********************************************************************************************************************************
    //***********************************************************************************************************************************
    public void onClick(View view) {
        DLog("onClick");
        String msg_code = "";

        int vId = view.getId();
        //******************************************
        if (vId == R.id.btnWiFiConnect) {
            connectWiFi();
            return;

        }else if (vId == R.id.btnWebSocketConnect){
            eventConnect();
            return;

        }else if (vId == R.id.btnClrScr_sta){
            clearList();
            return;

        } else if (vId == R.id.btnPumpOff_sta) {
            msg_code = "p_off";
        } else if (vId == R.id.btnSendMsg_sta) {
            msg_code = etInputMsg.getText().toString();
        }

        sendMessage(msg_code);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        etInputMsg.setText(m_adapter.getItem(position));

    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();

            String spinnerCMD = parent.getItemAtPosition(pos).toString();
            etInputMsg.setText(spinnerCMD);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    // EVENT HANDLING
    //***********************************************************************************************************************************
    //***********************************************************************************************************************************
    private void eventConnect(){
        updateMsgList("eventConnect");
        connectWebSocket();
    }


    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }

}