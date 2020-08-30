package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteWaypoint {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("sideOfStreet")
    @Expose
    private String sideOfStreet;
    @SerializedName("label")
    @Expose
    private String label;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSideOfStreet() {
        return sideOfStreet;
    }

    public void setSideOfStreet(String sideOfStreet) {
        this.sideOfStreet = sideOfStreet;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}