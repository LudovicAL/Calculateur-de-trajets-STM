package com.ludovical.tp1stm;

import android.util.Log;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Route implements Serializable {
    private String tripHeadSign;
    private Calendar requiredCalendar;
    private Calendar departureCalendar;
    private Calendar initialCalendar;
    private double initialLatitude;
    private double initialLongitude;
    private String aStopName;
    private Calendar aArrivalCalendar;
    private double aLatitude;
    private double aLongitude;
    private String bStopName;
    private Calendar bArrivalCalendar;
    private double bLatitude;
    private double bLongitude;
    private Calendar objectiveCalendar;
    private double objectiveLatitude;
    private double objectiveLongitude;
    private int walkDistance;
    private int correspondances;

    public Route(String tripHeadSign,
                 //INITIAL POSITION
                 Calendar initialCalendar,
                 double initialLatitude,
                 double initialLongitude,

                 //A POSITION
                 String aStopName,
                 String aArrivalTime,
                 double aLatitude,
                 double aLongitude,

                 //B POSITION
                 String bStopName,
                 String bArrivalTime,
                 double bLatitude,
                 double bLongitude,

                 //OBJECTIVE POSITION
                 double objectiveLatitude,
                 double objectiveLongitude) {
        //INITIAL POSITION
        this.initialCalendar = initialCalendar;
        this.initialLatitude = initialLatitude;
        this.initialLongitude = initialLongitude;

        //A POSITION
        this.aStopName = aStopName;
        this.aArrivalCalendar = new GregorianCalendar(initialCalendar.get(Calendar.YEAR), initialCalendar.get(Calendar.MONTH), initialCalendar.get(Calendar.DAY_OF_MONTH), Integer.parseInt(aArrivalTime.substring(0, 2)), Integer.parseInt(aArrivalTime.substring(3, 5)), 0);
        if (aArrivalCalendar.compareTo(initialCalendar) < 0) {
            aArrivalCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        this.aLatitude = aLatitude;
        this.aLongitude = aLongitude;

        //B POSITION
        this.bStopName = bStopName;
        this.bArrivalCalendar = new GregorianCalendar(initialCalendar.get(Calendar.YEAR), initialCalendar.get(Calendar.MONTH), initialCalendar.get(Calendar.DAY_OF_MONTH), Integer.parseInt(bArrivalTime.substring(0, 2)), Integer.parseInt(bArrivalTime.substring(3, 5)), 0);
        if (bArrivalCalendar.compareTo(aArrivalCalendar) < 0) {
            bArrivalCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        this.bLatitude = bLatitude;
        this.bLongitude = bLongitude;

        //OBJECTIVE POSITION
        this.objectiveLatitude = objectiveLatitude;
        this.objectiveLongitude = objectiveLongitude;
        calculateObjectiveCalendar();

        //OTHERS OPERATIONS
        this.tripHeadSign = tripHeadSign;
        calculateDepartureCalendar();
        this.walkDistance = calculateWalkDistance(initialLatitude, initialLongitude, aLatitude, aLongitude, bLatitude, bLongitude, objectiveLatitude, objectiveLongitude);     //À CORRIGER
        calculateRequiredCalendar();
        this.correspondances = 1;      //A hardcoded value until we devise a way for the query to return multi-correspondances itineraries
    }

    //Calculates the required total trip time
    private void calculateRequiredCalendar() {
        requiredCalendar = (Calendar)objectiveCalendar.clone();
        requiredCalendar.add(Calendar.MINUTE, -departureCalendar.get(Calendar.MINUTE));
        requiredCalendar.add(Calendar.HOUR, -departureCalendar.get(Calendar.HOUR));
        requiredCalendar.add(Calendar.DAY_OF_MONTH, -departureCalendar.get(Calendar.DAY_OF_MONTH));
        requiredCalendar.add(Calendar.MONTH, -departureCalendar.get(Calendar.MONTH));
        requiredCalendar.add(Calendar.YEAR, -departureCalendar.get(Calendar.YEAR));
    }

    //Calculates the suggested departure time
    private void calculateDepartureCalendar() {
        //Calculation of the distance in meters from Initial position to A position
        int distance = CommonTools.coordinatesToMeters(initialLatitude, initialLongitude, aLatitude, aLongitude);
        Log.d("test", "Distance from Initial position to A position = " + distance);
        //Convertion of the distance in time required to walk it at 5km/h
        int timeInMinutes = (int)((distance * 60) / 5000);
        //Creation of the new calendar value
        this.departureCalendar = (Calendar)aArrivalCalendar.clone();
        this.departureCalendar.add(Calendar.MINUTE, -timeInMinutes);
    }

    //Calculates the objective arrival time
    private void calculateObjectiveCalendar() {
        //Calculation of the distance in meters from B position to Objective position
        int distance = CommonTools.coordinatesToMeters(bLatitude, bLongitude, objectiveLatitude, objectiveLongitude);
        Log.d("test", "Distance from B position to Objective position = " + distance);
        //Convertion of the distance in time required to walk it at 5km/h
        int timeInMinutes = (int)((distance * 60) / 5000);
        //Creation of the new calendar value
        this.objectiveCalendar = (Calendar)bArrivalCalendar.clone();
        this.objectiveCalendar.add(Calendar.MINUTE, timeInMinutes);
    }

    //Calculates de total walking distance
    private int calculateWalkDistance(double initialLatitude, double initialLongitude, double aLatitude, double aLongitude, double bLatitude, double bLongitude, double objectiveLatitude, double objectiveLongitude) {
        int a = CommonTools.coordinatesToMeters(initialLatitude, initialLongitude, aLatitude, aLongitude);
        int b = CommonTools.coordinatesToMeters(bLatitude, bLongitude, objectiveLatitude, objectiveLongitude);
        return a + b;
    }

    @Override
    public String toString() {
        return R.string.line + ": " + tripHeadSign + "\n" + R.string.requiredTime + ": " + getRequiredTime();
    }

    //Custom Getters
    public String getDate() {
        return initialCalendar.get(Calendar.DAY_OF_MONTH) + "/" + initialCalendar.get(Calendar.MONTH) + "/" + initialCalendar.get(Calendar.YEAR);
    }

    public String getRequiredTime() {
        return CommonTools.timeToString(requiredCalendar.get(Calendar.HOUR), requiredCalendar.get(Calendar.MINUTE));
    }

    public String getDepartureTime() {
        return CommonTools.timeToString(departureCalendar.get(Calendar.HOUR), departureCalendar.get(Calendar.MINUTE));
    }

    public String getInitialTime() {
        return CommonTools.timeToString(initialCalendar.get(Calendar.HOUR), initialCalendar.get(Calendar.MINUTE));
    }

    public String getaArrivalTime() {
        return CommonTools.timeToString(aArrivalCalendar.get(Calendar.HOUR), aArrivalCalendar.get(Calendar.MINUTE));
    }

    public String getbArrivalTime() {
        return CommonTools.timeToString(bArrivalCalendar.get(Calendar.HOUR), bArrivalCalendar.get(Calendar.MINUTE));
    }

    public String getObjectiveTime() {
        return CommonTools.timeToString(objectiveCalendar.get(Calendar.HOUR), objectiveCalendar.get(Calendar.MINUTE));
    }

    public String getAllCoordinates() {
        return initialLatitude + "," + initialLongitude + ";" + aLatitude + "," + aLongitude + ";" + bLatitude + "," + bLongitude + ";" + objectiveLatitude + "," + objectiveLongitude;
    }

    //Automatic Getters
    public Calendar getInitialCalendar() {
        return initialCalendar;
    }

    public String getTripHeadSign() {
        return tripHeadSign;
    }

    public void setTripHeadSign(String tripHeadSign) {
        this.tripHeadSign = tripHeadSign;
    }

    public Calendar getRequiredCalendar() {
        return requiredCalendar;
    }

    public double getInitialLatitude() {
        return initialLatitude;
    }

    public double getInitialLongitude() {
        return initialLongitude;
    }

    public String getaStopName() {
        return aStopName;
    }

    public Calendar getaArrivalCalendar() {
        return aArrivalCalendar;
    }

    public double getaLatitude() {
        return aLatitude;
    }

    public double getaLongitude() {
        return aLongitude;
    }

    public String getbStopName() {
        return bStopName;
    }

    public Calendar getbArrivalCalendar() {
        return bArrivalCalendar;
    }

    public double getbLatitude() {
        return bLatitude;
    }

    public double getbLongitude() {
        return bLongitude;
    }

    public Calendar getObjectiveCalendar() {
        return objectiveCalendar;
    }

    public double getObjectiveLatitude() {
        return objectiveLatitude;
    }

    public double getObjectiveLongitude() {
        return objectiveLongitude;
    }

    public int getWalkDistance() {
        return walkDistance;
    }

    public int getCorrespondances() {
        return correspondances;
    }
}
