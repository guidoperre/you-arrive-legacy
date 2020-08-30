package com.guidoperre.youarrive.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.LastAlarmConfiguration;

import java.util.List;

@Dao
public interface LastAlarmConfigurationDAO {

    @Query("SELECT * FROM last_alarm_configuration")
    LiveData<List<LastAlarmConfiguration>> get();

    @Query("SELECT * FROM last_alarm_configuration")
    List<LastAlarmConfiguration> getAll();

    @Insert
    void insert(LastAlarmConfiguration lastAlarmConfiguration);

    @Query("DELETE FROM last_alarm_configuration")
    void deleteAll();
}

