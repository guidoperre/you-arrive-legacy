package com.guidoperre.youarrive.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.Alarm;

import java.util.List;

@Dao
public interface AlarmDAO {

    @Query("SELECT * FROM alarms WHERE title=:title")
    List<Alarm> getAlarm(String title);

    @Query("SELECT * FROM alarms")
    List<Alarm> getAllAlarm();

    @Query("SELECT * FROM alarms")
    LiveData<List<Alarm>> getAll();

    @Insert
    void insert(Alarm alarm);

    @Query("UPDATE alarms SET latitude=:latitude,longitude=:longitude,uri=:uri,volume=:volume,safe_zone=:safezone WHERE title=:title")
    void update(String title, double latitude, double longitude, String uri, int volume, int safezone);

    @Query("DELETE FROM alarms WHERE title=:title")
    void deleteAlarm(String title);

    @Query("DELETE FROM alarms")
    void deleteAll();

}
