package com.guidoperre.youarrive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.models.RoutePath;

import java.util.ArrayList;

public class RouteDetailAdapter  extends RecyclerView.Adapter<RouteDetailViewHolder>{

    private ArrayList<RoutePath> routePath;

    public RouteDetailAdapter(){
    }

    public void setRoutePath(ArrayList<RoutePath> routePath){
        this.routePath = routePath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RouteDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_detail_recycle_item_layout, parent,false);
        return new RouteDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteDetailViewHolder holder, int position) {
        holder.bind(routePath.get(position),position,routePath.size());
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (routePath != null)
            size = routePath.size();
        return size;
    }

}
