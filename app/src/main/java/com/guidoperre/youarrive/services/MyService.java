package com.guidoperre.youarrive.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.android.gms.maps.model.LatLng;
import com.guidoperre.youarrive.controllers.AlarmController;
import com.guidoperre.youarrive.controllers.ConfigurationController;
import com.guidoperre.youarrive.controllers.MapController;
import com.guidoperre.youarrive.controllers.RoutesController;
import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.AlarmLog;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.models.RemainingTime;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.models.RouteTransportLine;
import com.guidoperre.youarrive.repositories.AlarmLogRepository;
import com.guidoperre.youarrive.repositories.AlarmRepository;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;
import com.guidoperre.youarrive.repositories.RecoveryDataRepository;
import com.guidoperre.youarrive.repositories.RemainingTimeRepository;
import com.guidoperre.youarrive.repositories.RoutesRepository;
import com.guidoperre.youarrive.ui.awaitscreen.StopAlarmActivity;

public class MyService extends LifecycleService {

    public static boolean serviceActive;

    private AlarmController alarmController = new AlarmController();
    private MapController mapController = new MapController();
    private RoutesController routesController = new RoutesController();

    private AlarmLogRepository alarmLogRepository;
    private AlarmRepository alarmRepository;
    private RoutesRepository routesRepository = new RoutesRepository();
    private ServiceViewModel model;

    private Configuration configuration;
    private ConfigurationController configurationController;

    private Handler handler = new Handler();
    private Runnable runnable;
    private CountDownTimer countDownTimer;

    private WifiManager.WifiLock wifiLock;
    private PowerManager.WakeLock wakelock;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private LatLng lastKnowLocation;
    private Route actualRoute = new Route();
    private List<Alarm> allAlarms;
    private Alarm actualAlarm;
    private ArrayList<RouteTransportLine> remainingTransportLines = new ArrayList<>();
    private int actualPosition = 0;

