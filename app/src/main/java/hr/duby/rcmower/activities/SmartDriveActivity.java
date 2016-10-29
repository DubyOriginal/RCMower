package hr.duby.rcmower.activities;

import android.content.Intent;
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

import hr.duby.rcmower.Const;
import hr.duby.rcmower.MowerClient;
import hr.duby.rcmower.R;
import hr.duby.rcmower.gui.VerticalSeekBar;
import hr.duby.rcmower.network.HttpRequestMower;
import hr.duby.rcmower.util.BasicUtils;

public class SmartDriveActivity extends AppCompatActivity  implements View.OnClickListener {

    //WIDGETS
    private Button btnHome_csaa;
    private VerticalSeekBar vsb_speed;
    private Button btnForward, btnBack, btnLeft, btnRight, btnStop;

    //VARS
    private int mSpeed = Const.SPEED;  //initial value -> pwm value (0-255)
    private String BASE_URL;

    private void _____________ACTIVITY_METHODS_____________() {}
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

        BASE_URL = MowerClient.getInstance().getBASE_URL(this);
    }


    private void _____________EVENTS_HANDLING_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    @Override
    //**********************************************************************************************
    public void onClick(View view) {

        String CMD = "";
        switch (view.getId()){
            case R.id.btnHome_sda:
                gotoHomeActivity();
                break;

            case R.id.btnForward_sda:
                CMD = Const.CMD_FORWARD;
                break;

            case R.id.btnBack_sda:
                CMD = Const.CMD_BACK;
                break;

            case R.id.btnLeft_sda:
                CMD = Const.CMD_RLEFT;
                break;

            case R.id.btnRight_sda:
                CMD = Const.CMD_RRIGHT;
                break;

            case R.id.btnStop_sda:
                CMD = Const.CMD_STOP;
                break;

            default:
                DLog("unknown button");
                break;
        }

        String reqURL = BASE_URL + CMD;
        DLog(reqURL);

        if (CMD != null && CMD.length() > 0) {
            new HttpRequestMower(reqURL).execute();
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

                String reqURL = BASE_URL + Const.CMD_SPEED + mSpeed;
                DLog(reqURL);

                new HttpRequestMower(reqURL).execute();

            }
        });
    }



    private void _____________OTHER_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************


    //**********************************************************************************************
    private void gotoHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        if (intent != null){
            startActivity(intent);
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
