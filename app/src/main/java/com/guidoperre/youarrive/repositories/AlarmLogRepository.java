package com.guidoperre.youarrive.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.guidoperre.youarrive.models.AlarmLog;
import com.guidoperre.youarrive.room.AppDatabase;
import com.guidoperre.youarrive.room.dao.AlarmLogDAO;

public class AlarmLogRepository {

    private static AlarmLogDAO alarmLogDAO;

    public AlarmLogRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application.getApplicationContext());
        if (database != null)
            alarmLogDAO = database.alarmLogDAO();
    }

    public void insert(AlarmLog alarmLog){
        new InsertAlarmLogAsyncTask(alarmLogDAO).execute(alarmLog);
    }

    public void deleteAll(){
        new DeleteAllAsyncTask(alarmLogDAO).execute();
    }

    private static class InsertAlarmLogAsyncTask extends AsyncTask<AlarmLog,Void,Void> {

        private AlarmLogDAO alarmLogDAO;

        InsertAlarmLogAsyncTask(AlarmLogDAO alarmLogDAO){
            this.alarmLogDAO = alarmLogDAO;
        }

        @Override
        protected final Void doInBackground(AlarmLog... alarmLogs) {
            if (alarmLogs[0] != null)
                alarmLogDAO.insert(alarmLogs[0]);
            return null;
        }
    }

    private static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void> {

        private AlarmLogDAO alarmLogDAO;

        DeleteAllAsyncTask(AlarmLogDAO alarmLogDAO){
            this.alarmLogDAO = alarmLogDAO;
        }

        @Override
        protected final Void doInBackground(Void... voids) {
            alarmLogDAO.deleteAll();
            return null;
        }
    }
}