    private int time = 0;
    private boolean started = false;

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return super.onBind(intent);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onReady();
        powerManager();
        requestPositionFromGoogle();
        runnable = () -> {
            requestPositionFromGps();
            if (started)
                start();
        };
        start();
        return super.onStartCommand(intent, flags, startId);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void start() {
        started = true;
        handler.postDelayed(runnable, 15000);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onReady() {
        ConfigurationRepository configurationRepository = new ConfigurationRepository(getApplicationContext());
        configuration = configurationRepository.get().get(0);
        configurationController = new ConfigurationController();
        alarmLogRepository = new AlarmLogRepository(getApplication());
        alarmRepository = new AlarmRepository(getApplication());
        getAlarms();
        locationRequestParams();
        initializeViewModel();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeViewModel(){
        model = new ServiceViewModel();
        model.getRoutes().observe(this,this::refreshCountDown);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getAlarms(){
        RecoveryDataRepository recoveryDataRepository = new RecoveryDataRepository(getApplication());
        RecoveryData recoveryData = recoveryDataRepository.getRecoveryData().get(0);
        AlarmRepository alarmRepository = new AlarmRepository(getApplication());
        List<Alarm> rawAlarms = alarmRepository.getAllAlarms();

        actualRoute = routesController.parseRouteJson(Objects.requireNonNull(recoveryData.getRouteJSON()));

        if (rawAlarms != null && rawAlarms.size() > 0){
            if (rawAlarms.size() > 1){
                allAlarms = alarmController.sortAlarms(getApplication(),rawAlarms,actualRoute);
                actualAlarm = allAlarms.get(actualPosition);
            } else{
                allAlarms = rawAlarms;
                actualAlarm = rawAlarms.get(actualPosition);
            }
            setSafeZone();
            if (actualRoute != null){
                int remainingTime = alarmController.getRemainingTime(actualRoute, actualAlarm.getLocation().getLatitude(), actualAlarm.getLocation().getLongitude(), getApplication());
                if (remainingTime != 0)
                    setCountDown(remainingTime);
            }
        } else
            somethingGoWrong();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setSafeZone(){
        int DEFAULT_SAFEZONE_AUX = 200;
        switch (actualAlarm.getSafezone()){
            case 1:
                actualAlarm.setSafezone(DEFAULT_SAFEZONE_AUX);
                break;
            case 2:
                actualAlarm.setSafezone(100 + DEFAULT_SAFEZONE_AUX);
                break;
            case 3:
                actualAlarm.setSafezone(250 + DEFAULT_SAFEZONE_AUX);
                break;
            case 4:
                actualAlarm.setSafezone(500 + DEFAULT_SAFEZONE_AUX);
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void refreshCountDown(List<Route> routes){
        String type = configurationController.getType(configuration.getTransportType());
        if (routes != null && routes.size() > 0){
            if (actualRoute != null && !type.equals("car") && !type.equals("pedestrian")){
                    Route sameRoute = routesController.checkForSameRoute(routes,remainingTransportLines);
                    if (sameRoute != null && sameRoute.getSummary() != null && sameRoute.getSummary().getTravelTime() != null && sameRoute.getSummary().getTravelTime() != 0)
                        setCountDown(sameRoute.getSummary().getTravelTime() - Math.abs(routesController.betweenStopsDifference(lastKnowLocation,sameRoute,remainingTransportLines)));
            } else if (routes.get(0) != null && routes.get(0).getSummary() != null && routes.get(0).getSummary().getTravelTime() != null && routes.get(0).getSummary().getTravelTime() != 0)
                setCountDown(routes.get(0).getSummary().getTravelTime());
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setCountDown(int remainingTime){
        RemainingTimeRepository remainingTimeRepository = new RemainingTimeRepository(getApplication());
        remainingTimeRepository.insert(new RemainingTime(remainingTime /60));

        int realRemainingTime = (int) (remainingTime*0.85);

        if (countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(realRemainingTime*1000, 1000){
            public void onTick(long millisUntilFinished){
            }
            public void onFinish(){
                youArrive();
            }
        }.start();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void locationRequestParams(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("InvalidWakeLockTag")
    private void powerManager(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        assert powerManager != null;
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyService");
        wifiLock = ((WifiManager) Objects.requireNonNull(getApplicationContext().getSystemService(Context.WIFI_SERVICE))).createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock");
        if (!wifiLock.isHeld()){
            wifiLock.acquire();
        }
        if (!wakelock.isHeld()){
            wakelock.acquire(120*60*1000L /*2 hours*/);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void requestPositionFromGoogle(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "No se concedieron los permisos", Toast.LENGTH_SHORT).show();
        else
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void requestPositionFromGps(){
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No se concedieron los permisos", Toast.LENGTH_SHORT).show();
        }else{
            if (locManager != null){
                if (time % 16 == 0){
                    locManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                }else{
                    locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                }
                time++;
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            checkLocation(location.getLatitude(), location.getLongitude());
            insertAlarmLog("GPS",location.getLatitude(), location.getLongitude());
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult){
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location mLastLocation = locationList.get(locationList.size() - 1);
                checkLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                insertAlarmLog("Google",mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void insertAlarmLog(String font, double latitude, double longitude){
        AlarmLog alarmLog = new AlarmLog();
        Date date = new Date();
        String time = date.toString();
        double leftTravelDistance = mapController.convertDifferenceToMeters(new LatLng(actualAlarm.getLocation().getLatitude(),actualAlarm.getLocation().getLongitude()),new LatLng(latitude,longitude));

        alarmLog.setDate(time);
        alarmLog.setFont(font);
        alarmLog.setLeftTravelDistance(leftTravelDistance);
        alarmLog.setLatitude(latitude);
        alarmLog.setLongitude(longitude);

        alarmLogRepository.insert(alarmLog);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void checkLocation(double myLatitude, double myLongitude){
        double difference = mapController.convertDifferenceToMeters(new LatLng(actualAlarm.getLocation().getLatitude(),actualAlarm.getLocation().getLongitude()),new LatLng(myLatitude,myLongitude));

        if (Math.abs(difference) < actualAlarm.getSafezone()){
            youArrive();
            if (checkForNewAlarm())
                makeRouteCall(myLatitude,myLongitude,actualAlarm.getLocation().getLatitude(),actualAlarm.getLocation().getLongitude());
        } else
            makeRouteCall(myLatitude,myLongitude,actualAlarm.getLocation().getLatitude(),actualAlarm.getLocation().getLongitude());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void makeRouteCall(double myLatitude, double myLongitude, double latitude, double longitude){
        String type = configurationController.getType(configuration.getTransportType());
        String prefer = configurationController.getPrefer(configuration.getPrefTransports());

        if (actualRoute != null && !type.equals("car") && !type.equals("pedestrian")){
            lastKnowLocation = new LatLng(myLatitude,myLongitude);
            LatLng nextStopLocation = routesController.getNextStop(latitude, longitude, getApplication());
            remainingTransportLines = routesController.getRemainingTransportsLines(actualRoute,lastKnowLocation, nextStopLocation);
            LatLng stopLocation = routesController.getBestStopLocation(lastKnowLocation,actualRoute,remainingTransportLines);
            routesRepository.get(stopLocation.latitude+","+stopLocation.longitude,nextStopLocation.latitude+","+ nextStopLocation.longitude,"fastest" + ";" + type + ";" +"traffic:enabled",prefer,"metric","typeName,stops","sh","true","now","5");
        }else
            routesRepository.get(myLatitude+","+myLongitude,latitude+","+ longitude,"fastest" + ";" + type + ";" +"traffic:enabled",prefer,"metric","typeName,stops","sh","true","now","5");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void youArrive(){
        Intent intent = new Intent(MyService.this, StopAlarmActivity.class);
        intent.putExtra("alarm", actualPosition);
        intent.putExtra("alarmSize",allAlarms.size());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        if (!checkForNewAlarm())
            stop();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void somethingGoWrong(){
        Intent intent = new Intent(MyService.this, StopAlarmActivity.class);
        intent.putExtra("alarm", 0);
        intent.putExtra("alarmSize",1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stop();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private boolean checkForNewAlarm(){
        boolean isNewAlarm = false;
        actualPosition++;
        if (allAlarms.size() > actualPosition){
            setCountDown(alarmController.getRemainingTime(actualRoute, actualAlarm.getLocation().getLatitude(), actualAlarm.getLocation().getLongitude(), getApplication()));
            setSafeZone();
            alarmRepository.deleteAlarm(actualAlarm.getTitle());
            actualAlarm = allAlarms.get(actualPosition);
            isNewAlarm = true;
        }

        return isNewAlarm;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (handler != null){
            handler.removeCallbacks(runnable);
        }
        mFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
        if(wakelock.isHeld()){
            wakelock.release();
        }
        if(wifiLock.isHeld()){
            wifiLock.release();
        }
        if (countDownTimer != null)
            countDownTimer.cancel();
        serviceActive = false;
        model.cancelAsyncTasks();
        stopSelf();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy() {
        if(!serviceActive){
            started = false;
            stop();
            super.onDestroy();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}