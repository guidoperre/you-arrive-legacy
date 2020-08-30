package com.guidoperre.youarrive.ui.finalconfirmation;

import androidx.fragment.app.FragmentActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.MapController;
import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.controllers.RoutesController;
import com.guidoperre.youarrive.controllers.SearchController;
import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.repositories.AlarmRepository;
import com.guidoperre.youarrive.ui.awaitscreen.AwaitAlarmActivity;
import com.guidoperre.youarrive.utilities.UiParameters;
import com.guidoperre.youarrive.utilities.Utils;

import java.util.List;
import java.util.Objects;

public class RoutePreviewActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private MapController mapController;
    private RoutesController routesController = new RoutesController();
    private NavigationController navigationController = new NavigationController();

    private Route route;
    private String address;
    private double myLatitude;
    private double myLongitude;
    private double latitude;
    private double longitude;

    private TextView addressHolder;
    private TextView goBackPreview;
    private ImageButton myLocationButton;

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.lockScreenOrientation(this);
        setContentView(R.layout.activity_route_preview);
        onReady();
        finishPreviewListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onReady(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_final_confirm);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        new UiParameters().setFullScreenFlags(this);

        addressHolder = findViewById(R.id.address_select_route_preview);
        goBackPreview = findViewById(R.id.go_back_preview);
        myLocationButton = findViewById(R.id.myLocationButton);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getExtras(){
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("route") != null)
                route = routesController.parseRouteJson(Objects.requireNonNull(getIntent().getExtras()).getString("route"));
            address = getIntent().getExtras().getString("address");
            myLatitude = getIntent().getExtras().getDouble("myLatitude");
            myLongitude = getIntent().getExtras().getDouble("myLongitude");
            latitude = getIntent().getExtras().getDouble("latitude");
            longitude = getIntent().getExtras().getDouble("longitude");
            if (address != null){
                String[] parts = address.split(",");
                addressHolder.setText(new SearchController().setAddress(parts));
            }
            if (route != null)
                routesController.drawRoute(this, route, mapController);
        } else
            Toast.makeText(this, "Hubo un error, intente de nuevo", Toast.LENGTH_SHORT).show();
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
        setMarkers();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////OnlyAtStart////////////////////////////////////////////
    private void setLocationServices(){
        if (route != null){
            mapController.myLocationButtonForTwoPoints(this, myLocationButton, latitude,longitude,myLatitude,myLongitude);
            mapController.centerTwoPoints(this, myLocationButton);
        } else{
            mapController.alarmLocationButton(this, myLocationButton,new LatLng(latitude-0.001,longitude));
            mapController.moveCameraToSelectedPosition(new LatLng(latitude-0.001,longitude));
        }
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
    private void setMarkers(){
        AlarmRepository alarmRepository = new AlarmRepository(getApplication());
        List<Alarm> alarms = alarmRepository.getAllAlarms();

        if (alarms != null && alarms.size() > 0)
            for (Alarm alarm:alarms)
                mMap.addMarker(new MarkerOptions().position(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude())).flat(true).anchor(0.50f,0.50f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_add_alarm_foreground)).zIndex(6f).title(alarm.getTitle()));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void finishPreviewListener(){
        goBackPreview.setOnClickListener(v -> goBack());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void goBack(){
        navigationController.fullIntent(this, AwaitAlarmActivity.class, myLatitude, myLongitude, latitude, longitude, address, route, "route");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        goBack();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}