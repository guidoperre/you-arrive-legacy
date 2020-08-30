package com.guidoperre.youarrive.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitNewInstance {

        public HTTPRequest newInstance(String api_name){
            HTTPRequest service = null;
            Retrofit retrofit;


            switch (api_name){
                case "autosuggest":
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://autocomplete.geocoder.api.here.com/6.2/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    service = retrofit.create(HTTPRequest.class);
                    break;
                case "geocode":
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://geocoder.api.here.com/6.2/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    service = retrofit.create(HTTPRequest.class);
                    break;
                case "routes":
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://route.ls.hereapi.com/routing/7.2/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    service = retrofit.create(HTTPRequest.class);
                    break;
            }

            return service;
        }

}
