package com.android.locationkit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.locationkit.R;


public class LocationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "LocationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        checkPermissions();
        initView();
    }

    private void initView(){
      findViewById(R.id.tvLocUpdateCallback).setOnClickListener(this);
      findViewById(R.id.tvGetLocationAvail).setOnClickListener(this);
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED){
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        }

        if (requestCode == 2) {
            if (grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed");
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLocUpdateCallback:
                Intent updatesWithCallback = new Intent();
                updatesWithCallback.setClass(this, ReqLocUpdatesWithCallbackActivity.class);
                updatesWithCallback.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(updatesWithCallback);
                break;
            case R.id.tvGetLocationAvail:
                Intent locationAvailabilityIntent = new Intent();
                locationAvailabilityIntent.setClass(this, GetLocationAvailabilityActivity.class);
                locationAvailabilityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(locationAvailabilityIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }
}
