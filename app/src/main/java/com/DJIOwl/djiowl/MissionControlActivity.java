package com.DJIOwl.djiowl;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

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
    FragmentTransaction fragmentTransaction;
    private Fragment mFragment;
    private ImageView leftArrowButton;
    private ImageView rightArrowButton;

    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1s
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5s

    // To distinguish permission request(onRequestPermissionsResult / ActivityCompat.requestPermission)
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // Defines the permission required to run the app
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // External Storage
    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    SupportMapFragment mapFragment;

    private View mLayout;  // To use Snackbar, we need View

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);
        leftArrowButton = findViewById(R.id.leftArrowButton);
        rightArrowButton = findViewById(R.id.rightArrowButton);
        // first fragment to display
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragment = new CameraFragment(); // CreateNewNote is fragment you want to display
        fragmentTransaction.replace(R.id.fragmentView, mFragment);
        fragmentTransaction.commit();
        leftArrowButton.setVisibility(View.INVISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


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

        } else if (mFragment.getClass() == SupportMapFragment.class) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new ControlFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();

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

        } else if (mFragment.getClass() == SupportMapFragment.class) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new CameraPlusMapFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();
            rightArrowButton.setVisibility(View.INVISIBLE);

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

        long minTime = 1000; //millisecond
        float minDistance = 0;
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

}