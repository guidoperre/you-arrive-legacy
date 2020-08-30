package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RouteTransportLine {

    @SerializedName("lineName")
    @Expose
    private String lineName;
    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("stop")
    @Expose
    private List<RouteStop> stops;
    @SerializedName("id")
    @Expose
    private String id;

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<RouteStop> getStops() {
        return stops;
    }

    public void setStops(List<RouteStop> stops) {
        this.stops = stops;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}