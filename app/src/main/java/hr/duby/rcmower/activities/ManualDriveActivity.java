package hr.duby.rcmower.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.MowerClient;
import hr.duby.rcmower.R;
import hr.duby.rcmower.network.HttpRequestMower;

public class ManualDriveActivity extends AppCompatActivity implements View.OnClickListener {

    //WIDGETS
    private Button btnHome_cm;
    private Button buttonPin11, buttonPin12, buttonPin13;
    private EditText editTextIPAddress, editTextPortNumber;

    //VARs
    private String BASE_URL;

    @Override
    //**********************************************************************************************
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_drive);

        // assign buttons
        btnHome_cm = (Button) findViewById(R.id.btnHome_cm);
        buttonPin11 = (Button)findViewById(R.id.buttonPin11);
        buttonPin12 = (Button)findViewById(R.id.buttonPin12);
        buttonPin13 = (Button)findViewById(R.id.buttonPin13);

        // assign text inputs
        editTextIPAddress = (EditText)findViewById(R.id.editTextIPAddress);
        editTextPortNumber = (EditText)findViewById(R.id.editTextPortNumber);

        // set button listener (this class)
        btnHome_cm.setOnClickListener(this);
        buttonPin11.setOnClickListener(this);
        buttonPin12.setOnClickListener(this);
        buttonPin13.setOnClickListener(this);

    }

    @Override
    //**********************************************************************************************
    protected void onResume() {
        super.onResume();
        keepNavBarHidden();

        BASE_URL = MowerClient.getInstance().getBASE_URL(this);
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

            case R.id.buttonPin11:
                CMD = Const.CMD_FORWARD;
                break;

            case R.id.buttonPin12:
                CMD = Const.CMD_BACK;
                break;

            case R.id.buttonPin13:
                pinNum = "13";
                break;

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
