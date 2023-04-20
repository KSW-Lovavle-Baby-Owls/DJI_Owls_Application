package com.DJIOwl.djiowl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/*
This Activity contains 4 fragments:
    1. Camera
    2. Controller
    3. Map
    5. Camera and Map
Each provide a new view for the user to see and control the drone's path
There will be a dropdown to pick between manual and autonomous(mission) mode
 */
public class MissionControlActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    boolean trackDroneG;
    boolean trackDroneB;
    FragmentTransaction fragmentTransaction;
    private Fragment mFragment;
    private ImageView leftArrowButton;
    private ImageView rightArrowButton;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private Marker currentMarkerG = null;
    private Marker currentMarkerB = null;

    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);
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
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setVisibility(View.INVISIBLE); // only want to see when on a map fragment
        leftArrowButton = findViewById(R.id.leftArrowButton);
        rightArrowButton = findViewById(R.id.rightArrowButton);
        // first fragment to display
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragment = new CameraFragment();
        fragmentTransaction.replace(R.id.fragmentView, mFragment);
        fragmentTransaction.commit();
        leftArrowButton.setVisibility(View.INVISIBLE);


    }
    public void onMyDroneClick(View view) {
        trackDroneG = true;
        trackDroneB = false;

        // test
        setGoodDrone(new LatLng(40.4301, -86.9134));
    }
    public void onBadDroneClick(View view) {
        trackDroneG = false;
        trackDroneB = true;

        // test
        setBadDrone(new LatLng(40.4318, -86.9103));
    }
    public void leftArrowClick(View view) {
        if (mFragment.getClass() == CameraPlusMapFragment.class) {
            rightArrowButton.setVisibility(View.VISIBLE);
            mFragment = new SupportMapFragment();
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentView, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);
            radioGroup.setVisibility(View.VISIBLE);
            radioGroup.clearCheck();

        } else if (mFragment.getClass() == SupportMapFragment.class) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new ControlFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();
            radioGroup.setVisibility(View.INVISIBLE);

        } else if (mFragment.getClass() == ControlFragment.class) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new CameraFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();
            leftArrowButton.setVisibility(View.INVISIBLE);

        }
    }
    public void rightArrowClick(View view) {
        if (mFragment.getClass() == CameraFragment.class) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new ControlFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();
            leftArrowButton.setVisibility(View.VISIBLE);

        } else if (mFragment.getClass() == ControlFragment.class) {
            mapFragment = SupportMapFragment.newInstance();
            mFragment = new SupportMapFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentView, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);
            radioGroup.setVisibility(View.VISIBLE);
            radioGroup.clearCheck();

        } else if (mFragment.getClass() == SupportMapFragment.class) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new CameraPlusMapFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();
            rightArrowButton.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
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

        // testing only
        setGoodDrone(new LatLng(40.4301, -86.9134));
        setBadDrone(new LatLng(40.4318, -86.9103));

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

    // This is the method that receives the location of the good drone and moves the marker to it
    // Update UI through Async task? hard to know without real data. Create custom listener of some sort?
    public void setGoodDrone(LatLng location) {

        //LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "Unable to get location information";
        String markerSnippet = "Check the location permission and GPS";


        if (currentMarkerG != null) currentMarkerG.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(false);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentMarkerG = mMap.addMarker(markerOptions);

        if (trackDroneG) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
            mMap.moveCamera(cameraUpdate);
        }

    }
    public void setBadDrone(LatLng location) {
        String markerTitle = "Unable to get location information";
        String markerSnippet = "Check the location permission and GPS";
        if (currentMarkerB != null) currentMarkerB.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(false);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarkerB = mMap.addMarker(markerOptions);

        if (trackDroneB) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
            mMap.moveCamera(cameraUpdate);
        }

    }

}