package com.hbbmobile.bsnapp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hbbmobile.bsnapp.MainActivity;
import com.hbbmobile.bsnapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        welcomeThread.start();
    }

    Thread welcomeThread = new Thread() {
        @Override
        public void run() {
            try {
                super.run();
                sleep(1000);  //Delay of 10 seconds
            } catch (Exception e) {

            } finally {
                Intent i = new Intent(SplashActivity.this,
                        MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    };
}
