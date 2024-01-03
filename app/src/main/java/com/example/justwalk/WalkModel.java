package com.example.justwalk;

import java.util.List;
import java.util.Map;

public class WalkModel {
    public String Date;
    public String StartingTime;
    public String EndTime;
    public String Duration;
    public double Distance;
    public int Points;
    public int Steps;
    public double CaloriesBurned;
    public String Places;
    public int Image;

    public WalkModel(String date, String startingTime, String endTime, String duration, double distance, int points, int steps, double calories, String places, int image){
        Date = date;
        StartingTime = startingTime;
        EndTime = endTime;
        Duration = duration;
        Distance = distance;
        Points = points;
        Steps = steps;
        CaloriesBurned = calories;
        Places = places;
        Image = image;
    }
}