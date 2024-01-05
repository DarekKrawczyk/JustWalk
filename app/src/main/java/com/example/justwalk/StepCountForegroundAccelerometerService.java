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
import java.util.Random;

public class StepCountForegroundAccelerometerService extends Service {

    public static final String ACTION_STEPS_UPDATE = "com.example.justwalk.action.STEPS_UPDATE";
    private int STEPS_LIMIT_DB_CALL = 101;
    private double MagnitugePrvious = 0;
    private Integer stepCounterAcc = 0;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SensorEventListener sensorEventListener;
    private DatabaseReference dailyStatsRef;
    Random random = new Random();
    int stepsDBCAP = 0;

    int _lastPoints = 0;
    double _lastDistance = 0;
    double _lastCalories = 0;

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

        stepsDBCAP = random.nextInt(STEPS_LIMIT_DB_CALL);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            dailyStatsRef = FirebaseDatabase.getInstance().getReference().child("DailyStatistics").child(uid);
            //showToast("DB connected");
        }
        //showToast("StepsCountFGService - oncreate");


        if (stepSensor != null) {
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if(event != null){
                        float x_acc = event.values[0];
                        float y_acc = event.values[1];
                        float z_acc = event.values[2];

                        double magnitude = Math.sqrt(x_acc*x_acc + y_acc*y_acc + z_acc*z_acc);
                        double MagDelta = magnitude - MagnitugePrvious;
                        MagnitugePrvious = magnitude;

                        if(MagDelta > 6){

                            if(stepCounterAcc > stepsDBCAP){
                                stepsDBCAP = random.nextInt(STEPS_LIMIT_DB_CALL);

                                int dsteps = stepCounterAcc;

                                stepCounterAcc = 0;

                                double METvalue = 4.5; // Moderate walk
                                double ddistance = dsteps * 0.7;
                                int dpoints = Utility.CalculatePoints(dsteps);
                                double distanceKM = ddistance/1000;
                                double dcalories = Utility.CalculateCaloriesBurned(dsteps);
                                //double dcalories = Utility.CalculateCaloriesBurned(80, distanceKM, METvalue);
                                updateDailyStatistics(ddistance, dpoints, dsteps, dcalories);
                                //Toast.makeText(StepCountForegroundAccelerometerService.this, "ADDED TO DB", Toast.LENGTH_SHORT).show();

                                _lastCalories = dcalories;
                                _lastPoints = dpoints;
                                _lastDistance = ddistance;

                                // NOTIFY UI
                                Intent intent = new Intent(ACTION_STEPS_UPDATE);
                                intent.putExtra("distance", ddistance);
                                intent.putExtra("points", dpoints);
                                intent.putExtra("steps", dsteps);
                                intent.putExtra("calories", dcalories);
                                sendBroadcast(intent);
                            }

                            stepCounterAcc++;
                            //Toast.makeText(StepCountForegroundAccelerometerService.this, "STEPS: " + String.valueOf(stepCounterAcc), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Handle accuracy changes if needed.
                }
            };
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

            startForeground(NOTIFICATION_ID, createNotification());
            //showToast("StepsCountFGService - registered");
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
            //
            // showToast("Step Counter Sensor service destroyed");
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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String currentUserID = user.getUid();

        dailyStatsRef.child(currentUserID).child(todayDate).child(String.valueOf(currentHour))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DailyStatistics dailyStats = dataSnapshot.getValue(DailyStatistics.class);
                            if (dailyStats != null) {
                                dailyStats.Distance += newDistance;
                                dailyStats.Points += newPoints;
                                dailyStats.Steps += newSteps;
                                dailyStats.CaloriesBurned += newCaloriesBurned;

                                dailyStatsRef.child(todayDate).child(String.valueOf(currentHour)).setValue(dailyStats);
                            }
                        } else {
                            DailyStatistics newDailyStats = new DailyStatistics();
                            newDailyStats.UserID = currentUserID;
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
