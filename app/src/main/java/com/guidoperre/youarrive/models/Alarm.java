package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class Alarm {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @Embedded
    private GeoCode location;

    @ColumnInfo(name = "uri")
    private String uri;

    @ColumnInfo(name = "volume")
    private int volume;

    @ColumnInfo(name = "safe_zone")
    private int safezone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GeoCode getLocation() {
        return location;
    }

    public void setLocation(GeoCode location) {
        this.location = location;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getSafezone() {
        return safezone;
    }

    public void setSafezone(int safezone) {
        this.safezone = safezone;
    }
}
