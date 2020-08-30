package com.guidoperre.youarrive.application;

import android.app.Application;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;

import java.util.List;

public class YouArrive extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeConfiguration();
    }

    private void initializeConfiguration(){
        ConfigurationRepository configurationRepository = new ConfigurationRepository(this);
        List<Configuration> configurationList = configurationRepository.get();

        if (configurationList != null)
            if (configurationList.size() == 0)
                configurationRepository.insert(new Configuration("metric","publicTransportTimeTable","fastest",""));
    }

}
