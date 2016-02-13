package com.ludovical.tp1stm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

    }

    public void onDisplayActivityBackButtonClick(View v) {
        finish();
    }

}
