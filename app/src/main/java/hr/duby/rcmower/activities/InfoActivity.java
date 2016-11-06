package hr.duby.rcmower.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.MowerClient;
import hr.duby.rcmower.R;
import hr.duby.rcmower.util.JSONHelper;


public class InfoActivity extends AppCompatActivity   implements View.OnClickListener {

    //WIDGETS
    private Button btnHCSR04;
    private TextView tvResponse;
    private ProgressBar pbResponse;

    //VARS
    private String BASE_URL;

    @Override
    //**********************************************************************************************
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initGUI();
    }

    @Override
    //**********************************************************************************************
    protected void onResume() {
        super.onResume();
        keepNavBarHidden();

        BASE_URL = MowerClient.getInstance().getBASE_URL(this);
    }

    //**********************************************************************************************
    private void initGUI() {
        btnHCSR04 = (Button) findViewById(R.id.btnHCSR);
        tvResponse = (TextView) findViewById(R.id.tvResponse);
        pbResponse = (ProgressBar) findViewById(R.id.pbResponse);

        //init value
        pbResponse.setMax(Const.HCSR04_MAX);
        pbResponse.setProgress(0);
        tvResponse.setText("");

        btnHCSR04.setOnClickListener(this);
    }

    @Override
    //**********************************************************************************************
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnHCSR:
                startRequest_HCSR04();
                break;

        }
    }

    private void _____________EVENT_HANDLER_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    //**********************************************************************************************
    private void startRequest_HCSR04() {
        MowerClient.getInstance().request_HCSR04(InfoActivity.this, new MowerClient.OnResponse_HCSR04(){

            @Override
            public void onResponse_HCSR04Done(JSONObject response) {
                DLog("onResponse_HCSR04Done result: " + response);
                showResult_HCSR04(response);
            }
        } );

    }

    private void _____________SHOW_RESULT_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    //**********************************************************************************************
    private void showResult_HCSR04(JSONObject response) {
        if (response != null) {
            String statusValue = JSONHelper.getJSONValue(response, "status");
            if (statusValue != null){
                if (statusValue.equalsIgnoreCase("ok")) {
                    String distance = JSONHelper.getJSONValue(response, "distance");
                    if (distance == null){ distance = "-";}
                    final String dist = distance;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResponse.setText(dist);
                            pbResponse.setProgress(Integer.valueOf(dist));
                        }
                    });


                } // statusValue NOT OK
            } // statusValue == NULL
        }  // response == NULL
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
