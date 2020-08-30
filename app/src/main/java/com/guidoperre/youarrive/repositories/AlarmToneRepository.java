package com.guidoperre.youarrive.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.guidoperre.youarrive.models.AlarmTone;
import com.guidoperre.youarrive.room.AppDatabase;
import com.guidoperre.youarrive.room.dao.AlarmToneDAO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmToneRepository {

    private static AlarmToneDAO alarmToneDAO;

    private static LiveData<List<AlarmTone>> dataList;

    public AlarmToneRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application.getApplicationContext());
        if (database != null){
            alarmToneDAO = database.alarmToneDAO();
        }
        dataList = alarmToneDAO.get();
    }

    public LiveData<List<AlarmTone>> get(){ return dataList; }

    public List<AlarmTone> getAlarmTone(){
        try {
            return new GetAlarmToneAsyncTask(alarmToneDAO).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(AlarmTone alarmTone){
        new InsertAlarmToneAsyncTask(alarmToneDAO).execute(alarmTone);
    }

    public void deleteAll(){
        new DeleteAllAlarmToneAsyncTask(alarmToneDAO).execute();
    }

    private static class GetAlarmToneAsyncTask extends AsyncTask<Void,Void,List<AlarmTone>> {

        private AlarmToneDAO alarmToneDAO;

        GetAlarmToneAsyncTask(AlarmToneDAO alarmToneDAO){
            this.alarmToneDAO = alarmToneDAO;
        }

        @Override
        protected final List<AlarmTone> doInBackground(Void... voids) {
            alarmToneDAO.get();
            return null;
        }
    }

    private static class InsertAlarmToneAsyncTask extends AsyncTask<AlarmTone,Void,Void> {

        private AlarmToneDAO alarmToneDAO;

        InsertAlarmToneAsyncTask(AlarmToneDAO alarmToneDAO){
            this.alarmToneDAO = alarmToneDAO;
        }

        @Override
        protected final Void doInBackground(AlarmTone... alarmTones) {
            if (alarmTones[0] != null)
                alarmToneDAO.insert(alarmTones[0]);
            return null;
        }
    }

    private static class DeleteAllAlarmToneAsyncTask extends AsyncTask<Void,Void,Void> {

        private AlarmToneDAO alarmToneDAO;

        DeleteAllAlarmToneAsyncTask(AlarmToneDAO alarmToneDAO){
            this.alarmToneDAO = alarmToneDAO;
        }

        @Override
        protected final Void doInBackground(Void... voids) {
            alarmToneDAO.deleteAll();
            return null;
        }
    }

}
