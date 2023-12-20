package com.example.justwalk;

import static java.text.DateFormat.getDateTimeInstance;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    public static String ConvertTimestampToHoursAndMinutes(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Date date = new Date(timestamp);

        return sdf.format(date);
    }
    public static double ConvertToTotalSeconds(String time) {
        String[] timeComponents = time.split(":");

        int minutes = Integer.parseInt(timeComponents[0]);
        int seconds = Integer.parseInt(timeComponents[1]);
        double milliseconds = Double.parseDouble(timeComponents[2]);

        double secondsFromMinutes = minutes * 60.0;
        double secondsFromMilliseconds = milliseconds / 1000.0;

        double totalSeconds = secondsFromMinutes + seconds + secondsFromMilliseconds;

        return totalSeconds;
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

    public static String ParsePlacesToString(List<Place> places){
        if(places == null){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (Place place : places) {
            if (place != null) {
                stringBuilder.append(place.Name).append(" * ");
            }
        }

        int length = stringBuilder.length();
        if (length >= 3) {
            stringBuilder.setLength(length - 3);
        }

        return stringBuilder.toString();
    }

    public static String CalculateDuration(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            long durationMillis = endDate.getTime() - startDate.getTime();

            long hours = durationMillis / (60 * 60 * 1000);
            long minutes = (durationMillis % (60 * 60 * 1000)) / (60 * 1000);

            return String.format("%02d:%02d", hours, minutes);

        } catch (ParseException e) {
            e.printStackTrace();
            return "00:00";
        }
    }

    public static String ConvertTimestampToString(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date(timestamp);

        return sdf.format(date);
    }

}
