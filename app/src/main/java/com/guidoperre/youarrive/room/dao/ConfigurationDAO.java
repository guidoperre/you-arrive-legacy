package com.guidoperre.youarrive.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.guidoperre.youarrive.models.Configuration;

import java.util.List;

@Dao
public interface ConfigurationDAO {

    @Query("SELECT * FROM configuration")
    LiveData<List<Configuration>> get();

    @Query("SELECT * FROM configuration")
    List<Configuration> getAll();

    @Insert
    void insert(Configuration configuration);

    @Query("UPDATE configuration SET metric =:metric")
    void updateMetric(String metric);

    @Query("UPDATE configuration SET transport_type =:transportType")
    void updateTransportType(String transportType);

    @Query("UPDATE configuration SET transport_mode =:transportMode")
    void updateTransportMode(String transportMode);

    @Query("UPDATE configuration SET pref_transports =:prefTransports")
    void updatePrefTransports(String prefTransports);

    @Query("DELETE FROM configuration")
    void deleteAll();

}
