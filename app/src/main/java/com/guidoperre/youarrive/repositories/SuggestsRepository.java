package com.guidoperre.youarrive.repositories;

import android.app.Application;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.guidoperre.youarrive.models.AutoSuggest;
import com.guidoperre.youarrive.models.AutoSuggestResponse;
import com.guidoperre.youarrive.retrofit.HTTPRequest;
import com.guidoperre.youarrive.retrofit.RetrofitNewInstance;
import com.guidoperre.youarrive.room.AppDatabase;
import com.guidoperre.youarrive.room.dao.AutoSuggestDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;

import static com.guidoperre.youarrive.controllers.RetrofitController.getApiKey;
import static com.guidoperre.youarrive.controllers.RetrofitController.getAppCode;
import static com.guidoperre.youarrive.controllers.RetrofitController.getAppId;


public class SuggestsRepository {

    private static AutoSuggestDAO suggestDAO;
    private static MutableLiveData<List<AutoSuggest>> suggestList = new MutableLiveData<>();

    private static GetSuggestionAsyncTask getSuggestionAsyncTask;
    private static Call<AutoSuggestResponse> call;

    public SuggestsRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application.getApplicationContext());
        if (database != null){
            suggestDAO = database.autoSuggestDAO();
        }
    }

    public SuggestsRepository() {
    }

    public void get(String query,String countryCode){
        ArrayList<String> params = new ArrayList<>();

        params.add(0,query);
        if (countryCode != null && !countryCode.equals(""))
            params.add(1,countryCode);

        getSuggestionAsyncTask = (GetSuggestionAsyncTask) new GetSuggestionAsyncTask().execute(params);
    }

    public List<AutoSuggest> getByType(String type){
        try {
            return new GetSuggestionByTypeAsyncTask(suggestDAO).execute(type).get();
        } catch (ExecutionException | InterruptedException t) {
            return null;
        }
    }

    public MutableLiveData<List<AutoSuggest>> getSuggestions(){
        return suggestList;
    }

    public void insert(AutoSuggest suggestions){
        new InsertSuggestionsAsyncTask(suggestDAO).execute(suggestions);
    }

    public void updateByLocationID(String locationID, String latitude, String longitude){
        ArrayList<String> params = new ArrayList<>();
        params.add(0,locationID);
        params.add(1,latitude);
        params.add(2,longitude);
        new UpdateByLocationIDAsyncTask(suggestDAO).execute(params);
    }

    public void delete(String type){
        new DeleteAsyncTask(suggestDAO).execute(type);
    }

    public void deleteByLocationID(String locationID){
        new DeleteByLocationIDAsyncTask(suggestDAO).execute(locationID);
    }

    public void stopAsyncTask(){
        if (getSuggestionAsyncTask != null){
            getSuggestionAsyncTask.cancel(true);
            if (call != null && call.isExecuted())
                call.cancel();
        }
    }

    private static class GetSuggestionAsyncTask extends AsyncTask<ArrayList<String>,Void,Void>{


        GetSuggestionAsyncTask(){}

        @Override
        protected final Void doInBackground(ArrayList<String>... paramsList) {
            RetrofitNewInstance retrofit = new RetrofitNewInstance();
            HTTPRequest service = retrofit.newInstance("autosuggest");

            ArrayList<String> params = paramsList[0];

            call = service.suggestAPI(getAppId(),getAppCode(),params.get(0),"ARG");

            Response<AutoSuggestResponse> response;
            List<AutoSuggest> suggests;

            try {
                response = call.execute();
            } catch (IllegalArgumentException|IOException t){
                return null;
            }

            if (response.isSuccessful() && response.body() != null) {
                suggests = response.body().getResponse();
                if (suggests != null) {
                    for (AutoSuggest suggest : suggests)
                        suggest.setType("suggest");
                    suggestList.postValue(suggests);
                }
            }

            return null;
        }
    }

    private static class GetSuggestionByTypeAsyncTask extends AsyncTask<String,Void,List<AutoSuggest>>{

        private AutoSuggestDAO suggestDAO;

        GetSuggestionByTypeAsyncTask(AutoSuggestDAO suggestDAO){this.suggestDAO = suggestDAO;}

        @Override
        protected final List<AutoSuggest> doInBackground(String... query) {
            return suggestDAO.get(query[0]);
        }
    }

    private static class InsertSuggestionsAsyncTask extends AsyncTask<AutoSuggest,Void,Void>{

        private AutoSuggestDAO suggestDAO;

        InsertSuggestionsAsyncTask(AutoSuggestDAO suggestDAO){
            this.suggestDAO = suggestDAO;
        }

        @Override
        protected final Void doInBackground(AutoSuggest... suggestions) {
            if (suggestions[0] != null)
                suggestDAO.insert(suggestions[0]);
            return null;
        }
    }

    private static class UpdateByLocationIDAsyncTask extends AsyncTask<ArrayList<String>,Void,Void>{

        private AutoSuggestDAO suggestDAO;

        UpdateByLocationIDAsyncTask(AutoSuggestDAO suggestDAO){
            this.suggestDAO = suggestDAO;
        }

        @Override
        protected final Void doInBackground(ArrayList<String>... paramsList) {
            ArrayList<String> params = paramsList[0];
            suggestDAO.updateByLocationID(params.get(0),Double.parseDouble(params.get(1)),Double.parseDouble(params.get(2)));
            return null;
        }
    }

    private static class DeleteByLocationIDAsyncTask extends AsyncTask<String,Void,Void>{

        private AutoSuggestDAO suggestDAO;

        DeleteByLocationIDAsyncTask(AutoSuggestDAO suggestDAO){
            this.suggestDAO = suggestDAO;
        }

        @Override
        protected final Void doInBackground(String... locationID) {
            suggestDAO.deleteByLocationID(locationID[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<String,Void,Void>{

        private AutoSuggestDAO suggestDAO;

        DeleteAsyncTask(AutoSuggestDAO suggestDAO){
            this.suggestDAO = suggestDAO;
        }

        @Override
        protected final Void doInBackground(String... type) {
            suggestDAO.delete(type[0]);
            return null;
        }
    }

}
