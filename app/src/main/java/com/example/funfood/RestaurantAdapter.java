package com.example.funfood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

        final Restaurant restaurant = getItem(position);

        if (restaurant!=null) {

            // Recycle view
            TextView nomView = view.findViewById(R.id.nom_resto);
            nomView.setText(restaurant.getNom());

            final ImageView imageRestaurant = view.findViewById(restaurant.getImage());
            imageRestaurant.setImageResource(R.drawable.img_casa_di_panini);

            ImageButton shareBtn = view.findViewById(R.id.share_btn);

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, restaurant.getNom());
                    sendIntent.setType("text/plain");
                    getContext().startActivity(sendIntent);

                }
            });

            ImageButton callBtn = view.findViewById(R.id.call_btn);

            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:0760371766"));
                    getContext().startActivity(intent); 
                }
            });

        }

        return view;

    }

}
