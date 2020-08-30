package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteStop {

    @SerializedName("position")
    @Expose
    private GeoCode position;
    @SerializedName("travelTime")
    @Expose
    private Integer travelTime;
    @SerializedName("stopName")
    @Expose
    private String stopName;

    public GeoCode getPosition() {
        return position;
    }

    public void setPosition(GeoCode position) {
        this.position = position;
    }

    public Integer getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
