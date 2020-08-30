package com.guidoperre.youarrive.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.SearchController;
import com.guidoperre.youarrive.models.AutoSuggest;
import com.guidoperre.youarrive.repositories.SuggestsRepository;

import pl.droidsonroids.gif.GifImageView;

import static com.guidoperre.youarrive.ui.suggests.SuggestsActivity.setHomeFlag;

class SuggestionsViewHolder extends RecyclerView.ViewHolder {

    private TextView address;
    private TextView state;
    private TextView country;
    private TextView center;
    private ImageView icon;
    private ImageButton voidPin;
    private ImageButton fullPin;
    private ImageButton edit;
    private GifImageView loading;
    private View separator;

    private Context context;
    private AutoSuggest suggest;
    private SearchController controller;

    SuggestionsViewHolder(@NonNull View itemView) {
        super(itemView);

        context = itemView.getContext();
        address =itemView.findViewById(R.id.place_address);
        state =itemView.findViewById(R.id.place_area);
        country = itemView.findViewById(R.id.place_country);
        center = itemView.findViewById(R.id.place_center_line);
        icon = itemView.findViewById(R.id.search_icon);
        voidPin = itemView.findViewById(R.id.search_pin_void);
        fullPin = itemView.findViewById(R.id.search_pin_full);
        edit = itemView.findViewById(R.id.search_edit_home);
        loading = itemView.findViewById(R.id.loading_gif);
        separator = itemView.findViewById(R.id.place_item_separator);
    }

    void bind(final AutoSuggest suggest, final SuggestionsAdapter.OnItemClickListener listener){
        this.suggest = suggest;
        controller = new SearchController();
        setItemConfiguration();
        setPinClickListener();
        setItemClickListener(itemView,listener,getAdapterPosition());
        setEditClickListener(listener,getAdapterPosition());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setItemConfiguration(){
        String[] placeData;
        setAllInvisible();
        switch (suggest.getType()){
            case "manual_select":
                manualLayoutConfiguration();
                break;
            case "add_home":
                addHomeLayoutConfiguration();
                break;
            case "set_home":
                setHomeLayoutConfiguration();
                break;
            case "home":
                homeLayoutConfiguration();
                break;
            case "loading":
                loading.setVisibility(View.VISIBLE);
                break;
            case "bus":
                busLayoutConfiguration();
                break;
            default:
                placeData = suggest.getLabel().split(", ");
                if (placeData.length != 1)
                    placeLargeLayoutConfiguration(placeData);
                else
                    placeShortLayoutConfiguration();
                break;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void manualLayoutConfiguration(){
        String manualSelect = context.getString(R.string.manual_select);
        country.setText(manualSelect);
        voidPin.setVisibility(View.GONE);
        fullPin.setVisibility(View.GONE);
        country.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(R.mipmap.ic_manual_select_foreground);
        separator.setBackgroundColor(context.getColor(R.color.low_grey));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void addHomeLayoutConfiguration(){
        String addHome = context.getString(R.string.add_home);
        country.setText(addHome);
        country.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(R.mipmap.ic_house_foreground);
        separator.setBackgroundColor(context.getColor(R.color.low_grey));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setHomeLayoutConfiguration(){
        center.setVisibility(View.VISIBLE);
        separator.setBackgroundColor(context.getColor(R.color.colorAccent));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void busLayoutConfiguration(){
        setPins(suggest.getType());
        country.setText(suggest.getLabel());
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(R.mipmap.ic_bus_foreground);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void homeLayoutConfiguration(){
        String[] placeData = suggest.getLabel().split(", ");
        address.setText(controller.setAddress(placeData));
        state.setText(controller.setState(placeData));
        address.setVisibility(View.VISIBLE);
        state.setVisibility(View.VISIBLE);
        edit.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(R.mipmap.ic_house_foreground);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void placeLargeLayoutConfiguration(String[] placeData){
        setPins(suggest.getType());
        address.setText(controller.setAddress(placeData));
        state.setText(controller.setState(placeData));
        address.setVisibility(View.VISIBLE);
        state.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(R.mipmap.ic_place_foreground);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void placeShortLayoutConfiguration(){
        setPins(suggest.getType());
        country.setText(suggest.getLabel());
        country.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(R.mipmap.ic_place_foreground);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setAllInvisible(){
        address.setVisibility(View.INVISIBLE);
        state.setVisibility(View.INVISIBLE);
        country.setVisibility(View.INVISIBLE);
        center.setVisibility(View.INVISIBLE);
        icon.setVisibility(View.INVISIBLE);
        edit.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
        voidPin.setVisibility(View.INVISIBLE);
        fullPin.setVisibility(View.INVISIBLE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setPins(String type){
        if (setHomeFlag){
            voidPin.setVisibility(View.GONE);
            fullPin.setVisibility(View.GONE);
        }else if (type.equals("fixed")){
            voidPin.setVisibility(View.INVISIBLE);
            fullPin.setVisibility(View.VISIBLE);
        }else{
            voidPin.setVisibility(View.VISIBLE);
            fullPin.setVisibility(View.INVISIBLE);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setPinClickListener(){
        final SuggestsRepository repository = new SuggestsRepository();
        voidPin.setOnClickListener(v -> {
            voidPin.setVisibility(View.INVISIBLE);
            suggest.setType("fixed");
            repository.insert(suggest);
            fullPin.setVisibility(View.VISIBLE);

        });
        fullPin.setOnClickListener(v -> {
            voidPin.setVisibility(View.VISIBLE);
            repository.deleteByLocationID(suggest.getLocationId());
            fullPin.setVisibility(View.INVISIBLE);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setEditClickListener(final SuggestionsAdapter.OnItemClickListener listener , final int position){
        edit.setOnClickListener(view -> {
            suggest.setType("add_home");
            listener.OnItemClick(suggest, position);
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setItemClickListener(View itemView, final SuggestionsAdapter.OnItemClickListener listener , final int position){
        itemView.setOnClickListener(view -> listener.OnItemClick(suggest, position));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
