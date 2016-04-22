package com.stoyanov.developer.apptracker;

import java.util.Locale;

public class TimeConverter {

    public static String convert(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String result = "";
        if (hours != 0) {
            result = String.format(Locale.getDefault(),"%2d h ", hours);
        }
        if (minutes != 0) {
            result += String.format(Locale.getDefault(), "%2d m ", minutes);
        }
        if (seconds != 0) {
            result += String.format(Locale.getDefault(), "%2d s", seconds);
        }
        if (result.isEmpty()) {
            result = "0 s";
        }
        return result;
    }

    public static int convertToMinutes(int seconds) {
        return seconds / 60;
    }
}
