package com.example.justwalk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "LocationBroadcastReceiver";
    private LocationUpdateListener listener;

    public LocationBroadcastReceiver(LocationUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(LocationService.ACTION_LOCATION_UPDATE)) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);

            Log.d(TAG, "Received location update: " + latitude + ", " + longitude);

            // Do something with the location information
            // Notify the listener in the main activity
            if (listener != null) {
                listener.onLocationUpdate(latitude, longitude);
            }
        }
    }
}