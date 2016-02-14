package com.ludovical.tp1stm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    TextView textViewChoosenRouteId;
    TextView textViewRequiredTime;
    TextView textViewDepartureTime;
    TextView textViewArrivalTime;
    TextView textViewWalkDistance;
    TextView textViewCorrespondances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        //Locate the textview
        locateTextViews();
        //Retrieve the information passed by the previous activity and write it in the textviews
        retrieveIntentInformationAndprepareTextViews();
    }

    public void retrieveIntentInformationAndprepareTextViews() {
        Intent i = getIntent();
        textViewChoosenRouteId.setText(getString(R.string.itineraryNumber) + " " + i.getIntExtra("choosenRouteId", 0));
        textViewRequiredTime.setText(CommonTools.timeToString(i.getIntExtra("requiredTimeMinutes", 0), i.getIntExtra("requiredTimeHours", 0)));
        textViewDepartureTime.setText(CommonTools.timeToString(i.getIntExtra("departureTimeMinutes", 0), i.getIntExtra("departureTimeHours", 0)));
        textViewArrivalTime.setText(CommonTools.timeToString(i.getIntExtra("arrivalTimeMinutes", 0), i.getIntExtra("arrivalTimeHours", 0)));
        textViewWalkDistance.setText(i.getFloatExtra("walkDistance", 0.0f) + " " + getString(R.string.meter));
        textViewCorrespondances.setText("" + i.getIntExtra("correspondances", 0));
    }

    public void locateTextViews() {
        textViewChoosenRouteId = (TextView)findViewById(R.id.textViewChoosenRouteId);
        textViewRequiredTime = (TextView)findViewById(R.id.textViewRequiredTime);
        textViewDepartureTime = (TextView)findViewById(R.id.textViewDepartureTime);
        textViewArrivalTime = (TextView)findViewById(R.id.textViewArrivalTime);
        textViewWalkDistance = (TextView)findViewById(R.id.textViewWalkDistance);
        textViewCorrespondances = (TextView)findViewById(R.id.textViewCorrespondances);
    }

    public void onDisplayActivityBackButtonClick(View v) {
        finish();
    }
}
