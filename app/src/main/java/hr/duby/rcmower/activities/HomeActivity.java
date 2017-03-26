package hr.duby.rcmower.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private TextView tvStatus;

    private boolean isRegisteredWifiReceiver = false;

    private final WifiReceiver wifiReceiver = new WifiReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);

            if (intent.getAction().equals("ACTION_WIFI_CONNECTED")) {
                String[] netInfo = intent.getStringArrayExtra("NETINFO");
                DLog("ACTION_WIFI_CONNECTED -> netInfo: " + netInfo);
            }
        }
    };

    private MowerWSClient.OnWebSocketEvent webSocketListener = new MowerWSClient.OnWebSocketEvent() {
        @Override
        public void onWebSocketEvent(String eventCode, String eventMsg) {
            final String responseMsg = "eventCode: " + eventCode + ", eventMsg: " + eventMsg;
            DLog(responseMsg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(responseMsg);
                }
            });
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

        btnCtrlManual = (Button) findViewById(R.id.btnCtrlManual);
        btnCtrlSemiAutomatic = (Button) findViewById(R.id.btnCtrlSemiAutomatic);
        btnProgram = (Button) findViewById(R.id.btnProgram);
        btnInfo = (Button) findViewById(R.id.btnInfo);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnWiFiConnect = (Button) findViewById(R.id.btnSettings);
        btnWSConnect = (Button) findViewById(R.id.btnSettings);
        btnWSTest = (Button) findViewById(R.id.btnSettings);
        tvStatus = (TextView) findViewById(R.id.tvStatus_ha);

        btnCtrlManual.setOnClickListener(this);
        btnCtrlSemiAutomatic.setOnClickListener(this);
        btnProgram.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
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
    //**********************************************************************************************
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.btnCtrlManual:
                intent = new Intent(this, ManualDriveActivity.class);
                break;
            case R.id.btnCtrlSemiAutomatic:
                intent = new Intent(this, SmartDriveActivity.class);
                break;
            case R.id.btnProgram:
                intent = new Intent(this, TestLogActivity.class);
                break;
            case R.id.btnInfo:
                intent = new Intent(this, InfoActivity.class);
                break;
            case R.id.btnSettings:
                intent = new Intent(this, SetupActivity.class);
                break;
            case R.id.btnTestWS_ha:
                MowerWSClient.getInstance().sendMessage(Const.CMD_TEST, webSocketListener);
                return;
            case R.id.btnOpenWS_ha:
                MowerWSClient.getInstance().connectWebSocket(webSocketListener);
                return;
            default:
                DLog("unknown button");
                break;
        }
        if (intent != null){
            startActivity(intent);
        }
    }


    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);

    }


}
