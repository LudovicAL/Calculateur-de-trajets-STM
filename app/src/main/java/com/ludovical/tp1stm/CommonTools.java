package com.ludovical.tp1stm;

public class CommonTools {
    //Converts a date value to a string
    public static String dateToString(int day, int month, int year) {
        return addZero(day) + "/" + addZero(month) + "/" + year;
    }

    //Converts a time value to a string
    public static String timeToString(int hour, int minute) {
        return addZero(hour) + "h" + addZero(minute);
    }

    //Converts a string to a double
    public static double stringToDouble(String s) {
        return Double.parseDouble(s.trim());
    }

    //Adds zeros in front of numbers smaller then 10
    public static String addZero(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return "" + i;
        }
    }

    //Converts Latitude and Longitude distance to meters
    public static int coordinatesToMeters(double aLatitude, double aLongitude, double bLatitude, double bLongitude) {
        double R = 6378.137; // Radius of earth in KM
        double dLat = (bLatitude - aLatitude) * Math.PI / 180;
        double dLon = (bLongitude - aLongitude) * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(aLatitude * Math.PI / 180) * Math.cos(bLatitude * Math.PI / 180) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return (int)(d * 1000); // meters
    }
}
