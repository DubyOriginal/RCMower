package hr.duby.rcmower.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import hr.duby.rcmower.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    //WIDGETS
    private Button btnCtrlManual;
    private Button btnCtrlSemiAutomatic;
    private Button btnProgram;
    private Button btnInfo;
    private Button btnSettings;

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

        btnCtrlManual.setOnClickListener(this);
        btnCtrlSemiAutomatic.setOnClickListener(this);
        btnProgram.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
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
                break;
            case R.id.btnInfo:
                break;
            case R.id.btnSettings:
                intent = new Intent(this, SetupActivity.class);
                break;
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
