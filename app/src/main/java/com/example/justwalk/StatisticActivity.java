package com.example.justwalk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.justwalk.databinding.ActivityStatisticsBinding;
import com.github.mikephil.charting.charts.CombinedChart;
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

import java.util.ArrayList;
import java.util.List;


public class StatisticActivity extends DashboardBaseActivity {

    ActivityStatisticsBinding _activityBinding;
    Button _dailyStatsButton;
    Button _weeklyStatsButton;
    Button _monthlyStatsButton;
    Button _locationsStatsButton;
    ProgressBar _stepsProgressBar;
    ProgressBar _pointsProgressBar;
    ProgressBar _distanceProgressBar;
    CombinedChart _stepsChart;
    CombinedChart _pointsChart;
    CombinedChart _distanceChart;
    private Boolean _isDailyStatsDisplayed = false;
    private Boolean _isWeeklyStatsDisplayed = false;
    private Boolean _isMonthlyStatsDisplayed = false;

    DailyStatisticsManager dailyStatisticsManager;
    List<DailyStatistics> _allStats;
    List<DailyStatistics> _dailyStatistics;
    List<DailyStatistics> _weeklyStatistics;
    List<DailyStatistics> _monthlyStatistics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Statistics");

        _dailyStatsButton = _activityBinding.getRoot().findViewById(R.id.activityDailyStatisticsButton);
        _weeklyStatsButton = _activityBinding.getRoot().findViewById(R.id.activityWeeklyStatisticsButton);
        _monthlyStatsButton = _activityBinding.getRoot().findViewById(R.id.activityMonthlyStatisticsButton);
        _locationsStatsButton = _activityBinding.getRoot().findViewById(R.id.activityLocationStatisticsButton);

        _stepsProgressBar = _activityBinding.getRoot().findViewById(R.id.StepsProgressBar);
        _pointsProgressBar = _activityBinding.getRoot().findViewById(R.id.PointsProgressBar);
        _distanceProgressBar = _activityBinding.getRoot().findViewById(R.id.DistanceProgressBar);

        _stepsChart = _activityBinding.getRoot().findViewById(R.id.statisticsChartSteps);
        _pointsChart = _activityBinding.getRoot().findViewById(R.id.statisticsChartPoints);
        _distanceChart = _activityBinding.getRoot().findViewById(R.id.statisticsChartDistance);

        _stepsChart.setVisibility(View.INVISIBLE);
        _pointsChart.setVisibility(View.INVISIBLE);
        _distanceChart.setVisibility(View.INVISIBLE);

        _stepsProgressBar.setVisibility(View.VISIBLE);
        _pointsProgressBar.setVisibility(View.VISIBLE);
        _distanceProgressBar.setVisibility(View.VISIBLE);

        _allStats = new ArrayList<>();
        _dailyStatistics = new ArrayList<>();
        _weeklyStatistics = new ArrayList<>();
        _monthlyStatistics = new ArrayList<>();

        _isDailyStatsDisplayed = true;

        _dailyStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isDailyStatsDisplayed = true;
                _isWeeklyStatsDisplayed = false;
                _isMonthlyStatsDisplayed = false;

