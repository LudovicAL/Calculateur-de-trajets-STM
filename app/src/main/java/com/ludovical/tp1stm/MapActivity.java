package com.ludovical.tp1stm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.Serializable;

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener {

    private Coordinates actualCoordinates;
    private Coordinates otherCoordinates;
    private Polyline polyline;
    private Marker actualMarker;
    private Marker otherMarker;
    private GoogleMap googleMap;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //Retrieve data passed by previous activity
        retrieveIntentInformation();
        //Prepare the mapFragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapForSelection);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                loadMap();
            }
        });
    }

    //Prepares the map object
    private void loadMap() {
        updateMap();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(actualCoordinates.getLatitude(), actualCoordinates.getLongitude()), 12));
        googleMap.setOnMapLongClickListener(this);
    }

    //Behavior when user long-clicks the map
    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d("test", "Long click registered.");
        actualCoordinates.setName("");
        actualCoordinates.setLatitude(latLng.latitude);
        actualCoordinates.setLongitude(latLng.longitude);
        updateMap();
        Toast.makeText(MapActivity.this, getResources().getString(R.string.changeRegistered), Toast.LENGTH_SHORT).show();
    }

    //Update the map's markers and polyline
    private void updateMap() {
        if (googleMap != null) {
            googleMap.clear();
            addMarker(actualMarker, actualCoordinates, BitmapDescriptorFactory.HUE_GREEN);
            addMarker(otherMarker, otherCoordinates, BitmapDescriptorFactory.HUE_RED);
            polyline = googleMap.addPolyline(new PolylineOptions().geodesic(true)
                            .add(new LatLng(actualCoordinates.getLatitude(), actualCoordinates.getLongitude()))
                            .add(new LatLng(otherCoordinates.getLatitude(), otherCoordinates.getLongitude()))
            );
        }
    }

    //Add a marker on the map
    private void addMarker(Marker marker, Coordinates coordinates, float color) {
        BitmapDescriptor actualCoordinatesMarkerColor = BitmapDescriptorFactory.defaultMarker(color);
        LatLng actualCoordinatesPosition = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        marker = googleMap.addMarker(new MarkerOptions()
                .position(actualCoordinatesPosition)
                .icon(actualCoordinatesMarkerColor));
    }

    //Retrieves the information passed with the Intent
    private void retrieveIntentInformation() {
        Intent intent = getIntent();
        actualCoordinates = (Coordinates) intent.getSerializableExtra("actualCoordinates");
        otherCoordinates = (Coordinates) intent.getSerializableExtra("otherCoordinates");
        message = (String) intent.getStringExtra("message");
        TextView tv = (TextView)findViewById(R.id.textViewSelectOnMap);
        tv.setText(message);
    }

    //Fired when user clicks the Application "Back" button
    public void onMapActivityBackButtonClick(View v) {
        onBackPressed();
    }

    //Fired when user clicks the Android "Back" button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("coordinates", (Serializable) actualCoordinates);
        setResult(RESULT_OK, intent);
        //finish();
        super.onBackPressed();
    }

    //Saving instance state is good practice
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("actualCoordinates", actualCoordinates);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        actualCoordinates = (Coordinates)savedInstanceState.getSerializable("actualCoordinates");
        LatLng latLng = CommonTools.coordinatesToLatLng(actualCoordinates);
        onMapLongClick(latLng);
    }
}
