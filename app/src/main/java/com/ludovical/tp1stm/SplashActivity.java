package com.ludovical.tp1stm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 5000; //Length of the splash activity, in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(newIntent);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
