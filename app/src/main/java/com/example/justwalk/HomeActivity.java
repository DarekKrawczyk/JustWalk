package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.example.justwalk.databinding.ActivityDashboardBinding;
import com.example.justwalk.databinding.ActivityHomeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;

public class HomeActivity extends DashboardBaseActivity implements  StepChangeListener{

    ActivityHomeBinding _activityBinding;
    DailyStatisticsManager dailyStatisticsManager;
    List<DailyStatistics> _dailyStatistics;
    Boolean _isLoadingFinished = false;
    List<String> _previous7DaysNames;
    TextView _activity_home_calories_text;
    TextView _activity_home_steps_text;

    CardView stepsCardView;
    CardView caloriesCardView;

    private double MagnitugePrvious = 0;
    private Integer stepCounterAcc = 0;
    private SensorManager _sensorManager;
    private Sensor _stepCounterSensor;
    private int _stepsCounted;

    private ProgressBar loadingChartProgressBar;
    private StepChangeBroadcastReceiver stepsRevicer;
    CombinedChart combinedChart;

    protected void onPause(){
        super.onPause();

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putInt("StepCount", stepCounterAcc);
        editor.apply();
    }

    protected void onStop(){
        super.onStop();

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putInt("StepCount", stepCounterAcc);
        editor.apply();
    }

    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        stepCounterAcc = sharedPref.getInt("StepCount", 0);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stepsRevicer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Home");

        UserHolder.LoadUser();

        stepsCardView = _activityBinding.getRoot().findViewById(R.id.StepsCardView);
        caloriesCardView = _activityBinding.getRoot().findViewById(R.id.CaloriesCardView);


        combinedChart = _activityBinding.activityHomeBarChart;
        loadingChartProgressBar = _activityBinding.getRoot().findViewById(R.id.DailyProgressBar);

        loadingChartProgressBar.setVisibility(View.VISIBLE);
        combinedChart.setVisibility(View.INVISIBLE);


        // STEP COUNTER
        /*
        _sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        _stepCounterSensor = _sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(_stepCounterSensor == null){
            Toast.makeText(this, "STEP COUNTER NOT AVAILABLE", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "STEP COUNTER OK", Toast.LENGTH_SHORT).show();
        }
        */
        // STEP COUNTER FORGROUND SERVICE - NOT WORKING XD
        //startService(new Intent(this, StepCountForegroundService.class));

        // STEP COUNTER BY ACCELOROMETER
        /*
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener stepDetector = new SensorEventListener() {
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
                        stepCounterAcc++;
                        Toast.makeText(HomeActivity.this, "STEPS: " + String.valueOf(stepCounterAcc), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
         */
        // END SENSOR
        stepsRevicer = new StepChangeBroadcastReceiver(this);
        registerReceiver(stepsRevicer, new IntentFilter(StepCountForegroundAccelerometerService.ACTION_STEPS_UPDATE));
        startService(new Intent(this, StepCountForegroundAccelerometerService.class));


        _previous7DaysNames = new ArrayList<>();
        _previous7DaysNames = Utility.GetPreviousWeekDays();

        _activity_home_steps_text = _activityBinding.getRoot().findViewById(R.id.activityHomeStepsText);
        _activity_home_calories_text = _activityBinding.getRoot().findViewById(R.id.activityHomeCaloriesText);

        _dailyStatistics = new ArrayList<>();

        dailyStatisticsManager = new DailyStatisticsManager();
        dailyStatisticsManager.getAllDailyStatistics(new DailyStatisticsManager.OnDailyStatisticsListListener() {
            @Override
            public void onDailyStatisticsList(List<DailyStatistics> dailyStatisticsList) {
                // Process the list of daily statistics
                for (DailyStatistics dailyStats : dailyStatisticsList) {
                    _dailyStatistics.add(dailyStats);
                    String date = dailyStats.Date;
                    double distance = dailyStats.Distance;
                    int points = dailyStats.Points;
                    int steps = dailyStats.Steps;
                    double caloriesBurned = dailyStats.CaloriesBurned;

                    //TODO: implement geting data
                }
                _isLoadingFinished = true;
                List<DailyStatistics> _weeklyStatistics = DailyStatistics.GetLastWeek(DailyStatistics.AgregateDay(_dailyStatistics));
                _weeklyStatistics = Utility.SortDates(_weeklyStatistics);

                stepsCardView.animate().rotationX(360).setDuration(500).setStartDelay(0);
                caloriesCardView.animate().rotationX(360).setDuration(500).setStartDelay(0);

                // Get this day
                DailyStatistics todayStats = _weeklyStatistics.get(_weeklyStatistics.size()-1);
                _activity_home_steps_text.setText(String.valueOf(todayStats.Steps));

                String calors = String.valueOf((int) Math.round(todayStats.CaloriesBurned));

                _activity_home_calories_text.setText(calors);

                List<String> weekDays = Utility.GetPreviousWeekDays();
                ChartDataPlacer.PlaceDailyData(_weeklyStatistics, weekDays, combinedChart, 1, "Current day", "Steps in each day", "Average weakly steps");

                loadingChartProgressBar.setVisibility(View.INVISIBLE);
                combinedChart.setVisibility(View.VISIBLE);

            }
        });


    }

    @Override
    public void onStepChange(DailyStatistics stats) {
        String cals = _activity_home_calories_text.getText().toString();
        String steps = _activity_home_steps_text.getText().toString();
        try{
            double calsDbl = Double.valueOf(cals);
            Integer stepsInt = Integer.parseInt(steps);

            calsDbl += stats.CaloriesBurned;
            stepsInt += stats.Steps;

            int calsInt = (int) calsDbl;

            _activity_home_steps_text.setText(String.valueOf(stepsInt));
            _activity_home_calories_text.setText(String.valueOf(calsInt));

        }catch(Exception ex){
            Toast.makeText(this, "CALORIES CAST ERROR", Toast.LENGTH_SHORT).show();
        }
    }
}