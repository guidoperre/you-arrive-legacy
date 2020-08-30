package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "last_alarm_configuration")
public class LastAlarmConfiguration {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "safe_zone")
    private int lastSafeZoneSelected;

    @ColumnInfo(name = "volume")
    private int lastVolumeSelected;

    public LastAlarmConfiguration(int lastVolumeSelected, int lastSafeZoneSelected) {
        this.lastVolumeSelected = lastVolumeSelected;
        this.lastSafeZoneSelected = lastSafeZoneSelected;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLastSafeZoneSelected() {
        return lastSafeZoneSelected;
    }

    public void setLastSafeZoneSelected(int lastSafeZoneSelected) {
        this.lastSafeZoneSelected = lastSafeZoneSelected;
    }

    public int getLastVolumeSelected() {
        return lastVolumeSelected;
    }

    public void setLastVolumeSelected(int lastVolumeSelected) {
        this.lastVolumeSelected = lastVolumeSelected;
    }
}
