package com.guidoperre.youarrive.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

    @SerializedName("waypoint")
    @Expose
    private List<RouteWaypoint> waypoint = null;
    @SerializedName("mode")
    @Expose
    private RouteMode mode;
    @SerializedName("shape")
    @Expose
    private List<String> shape = null;
    @SerializedName("leg")
    @Expose
    private List<RouteLeg> leg = null;
    @SerializedName("publicTransportLine")
    @Expose
    private List<RouteTransportLine> publicTransportLine = null;
    @SerializedName("summary")
    @Expose
    private RouteSummary summary;

    public List<RouteWaypoint> getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(List<RouteWaypoint> waypoint) {
        this.waypoint = waypoint;
    }

    public RouteMode getMode() {
        return mode;
    }

    public void setMode(RouteMode mode) {
        this.mode = mode;
    }

    public List<String> getShape() {
        return shape;
    }

    public void setShape(List<String> shape) {
        this.shape = shape;
    }

    public List<RouteLeg> getLeg() {
        return leg;
    }

    public void setLeg(List<RouteLeg> leg) {
        this.leg = leg;
    }

    public List<RouteTransportLine> getPublicTransportLine() {
        return publicTransportLine;
    }

    public void setPublicTransportLine(List<RouteTransportLine> publicTransportLine) {
        this.publicTransportLine = publicTransportLine;
    }

    public RouteSummary getSummary() {
        return summary;
    }

    public void setSummary(RouteSummary summary) {
        this.summary = summary;
    }

}