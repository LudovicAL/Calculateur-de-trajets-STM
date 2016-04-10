package com.ludovical.tp1stm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;

public class SelectorActivity extends AppCompatActivity {

    private ArrayList<Route> routeList;
    private GoogleMap googleMap;
    private int selectedRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        selectedRoute = 0;
        routeList = new ArrayList<>();
        //Retrieve data passed by previous activity
        retrieveIntentInformation();
        //Prepare the mapFragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                updateMap();
            }
        });
    }

    private void updateMap() {
        if (routeList != null && !routeList.isEmpty()) {
            googleMap.clear();
            String selectedRouteCoordinatesString = routeList.get(selectedRoute).getAllCoordinates();
            PolylineOptions polylineOption = new PolylineOptions();
            polylineOption.clickable(true);
            for (String latLon : selectedRouteCoordinatesString.split(";")) {
                polylineOption.add(new LatLng(Double.parseDouble(latLon.split(",")[0]), Double.parseDouble(latLon.split(",")[1])));
            }
            googleMap.addPolyline(polylineOption);
            googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                @Override
                public void onPolylineClick(Polyline polyline) {
                    polyline.setColor(Color.RED);
                }
            });
        }
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

}
