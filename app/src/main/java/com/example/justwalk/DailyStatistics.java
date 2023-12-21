package com.example.justwalk;

public class DailyStatistics {
    public String Date;
    public double Distance;
    public int Points;
    public int Steps;
    public double CaloriesBurned;

    public DailyStatistics(){

    }
    public DailyStatistics(String date, double distance, int points, int steps, double calories){
        Date = date;
        Distance = distance;
        Points = points;
        Steps = steps;
        CaloriesBurned = calories;
    }
}
