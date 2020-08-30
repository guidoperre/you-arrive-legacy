package com.guidoperre.youarrive.controllers;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.models.AutoSuggest;
import com.guidoperre.youarrive.models.GeoCode;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.ui.finalconfirmation.SetRouteAlarmsActivity;
import com.guidoperre.youarrive.ui.finalconfirmation.SetAlarmActivity;
import com.guidoperre.youarrive.ui.firstconfirmation.FirstConfirmationActivity;

public class NavigationController {

    /////////////////////////////////////Generic Intents////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void basicIntent(Activity thisActivity, Class toActivity){
        Intent intent = new Intent(thisActivity, toActivity);
        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void semiBasicIntent(Activity thisActivity, Class toActivity, String address){
        Intent intent = new Intent(thisActivity, toActivity);

        intent.putExtra("address",address);

        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void normalIntent(Activity thisActivity, Class toActivity, double latitude, double longitude, String address){
        Intent intent = new Intent(thisActivity, toActivity);

        intent.putExtra("address",address);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void semiFullIntent(Activity thisActivity, Class toActivity, double myLatitude, double myLongitude, double latitude, double longitude, String address){
        Intent intent = new Intent(thisActivity, toActivity);

        intent.putExtra("myLatitude", myLatitude);
        intent.putExtra("myLongitude", myLongitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("address",address);

        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void fullIntent(Activity thisActivity, Class toActivity, double myLatitude, double myLongitude, double latitude, double longitude, String address, Route route, String screen){
        Intent intent = new Intent(thisActivity, toActivity);

        intent.putExtra("myLatitude", myLatitude);
        intent.putExtra("myLongitude", myLongitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("address",address);
        if (route != null){
            Gson gson = new Gson();
            String routeJSON = gson.toJson(route);
            intent.putExtra("route",routeJSON);
        }
        if (screen != null)
            intent.putExtra("screen",screen);

        thisActivity.startActivity(intent);
        thisActivity.overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////SuggestsActivity///////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void goToHome(Activity activity, final AutoSuggest home){
        Intent i = new Intent(activity, FirstConfirmationActivity.class);

        if (!home.getLabel().equals("") && home.getLatitude() != 0 && home.getLongitude() != 0){
            i.putExtra("latitude", home.getLatitude());
            i.putExtra("longitude", home.getLongitude());
            i.putExtra("address",home.getLabel());
            activity.startActivity(i);
            activity.overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
            activity.finish();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void goToFixed(Activity activity, final AutoSuggest fixed){
        Intent i = new Intent(activity, FirstConfirmationActivity.class);

        if (!fixed.getLabel().equals("") && fixed.getLatitude() != 0 && fixed.getLongitude() != 0){
            i.putExtra("latitude", fixed.getLatitude());
            i.putExtra("longitude", fixed.getLongitude());
            i.putExtra("address",fixed.getLabel());
            activity.startActivity(i);
            activity.overridePendingTransition(R.transition.fade_in, R.transition.fade_out);
            activity.finish();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////Manual Selection Method//////////////////////////////////////
    public void goManual(Activity activity){
        Intent intent = new Intent(activity, FirstConfirmationActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void goPlace(Activity activity, GeoCode location, AutoSuggest suggest){
        if (location != null){
            Intent intent = new Intent(activity,FirstConfirmationActivity.class);

            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            new SearchController().saveFixedOrHome(location,suggest);

            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("address", suggest.getLabel());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.transition.fade_in, R.transition.fade_out);

        } else
            Toast.makeText(activity,  R.string.geocode_error_message, Toast.LENGTH_LONG).show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////SelectRouteActivity/////////////////////////////////////

    ///////////////////////////////Manual Selection Method//////////////////////////////////////
    public void selectNoRoute(Activity activity, double latitude, double longitude,double myLatitude, double myLongitude, String address){
        Intent intent = new Intent(activity, SetAlarmActivity.class);

        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("myLatitude", myLatitude);
        intent.putExtra("myLongitude", myLongitude);
        intent.putExtra("address",address);

        activity.startActivity(intent);
        activity.overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
        activity.finishAndRemoveTask();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void selectRoute(Activity activity, double latitude, double longitude, double myLatitude, double myLongitude, String address, Route route){
        Intent intent = new Intent(activity, SetRouteAlarmsActivity.class);
        Gson gson = new Gson();
        String routeJSON = gson.toJson(route);

        intent.putExtra("myLatitude", myLatitude);
        intent.putExtra("myLongitude", myLongitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("address",address);
        intent.putExtra("route",routeJSON);

        activity.startActivity(intent);
        activity.overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
        activity.finishAndRemoveTask();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

}
