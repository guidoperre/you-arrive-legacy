package com.guidoperre.youarrive.controllers;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.AlarmTone;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.models.RouteManeuver;
import com.guidoperre.youarrive.models.RouteStop;
import com.guidoperre.youarrive.models.RouteTransportLine;
import com.guidoperre.youarrive.repositories.AlarmRepository;
import com.guidoperre.youarrive.repositories.AlarmToneRepository;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;
import com.guidoperre.youarrive.repositories.RecoveryDataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlarmController {

    private static int current_volume_level = 0;
    private int max_volume_level = 0;

    private static MediaPlayer alarmTone;

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void alarmStart(Application application, Object audio, int alarmPosition){
        List<Alarm> alarm = getAlarms(application);
        AudioManager mAudioManager = (AudioManager) audio;

        if (mAudioManager != null) {
            current_volume_level = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            max_volume_level = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        }
        if (mAudioManager != null)
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max_volume_level, 0);

        alarmTone = MediaPlayer.create(application.getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        if (alarm.size() != 0){
            Uri uri = Uri.parse(alarm.get(alarmPosition).getUri());
            if (uri != null)
                alarmTone = MediaPlayer.create(application.getApplicationContext(),uri);
            if (mAudioManager != null)
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarm.get(alarmPosition).getVolume()*max_volume_level/100, 0);
        }

        alarmTone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        alarmTone.setLooping(true);

        if (!alarmTone.isPlaying())
            alarmTone.start();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void alarmStop(Object audio){
        AudioManager mAudioManager = (AudioManager) audio;

        if (mAudioManager != null)
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current_volume_level, 0);

        alarmTone.stop();
        alarmTone.prepareAsync();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<AlarmTone> createItems(){
        ArrayList<AlarmTone> alarmToneItems = new ArrayList<>();

        alarmToneItems.add(new AlarmTone("default"));
        alarmToneItems.add(new AlarmTone("custom"));

        return alarmToneItems;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void loadCustomFile(Application application, Uri file){
        AlarmToneRepository repository = new AlarmToneRepository(application);
        AlarmTone alarmTone = new AlarmTone("custom");

        alarmTone.setUri(file.toString());
        alarmTone.setTitle(getMedia(application.getApplicationContext(),file));

        repository.deleteAll();
        repository.insert(alarmTone);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void loadDefaultFile(Application application, Uri file){
        AlarmToneRepository repository = new AlarmToneRepository(application);
        AlarmTone alarmTone = new AlarmTone("default");

        alarmTone.setUri(file.toString());
        alarmTone.setTitle("default_alarmtone.mp3");

        repository.deleteAll();
        repository.insert(alarmTone);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private String getMedia(Context context, Uri uri){
        String alarmToneString = "non-titled.mp3";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null){
            if (!cursor.moveToFirst()) {
                alarmToneString = "non-titled.mp3";
            } else {
                do {
                    alarmToneString = cursor.getString(2);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return alarmToneString;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private List<Alarm> getAlarms(Application application){
        AlarmRepository alarmRepository = new AlarmRepository(application);
        List<Alarm> alarms = alarmRepository.getAllAlarms();
        ArrayList<Alarm> sortedAlarms;

        if (alarms.size() > 1)
            sortedAlarms = new ArrayList<>(sortAlarms(application, alarms, null));
        else
            sortedAlarms = new ArrayList<>(alarms);

        return sortedAlarms;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public List<Alarm> sortAlarms(Application application,List<Alarm> alarms,Route route){
        if (route == null){
            RecoveryDataRepository recoveryDataRepository = new RecoveryDataRepository(application);
            RecoveryData recoveryData = recoveryDataRepository.getRecoveryData().get(0);

            route = new RoutesController().parseRouteJson(Objects.requireNonNull(recoveryData.getRouteJSON()));
        }

        MapController mapController = new MapController();

        ArrayList<Alarm> sortedAlarms = new ArrayList<>();
        ArrayList<LatLng> shape = mapController.convertToLatLng(route.getShape());
        ArrayList<LatLng> pointsList = mapController.expandedShape(shape);
        int alarmListSize = alarms.size();

        for (LatLng point:pointsList){
            for (Alarm alarm:alarms){
                if (Math.abs(mapController.convertDifferenceToMeters(point,new LatLng(alarm.getLocation().getLatitude(),alarm.getLocation().getLongitude()))) < 50){
                    alarms.remove(alarm);
                    sortedAlarms.add(alarm);
                    break;
                }
            }
            if (sortedAlarms.size() == alarmListSize)
                break;
        }

        return sortedAlarms;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public int getRemainingTime(Route route, double latitude, double longitude, Application application){
        int remainingTime = 0;
        boolean stopFor = false;

        ConfigurationRepository configurationRepository = new ConfigurationRepository(application);
        Configuration configuration = configurationRepository.get().get(0);
        ConfigurationController configurationController = new ConfigurationController();
        String type = configurationController.getType(configuration.getTransportType());

        if (type.equals("car") || type.equals("pedestrian"))
            remainingTime = route.getSummary().getTravelTime();
        else{
            LatLng nextStopLocation = new RoutesController().getNextStop(latitude, longitude, application);
            for (RouteManeuver maneuver:route.getLeg().get(0).getManeuver()){
                if (maneuver.getType().equals("PrivateTransportManeuverType"))
                    remainingTime += maneuver.getTravelTime();
                else{
                    for (RouteTransportLine transportLine:route.getPublicTransportLine()){
                        for (RouteStop stop: transportLine.getStops()){
                            if (stop.getStopName().equals(maneuver.getStopName()))
                                for (RouteStop usefulStop: transportLine.getStops()){
                                    if (new MapController().convertDifferenceToMeters(new LatLng(usefulStop.getPosition().getLatitude(),usefulStop.getPosition().getLongitude()),nextStopLocation) < 50){
                                        stopFor = true;
                                        break;
                                    }
                                    remainingTime+= usefulStop.getTravelTime();
                                }
                            if (stopFor)
                                break;
                        }
                        if (stopFor)
                            break;
                    }
                }
                if (stopFor)
                    break;
            }
        }

        return remainingTime;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
