package com.ludovical.tp1stm;

public class CommonTools {
    public static String dateToString(int day, int month, int year) {
        return day + "/" + month + "/" + year;
    }

    public static String timeToString(int minute, int hour) {
        return hour + "h" + minute;
    }
}
