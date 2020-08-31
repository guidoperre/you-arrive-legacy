package com.guidoperre.youarrive.ui.finalconfirmation;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.DialogController;
import com.guidoperre.youarrive.controllers.MapController;
import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.controllers.RoutesController;
import com.guidoperre.youarrive.controllers.SearchController;
import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.repositories.AlarmRepository;
import com.guidoperre.youarrive.repositories.RecoveryDataRepository;
import com.guidoperre.youarrive.repositories.RoutesRepository;
import com.guidoperre.youarrive.services.MyService;
import com.guidoperre.youarrive.ui.awaitscreen.AwaitAlarmActivity;
import com.guidoperre.youarrive.ui.routes.SelectRouteActivity;
import com.guidoperre.youarrive.utilities.UiParameters;
import com.guidoperre.youarrive.utilities.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetRouteAlarmsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    ////////////////////////////////////////////////////////////////////////////////////////////
    GoogleMap mMap;

    private MapController mapController;
    private RoutesController routesController = new RoutesController();
    private NavigationController navigationController = new NavigationController();

    private Route route;
    private ArrayList<RoutePath> routePath;
    private String address;
    private double myLatitude;
    private double myLongitude;
    private double latitude;
    private double longitude;

    private TextView addressHolder;
    private TextView tvTravelTime;
    private TextView tvAlarmCount;
    private TextView finishActivityButton;
    private ImageButton myLocationButton;
    private ImageButton addAlarm;
    private ImageButton routeDetails;
    private ImageView ellipsize;
    private RelativeLayout routeLayout;
    private ConstraintLayout bottomLayout;
    private ConstraintLayout mainLayout;
    private RelativeLayout mapContainer;
    private View separator;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.FinalConfirmationTheme);
        Utils.lockScreenOrientation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_route_alarms);
        onReady();
        finishListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onReady(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_final_confirm);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        new UiParameters().setFullScreenFlags(this);

        addressHolder = findViewById(R.id.address_select_route_finalConfirm);
        tvTravelTime = findViewById(R.id.travel_time);
        tvAlarmCount = findViewById(R.id.alarm_number);
        finishActivityButton = findViewById(R.id.ready_final_confirmation);
        myLocationButton = findViewById(R.id.myLocationButton);
        addAlarm = findViewById(R.id.addAlarm_finalConfirm);
        routeDetails = findViewById(R.id.open_route_detail);
        ellipsize = findViewById(R.id.ellipsize);
        routeLayout = findViewById(R.id.route_layout);
        separator = findViewById(R.id.view_bottom_final_confirmation);
        bottomLayout = findViewById(R.id.confirm_main_layout);
        mainLayout = findViewById(R.id.main_layout);
        mapContainer = findViewById(R.id.map_container);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getExtras(){
        if (getIntent().getExtras() != null) {
            route =  routesController.parseRouteJson(Objects.requireNonNull(getIntent().getExtras()).getString("route"));
            address = getIntent().getExtras().getString("address");
            myLatitude = getIntent().getExtras().getDouble("myLatitude");
            myLongitude = getIntent().getExtras().getDouble("myLongitude");
            latitude = getIntent().getExtras().getDouble("latitude");
            longitude = getIntent().getExtras().getDouble("longitude");
            if (address != null){
                String[] parts = address.split(",");
                addressHolder.setText(new SearchController().setAddress(parts));
            }
        } else
            Toast.makeText(this, "Hubo un error, intente de nuevo", Toast.LENGTH_SHORT).show();
        setUiComponents();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapController = new MapController(mMap);

        setMapGestureConfiguration();
        setMapTheme(mMap);

        getExtras();
        setLocationServices();
        recoverMarkers();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////OnlyAtStart////////////////////////////////////////////
    private void setLocationServices(){
        mapController.myLocationButtonForTwoPoints(this, myLocationButton, latitude,longitude,myLatitude,myLongitude);
        mapController.centerTwoPoints(this, myLocationButton);
        mMap.setOnMarkerClickListener(this);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void setLocationServicesFragment(ImageButton myLocationButton, LatLng alarmPosition){
        mapController.alarmLocationButton(this, myLocationButton, alarmPosition);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setMapTheme(GoogleMap googleMap){
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setMapGestureConfiguration(){
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void recoverMarkers(){
        AlarmRepository alarmRepository = new AlarmRepository(getApplication());
        List<Alarm> alarms = alarmRepository.getAllAlarms();

        if (alarms != null && alarms.size() > 0){
            for (Alarm alarm:alarms)
                mMap.addMarker(new MarkerOptions().position(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude())).flat(true).anchor(0.50f,0.50f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_add_alarm_foreground)).zIndex(6f).title(alarm.getTitle()));
            tvAlarmCount.setText(String.valueOf(alarms.size()));
            if (alarms.size() > 0)
                finishActivityButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_first_confirm));
            else
                finishActivityButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.set_alarm_confirm_black));
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle() != null && !marker.getTitle().equals("")){
            marker.remove();
            createAddAlarmFragment(marker.getTitle());
        }

        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setTravelTime(int travelTime){
        if (travelTime<=60){
            String travelText=travelTime+"min";
            tvTravelTime.setText(travelText);
        }else{
            String travelText=(travelTime/60)+"h y "+(travelTime%60)+"min";
            tvTravelTime.setText(travelText);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setAlarmCount(){
        AlarmRepository repository = new AlarmRepository(getApplication());
        int alarmCount = repository.getAllAlarms().size();
        tvAlarmCount.setText(String.valueOf(alarmCount));
        if (alarmCount > 0)
            finishActivityButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.round_first_confirm));
         else
            finishActivityButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.set_alarm_confirm_black));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setUiComponents(){
        drawRoute();
        fragmentChangeListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void drawRoute(){
        if (route != null){
            setTravelTime(route.getSummary().getTravelTime()/60);
            routesController.drawRoute(this, route, mapController);
            routePath = routesController.setItemList(route,false);
            routesController.setIcons(this,routePath, routeLayout, ellipsize);
            if (routePath != null)
                new RoutesRepository(getApplication()).getRoutePathNames(this,routePath);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void finishListener(){
        finishActivityButton.setOnClickListener(v -> {
            if (!tvAlarmCount.getText().toString().equals("0")){
                Gson gson = new Gson();
                String routeJSON = gson.toJson(route);
                stopService();
                new RecoveryDataRepository(getApplication()).insert(new RecoveryData("route",routeJSON,address,myLatitude,myLongitude,latitude,longitude));
                navigationController.fullIntent(this, AwaitAlarmActivity.class, myLatitude, myLongitude, latitude, longitude, address, route, "route");
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void fragmentChangeListener(){
        routeDetails.setOnClickListener(v -> createRouteDetailFragment());
        addAlarm.setOnClickListener(v -> createAddAlarmFragment("new"));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createRouteDetailFragment(){
        RouteDetailFragment routeDetailFragment = new RouteDetailFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String routePathJSON = gson.toJson(routePath);

        bundle.putString("routePath", routePathJSON);
        routeDetailFragment.setArguments(bundle);

        fragmentManager.beginTransaction().setCustomAnimations(R.anim.top_in,0).replace(R.id.fragment_container, routeDetailFragment , "ROUTE_DETAIL_FRAGMENT").commit();
        separator.animate().alpha(0f).setDuration(500).withEndAction(() -> separator.setVisibility(View.GONE));
        mainLayout.animate().alpha(0f).setDuration(500).withEndAction(() -> mainLayout.setVisibility(View.GONE));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createAddAlarmFragment(String title){
        AddAlarmFragment addAlarmFragment = new AddAlarmFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String routePathJSON = gson.toJson(routePath);
        String travelTime = gson.toJson(route.getShape());

        bundle.putString("title", title);
        bundle.putString("routePath", routePathJSON);
        bundle.putString("routeShape", travelTime);
        addAlarmFragment.setArguments(bundle);

        fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in,0).replace(R.id.fragment_container, addAlarmFragment, "ADD_ALARM_FRAGMENT").commit();
        mainLayout.animate().alpha(0f).setDuration(300).withEndAction(() -> mainLayout.setVisibility(View.GONE));
        bottomLayout.animate().alpha(0f).setDuration(400).withEndAction(() -> bottomLayout.setVisibility(View.GONE));
        separator.animate().alpha(0f).setDuration(400).withEndAction(() -> separator.setVisibility(View.GONE));
        mapContainer.animate().translationY((float) (new Utils().getLayoutHeight()*0.250)).setDuration(250);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getBackFromRouteDetail(Fragment routeDetailsFragment){
        fragmentManager.beginTransaction().setCustomAnimations(0,R.anim.top_out).remove(routeDetailsFragment).commit();
        mainLayout.setVisibility(View.VISIBLE);
        mainLayout.animate().alpha(1f).setDuration(350).setListener(null);
        separator.setVisibility(View.VISIBLE);
        separator.animate().alpha(1f).setDuration(350).setListener(null);
        bottomLayout.setVisibility(View.VISIBLE);
        bottomLayout.animate().alpha(1f).setDuration(100).setListener(null);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    void getBackFromAddAlarm(Fragment addAlarmFragment){
        setLocationServices();
        setAlarmCount();

        fragmentManager.beginTransaction().setCustomAnimations(0,R.anim.fade_out).remove(addAlarmFragment).commit();
        separator.setVisibility(View.VISIBLE);
        separator.animate().alpha(1f).setDuration(400).setListener(null);
        mainLayout.setVisibility(View.VISIBLE);
        mainLayout.animate().alpha(1f).setDuration(400).setListener(null);
        bottomLayout.setVisibility(View.VISIBLE);
        bottomLayout.animate().alpha(1f).setDuration(400).setListener(null);
        mapContainer.animate().translationY((float) 0).setDuration(250);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void stopService(){
        if (MyService.serviceActive){
            MyService.serviceActive = false;
            stopService(new Intent(this,MyService.class));
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////Super///////////////////////////////////////////////
    @Override
    public void onBackPressed () {
        Fragment routeDetailsFragment = fragmentManager.findFragmentByTag("ROUTE_DETAIL_FRAGMENT");
        Fragment addAlarmFragment = fragmentManager.findFragmentByTag("ADD_ALARM_FRAGMENT");

        if (routeDetailsFragment != null && routeDetailsFragment.isVisible())
            getBackFromRouteDetail(routeDetailsFragment);
        else if (addAlarmFragment != null && addAlarmFragment.isVisible())
            getBackFromAddAlarm(addAlarmFragment);
        else{
            new DialogController().openCancelRouteDialog(this,this,SelectRouteActivity.class,new Intent(this,MyService.class),myLatitude,myLongitude,latitude,longitude,address);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
