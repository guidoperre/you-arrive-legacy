package com.guidoperre.youarrive.ui.awaitscreen;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.guidoperre.youarrive.models.RemainingTime;
import com.guidoperre.youarrive.repositories.RemainingTimeRepository;

import java.util.List;

class AwaitAlarmViewModel extends AndroidViewModel {

    private LiveData<List<RemainingTime>> dataList;

    public AwaitAlarmViewModel(@NonNull Application application) {
        super(application);
        RemainingTimeRepository remainingTimeRepository = new RemainingTimeRepository(application);
        dataList = remainingTimeRepository.get();
    }

    LiveData<List<RemainingTime>> getTime(){
        return dataList;
    }


}
