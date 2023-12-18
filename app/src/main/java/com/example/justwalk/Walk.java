package com.example.justwalk;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Walk {
    public Map<String, String> Timestamp;
    public double Distance;
    public int Points;
    public int Steps;
    public double CaloriesBurned;
    public List<Place> Places;

    public Walk(Map<String, String> timestamp, double distance, int steps, int points, double caloriesBurned, List<Place> places){
        Timestamp = timestamp;
        Distance = distance;
        Steps = steps;
        Points = points;
        CaloriesBurned = caloriesBurned;
        Places = places;
    }
}
