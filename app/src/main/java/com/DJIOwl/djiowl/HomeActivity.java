package com.DJIOwl.djiowl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
In the home class, the user will select the UAV he/she wishes to follow
there is a UAV ID drop down list as well as a google maps display with all the UAVs' locations
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}