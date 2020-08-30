package com.guidoperre.youarrive.ui.main;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.MapStyleOptions;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.DialogController;
import com.guidoperre.youarrive.controllers.MapController;
import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.controllers.PermissionsController;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;
import com.guidoperre.youarrive.ui.suggests.SuggestsActivity;
import com.guidoperre.youarrive.utilities.UiParameters;
import com.guidoperre.youarrive.utilities.Utils;

import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    /////////////////////////////////////Variables//////////////////////////////////////////////
    private GoogleMap mMap;
    public static boolean flagAlert;
    ////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////OnCreate/////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        Utils.lockScreenOrientation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        onReady();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////First´s things first//////////////////////////////////////////
    private void onReady(){
        //Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null)
            mapFragment.getMapAsync(this);
        //Permissions
        setPermissions();
        //Views
        new UiParameters().setFullScreenFlags(this);
        //Start Dialog
        openStartDialog();
        //OnClickListener
        setListeners();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////Manipulate map////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setMapGestureConfiguration();
        setMapTheme(googleMap);
        setLocationServices();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setMapGestureConfiguration(){
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////Location Services/////////////////////////////////////////
    private void setLocationServices(){
        ImageButton myLocationButton = findViewById(R.id.myLocationButton);
        new MapController(mMap).moveCameraToMyPosition(this, myLocationButton);
        new MapController(mMap).myLocationButton(this, myLocationButton);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////Map Theme//////////////////////////////////////////////
    private void setMapTheme(GoogleMap googleMap){
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////Open Dialog///////////////////////////////////////////
    private void openStartDialog(){
        if (!flagAlert){
            new DialogController().openStartDialog(this);
            flagAlert=true;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////Close Dialog///////////////////////////////////////////
    private void openExitDialog(){
        new DialogController().openExitDialog(this,MapsActivity.this);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setListeners(){
        NavigationController navigationController = new NavigationController();

        TextView searchBarText = findViewById(R.id.textbuttom_search);
        ImageButton enterSearch = findViewById(R.id.search_button);
        ImageButton settings = findViewById(R.id.optionButton);

        searchBarText.setOnClickListener(view -> navigationController.basicIntent(this,SuggestsActivity.class));
        enterSearch.setOnClickListener(view -> navigationController.basicIntent(this,SuggestsActivity.class));
        settings.setOnClickListener(view -> new DialogController().openMapSettingsDialog(this, getApplication()));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////Permissions////////////////////////////////////////////
    private void setPermissions(){
        new PermissionsController().checkInitPermission(this);
        new PermissionsController().checkWhiteListPermission(this);
        new PermissionsController().checkIfGPSIsActivated(this);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////Super////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        openExitDialog();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
}

