package com.guidoperre.youarrive.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.repositories.RoutesRepository;

import java.util.List;

public class ServiceViewModel extends ViewModel {

    private MutableLiveData<List<Route>> dataList;

    private RoutesRepository routesRepository = new RoutesRepository();

    ServiceViewModel() {
        dataList = routesRepository.getRoutes();
    }

    LiveData<List<Route>> getRoutes(){
        return dataList;
    }

    void cancelAsyncTasks(){
        routesRepository.stopAsyncTask();
    }

}
