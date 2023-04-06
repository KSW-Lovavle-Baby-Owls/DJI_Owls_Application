package com.DJIOwl.djiowl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);
    }
}