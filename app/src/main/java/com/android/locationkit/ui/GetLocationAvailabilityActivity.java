package com.android.locationkit.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.locationkit.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationServices;

public class GetLocationAvailabilityActivity extends Activity implements View.OnClickListener
    {
        public static final String TAG = "LocationAvailability";
        private FusedLocationProviderClient mFusedLocationProviderClient;
        private TextView mTvLogs;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlocation_availability);
        initView();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }
     private void initView(){
         findViewById(R.id.tvGetLocationAvl).setOnClickListener(this);
         mTvLogs = findViewById(R.id.tvLogs);
         mTvLogs.setMovementMethod(new ScrollingMovementMethod());
     }
        /**
         * Obtaining Location Availability
         */
        private void getLocationAvailability() {
        try {
            Task<LocationAvailability> locationAvailability = mFusedLocationProviderClient.getLocationAvailability();
            locationAvailability.addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
                @Override
                public void onSuccess(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        String logDetails  = "getLocationAvailability onSuccess:" + locationAvailability.toString();
                        Log.i(TAG,
                                logDetails);
                        mTvLogs.setText(logDetails);
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            String logDetails  = "getLocationAvailability onFailure:" + e.getMessage();
                            Log.e(TAG, logDetails);
                            mTvLogs.setText(logDetails);
                        }
                    });
        } catch (Exception e) {
            String logDetails  = "getLocationAvailability exception:" + e.getMessage();
            Log.e(TAG, logDetails);
            mTvLogs.setText(logDetails);
        }
    }

        @Override
        public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tvGetLocationAvl:
                    getLocationAvailability();
                    break;
               }
        } catch (Exception e) {
            Log.e(TAG, "getLocationAvailability Exception:" + e);
        }
    }

}
