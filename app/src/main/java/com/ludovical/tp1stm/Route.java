package com.ludovical.tp1stm;

public class Route {
    private int requiredTimeHours;
    private int requiredTimeMinutes;
    private int departureTimeHours;
    private int departureTimeMinutes;
    private int arrivalTimeHours;
    private int arrivalTimeMinutes;
    private float walkDistance;
    private int changesAmount;

    public Route(int requiredTimeHours, int requiredTimeMinutes, int departureTimeHours, int departureTimeMinutes, int arrivalTimeHours, int arrivalTimeMinutes, float walkDistance, int changesAmount) {
        this.requiredTimeHours = requiredTimeHours;
        this.requiredTimeMinutes = requiredTimeMinutes;
        this.departureTimeHours = departureTimeHours;
        this.departureTimeMinutes = departureTimeMinutes;
        this.arrivalTimeHours = arrivalTimeHours;
        this.arrivalTimeMinutes = arrivalTimeMinutes;
        this.walkDistance = walkDistance;
        this.changesAmount = changesAmount;
    }

    @Override
    public String toString() {
        return "Route{" +
                "requiredTimeHours=" + requiredTimeHours +
                ", requiredTimeMinutes=" + requiredTimeMinutes +
                ", departureTimeHours=" + departureTimeHours +
                ", departureTimeMinutes=" + departureTimeMinutes +
                ", arrivalTimeHours=" + arrivalTimeHours +
                ", arrivalTimeMinutes=" + arrivalTimeMinutes +
                ", walkDistance=" + walkDistance +
                ", changesAmount=" + changesAmount +
                '}';
    }

    public int getRequiredTimeHours() {
        return requiredTimeHours;
    }

    public void setRequiredTimeHours(int requiredTimeHours) {
        this.requiredTimeHours = requiredTimeHours;
    }

    public int getRequiredTimeMinutes() {
        return requiredTimeMinutes;
    }

    public void setRequiredTimeMinutes(int requiredTimeMinutes) {
        this.requiredTimeMinutes = requiredTimeMinutes;
    }

    public int getDepartureTimeHours() {
        return departureTimeHours;
    }

    public void setDepartureTimeHours(int departureTimeHours) {
        this.departureTimeHours = departureTimeHours;
    }

    public int getDepartureTimeMinutes() {
        return departureTimeMinutes;
    }

    public void setDepartureTimeMinutes(int departureTimeMinutes) {
        this.departureTimeMinutes = departureTimeMinutes;
    }

    public int getArrivalTimeHours() {
        return arrivalTimeHours;
    }

    public void setArrivalTimeHours(int arrivalTimeHours) {
        this.arrivalTimeHours = arrivalTimeHours;
    }

    public int getArrivalTimeMinutes() {
        return arrivalTimeMinutes;
    }

    public void setArrivalTimeMinutes(int arrivalTimeMinutes) {
        this.arrivalTimeMinutes = arrivalTimeMinutes;
    }

    public float getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(float walkDistance) {
        this.walkDistance = walkDistance;
    }

    public int getChangesAmount() {
        return changesAmount;
    }

    public void setChangesAmount(int changesAmount) {
        this.changesAmount = changesAmount;
    }
}
