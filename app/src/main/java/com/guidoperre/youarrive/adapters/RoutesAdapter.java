package com.guidoperre.youarrive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.models.Route;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesViewHolder>{

    private List<Route> routes;
    private OnItemClickListener listener;

    public RoutesAdapter(ArrayList<Route> routes, OnItemClickListener listener){
        this.routes = routes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoutesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_recycle_item_layout, parent,false);
        return new RoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutesViewHolder holder, int position) {
        holder.bind(routes.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(Route routesApiList, int position);
    }
}
