package com.guidoperre.youarrive.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.ConfigurationController;
import com.guidoperre.youarrive.controllers.RoutesController;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;
import com.guidoperre.youarrive.utilities.Utils;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

class RoutesViewHolder extends RecyclerView.ViewHolder{

    private RelativeLayout iconsLayout;
    private RelativeLayout iconsLayoutLarge;
    private View largeLayout;
    private TextView noRouteText;
    private TextView arriveTime;
    private ImageButton noRouteIcon;
    private GifImageView loading;
    private ImageView ellipsize;

    private Context context;
    private RoutesController controller;

    private int actualWidth = 0;
    private boolean doubleLayoutFlag = false;

    RoutesViewHolder(@NonNull View itemView) {
        super(itemView);

        context = itemView.getContext();
        noRouteText = itemView.findViewById(R.id.no_route_text);
        noRouteIcon = itemView.findViewById(R.id.no_route_icon);
        iconsLayout = itemView.findViewById(R.id.icons_layout);
        iconsLayoutLarge = itemView.findViewById(R.id.icons_layout_large);
        largeLayout = itemView.findViewById(R.id.large_layout);
        arriveTime = itemView.findViewById(R.id.arrive_time);
        loading  = itemView.findViewById(R.id.loading_gif_route);
        ellipsize = itemView.findViewById(R.id.ellipsize);
    }

