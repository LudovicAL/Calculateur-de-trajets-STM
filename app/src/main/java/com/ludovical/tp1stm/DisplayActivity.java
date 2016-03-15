package com.ludovical.tp1stm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    Route route;
    TextView textViewDepartureTime;
    TextView textViewBoardingStopName;
    TextView textViewTransitBoardingTime;
    TextView textViewArrivalStopName;
    TextView textViewTransitArrivalTime;
    TextView textViewArrivalTime;
    TextView textViewTotalRequiredTime;
    TextView textViewTotalWalkingDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        //Locate the TextViews
        locateTextViews();
        //Retrieve the information passed by the previous activity
        retrieveIntentInformation();
        //Fill the TextViews;
        fillTextViews();
    }

    //Retrieve the information passed with the Intent and prepares the textviews accordingly
    public void retrieveIntentInformation() {
        Intent intent = getIntent();
        route = (Route) intent.getSerializableExtra("route");
    }

    ////Fills the TextViews
    public void fillTextViews(){
        textViewDepartureTime.setText(route.getDepartureTime());
        textViewBoardingStopName.setText(route.getaStopName());
        textViewTransitBoardingTime.setText(route.getaArrivalTime());
        textViewArrivalStopName.setText(route.getbStopName());
        textViewTransitArrivalTime.setText(route.getbArrivalTime());
        textViewArrivalTime.setText(route.getObjectiveTime());
        textViewTotalRequiredTime.setText(route.getRequiredTime());
        textViewTotalWalkingDistance.setText(route.getWalkDistance() + " m");
    }

    //Locates the required textview widgets
    public void locateTextViews() {
        textViewDepartureTime = (TextView)findViewById(R.id.textViewDepartureTime);
        textViewBoardingStopName = (TextView)findViewById(R.id.textViewBoardingStopName);
        textViewTransitBoardingTime = (TextView)findViewById(R.id.textViewTransitBoardingTime);
        textViewArrivalStopName = (TextView)findViewById(R.id.textViewArrivalStopName);
        textViewTransitArrivalTime = (TextView)findViewById(R.id.textViewTransitArrivalTime);
        textViewArrivalTime = (TextView)findViewById(R.id.textViewArrivalTime);
        textViewTotalRequiredTime = (TextView)findViewById(R.id.textViewTotalRequiredTime);
        textViewTotalWalkingDistance = (TextView)findViewById(R.id.textViewTotalWalkingDistance);
    }

    //Fired when users clicks the "Back" button
    public void onDisplayActivityBackButtonClick(View v) {
        finish();
    }
}
