package com.guidoperre.youarrive.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.AutoSuggest;

import java.util.List;

@Dao
public interface AutoSuggestDAO {

    @Query("SELECT * FROM suggests WHERE type=:type")
    List<AutoSuggest> get(String type);

    @Insert
    void insert(AutoSuggest suggest);

    @Query("UPDATE suggests SET longitude=:longitude,latitude=:latitude  WHERE location_id=:locationID")
    void updateByLocationID(String locationID, double latitude, double longitude);

    @Query("DELETE FROM suggests WHERE type=:type")
    void delete(String type);

    @Query("DELETE FROM suggests WHERE location_id=:locationID")
    void deleteByLocationID(String locationID);

    @Query("DELETE FROM suggests")
    void deleteAll();
}
