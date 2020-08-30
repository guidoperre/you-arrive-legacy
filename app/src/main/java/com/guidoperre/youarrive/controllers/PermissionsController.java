package com.guidoperre.youarrive.controllers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import static android.content.Context.POWER_SERVICE;

public class PermissionsController {

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int INITIAL_REQUEST = 0;

    public PermissionsController(){ }

    ////////////////////////////////////Init Permissions////////////////////////////////////////
    public void checkInitPermission(Activity activity){
        if (!canAccessLocation(activity) || !canAccessCoarse(activity)) {
            activity.requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////Battery Permissions//////////////////////////////////////
    @SuppressLint("BatteryLife")
    public void checkWhiteListPermission(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////Gps Permissions//////////////////////////////////////
    public void checkIfGPSIsActivated(Context context) {
        try {
            int gpsSignal = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsSignal == 0) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////Location Permissions////////////////////////////////////
    boolean canAccessLocation(Activity activity) {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION,activity));
    }

    boolean canAccessCoarse(Activity activity) {
        return(hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION,activity));
    }

    private boolean hasPermission(String perm, Activity activity) {
        return (PackageManager.PERMISSION_GRANTED == activity.checkSelfPermission(perm));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
}
