package com.DJIOwl.djiowl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
/*
in this activity, the loading screen will be displayed
and background connectivity code will be run
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}