package com.guidoperre.youarrive.ui.finalconfirmation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.adapters.RouteDetailAdapter;
import com.guidoperre.youarrive.controllers.RoutesController;
import com.guidoperre.youarrive.models.RoutePath;
import com.guidoperre.youarrive.repositories.AlarmRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouteDetailFragment extends Fragment {

    private FinalConfirmationViewModel model;

    private View mView;

    private ArrayList<RoutePath> routePath;

    private RoutesController routesController = new RoutesController();
    private RouteDetailAdapter adapter;

    public RouteDetailFragment() {}

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_detail, container, false);
        this.mView = view;
        return view;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        initializeViewModel();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();
        initializeRecycler();
        drawRoute();
        closeListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getExtras(){
        routePath =  routesController.parseRoutePathJson(Objects.requireNonNull(getArguments()).getString("routePath"));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeRecycler(){
        RecyclerView recyclerView = mView.findViewById(R.id.route_detail_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mView.getContext());
        adapter = new RouteDetailAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeViewModel(){
        model = new FinalConfirmationViewModel(Objects.requireNonNull(getActivity()).getApplication());
        model.getRoutePath().observe(this, this::updateRouteDetail);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void updateRouteDetail(List<RoutePath> routePath){
        this.routePath.clear();
        this.routePath.add(setFirstPlace(routePath));
        this.routePath.addAll(routePath);
        this.routePath.add(setLastPlace(routePath));
        adapter.setRoutePath(this.routePath);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private RoutePath setFirstPlace(List<RoutePath> routePath){
        RoutePath firstPlace = new RoutePath();
        firstPlace.setStartRoadName(routePath.get(0).getStartRoadName());
        return firstPlace;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private RoutePath setLastPlace(List<RoutePath> routePath){
        RoutePath lastPlace = new RoutePath();
        lastPlace.setEndRoadName(routePath.get(routePath.size()-1).getEndRoadName());
        return lastPlace;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void drawRoute(){
        if (mView != null){
            RelativeLayout routeLayout = mView.findViewById(R.id.route_layout);
            ImageView ellipsize = mView.findViewById(R.id.ellipsize);

            if (routePath != null){
                routesController.setIcons(mView.getContext(), routePath, routeLayout, ellipsize);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void closeListener(){
        ImageButton closeDetail = mView.findViewById(R.id.close_route_detail);
        closeDetail.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();
        model.getRoutePath().removeObservers(this);
    }
}
