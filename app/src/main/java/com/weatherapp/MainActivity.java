package com.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(Choose_city.EXTRA_MESSAGE);
        //TODO: передать название города
        setContentView(R.layout.activity_main);
    }
}