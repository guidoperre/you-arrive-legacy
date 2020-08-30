package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm_log")
public class AlarmLog {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "font")
    private String font;

    @ColumnInfo(name = "travel_distance")
    private double leftTravelDistance;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "activated")
    private int isAlarmActivated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public double getLeftTravelDistance() {
        return leftTravelDistance;
    }

    public void setLeftTravelDistance(double leftTravelDistance) {
        this.leftTravelDistance = leftTravelDistance;
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

    public int getIsAlarmActivated() {
        return isAlarmActivated;
    }

    public void setIsAlarmActivated(int isAlarmActivated) {
        this.isAlarmActivated = isAlarmActivated;
    }
}
