package com.guidoperre.youarrive.controllers;

public class ConfigurationController {

    ////////////////////////////////////////////////////////////////////////////////////////////
    public String getMetric(String metric){
        String finalMetric = "metric";

        if (metric.equals("metric") || metric.equals("imperial"))
            finalMetric = metric;

        return finalMetric;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public String getType(String type){
        String finalType = "publicTransportTimeTable";

        if (type.equals("publicTransportTimeTable") || type.equals("car") || type.equals("pedestrian"))
            finalType = type;

        return finalType;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public String getMode(String mode){
        String finalMode = "fastest";

        if (mode.equals("fastest") || mode.equals("shortest") || mode.equals("balanced"))
            finalMode = mode;

        return finalMode;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public String getPrefer(String prefer){
        StringBuilder finalPrefer = new StringBuilder();

        String[] preferList = prefer.split(",");

        for (String pref : preferList){
            if (pref.equals("bus")){
                String bus = "busPublic,busTouristic,busIntercity,busExpress";
                if (finalPrefer.toString().equals(""))
                    finalPrefer = new StringBuilder(bus);
                else
                    finalPrefer.append(",").append(bus);
            }
            if (pref.equals("train")) {
                String train = "railLight,railRegional,trainRegional,trainIntercity,trainHighSpeed";
                if (finalPrefer.toString().equals(""))
                    finalPrefer = new StringBuilder(train);
                else
                    finalPrefer.append(",").append(train);
            }
            if (pref.equals("metro")){
                String metro = "railMetro,railMetroRegional";
                if (finalPrefer.toString().equals(""))
                    finalPrefer = new StringBuilder(metro);
                else
                    finalPrefer.append(",").append(metro);
            }
            if (pref.equals("rail")){
                String rail = "monoRail";
                if (finalPrefer.toString().equals(""))
                    finalPrefer = new StringBuilder(rail);
                else
                    finalPrefer.append(",").append(rail);
            }
        }

        return finalPrefer.toString();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

}
