package com.guidoperre.youarrive.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.RecoveryData;

import java.util.List;

@Dao
public interface RecoveryDataDAO {

    @Query("SELECT * FROM recovery_data")
    List<RecoveryData> get();

    @Insert
    void insert(RecoveryData recoveryData);

    @Query("DELETE FROM recovery_data")
    void deleteAll();
}
