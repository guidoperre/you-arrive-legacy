package com.guidoperre.youarrive.repositories;

import android.os.AsyncTask;

import com.guidoperre.youarrive.models.GeoCode;
import com.guidoperre.youarrive.models.GeoCodeResponse;
import com.guidoperre.youarrive.retrofit.HTTPRequest;
import com.guidoperre.youarrive.retrofit.RetrofitNewInstance;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;

import static com.guidoperre.youarrive.controllers.RetrofitController.getApiKey;
import static com.guidoperre.youarrive.controllers.RetrofitController.getAppCode;
import static com.guidoperre.youarrive.controllers.RetrofitController.getAppId;

public class GeoCodeRepository {

    public GeoCodeRepository() {
    }

    public GeoCode callGeocode(String locationID){
        try{
            return new callGeocodeAsyncTask().execute(locationID).get();
        } catch (InterruptedException | ExecutionException t){
            return null;
        }
    }

    private static class callGeocodeAsyncTask extends AsyncTask<String,Void,GeoCode> {

        callGeocodeAsyncTask(){}

        @Override
        protected final GeoCode doInBackground(String... locationID) {
            RetrofitNewInstance retrofit = new RetrofitNewInstance();
            HTTPRequest service = retrofit.newInstance("geocode");
            Call<GeoCodeResponse> call = service.geocodeAPI(getAppId(),getAppCode(),locationID[0]);
            Response<GeoCodeResponse> response;
            GeoCodeResponse location;
            GeoCode geoCode = new GeoCode();

            try {
                response = call.execute();
            } catch (IllegalArgumentException| IOException t){
                return null;
            }

            if (response.isSuccessful()) {
                location = response.body();
                if (location != null)
                    geoCode = location.getResponse().getView().get(0).getResult().get(0).getLocation().getGeocode();
            }

            return geoCode;
        }
    }
}
