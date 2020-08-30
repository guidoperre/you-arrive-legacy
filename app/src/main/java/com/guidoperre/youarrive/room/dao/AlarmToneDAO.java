package com.guidoperre.youarrive.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.AlarmTone;

import java.util.List;

@Dao
public interface AlarmToneDAO {

    @Query("SELECT * FROM alarmTone")
    LiveData<List<AlarmTone>> get();

    @Insert
    void insert(AlarmTone alarmTone);

    @Query("DELETE FROM alarmTone")
    void deleteAll();
}
