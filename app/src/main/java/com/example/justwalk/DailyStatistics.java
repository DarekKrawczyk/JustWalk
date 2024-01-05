package com.example.justwalk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyStatistics {
    public String UserID;
    public String Date;
    public String Hour;
    public double Distance;
    public int Points;
    public int Steps;
    public double CaloriesBurned;

    public DailyStatistics(){

    }
    public DailyStatistics(String userID, String date, String hour, double distance, int points, int steps, double calories){
        UserID = userID;
        Date = date;
        Hour = hour;
        Distance = distance;
        Points = points;
        Steps = steps;
        CaloriesBurned = calories;
    }


    public static List<DailyStatistics> GetLastWeek(List<DailyStatistics> data) {
        List<DailyStatistics> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Get the current date
        Date currentDate = new Date();

        // Create a calendar instance for date manipulation
        Calendar calendar = Calendar.getInstance();

        // Format for day-month-year comparison
        SimpleDateFormat dayMonthYearFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Iterate over the last 7 days
        for (int i = 0; i < 7; i++) {
            // Calculate the date for the current iteration
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            Date currentDateMinusDays = calendar.getTime();
            String currentDateMinusDaysStr = dayMonthYearFormat.format(currentDateMinusDays);

            // Check if there is data for the current date (day-month-year comparison)
            boolean hasData = false;
            for (DailyStatistics entry : data) {
                try {
                    Date entryDate = dateFormat.parse(entry.Date);
                    String entryDateStr = dayMonthYearFormat.format(entryDate);

                    if (currentDateMinusDaysStr.equals(entryDateStr)) {
                        result.add(entry);
                        hasData = true;
                        break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // If no data for the current date, add a placeholder entry
            if (!hasData) {
                DailyStatistics placeholderEntry = new DailyStatistics("me",currentDateMinusDaysStr, Utility.GetCurrentHour(),0.0, 0, 0, 0.0);
                result.add(placeholderEntry);
            }
        }

        return result;
    }

    public static List<DailyStatistics> GetDailyStats(List<DailyStatistics> data) {
        List<DailyStatistics> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Get the current date
        Date currentDate = new Date();

        // Create a calendar instance for date manipulation
        Calendar calendar = Calendar.getInstance();

        // Format for day-month-year comparison
        SimpleDateFormat dayMonthYearFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<String> hours = Utility.GenerateHourList();

        // Iterate through the last 24 hours of the current day
        for (int hour = 0; hour < 24; hour++) {
            // Calculate the date and hour for the current iteration
            calendar.setTime(currentDate);
            String currentDateStr = dayMonthYearFormat.format(currentDate);
            String currentHourStr = hours.get(hour);

            // Check if there is data for the current date and hour
            boolean hasData = false;
            for (DailyStatistics entry : data) {
                try {
                    String entryDate = entry.Date;
                    //String entryDateStr = dayMonthYearFormat.format(entryDate);

                    if (currentDateStr.equals(entryDate) && currentHourStr.equals(entry.Hour)) {
                        result.add(entry);
                        hasData = true;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // If no data for the current date and hour, add a placeholder entry
            if (!hasData) {
                DailyStatistics placeholderEntry = new DailyStatistics("me", currentDateStr, currentHourStr, 0.0, 0, 0, 0.0);
                result.add(placeholderEntry);
            }
        }

        return result;
    }

    public static List<DailyStatistics> AgregateDay(List<DailyStatistics> input){
        List<DailyStatistics> result = new ArrayList<>();
        for(int i = 0; i < input.size(); i++){
            int existIndex = -1;
            for(int j = 0; j < result.size(); j++){
                if(input.get(i).Date == result.get(j).Date){
                    existIndex = j;
                    break;
                }
            }

            // Item alerady exsits
            if(existIndex >= 0){
                result.get(existIndex).Steps += input.get(i).Steps;
                result.get(existIndex).Points += input.get(i).Points;
                result.get(existIndex).Distance += input.get(i).Distance;
                result.get(existIndex).CaloriesBurned += input.get(i).CaloriesBurned;

            } else{
                // Item doesnt exsit
                result.add(input.get(i));
            }


        }
        return result;
    }

}
