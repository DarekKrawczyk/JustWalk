package com.example.justwalk;

import android.graphics.Color;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ChartDataPlacer {
    public static void PlaceDailyData(List<DailyStatistics> _dailyStatistics, List<String> chartLabels, CombinedChart chart, Integer itemType, String currentItemCaption,String dataCaptionChart, String dataCaptionAvg){
        // Item type: 1) Steps; 2) Points; 3) Distance.
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < _dailyStatistics.size(); i++) {
            float data;
            if(itemType == 1){
                data = _dailyStatistics.get(i).Steps;
            }
            else if (itemType == 2){
                data = _dailyStatistics.get(i).Points;
            }
            else {
                data = (float)_dailyStatistics.get(i).Distance;
            }
            barEntries.add(new BarEntry(i, data));
            colors.add(Color.rgb(66, 133, 244));
        }
        // For daily
        if(chartLabels.size() == 24){

            int currentHour = colors.size()-1;
            LocalTime currentTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currentTime = LocalTime.now();
                currentHour = currentTime.getHour();

                colors.set(currentHour, Color.rgb(255, 255, 255));
            }
        }
        else {
            colors.set(colors.size()-1, Color.rgb(255, 255, 255));
        }

        // BarDataSet customization
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(colors);

        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Display text only for positive values
                if (value > 0) {
                    //return String.valueOf(value);
                    return "";
                } else {
                    return ""; // Empty string for non-positive values
                }
            }
        });

        // BarData customization
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f); // Set bar width

        // Calculate average value for the week's steps
        float avgVal = 0;

        if(itemType == 1){
            avgVal = Utility.CalculateAverageSteps(_dailyStatistics);
        }
        else if (itemType == 2){
            avgVal = Utility.CalculateAveragePoints(_dailyStatistics);
        }
        else {
            avgVal = Utility.CalculateAverageDistance(_dailyStatistics);
        }

        // Add a red line representing the average value
        ArrayList<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < _dailyStatistics.size(); i++) {
            lineEntries.add(new Entry(i, avgVal));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setDrawIcons(false);
        lineDataSet.setColor(Color.RED);

        LineData lineData = new LineData(lineDataSet);
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value > 0) {
                    return "";
                } else {
                    return "";
                }
            }
        });

        // X-axis customization
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(chartLabels));
        xAxis.setDrawAxisLine(false); // Hide the X-axis line
        xAxis.setAxisMinimum(-0.6f);

        float limitx = (float)_dailyStatistics.size() - 0.60f;

        xAxis.setAxisMaximum(limitx);
        xAxis.setGranularity(0); // Set the granularity of x-axis labels

        // Y-axis customization
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setDrawAxisLine(true);
        yAxisLeft.setDrawLabels(true);

        // Disable the right Y-axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Additional styling
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.setDrawBorders(false);
        chart.animateY(1000);

        // Set both BarData and LineData to CombinedData
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        // Customizing appearance of the chart
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.BLACK);
        chart.setBorderWidth(2f);

        // Disable legend
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);

        chart.setData(combinedData);

        chart.invalidate();
    }
}
