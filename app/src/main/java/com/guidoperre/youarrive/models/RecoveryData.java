package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recovery_data")
public class RecoveryData {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "screen")
    private String lastScreen;

    @ColumnInfo(name = "route")
    private String routeJSON;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "myLatitude")
    private double myLatitude;

    @ColumnInfo(name = "myLongitude")
    private double myLongitude;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    public RecoveryData(String lastScreen, String routeJSON, String address, double myLatitude, double myLongitude, double latitude, double longitude) {
        this.lastScreen = lastScreen;
        this.routeJSON = routeJSON;
        this.address = address;
        this.myLatitude = myLatitude;
        this.myLongitude = myLongitude;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastScreen() {
        return lastScreen;
    }

    public void setLastScreen(String lastScreen) {
        this.lastScreen = lastScreen;
    }

    public String getRouteJSON() {
        return routeJSON;
    }

    public void setRouteJSON(String routeJSON) {
        this.routeJSON = routeJSON;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getMyLatitude() {
        return myLatitude;
    }

    public void setMyLatitude(double myLatitude) {
        this.myLatitude = myLatitude;
    }

    public double getMyLongitude() {
        return myLongitude;
    }

    public void setMyLongitude(double myLongitude) {
        this.myLongitude = myLongitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
