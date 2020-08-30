package com.guidoperre.youarrive.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.guidoperre.youarrive.models.RemainingTime;
import com.guidoperre.youarrive.room.AppDatabase;
import com.guidoperre.youarrive.room.dao.RemainingTimeDAO;

import java.util.List;

public class RemainingTimeRepository {

    private static RemainingTimeDAO remainingTimeDAO;

    private static LiveData<List<RemainingTime>> dataList;

    public RemainingTimeRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application.getApplicationContext());
        if (database != null){
            remainingTimeDAO = database.remainingTimeDAO();
        }
        dataList = remainingTimeDAO.get();
    }

    public LiveData<List<RemainingTime>> get(){ return dataList; }

    public void insert(RemainingTime remainingTime){
        new InsertTimeAsyncTask(remainingTimeDAO).execute(remainingTime);
    }

    public void deleteAll(){
        new DeleteAllAsyncTask(remainingTimeDAO).execute();
    }


    private static class InsertTimeAsyncTask extends AsyncTask<RemainingTime,Void,Void> {

        private RemainingTimeDAO remainingTimeDAO;

        InsertTimeAsyncTask(RemainingTimeDAO remainingTimeDAO){
            this.remainingTimeDAO = remainingTimeDAO;
        }

        @Override
        protected final Void doInBackground(RemainingTime... remainingTimes) {
            if (remainingTimes[0] != null)
                remainingTimeDAO.insert(remainingTimes[0]);
            return null;
        }
    }

    private static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void> {

        private RemainingTimeDAO remainingTimeDAO;

        DeleteAllAsyncTask(RemainingTimeDAO remainingTimeDAO){
            this.remainingTimeDAO = remainingTimeDAO;
        }

        @Override
        protected final Void doInBackground(Void... voids) {
            remainingTimeDAO.deleteAll();
            return null;
        }
    }

}