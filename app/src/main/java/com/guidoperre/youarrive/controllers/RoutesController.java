package com.guidoperre.youarrive.controllers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.models.Alarm;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.models.RouteManeuver;
import com.guidoperre.youarrive.models.RouteMode;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.models.RouteShape;
import com.guidoperre.youarrive.models.RouteStop;
import com.guidoperre.youarrive.models.RouteTransportLine;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;
import com.guidoperre.youarrive.repositories.RecoveryDataRepository;
import com.guidoperre.youarrive.utilities.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoutesController {

    private Context context;

    private int publicTransportAux = 0;

    private ImageView ellipsize;
    private int actualWidth = 0;
    private int layoutWidth = 0;

    public RoutesController() {
    }

    public RoutesController(Context context) {
        this.context = context;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public Route parseRouteJson(String routeJson){
        Gson gson = new Gson();
        return gson.fromJson(routeJson,Route.class);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<RoutePath> parseRoutePathJson(String routePathJson){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<RoutePath>>() {}.getType();
        return gson.fromJson(routePathJson,listType);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public List<String> parseRouteShapeJson(String routeShapeJson){
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(routeShapeJson,listType);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<Route> setItems(){
        ArrayList<Route> resultRoutes = new ArrayList<>();
        resultRoutes.add(0, getFirstPlace());
        resultRoutes.add(1, getSecondPlace());
        return resultRoutes;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////Establish home option///////////////////////////////////////
    public Route getFirstPlace() {
        Route firstItem = new Route();
        RouteMode mode = new RouteMode();
        mode.setType("No seleccionar recorrido");
        firstItem.setMode(mode);
        return firstItem;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private Route getSecondPlace() {
        Route secondItem = new Route();
        RouteMode mode = new RouteMode();
        mode.setType("loading");
        secondItem.setMode(mode);
        return secondItem;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<RoutePath> setItemList(final Route route, boolean simplify){
        List<RouteManeuver> maneuverList = route.getLeg().get(0).getManeuver();
        return makeRoute(maneuverList,route,simplify);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public int getSafeZoneRange(int safezone){
        int metres = 0;
        switch (safezone){
            case 1:
                metres = 0;
                break;
            case 2:
                metres = 100;
                break;
            case 3:
                metres = 250;
                break;
            case 4:
                metres = 500;
                break;
        }
        return metres;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public LatLng getNextStop(double latitude, double longitude, Application application){
        RecoveryDataRepository recoveryDataRepository = new RecoveryDataRepository(application);
        RecoveryData recoveryData = recoveryDataRepository.getRecoveryData().get(0);

        MapController mapController = new MapController();
        Route route = parseRouteJson(Objects.requireNonNull(recoveryData.getRouteJSON()));

        ArrayList<RouteStop> allStops = new ArrayList<>();
        ArrayList<LatLng> shape = mapController.convertToLatLng(route.getShape());
        ArrayList<LatLng> pointsList = mapController.expandedShape(shape);

        for (RouteTransportLine transportLine: route.getPublicTransportLine())
            allStops.addAll(transportLine.getStops());

        LatLng nextStop = new LatLng(allStops.get(allStops.size()-1).getPosition().getLatitude(),allStops.get(allStops.size()-1).getPosition().getLongitude());

        for (int i=pointsList.size()-1;i>=0;i--){
            LatLng point = pointsList.get(i);
            for (int x=allStops.size()-1;x>=0;x--){
                RouteStop stop = allStops.get(x);
                if (mapController.convertDifferenceToMeters(point,new LatLng(stop.getPosition().getLatitude(),stop.getPosition().getLongitude())) < 50){
                    nextStop = new LatLng(stop.getPosition().getLatitude(),stop.getPosition().getLongitude());
                    break;
                }
            }
            if (mapController.convertDifferenceToMeters(point,new LatLng(latitude,longitude)) < 50)
                break;
        }

        return nextStop;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<RouteTransportLine> getRemainingTransportsLines(Route route, LatLng myPosition, LatLng alarmStop){
        MapController mapController = new MapController();

        ArrayList<RouteStop> allStops = new ArrayList<>();
        ArrayList<LatLng> shape = mapController.convertToLatLng(route.getShape());
        ArrayList<LatLng> pointsList = mapController.expandedShape(shape);

        for (RouteTransportLine transportLine: route.getPublicTransportLine())
            allStops.addAll(transportLine.getStops());

        ArrayList<RouteStop> remainingStopFromStart = new ArrayList<>();

        for (RouteStop stop:allStops){
            remainingStopFromStart.add(stop);
            if (mapController.convertDifferenceToMeters(new LatLng(stop.getPosition().getLatitude(),stop.getPosition().getLongitude()),alarmStop) < 25)
                break;
        }

        LatLng closestRoutePosition = mapController.findTheClosestPoint(pointsList,myPosition);
        ArrayList<RouteStop> remainingStops = new ArrayList<>();

        boolean last = false;
        for (int i=pointsList.size()-1;i>=0;i--){
            boolean stopFor = false;
            LatLng point = pointsList.get(i);
            for (int x=remainingStopFromStart.size()-1;x>=0;x--){
                RouteStop stop = remainingStopFromStart.get(x);
                if (mapController.convertDifferenceToMeters(point,new LatLng(stop.getPosition().getLatitude(),stop.getPosition().getLongitude())) < 25){
                    int equalAux = 0;
                    for (RouteStop remainingStop:remainingStops)
                        if (!remainingStop.getStopName().equals(stop.getStopName()) && remainingStop.getPosition() != stop.getPosition())
                            equalAux++;
                    if (equalAux == remainingStops.size()){
                        if (last)
                            stopFor = true;
                        remainingStops.add(stop);
                        break;
                    }
                }
            }
            if (mapController.convertDifferenceToMeters(point,closestRoutePosition) < 50)
                last = true;
            if (stopFor)
                break;
        }

        ArrayList<RouteTransportLine> remainingTransportLines = new ArrayList<>();

        for (RouteTransportLine transportLine: route.getPublicTransportLine()){
            RouteTransportLine newTransportLine = new RouteTransportLine();
            ArrayList<RouteStop> stops = new ArrayList<>();

            for (RouteStop stop:transportLine.getStops())
                for (RouteStop remainingStop:remainingStops) {
                    if (stop.getStopName().equals(remainingStop.getStopName()) && stop.getPosition() == remainingStop.getPosition()){
                        if (newTransportLine.getLineName() == null)
                            newTransportLine = transportLine;
                        stops.add(remainingStop);
                    }
                }

            if (newTransportLine.getLineName() != null){
                newTransportLine.getStops().clear();
                newTransportLine.getStops().addAll(stops);
                remainingTransportLines.add(newTransportLine);
            }

        }

        return remainingTransportLines;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public LatLng getBestStopLocation(LatLng myPosition, Route route, ArrayList<RouteTransportLine> remainingTransportLines){
        LatLng bestStopLocation;
        String type = getTypeOfRoute(myPosition,route);

        if (type.equals("walk"))
            bestStopLocation = myPosition;
        else
            bestStopLocation = new LatLng(remainingTransportLines.get(0).getStops().get(0).getPosition().getLatitude(),remainingTransportLines.get(0).getStops().get(0).getPosition().getLongitude());

        return bestStopLocation;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////



    public int betweenStopsDifference(LatLng myPosition, Route route, ArrayList<RouteTransportLine> remainingTransportLines){
        MapController mapController = new MapController();

        int difference = 0;
        String type = getTypeOfRoute(myPosition,route);

        ArrayList<RouteStop> allStops = new ArrayList<>();
        for (RouteTransportLine transportLine: remainingTransportLines)
            allStops.addAll(transportLine.getStops());

        if (!type.equals("walk") && allStops.size() > 1){
            int firstStopTime = allStops.get(0).getTravelTime();

            LatLng firstStopLocation = new LatLng(allStops.get(0).getPosition().getLatitude(),allStops.get(0).getPosition().getLongitude());
            LatLng secondStopLocation = new LatLng(allStops.get(1).getPosition().getLatitude(),allStops.get(1).getPosition().getLongitude());

            double differenceBetweenMyPositionAndFirstStop = Math.abs(mapController.convertDifferenceToMeters(myPosition,firstStopLocation));
            double differenceBetweenMyPositionAndSecondStop = Math.abs(mapController.convertDifferenceToMeters(myPosition,secondStopLocation));
            double totalDifferenceBetweenBothStops = differenceBetweenMyPositionAndFirstStop + differenceBetweenMyPositionAndSecondStop;

            if (totalDifferenceBetweenBothStops != 0)
                difference = (int) (differenceBetweenMyPositionAndFirstStop * firstStopTime / totalDifferenceBetweenBothStops);
        }

        return difference;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private String getTypeOfRoute(LatLng myPosition, Route route){
        MapController mapController = new MapController();

        ArrayList<LatLng> shape = mapController.convertToLatLng(route.getShape());
        ArrayList<LatLng> pointsList = mapController.expandedShape(shape);

        ArrayList<RouteShape> shapeList = makeShapeList(route,pointsList);
        LatLng closestRoutePosition = mapController.findTheClosestPoint(pointsList,myPosition);

        String type = "walk";

        for (RouteShape routeShape:shapeList){
            boolean stopFor = false;
            for (LatLng point:routeShape.getShape())
                if (mapController.convertDifferenceToMeters(point,closestRoutePosition) < 30){
                    type = routeShape.getType();
                    stopFor = true;
                    break;
                }
            if (stopFor)
                break;
        }

        return type;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public Route checkForSameRoute(List<Route> routes, ArrayList<RouteTransportLine> remainingTransportLines){
        Route sameRoute = new Route();
        ArrayList<RouteStop> allStops = new ArrayList<>();

        for (RouteTransportLine transportLine: remainingTransportLines)
            allStops.addAll(transportLine.getStops());

        for (Route route:routes){
            int equals = 0;
            for (RouteTransportLine routeTransportLine:route.getPublicTransportLine())
                for (RouteStop routeRemainingStop: allStops)
                    for (RouteStop stop:routeTransportLine.getStops())
                        if (stop.getStopName().equals(routeRemainingStop.getStopName()) || stop.getPosition() == routeRemainingStop.getPosition())
                            equals++;
            if (equals == allStops.size()){
                sameRoute = route;
                break;
            }
        }

        return sameRoute;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<RoutePath> makeRoute(List<RouteManeuver> maneuverList, Route route, boolean simplify) {
        ArrayList<RoutePath> routePathsList = new ArrayList<>();

        String lastManeuverType = "";
        int lastPosition = -1;
        int time = 0;
        int length = 0;
        boolean alertFlag = true;
        publicTransportAux = 0;

        for (int i=0; i < maneuverList.size(); i++){
            RouteManeuver maneuver = maneuverList.get(i);
            if (lastManeuverType.equals("") || !lastManeuverType.equals(maneuver.getType())){
                RoutePath item = setItem(maneuver,route,simplify);
                alertFlag = true;
                if (item != null){
                    if (lastPosition >= 0){
                        routePathsList.get(lastPosition).setEndLatitude(maneuver.getPosition().getLatitude());
                        routePathsList.get(lastPosition).setEndLongitude(maneuver.getPosition().getLongitude());
                    }
                    routePathsList.add(item);
                    alertFlag = false;
                    lastPosition++;
                }
                time = maneuver.getTravelTime();
                length = maneuver.getLength();
                lastManeuverType = maneuver.getType();
            }else{
                if (!alertFlag){
                    routePathsList.get(lastPosition).setTime(time + maneuver.getTravelTime());
                    routePathsList.get(lastPosition).setLength(length + maneuver.getLength());
                }
                time += maneuver.getTravelTime();
                length += maneuver.getLength();
            }
            if (i == maneuverList.size()-1 && lastPosition != -1){
                routePathsList.get(lastPosition).setEndLatitude(maneuver.getPosition().getLatitude());
                routePathsList.get(lastPosition).setEndLongitude(maneuver.getPosition().getLongitude());
            }
        }


        return routePathsList;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void drawRoute(Activity activity, Route route, MapController controller){
        ArrayList<LatLng> pointsList = new MapController().convertToLatLng(route.getShape());
        ArrayList<RouteShape> shapeList = makeShapeList(route,pointsList);

        ConfigurationRepository configurationRepository = new ConfigurationRepository(activity.getApplicationContext());
        Configuration configuration = configurationRepository.get().get(0);
        ConfigurationController configurationController = new ConfigurationController();
        String type = configurationController.getType(configuration.getTransportType());

        for (int i=0; i< shapeList.size() ;i++){
            RouteShape shape = shapeList.get(i);
            if (i==0){
                controller.setMarkerStartEnd(shape.getShape().get(0),false);
                controller.setMarkerStartEnd(shape.getShape().get(shape.getShape().size()-1),true);
            } else if (i==shapeList.size()-1){
                controller.setMarkerStartEnd(shape.getShape().get(0),true);
                controller.setMarkerStartEnd(shape.getShape().get(shape.getShape().size()-1),false);
            } else {
                controller.setMarkerStartEnd(shape.getShape().get(0),true);
                controller.setMarkerStartEnd(shape.getShape().get(shape.getShape().size()-1),true);
            }
            if (shape.getType().equals("walk") && !type.equals("car"))
                controller.setDotLine(activity, shape.getShape());
            else
                controller.setStrokeLine(activity, shape.getShape());
        }
   }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<RouteShape> makeShapeList(Route route, ArrayList<LatLng> pointsList){
        ArrayList<RoutePath> routePath = makeRoute(route.getLeg().get(0).getManeuver(),route,false);
        ArrayList<RouteShape> shapeList = new ArrayList<>();

        int lastPosition = 0;

        for (RoutePath pathPoint: routePath){
            ArrayList<LatLng> shapeFrame = new ArrayList<>();
            RouteShape shape = new RouteShape();
            for (int i=lastPosition; i<pointsList.size(); i++){
                shapeFrame.add(pointsList.get(i));
                if (Math.abs(pointsList.get(i).latitude - pathPoint.getEndLatitude()) < 0.00005 && Math.abs(pointsList.get(i).longitude - pathPoint.getEndLongitude()) < 0.00005){
                    shape.setShape(shapeFrame);
                    shape.setId(pathPoint.getStopID());
                    shape.setType(pathPoint.getType());
                    shapeList.add(shape);
                    lastPosition = i;
                    break;
                }
            }
        }

        shapeList = cleanShape(shapeList);

        return shapeList;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<RouteShape> cleanShape(ArrayList<RouteShape> shapeList){
        ArrayList<RouteShape> newShapeList = new ArrayList<>();
        newShapeList.add(shapeList.get(0));

        for (RouteShape shape:shapeList){
            int ok = 0;
            for (RouteShape newShape:newShapeList){
                if (!shape.getId().equals(newShape.getId()))
                    ok++;
                if (ok == newShapeList.size())
                    newShapeList.add(shape);
            }
        }

        return newShapeList;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private RoutePath setItem(RouteManeuver maneuver, Route route, Boolean simplify){
        RoutePath newItem = new RoutePath("walk");
        newItem.setTime(maneuver.getTravelTime());
        newItem.setLength(maneuver.getLength());
        newItem.setStartLatitude(maneuver.getPosition().getLatitude());
        newItem.setStartLongitude(maneuver.getPosition().getLongitude());
        newItem.setStopID(maneuver.getId());

        if (!maneuver.getType().equals("PrivateTransportManeuverType") && route.getPublicTransportLine().size() > publicTransportAux) {
            newItem.setType(route.getPublicTransportLine().get(publicTransportAux).getType());
            newItem.setName(route.getPublicTransportLine().get(publicTransportAux).getLineName());
            publicTransportAux++;
        } else if (simplify){
            newItem = null;
        }

        return newItem;
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void setIcons(Context context, ArrayList<RoutePath> routePathsList, RelativeLayout layout, ImageView ellipsize){
        this.ellipsize = ellipsize;
        this.context = context;

        actualWidth = 0;
        layoutWidth = new Utils().getLayoutWidth();
        for (int i=0;i<routePathsList.size();i++){
            if (routePathsList.get(i).getType().equals("walk")){
                ConfigurationRepository configurationRepository = new ConfigurationRepository(context.getApplicationContext());
                Configuration configuration = configurationRepository.get().get(0);
                ConfigurationController configurationController = new ConfigurationController();
                String type = configurationController.getType(configuration.getTransportType());
                if (type.equals("car"))
                    createCar(routePathsList.get(i).getTime(),i+1,layout);
                else
                    createWalkingMan(routePathsList.get(i).getTime(),i+1,layout);
            }else if(routePathsList.get(i).getType().equals("busPublic")){
                createBus(routePathsList.get(i).getTime(),i+1,routePathsList,layout);
            }else{
                createTrain(routePathsList.get(i).getTime(),i+1,routePathsList,layout);
            }
            if (i!=(routePathsList.size()-1)){
                createRightArrow(i+1,layout);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createRightArrow(int position, RelativeLayout layout){
        ImageView imageView = createArrowIcon(position);
        RelativeLayout.LayoutParams lpIv = createArrowIconParams(position,20);
        imageView.setLayoutParams(lpIv);
        actualWidth += 24;

        if (actualWidth < layoutWidth)
            layout.addView(imageView);
        else
            ellipsize.setVisibility(View.VISIBLE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createWalkingMan(int time,int position, RelativeLayout layout){
        ImageView imageView = createWalkIcon(position);
        RelativeLayout.LayoutParams lpIv = createWalkIconParams(position,30);
        imageView.setLayoutParams(lpIv);
        actualWidth += 30;

        TextView textView = createTimeText(position,time);
        RelativeLayout.LayoutParams lpTv = createWalkTextParams(imageView.getId(),63);
        textView.setLayoutParams(lpTv);
        int textWidth = new Utils().getTextWidth(textView.getText().toString(),context) - (int) (5 * Resources.getSystem().getDisplayMetrics().density);
        actualWidth += textWidth;

        if (actualWidth < layoutWidth){
            layout.addView(imageView);
            layout.addView(textView);
        }else
            ellipsize.setVisibility(View.VISIBLE);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createCar(int time,int position, RelativeLayout layout){
        ImageView imageView = createCarIcon(position);
        RelativeLayout.LayoutParams lpIv = createCarIconParams(position,40);
        imageView.setLayoutParams(lpIv);
        actualWidth += 40;

        TextView textView = createTimeText(position,time);
        RelativeLayout.LayoutParams lpTv = createWalkTextParams(imageView.getId(),50);
        textView.setLayoutParams(lpTv);
        int textWidth = new Utils().getTextWidth(textView.getText().toString(),context) - (int) (5 * Resources.getSystem().getDisplayMetrics().density);
        actualWidth += textWidth;

        if (actualWidth < layoutWidth){
            layout.addView(imageView);
            layout.addView(textView);
        }else
            ellipsize.setVisibility(View.VISIBLE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createBus(int time, int position,ArrayList<RoutePath> routePathsList, RelativeLayout layout) {
        ImageView imageView = createPublicTransportIcon(position,routePathsList);
        RelativeLayout.LayoutParams lpIv = createPublicTransportIconParams(position,35);
        imageView.setLayoutParams(lpIv);
        actualWidth += 35;

        TextView textView = createTimeText(position,time);
        RelativeLayout.LayoutParams lpTv = createWalkTextParams(imageView.getId(),63);
        textView.setLayoutParams(lpTv);
        int textWidth = new Utils().getTextWidth(textView.getText().toString(),context) - (int) (5 * Resources.getSystem().getDisplayMetrics().density);
        actualWidth += textWidth;

        if (actualWidth < layoutWidth){
            layout.addView(imageView);
            layout.addView(textView);
        }else
            ellipsize.setVisibility(View.VISIBLE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createTrain(int time, int position,ArrayList<RoutePath> routePathsList, RelativeLayout layout){
        ImageView imageView = createPublicTransportIcon(position,routePathsList);
        RelativeLayout.LayoutParams lpIv = createPublicTransportIconParams(position,35);
        imageView.setLayoutParams(lpIv);
        actualWidth += 35;

        TextView textView = createTimeText(position,time);
        RelativeLayout.LayoutParams lpTv = createWalkTextParams(imageView.getId(),63);
        textView.setLayoutParams(lpTv);
        int textWidth = new Utils().getTextWidth(textView.getText().toString(),context) - (int) (5 * Resources.getSystem().getDisplayMetrics().density);
        actualWidth += textWidth;

        if (actualWidth < layoutWidth){
            layout.addView(imageView);
            layout.addView(textView);
        }else
            ellipsize.setVisibility(View.VISIBLE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ImageView createArrowIcon(int position){
        ImageView imageView = new ImageView(context);
        int id = context.getResources().getIdentifier("arrow_"+position,"id",context.getPackageName());

        imageView.setId(id);
        imageView.setImageResource(R.mipmap.ic_right_arrow_foreground);

        return imageView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public RelativeLayout.LayoutParams createArrowIconParams(int position, int size){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) (size * Resources.getSystem().getDisplayMetrics().density),(int)(size * Resources.getSystem().getDisplayMetrics().density));
        int id = context.getResources().getIdentifier("text_"+position,"id",context.getPackageName());

        lp.setMargins((int) (2 * Resources.getSystem().getDisplayMetrics().density),0,(int)(2 * Resources.getSystem().getDisplayMetrics().density),0);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.END_OF,id);

        return lp;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ImageView createWalkIcon(int position){
        ImageView imageView = new ImageView(context);

        int id = context.getResources().getIdentifier("icon_"+position,"id",context.getPackageName());
        imageView.setImageResource(R.mipmap.ic_walking_man_foreground);
        imageView.setId(id);

        return imageView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public RelativeLayout.LayoutParams createWalkIconParams(int position, int size) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) (size * Resources.getSystem().getDisplayMetrics().density), (int) (size * Resources.getSystem().getDisplayMetrics().density));

        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        if (position==1){
            lp.addRule(RelativeLayout.END_OF,R.id.icons_layout);
        }else{
            int id = context.getResources().getIdentifier("arrow_"+(position-1),"id",context.getPackageName());
            lp.addRule(RelativeLayout.END_OF,id);
        }

        return lp;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ImageView createCarIcon(int position){
        ImageView imageView = new ImageView(context);

        int id = context.getResources().getIdentifier("icon_"+position,"id",context.getPackageName());
        imageView.setImageResource(R.mipmap.ic_car_foreground);
        imageView.setId(id);

        return imageView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public RelativeLayout.LayoutParams createCarIconParams(int position, int size) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) (size * Resources.getSystem().getDisplayMetrics().density), (int) (size * Resources.getSystem().getDisplayMetrics().density));

        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        if (position==1){
            lp.addRule(RelativeLayout.END_OF,R.id.icons_layout);
        }else{
            int id = context.getResources().getIdentifier("arrow_"+(position-1),"id",context.getPackageName());
            lp.addRule(RelativeLayout.END_OF,id);
        }

        return lp;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public TextView createTimeText(int position, int time){
        TextView textView = new TextView(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.quicksand_medium);
        int id = context.getResources().getIdentifier("text_"+position,"id",context.getPackageName());

        textView.setId(id);
        textView.setText(String.valueOf(time/60));
        textView.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        textView.setTextSize(14);
        textView.setTypeface(face);

        return textView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public RelativeLayout.LayoutParams createWalkTextParams(int id, int margin){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp.setMargins(0,(int) (margin * Resources.getSystem().getDisplayMetrics().density),0,0);
        lp.addRule(RelativeLayout.END_OF,id);

        return lp;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ImageView createPublicTransportIcon(int position,ArrayList<RoutePath> routePathsList) {
        ImageView imageView = new ImageView(context);

        int id = context.getResources().getIdentifier("icon_"+position,"id",context.getPackageName());
        if (routePathsList.get(position-1).getType().equals("busPublic")){
            imageView.setImageResource(R.mipmap.ic_bus_foreground);
        }else{
            imageView.setImageResource(R.mipmap.ic_train_foreground);
        }
        imageView.setId(id);

        return imageView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public RelativeLayout.LayoutParams createPublicTransportIconParams(int position,int size){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) (size * Resources.getSystem().getDisplayMetrics().density),(int)(size * Resources.getSystem().getDisplayMetrics().density));

        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        if (position==1){
            lp.addRule(RelativeLayout.END_OF,R.id.icons_layout);
        }else{
            int id = context.getResources().getIdentifier("arrow_"+(position-1),"id",context.getPackageName());
            lp.addRule(RelativeLayout.END_OF,id);
        }

        return lp;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public TextView createPublicTransportText(int position, String busName){
        TextView textView = new TextView(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.quicksand_medium);
        int id = context.getResources().getIdentifier("text_"+position,"id",context.getPackageName());
        String[] parts = busName.split(" ");

        textView.setId(id);
        if (parts.length>1){
            textView.setText(parts[1]);
        }else{
            textView.setText(busName);
        }
        textView.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
        textView.setTextSize(14);
        textView.setBackground(context.getDrawable(R.drawable.bus_number_background));
        textView.setTypeface(face);
        textView.setPadding((int) (5 * Resources.getSystem().getDisplayMetrics().density),(int) (2 * Resources.getSystem().getDisplayMetrics().density),(int) (5 * Resources.getSystem().getDisplayMetrics().density),(int) (2 * Resources.getSystem().getDisplayMetrics().density));

        return textView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public RelativeLayout.LayoutParams createPublicTransportTextParams(int id){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.END_OF,id);

        return lp;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
