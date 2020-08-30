package com.guidoperre.youarrive.ui.suggests;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.guidoperre.youarrive.models.AutoSuggest;
import com.guidoperre.youarrive.repositories.SuggestsRepository;

import java.util.List;

class SuggestsViewModel extends AndroidViewModel {

    private MutableLiveData<List<AutoSuggest>> dataList;

    private SuggestsRepository suggestsRepository;

    SuggestsViewModel(@NonNull Application application) {
        super(application);
        suggestsRepository = new SuggestsRepository(application);
        dataList = suggestsRepository.getSuggestions();
    }

    LiveData<List<AutoSuggest>> getSuggestions(){
        return dataList;
    }

    void cancelAsyncTasks(){
        suggestsRepository.stopAsyncTask();
    }

}
