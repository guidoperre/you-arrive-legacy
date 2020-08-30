package com.guidoperre.youarrive.models;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteManeuverPosition {

        @SerializedName("latitude")
        @ColumnInfo(name = "latitude")
        @Expose
        private double latitude;
        @SerializedName("longitude")
        @ColumnInfo(name ="longitude")
        @Expose
        private double longitude;

        public RouteManeuverPosition(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
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
