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
import hr.duby.rcmower.MowerHTTPClient;
import hr.duby.rcmower.MowerWSClient;
import hr.duby.rcmower.R;
import hr.duby.rcmower.util.JSONHelper;


public class InfoActivity extends AppCompatActivity   implements View.OnClickListener {

    //WIDGETS
    private Button btnHCSR04, btnReadAnalog, btnReadSHT11;
    private TextView tvResponseValue1, tvResponseTime;
    private ProgressBar pbResponse, pbInProgress;

    //VARS
    private String BASE_URL;

    @Override
    //**********************************************************************************************
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initGUI();

        MowerWSClient.getInstance().connectWebSocket(new MowerWSClient.OnWebSocketEvent(){
            @Override
            public void onWebSocketEvent(String eventCode, String eventMsg) {
                DLog("eventCode: " + eventCode + ", eventMsg: " + eventMsg);
            }
        });
    }

    @Override
    //**********************************************************************************************
    protected void onResume() {
        super.onResume();

        //BASE_URL = MowerHTTPClient.getInstance().getBASE_URL(this);


    }

    //**********************************************************************************************
    private void initGUI() {
        btnHCSR04 = (Button) findViewById(R.id.btnHCSR);
        btnReadAnalog = (Button) findViewById(R.id.btnReadAnalog);
        btnReadSHT11  = (Button) findViewById(R.id.btnReadSHT11);
        tvResponseValue1 = (TextView) findViewById(R.id.tvResponseValue1);
        tvResponseTime = (TextView) findViewById(R.id.tvResponseTime);
        pbResponse = (ProgressBar) findViewById(R.id.pbResponse);
        pbInProgress = (ProgressBar) findViewById(R.id.pbInProgress);

        //init value
        pbResponse.setProgress(0);
        tvResponseValue1.setText("");

        pbInProgress.setVisibility(View.INVISIBLE);

        btnHCSR04.setOnClickListener(this);
        btnReadAnalog.setOnClickListener(this);
        btnReadSHT11.setOnClickListener(this);
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

            case R.id.btnReadSHT11:
                startRequest_ReadSHT11();
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
        MowerHTTPClient.getInstance().request_HCSR04(InfoActivity.this, new MowerHTTPClient.OnResponse_HCSR04(){
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

        MowerWSClient.getInstance().sendMessage("A105", new MowerWSClient.OnWebSocketEvent() {
            @Override
            public void onWebSocketEvent(String eventCode, String eventMsg) {
                DLog("eventCode: " + eventCode + ", eventMsg: " + eventMsg);
            }
        });

        /*
        MowerHTTPClient.getInstance().request_ReadAnalog(InfoActivity.this, new MowerHTTPClient.OnResponse_ReadAnalog(){
            @Override
            public void onResponse_ReadAnalogDone(String resTime, JSONObject response) {
                DLog("onResponse_ReadAnalogDone result: " + response);
                pbInProgress.setVisibility(View.INVISIBLE);
                tvResponseTime.setText(resTime);
                showResult_ReadAnalog(response);
            }
        });
        */

    }

    //**********************************************************************************************
    private void startRequest_ReadSHT11() {
        pbResponse.setMax(Const.SHT11_MAX);
        tvResponseTime.setText("");
        pbInProgress.setVisibility(View.VISIBLE);
        MowerHTTPClient.getInstance().request_ReadSHT11(InfoActivity.this, new MowerHTTPClient.OnResponse_ReadSHT11(){
            @Override
            public void onResponse_ReadSHT11Done(String resTime, JSONObject response) {
                DLog("onResponse_ReadSHT11Done result: " + response);
                pbInProgress.setVisibility(View.INVISIBLE);
                tvResponseTime.setText(resTime);
                showResult_ReadSHT11(response);
            }
        } );

    }


    private void _____________SHOW_RESULT_____________() {}
    //*************************************************************************************************************************************************
    //*************************************************************************************************************************************************

    private String value1 = "-";
    private String value2 = "-";
    private int value1Int = 0;
    private int value2Int = 0;
    //**********************************************************************************************
    private void showResult_HCSR04(JSONObject response) {
        if (response != null) {
            String statusValue = JSONHelper.getJSONValue(response, "status");
            if (statusValue != null){
                if (statusValue.equalsIgnoreCase("ok")) {
                    String distance = JSONHelper.getJSONValue(response, "distance");
                    if (distance == null){ distance = "0";}
                    value1 = distance;
                    value1Int = Integer.valueOf(value1);

                } // statusValue NOT OK
            } // statusValue == NULL
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponseValue1.setText(value1);
                pbResponse.setProgress(value1Int);
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
                    value1 = analog;
                    value1Int = Integer.valueOf(value1);

                } // statusValue NOT OK
            } // statusValue == NULL
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponseValue1.setText(value1);
                pbResponse.setProgress(value1Int);
                //startRequest_HCSR04();
            }
        });
    }

    private void showResult_ReadSHT11(JSONObject response) {
        if (response != null) {
            String statusValue = JSONHelper.getJSONValue(response, "status");
            if (statusValue != null){
                if (statusValue.equalsIgnoreCase("ok")) {
                    String temp = JSONHelper.getJSONValue(response, "temp");
                    String humidity = JSONHelper.getJSONValue(response, "humidity");
                    if (temp == null){ temp = "-273";}
                    value1 = temp;
                    value1Int = Integer.valueOf(value1);

                    if (humidity == null){ humidity = "0";}
                    value2 = humidity;
                    value2Int = Integer.valueOf(value2);

                } // statusValue NOT OK
            } // statusValue == NULL
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResponseValue1.setText(value1);
                pbResponse.setProgress(value1Int);
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
