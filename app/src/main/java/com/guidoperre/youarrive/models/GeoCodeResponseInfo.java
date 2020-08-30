package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeoCodeResponseInfo {

    @SerializedName("View")
    @Expose
    private List<GeoCodeView> view;

    public List<GeoCodeView> getView() {
        return view;
    }

    public void setView(List<GeoCodeView> view) {
        this.view = view;
    }
}
