package com.example.funfood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

        private final int layout;

    public RestaurantAdapter(Context context, int resource, @NonNull List<Restaurant> objects) {
        super(context, resource, objects);
        this.layout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null){
            // Create new view
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(layout, null);
        } else {
            view = convertView;
        }

        Restaurant restaurant = getItem(position);

        if (restaurant!=null) {

            // Recycle view
            TextView nomView = view.findViewById(R.id.nom_resto);
            nomView.setText(restaurant.getNom());

            ImageView imageRestaurant = view.findViewById(restaurant.getImage());
            imageRestaurant.setImageResource(R.drawable.img_casa_di_panini);
        }

        return view;

    }

}
