package com.guidoperre.youarrive.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteLeg {

    @SerializedName("length")
    @Expose
    private Integer length;
    @SerializedName("travelTime")
    @Expose
    private Integer travelTime;
    @SerializedName("maneuver")
    @Expose
    private List<RouteManeuver> maneuver = null;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }

    public List<RouteManeuver> getManeuver() {
        return maneuver;
    }

    public void setManeuver(List<RouteManeuver> maneuver) {
        this.maneuver = maneuver;
    }

}