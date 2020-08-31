package com.guidoperre.youarrive.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.ConfigurationController;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

class RouteDetailViewHolder extends RecyclerView.ViewHolder{

    private ImageView icon;
    private View topDot;
    private View botDot;
    private ConstraintLayout publicText;
    private TextView publicTextTitle;
    private TextView publicTextDescription;
    private TextView walkTextTitle;
    private TextView placeTextTitle;

    private Configuration configuration;
    private ConfigurationController configurationController;

    RouteDetailViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.route_detail_icon);
        topDot = itemView.findViewById(R.id.top_dot);
        botDot = itemView.findViewById(R.id.bottom_dot);
        publicText = itemView.findViewById(R.id.public_transport_text);
        publicTextTitle = itemView.findViewById(R.id.public_transport_title);
        publicTextDescription = itemView.findViewById(R.id.public_transport_description);
        walkTextTitle = itemView.findViewById(R.id.walk_text);
        placeTextTitle = itemView.findViewById(R.id.place_text);
        setAllInvisible();
    }

    private void setAllInvisible(){
        topDot.setVisibility(View.INVISIBLE);
        botDot.setVisibility(View.INVISIBLE);
        publicText.setVisibility(View.INVISIBLE);
        walkTextTitle.setVisibility(View.INVISIBLE);
        placeTextTitle.setVisibility(View.INVISIBLE);
    }

    void bind(RoutePath routePath, int position, int size){
        ConfigurationRepository configurationRepository = new ConfigurationRepository(itemView.getContext().getApplicationContext());
        configuration = configurationRepository.get().get(0);
        configurationController = new ConfigurationController();

        setImage(routePath, position, size);
        setText(routePath, position, size);
        setDots(position, size);
    }

    private void setImage(RoutePath routePath, int position, int size){
            if (position == 0)
                icon.setImageResource(R.mipmap.ic_manual_select_foreground);
            else if (position == size-1)
                icon.setImageResource(R.mipmap.ic_place_foreground);
            else if (routePath.getType().equals("walk")){
                String type = configurationController.getType(configuration.getTransportType());
                if (type.equals("car"))
                    icon.setImageResource(R.mipmap.ic_car_foreground);
                else
                    icon.setImageResource(R.mipmap.ic_little_walking_man_foreground);
            }
            else if(routePath.getType().equals("busPublic"))
                icon.setImageResource(R.mipmap.ic_bus_foreground);
            else
                icon.setImageResource(R.mipmap.ic_train_foreground);
    }

    private void setText(RoutePath routePath, int position, int size){
        if (position == 0)
            setPlaceText(routePath,true);
        else if (position == size-1)
            setPlaceText(routePath,false);
        else if (routePath.getType().equals("walk")){
            String type = configurationController.getType(configuration.getTransportType());
            if (type.equals("car"))
                setCarText(routePath);
            else
                setWalkText(routePath);
        }
        else if(routePath.getType().equals("busPublic"))
            setPublicTransportText(routePath,true);
        else
            setPublicTransportText(routePath,false);
    }

    private void setPublicTransportText(RoutePath routePath, boolean isBus){
        String title;
        String description;
        if (isBus)
            title = itemView.getResources().getString(R.string.bus) + " " + routePath.getName();
        else
            title = itemView.getResources().getString(R.string.train) + " "  + routePath.getName();
        description = routePath.getStartRoadName() + " " + itemView.getResources().getString(R.string.to) + " "  + " " + routePath.getEndRoadName();
        publicText.setVisibility(View.VISIBLE);
        publicTextTitle.setText(title);
        publicTextDescription.setText(description);

    }

    private void setWalkText(RoutePath routePath){
        String text = itemView.getResources().getString(R.string.walk) + " "  + routePath.getTime()/60 + " " + itemView.getResources().getString(R.string.minutes) + " (" + getRouteLength(routePath.getLength()) + getTypeOfMetric()+ ")";
        walkTextTitle.setVisibility(View.VISIBLE);
        walkTextTitle.setText(text);
    }

    private void setCarText(RoutePath routePath){
        String text = itemView.getResources().getString(R.string.drive) + " "  + routePath.getTime()/60 + " " + itemView.getResources().getString(R.string.minutes) + " (" + getRouteLength(routePath.getLength()) + getTypeOfMetric() + ")";
        walkTextTitle.setVisibility(View.VISIBLE);
        walkTextTitle.setText(text);
    }

    private void setPlaceText(RoutePath routePath,boolean isStart){
        placeTextTitle.setVisibility(View.VISIBLE);
        if (isStart)
            placeTextTitle.setText(R.string.your_location);
        else
            placeTextTitle.setText(routePath.getEndRoadName());
    }

    private String getRouteLength(int routeLength){
        String metric = configuration.getMetric();
        float length = routeLength;

        if (metric.equals("imperial"))
            length = length / 1609;

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');

        DecimalFormat format = new DecimalFormat("#.##", decimalFormatSymbols);

        return format.format(length);
    }

    private String getTypeOfMetric(){
        String abbreviation = "m";
        String metric = configuration.getMetric();

        if (metric.equals("imperial"))
            abbreviation = "mi";

        return abbreviation;
    }

    private void setDots(int position,int routeSize){
        if (position == 0)
            botDot.setVisibility(View.VISIBLE);
        else if (position == routeSize-1)
            topDot.setVisibility(View.VISIBLE);
        else{
            botDot.setVisibility(View.VISIBLE);
            topDot.setVisibility(View.VISIBLE);
        }
    }
}
