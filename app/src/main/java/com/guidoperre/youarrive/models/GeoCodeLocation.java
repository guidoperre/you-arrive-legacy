package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoCodeLocation {

    @SerializedName("DisplayPosition")
    @Expose
    private GeoCode geocode;

    public GeoCode getGeocode() {
        return geocode;
    }

    public void setGeocode(GeoCode geocode) {
        this.geocode = geocode;
    }
}
