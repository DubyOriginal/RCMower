package hr.duby.rcmower.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.MowerWSClient;
import hr.duby.rcmower.R;
import hr.duby.rcmower.broadcast_receivers.WifiReceiver;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    //WIDGETS
    private Button btnCtrlManual;
    private Button btnCtrlSemiAutomatic;
    private Button btnProgram;
    private Button btnInfo;
    private Button btnSettings;
    private Button btnWiFiConnect, btnWSConnect, btnWSTest;
    private ListView lvStatus;

    //VARS
    private ArrayAdapter<String> m_adapter;
    private ArrayList<String> msgCodeList;
    private boolean isRegisteredWifiReceiver = false;

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

    private MowerWSClient.OnWebSocketEvent webSocketListener = new MowerWSClient.OnWebSocketEvent() {
        @Override
        public void onWebSocketEvent(String eventCode, String eventMsg) {
            //final String responseMsg = "eventCode: " + eventCode + ", eventMsg: " + eventMsg;
            final String responseMsg = eventCode + " - " + eventMsg;
            DLog(responseMsg);
            updateMsgList(responseMsg);
        }
    };

    private void _____________ACTIVITY_METHODS_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DLog("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initWidgets();

        msgCodeList = new ArrayList<String>();
        //m_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgCodeList);
        m_adapter = new ArrayAdapter<String>(this, R.layout.row_status_white, R.id.tvRowStatus, msgCodeList);
        lvStatus.setAdapter(m_adapter);
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

    private void initWidgets() {
        lvStatus = (ListView) findViewById(R.id.lvStatus_ha);

        btnCtrlManual = (Button) findViewById(R.id.btnCtrlManual_ha);
        btnCtrlSemiAutomatic = (Button) findViewById(R.id.btnCtrlSemiAutomatic_ha);
        btnProgram = (Button) findViewById(R.id.btnProgram_ha);
        btnInfo = (Button) findViewById(R.id.btnInfo_ha);
        btnSettings = (Button) findViewById(R.id.btnSettings_ha);
        btnWiFiConnect = (Button) findViewById(R.id.btnWiFiConnect_ha);
        btnWSConnect = (Button) findViewById(R.id.btnWSConnect_ha);
        btnWSTest = (Button) findViewById(R.id.btnWSTest_ha);
        lvStatus = (ListView) findViewById(R.id.lvStatus_ha);

        btnCtrlManual.setOnClickListener(this);
        btnCtrlSemiAutomatic.setOnClickListener(this);
        btnProgram.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnWiFiConnect.setOnClickListener(this);
        btnWSConnect.setOnClickListener(this);
        btnWSTest.setOnClickListener(this);
    }

    private void _____________HANDLING_EVENT_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    @Override
    //**********************************************************************************************
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btnCtrlManual_ha:
                intent = new Intent(this, ManualDriveActivity.class);
                break;
            case R.id.btnCtrlSemiAutomatic_ha:
                intent = new Intent(this, SmartDriveActivity.class);
                break;
            case R.id.btnProgram_ha:
                intent = new Intent(this, TestLogActivity.class);
                break;
            case R.id.btnInfo_ha:
                intent = new Intent(this, InfoActivity.class);
                break;
            case R.id.btnSettings_ha:
                intent = new Intent(this, SetupActivity.class);
                break;
            case R.id.btnWiFiConnect_ha:
                updateMsgList("WiFi connecting...");
                connectWiFi();
                return;
            case R.id.btnWSConnect_ha:
                updateMsgList("WS connecting...");
                MowerWSClient.getInstance().connectWebSocket(webSocketListener);
                return;
            case R.id.btnWSTest_ha:
                updateMsgList("sendMessage -> " + Const.CMD_TEST);
                MowerWSClient.getInstance().sendMessage(Const.CMD_TEST, webSocketListener);
                return;
            default:
                DLog("unknown button");
                break;
        }
        if (intent != null){
            startActivity(intent);
        }
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

    private void updateMsgList(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgCodeList.add(msg);
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


    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);

    }


}
