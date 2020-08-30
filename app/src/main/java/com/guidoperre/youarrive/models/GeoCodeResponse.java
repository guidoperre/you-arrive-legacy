package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GeoCodeResponse {

    @SerializedName("Response")
    @Expose
    private GeoCodeResponseInfo response;

    public GeoCodeResponseInfo getResponse() {
        return response;
    }

    public void setResponse(GeoCodeResponseInfo response) {
        this.response = response;
    }
}
