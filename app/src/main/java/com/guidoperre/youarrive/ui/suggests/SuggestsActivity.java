package com.guidoperre.youarrive.ui.suggests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.models.AutoSuggest;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.adapters.SuggestionsAdapter;
import com.guidoperre.youarrive.controllers.SearchController;
import com.guidoperre.youarrive.repositories.GeoCodeRepository;
import com.guidoperre.youarrive.repositories.SuggestsRepository;
import com.guidoperre.youarrive.ui.main.MapsActivity;
import com.guidoperre.youarrive.utilities.Utils;

public class SuggestsActivity extends AppCompatActivity {

    //////////////////////////////////Variables///////////////////////////////////////////////
    private SuggestionsAdapter adapter;

    private SuggestsViewModel model;

    private SearchController searchController = new SearchController();
    private NavigationController navigationController = new NavigationController();
    private ArrayList<AutoSuggest> suggests = new ArrayList<>();

    public EditText searchPlace;

    private Handler handler = new Handler();
    private Runnable runnable;

    private String countryCodeValue;

    public static boolean setHomeFlag = false;
    private boolean isTextEmptyFlag = true;
    ////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////OnCreate/////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SearchTheme);
        Utils.lockScreenOrientation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggests);

        getLocationCode();
        onTextViewChange();
        initializeViewModel();
        instanceRecyclerView();
        getExtras();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getLocationCode(){
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            countryCodeValue = tm.getNetworkCountryIso();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////Text Change Call///////////////////////////////////////
    private void onTextViewChange(){
        searchPlace = findViewById(R.id.search_text);
        searchPlace.requestFocus();
        searchPlace.setSelection(0);
        searchPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(final Editable editable) {
                afterTextChangedRunnable(editable.toString());
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void afterTextChangedRunnable(String query){
        if (runnable != null)
            handler.removeCallbacks(runnable);

        deleteSuggestions();
        if (!setHomeFlag){
            refreshFixed();
        }
        adapter.notifyDataSetChanged();

        if (!query.equals("")){
            suggests.add(searchController.getLoadingGif());
            isTextEmptyFlag = false;
            runnable = () -> new SuggestsRepository().get(query,countryCodeValue);
            handler.postDelayed(runnable, 500);
        } else
            isTextEmptyFlag = true;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////getExtras//////////////////////////////////////////////
    private void getExtras(){
        if (getIntent().getExtras() != null){
            String address = getIntent().getExtras().getString("address");
            if (address != null && !address.equals("")) {
                searchPlace.setText(address);
                searchPlace.setSelection(searchPlace.getText().length());
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeViewModel(){
        model = new SuggestsViewModel(getApplication());
        model.getSuggestions().observe(this, this::updateSuggestions);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////RecyclerView Adapter//////////////////////////////////////
    private void instanceRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.placesRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        adapterClickListener();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        suggests.clear();
        suggests.addAll(searchController.createStaticsItems());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void adapterClickListener(){
        adapter = new SuggestionsAdapter(suggests, (suggest, position) -> {
            model.cancelAsyncTasks();
            switch (suggest.getType()){
                case "manual_select":
                    navigationController.goManual(this);
                    break;
                case "add_home":
                    searchPlace.setText("");
                    suggests.clear();
                    suggests.add(searchController.setHome());
                    adapter.notifyDataSetChanged();
                    setHomeFlag = true;
                    break;
                case "home":
                    if (searchController.isHomeComplete(suggest))
                        navigationController.goToHome(this, suggest);
                    else
                        navigationController.goPlace(this, new GeoCodeRepository().callGeocode(suggest.getLocationId()),suggest);
                    break;
                case "fixed":
                    if (searchController.isFixedComplete(suggest))
                        navigationController.goToFixed(this, suggest);
                    else
                        navigationController.goPlace(this, new GeoCodeRepository().callGeocode(suggest.getLocationId()),suggest);
                    break;
                case "set_home":
                    break;
                default:
                    if (!setHomeFlag)
                        navigationController.goPlace(this, new GeoCodeRepository().callGeocode(suggest.getLocationId()),suggest);
                    else{
                        setHomeFlag = false;
                        searchController.saveHome(suggest,0.0,0.0);
                        searchPlace.setText("");
                        suggests.clear();
                        suggests.addAll(searchController.createStaticsItems());
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void updateSuggestions(List<AutoSuggest> autoSuggests){
        if (!isTextEmptyFlag) {
            deleteSuggestions();
            for (AutoSuggest suggest : autoSuggests) {
                int ok = 0;
                for (int i = 0; i < suggests.size(); i++) {
                    if (suggests.get(i).getType().equals("loading")) {
                        ok++;
                        suggests.remove(i);
                    } else if (!suggest.getLocationId().equals(suggests.get(i).getLocationId()))
                        ok++;
                    if (ok == suggests.size())
                        suggests.add(suggest);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void deleteSuggestions(){
        for (int i = suggests.size()-1; i != 0; i--){
            if (suggests.get(i).getType().equals("suggest"))
                suggests.remove(i);
            else if (suggests.get(i).getType().equals("loading"))
                suggests.remove(i);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void refreshFixed(){
        for (int i = suggests.size()-1; i != 0; i--){
            if (suggests.get(i).getType().equals("fixed"))
                suggests.remove(i);
        }
        suggests.addAll(searchController.getFixedLocations());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////Super OnBack//////////////////////////////////////////
    @Override
    public void onBackPressed () {
        if (setHomeFlag){
            model.cancelAsyncTasks();
            setHomeFlag=false;
            suggests.clear();
            suggests.addAll(searchController.createStaticsItems());
            adapter.notifyDataSetChanged();
        }else{
            Intent intent = new Intent(SuggestsActivity.this, MapsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
