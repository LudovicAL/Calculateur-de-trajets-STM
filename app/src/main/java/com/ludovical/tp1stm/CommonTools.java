package com.ludovical.tp1stm;

public class CommonTools {
    public static String dateToString(int day, int month, int year) {
        return addZero(day) + "/" + addZero(month) + "/" + year;
    }

    public static String timeToString(int minute, int hour) {
        return hour + "h" + addZero(minute);
    }

    public static String addZero(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return "" + i;
        }
    }
}
