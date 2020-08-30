package com.guidoperre.youarrive.repositories;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import com.guidoperre.youarrive.models.Configuration;

import com.guidoperre.youarrive.room.AppDatabase;
import com.guidoperre.youarrive.room.dao.ConfigurationDAO;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class ConfigurationRepository {

    private static ConfigurationDAO configurationDAO;

    public ConfigurationRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application.getApplicationContext());
        if (database != null)
            configurationDAO = database.configurationDAO();
    }

    public ConfigurationRepository(Context applicationContext) {
        AppDatabase database = AppDatabase.getDatabase(applicationContext);
        if (database != null)
            configurationDAO = database.configurationDAO();
    }


    public List<Configuration> get(){
        try {
            return new GetAsyncTask(configurationDAO).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Configuration configuration){
        new InsertAsyncTask(configurationDAO).execute(configuration);
    }

    public void updateMetric(String metric){
        new UpdateMetricAsyncTask(configurationDAO).execute(metric);
    }

    public void updateType(String type){
        new UpdateTypeAsyncTask(configurationDAO).execute(type);
    }

    public void updateMode(String mode){
        new UpdateModeAsyncTask(configurationDAO).execute(mode);
    }

    public void updatePrefer(String prefer){
        new UpdatePreferAsyncTask(configurationDAO).execute(prefer);
    }

    private static class GetAsyncTask extends AsyncTask<Void,Void,List<Configuration>> {

        private ConfigurationDAO configurationDAO;

        GetAsyncTask(ConfigurationDAO configurationDAO){
            this.configurationDAO = configurationDAO;
        }

        @Override
        protected final List<Configuration> doInBackground(Void... voids) {
            return configurationDAO.getAll();
        }
    }

    private static class InsertAsyncTask extends AsyncTask<Configuration,Void,Void>{

        private ConfigurationDAO configurationDAO;

        InsertAsyncTask(ConfigurationDAO configurationDAO){
            this.configurationDAO = configurationDAO;
        }

        @Override
        protected final Void doInBackground(Configuration... configurations) {
            if (configurations[0] != null)
                configurationDAO.insert(configurations[0]);
            return null;
        }
    }

    private static class UpdateMetricAsyncTask extends AsyncTask<String,Void,Void>{

        private ConfigurationDAO configurationDAO;

        UpdateMetricAsyncTask(ConfigurationDAO configurationDAO){
            this.configurationDAO = configurationDAO;
        }

        @Override
        protected final Void doInBackground(String... metric) {
            configurationDAO.updateMetric(metric[0]);
            return null;
        }
    }

    private static class UpdateTypeAsyncTask extends AsyncTask<String,Void,Void>{

        private ConfigurationDAO configurationDAO;

        UpdateTypeAsyncTask(ConfigurationDAO configurationDAO){
            this.configurationDAO = configurationDAO;
        }

        @Override
        protected final Void doInBackground(String... type) {
            configurationDAO.updateTransportType(type[0]);
            return null;
        }
    }

    private static class UpdateModeAsyncTask extends AsyncTask<String,Void,Void>{

        private ConfigurationDAO configurationDAO;

        UpdateModeAsyncTask(ConfigurationDAO configurationDAO){
            this.configurationDAO = configurationDAO;
        }

        @Override
        protected final Void doInBackground(String... mode) {
            configurationDAO.updateTransportMode(mode[0]);
            return null;
        }
    }

    private static class UpdatePreferAsyncTask extends AsyncTask<String,Void,Void>{

        private ConfigurationDAO configurationDAO;

        UpdatePreferAsyncTask(ConfigurationDAO configurationDAO){
            this.configurationDAO = configurationDAO;
        }

        @Override
        protected final Void doInBackground(String... prefer) {
            configurationDAO.updatePrefTransports(prefer[0]);
            return null;
        }
    }

}
