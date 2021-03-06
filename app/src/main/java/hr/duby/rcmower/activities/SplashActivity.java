package hr.duby.rcmower.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import hr.duby.rcmower.BuildConfig;
import hr.duby.rcmower.Const;
import hr.duby.rcmower.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvVersionName = (TextView) findViewById(R.id.tvVersionName);
        if (tvVersionName != null){
            tvVersionName.setText(BuildConfig.VERSION_NAME);

        }

        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(Const.SPLASH_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    //Intent intent = new Intent("hr.duby.rcmower.activities.HomeActivity");
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }

}
