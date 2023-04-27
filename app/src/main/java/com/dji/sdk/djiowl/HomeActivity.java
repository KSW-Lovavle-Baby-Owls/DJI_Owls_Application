package com.dji.sdk.djiowl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.dji.sdk.djiowl.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/*
In the home class, the user will select the UAV he/she wishes to follow
there is a UAV ID drop down list as well as a google maps display with all the UAVs' locations
 */
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener  {
    private GoogleMap mMap;
    ArrayList<Drone> dronesToTrack = new ArrayList<>();
    Drone selectedDrone;
    Button trackDronebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if
            (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
        // test
        dronesToTrack.add(new Drone("drone1", new LatLng(40.4301, -86.9134)));
        dronesToTrack.add(new Drone("drone2", new LatLng(40.4318, -86.9103)));
        dronesToTrack.add(new Drone("drone3", new LatLng(40.4301, -86.9140)));

        trackDronebtn = findViewById(R.id.trackDronebtn);

        Spinner spinner = findViewById(R.id.idSpinner);
        spinner.setOnItemSelectedListener(this);
        String[] droneIDs = new String[dronesToTrack.size()];
        for (int i = 0; i < dronesToTrack.size(); i++) {
            droneIDs[i] = dronesToTrack.get(i).getId();
        }
        // Create the instance of ArrayAdapter
        // having the list of courses
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                droneIDs);

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spinner.setAdapter(ad);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)  {
        Log.d("Map", "Map ready.");
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if
            (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        setDroneMarkers();

        long minTime = 1000; //millisecond
        float minDistance = 0;

        // this location listener is to the device this application is running on only
        // to follow a different device, we will need a check box or radio button of some sort to
        // either pick to follow the good drone or enemy drone
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                LatLng curPoint = new LatLng(latitude, longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,16));
                locationManager.removeUpdates(this);
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance,  listener);
    }

    public void setDroneMarkers() {
        for (int i = 0; i < dronesToTrack.size(); i++ ) {
            Drone drone = dronesToTrack.get(i);
            LatLng location = drone.getLocation();
            String markerTitle = drone.getId();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            markerOptions.title(markerTitle);
            markerOptions.draggable(false);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptions);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedDrone = dronesToTrack.get(position);
        trackDronebtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        trackDronebtn.setVisibility(View.INVISIBLE);
    }
    public void trackDroneClick(View view) {
        // for now this just sends the ID to the next activity. I remember we did not want to use intent
        // so this is just a test and for navigation testing purposes
        Intent MCIntent = new Intent(HomeActivity.this, MissionControlActivity.class);
        MCIntent.putExtra("droneID", selectedDrone.getId());
        HomeActivity.this.startActivity(MCIntent);

    }
}