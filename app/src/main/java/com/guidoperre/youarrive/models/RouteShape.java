package com.guidoperre.youarrive.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RouteShape {

    private String id;
    private ArrayList<LatLng> shape;
    private String type;

    public RouteShape(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<LatLng> getShape() {
        return shape;
    }

    public void setShape(ArrayList<LatLng> shape) {
        this.shape = shape;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
