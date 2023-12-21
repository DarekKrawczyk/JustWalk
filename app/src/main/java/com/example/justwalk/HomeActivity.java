package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.justwalk.databinding.ActivityDashboardBinding;
import com.example.justwalk.databinding.ActivityHomeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends DashboardBaseActivity {

    ActivityHomeBinding _activityBinding;
    DailyStatisticsManager dailyStatisticsManager;
    List<DailyStatistics> _dailyStatistics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Home");


        BarChart barChart = _activityBinding.activityHomeBarChart;

        _dailyStatistics = new ArrayList<>();

        dailyStatisticsManager = new DailyStatisticsManager();
        dailyStatisticsManager.getAllDailyStatistics(new DailyStatisticsManager.OnDailyStatisticsListListener() {
            @Override
            public void onDailyStatisticsList(List<DailyStatistics> dailyStatisticsList) {
                // Process the list of daily statistics
                for (DailyStatistics dailyStats : dailyStatisticsList) {
                    String date = dailyStats.Date;
                    double distance = dailyStats.Distance;
                    int points = dailyStats.Points;
                    int steps = dailyStats.Steps;
                    double caloriesBurned = dailyStats.CaloriesBurned;

                    //TODO: implement geting data!
                }
            }
        });


        // Sample data
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 10));
        entries.add(new BarEntry(2, 20));
        entries.add(new BarEntry(3, 30));
        entries.add(new BarEntry(4, 40));

        BarDataSet dataSet = new BarDataSet(entries, "Sample Data");
        dataSet.setColor(Color.rgb(0, 155, 0)); // Set color

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f); // Set bar width

        barChart.setData(barData);

        // Customize X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter());

        // Customize Y-axis
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        // Optional: Customize additional chart properties
        // ...

        // Invalidate the chart to refresh
        barChart.invalidate();
    }

    // Custom ValueFormatter for X-axis labels
    private static class XAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            // Convert float value to the desired X-axis label
            return String.valueOf((int) value);
        }
    }
}