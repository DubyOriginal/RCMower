package hr.duby.rcmower.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.R;
import hr.duby.rcmower.util.BasicUtils;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener{

    //WIDGETS
    private EditText etIPAddress, etPort_setup;
    private Button btnCancel, btnSave;

    @Override
    //**********************************************************************************************
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        initGUI();
    }

    //**********************************************************************************************
    private void initGUI() {
        etIPAddress = (EditText) findViewById(R.id.etIPAdress_setup);
        etPort_setup = (EditText) findViewById(R.id.etPort_setup);
        btnCancel = (Button) findViewById(R.id.btnCancel_setup);
        btnSave = (Button) findViewById(R.id.btnSave_setup);

        etIPAddress.setText(BasicUtils.getVALUEFromSharedPrefs(this, Const.PREF_IP, "192.168.1.24"));
        etPort_setup.setText(BasicUtils.getVALUEFromSharedPrefs(this, Const.PREF_PORT, "80"));

        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }


    @Override
    //**********************************************************************************************
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCancel_setup:
                break;

            case R.id.btnSave_setup:
                String ipaddress = etIPAddress.getText().toString();
                String port = etPort_setup.getText().toString();

                if (ipaddress != null && port != null && ipaddress.length() > 0 && port.length()>0){
                    BasicUtils.setVALUEToSharedPrefs(this, Const.PREF_IP, ipaddress);
                    BasicUtils.setVALUEToSharedPrefs(this, Const.PREF_PORT, port);

                    String ws = "ws://" + ipaddress + ":" + port;
                    BasicUtils.setVALUEToSharedPrefs(this, Const.PREF_WS, ws);
                }
                break;
        }
    }
}
