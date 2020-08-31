package com.guidoperre.youarrive.ui.finalconfirmation;

import android.content.Context;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.AlarmController;
import com.guidoperre.youarrive.controllers.ConfigurationController;
import com.guidoperre.youarrive.controllers.MapController;
import com.guidoperre.youarrive.controllers.RoutesController;
import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.AlarmTone;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.GeoCode;
import com.guidoperre.youarrive.models.LastAlarmConfiguration;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.repositories.AlarmRepository;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddAlarmFragment extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private boolean backFlag = false;

    private View mView;
    private FinalConfirmationViewModel model;

    private GoogleMap map;
    private Circle circle;

    private Alarm alarm = new Alarm();
    private Alarm recoverAlarm = new Alarm();
    private AlarmRepository alarmRepository;
    private boolean isNew = true;

    private RoutesController routesController = new RoutesController();

    private Configuration configuration;
    private ConfigurationController configurationController;

    private ArrayList<RoutePath> routePath;
    private List<String> routeShape;
    private String alarmTitle;

    private Marker alarmMarker;
    private Polyline routePolyline;

    private SeekBar seekBar;
    private TextView seekBarProgress;

    private TextView safeZoneTitle;
    private TextView firstOption;
    private TextView secondOption;
    private TextView thirdOption;
    private TextView fourthOption;

    private TextView alarmToneTitle;

    public AddAlarmFragment() {}

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_alarm, container, false);
        this.mView = view;

        seekBar = mView.findViewById(R.id.seekBar);
        seekBarProgress = mView.findViewById(R.id.volume_percent);
        safeZoneTitle = mView.findViewById(R.id.safezone_title);
        firstOption = mView.findViewById(R.id.safezone_first_option);
        secondOption = mView.findViewById(R.id.safezone_second_option);
        thirdOption = mView.findViewById(R.id.safezone_third_option);
        fourthOption = mView.findViewById(R.id.safezone_fourth_option);

        if (getActivity() != null){
            ConfigurationRepository configurationRepository = new ConfigurationRepository(getActivity().getApplicationContext());
            configuration = configurationRepository.get().get(0);
        }
        configurationController = new ConfigurationController();

        checkIfIsNew(alarmTitle);
        seekBarListener();
        safeZoneListener();
        setAlarmTone();
        setStartMarker();
        setListeners();

        return view;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewModel();
        getExtras();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();
        locationListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getExtras(){
        if (getActivity() != null){
            alarmRepository = new AlarmRepository(getActivity().getApplication());
            map = ((SetRouteAlarmsActivity) getActivity()).mMap;
        }

        routePath =  routesController.parseRoutePathJson(Objects.requireNonNull(getArguments()).getString("routePath"));
        routeShape = routesController.parseRouteShapeJson(Objects.requireNonNull(getArguments()).getString("routeShape"));
        alarmTitle = Objects.requireNonNull(getArguments().getString("title"));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeViewModel(){
        model = new FinalConfirmationViewModel(Objects.requireNonNull(getActivity()).getApplication());
        model.getAlarmTone().observe(this,alarmTones -> {
            if (alarmTones != null)
                if (alarmTones.size() != 0)
                    updateAlarmTone(alarmTones.get(0));
                else
                    new AlarmController().loadDefaultFile(getActivity().getApplication(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            else
                new AlarmController().loadDefaultFile(getActivity().getApplication(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void checkIfIsNew(String title){
        if (title.equals("new")){
            String type = "";
            if (configuration != null)
                type = configurationController.getType(configuration.getTransportType());
            if (type.equals("car") || type.equals("pedestrian"))
                alarm.setLocation(new GeoCode(routePath.get(routePath.size()-1).getEndLatitude(),routePath.get(routePath.size()-1).getEndLongitude()));
            else
                alarm.setLocation(new GeoCode(routePath.get(routePath.size()-1).getStartLatitude(),routePath.get(routePath.size()-1).getStartLongitude()));

            isNew = true;
            alarm.setTitle(setAlarmTitle());
            setLastConfiguration(alarmRepository.getLastConfiguration());
        }else{
            isNew = false;
            Alarm editAlarm = alarmRepository.getAlarm(title).get(0);
            alarm.setTitle(editAlarm.getTitle());
            alarm.setLocation(editAlarm.getLocation());
            alarm.setUri(editAlarm.getUri());
            alarm.setVolume(editAlarm.getVolume());
            alarm.setSafezone(editAlarm.getSafezone());
            recoverAlarm.setTitle(editAlarm.getTitle());
            recoverAlarm.setLocation(editAlarm.getLocation());
            recoverAlarm.setUri(editAlarm.getUri());
            recoverAlarm.setVolume(editAlarm.getVolume());
            recoverAlarm.setSafezone(editAlarm.getSafezone());
        }
        setSafeZone();
        setSeekBar();
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
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private String setAlarmTitle(){
        String alarmTitle = "";
        if (getActivity() != null){
            AlarmRepository alarmRepository = new AlarmRepository(getActivity().getApplication());
            List<Alarm> alarms = alarmRepository.getAllAlarms();

            if(alarms != null && alarms.size() != 0)
                alarmTitle = "alarm_"+(alarms.size()+1);
            else
                alarmTitle = "alarm_1";
        }
        return alarmTitle;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setSafeZone(){
        if (getActivity() != null)
            setSafeZoneMetric(getActivity().getApplicationContext());
            switch (alarm.getSafezone()){
                case 1:
                    firstOption.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.safezone_first_background));
                    firstOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorPrimary));
                    addRangeCircle(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()), Integer.parseInt(getActivity().getApplicationContext().getResources().getString(R.string.safezone_first_option_metres)));
                    break;
                case 2:
                    secondOption.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent));
                    secondOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorPrimary));
                    addRangeCircle(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()), Integer.parseInt(getActivity().getApplicationContext().getResources().getString(R.string.safezone_second_option_metres)));
                    break;
                case 3:
                    thirdOption.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent));
                    thirdOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorPrimary));
                    addRangeCircle(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()), Integer.parseInt(getActivity().getApplicationContext().getResources().getString(R.string.safezone_third_option_metres)));
                    break;
                case 4:
                    fourthOption.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.safezone_fourth_background));
                    fourthOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorPrimary));
                    addRangeCircle(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()), Integer.parseInt(getActivity().getApplicationContext().getResources().getString(R.string.safezone_fourth_option_metres)));
                    break;
            }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setSafeZoneMetric(Context context){
        ConfigurationRepository configurationRepository = new ConfigurationRepository(context);
        Configuration configuration = configurationRepository.get().get(0);

        String safeZoneTitleText = context.getResources().getString(R.string.safezone_title) + " (" + context.getResources().getString(R.string.metres) + ")";
        String firstOptionText = context.getResources().getString(R.string.safezone_first_option_metres);
        String secondOptionText = context.getResources().getString(R.string.safezone_second_option_metres);
        String thirdOptionText = context.getResources().getString(R.string.safezone_third_option_metres);
        String fourthOptionText = context.getResources().getString(R.string.safezone_fourth_option_metres);

        if (configuration.getMetric().equals("imperial")){
            safeZoneTitleText = context.getResources().getString(R.string.safezone_title) + " (" + context.getResources().getString(R.string.miles) + ")";
            firstOptionText = context.getResources().getString(R.string.safezone_first_option_imperial);
            secondOptionText = context.getResources().getString(R.string.safezone_second_option_imperial);
            thirdOptionText = context.getResources().getString(R.string.safezone_third_option_imperial);
            fourthOptionText = context.getResources().getString(R.string.safezone_fourth_option_imperial);
        }

        safeZoneTitle.setText(safeZoneTitleText);
        firstOption.setText(firstOptionText);
        secondOption.setText(secondOptionText);
        thirdOption.setText(thirdOptionText);
        fourthOption.setText(fourthOptionText);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void safeZoneListener(){
        firstOption.setOnClickListener(v -> {
            if (getActivity() != null){
                resetBackgroundSafeZone();
                alarm.setSafezone(1);
                firstOption.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.safezone_first_background));
                firstOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorPrimary));
                addRangeCircle(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()), Integer.parseInt(getActivity().getApplicationContext().getResources().getString(R.string.safezone_first_option_metres)));
            }
        });
        secondOption.setOnClickListener(v -> {
            if (getActivity() != null){
                resetBackgroundSafeZone();
                alarm.setSafezone(2);
                secondOption.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent));
                secondOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorPrimary));
                addRangeCircle(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()), Integer.parseInt(getActivity().getApplicationContext().getResources().getString(R.string.safezone_second_option_metres)));
            }
        });
        thirdOption.setOnClickListener(v -> {
            if (getActivity() != null){
                resetBackgroundSafeZone();
                alarm.setSafezone(3);
                thirdOption.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent));
                thirdOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorPrimary));
                addRangeCircle(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()), Integer.parseInt(getActivity().getApplicationContext().getResources().getString(R.string.safezone_third_option_metres)));
            }
        });
        fourthOption.setOnClickListener(v -> {
            if (getActivity() != null){
                resetBackgroundSafeZone();
                alarm.setSafezone(4);
                fourthOption.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.safezone_fourth_background));
                fourthOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorPrimary));
                addRangeCircle(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()), Integer.parseInt(getActivity().getApplicationContext().getResources().getString(R.string.safezone_fourth_option_metres)));
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void resetBackgroundSafeZone(){
        if (getActivity() != null)
            switch (alarm.getSafezone()){
                case 1:
                    firstOption.setBackgroundResource(0);
                    firstOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent));
                    break;
                case 2:
                    secondOption.setBackgroundResource(0);
                    secondOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent));
                    break;
                case 3:
                    thirdOption.setBackgroundResource(0);
                    thirdOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent));
                    break;
                case 4:
                    fourthOption.setBackgroundResource(0);
                    fourthOption.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(),R.color.colorAccent));
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
        ImageView selectAlarmTone = mView.findViewById(R.id.select_alarmtone_file);
        alarmToneTitle = mView.findViewById(R.id.alarmtone_tone);

        selectAlarmTone.setOnClickListener(v -> {
            if (getActivity() != null){
                FragmentManager transition = getActivity().getSupportFragmentManager();
                AlarmToneDialog dialog = AlarmToneDialog.newInstance();
                dialog.show(transition,"alarmToneDialog");
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void updateAlarmTone(AlarmTone alarmTone){
        alarm.setUri(alarmTone.getUri());
        alarmToneTitle.setText(alarmTone.getTitle());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setStartMarker(){
        if (getActivity() != null){
            if (map != null){
                alarmMarker = map.addMarker(new MarkerOptions().position(new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude())).flat(true).anchor(0.50f,0.50f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_edit_alarm_foreground)).zIndex(6f).title(alarm.getTitle()));
                drawInvisiblePolyline();
                map.setOnMapClickListener(this);
                map.setOnMarkerClickListener(this);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////Circle////////////////////////////////////////////////
    private void addRangeCircle(LatLng target, int radius){
        if (getContext() != null && map != null){
            if (circle != null){
                circle.remove();
            }
            circle = map.addCircle(new CircleOptions()
                    .center(target)
                    .fillColor(ContextCompat.getColor(getContext(),R.color.purple))
                    .strokeColor(ContextCompat.getColor(getContext(),R.color.purple))
                    .strokeWidth(3)
                    .radius(radius)
            );
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void drawInvisiblePolyline(){
        MapController mapController = new MapController();
        ArrayList<LatLng> shape = mapController.convertToLatLng(routeShape);
        ArrayList<LatLng> pointsList = mapController.expandedShape(shape);
        routePolyline = map.addPolyline(new PolylineOptions().addAll(pointsList).width(15).color(Objects.requireNonNull(getActivity()).getColor(R.color.transparent)).clickable(false));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setListeners(){
        TextView saveAlarm = mView.findViewById(R.id.save_alarm);
        TextView deleteAlarm = mView.findViewById(R.id.delete_alarm);

        saveAlarm.setOnClickListener(v -> {
            backFlag = true;
            alarmMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_add_alarm_foreground));

            if (isNew)
                alarmRepository.insert(alarm);
            else
                alarmRepository.update(alarm.getTitle(),alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude(),alarm.getUri(),alarm.getVolume(),alarm.getSafezone());

            alarmRepository.insertConfiguration(new LastAlarmConfiguration(alarm.getVolume(),alarm.getSafezone()));

            if (getActivity() != null)
                ((SetRouteAlarmsActivity) getActivity()).getBackFromAddAlarm(this);
        });

        deleteAlarm.setOnClickListener(v -> {
            backFlag = true;
            alarmRepository.deleteAlarm(alarm.getTitle());
            alarmMarker.remove();
            if (getActivity() != null)
                ((SetRouteAlarmsActivity) getActivity()).getBackFromAddAlarm(this);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void locationListener(){
        ImageButton myLocationButton = mView.findViewById(R.id.myLocationButton);
        myLocationButton.setVisibility(View.INVISIBLE);
        if (getActivity() != null)
            ((SetRouteAlarmsActivity) getActivity()).setLocationServicesFragment(myLocationButton,new LatLng(alarm.getLocation().getLatitude()-0.001,alarm.getLocation().getLongitude()));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        if (!backFlag)
            if (!isNew)
                alarmMarker.setPosition(new LatLng(recoverAlarm.getLocation().getLatitude(),recoverAlarm.getLocation().getLongitude()));
            else
                alarmMarker.remove();
        if (circle != null)
            circle.remove();
        model.getAlarmTone().removeObservers(this);
        super.onDestroy();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapClick(LatLng latLng) {
        if (PolyUtil.isLocationOnPath(latLng, routePolyline.getPoints(), true, 30)) {
            alarmMarker.setPosition(latLng);
            addRangeCircle(latLng,routesController.getSafeZoneRange(alarm.getSafezone()));
            alarm.setLocation(new GeoCode(latLng.latitude, latLng.longitude));
            locationListener();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onMarkerClick(Marker marker) {
        alarmMarker.setPosition(marker.getPosition());
        addRangeCircle(marker.getPosition(),routesController.getSafeZoneRange(alarm.getSafezone()));
        alarm.setLocation(new GeoCode(marker.getPosition().latitude,marker.getPosition().longitude));
        locationListener();
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////



}
