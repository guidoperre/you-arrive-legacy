package com.guidoperre.youarrive.ui.finalconfirmation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.guidoperre.youarrive.models.AlarmTone;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.repositories.AlarmToneRepository;
import com.guidoperre.youarrive.repositories.RoutesRepository;

import java.util.List;

class FinalConfirmationViewModel extends AndroidViewModel {

    private LiveData<List<RoutePath>> routePathList;
    private LiveData<List<AlarmTone>> alarmToneList;

    FinalConfirmationViewModel(@NonNull Application application) {
        super(application);
        RoutesRepository routesRepository = new RoutesRepository();
        AlarmToneRepository alarmToneRepository = new AlarmToneRepository(application);
        routePathList = routesRepository.getRoutePath();
        alarmToneList =  alarmToneRepository.get();
    }

    LiveData<List<RoutePath>> getRoutePath(){
        return routePathList;
    }

    LiveData<List<AlarmTone>> getAlarmTone(){
        return alarmToneList;
    }

}
