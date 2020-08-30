package com.guidoperre.youarrive.ui.finalconfirmation;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.adapters.AlarmToneAdapter;
import com.guidoperre.youarrive.controllers.AlarmController;
import java.util.Objects;

public class AlarmToneDialog extends DialogFragment {

    private final static int RQS_OPEN_AUDIO_MP3 = 1;

    private AlarmController controller = new AlarmController();

    private View mView;
    private AlarmToneAdapter adapter;

    public AlarmToneDialog() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_alarmtone_dialog, container, false);
        this.mView = v;
        return v;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    static AlarmToneDialog newInstance(){
        return new AlarmToneDialog();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();
        setDialogMetrics();
        initializeRecycler();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setDialogMetrics(){
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        if (getDialog() != null && getDialog().getWindow() != null){
            getDialog().getWindow().setLayout(width, height);
            getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()),R.drawable.dialog_background));
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeRecycler(){
        RecyclerView recyclerView = mView.findViewById(R.id.alarmtone_dialog_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mView.getContext());

        setAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setAdapter(){
        adapter = new AlarmToneAdapter(controller.createItems(),(alarmTone, position) -> {
            switch (alarmTone.getType()){
                case "default":
                    Uri file = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    if (getActivity() != null)
                        controller.loadDefaultFile(getActivity().getApplication(),file);
                    dismiss();
                    break;
                case "custom":
                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);
                    break;
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1 || data == null || data.getData() == null)
            return;
        if (requestCode == RQS_OPEN_AUDIO_MP3) {
            Uri uri= data.getData();
            if (uri != null)
                if (getActivity() != null)
                    controller.loadCustomFile(getActivity().getApplication(), uri);
            dismiss();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
