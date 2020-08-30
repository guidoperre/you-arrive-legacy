package com.guidoperre.youarrive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AutoSuggestResponse {

    @SerializedName("suggestions")
    @Expose
    private List<AutoSuggest> response;

    public List<AutoSuggest> getResponse() {
        return response;
    }

    public void setResponse(List<AutoSuggest> response) {
        this.response = response;
    }
}
