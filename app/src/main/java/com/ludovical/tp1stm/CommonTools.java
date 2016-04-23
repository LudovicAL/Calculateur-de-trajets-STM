package com.ludovical.tp1stm;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CommonTools {
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

    //Converts a short date string to a Calendar object
    public static Calendar yyyymmddToCalendar (String s) {
        if (s != null && !s.isEmpty()) {
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyyMMdd");
            try {
                calendar.setTime(myDateFormat.parse(s));
                return calendar;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //Converts a long date string to a Calendar object
    public static Calendar longDateStringToCalendar (String s) {
        if (s != null && !s.isEmpty()) {
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat myDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
            try {
                calendar.setTime(myDateFormat.parse(s));
                return calendar;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //Converts a month abreviation to a number
    public static String monthAbreviationToNumber(String monthAsAbreviatedString) {
        switch(monthAsAbreviatedString) {
            case "Jan":
                return "01";
            case "Fev":
                return "02";
            case "Mar":
                return "03";
            case "Avr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
            default:
                return "01";
        }
    }

    //Converts a Calendar's date to a string
    public static String calendarToDateString(Calendar calendar) {
        if (calendar != null) {
            SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return myDateFormat.format(calendar.getTime());
        } else {
            return null;
        }
    }

    //Converts a Calendar's time to a string
    public static String calendarToTimeString(Calendar calendar) {
        if (calendar != null) {
            SimpleDateFormat myTimeFormat = new SimpleDateFormat("HH:mm:ss");
            return myTimeFormat.format(calendar.getTime());
        } else {
            return null;
        }
    }

    //Converts a Coordinates object to a LatLng object
    public static LatLng coordinatesToLatLng(Coordinates coordinates) {
        LatLng latLng = null;
        if (coordinates != null) {
            latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        }
        return latLng;
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
