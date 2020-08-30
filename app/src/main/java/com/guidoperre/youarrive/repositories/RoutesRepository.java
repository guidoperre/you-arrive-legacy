package com.guidoperre.youarrive.repositories;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.models.RouteResponse;
import com.guidoperre.youarrive.retrofit.HTTPRequest;
import com.guidoperre.youarrive.retrofit.RetrofitNewInstance;
import com.guidoperre.youarrive.room.AppDatabase;
import com.guidoperre.youarrive.room.dao.RoutePathDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

import static com.guidoperre.youarrive.controllers.RetrofitController.getApiKey;

public class RoutesRepository {

    private static RoutePathDAO routePathDAO;
    private static MutableLiveData<List<Route>> routeList = new MutableLiveData<>();
    private static LiveData<List<RoutePath>> routePathList;

    private static GetRoutesAsyncTask getRoutesAsyncTask;
    private static Call<RouteResponse> call;

    public RoutesRepository(Application application){
        AppDatabase database = AppDatabase.getDatabase(application.getApplicationContext());
        if (database != null){
            routePathDAO = database.routePathDAO();
        }
        routePathList = routePathDAO.get();
    }

    public RoutesRepository(){
        routeList.setValue(Collections.emptyList());
    }

    public void get(String waypoint0, String waypoint1, String mode, String prefer, String metric, String lineAttributes, String routeAttributes, String combineChange,String departure, String alternatives){
        ArrayList<String> params = new ArrayList<>();
        params.add(0,waypoint0);
        params.add(1,waypoint1);
        params.add(2,mode);
        params.add(3,prefer);
        params.add(4,metric);
        params.add(5,lineAttributes);
        params.add(6,routeAttributes);
        params.add(7,combineChange);
        params.add(8,departure);
        params.add(9,alternatives);
        getRoutesAsyncTask = (GetRoutesAsyncTask) new GetRoutesAsyncTask().execute(params);
    }

    public LiveData<List<RoutePath>> getRoutePath(){ return routePathList; }

    public void getRoutePathNames(Context context, ArrayList<RoutePath> routePathList){
        new GetLinkAddressAsyncTask(context,routePathDAO).execute(routePathList);
    }

    public MutableLiveData<List<Route>> getRoutes(){
        return routeList;
    }

    public void stopAsyncTask(){
        if (getRoutesAsyncTask != null){
            getRoutesAsyncTask.cancel(true);
            if (call != null && call.isExecuted())
                call.cancel();
        }
    }

    private static class GetRoutesAsyncTask extends AsyncTask<ArrayList<String>,Void,Void> {

        GetRoutesAsyncTask(){}

        @Override
        protected final Void doInBackground(ArrayList<String>... paramsList) {
            RetrofitNewInstance retrofit = new RetrofitNewInstance();
            HTTPRequest service = retrofit.newInstance("routes");
            ArrayList<String> params = paramsList[0];

            call = service.routesAPI(getApiKey(),params.get(0),params.get(1),params.get(2),params.get(3),params.get(4),params.get(5),params.get(6),params.get(7),params.get(8),params.get(9));
            Response<RouteResponse> response;
            RouteResponse routes;

            try {
                response = call.execute();
            } catch (Throwable t){
                return null;
            }

            if (response.isSuccessful()) {
                routes = response.body();
                if (routes != null) {
                    routeList.postValue(routes.getResponse().getRoutes());
                }
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class GetLinkAddressAsyncTask extends AsyncTask<ArrayList<RoutePath>,Void,Void> {

        private RoutePathDAO routePathDAO;
        private Context mContext;

        GetLinkAddressAsyncTask(Context context, RoutePathDAO routePathDAO){
            this.routePathDAO = routePathDAO;
            this.mContext = context;
        }

        @Override
        protected final Void doInBackground(ArrayList<RoutePath>... routePathList) {
            ArrayList<RoutePath> routePath = routePathList[0];
            ArrayList<RoutePath> insertRoutePath = new ArrayList<>();
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            routePathDAO.deleteAll();

            for (RoutePath path: routePath){
                try {
                    List<Address> myActualAddress = geocoder.getFromLocation(path.getStartLatitude(), path.getStartLongitude(), 1);

                    if (myActualAddress.size() != 0) {
                        String aux = myActualAddress.get(0).getAddressLine(0);
                        String[] address_title = aux.split(",");
                        path.setStartRoadName(address_title[0]);
                    }
                } catch (IOException ignored){}
                try {
                    List<Address> myActualAddress = geocoder.getFromLocation(path.getEndLatitude(), path.getEndLongitude(), 1);

                    if (myActualAddress.size() != 0) {
                        String aux = myActualAddress.get(0).getAddressLine(0);
                        String[] address_title = aux.split(",");
                        path.setEndRoadName(address_title[0]);
                    }
                } catch (IOException ignored){}
                insertRoutePath.add(path);
            }
            routePathDAO.insert(insertRoutePath);
            return null;
        }
    }

}
