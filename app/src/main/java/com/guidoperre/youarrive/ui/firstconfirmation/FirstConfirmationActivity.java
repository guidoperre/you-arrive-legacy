package com.guidoperre.youarrive.ui.firstconfirmation;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.MapController;
import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.controllers.SearchController;
import com.guidoperre.youarrive.ui.routes.SelectRouteActivity;
import com.guidoperre.youarrive.ui.suggests.SuggestsActivity;
import com.guidoperre.youarrive.utilities.UiParameters;
import com.guidoperre.youarrive.utilities.Utils;

import pl.droidsonroids.gif.GifImageView;

public class FirstConfirmationActivity extends FragmentActivity implements OnMapReadyCallback {
    ////////////////////////////////////////////////////////////////////////////////////////////
    private GoogleMap mMap;

    private MapController mapController;
    private NavigationController navigationController = new NavigationController();

    private TextView addressHolder;
    private GifImageView loadingDots;
    private ImageButton myLocationButton;

    private double latitude;
    private double longitude;

    private Handler handler = new Handler();
    private Runnable runnable;
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.FirstConfirmTheme);
        Utils.lockScreenOrientation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_confirmation);

        onReady();
        onReadyListener();
        onBackListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapController = new MapController(mMap);

        setMapGestureConfiguration();
        setMapTheme();

        setLocationServices();
        getExtras();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onReady(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_to_first_confirm);
        if(mapFragment != null)
            mapFragment.getMapAsync(this);
        new UiParameters().setFullScreenFlags(this);

        addressHolder = findViewById(R.id.firstConfirm_back);
        loadingDots = findViewById(R.id.address_loading_dots);
        myLocationButton = findViewById(R.id.myLocationButton);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////Location Services/////////////////////////////////////////
    private void setLocationServices(){
        mapController.moveCameraToMyPosition(this, myLocationButton);
        mapController.myLocationButton(this, myLocationButton);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////Map Theme//////////////////////////////////////////////
    private void setMapTheme(){
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));

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
        if (getIntent().getExtras() != null){
            uiSettings.setScrollGesturesEnabled(false);
            uiSettings.setScrollGesturesEnabledDuringRotateOrZoom(false);
            uiSettings.setZoomGesturesEnabled(false);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getExtras(){
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getDouble("latitude") != 0.0 && getIntent().getExtras().getDouble("longitude") != 0.0) {
                latitude = getIntent().getExtras().getDouble("latitude");
                longitude = getIntent().getExtras().getDouble("longitude");
                String address = getIntent().getExtras().getString("address");
                if (address != null){
                    String[] parts = address.split(",");
                    addressHolder.setText(new SearchController().setAddress(parts));
                }
                mapController.moveCameraToSelectedPosition(new LatLng(latitude,longitude));
            }
        }else{
            mapController.moveCameraToMyPosition(this, myLocationButton);
            cameraMovementListener();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void cameraMovementListener(){
        loadingDots.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> {
            mMap.setOnCameraIdleListener(() -> {
                if (handler != null){
                    handler.removeCallbacks(runnable);
                }
                loadingDots.setVisibility(View.VISIBLE);
                addressHolder.setText("");
                placeHoldTime();
            });
            tryGetLocation();
        }, 2000);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void placeHoldTime(){
        runnable = this::tryGetLocation;
        handler.postDelayed(runnable, 1000);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////Try To Get Selected Location////////////////////////////////////
    private void tryGetLocation(){
        latitude = mMap.getCameraPosition().target.latitude;
        longitude = mMap.getCameraPosition().target.longitude;

        String address = mapController.tryGetGeocode(this, latitude,longitude);

        loadingDots.setVisibility(View.INVISIBLE);
        addressHolder.setText(address);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onReadyListener(){
        TextView firstConfirmButton = findViewById(R.id.firstConfirmButton);
        firstConfirmButton.setOnClickListener(v -> {
            LatLng myPosition = mapController.getMyPosition(this);
            navigationController.semiFullIntent(this,SelectRouteActivity.class,myPosition.latitude,myPosition.longitude,latitude,longitude,addressHolder.getText().toString());
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onBackListener(){
        ImageButton backButtonSecondary = findViewById(R.id.firstConfirm_back_secondary);
        TextView backButton = findViewById(R.id.firstConfirm_back);

        backButton.setOnClickListener(v -> onBackManualPressed());
        backButtonSecondary.setOnClickListener(v -> onBackManualPressed());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onBackManualPressed(){
        navigationController.semiBasicIntent(this,SuggestsActivity.class,addressHolder.getText().toString());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////Super///////////////////////////////////////////////
    @Override
    public void onBackPressed () {
        navigationController.basicIntent(this,SuggestsActivity.class);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
