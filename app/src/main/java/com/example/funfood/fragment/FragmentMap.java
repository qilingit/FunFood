package com.example.funfood.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funfood.R;
import com.example.funfood.api.APIClient;
import com.example.funfood.api.GoogleMapAPI;
import com.example.funfood.entities.PlacesResults;
import com.example.funfood.entities.Result;
import com.example.funfood.functionnalities.UpdateMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import android.location.LocationListener;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentMap extends Fragment
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final String TAG = "FragmentMap";

    public ArrayList<MarkerOptions> markerList = new ArrayList<>();
    public List<Result> results;
    GoogleMap mGoogleMap;
    SupportMapFragment supportMapFragment;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    public Context mContext;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Spinner mySpinner;
    public Location currentBestLocation = null;
    private UpdateMap updateMap = new UpdateMap();
    private View rootView;
    private SupportMapFragment mapFragment;

    public FragmentMap() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        getMyLocation();
        currentBestLocation = getLastBestLocation();


        Log.d("OnCreate", "Passed");
        //use SuppoprtMapFragment for using in fragment instead of activity  FragmentMap = activity   SupportMapFragment = fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        //assert mapFragment != null;

        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map_fragment, mapFragment).commit();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap myMap) {
                if (myMap != null) {
                    myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    myMap.clear();

                    myMap.getUiSettings().setAllGesturesEnabled(true);
                    getMyLocation();

                    CameraPosition cameraCurrentLocation = CameraPosition.builder()
                            .target(new LatLng(currentBestLocation.getLatitude(), currentBestLocation.getLongitude()))
                            .zoom(16)
                            .bearing(0)
                            .tilt(45)
                            .build();

                    myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraCurrentLocation), 2000, null);
                    myMap.addMarker(new MarkerOptions()
                            .position(new LatLng(currentBestLocation.getLatitude(), currentBestLocation.getLongitude()))
                            .title("UPMC")
                            .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_place_black_24dp)));
                    Log.d("FragmentMap", "addMarker");
                    mGoogleMap = myMap;
                }
            }
        }

        );

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.d("===>OnAttachFragment", "passed");
    }

    @Override
    public void onResume() {
        Log.d("===>OnResume", "passed");
        super.onResume();
        mapFragment.getMapAsync(this);
        setUpMapIfNeed();
    }

    private void setUpMapIfNeed() {
        if (mGoogleMap != null) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        //mapFragment.getMapAsync(this);

        //updateSearch(rootView, mapFragment);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        //assert mapFragment != null;

        updateSearch(rootView, mapFragment);
        //mapFragment.getMapAsync(this);

        Log.d("onCreateView", "Passed");
        return rootView;
    }

    @Override
    public void onDestroy() {
        Log.d("===>onDestroy", "passed");
        super.onDestroy();
        mGoogleMap = null;
    }

    @Override
    public void onAttach(Context context) {
        Log.d("===>onAttach", "passed");
        super.onAttach(context);
    }

    public void updateSearch(View rootView, final SupportMapFragment mapFragment ) {
        // Spinner
        mySpinner = rootView.findViewById(R.id.spinnerType);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String typeCuisine = parent.getItemAtPosition(position).toString();
                Resources res = getResources();
                //String[] typeCuisineSpinner = res.getStringArray(R.array.spinner_array);
                if(parent.getItemAtPosition(position).toString() != "Tous"){

                }
                requestRestaurant(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap myMap) {
                myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                myMap.clear();

                myMap.getUiSettings().setAllGesturesEnabled(true);
                CameraPosition cameraCurrentLocation = CameraPosition.builder()
                        .target(new LatLng(currentBestLocation.getLatitude(), currentBestLocation.getLongitude()))
                        .zoom(16)
                        .bearing(0)
                        .tilt(45)
                        .build();

                myMap.addMarker(new MarkerOptions()
                        .position(new LatLng(currentBestLocation.getLatitude(), currentBestLocation.getLongitude()))
                        .title("current location")
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_place_black_24dp)));
                Log.d("****mapUpdate", "passed");

                myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraCurrentLocation), 2000, null);
                if(mGoogleMap == null) {
                    Log.d("mGoogleMap", "vide");
                }
                if(myMap == null) {
                    Log.d("myMap", "vide");
                }
                if(results != null) {
                    Log.d("****updateMarker", "passed");

                }

                mGoogleMap = myMap;
                //updateMap.showMarker(myMap, markerList, results);
            }
        });
        mapFragment.getMapAsync(this);
    }

    // for passe to method onMayReady
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("****onActivityCreated", "passed");
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        FragmentManager fm = getActivity().getSupportFragmentManager();/// getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_fragment);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_fragment, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
    }

    public void getMyLocation() {
        Log.d(TAG, "=> getDeviceLocation: getting the device current location");
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                makeUseOfNewLocation(location);

                if (currentBestLocation == null){
                    currentBestLocation = location;
                }

                //get current location
                Log.d(TAG, "onLocationChanged: latitude " + location.getLatitude());
                /*currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                Log.d(TAG, "position actuelle : " + currentLocation.latitude + ", " + currentLocation.longitude);*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TOD
            //System.exit(1);
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,
                500, mLocationListener);

    }

    /**
     *
     * @return
     */
    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return TODO;
            //System.exit(1);
            checkLocationPermission();
        }
        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;


        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    public BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

   @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Log.d("==> OnMapReady", "Passed");
        // Initialize Google play service
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("On connected ", "test");
        }
    }

    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if(mCurrLocationMarker != null){
            mCurrLocationMarker.remove();
        }

        // Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Position actuelle");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        // move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        Log.d("CheckLocationPermission", "passed");
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            // Should we show an explanation?
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public void onFragmentInteraction() {

    }

    /**
     * Request to Google Place API by type of cooking
     * @param cusineType
     */
    public void requestRestaurant(String cusineType){
        //before a search, we clear the marker list, just keep the current location marker
        markerList.clear();
        markerList.add(buildMarker(currentBestLocation.getLatitude(), currentBestLocation.getLongitude(), "current location"));

        Log.d("request", "clicked" + ", Type : " + cusineType);
        //Log.d("Position", String.valueOf(currentLocation.latitude) + String.valueOf(currentLocation.longitude));
        Log.d("Position", String.valueOf(currentBestLocation.getLatitude()) + ", " + String.valueOf(currentBestLocation.getLongitude()));

        String currentLocation = String.valueOf(currentBestLocation.getLatitude()+","+currentBestLocation.getLongitude());
        // research part
        int radius = mContext.getResources().getInteger(R.integer.searcheRadius);
        String key = mContext.getResources().getString(R.string.google_places_API_key);
        String type = mContext.getResources().getString(R.string.searchType);
        String language = mContext.getResources().getString(R.string.language);
        cusineType = switchName(cusineType);
        if(results != null){

            results.removeAll(results);
        }
        GoogleMapAPI googleMapAPI = APIClient.getClient().create(GoogleMapAPI.class);
        googleMapAPI.getNearBy(currentLocation, radius, type, cusineType, key, language).enqueue(new Callback<PlacesResults>() {
            @Override
            public void onResponse(Call<PlacesResults> call, Response<PlacesResults> response) {
                Result result;
                if (response.isSuccessful()) {
                    results = response.body().getResults();
                    for (int i=0; i<results.size(); i++){
                        result = results.get(i);
                        // if the result is not empty, add to marker list
                        if(result != null) {
                            markerList.add(buildMarker(result.getGeometry().getLocation().getLatitude(), result.getGeometry().getLocation().getLongitude(), result.getName()));
                        }
                    }

                    // update the map
                    mGoogleMap.clear();
                    updateMap.showMarker(mGoogleMap, markerList, results);

                } else {
                    Toast.makeText(mContext, "Failed", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<PlacesResults> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * This method modify the last know good location according to the arguments.
     *
     * @param location The possible new location.
     */
    void makeUseOfNewLocation(Location location) {
        if ( isBetterLocation(location, currentBestLocation) ) {
            currentBestLocation = location;
        }
    }

    /** Determines whether one location reading is better than the current location fix
     * @param location  The new location that you want to evaluate
     * @param currentBestLocation  The current location fix, to which you want to compare the new one.
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    // Checks whether two providers are the same
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public MarkerOptions buildMarker(double latitude, double longitude, String name) {
        BitmapDescriptor icon = bitmapDescriptorFromVector(mContext, R.drawable.ic_place_result_24dp);
        if(name.equals("current location")){
            icon = bitmapDescriptorFromVector(mContext, R.drawable.ic_place_black_24dp);
        }
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(name)
                .icon(icon);
    }

    /**
     * switch the french name to english, use to request to Google Place API
     * @param oldName
     * @return
     */
    public String switchName(String oldName) {
        String newName = oldName;
        if (oldName.equals("Français")){
            newName = "french";
        }
        if (oldName.equals("Japonais")){
            newName = "japon";
        }
        if (oldName.equals("Chinois")){
            newName = "chinese";
        }
        return newName;
    }
}
