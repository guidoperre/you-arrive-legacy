package com.guidoperre.youarrive.controllers;

import com.guidoperre.youarrive.models.AutoSuggest;
import com.guidoperre.youarrive.models.GeoCode;
import com.guidoperre.youarrive.repositories.SuggestsRepository;

import java.util.ArrayList;
import java.util.List;

import static com.guidoperre.youarrive.ui.suggests.SuggestsActivity.setHomeFlag;

public class SearchController {

    private SuggestsRepository repository;

    public SearchController(){
        repository = new SuggestsRepository();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<AutoSuggest> createStaticsItems(){
        ArrayList<AutoSuggest> resultGeocode = new ArrayList<>();

        if (!setHomeFlag){
            resultGeocode.add(0,getManualSelection());
            resultGeocode.add(1,getHome());
            resultGeocode.addAll(getFixedLocations());
        }else{
            resultGeocode.add(0,setHome());
        }

        return resultGeocode;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////Establish manual option////////////////////////////////////
    private AutoSuggest getManualSelection() {
        AutoSuggest secondItem = new AutoSuggest();
        secondItem.setType("manual_select");
        return secondItem;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private AutoSuggest getHome() {
        List<AutoSuggest> homeList = repository.getByType("home");
        AutoSuggest home = new AutoSuggest();

        if (homeList.size() >= 1){
            if (homeList.get(0).getLabel() != null && !homeList.get(0).getLabel().equals("")){
                home = homeList.get(0);
                return home;
            }
        }
        home.setType("add_home");

        return home;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public List<AutoSuggest> getFixedLocations() {
        return repository.getByType("fixed");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////Set gif option///////////////////////////////////////////
    public AutoSuggest getLoadingGif() {
        AutoSuggest loadingItem = new AutoSuggest();
        loadingItem.setType("loading");
        return loadingItem;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void saveFixedOrHome(GeoCode location, AutoSuggest suggest){

        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        switch (suggest.getType()){
            case "fixed":
                saveFixed(suggest,latitude,longitude);
                break;
            case "home":
                saveHome(suggest,latitude,longitude);
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public AutoSuggest setHome(){
        AutoSuggest setHome = new AutoSuggest();
        setHome.setType("set_home");
        return setHome;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void saveHome(AutoSuggest home, double latitude, double longitude){
        repository.delete("home");
        home.setType("home");
        home.setLatitude(latitude);
        home.setLongitude(longitude);
        repository.insert(home);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    void saveFixed(AutoSuggest fixed, double latitude, double longitude){
        repository.updateByLocationID(fixed.getLocationId(),String.valueOf(latitude),String.valueOf(longitude));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isFixedComplete(final AutoSuggest fixed){
        List<AutoSuggest> fixedList = getFixedLocations();
        for (AutoSuggest fixedUnit: fixedList){
            if (fixedUnit.getLabel().equals(fixed.getLabel()) && fixedUnit.getLatitude() != 0 && fixedUnit.getLongitude() != 0){
                return true;
            }
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isHomeComplete(final AutoSuggest fixed){
        AutoSuggest home = getHome();
        return home.getLabel().equals(fixed.getLabel()) && home.getLatitude() != 0.0 && home.getLongitude() != 0.0;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public String setAddress(String[] codeData){
        String address_text="";
        switch (codeData.length){
            case 1:
                address_text = codeData[0];
                break;
            case 2:
                address_text = codeData[1];
                break;
            case 3:
                address_text = codeData[2];
                break;
            case 4:
                address_text = codeData[3];
                break;
            case 5:
                address_text = codeData[4];
                break;
        }
        return address_text;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public String setState(String[] codeData){
        String state_text;
        if (codeData.length == 2) {
            state_text = codeData[0];
        } else {
            state_text = codeData[0] + ", " + codeData[1];
        }
        return state_text;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
