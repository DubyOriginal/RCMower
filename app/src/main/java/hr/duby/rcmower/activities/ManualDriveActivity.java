package hr.duby.rcmower.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.MowerWSClient;
import hr.duby.rcmower.R;
import hr.duby.rcmower.broadcast_receivers.WifiReceiver;
import hr.duby.rcmower.gui.TouchPadDraw;


public class ManualDriveActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //WIDGETS
    private Button btnHome, btnTestWS, btnOpenWS;
    private TextView tvStatus;
    private RelativeLayout rlTouchPad;
    private Switch switchRun;

    //VARs
    private TouchPadDraw touchPadDraw;
    private boolean RUNNING = false;
    private boolean isRegisteredWifiReceiver = false;
    private final int REFRESH_RATE = 700;
    private Handler mHandler = new Handler();


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




    @Override
    //**********************************************************************************************
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_drive);

        // assign buttons
        btnHome = (Button) findViewById(R.id.btnHome_mda);
        btnTestWS = (Button) findViewById(R.id.btnTestWS_mda);
        btnOpenWS = (Button) findViewById(R.id.btnOpenWS_mda);
        tvStatus = (TextView) findViewById(R.id.tvStatus_mda);
        switchRun = (Switch) findViewById(R.id.switchRun_mda);
        rlTouchPad = (RelativeLayout) findViewById(R.id.llTouchPad);
        touchPadDraw = new TouchPadDraw(this);
        rlTouchPad.addView(touchPadDraw);

        // set button listener (this class)
        btnHome.setOnClickListener(this);
        btnTestWS.setOnClickListener(this);
        btnOpenWS.setOnClickListener(this);
        switchRun.setOnCheckedChangeListener(this);

        tvStatus.setText("");

    }

    @Override
    protected void onResume() {
        super.onResume();

        keepNavBarHidden();

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

    //**********************************************************************************************
    private void startRequest_Drive() {
        if (RUNNING){
            DLog("-------------------------------------------------------");
            String prepareCMD = Const.D_MANUAL + "," +  touchPadDraw.getTouchedPointAsCMD();
            MowerWSClient.getInstance().sendMessage(prepareCMD, webSocketListener);
            DLog("Msg to SEND: " + prepareCMD);
        }
    }

    private void _____________DRIVING_TIMER_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    //***********************************************
    private Runnable startTimer = new Runnable() {
        public void run() {
            //elapsedTime = System.currentTimeMillis() - startTime;
            startRequest_Drive();  //will be executed every: REFRESH_RATE ms
            mHandler.postDelayed(startTimer, REFRESH_RATE);
        }
    };

    public void startTimer(){
        //restart time
        //startTime = System.currentTimeMillis();
        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
    }

    public void stopTimer(){
        mHandler.removeCallbacks(startTimer);
    }

    private void _____________EVENT_HANDLING_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    @Override
    //**********************************************************************************************
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DLog("onCheckedChanged");
        if (buttonView.getId() == R.id.switchRun_mda){
            if (buttonView != null && isChecked){
                RUNNING = true;
                startTimer();
            }else{
                stopTimer();
                RUNNING = false;
            }
        }
    }

    @Override
    //**********************************************************************************************
    public void onClick(View view) {
        String pinNum = "";

        String CMD = "";
        switch (view.getId()){
            case R.id.btnHome_mda:
                gotoHomeActivity();
                return;

            case R.id.btnTestWS_mda:
                MowerWSClient.getInstance().sendMessage(Const.CMD_TEST, webSocketListener);
                return;
            case R.id.btnOpenWS_mda:
                MowerWSClient.getInstance().connectWebSocket(webSocketListener);
                return;
            default:
                DLog("unknown button");
                break;
        }
    }

    private void _____________OTHER_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    //**********************************************************************************************
    private void gotoHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        if (intent != null){
            startActivity(intent);
            finish();
        }
    }

    //**********************************************************************************************
    private void keepNavBarHidden() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideNavBar();
            }
        }, 500);
    }

    //**********************************************************************************************
    private void hideNavBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    //**********************************************************************************************
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }


}
