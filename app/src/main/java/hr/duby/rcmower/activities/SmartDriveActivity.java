package hr.duby.rcmower.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.MowerHTTPClient;
import hr.duby.rcmower.MowerWSClient;
import hr.duby.rcmower.R;
import hr.duby.rcmower.broadcast_receivers.WifiReceiver;
import hr.duby.rcmower.gui.VerticalSeekBar;
import hr.duby.rcmower.network.http.HttpRequestMower;
import hr.duby.rcmower.util.DVector;

public class SmartDriveActivity extends AppCompatActivity implements View.OnClickListener {

    //WIDGETS
    private Button btnHome_csaa;
    private VerticalSeekBar vsb_speed;
    private Button btnForward, btnBack, btnLeft, btnRight, btnStop;
    private TextView tvStatus;

    //VARS
    private int mSpeed = Const.SPEED;  //initial value -> pwm value (0-255)
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

    private void _____________ACTIVITY_METHODS_____________() {
    }
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    @Override
    //**********************************************************************************************
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_smart_drive);

        btnHome_csaa = (Button) findViewById(R.id.btnHome_sda);
        btnForward = (Button) findViewById(R.id.btnForward_sda);
        btnBack = (Button) findViewById(R.id.btnBack_sda);
        btnStop = (Button) findViewById(R.id.btnStop_sda);
        btnLeft = (Button) findViewById(R.id.btnLeft_sda);
        btnRight = (Button) findViewById(R.id.btnRight_sda);
        vsb_speed = (VerticalSeekBar) findViewById(R.id.vsb_speed);
        tvStatus = (TextView) findViewById(R.id.tvStatus_sda);

        btnHome_csaa.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        mOnSeekBarChangeListener();

        //***************************************
        vsb_speed.setMax(999);  //pwm (0-255)
        vsb_speed.setProgress(mSpeed);
    }


    @Override
    //**********************************************************************************************
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

        if (isRegisteredWifiReceiver) {
            unregisterReceiver(wifiReceiver);
            isRegisteredWifiReceiver = false;
        }
    }

    private void _____________EVENTS_HANDLING_____________() {
    }
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    @Override
    //**********************************************************************************************
    public void onClick(View view) {

        String driveParams = "";
        switch (view.getId()) {
            case R.id.btnHome_sda:
                gotoHomeActivity();
                break;

            case R.id.btnForward_sda:
                driveParams = prepareFORWARDParams();
                break;

            case R.id.btnBack_sda:
                driveParams = prepareBACKParams();
                break;

            case R.id.btnLeft_sda:
                driveParams = prepareRLEFTParams();
                break;

            case R.id.btnRight_sda:
                driveParams = prepareRRIGHTParams();
                break;

            case R.id.btnStop_sda:
                driveParams = prepareSTOPParams();
                break;

            default:
                DLog("unknown button");
                break;
        }


        if (driveParams != null && driveParams.length() > 0) {
            startRequest_Drive(driveParams);
        }

    }

    //**********************************************************************************************
    private void mOnSeekBarChangeListener() {
        vsb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSpeed = seekBar.getProgress();
                if (mSpeed < 100) mSpeed = 100;
                DLog("seekBar.position: " + mSpeed);
            }
        });
    }


    private void _____________DRIVING_METHODS_____________() {
    }
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    private void startRequest_Drive(String driveParams) {

        String prepareCMD = Const.D_SMART + "," + driveParams;
        MowerWSClient.getInstance().sendMessage(prepareCMD, webSocketListener);
        DLog("Msg to SEND: " + prepareCMD);

    }

    //**************************************************************************************
    private String prepareFORWARDParams() {
        String motASpeed = String.valueOf(mSpeed);
        String motBSpeed = String.valueOf(mSpeed);
        return motASpeed + "," + motBSpeed;
    }

    private String prepareBACKParams() {
        String motASpeed = "-" + String.valueOf(mSpeed);
        String motBSpeed = "-" + String.valueOf(mSpeed);
        return motASpeed + "," + motBSpeed;
    }

    private String prepareRLEFTParams() {
        String motASpeed = "-" + String.valueOf(mSpeed);
        String motBSpeed = "-" + String.valueOf(mSpeed);
        return motASpeed + "," + motBSpeed;
    }

    private String prepareRRIGHTParams() {
        return "";
    }

    private String prepareSTOPParams() {
        return "";
    }


    private void _____________OTHER_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************


    //**********************************************************************************************
    private void gotoHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        if (intent != null) {
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
