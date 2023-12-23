package com.example.justwalk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StepChangeBroadcastReceiver extends BroadcastReceiver {
        private final String TAG = "StepChangeBroadcastReceiver";
        private StepChangeListener listener;

        public StepChangeBroadcastReceiver(StepChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(StepCountForegroundAccelerometerService.ACTION_STEPS_UPDATE)) {
                double distance = intent.getDoubleExtra("distance", 0.0);
                int points = intent.getIntExtra("points", 0);
                int steps = intent.getIntExtra("steps", 0);
                double calories = intent.getDoubleExtra("calories", 0.0);

                DailyStatistics ds = new DailyStatistics();
                ds.Steps = steps;
                ds.Points = points;
                ds.Distance = distance;
                ds.CaloriesBurned = calories;

                Log.d(TAG, "Received daily statistics update: " + distance + ", " + points + ", " + steps + ", " + calories);

                if (listener != null) {
                    listener.onStepChange(ds);
                }
            }
        }
    }
