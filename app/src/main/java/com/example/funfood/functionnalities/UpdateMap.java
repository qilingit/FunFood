package com.example.funfood.functionnalities;

import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.funfood.R;
import com.example.funfood.entities.Result;
import com.example.funfood.entities.ResultLocation;
import com.example.funfood.fragment.FragmentMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class UpdateMap {
    FragmentMap fragmentMap;

    /**
     * show the specified marker in map
     * @param locationInit
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initMap(GoogleMap map, Location locationInit){

        initMarker(fragmentMap.markerList, locationInit);
        for (int i=0; i<fragmentMap.markerList.size(); i++){
            map.addMarker(fragmentMap.markerList.get(i));
        }

    }

    /**
     * delete marker if it exists
     * @param marker
     */
    public void deleteMarker(MarkerOptions marker){
        if(fragmentMap.markerList.contains(marker)){
            fragmentMap.markerList.remove(marker);
        }
    }

    /**
     * show markerList in map
     * @param map
     * @param markerList
     */
    public void showMarker(GoogleMap map, ArrayList<MarkerOptions> markerList, List<Result> listResult) {

        for (int i=0; i<markerList.size(); i++) {

            map.addMarker(markerList.get(i));
            //System.out.println("Test show marker : " + markerList.get(i).getTitle());
        }
    }

    /**
     * Init list with locationInit
     * @param markerList
     * @param locationInit
     */
    public void initMarker(ArrayList<MarkerOptions> markerList, Location locationInit) {
        markerList.removeAll(markerList);
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(locationInit.getLatitude(), locationInit.getLongitude()))
                .title("current location")
                .icon(fragmentMap.bitmapDescriptorFromVector(fragmentMap.getActivity(), R.drawable.ic_place_black_24dp));
        markerList.add(marker);
    }



}
