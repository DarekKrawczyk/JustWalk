package com.example.justwalk;

import static java.text.DateFormat.getDateTimeInstance;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Utility {
    public static int CalculateSteps(double distance, double strideLength){
        return (int) Math.round(distance / strideLength);
    }
    public static double CalculateCaloriesBurned(double weightKg, double distanceKm, double metValue) {
        return (metValue * weightKg * distanceKm) / 200.0;
    }
    public static double CalculateCaloriesBurned(int steps) {
        // # of steps*.04 = calories
        return steps*0.04;
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

    public static int CalculatePoints(int steps){
        Random random = new Random();
        int randomNumber = random.nextInt(4) + 1;
        return randomNumber;
    }

    public static String ExtractDate(String inputDateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = inputFormat.parse(inputDateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> GetPrevoiusMonths() {
        List<String> last12Months = new ArrayList<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.forLanguageTag("EN"));
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 12; i++) {
            Date currentDate = calendar.getTime();

            String currentMonth = monthFormat.format(currentDate);
            last12Months.add(currentMonth);

            calendar.add(Calendar.MONTH, -1);
        }

        Collections.reverse(last12Months);

        return last12Months;
    }
    public static List<String> GetPreviousWeekDays() {

        List<String> previousDays = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.forLanguageTag("EN"));
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            Date currentDate = calendar.getTime();

            String currentDay = dayFormat.format(currentDate);

            String changedCurDay = Character.toUpperCase(currentDay.charAt(0)) + currentDay.substring(1).toLowerCase();
            previousDays.add(changedCurDay);

            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

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

    public static float CalculateAveragePoints(List<DailyStatistics> dailyStatisticsList) {
        if (dailyStatisticsList.isEmpty()) {
            return 0f;
        }

        int totalSteps = 0;
        for (DailyStatistics entry : dailyStatisticsList) {
            totalSteps += entry.Points;
        }

        return (float) totalSteps / dailyStatisticsList.size();
    }

    public static float CalculateAverageDistance(List<DailyStatistics> dailyStatisticsList) {
        if (dailyStatisticsList.isEmpty()) {
            return 0f;
        }

        int totalSteps = 0;
        for (DailyStatistics entry : dailyStatisticsList) {
            totalSteps += entry.Distance;
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

        java.util.Map<String, DailyStatistics> monthlyAggregationMap = new java.util.HashMap<>();

        for (DailyStatistics entry : data) {
            try {
                Date entryDate = dateFormat.parse(entry.Date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(entryDate);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;

                String monthYearKey = String.format("%04d-%02d", year, month);

                DailyStatistics aggregatedStats = monthlyAggregationMap.get(monthYearKey);
                if (aggregatedStats == null) {
                    aggregatedStats = new DailyStatistics();
                    aggregatedStats.Date = monthYearKey;
                    aggregatedStats.Hour = "00:00";
                    monthlyAggregationMap.put(monthYearKey, aggregatedStats);
                }

                aggregatedStats.Distance += entry.Distance;
                aggregatedStats.Steps += entry.Steps;
                aggregatedStats.Points += entry.Points;
                aggregatedStats.CaloriesBurned += entry.CaloriesBurned;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        addDefaultEntries(monthlyAggregationMap);

        result.addAll(monthlyAggregationMap.values());

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
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("yyyy-MM");

        for (int i = 0; i < 12; i++) {
            calendar.setTime(currentDate);
            calendar.add(Calendar.MONTH, -i);
            String monthYearKey = monthYearFormat.format(calendar.getTime());

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
        String[] shortMonthNames = new DateFormatSymbols().getShortMonths();

        for (String monthName : shortMonthNames) {
            if (!monthName.isEmpty()) {
                String changedCurMnth = Character.toUpperCase(monthName.charAt(0)) + monthName.substring(1).toLowerCase();
                monthNames.add(changedCurMnth);
            }
        }

        return monthNames;
    }

    public static String GetDayName(LocalDate date) {
        DayOfWeek dayOfWeek = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dayOfWeek = date.getDayOfWeek();
        }
        return dayOfWeek.toString();
    }
    public static List<String> SplitString(String input) {
        String[] parts = input.split("\\s*\\*\\s*");
        List<String> resultList = Arrays.asList(parts);
        return resultList;
    }
}
