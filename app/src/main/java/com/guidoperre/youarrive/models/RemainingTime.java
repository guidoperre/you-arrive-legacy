package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "remaining_time")
public class RemainingTime {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "time")
    private int remainingTime;

    public RemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
