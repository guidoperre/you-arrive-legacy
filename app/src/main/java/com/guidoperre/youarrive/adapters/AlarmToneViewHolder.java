package com.guidoperre.youarrive.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.models.AlarmTone;

public class AlarmToneViewHolder extends RecyclerView.ViewHolder{

    private TextView title;
    private ImageView icon;
    private View separator;

    AlarmToneViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.select_alarmtone_text);
        icon = itemView.findViewById(R.id.select_alarmtone_icon);
        separator = itemView.findViewById(R.id.alarmTone_separator);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void bind(AlarmTone alarmTone, final AlarmToneAdapter.OnItemClickListener listener){
        setItemClickListener(alarmTone,itemView,listener,getAdapterPosition());

        switch (alarmTone.getType()){
            case "default":
                title.setText(R.string.default_alarm_text);
                icon.setImageResource(R.mipmap.ic_tone_foreground);
                break;
            case "custom":
                title.setText(R.string.select_file_text);
                icon.setImageResource(R.mipmap.ic_folder_foreground);
                separator.setVisibility(View.GONE);
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setItemClickListener(AlarmTone alarmTone, View itemView, final AlarmToneAdapter.OnItemClickListener listener , final int position){
        itemView.setOnClickListener(view -> listener.OnItemClick(alarmTone, position));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

}
