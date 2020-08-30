package com.guidoperre.youarrive.controllers;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.models.RouteStop;
import com.guidoperre.youarrive.models.RouteTransportLine;
import com.guidoperre.youarrive.repositories.RecoveryDataRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapController {

    private GoogleMap map;

    private static double myLatitude;
    private static double myLongitude;
    private static double latitude;
    private static double longitude;

    public MapController(GoogleMap map){
        this.map = map;
    }

    public MapController() {
    }

    /////////////////////////////////Location Button///////////////////////////////////////////
    public void myLocationButton(Activity activity, ImageButton myLocationButton) {
        myLocationButton.setOnClickListener(v -> moveCameraToMyPosition(activity,myLocationButton));
        map.setOnCameraMoveListener(() -> myLocationButton.setVisibility(View.VISIBLE));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////Location Button///////////////////////////////////////////
    public void alarmLocationButton(Activity activity, ImageButton myLocationButton, LatLng alarmLocation) {
        moveCameraToAlarm(activity,myLocationButton,alarmLocation);
        myLocationButton.setOnClickListener(v -> moveCameraToAlarm(activity,myLocationButton,alarmLocation));
        map.setOnCameraMoveListener(() -> myLocationButton.setVisibility(View.VISIBLE));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////Location Button///////////////////////////////////////////
    public void myLocationButtonForTwoPoints(Activity activity, ImageButton myLocationButton, double latitude, double longitude, double myLatitude, double myLongitude) {
        MapController.latitude = latitude;
        MapController.longitude = longitude;
        MapController.myLatitude = myLatitude;
        MapController.myLongitude = myLongitude;

        myLocationButton.setOnClickListener(v -> centerTwoPoints(activity, myLocationButton));
        map.setOnCameraMoveListener(() -> myLocationButton.setVisibility(View.VISIBLE));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////MoveCameraToMyPosition//////////////////////////////////////
    public void moveCameraToMyPosition(Activity activity, ImageButton myLocationButton) {
        PermissionsController permissionsController = new PermissionsController();

        if (permissionsController.canAccessCoarse(activity) && permissionsController.canAccessLocation(activity)) {
            final LatLng myPosition = getMyPosition(activity);

            CameraPosition cameraOfMyPosition = new CameraPosition.Builder()
                    .target(myPosition)
                    .zoom(15)
                    .bearing(360)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraOfMyPosition), 750, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    myLocationButton.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////MoveCameraToMyPosition//////////////////////////////////////
    private void moveCameraToAlarm(Activity activity, ImageButton myLocationButton, LatLng alarmPosition) {
        PermissionsController permissionsController = new PermissionsController();

        if (permissionsController.canAccessCoarse(activity) && permissionsController.canAccessLocation(activity)) {
            CameraPosition cameraOfMyPosition = new CameraPosition.Builder()
                    .target(alarmPosition)
                    .zoom(15)
                    .bearing(360)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraOfMyPosition), 500, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    myLocationButton.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////GetActualPosition///////////////////////////////////////
    public LatLng getMyPosition(Activity activity) {
        double myLatitude= 0,myLongitude = 0;
        LocationManager locManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity.getApplicationContext(), "No se concedieron los permisos", Toast.LENGTH_SHORT).show();
        }else{
            map.setMyLocationEnabled(true);
            Location loc = null;
            if (locManager != null) {
                loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc == null){
                    loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (loc == null){
                    loc = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
            }
            myLatitude = (loc != null ? loc.getLatitude() : 0);
            myLongitude = (loc != null ? loc.getLongitude() : 0);
        }
        return new LatLng(myLatitude, myLongitude);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void moveCameraToSelectedPosition(LatLng position){
        CameraPosition cameraOfMyPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(15)
                .bearing(360)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraOfMyPosition));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public String tryGetGeocode(Context context, double latitude, double longitude){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = "No encontrado";

        try {
            List<Address> myActualAddress = geocoder.getFromLocation(latitude, longitude, 1);

            if (myActualAddress.size() != 0) {
                String aux = myActualAddress.get(0).getAddressLine(0);
                String[] address_title = aux.split(",");
                address =  address_title[0];
            }

        } catch (IOException ignored){}

        return address;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<LatLng> convertToLatLng(List<String> shapes){
        ArrayList<LatLng> points = new ArrayList<>();
        for (String shape: shapes){
            String[] coordinates = shape.split(",");
            points.add(new LatLng(Double.parseDouble(coordinates[0]),Double.parseDouble(coordinates[1])));
        }
        return points;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    void setDotLine(Activity activity, ArrayList<LatLng> points){
        List<PatternItem> pattern = Arrays.asList(new Gap(25),new Dot());

        map.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(15)
                .pattern(pattern)
                .color(activity.getColor(R.color.colorAccent)));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    void setStrokeLine(Activity activity, ArrayList<LatLng> points){
        map.addPolyline(new PolylineOptions()
                .addAll(points)
                .width(15)
                .color(activity.getColor(R.color.colorAccent)));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    void setMarkerStartEnd(LatLng center, boolean small){
        if (small)
            map.addMarker(new MarkerOptions().position(center).flat(true).anchor(0.50f,0.50f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_small_circle_marker_foreground)));
        else
            map.addMarker(new MarkerOptions().position(center).flat(true).anchor(0.50f,0.50f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_circle_marker_foreground)));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void centerTwoPoints(Activity activity, ImageButton myLocationButton){
        if (latitude < myLatitude) {
            if (longitude < myLongitude){
                makeLatLngBound(activity, myLocationButton, 1);
            }else{
                makeLatLngBound(activity, myLocationButton, 2);
            }
        }else{
            if (longitude < myLongitude){
                makeLatLngBound(activity, myLocationButton, 3);
            }else{
                makeLatLngBound(activity, myLocationButton, 4);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void makeLatLngBound(Activity activity, ImageButton myLocationButton, int type){
        LatLngBounds markerCamera;

        switch (type){
            case 1:
                if (latitude-myLatitude > 0.01){
                    markerCamera = new LatLngBounds(new LatLng(latitude, longitude), new LatLng(myLatitude+0.01,myLongitude));
                }else{
                    markerCamera = new LatLngBounds(new LatLng(latitude, longitude), new LatLng(myLatitude,myLongitude));
                }
                moveCameraToBound(activity, myLocationButton, markerCamera);
                break;
            case 2:
                if (latitude-myLatitude > 0.01){
                    markerCamera = new LatLngBounds(new LatLng(latitude, myLongitude), new LatLng(myLatitude+0.01,longitude));
                }else{
                    markerCamera = new LatLngBounds(new LatLng(latitude, myLongitude), new LatLng(myLatitude,longitude));
                }
                moveCameraToBound(activity, myLocationButton, markerCamera);
                break;
            case 3:
                if (myLatitude-latitude > 0.01){
                    markerCamera = new LatLngBounds(new LatLng(myLatitude, longitude), new LatLng(latitude+0.01,myLongitude));
                }else{
                    markerCamera = new LatLngBounds(new LatLng(myLatitude, longitude), new LatLng(latitude,myLongitude));
                }
                moveCameraToBound(activity, myLocationButton, markerCamera);
                break;
            case 4:
                if (myLatitude-latitude > 0.01){
                    markerCamera = new LatLngBounds(new LatLng(myLatitude, myLongitude), new LatLng(latitude+0.01, longitude));
                }else{
                    markerCamera = new LatLngBounds(new LatLng(myLatitude, myLongitude), new LatLng(latitude, longitude));
                }
                moveCameraToBound(activity, myLocationButton, markerCamera);
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void moveCameraToBound(Activity activity, ImageButton myLocationButton, LatLngBounds markerCamera) {
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        int paddingHorizontal = (int) (width * 0.15);
        int paddingVertical = (int) (height * 0.3);

        map.setPadding(paddingHorizontal,paddingVertical,paddingHorizontal,paddingVertical);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(markerCamera, width, height, 0), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                map.setPadding(0,0,0,0);
                myLocationButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancel() {
                map.setPadding(0,0,0,0);
            }
        });

    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<LatLng> expandedShape(ArrayList<LatLng> shape){
        ArrayList<LatLng> newShape = new ArrayList<>();

        for (int i = 0; i < shape.size(); i++){
            newShape.add(shape.get(i));
            if (i != shape.size()-1 && convertDifferenceToMeters(shape.get(i),shape.get(i+1)) > 100)
                newShape.addAll(getBetweenPoints(shape.get(i),shape.get(i+1),(int) convertDifferenceToMeters(shape.get(i),shape.get(i+1))/50));
        }

        return newShape;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<LatLng> getBetweenPoints(LatLng position1, LatLng position2, int divider){
        //Get the point separation distance
        double pointsDistance = (Math.abs(position1.longitude - position2.longitude)) / (divider+1);
        //Get gradient
        double gradient = 0;
        if (position1.longitude <= position2.longitude)
            gradient = (position2.latitude - position1.latitude) / (position2.longitude-position1.longitude);
        else
            gradient = (position1.latitude - position2.latitude) / (position1.longitude-position2.longitude);
        //Get origin
        final double origin = position1.latitude - position1.longitude*gradient;

        //Use the equation y = x.(latitude/longitude)
        //Keep in mind that the x will have to have values between the latitudes of both positions, increasing it gradually using the pointsDistance to get the necessary points
        ArrayList<LatLng> betweenPoints = new ArrayList<>();

        //Always go left to right
        if (position1.longitude <= position2.longitude)
            for (double i=position1.longitude+pointsDistance; i<position2.longitude; i += pointsDistance)
                betweenPoints.add(new LatLng(gradient*i+origin,i));
        else
            for (double i=position2.longitude+pointsDistance; i<position1.longitude; i += pointsDistance)
                betweenPoints.add(new LatLng(gradient*i+origin,i));

        return betweenPoints;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////Use Haversine Formula///////////////////////////////////////
    public double convertDifferenceToMeters(LatLng position1, LatLng position2){
        final double earthRadius = 6378.137; // Radius of earth in KM
        final double latitudeDifference = position2.latitude * Math.PI /180 - position1.latitude * Math.PI /180;
        final double longitudeDifference = position2.longitude * Math.PI /180 - position1.longitude * Math.PI /180;
        final double step1 = Math.sin(latitudeDifference/2) * Math.sin(latitudeDifference/2) + Math.cos(position1.latitude * Math.PI / 180) * Math.cos(position2.latitude * Math.PI / 180) * Math.sin(longitudeDifference/2) * Math.sin(longitudeDifference/2);
        final double step2 = 2 * Math.atan2(Math.sqrt(step1), Math.sqrt(1-step1));
        final double step3 = earthRadius * step2;
        return step3 * 1000;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public LatLng findTheClosestPoint(List<LatLng> pointList, LatLng position){
        LatLng closestPoint = null;
        double closestLatitude = 0;

        //Get the closest latitude to the clicked point y the map
        for (LatLng point: pointList){
            double difference = Math.abs(position.latitude - point.latitude);
            if (closestLatitude == 0)
                closestLatitude = difference;
            else if (difference < closestLatitude)
                closestLatitude = difference;
        }

        ArrayList<LatLng> closestLatitudePoints = new ArrayList<>();
        //Check if there are more points with that latitude
        for (LatLng point: pointList){
            double difference = Math.abs(position.latitude - point.latitude);
            if (difference == closestLatitude)
                closestLatitudePoints.add(point);
        }

        //If the arrayList only have one latLng then this latLng is the closest point, if not, proceed to find the closest longitude
        if (closestLatitudePoints.size() == 1)
            closestPoint = closestLatitudePoints.get(0);
        else{
            double closestLongitude = 0;

            //Same operation that we used above
            for (LatLng point: closestLatitudePoints){
                double difference = Math.abs(position.longitude - point.longitude);
                if (closestLongitude == 0)
                    closestLongitude = difference;
                else if (difference < closestLongitude)
                    closestLongitude = difference;
            }

            //Get the point
            for (LatLng point: closestLatitudePoints){
                double difference = Math.abs(position.longitude - point.longitude);
                if (difference == closestLongitude)
                    closestPoint = point;
            }
        }

        return closestPoint;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
