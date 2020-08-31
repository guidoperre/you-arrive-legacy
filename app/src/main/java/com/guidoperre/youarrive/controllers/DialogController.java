package com.guidoperre.youarrive.controllers;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.models.Configuration;
import com.guidoperre.youarrive.repositories.ConfigurationRepository;
import com.guidoperre.youarrive.services.MyService;
import com.guidoperre.youarrive.ui.main.MapsActivity;
import com.guidoperre.youarrive.ui.routes.RoutesViewModel;

import java.util.Objects;

public class DialogController {

    private ConfigurationRepository configurationRepository;

    private String metric = "";
    private String actualMetric = "";

    private String type = "";
    private String actualType = "";

    private String mode = "";
    private String actualMode = "";

    private String prefer = "";
    private String actualPrefer = "";

    private ImageView walkingMan;
    private ImageView car;
    private ImageView bus;
    private ImageView  time;
    private ImageView distance;
    private ImageView balance;
    private CheckBox busCheckBox;
    private CheckBox trainCheckBox;
    private CheckBox metroCheckBox;
    private CheckBox railCheckBox;

    ///////////////////////////////////MainActivity-Open////////////////////////////////////////
    public void openStartDialog(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.start_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        Button ready = dialog.findViewById(R.id.close_dialog);
        TextView text = dialog.findViewById(R.id.centerText);
        text.setText(context.getText(R.string.startDialog_first_advice));

        ready.setOnClickListener(v -> dialog.cancel());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////MainActivity-Exit///////////////////////////////////////
    public void openExitDialog(Context context, final Activity activity){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.exit_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        Button yes = dialog.findViewById(R.id.close_app);
        Button no  = dialog.findViewById(R.id.dont_close_app);

        no.setOnClickListener(v -> dialog.cancel());
        yes.setOnClickListener(v -> {
            activity.finishAffinity();
            System.exit(0);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void openCancelRouteDialog(Context context, Activity activity, Class goToClass, Intent serviceIntent,double myLatitude, double myLongitude, double latitude, double longitude, String address){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.cancel_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        Button yes = dialog.findViewById(R.id.yes);
        Button no  = dialog.findViewById(R.id.no);

        no.setOnClickListener(v -> dialog.cancel());
        yes.setOnClickListener(v -> {
            stopService(serviceIntent);
            if (goToClass == MapsActivity.class)
                new NavigationController().basicIntent(activity, goToClass);
            else
                new NavigationController().semiFullIntent(activity, goToClass,myLatitude,myLongitude,latitude,longitude,address);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void openMapSettingsDialog(Context context, Application application){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.map_configuration_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.setOnDismissListener(dialog1 -> {
            if (actualMetric != null && !metric.equals("") && !actualMetric.equals(metric))
                configurationRepository.updateMetric(metric);
        });

        configurationRepository = new ConfigurationRepository(application);
        Configuration configuration = configurationRepository.get().get(0);
        actualMetric= new ConfigurationController().getMetric(context, configuration.getMetric());

        setMetricSpinner(dialog);
    }

    private void setMetricSpinner(Dialog dialog){
        Spinner selectMetricSystem = dialog.findViewById(R.id.select_metric);

        ArrayAdapter<String> metricAdapter = new ArrayAdapter<>(dialog.getContext(), R.layout.spinner_item_layout);
        metricAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectMetricSystem.setAdapter(metricAdapter);

        if (actualMetric.equals(dialog.getContext().getResources().getString(R.string.metres))){
            metricAdapter.add(dialog.getContext().getResources().getString(R.string.metres));
            metricAdapter.add(dialog.getContext().getResources().getString(R.string.miles));
        }else{
            metricAdapter.add(dialog.getContext().getResources().getString(R.string.miles));
            metricAdapter.add(dialog.getContext().getResources().getString(R.string.metres));
        }
        metricAdapter.notifyDataSetChanged();

        selectMetricSystem.setOnItemSelectedListener(metricListener);
    }

    private AdapterView.OnItemSelectedListener metricListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedMetric = parent.getItemAtPosition(position).toString();
            ConfigurationController configurationController = new ConfigurationController();

            if (!selectedMetric.equals(actualMetric))
                metric = configurationController.getRawMetric(parent.getContext(),selectedMetric);
            else
                metric = "";
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void openRouteSettingsDialog(Context context, Application application, RoutesViewModel model){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.route_configuration_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.setOnDismissListener(dialog1 -> {
            if (actualMode != null && !mode.equals("") && !actualMode.equals(mode))
                configurationRepository.updateMode(mode);
            if (actualType != null && !type.equals("") && !actualType.equals(type))
                configurationRepository.updateType(type);
            if (actualPrefer != null && !actualPrefer.equals(prefer))
                configurationRepository.updatePrefer(prefer);
            model.makeCall.setValue(true);
        });

        configurationRepository = new ConfigurationRepository(application);
        Configuration configuration = configurationRepository.get().get(0);
        actualType= configuration.getTransportType();
        actualMode = configuration.getTransportMode();
        actualPrefer = configuration.getPrefTransports();

        walkingMan = dialog.findViewById(R.id.type_option_one);
        car = dialog.findViewById(R.id.type_option_two);
        bus = dialog.findViewById(R.id.type_option_three);
        time = dialog.findViewById(R.id.mode_option_one);
        distance = dialog.findViewById(R.id.mode_option_two);
        balance = dialog.findViewById(R.id.mode_option_three);
        busCheckBox = dialog.findViewById(R.id.bus_checkbox);
        trainCheckBox = dialog.findViewById(R.id.train_checkbox);
        metroCheckBox = dialog.findViewById(R.id.metro_checkbox);
        railCheckBox = dialog.findViewById(R.id.rail_checkbox);

        setType(configuration.getTransportType(),context);
        typeListener(context);
        setMode(configuration.getTransportMode(),context);
        modeListener(context);
        setPrefer(configuration.getPrefTransports());
        preferListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setType(String type, Context context){
        this.type = type;
        switch (type) {
            case "pedestrian":
                walkingMan.setBackground(ContextCompat.getDrawable(context,R.drawable.safezone_first_background));
                walkingMan.setImageResource(R.mipmap.ic_walking_man_white_foreground);
                break;
            case "car":
                car.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                car.setImageResource(R.mipmap.ic_car_white_foreground);
                break;
            case "publicTransportTimeTable":
                bus.setBackground(ContextCompat.getDrawable(context, R.drawable.safezone_fourth_background));
                bus.setImageResource(R.mipmap.ic_bus_white_foreground);
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void typeListener(Context context){
        walkingMan.setOnClickListener(v -> {
            resetBackgroundType();
            this.type = "pedestrian";
            walkingMan.setBackground(ContextCompat.getDrawable(context,R.drawable.safezone_first_background));
            walkingMan.setImageResource(R.mipmap.ic_walking_man_white_foreground);

        });
        car.setOnClickListener(v -> {
            resetBackgroundType();
            this.type = "car";
            car.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            car.setImageResource(R.mipmap.ic_car_white_foreground);

        });
        bus.setOnClickListener(v -> {
            resetBackgroundType();
            this.type = "publicTransportTimeTable";
            bus.setBackground(ContextCompat.getDrawable(context, R.drawable.safezone_fourth_background));
            bus.setImageResource(R.mipmap.ic_bus_white_foreground);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void resetBackgroundType() {
        switch (type) {
            case "pedestrian":
                walkingMan.setBackgroundResource(0);
                walkingMan.setImageResource(R.mipmap.ic_walking_man_foreground);
                break;
            case "car":
                car.setBackgroundResource(0);
                car.setImageResource(R.mipmap.ic_car_foreground);
                break;
            case "publicTransportTimeTable":
                bus.setBackgroundResource(0);
                bus.setImageResource(R.mipmap.ic_bus_foreground);
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setMode(String mode, Context context){
        this.mode = mode;
        switch (mode) {
            case "fastest":
                time.setBackground(ContextCompat.getDrawable(context,R.drawable.safezone_first_background));
                time.setImageResource(R.mipmap.ic_alarm_white_big_foreground);
                break;
            case "shortest":
                distance.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                distance.setImageResource(R.mipmap.ic_rule_white_foreground);
                break;
            case "balanced":
                balance.setBackground(ContextCompat.getDrawable(context, R.drawable.safezone_fourth_background));
                balance.setImageResource(R.mipmap.ic_balance_white_foreground);
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void modeListener(Context context){
        time.setOnClickListener(v -> {
            resetBackgroundMode();
            this.mode = "fastest";
            time.setBackground(ContextCompat.getDrawable(context,R.drawable.safezone_first_background));
            time.setImageResource(R.mipmap.ic_alarm_white_big_foreground);

        });
        distance.setOnClickListener(v -> {
            resetBackgroundMode();
            this.mode = "shortest";
            distance.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            distance.setImageResource(R.mipmap.ic_rule_white_foreground);

        });
        balance.setOnClickListener(v -> {
            resetBackgroundMode();
            this.mode = "balanced";
            balance.setBackground(ContextCompat.getDrawable(context, R.drawable.safezone_fourth_background));
            balance.setImageResource(R.mipmap.ic_balance_white_foreground);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void resetBackgroundMode() {
        switch (mode) {
            case "fastest":
                time.setBackgroundResource(0);
                time.setImageResource(R.mipmap.ic_alarm_big_foreground);
                break;
            case "shortest":
                distance.setBackgroundResource(0);
                distance.setImageResource(R.mipmap.ic_rule_foreground);
                break;
            case "balanced":
                balance.setBackgroundResource(0);
                balance.setImageResource(R.mipmap.ic_balance_foreground);
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setPrefer(String prefer) {
        if (prefer == null)
            prefer = "";

        String[] preferList = prefer.split(",");

        for (String pref : preferList){
            if (pref.equals("bus"))
                busCheckBox.setChecked(false);
            if (pref.equals("train"))
                trainCheckBox.setChecked(false);
            if (pref.equals("metro"))
                metroCheckBox.setChecked(false);
            if (pref.equals("rail"))
                railCheckBox.setChecked(false);
        }

        if (!busCheckBox.isChecked()) {
            if (prefer.equals(""))
                this.prefer = "bus";
            else
                this.prefer += ",bus";
        }
        if (!trainCheckBox.isChecked()) {
            if (prefer.equals(""))
                this.prefer = "train";
            else
                this.prefer += ",train";
        }
        if (!metroCheckBox.isChecked()) {
            if (prefer.equals(""))
                this.prefer = "metro";
            else
                this.prefer += ",metro";
        }
        if (!railCheckBox.isChecked()) {
            if (prefer.equals(""))
                this.prefer = "rail";
            else
                this.prefer += ",rail";
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void preferListener(){
        busCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefer = prefer.replaceAll(",bus","");
            prefer = prefer.replaceAll("bus,","");
            prefer = prefer.replaceAll("bus","");
            if (!isChecked)
                if (prefer.equals(""))
                    prefer = "bus";
                else
                    prefer += ",bus";
        });
        trainCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefer = prefer.replaceAll(",train","");
            prefer = prefer.replaceAll("train,","");
            prefer = prefer.replaceAll("train","");
            if (!isChecked)
                if (prefer.equals(""))
                    prefer = "train";
                else
                    prefer += ",train";
        });
        metroCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefer = prefer.replaceAll(",metro","");
            prefer = prefer.replaceAll("metro,","");
            prefer = prefer.replaceAll("metro","");
            if (!isChecked)
                if (prefer.equals(""))
                    prefer = "metro";
                else
                    prefer += ",metro";
        });
        railCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefer = prefer.replaceAll(",rail","");
            prefer = prefer.replaceAll("rail,","");
            prefer = prefer.replaceAll("rail","");
            if (!isChecked)
                if (prefer.equals(""))
                    prefer = "rail";
                else
                    prefer += ",rail";
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void stopService(Intent serviceIntent){
        if (MyService.serviceActive){
            MyService.serviceActive = false;
            stopService(serviceIntent);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
