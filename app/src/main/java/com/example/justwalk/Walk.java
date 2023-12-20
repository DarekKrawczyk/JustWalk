package com.example.justwalk;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Walk {
    public long Timestamp;
    public long StartingTimeInMilis;
    public long EndingTimeInMilis;
    public long DurationTimeInSecs;
    public double Distance;
    public int Points;
    public int Steps;
    public double CaloriesBurned;
    public List<Place> Places;

    public Walk(){

    }
    public Walk(long timestamp, long startingTimeInMilis,long endingTimeInMilis, long durationTimeInSecs, double distance, int steps, int points, double caloriesBurned, List<Place> places){
        Timestamp = timestamp;
        StartingTimeInMilis = startingTimeInMilis;
        EndingTimeInMilis = endingTimeInMilis;
        DurationTimeInSecs = durationTimeInSecs;
        Distance = distance;
        Steps = steps;
        Points = points;
        CaloriesBurned = caloriesBurned;
        Places = places;
    }
}
