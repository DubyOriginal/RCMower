package hr.duby.rcmower.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.MowerClient;
import hr.duby.rcmower.R;
import hr.duby.rcmower.data.MPoint;
import hr.duby.rcmower.gui.TouchPadDraw;
import hr.duby.rcmower.network.HttpRequestMower;


public class ManualDriveActivity extends AppCompatActivity implements View.OnClickListener {

    //WIDGETS
    private Button btnHome_cm;
    private RelativeLayout rlTouchPad;

    //VARs
    private String BASE_URL;
    private TouchPadDraw touchPadDraw;

    @Override
    //**********************************************************************************************
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_drive);

        // assign buttons
        btnHome_cm = (Button) findViewById(R.id.btnHome_cm);
        rlTouchPad = (RelativeLayout) findViewById(R.id.llTouchPad);
        touchPadDraw = new TouchPadDraw(this);
        rlTouchPad.addView(touchPadDraw);

        // set button listener (this class)
        btnHome_cm.setOnClickListener(this);

    }

    @Override
    //**********************************************************************************************
    protected void onResume() {
        super.onResume();
        keepNavBarHidden();

        BASE_URL = MowerClient.getInstance().getBASE_URL(this);

        startRequest_Drive();

    }

    //**********************************************************************************************
    private void startRequest_Drive() {
        MowerClient.getInstance().request_DRIVE(ManualDriveActivity.this, touchPadDraw.getRelativePoint(), new MowerClient.OnResponse_Drive() {
            @Override
            public void onResponse_DriveDone(String resTime, JSONObject result) {
                startRequest_Drive();
            }
        });

    }

    private void _____________EVENT_HANDLING_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    @Override
    //**********************************************************************************************
    public void onClick(View view) {
        String pinNum = "";

        String CMD = "";
        switch (view.getId()){
            case R.id.btnHome_cm:
                gotoHomeActivity();
                return;

            default:
                DLog("unknown button");
                break;
        }

        /*
        MowerClient.getInstance().request_GIO0(digital, new MowerClient.OnResponse_GIO0(){
            @Override
            public void onResponse_GIO0() {

            }
        });*/

        String reqURL = BASE_URL + CMD;
        DLog(reqURL);

        // execute HTTP request
         new HttpRequestMower(reqURL).execute();

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
