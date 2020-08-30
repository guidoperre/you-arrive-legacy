package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarmTone")
public class AlarmTone {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "uri")
    private String uri;

    @ColumnInfo(name = "title")
    private String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AlarmTone(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
