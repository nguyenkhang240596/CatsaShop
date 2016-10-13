package com.kalis.googlemap.model;

/**
 * Created by Kalis on 1/2/2016.
 */
public class Steps {
    private Location start_location;
    private Location end_location;
    private OverviewPolyLine polyline;
    private String travel_mode;
    private Duration duration;
    private Distance distance;

    public Duration getDuration() {
        return duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public String getTravelMode() {
        return travel_mode;
    }

    public Location getStart_location() {
        return start_location;
    }

    public Location getEnd_location() {
        return end_location;
    }

    public OverviewPolyLine getPolyline() {
        return polyline;
    }
}
