package com.example.ai_driving_app;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class nearByDrivingSchool extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private PlacesClient placesClient;
    private String placeId;
    SupportMapFragment mapFragment;
    private static final String TAG = "LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_driving_school);

        // Initialize the Places API
        Places.initialize(getApplicationContext(), "AIzaSyD5QdIiBcHYhVunbhYlqUWO2cUI5sapSh8");
        placesClient = Places.createClient(this);


        // Initialize the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission not granted. Requesting...");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Location permission already granted. Requesting location updates...");
            // Permission already granted, request location updates

        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "Google Map is ready.");
        requestLocationUpdates();
        // Map is ready, no need for further initialization here
    }

    private void requestLocationUpdates() {
        Log.d("LocationUpdates", "Requesting location updates...");
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Request location updates
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Get the current location
                            double currentLatitude = location.getLatitude();
                            double currentLongitude = location.getLongitude();

                            Log.d(TAG, "Location received. Latitude: " + currentLatitude + ", Longitude: " + currentLongitude);

                            // Display the current location on the map
                            LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                            updateMapWithLocation(currentLocation);
                        } else {
                            Log.e(TAG, "Last known location is null.");
                            // Handle the case where getLastLocation() returns null
                            // You may want to request location updates using a different method
                            // or inform the user that the location is not available
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Log.e(TAG, "Failed to get last known location: " + e.getMessage());
                        // Handle failure to get last known location
                        // You may want to request location updates using a different method
                        // or inform the user that the location is not available
                    });
        }
    }


    private void updateMapWithLocation(LatLng location) {
        Log.d(TAG, "Updating map with location...");

        mapFragment.getMapAsync(googleMap -> {
            // Add a marker for the user's location
            googleMap.addMarker(new MarkerOptions().position(location).title("Your Location"));

            // Request and add markers for nearby places
            requestNearbyPlaces(googleMap, location);

            // Move the camera to the user's location
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        });

    }

    private void requestNearbyPlaces(GoogleMap googleMap, LatLng location) {
        Log.d(TAG, "Requesting nearby places...");

        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Permission(s) error.");
            return;
        }

        placesClient.findCurrentPlace(request).addOnSuccessListener((response) -> {
            List<PlaceLikelihood> placeLikelihoods = response.getPlaceLikelihoods();

            Log.d(TAG, "Places response recieved.");
            // Add markers for nearby places
            for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                Place place = placeLikelihood.getPlace();
                LatLng placeLocation = place.getLatLng();

                if (placeLocation != null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(placeLocation)
                            .title(place.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))); // Marker color
                }
            }
        }).addOnFailureListener((exception) -> {
            // Handle failure
            Log.d(TAG, "Exception: " + exception.getMessage());
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                requestLocationUpdates();
            }
        }
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
