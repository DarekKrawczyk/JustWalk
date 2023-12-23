package com.example.justwalk;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCountForegroundService extends Service {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SensorEventListener sensorEventListener;
    private DatabaseReference dailyStatsRef;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "StepCountChannel";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        // Initialize SensorManager and Step Counter Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            dailyStatsRef = FirebaseDatabase.getInstance().getReference().child("DailyStatistics").child(uid);
            showToast("DB connected");
        }
        showToast("StepsCountFGService - oncreate");

        if (stepSensor != null) {
            // Register SensorEventListener
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                        int steps = (int) event.values[0];
                        // Update your step count or perform any necessary actions.
                        showToast("Steps: " + steps);
                        int dsteps = steps;
                        double METvalue = 4.5; // Moderate walk
                        double ddistance = dsteps * 0.7;
                        int dpoints = 100;
                        double distanceKM = ddistance/1000;
                        double dcalories = Utility.CalculateCaloriesBurned(80, distanceKM, METvalue);
                        updateDailyStatistics(ddistance, dpoints, dsteps, dcalories);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Handle accuracy changes if needed.
                }
            };
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

            startForeground(NOTIFICATION_ID, createNotification());
            showToast("StepsCountFGService - registered");
        } else {
            showToast("Step Counter Sensor not available on this device.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the SensorEventListener when the service is destroyed.
        if (sensorManager != null && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
            showToast("Step Counter Sensor service destroyed");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Step Count Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Step Count Service")
                .setContentText("Tracking your steps...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
    }

    public void updateDailyStatistics(final double newDistance, final int newPoints, final int newSteps, final double newCaloriesBurned) {
        if (dailyStatsRef == null) {
            return;
        }

        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentHour = Utility.GetCurrentHour();

        dailyStatsRef.child(todayDate).child(currentHour).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Entry for today and the current hour exists, modify the values
                    DailyStatistics dailyStats = dataSnapshot.getValue(DailyStatistics.class);
                    if (dailyStats != null) {
                        dailyStats.Distance += newDistance;
                        dailyStats.Points += newPoints;
                        dailyStats.Steps += newSteps;
                        dailyStats.CaloriesBurned += newCaloriesBurned;

                        // Update the modified entry
                        dailyStatsRef.child(todayDate).child(String.valueOf(currentHour)).setValue(dailyStats);
                    }
                } else {
                    // Entry for today or the current hour does not exist, add a new entry
                    DailyStatistics newDailyStats = new DailyStatistics();
                    newDailyStats.Date = todayDate;
                    newDailyStats.Hour = currentHour;
                    newDailyStats.Distance = newDistance;
                    newDailyStats.Points = newPoints;
                    newDailyStats.Steps = newSteps;
                    newDailyStats.CaloriesBurned = newCaloriesBurned;

                    // Add the new entry
                    dailyStatsRef.child(todayDate).child(String.valueOf(currentHour)).setValue(newDailyStats);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
