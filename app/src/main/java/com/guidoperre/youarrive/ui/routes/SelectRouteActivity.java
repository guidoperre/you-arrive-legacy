package com.guidoperre.youarrive.ui.routes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.adapters.RoutesAdapter;
import com.guidoperre.youarrive.controllers.ConfigurationController;
import com.guidoperre.youarrive.controllers.DialogController;
import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.controllers.RoutesController;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.repositories.AlarmRepository;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;
import com.guidoperre.youarrive.repositories.RecoveryDataRepository;
import com.guidoperre.youarrive.repositories.RoutesRepository;
import com.guidoperre.youarrive.ui.firstconfirmation.FirstConfirmationActivity;
import com.guidoperre.youarrive.utilities.Utils;

public class SelectRouteActivity extends AppCompatActivity {
    //////////////////////////////////Variables///////////////////////////////////////////////
    private RoutesAdapter adapter;

    private RoutesViewModel model;

    private RoutesController routesController = new RoutesController();
    private NavigationController navigationController = new NavigationController();

    private ArrayList<Route> resultRoutes = new ArrayList<>();

    private String address;
    private double myLatitude;
    private double myLongitude;
    private double latitude;
    private double longitude;
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////onCreate//////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.RouteSelect);
        Utils.lockScreenOrientation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);
        getExtras();
        initializeViewModel();
        instanceRecyclerView();
        makeCall();
        setSettingsListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////getExtras//////////////////////////////////////////////
    private void getExtras(){
        TextView addressText = findViewById(R.id.address_select_route);

        if (getIntent().getExtras() != null){
            address = getIntent().getExtras().getString("address");
            myLatitude = getIntent().getExtras().getDouble("myLatitude");
            myLongitude = getIntent().getExtras().getDouble("myLongitude");
            latitude = getIntent().getExtras().getDouble("latitude");
            longitude = getIntent().getExtras().getDouble("longitude");
            addressText.setText(address);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeViewModel(){
        model = new RoutesViewModel(getApplication());
        model.getRoutes().observe(this, this::updateRoutes);
        model.makeCall.observe(this, change -> {
            if (change){
                model.makeCall.setValue(false);
                makeCall();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////RecyclerView Adapter//////////////////////////////////////
    private void instanceRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.routesRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        createStaticItems();
        resultAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////4

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void resultAdapter(){
        adapter = new RoutesAdapter(resultRoutes, (resultRoutes, position) -> {
            model.cancelAsyncTasks();
            new AlarmRepository(getApplication()).deleteAll();
            new RecoveryDataRepository(getApplication()).deleteAll();
            if(position == 0)
                navigationController.selectNoRoute(this,latitude,longitude,myLatitude,myLongitude,address);
            else if (!resultRoutes.getMode().getType().equals("loading"))
                navigationController.selectRoute(this,latitude,longitude,myLatitude,myLongitude,address,resultRoutes);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void updateRoutes(List<Route> routes) {
        if (routes.size() != 0){
            resultRoutes.clear();
            resultRoutes.add( new RoutesController().getFirstPlace());
            resultRoutes.addAll(routes);
            adapter.notifyDataSetChanged();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createStaticItems(){
        resultRoutes = routesController.setItems();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void makeCall(){
        ConfigurationRepository configurationRepository = new ConfigurationRepository(getApplication());
        Configuration configuration = configurationRepository.get().get(0);
        ConfigurationController configurationController = new ConfigurationController();
        String type = configurationController.getType(configuration.getTransportType());
        String mode = configurationController.getMode(configuration.getTransportMode());
        String prefer = configurationController.getPrefer(configuration.getPrefTransports());
        new RoutesRepository().get(myLatitude+","+myLongitude,latitude+","+longitude,mode + ";" + type + ";" +"traffic:enabled",prefer,"metric","typeName,stops","sh","true","now","5");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setSettingsListener(){
        ImageButton settings = findViewById(R.id.routes_settings);
        settings.setOnClickListener(view -> {
            model.cancelAsyncTasks();
            new DialogController().openRouteSettingsDialog(this,getApplication(),model);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////Super OnBack//////////////////////////////////////////
    @Override
    public void onBackPressed () {
        navigationController.normalIntent(this,FirstConfirmationActivity.class,latitude,longitude,address);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
