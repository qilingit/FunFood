package com.example.funfood;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView map;
    private MapboxMap mapbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiaGF0b3UiLCJhIjoiY2p0NXQzcm9kMDdtYTRhbnZ2ZHh2OGFwZiJ9.QF8RTZm-41OkTzTuvVO-HA");

        setContentView(R.layout.activity_main);

        map = findViewById(R.id.theMap);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapbox = mapboxMap;
        if(mapbox != null){
            mapbox.setStyleUrl(Style.DARK);
        }
    }
}
