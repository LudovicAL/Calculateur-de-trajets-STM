package com.ludovical.tp1stm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;

public class SelectorActivity extends AppCompatActivity {

    private ArrayList<Route> routeList;
    private GoogleMap googleMap;
    private int selectedRoute;
    private TextView textViewResultDisplay;
    private Marker initialPositionMarker;
    private Marker objectivePositionMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        routeList = new ArrayList<>();
        //Retrieve data passed by previous activity
        retrieveIntentInformation();
        //Update de display
        TextView textViewResultNumber = (TextView)findViewById(R.id.textViewResultNumber);
        textViewResultNumber.setText(routeList.size() + " " + getResources().getString(R.string.itineraryFound));
        selectedRoute = 0;
        textViewResultDisplay = (TextView)findViewById(R.id.textViewResultDisplay);
        updateDisplay();
        //Prepare the mapFragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                updateMap();
            }
        });
    }

    //Updates the displayed information
    private void updateDisplay() {
        if (routeList != null && routeList.size() > selectedRoute) {
            textViewResultDisplay.setText("Option #" + (selectedRoute + 1) + ": " + routeList.get(selectedRoute).getTripHeadSign() + "\n" + routeList.get(selectedRoute).getRequiredTime());
        }
    }

    //Updates the map object
    private void updateMap() {
        if (routeList != null && !routeList.isEmpty() && googleMap != null) {
            googleMap.clear();
            String selectedRouteCoordinatesString = routeList.get(selectedRoute).getAllCoordinates();
            PolylineOptions polylineOption = new PolylineOptions();
            polylineOption.clickable(true);
            for (String latLon : selectedRouteCoordinatesString.split(";")) {
                polylineOption.add(new LatLng(Double.parseDouble(latLon.split(",")[0]), Double.parseDouble(latLon.split(",")[1])));
            }
            googleMap.addPolyline(polylineOption);
            addMarker(initialPositionMarker, routeList.get(selectedRoute).getInitialCoordinates(), BitmapDescriptorFactory.HUE_GREEN, getResources().getString(R.string.initialPosition));
            addMarker(objectivePositionMarker, routeList.get(selectedRoute).getObjectiveCoordinates(), BitmapDescriptorFactory.HUE_RED, getResources().getString(R.string.objectivePosition));
            updateCamera();
        }
    }

    //Positions the camera on the markers
    private void updateCamera() {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        LatLng initialLatLng = new LatLng(routeList.get(selectedRoute).getInitialLatitude(), routeList.get(selectedRoute).getInitialLongitude());
        LatLng objectiveLatLng = new LatLng(routeList.get(selectedRoute).getObjectiveLatitude(), routeList.get(selectedRoute).getObjectiveLongitude());
        b.include(initialLatLng);
        b.include(objectiveLatLng);
        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 240, 240, 0);
        googleMap.moveCamera(cu);
    }

    //Add a marker on the map object
    private void addMarker(Marker marker, Coordinates coordinates, float color, String title) {
        BitmapDescriptor actualCoordinatesMarkerColor = BitmapDescriptorFactory.defaultMarker(color);
        LatLng actualCoordinatesPosition = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        marker = googleMap.addMarker(new MarkerOptions()
            .position(actualCoordinatesPosition)
            .title(title)
            .icon(actualCoordinatesMarkerColor));
    }

    //Retrieves the information passed with the Intent
    private void retrieveIntentInformation() {
        Intent intent = getIntent();
        for (int i = 0; i < MainActivity.NUMBER_OF_RESULTS; i++) {
            Route route = (Route) intent.getSerializableExtra("route" + i);
            routeList.add(route);
            Log.d("test", route.toString());
        }
    }

    //Fired when user clicks the "Back" button
    public void onSelectorActivityBackButtonClick(View v) {
        finish();
    }

    //Fired when user clicks the "Previous" button
    public void onPreviousItineraryButtonClick(View v) {
        if (selectedRoute > 0) {
            selectedRoute--;
        } else {
            selectedRoute = routeList.size() - 1;
        }
        updateDisplay();
        updateMap();
    }

    //Fired when user clicks the "Next" button
    public void onNextItineraryButtonClick(View v) {
        if (selectedRoute < routeList.size() - 1) {
            selectedRoute++;
        } else {
            selectedRoute = 0;
        }
        updateDisplay();
        updateMap();
    }

    //Fired when user clicks the "Select itinerary" button
    public void onSelectItineraryButtonClick(View v) {
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        intent.putExtra("route", routeList.get(selectedRoute));
        startActivity(intent);
    }

    //Saving instance state is good practice
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("selectedRoute", selectedRoute);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedRoute = savedInstanceState.getInt("selectedRoute");
        updateDisplay();
        updateMap();
    }
}
