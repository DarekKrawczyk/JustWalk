package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

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

public class HomeActivity extends DashboardBaseActivity {

    ActivityHomeBinding _activityBinding;
    DailyStatisticsManager dailyStatisticsManager;
    List<DailyStatistics> _dailyStatistics;
    Boolean _isLoadingFinished = false;
    List<String> _previous7DaysNames;
    TextView _activity_home_calories_text;
    TextView _activity_home_steps_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Home");

        _previous7DaysNames = new ArrayList<>();
        _previous7DaysNames = Utility.GetPreviousWeekDays();

        CombinedChart combinedChart = _activityBinding.activityHomeBarChart;
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
                _dailyStatistics = DailyStatistics.GetLastWeek(_dailyStatistics);
                _dailyStatistics = Utility.SortDates(_dailyStatistics);

                // Get this day
                DailyStatistics todayStats = _dailyStatistics.get(_dailyStatistics.size()-1);
                _activity_home_steps_text.setText(String.valueOf(todayStats.Steps));

                String calors = String.valueOf((int) Math.round(todayStats.CaloriesBurned));

                _activity_home_calories_text.setText(calors);

                // Sample data
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                for (int i = 0; i < _dailyStatistics.size(); i++) {
                    barEntries.add(new BarEntry(i, _dailyStatistics.get(i).Steps));
                }

                // BarDataSet customization
                BarDataSet barDataSet = new BarDataSet(barEntries, "Steps of last week");
                barDataSet.setColor(Color.rgb(66, 133, 244)); // Change to your desired color
                barDataSet.setValueTextColor(Color.rgb(66, 133, 244)); // Value text color
                barDataSet.setValueTextSize(20f);

                // BarData customization
                BarData barData = new BarData(barDataSet);
                barData.setBarWidth(0.7f); // Set bar width

                // Calculate average value for the week's steps
                float averageSteps = Utility.CalculateAverageSteps(_dailyStatistics);

                // Add a red line representing the average value
                ArrayList<Entry> lineEntries = new ArrayList<>();
                for (int i = 0; i < _dailyStatistics.size(); i++) {
                    lineEntries.add(new Entry(i, averageSteps));
                }
                LineDataSet lineDataSet = new LineDataSet(lineEntries, "Average Steps");
                lineDataSet.setDrawIcons(false);
                lineDataSet.setColor(Color.RED);
                lineDataSet.setDrawValues(true); // Do not display values for the line
                lineDataSet.setValueTextSize(20f);
                lineDataSet.setValueTextColor(Color.rgb(255, 0, 0)); // Value text color
                LineData lineData = new LineData(lineDataSet);

                // X-axis customization
                XAxis xAxis = combinedChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(_previous7DaysNames));
                xAxis.setDrawAxisLine(false); // Hide the X-axis line

                // Y-axis customization
                YAxis yAxisLeft = combinedChart.getAxisLeft();
                yAxisLeft.setAxisMinimum(0f);
                yAxisLeft.setDrawGridLines(false); // Hide grid lines on the left Y-axis
                yAxisLeft.setDrawAxisLine(false); // Hide the Y-axis line
                yAxisLeft.setDrawLabels(false); // Hide Y-axis labels

                // Disable the right Y-axis
                YAxis yAxisRight = combinedChart.getAxisRight();
                yAxisRight.setEnabled(false);

                // Additional styling
                Description description = new Description();
                description.setText(""); // Remove the Y-axis description on the left side
                combinedChart.setDescription(description); // Disable description
                combinedChart.setDrawBorders(false); // Disable chart borders
                combinedChart.animateY(1000); // Animation duration

                // Set both BarData and LineData to CombinedData
                CombinedData combinedData = new CombinedData();
                combinedData.setData(barData);
                combinedData.setData(lineData);

                combinedChart.setData(combinedData);

                // Invalidate the chart to apply changes
                combinedChart.invalidate();
            }
        });


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