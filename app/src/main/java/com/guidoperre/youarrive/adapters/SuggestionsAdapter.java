package com.guidoperre.youarrive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.guidoperre.youarrive.models.AutoSuggest;
import com.guidoperre.youarrive.R;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsViewHolder>{

    private List<AutoSuggest> suggestsList;
    private OnItemClickListener listener;

    public SuggestionsAdapter(ArrayList<AutoSuggest> suggestsList, OnItemClickListener listener){
        this.suggestsList = suggestsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_recycler_item_layout, parent,false);
        return new SuggestionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionsViewHolder holder, int position) {
        holder.bind(suggestsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return suggestsList.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(AutoSuggest suggest, int position);
    }

}
