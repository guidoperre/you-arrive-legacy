package com.guidoperre.youarrive.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.RoutePath;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface RoutePathDAO {

    @Query("SELECT * FROM routePath")
    LiveData<List<RoutePath>> get();

    @Insert
    void insert(ArrayList<RoutePath> routePath);

    @Query("DELETE FROM routePath")
    void deleteAll();

}
