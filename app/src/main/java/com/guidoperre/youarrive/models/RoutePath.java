package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "routePath")
public class RoutePath {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @Ignore
    private String stopID;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "time")
    private Integer time;
    @ColumnInfo(name = "length")
    private Integer length;
    @ColumnInfo(name = "startLatitude")
    private double startLatitude;
    @ColumnInfo(name = "startLongitude")
    private double startLongitude;
    @ColumnInfo(name = "endLatitude")
    private double endLatitude;
    @ColumnInfo(name = "endLongitude")
    private double endLongitude;
    @ColumnInfo(name = "startRoadName")
    private String startRoadName;
    @ColumnInfo(name = "endRoadName")
    private String endRoadName;

    public RoutePath() {
    }

    @Ignore
    public RoutePath(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getStartRoadName() {
        return startRoadName;
    }

    public void setStartRoadName(String startRoadName) {
        this.startRoadName = startRoadName;
    }

    public String getEndRoadName() {
        return endRoadName;
    }

    public void setEndRoadName(String endRoadName) {
        this.endRoadName = endRoadName;
    }
}