package com.guidoperre.youarrive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.models.AlarmTone;

import java.util.ArrayList;

public class AlarmToneAdapter extends RecyclerView.Adapter<AlarmToneViewHolder>{

    private ArrayList<AlarmTone> list;
    private OnItemClickListener listener;

    public AlarmToneAdapter(ArrayList<AlarmTone> alarmTones, OnItemClickListener listener){
        this.list = alarmTones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlarmToneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_alarmtone_item_layout, parent,false);
        return new AlarmToneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmToneViewHolder holder, int position) {
        holder.bind(list.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(AlarmTone alarmTone, int position);
    }
}