    void bind(final Route route, final RoutesAdapter.OnItemClickListener listener){
        controller = new RoutesController(context);
        setVisibility();
        setItems(route,route.getMode().getType());
        setItemClickListener(itemView,listener,route,getAdapterPosition());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setVisibility(){
        actualWidth = 0;
        doubleLayoutFlag = false;
        if(iconsLayout.getVisibility() == View.VISIBLE && iconsLayout.getChildCount() > 0)
            iconsLayout.removeAllViews();
        if(iconsLayoutLarge.getVisibility() == View.VISIBLE && iconsLayoutLarge.getChildCount() > 0)
            iconsLayoutLarge.removeAllViews();
        noRouteText.setVisibility(View.INVISIBLE);
        noRouteIcon.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
        ellipsize.setVisibility(View.INVISIBLE);
        largeLayout.setVisibility(View.GONE);
        arriveTime.setVisibility(View.INVISIBLE);
        iconsLayoutLarge.setVisibility(View.GONE);
        iconsLayout.setVisibility(View.INVISIBLE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setItems(Route route,String mode){
        switch (mode){
            case "No seleccionar recorrido":
                noRouteText.setVisibility(View.VISIBLE);
                noRouteIcon.setVisibility(View.VISIBLE);
                break;
            case "loading":
                loading.setVisibility(View.VISIBLE);
                break;
            default:
                arriveTime.setVisibility(View.VISIBLE);
                iconsLayout.setVisibility(View.VISIBLE);
                ArrayList<RoutePath> routePathsList;
                if (route.getPublicTransportLine() != null && route.getPublicTransportLine().size() > 0)
                    routePathsList = controller.setItemList(route,true);
                else
                    routePathsList = controller.setItemList(route,false);
                setIcons(routePathsList);
                setTravelTime((route.getSummary().getTravelTime()/60));
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setIcons(ArrayList<RoutePath> routePathsList){
        for (int i=0;i<routePathsList.size();i++){
            if (routePathsList.get(i).getType().equals("walk")){
                ConfigurationRepository configurationRepository = new ConfigurationRepository(itemView.getContext().getApplicationContext());
                Configuration configuration = configurationRepository.get().get(0);
                ConfigurationController configurationController = new ConfigurationController();
                String type = configurationController.getType(configuration.getTransportType());
                if (type.equals("car"))
                    createCar(routePathsList.get(i).getTime(),i+1);
                else
                    createWalkingMan(routePathsList.get(i).getTime(),i+1);
            }else if(routePathsList.get(i).getType().equals("busPublic")){
                createBus(routePathsList.get(i).getName(),i+1,routePathsList);
            }else{
                createTrain(routePathsList.get(i).getName(),i+1,routePathsList );
            }
            if (i!=(routePathsList.size()-1)){
                createRightArrow(i+1);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createRightArrow(int position){
        ImageView imageView = controller.createArrowIcon(position);
        RelativeLayout.LayoutParams lpIv = controller.createArrowIconParams(position,20);
        imageView.setLayoutParams(lpIv);
        actualWidth += 24;
        setLayoutSizeForArrows(imageView);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createWalkingMan(int time,int position){
        ImageView imageView = controller.createWalkIcon(position);
        RelativeLayout.LayoutParams lpIv = controller.createWalkIconParams(position,30);
        imageView.setLayoutParams(lpIv);
        actualWidth += 20;

        TextView textView = controller.createTimeText(position,time);
        RelativeLayout.LayoutParams lpTv = controller.createWalkTextParams(imageView.getId(),50);
        textView.setLayoutParams(lpTv);
        int textWidth = new Utils().getTextWidth(textView.getText().toString(),context) - (int) (5 * Resources.getSystem().getDisplayMetrics().density);
        actualWidth += textWidth;

        setLayoutSize(textWidth,imageView,textView);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createCar(int time,int position){
        ImageView imageView = controller.createCarIcon(position);
        RelativeLayout.LayoutParams lpIv = controller.createCarIconParams(position,30);
        imageView.setLayoutParams(lpIv);
        actualWidth += 20;

        TextView textView = controller.createTimeText(position,time);
        RelativeLayout.LayoutParams lpTv = controller.createWalkTextParams(imageView.getId(),50);
        textView.setLayoutParams(lpTv);
        int textWidth = new Utils().getTextWidth(textView.getText().toString(),context) - (int) (5 * Resources.getSystem().getDisplayMetrics().density);
        actualWidth += textWidth;

        setLayoutSize(textWidth,imageView,textView);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createBus(String busName, int position,ArrayList<RoutePath> routePathsList) {
        ImageView imageView = controller.createPublicTransportIcon(position,routePathsList);
        RelativeLayout.LayoutParams lpIv = controller.createPublicTransportIconParams(position,30);
        imageView.setLayoutParams(lpIv);
        actualWidth += 30;

        TextView textView = controller.createPublicTransportText(position, busName);
        RelativeLayout.LayoutParams lpTv = controller.createPublicTransportTextParams(imageView.getId());
        textView.setLayoutParams(lpTv);
        int textWidth = new Utils().getTextWidth(textView.getText().toString(),context) + (int) (10 * Resources.getSystem().getDisplayMetrics().density);
        actualWidth += textWidth;

        setLayoutSize(textWidth,imageView,textView);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void createTrain(String busName, int position,ArrayList<RoutePath> routePathsList){
        ImageView imageView = controller.createPublicTransportIcon(position,routePathsList);
        RelativeLayout.LayoutParams lpIv = controller.createPublicTransportIconParams(position,30);
        imageView.setLayoutParams(lpIv);
        actualWidth += 30;

        TextView textView = controller.createPublicTransportText(position, busName);
        RelativeLayout.LayoutParams lpTv = controller.createPublicTransportTextParams(imageView.getId());
        textView.setLayoutParams(lpTv);
        int textWidth = new Utils().getTextWidth(textView.getText().toString(),context) + (int) (10 * Resources.getSystem().getDisplayMetrics().density);
        actualWidth += textWidth;

        setLayoutSize(textWidth,imageView,textView);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setLayoutSize(int textWidth, ImageView imageView, TextView textView){
        if (actualWidth - 0.15 * textWidth <= new Utils().getLayoutWidth() && !doubleLayoutFlag){
            iconsLayout.addView(imageView);
            iconsLayout.addView(textView);
        }else if (doubleLayoutFlag){
            if (actualWidth < new Utils().getLayoutWidth()*2){
                iconsLayoutLarge.addView(imageView);
                iconsLayoutLarge.addView(textView);
            }else
                ellipsize.setVisibility(View.VISIBLE);
        }else{
            largeLayout.setVisibility(View.VISIBLE);
            iconsLayoutLarge.setVisibility(View.VISIBLE);
            iconsLayoutLarge.addView(imageView);
            iconsLayoutLarge.addView(textView);
            doubleLayoutFlag=true;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setLayoutSizeForArrows(ImageView imageView){
        if ((actualWidth - 0.15*24)<=(new Utils().getLayoutWidth()) && !doubleLayoutFlag){
            iconsLayout.addView(imageView);
        }else if (doubleLayoutFlag){
            if (actualWidth < new Utils().getLayoutWidth()*2)
                iconsLayoutLarge.addView(imageView);
            else
                ellipsize.setVisibility(View.VISIBLE);
        }else{
            largeLayout.setVisibility(View.VISIBLE);
            iconsLayoutLarge.setVisibility(View.VISIBLE);
            iconsLayoutLarge.addView(imageView);
            doubleLayoutFlag=true;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setTravelTime(int travelTime){
        if (travelTime<=60){
            String travelText=travelTime+"min";
            arriveTime.setText(travelText);
        }else{
            String travelText=(travelTime/60)+"h y "+(travelTime%60)+"min";
            arriveTime.setText(travelText);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setItemClickListener(View itemView, final RoutesAdapter.OnItemClickListener listener , final Route routesApiList, final int position){
        itemView.setOnClickListener(view -> listener.OnItemClick(routesApiList, position));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}

