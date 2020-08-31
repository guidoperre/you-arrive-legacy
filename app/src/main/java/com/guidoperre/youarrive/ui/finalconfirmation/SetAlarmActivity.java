package com.guidoperre.youarrive.ui.finalconfirmation;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.AlarmController;
import com.guidoperre.youarrive.controllers.DialogController;
import com.guidoperre.youarrive.controllers.MapController;
import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.AlarmTone;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.GeoCode;
import com.guidoperre.youarrive.models.LastAlarmConfiguration;
import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.repositories.AlarmRepository;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;
import com.guidoperre.youarrive.repositories.RecoveryDataRepository;
import com.guidoperre.youarrive.services.MyService;
import com.guidoperre.youarrive.ui.awaitscreen.AwaitAlarmActivity;
import com.guidoperre.youarrive.ui.routes.SelectRouteActivity;
import com.guidoperre.youarrive.utilities.UiParameters;
import com.guidoperre.youarrive.utilities.Utils;

import java.util.List;

public class SetAlarmActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Circle circle;

    private MapController mapController;
    private NavigationController navigationController = new NavigationController();

    private FinalConfirmationViewModel model;

    private Alarm alarm = new Alarm();
    private AlarmRepository alarmRepository;

    private SeekBar seekBar;
    private TextView seekBarProgress;
    private TextView safeZoneTitle;
    private TextView firstOption;
    private TextView secondOption;
    private TextView thirdOption;
    private TextView fourthOption;
    private TextView alarmToneTitle;
    private ImageButton myLocationButton;

    private String address;
    private double latitude;
    private double longitude;
    private double myLatitude;
    private double myLongitude;

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.FinalConfirmationTheme);
        Utils.lockScreenOrientation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        initializeViewModel();
        onReady();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onReady(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.setAlarmMap);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        new UiParameters().setFullScreenFlags(this);
        alarmRepository = new AlarmRepository(getApplication());

        myLocationButton = findViewById(R.id.myLocationButton);
        seekBar = findViewById(R.id.seekBar);
        seekBarProgress = findViewById(R.id.volume_percent);
        safeZoneTitle = findViewById(R.id.safezone_title);
        firstOption = findViewById(R.id.safezone_first_option);
        secondOption = findViewById(R.id.safezone_second_option);
        thirdOption = findViewById(R.id.safezone_third_option);
        fourthOption = findViewById(R.id.safezone_fourth_option);

        getExtras();
        setListeners();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getExtras(){
        if (getIntent().getExtras() != null) {
            address = getIntent().getExtras().getString("address");
            myLatitude = getIntent().getExtras().getDouble("myLatitude");
            myLongitude = getIntent().getExtras().getDouble("myLongitude");
            latitude = getIntent().getExtras().getDouble("latitude");
            longitude = getIntent().getExtras().getDouble("longitude");
        } else
            Toast.makeText(this, "Hubo un error, intente de nuevo", Toast.LENGTH_SHORT).show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setLastConfiguration(List<LastAlarmConfiguration> lastAlarmConfigurations){
        if (lastAlarmConfigurations != null && lastAlarmConfigurations.size() != 0){
            alarm.setSafezone(lastAlarmConfigurations.get(0).getLastSafeZoneSelected());
            alarm.setVolume(lastAlarmConfigurations.get(0).getLastVolumeSelected());
        }else{
            alarm.setSafezone(4);
            alarm.setVolume(50);
        }
        setSafeZone();
        setSeekBar();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeViewModel(){
        model = new FinalConfirmationViewModel(getApplication());
        model.getAlarmTone().observe(this,alarmTones -> {
            if (alarmTones != null)
                if (alarmTones.size() != 0)
                    updateAlarmTone(alarmTones.get(0));
                else
                    new AlarmController().loadDefaultFile(getApplication(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            else
                new AlarmController().loadDefaultFile(getApplication(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapController = new MapController(mMap);

        setMapGestureConfiguration();
        setMapTheme(mMap);

        setLocationServices();
        setLastConfiguration(alarmRepository.getLastConfiguration());
        seekBarListener();
        setAlarmTone();
        safeZoneListener();
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

    /////////////////////////////////////OnlyAtStart////////////////////////////////////////////
    private void setLocationServices(){
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).flat(true).anchor(0.50f,0.50f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_edit_alarm_foreground)).zIndex(6f));
        mapController.alarmLocationButton(this, myLocationButton,new LatLng(latitude-0.001,longitude));
        mapController.moveCameraToSelectedPosition(new LatLng(latitude-0.001,longitude));
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
    private void setSafeZone(){
        setSafeZoneMetric();
        switch (alarm.getSafezone()) {
            case 1:
                firstOption.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.safezone_first_background));
                firstOption.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                addRangeCircle(new LatLng(latitude,longitude), Integer.parseInt(getApplicationContext().getResources().getString(R.string.safezone_first_option_metres)));
                break;
            case 2:
                secondOption.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                secondOption.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                addRangeCircle(new LatLng(latitude,longitude), Integer.parseInt(getApplicationContext().getResources().getString(R.string.safezone_second_option_metres)));
                break;
            case 3:
                thirdOption.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                thirdOption.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                addRangeCircle(new LatLng(latitude,longitude), Integer.parseInt(getApplicationContext().getResources().getString(R.string.safezone_third_option_metres)));
                break;
            case 4:
                fourthOption.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.safezone_fourth_background));
                fourthOption.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                addRangeCircle(new LatLng(latitude,longitude), Integer.parseInt(getApplicationContext().getResources().getString(R.string.safezone_fourth_option_metres)));
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    private void setSafeZoneMetric(){
        ConfigurationRepository configurationRepository = new ConfigurationRepository(getApplicationContext());
        Configuration configuration = configurationRepository.get().get(0);

        String safeZoneTitleText = getApplicationContext().getResources().getString(R.string.safezone_title) + " (" + getApplicationContext().getResources().getString(R.string.metres) + ")";
        String firstOptionText = getApplicationContext().getResources().getString(R.string.safezone_first_option_metres);
        String secondOptionText = getApplicationContext().getResources().getString(R.string.safezone_second_option_metres);
        String thirdOptionText = getApplicationContext().getResources().getString(R.string.safezone_third_option_metres);
        String fourthOptionText = getApplicationContext().getResources().getString(R.string.safezone_fourth_option_metres);

        if (configuration.getMetric().equals("imperial")){
            safeZoneTitleText = getApplicationContext().getResources().getString(R.string.safezone_title) + " (" + getApplicationContext().getResources().getString(R.string.miles) + ")";
            firstOptionText = getApplicationContext().getResources().getString(R.string.safezone_first_option_imperial);
            secondOptionText = getApplicationContext().getResources().getString(R.string.safezone_second_option_imperial);
            thirdOptionText = getApplicationContext().getResources().getString(R.string.safezone_third_option_imperial);
            fourthOptionText = getApplicationContext().getResources().getString(R.string.safezone_fourth_option_imperial);
        }

        safeZoneTitle.setText(safeZoneTitleText);
        firstOption.setText(firstOptionText);
        secondOption.setText(secondOptionText);
        thirdOption.setText(thirdOptionText);
        fourthOption.setText(fourthOptionText);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void safeZoneListener(){
        firstOption.setOnClickListener(v -> {
            resetBackgroundSafeZone();
            alarm.setSafezone(1);
            firstOption.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.safezone_first_background));
            firstOption.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
            addRangeCircle(new LatLng(latitude,longitude), Integer.parseInt(getApplicationContext().getResources().getString(R.string.safezone_first_option_metres)));
        });
        secondOption.setOnClickListener(v -> {
            resetBackgroundSafeZone();
            alarm.setSafezone(2);
            secondOption.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
            secondOption.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
            addRangeCircle(new LatLng(latitude,longitude), Integer.parseInt(getApplicationContext().getResources().getString(R.string.safezone_second_option_metres)));
        });
        thirdOption.setOnClickListener(v -> {
            resetBackgroundSafeZone();
            alarm.setSafezone(3);
            thirdOption.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
            thirdOption.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
            addRangeCircle(new LatLng(latitude,longitude), Integer.parseInt(getApplicationContext().getResources().getString(R.string.safezone_third_option_metres)));
        });
        fourthOption.setOnClickListener(v -> {
            resetBackgroundSafeZone();
            alarm.setSafezone(4);
            fourthOption.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.safezone_fourth_background));
            fourthOption.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
            addRangeCircle(new LatLng(latitude,longitude), Integer.parseInt(getApplicationContext().getResources().getString(R.string.safezone_fourth_option_metres)));
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void resetBackgroundSafeZone() {
        switch (alarm.getSafezone()) {
            case 1:
                firstOption.setBackgroundResource(0);
                firstOption.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                break;
            case 2:
                secondOption.setBackgroundResource(0);
                secondOption.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                break;
            case 3:
                thirdOption.setBackgroundResource(0);
                thirdOption.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                break;
            case 4:
                fourthOption.setBackgroundResource(0);
                fourthOption.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                break;

        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setSeekBar(){
        String progressText = alarm.getVolume() +"%";

        seekBar.setProgress(alarm.getVolume());
        seekBarProgress.setText(progressText);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void seekBarListener(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                String progressText = progress +"%";
                seekBarProgress.setText(progressText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                alarm.setVolume(seekBar.getProgress());
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setAlarmTone(){
        ImageView selectAlarmTone = findViewById(R.id.select_alarmtone_file);
        alarmToneTitle = findViewById(R.id.alarmtone_tone);

        selectAlarmTone.setOnClickListener(v -> {
            FragmentManager transition = getSupportFragmentManager();
            AlarmToneDialog dialog = AlarmToneDialog.newInstance();
            dialog.show(transition,"alarmToneDialog");
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void updateAlarmTone(AlarmTone alarmTone){
        alarm.setUri(alarmTone.getUri());
        alarmToneTitle.setText(alarmTone.getTitle());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////Circle////////////////////////////////////////////////
    private void addRangeCircle(LatLng target, int radius){
        if (circle != null){
            circle.remove();
        }
        circle = mMap.addCircle(new CircleOptions()
                .center(target)
                .fillColor(ContextCompat.getColor(getApplicationContext(),R.color.purple))
                .strokeColor(ContextCompat.getColor(getApplicationContext(),R.color.purple))
                .strokeWidth(3)
                .radius(radius)
        );
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setListeners(){
        TextView confirmAlarm = findViewById(R.id.confirm_alarm);

        confirmAlarm.setOnClickListener(v -> {
            stopService();
                
            alarm.setLocation(new GeoCode(latitude,longitude));
            alarmRepository.deleteAll();
            alarmRepository.insert(alarm);
            alarmRepository.insertConfiguration(new LastAlarmConfiguration(alarm.getVolume(),alarm.getSafezone()));
            new RecoveryDataRepository(getApplication()).insert(new RecoveryData("manual","",address,myLatitude,myLongitude,latitude,longitude));

            navigationController.fullIntent(this, AwaitAlarmActivity.class,myLatitude,myLongitude,latitude,longitude,address,null,"manual");
        });
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

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        new DialogController().openCancelRouteDialog(this,this,SelectRouteActivity.class,new Intent(this,MyService.class),myLatitude,myLongitude,latitude,longitude,address);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        model.getAlarmTone().removeObservers(this);
        super.onDestroy();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
