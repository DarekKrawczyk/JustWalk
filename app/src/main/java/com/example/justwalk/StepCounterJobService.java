package com.example.justwalk;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.JobIntentService;

public class StepCounterJobService extends JobService implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private static final String TAG = "StepCounterJobService";

    // Unique job ID
    private static final int JOB_ID = 35745732;

    // Enqueue the work to the JobIntentService
    public static void enqueueWork(Context context, Intent work) {
        ComponentName componentName = new ComponentName(context, StepCounterJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        JobIntentService.enqueueWork(context, StepCounterJobService.class, JOB_ID, work);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                jobScheduler.schedule(jobInfo);
            }
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCounterSensor != null) {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int steps = (int) event.values[0];
            Toast.makeText(this, "STEP" + String.valueOf(steps), Toast.LENGTH_SHORT).show();
            // TODO: Handle the step count data (e.g., update UI, store in database, etc.).
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for the step counter sensor.
    }
}
