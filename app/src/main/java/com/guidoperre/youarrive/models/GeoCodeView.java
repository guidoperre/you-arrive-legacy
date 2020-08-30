package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeoCodeView {

    @SerializedName("Result")
    @Expose
    private List<GeoCodeResult> result;

    public List<GeoCodeResult> getResult() {
        return result;
    }

    public void setResult(List<GeoCodeResult> result) {
        this.result = result;
    }
}
