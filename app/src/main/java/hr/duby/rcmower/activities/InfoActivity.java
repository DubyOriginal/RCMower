package hr.duby.rcmower.activities;

import android.content.Intent;
import android.os.Bundle;
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
    private Button btnHCSR04, btnReadAnalog;
    private TextView tvResponseValue, tvResponseTime;
    private ProgressBar pbResponse, pbInProgress;

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

        BASE_URL = MowerClient.getInstance().getBASE_URL(this);
    }

    //**********************************************************************************************
    private void initGUI() {
        btnHCSR04 = (Button) findViewById(R.id.btnHCSR);
        btnReadAnalog = (Button) findViewById(R.id.btnReadAnalog);
        tvResponseValue = (TextView) findViewById(R.id.tvResponseValue);
        tvResponseTime = (TextView) findViewById(R.id.tvResponseTime);
        pbResponse = (ProgressBar) findViewById(R.id.pbResponse);
        pbInProgress = (ProgressBar) findViewById(R.id.pbInProgress);

        //init value
        pbResponse.setProgress(0);
        tvResponseValue.setText("");

        pbInProgress.setVisibility(View.INVISIBLE);

        btnHCSR04.setOnClickListener(this);
        btnReadAnalog.setOnClickListener(this);
    }

    @Override
    //**********************************************************************************************
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnHCSR:
                startRequest_HCSR04();
                break;

            case R.id.btnReadAnalog:
                startRequest_ReadAnalog();
                break;
        }
    }

    private void _____________EVENT_HANDLER_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    //**********************************************************************************************
    private void startRequest_HCSR04() {
        pbResponse.setMax(Const.HCSR04_MAX);
        tvResponseTime.setText("");
        pbInProgress.setVisibility(View.VISIBLE);
        MowerClient.getInstance().request_HCSR04(InfoActivity.this, new MowerClient.OnResponse_HCSR04(){
            @Override
            public void onResponse_HCSR04Done(String resTime, JSONObject response) {
                DLog("onResponse_HCSR04Done result: " + response);
                pbInProgress.setVisibility(View.INVISIBLE);
                tvResponseTime.setText(resTime);
                showResult_HCSR04(response);
            }
        } );

    }

    //**********************************************************************************************
    private void startRequest_ReadAnalog() {
        pbResponse.setMax(Const.ANALOG_MAX);
        tvResponseTime.setText("");
        pbInProgress.setVisibility(View.VISIBLE);
        MowerClient.getInstance().request_ReadAnalog(InfoActivity.this, new MowerClient.OnResponse_ReadAnalog(){
            @Override
            public void onResponse_ReadAnalogDone(String resTime, JSONObject response) {
                DLog("onResponse_ReadAnalogDone result: " + response);
                pbInProgress.setVisibility(View.INVISIBLE);
                tvResponseTime.setText(resTime);
                showResult_ReadAnalog(response);
            }
        } );

    }

    private void _____________SHOW_RESULT_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    private String value = "-";
    private int valueInt = 0;
    //**********************************************************************************************
    private void showResult_HCSR04(JSONObject response) {
        if (response != null) {
            String statusValue = JSONHelper.getJSONValue(response, "status");
            if (statusValue != null){
                if (statusValue.equalsIgnoreCase("ok")) {
                    String distance = JSONHelper.getJSONValue(response, "distance");
                    if (distance == null){ distance = "0";}
                    value = distance;
                    valueInt = Integer.valueOf(value);

                } // statusValue NOT OK
            } // statusValue == NULL
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponseValue.setText(value);
                pbResponse.setProgress(valueInt);
                //startRequest_HCSR04();
            }
        });
    }

    //**********************************************************************************************
    private void showResult_ReadAnalog(JSONObject response) {
        if (response != null) {
            String statusValue = JSONHelper.getJSONValue(response, "status");
            if (statusValue != null){
                if (statusValue.equalsIgnoreCase("ok")) {
                    String analog = JSONHelper.getJSONValue(response, "analog");
                    if (analog == null){ analog = "0";}
                    value = analog;
                    valueInt = Integer.valueOf(value);

                } // statusValue NOT OK
            } // statusValue == NULL
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponseValue.setText(value);
                pbResponse.setProgress(valueInt);
                //startRequest_HCSR04();
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
    private void DLog(String msg) {
        String className = this.getClass().getSimpleName();
        Log.d("DTag", className + ": " + msg);
    }
}