                DrawStepsChart();
                DrawPointsChart();
                DrawDistanceChart();
            }
        });

        _weeklyStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isDailyStatsDisplayed = false;
                _isWeeklyStatsDisplayed = true;
                _isMonthlyStatsDisplayed = false;

                DrawStepsChart();
                DrawPointsChart();
                DrawDistanceChart();
            }
        });

        _monthlyStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isDailyStatsDisplayed = false;
                _isWeeklyStatsDisplayed = false;
                _isMonthlyStatsDisplayed = true;

                DrawStepsChart();
                DrawPointsChart();
                DrawDistanceChart();
            }
        });

        _locationsStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatisticActivity.this, LocationsActivity.class);
                startActivity(intent);
            }
        });

        dailyStatisticsManager = new DailyStatisticsManager();
        dailyStatisticsManager.getAllDailyStatistics(new DailyStatisticsManager.OnDailyStatisticsListListener() {
            @Override
            public void onDailyStatisticsList(List<DailyStatistics> dailyStatisticsList) {
                // Process the list of daily statistics
                List<DailyStatistics> dStats = new ArrayList<>();
                List<DailyStatistics> mStats = new ArrayList<>();
                for (DailyStatistics dailyStats : dailyStatisticsList) {
                    _allStats.add(dailyStats);

                    DailyStatistics DailyStat = new DailyStatistics();
                    DailyStat.Date = dailyStats.Date;
                    DailyStat.Steps = dailyStats.Steps;
                    DailyStat.Distance = dailyStats.Distance;
                    DailyStat.CaloriesBurned = dailyStats.CaloriesBurned;
                    DailyStat.Hour= dailyStats.Hour;
                    DailyStat.Points= dailyStats.Points;
                    dStats.add(DailyStat);

                    DailyStatistics MonthlyStat = new DailyStatistics();
                    MonthlyStat.Date = dailyStats.Date;
                    MonthlyStat.Steps = dailyStats.Steps;
                    MonthlyStat.Distance = dailyStats.Distance;
                    MonthlyStat.CaloriesBurned = dailyStats.CaloriesBurned;
                    MonthlyStat.Hour= dailyStats.Hour;
                    MonthlyStat.Points= dailyStats.Points;
                    mStats.add(MonthlyStat);
                }

                _monthlyStatistics = Utility.AggregateByMonth(mStats);
                //_monthlyStatistics = _allStats;

                _weeklyStatistics = DailyStatistics.GetLastWeek(DailyStatistics.AgregateDay(_allStats));
                _weeklyStatistics = Utility.SortDates(_weeklyStatistics);

                _dailyStatistics = DailyStatistics.GetDailyStats(dStats);
                _dailyStatistics = Utility.SortDates(_dailyStatistics);

                DrawStepsChart();
                DrawPointsChart();
                DrawDistanceChart();
            }
        });
    }
    public void DrawStepsChart(){
        if(_isDailyStatsDisplayed){
            // DAILY

            List<String> hourList = Utility.GenerateHourList();
            ChartDataPlacer.PlaceDailyData(_dailyStatistics, hourList, _stepsChart, 1, "Current day","Steps by hour", "Average daily steps");
        }
        else if(_isWeeklyStatsDisplayed){
            // WEEKLY
            List<String> weekDays = Utility.GetPreviousWeekDays();
            ChartDataPlacer.PlaceDailyData(_weeklyStatistics, weekDays, _stepsChart, 1, "Current day","Steps in each day", "Average weakly steps");
        }
        else if(_isMonthlyStatsDisplayed){
            // MONTHLY
            List<String> months = Utility.GetPrevoiusMonths();
            ChartDataPlacer.PlaceDailyData(_monthlyStatistics, months, _stepsChart, 1, "Current day","Steps in each month", "Average yearly steps");
        }

        _stepsProgressBar.setVisibility(View.INVISIBLE);
        _stepsChart.setVisibility(View.VISIBLE);

    }
    public void DrawPointsChart(){
        if(_isDailyStatsDisplayed){
            // DAILY

            List<String> hourList = Utility.GenerateHourList();
            ChartDataPlacer.PlaceDailyData(_dailyStatistics, hourList, _pointsChart, 2, "Current day","Points by hour", "Average daily points");
        }
        else if(_isWeeklyStatsDisplayed){
            // WEEKLY
            List<String> weekDays = Utility.GetPreviousWeekDays();
            ChartDataPlacer.PlaceDailyData(_weeklyStatistics, weekDays, _pointsChart, 2, "Current day","Points in each day", "Average weakly points");
        }
        else if(_isMonthlyStatsDisplayed){
            // MONTHLY
            List<String> months = Utility.GetPrevoiusMonths();
            ChartDataPlacer.PlaceDailyData(_monthlyStatistics, months, _pointsChart, 2, "Current day","Points in each month", "Average yearly points");
        }

        _pointsProgressBar.setVisibility(View.INVISIBLE);
        _pointsChart.setVisibility(View.VISIBLE);

    }
    public void DrawDistanceChart(){
        if(_isDailyStatsDisplayed){
            // DAILY

            List<String> hourList = Utility.GenerateHourList();
            ChartDataPlacer.PlaceDailyData(_dailyStatistics, hourList, _distanceChart, 3, "Current day","Distance by hour", "Average daily distance");
        }
        else if(_isWeeklyStatsDisplayed){
            // WEEKLY
            List<String> weekDays = Utility.GetPreviousWeekDays();
            ChartDataPlacer.PlaceDailyData(_weeklyStatistics, weekDays, _distanceChart, 3, "Current day","Distance in each day", "Average weakly distance");
        }
        else if(_isMonthlyStatsDisplayed){
            // MONTHLY
            List<String> months = Utility.GetPrevoiusMonths();
            ChartDataPlacer.PlaceDailyData(_monthlyStatistics, months, _distanceChart, 3, "Current day","Distance in each month", "Average yearly distance");
        }

        _distanceProgressBar.setVisibility(View.INVISIBLE);
        _distanceChart.setVisibility(View.VISIBLE);

    }
}