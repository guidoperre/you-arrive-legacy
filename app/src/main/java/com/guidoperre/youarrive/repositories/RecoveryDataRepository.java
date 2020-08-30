package com.guidoperre.youarrive.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.room.AppDatabase;
import com.guidoperre.youarrive.room.dao.RecoveryDataDAO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecoveryDataRepository {

    private static RecoveryDataDAO recoveryDataDAO;

    public RecoveryDataRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application.getApplicationContext());
        if (database != null){
            recoveryDataDAO = database.recoveryDataDAO();
        }
    }

    public List<RecoveryData> getRecoveryData(){
        try {
            return new GetRecoveryDataAsyncTask(recoveryDataDAO).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(RecoveryData alarmTone){
        new InsertRecoveryDataAsyncTask(recoveryDataDAO).execute(alarmTone);
    }

    public void deleteAll(){
        new DeleteAllRecoveryDataAsyncTask(recoveryDataDAO).execute();
    }

    private static class GetRecoveryDataAsyncTask extends AsyncTask<Void,Void,List<RecoveryData>> {

        private RecoveryDataDAO recoveryDataDAO;

        GetRecoveryDataAsyncTask(RecoveryDataDAO recoveryDataDAO){
            this.recoveryDataDAO = recoveryDataDAO;
        }

        @Override
        protected final List<RecoveryData> doInBackground(Void... voids) {
            return recoveryDataDAO.get();
        }
    }

    private static class InsertRecoveryDataAsyncTask extends AsyncTask<RecoveryData,Void,Void> {

        private RecoveryDataDAO recoveryDataDAO;

        InsertRecoveryDataAsyncTask(RecoveryDataDAO recoveryDataDAO){
            this.recoveryDataDAO = recoveryDataDAO;
        }

        @Override
        protected final Void doInBackground(RecoveryData... recoveryData) {
            if (recoveryData[0] != null)
                recoveryDataDAO.insert(recoveryData[0]);
            return null;
        }
    }

    private static class DeleteAllRecoveryDataAsyncTask extends AsyncTask<Void,Void,Void> {

        private RecoveryDataDAO recoveryDataDAO;

        DeleteAllRecoveryDataAsyncTask(RecoveryDataDAO recoveryDataDAO){
            this.recoveryDataDAO = recoveryDataDAO;
        }

        @Override
        protected final Void doInBackground(Void... voids) {
            recoveryDataDAO.deleteAll();
            return null;
        }
    }
}
