package com.example.justwalk;

import static java.text.DateFormat.getDateTimeInstance;

import java.text.DateFormat;
import java.util.Date;

public class Utility {
    public static int CalculateSteps(double distance, double strideLength){
        return (int) Math.round(distance / strideLength);
    }
    public static double CalculateCaloriesBurned(double weightKg, double distanceKm, double metValue) {
        return (metValue * weightKg * distanceKm) / 200.0;
    }

    public static double ConvertToTotalMinutes(String time) {
        String[] timeComponents = time.split(":");

        int minutes = Integer.parseInt(timeComponents[0]);
        int seconds = Integer.parseInt(timeComponents[1]);
        double milliseconds = Double.parseDouble(timeComponents[2]);

        double minutesFromSeconds = seconds / 60.0;
        double minutesFromMilliseconds = milliseconds / 60000.0;

        double totalMinutes = minutes + minutesFromSeconds + minutesFromMilliseconds;

        return totalMinutes;
    }

    public static String GetTimeDate(long timestamp){
        try{
            DateFormat dateFormat = getDateTimeInstance();
            Date netDate = (new Date(timestamp));
            return dateFormat.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }
}
