package com.example.ai_driving_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class nearByDrivingSchool extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_driving_school);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Customize the map if needed
        // For example, add a marker at a specific location and move the camera
        LatLng drivingSchoolLocation = new LatLng(37.7749, -122.4194); // Replace with the actual coordinates
        googleMap.addMarker(new MarkerOptions().position(drivingSchoolLocation).title("Driving School"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(drivingSchoolLocation));
    }
}
