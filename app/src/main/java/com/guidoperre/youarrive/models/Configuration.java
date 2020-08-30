package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "configuration")
public class Configuration {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "metric")
    private String metric;

    @ColumnInfo(name = "transport_type")
    private String transportType;

    @ColumnInfo(name = "transport_mode")
    private String transportMode;

    @ColumnInfo(name = "pref_transports")
    private String prefTransports;

    public Configuration(String metric, String transportType, String transportMode, String prefTransports) {
        this.metric = metric;
        this.transportType = transportType;
        this.transportMode = transportMode;
        this.prefTransports = prefTransports;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getPrefTransports() {
        return prefTransports;
    }

    public void setPrefTransports(String prefTransports) {
        this.prefTransports = prefTransports;
    }
}
