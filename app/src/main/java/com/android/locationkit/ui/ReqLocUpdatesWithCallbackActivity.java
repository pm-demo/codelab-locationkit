package com.android.locationkit.ui;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.locationkit.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;

import java.util.List;

public class ReqLocUpdatesWithCallbackActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "WithCallbackActivity";
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SettingsClient mSettingsClient;
    private TextView mTvLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_update_callback);
        initView();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest();
        // Sets the interval for location update (unit: Millisecond)
        mLocationRequest.setInterval(5000);
        // Sets the priority
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    String logDetails;
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                Log.i(TAG,
                                        "onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude()
                                                + "," + location.getLatitude() + "," + location.getAccuracy());
                                logDetails = "Location :" + location.getLongitude() + "," + location.getLatitude() +
                                         "," + location.getAccuracy();
                                mTvLogs.setText(logDetails);
                            }
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        String logDetails  = "onLocationAvailability isLocationAvailable:" + flag;
                        Log.i(TAG, logDetails);
                        mTvLogs.setText(logDetails);
                    }
                }
            };
        }

    }
    private void initView(){
        findViewById(R.id.tvReqUpdate).setOnClickListener(this);
        findViewById(R.id.tvRemoveUpdate).setOnClickListener(this);
        mTvLogs = findViewById(R.id.tvLogs);
        mTvLogs.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * Requests a location update and calls back on the specified Looper thread.
     */
    private void requestLocationUpdatesWithCallback() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask = mSettingsClient.checkLocationSettings(locationSettingsRequest);
            locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.i(TAG, "check location settings success");
                    mFusedLocationProviderClient
                            .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG,
                                            "requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                                }
                            });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "checkLocationSetting onFailure:" + e.getMessage());
                            int statusCode = ((ApiException) e).getStatusCode();

                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        //When the startResolutionForResult is invoked, a dialog box is displayed, asking you to open the corresponding permission.
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(ReqLocUpdatesWithCallbackActivity.this, 0);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.e(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReqUpdate:
                requestLocationUpdatesWithCallback();
                break;
            case R.id.tvRemoveUpdate:
                removeLocationUpdatesWithCallback();
                break;
        }
    }


    /**
     * Removed when the location update is no longer required.
     */
    private void removeLocationUpdatesWithCallback() {
        try {
            Task<Void> voidTask = mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess");
                   mTvLogs.setText("removeLocationUpdatesWithCallback onSuccess");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            String logDetails  = "removeLocationUpdatesWithCallback onFailure:" + e.getMessage();
                            Log.e(TAG, logDetails);
                            mTvLogs.setText(logDetails);
                        }
                    });
        } catch (Exception e) {
            String logDetails  = "removeLocationUpdatesWithCallback onFailure:" + e.getMessage();
            Log.e(TAG, logDetails);
            mTvLogs.setText(logDetails);
        }
    }

    @Override
    protected void onDestroy() {
        //Removed when the location update is no longer required.
        removeLocationUpdatesWithCallback();
        super.onDestroy();
    }
}
