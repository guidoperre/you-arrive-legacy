package com.guidoperre.youarrive.ui.routes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.repositories.RoutesRepository;

import java.util.List;

public class RoutesViewModel extends AndroidViewModel {

    private MutableLiveData<List<Route>> dataList;

    public MutableLiveData<Boolean> makeCall = new MutableLiveData<>();

    private RoutesRepository routesRepository = new RoutesRepository();

    RoutesViewModel(@NonNull Application application) {
        super(application);
        dataList = routesRepository.getRoutes();
    }

    LiveData<List<Route>> getRoutes(){
        return dataList;
    }

    void cancelAsyncTasks(){
        routesRepository.stopAsyncTask();
    }

}
