package com.example.justwalk;

import static java.text.DateFormat.getDateTimeInstance;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public static List<String> GetPreviousWeekDays() {
        List<String> previousDays = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            // Get the current date
            Date currentDate = calendar.getTime();

            // Get the name of the current day
            String currentDay = dayFormat.format(currentDate);

            // Add the current day to the list
            previousDays.add(currentDay);

            // Move to the previous day
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        // Reverse the list to have the days in chronological order
        Collections.reverse(previousDays);

        return previousDays;
    }

    public static List<DailyStatistics> SortDates(List<DailyStatistics> dateStrings) {
        Collections.sort(dateStrings, new Comparator<DailyStatistics>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            public int compare(DailyStatistics dateStr1, DailyStatistics dateStr2) {
                try {
                    Date date1 = dateFormat.parse(dateStr1.Date);
                    Date date2 = dateFormat.parse(dateStr2.Date);
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        return dateStrings;
    }

    public static float CalculateAverageSteps(List<DailyStatistics> dailyStatisticsList) {
        if (dailyStatisticsList.isEmpty()) {
            return 0f;
        }

        int totalSteps = 0;
        for (DailyStatistics entry : dailyStatisticsList) {
            totalSteps += entry.Steps;
        }

        return (float) totalSteps / dailyStatisticsList.size();
    }

    static List<String> GenerateHourList() {
        List<String> hourList = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            String formattedHour = String.format("%02d:00", hour);
            hourList.add(formattedHour);
        }

        return hourList;
    }

    static String GetCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        String formattedHour = String.format("%02d", currentHour);

        return formattedHour + ":00";
    }

    public static List<DailyStatistics> AggregateByMonth(List<DailyStatistics> data) {
        List<DailyStatistics> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Map to store aggregated statistics for each month
        // The key is the month-year format, e.g., "2023-01"
        // The value is the aggregated statistics for that month
        java.util.Map<String, DailyStatistics> monthlyAggregationMap = new java.util.HashMap<>();

        // Iterate through the data and aggregate by month
        for (DailyStatistics entry : data) {
            try {
                Date entryDate = dateFormat.parse(entry.Date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(entryDate);

                // Get the month and year
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1; // Months are zero-based

                // Create a key for the month-year format
                String monthYearKey = String.format("%04d-%02d", year, month);

                // Get or create the aggregated statistics for the month
                DailyStatistics aggregatedStats = monthlyAggregationMap.get(monthYearKey);
                if (aggregatedStats == null) {
                    aggregatedStats = new DailyStatistics();
                    aggregatedStats.Date = monthYearKey;
                    aggregatedStats.Hour = "00:00";
                    monthlyAggregationMap.put(monthYearKey, aggregatedStats);
                }

                // Aggregate the values for the month
                aggregatedStats.Distance += entry.Distance;
                aggregatedStats.Steps += entry.Steps;
                aggregatedStats.Points += entry.Points;
                aggregatedStats.CaloriesBurned += entry.CaloriesBurned;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Add default entries for months with no existing data
        addDefaultEntries(monthlyAggregationMap);

        // Add aggregated entries to the result
        result.addAll(monthlyAggregationMap.values());

        // Sort the result list by month-year key
        Collections.sort(result, new Comparator<DailyStatistics>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

            @Override
            public int compare(DailyStatistics o1, DailyStatistics o2) {
                try {
                    Date date1 = dateFormat.parse(o1.Date);
                    Date date2 = dateFormat.parse(o2.Date);
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        return result;
    }

    private static void addDefaultEntries(java.util.Map<String, DailyStatistics> monthlyAggregationMap) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("yyyy-MM");

        // Iterate over the last 12 months
        for (int i = 0; i < 12; i++) {
            // Calculate the month for the current iteration
            calendar.setTime(currentDate);
            calendar.add(Calendar.MONTH, -i);
            String monthYearKey = monthYearFormat.format(calendar.getTime());

            // Add default entry if missing
            if (!monthlyAggregationMap.containsKey(monthYearKey)) {
                DailyStatistics defaultEntry = new DailyStatistics();
                defaultEntry.Date = monthYearKey;
                defaultEntry.Hour = "00:00";
                monthlyAggregationMap.put(monthYearKey, defaultEntry);
            }
        }
    }

    public static List<String> GenerateMonthNames() {
        List<String> monthNames = new ArrayList<>();

        // Get the short month names using the default locale
        String[] shortMonthNames = new DateFormatSymbols().getShortMonths();

        // Add non-empty month names to the list
        for (String monthName : shortMonthNames) {
            if (!monthName.isEmpty()) {
                monthNames.add(monthName);
            }
        }

        return monthNames;
    }
}
