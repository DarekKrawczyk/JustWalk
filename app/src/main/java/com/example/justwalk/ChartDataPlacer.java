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
        colors.set(colors.size()-1, Color.rgb(255, 255, 255));

        // BarDataSet customization
        BarDataSet barDataSet = new BarDataSet(barEntries, dataCaptionChart);
        //barDataSet.setColor(Color.rgb(66, 133, 244)); // Change to your desired color

        barDataSet.setColors(colors);
        //barDataSet.setValueTextColor(Color.rgb(66, 133, 244)); // Value text color
        //barDataSet.setValueTextSize(15f);


        // Set a custom ValueFormatter to conditionally display text labels for positive values only
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
        float averageSteps = Utility.CalculateAverageSteps(_dailyStatistics);

        // Add a red line representing the average value
        ArrayList<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < _dailyStatistics.size(); i++) {
            lineEntries.add(new Entry(i, averageSteps));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, dataCaptionAvg);
        lineDataSet.setDrawIcons(false);
        lineDataSet.setColor(Color.RED);
        //lineDataSet.setDrawValues(true); // Do not display values for the line
        //lineDataSet.setValueTextSize(20f);
        //lineDataSet.setValueTextColor(Color.rgb(255, 0, 0)); // Value text color
        LineData lineData = new LineData(lineDataSet);
        lineDataSet.setValueFormatter(new ValueFormatter() {
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
        yAxisLeft.setDrawGridLines(true); // Hide grid lines on the left Y-axis
        yAxisLeft.setDrawAxisLine(true); // Hide the Y-axis line
        yAxisLeft.setDrawLabels(true); // Hide Y-axis labels

        // Disable the right Y-axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Additional styling
        Description description = new Description();
        description.setText(""); // Remove the Y-axis description on the left side
        chart.setDescription(description); // Disable description
        chart.setDrawBorders(false); // Disable chart borders
        chart.animateY(1000); // Animation duration

        // Set both BarData and LineData to CombinedData
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        // Customizing appearance of the chart
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.BLACK);
        chart.setBorderWidth(2f);

        // Customizing the appearance of the legend
        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        chart.setData(combinedData);

        // Invalidate the chart to apply changes
        chart.invalidate();
    }

    public static void PlaceWeaklyData(List<DailyStatistics> statistics, List<String> chartLabels, CombinedChart chart, Integer itemType, String dataCaption){
        // Item type: 1) Steps; 2) Points; 3) Distance.
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < statistics.size(); i++) {
            float data;
            if(itemType == 1){
                data = statistics.get(i).Steps;
            }
            else if (itemType == 2){
                data = statistics.get(i).Points;
            }
            else {
                data = (float)statistics.get(i).Distance;
            }
            barEntries.add(new BarEntry(i, data));
        }


        // BarDataSet customization
        String barDataLabe = "Today's " + dataCaption;
        BarDataSet barDataSet = new BarDataSet(barEntries, barDataLabe);
        barDataSet.setColor(Color.rgb(66, 133, 244)); // Change to your desired color
        barDataSet.setValueTextColor(Color.rgb(66, 133, 244)); // Value text color
        barDataSet.setValueTextSize(15f);

        // Set a custom ValueFormatter to conditionally display text labels for positive values only
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Display text only for positive values
                if (value > 0) {
                    return String.valueOf(value);
                } else {
                    return ""; // Empty string for non-positive values
                }
            }
        });

        // BarData customization
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f); // Set bar width

        // Calculate average value for the week's steps
        float averageSteps = Utility.CalculateAverageSteps(statistics);

        // Add a red line representing the average value
        ArrayList<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < statistics.size(); i++) {
            lineEntries.add(new Entry(i, averageSteps));
        }

        String lineDataLabel = "Averege " + dataCaption;
        LineDataSet lineDataSet = new LineDataSet(lineEntries, lineDataLabel);
        lineDataSet.setDrawIcons(false);
        lineDataSet.setColor(Color.RED);
        //lineDataSet.setDrawValues(true); // Do not display values for the line
        //lineDataSet.setValueTextSize(20f);
        //lineDataSet.setValueTextColor(Color.rgb(255, 0, 0)); // Value text color
        LineData lineData = new LineData(lineDataSet);

        // X-axis customization
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(chartLabels));
        xAxis.setDrawAxisLine(false); // Hide the X-axis line

        // Y-axis customization
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setDrawGridLines(false); // Hide grid lines on the left Y-axis
        yAxisLeft.setDrawAxisLine(false); // Hide the Y-axis line
        yAxisLeft.setDrawLabels(false); // Hide Y-axis labels

        // Disable the right Y-axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Additional styling
        Description description = new Description();
        description.setText(""); // Remove the Y-axis description on the left side
        chart.setDescription(description); // Disable description
        chart.setDrawBorders(false); // Disable chart borders
        chart.animateY(1000); // Animation duration

        // Set both BarData and LineData to CombinedData
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        chart.setData(combinedData);

        // Invalidate the chart to apply changes
        chart.invalidate();
    }
}
