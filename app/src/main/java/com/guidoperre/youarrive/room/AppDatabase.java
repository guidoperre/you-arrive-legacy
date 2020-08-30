package com.guidoperre.youarrive.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.AlarmLog;
import com.guidoperre.youarrive.models.AlarmTone;
import com.guidoperre.youarrive.models.AutoSuggest;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.LastAlarmConfiguration;
import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.models.RemainingTime;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.room.dao.AlarmDAO;
import com.guidoperre.youarrive.room.dao.AlarmLogDAO;
import com.guidoperre.youarrive.room.dao.AlarmToneDAO;
import com.guidoperre.youarrive.room.dao.AutoSuggestDAO;
import com.guidoperre.youarrive.room.dao.ConfigurationDAO;
import com.guidoperre.youarrive.room.dao.LastAlarmConfigurationDAO;
import com.guidoperre.youarrive.room.dao.RecoveryDataDAO;
import com.guidoperre.youarrive.room.dao.RemainingTimeDAO;
import com.guidoperre.youarrive.room.dao.RoutePathDAO;


@Database(entities = {AutoSuggest.class, RoutePath.class, AlarmTone.class, LastAlarmConfiguration.class, Alarm.class, AlarmLog.class, RecoveryData.class, RemainingTime.class, Configuration.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AutoSuggestDAO autoSuggestDAO();
    public abstract RoutePathDAO routePathDAO();
    public abstract AlarmToneDAO alarmToneDAO();
    public abstract LastAlarmConfigurationDAO lastAlarmConfigurationDAO();
    public abstract AlarmDAO alarmDAO();
    public abstract AlarmLogDAO alarmLogDAO();
    public abstract RecoveryDataDAO recoveryDataDAO();
    public abstract RemainingTimeDAO remainingTimeDAO();
    public abstract ConfigurationDAO configurationDAO();

    private static AppDatabase Instance = null;
    public static synchronized AppDatabase getDatabase(Context context){
        if (Instance == null)
            Instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,"you_arrive_database").fallbackToDestructiveMigration().build();
        return Instance;
    }
}
