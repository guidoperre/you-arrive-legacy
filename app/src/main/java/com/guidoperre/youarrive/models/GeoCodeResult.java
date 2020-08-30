package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoCodeResult {

    @SerializedName("Location")
    @Expose
    private GeoCodeLocation location;

    public GeoCodeLocation getLocation() {
        return location;
    }

    public void setLocation(GeoCodeLocation location) {
        this.location = location;
    }
}
