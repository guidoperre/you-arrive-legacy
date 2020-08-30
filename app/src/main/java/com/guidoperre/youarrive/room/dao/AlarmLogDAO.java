package com.guidoperre.youarrive.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.AlarmLog;

@Dao
public interface AlarmLogDAO {

    @Insert
    void insert(AlarmLog alarmLog);

    @Query("DELETE FROM alarm_log")
    void deleteAll();

}
