package com.example.offlinemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    boolean mapReady = false;
    boolean currLocation = false;
    GoogleMap mMap;

    MarkerOptions home;
    MarkerOptions work;
    ArrayList<MarkerOptions> markers;

    LocationRequest locationRequest;
    int cameraPadding;

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markers = new ArrayList<>();

        work = new MarkerOptions().position(new LatLng(30.1609262, -95.4584287)).title("WORK");
        home = new MarkerOptions().position(new LatLng(29.576138, -95.5730441)).title("HOME");

        markers.add(work);
        markers.add(home);

        cameraPadding = 40;

        Button btnHome = findViewById(R.id.btnWork);
        Button btnWork = findViewById(R.id.btnAddHome);
        Button btnDefault = findViewById(R.id.btnDefault);

        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady)
                    mMap.clear();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady && currLocation)
                    updateMarkers();
            }
        });

        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady)
                    updateMarkers();
            }
        });


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.online_maps);
        supportMapFragment.getMapAsync(this);


        startLocationUpdates();
    }

    private void updateMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions markerOptions : markers) {
            mMap.addMarker(markerOptions);
            builder.include(markerOptions.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, cameraPadding);
        mMap.animateCamera(cameraUpdate);
    }

    public void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingClient = LocationServices.getSettingsClient(this);
        settingClient.checkLocationSettings(locationSettingsRequest);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onLocationChanged(Location location) {
        currLocation = true;
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        Toast.makeText(this, "Lat : " + location.getLatitude() + " Long : " + location.getLongitude() + " mode : " + location.getProvider(), Toast.LENGTH_LONG).show();
//        cp = CameraPosition.builder().target(new LatLng(latitude, longitude)).zoom(25).build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 1000, null);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
