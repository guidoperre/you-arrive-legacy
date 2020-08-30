package com.guidoperre.youarrive.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.RemainingTime;

import java.util.List;

@Dao
public interface RemainingTimeDAO {

    @Query("SELECT * FROM remaining_time")
    LiveData<List<RemainingTime>> get();

    @Insert
    void insert(RemainingTime remainingTime);

    @Query("DELETE FROM remaining_time")
    void deleteAll();

}
