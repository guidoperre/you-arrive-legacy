package com.guidoperre.youarrive.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.LastAlarmConfiguration;
import com.guidoperre.youarrive.room.AppDatabase;
import com.guidoperre.youarrive.room.dao.AlarmDAO;
import com.guidoperre.youarrive.room.dao.LastAlarmConfigurationDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmRepository {

    private static AlarmDAO alarmDAO;
    private static LastAlarmConfigurationDAO lastAlarmConfigurationDAO;

    private static LiveData<List<Alarm>> dataList;

    public AlarmRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application.getApplicationContext());
        if (database != null){
            alarmDAO = database.alarmDAO();
            lastAlarmConfigurationDAO = database.lastAlarmConfigurationDAO();
        }
        dataList = alarmDAO.getAll();
    }

    public LiveData<List<Alarm>> get(){ return dataList; }

    public List<Alarm> getAlarm(String title){
        try {
            return new GetAlarmAsyncTask(alarmDAO).execute(title).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Alarm> getAllAlarms(){
        try {
            return new GetAllAlarmsAsyncTask(alarmDAO).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Alarm alarm){
        new InsertAlarmAsyncTask(alarmDAO).execute(alarm);
    }

    public void update(String title,double latitude, double longitude, String uri, int volume, int safezone){
        ArrayList<String> params = new ArrayList<>();

        params.add(0,title);
        params.add(1,String.valueOf(latitude));
        params.add(2,String.valueOf(longitude));
        params.add(3,uri);
        params.add(4,String.valueOf(volume));
        params.add(5,String.valueOf(safezone));

        new UpdateAlarmAsyncTask(alarmDAO).execute(params);
    }

    public void deleteAlarm(String title){
        new DeleteAlarmAsyncTask(alarmDAO).execute(title);
    }

    public void deleteAll(){
        new DeleteAllAlarmAsyncTask(alarmDAO).execute();
    }

    public List<LastAlarmConfiguration> getLastConfiguration(){
        try {
                return new GetConfigurationAsyncTask(lastAlarmConfigurationDAO).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertConfiguration(LastAlarmConfiguration lastConfigurationList){
        new InsertConfigurationAsyncTask(lastAlarmConfigurationDAO).execute(lastConfigurationList);
    }

    private static class GetAlarmAsyncTask extends AsyncTask<String,Void,List<Alarm>> {

        private AlarmDAO alarmDAO;

        GetAlarmAsyncTask(AlarmDAO alarmDAO){
            this.alarmDAO = alarmDAO;
        }

        @Override
        protected final List<Alarm> doInBackground(String... strings) {
            return alarmDAO.getAlarm(strings[0]);
        }
    }

    private static class GetAllAlarmsAsyncTask extends AsyncTask<Void,Void,List<Alarm>> {

        private AlarmDAO alarmDAO;

        GetAllAlarmsAsyncTask(AlarmDAO alarmDAO){
            this.alarmDAO = alarmDAO;
        }

        @Override
        protected final List<Alarm> doInBackground(Void... voids) {
            return alarmDAO.getAllAlarm();
        }
    }

    private static class InsertAlarmAsyncTask extends AsyncTask<Alarm,Void,Void> {

        private AlarmDAO alarmDAO;

        InsertAlarmAsyncTask(AlarmDAO alarmDAO){
            this.alarmDAO = alarmDAO;
        }

        @Override
        protected final Void doInBackground(Alarm... alarms) {
            if (alarms[0] != null)
                alarmDAO.insert(alarms[0]);
            return null;
        }
    }

    private static class UpdateAlarmAsyncTask extends AsyncTask<ArrayList<String>,Void,Void> {

        private AlarmDAO alarmDAO;

        UpdateAlarmAsyncTask(AlarmDAO alarmDAO){
            this.alarmDAO = alarmDAO;
        }

        @Override
        protected final Void doInBackground(ArrayList<String>... paramsList) {
            ArrayList<String> params = paramsList[0];
            if (params != null)
                alarmDAO.update(params.get(0), Double.parseDouble(params.get(1)), Double.parseDouble(params.get(2)), params.get(3), Integer.parseInt(params.get(4)), Integer.parseInt(params.get(5)));
            return null;
        }
    }

    private static class DeleteAlarmAsyncTask extends AsyncTask<String,Void,Void> {

        private AlarmDAO alarmDAO;

        DeleteAlarmAsyncTask(AlarmDAO alarmDAO){
            this.alarmDAO = alarmDAO;
        }

        @Override
        protected final Void doInBackground(String... params) {
            if (params != null)
                alarmDAO.deleteAlarm(params[0]);
            return null;
        }
    }

    private static class DeleteAllAlarmAsyncTask extends AsyncTask<Void,Void,Void> {

        private AlarmDAO alarmDAO;

        DeleteAllAlarmAsyncTask(AlarmDAO alarmDAO){
            this.alarmDAO = alarmDAO;
        }

        @Override
        protected final Void doInBackground(Void... voids) {
            alarmDAO.deleteAll();
            return null;
        }
    }

    private static class GetConfigurationAsyncTask extends AsyncTask<Void,Void,List<LastAlarmConfiguration>> {

        private LastAlarmConfigurationDAO lastAlarmConfigurationDAO;

        GetConfigurationAsyncTask(LastAlarmConfigurationDAO lastAlarmConfigurationDAO){
            this.lastAlarmConfigurationDAO = lastAlarmConfigurationDAO;
        }

        @Override
        protected final List<LastAlarmConfiguration> doInBackground(Void... voids) {
            return lastAlarmConfigurationDAO.getAll();
        }
    }

    private static class InsertConfigurationAsyncTask extends AsyncTask<LastAlarmConfiguration,Void,Void> {

        private LastAlarmConfigurationDAO lastAlarmConfigurationDAO;

        InsertConfigurationAsyncTask(LastAlarmConfigurationDAO lastAlarmConfigurationDAO){
            this.lastAlarmConfigurationDAO = lastAlarmConfigurationDAO;
        }

        @Override
        protected final Void doInBackground(LastAlarmConfiguration... configurations) {
            if (configurations[0] != null){
                lastAlarmConfigurationDAO.deleteAll();
                lastAlarmConfigurationDAO.insert(configurations[0]);
            }
            return null;
        }
    }
}
