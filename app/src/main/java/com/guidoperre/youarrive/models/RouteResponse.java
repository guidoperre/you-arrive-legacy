package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteResponse {

    @SerializedName("response")
    @Expose
    private RouteResponseInfo response;

    public RouteResponseInfo getResponse() {
        return response;
    }

    public void setResponse(RouteResponseInfo response) {
        this.response = response;
    }
}
