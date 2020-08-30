package com.guidoperre.youarrive.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteMode {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("transportModes")
    @Expose
    private List<String> transportModes = null;
    @SerializedName("trafficMode")
    @Expose
    private String trafficMode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTransportModes() {
        return transportModes;
    }

    public void setTransportModes(List<String> transportModes) {
        this.transportModes = transportModes;
    }

    public String getTrafficMode() {
        return trafficMode;
    }

    public void setTrafficMode(String trafficMode) {
        this.trafficMode = trafficMode;
    }

}