package com.ludovical.tp1stm;

public class CommonTools {
    //Converts a date value to a string
    public static String dateToString(int day, int month, int year) {
        return addZero(day) + "/" + addZero(month) + "/" + year;
    }

    //Converts a time value to a string
    public static String timeToString(int minute, int hour) {
        return hour + "h" + addZero(minute);
    }

    //Adds zeros in front of numbers smaller then 10
    public static String addZero(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return "" + i;
        }
    }
}
