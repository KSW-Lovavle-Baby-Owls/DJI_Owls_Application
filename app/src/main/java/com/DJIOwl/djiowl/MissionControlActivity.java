package com.DJIOwl.djiowl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/*
This Activity contains 4 fragments:
    1. Camera
    2. Controller
    3. Map
    5. Camera and Map
Each provide a new view for the user to see and control the drone's path
There will be a dropdown to pick between manual and autonomous(mission) mode
 */
public class MissionControlActivity extends AppCompatActivity {
    FragmentTransaction fragmentTransaction;
    private Fragment mFragment;
    private ImageView leftArrowButton;
    private ImageView rightArrowButton;

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

    }
    public void leftArrowClick(View view) {
        if (mFragment.getClass() == CameraPlusMapFragment.class) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new MapFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();
            rightArrowButton.setVisibility(View.VISIBLE);

        } else if (mFragment.getClass() == MapFragment.class) {
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
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new MapFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();

        } else if (mFragment.getClass() == MapFragment.class) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragment = new CameraPlusMapFragment();
            fragmentTransaction.replace(R.id.fragmentView, mFragment);
            fragmentTransaction.commit();
            rightArrowButton.setVisibility(View.INVISIBLE);

        }
    }
}