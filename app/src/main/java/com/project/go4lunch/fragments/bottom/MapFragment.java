package com.project.go4lunch.fragments.bottom;

import static com.project.go4lunch.BuildConfig.MAPS_API_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.go4lunch.R;
import com.project.go4lunch.model.restaurant.Restaurant;
import com.project.go4lunch.model.restaurant.Result;
import com.project.go4lunch.utils.network.GoogleCalls;
import com.project.go4lunch.utils.network.NetworkAsyncTask;

public class MapFragment extends Fragment implements OnMapReadyCallback, NetworkAsyncTask.Listeners, GoogleCalls.Callbacks {

    double lat, lng;
    int defaultRadius, radius;
    FloatingActionButton fabLocation;

    boolean isSearch;
    boolean isRadiusSet;

    Activity activity;
    GoogleMap mMap;

    int LOCATION_REQUEST_CODE = 10001;
    private static final String TAG = "MainActivity";
    FusedLocationProviderClient fusedLocationProviderClient;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPref.edit();

        if (sharedPref.getInt(getString(R.string.newRadius), 0) == 0) {
            editor.putInt(getString(R.string.newRadius), 500).commit();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        activity = getActivity();

        defaultRadius = 500;
        isSearch = sharedPref.getBoolean(getString(R.string.isSearch), false);
        isRadiusSet = false;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        fabLocation = view.findViewById(R.id.fab_location);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        getLastLocation();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (!isSearch) {
                getLastLocation();
            } else {
                setLastLocationSearched();
            }
        } else {
            askLocationPermission();
        }
    }

    // ------------------
    //  LOCATION
    // ------------------

    public void getLastLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        if (!sharedPref.getBoolean(getString(R.string.isSearch), false)) {
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                    editor.putString(getString(R.string.searchLat), String.valueOf(lat)).commit();
                    editor.putString(getString(R.string.searchLng), String.valueOf(lng)).commit();

                    radius = sharedPref.getInt(getString(R.string.newRadius), defaultRadius);

                    LatLng userLocation = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("You are Here"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
                    executeHttpRequestWithRetrofit(lat, lng, radius);
                }
            });
        }

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
            }
        });

    }

    private void setLastLocationSearched() {
        lat = Double.parseDouble(sharedPref.getString(getString(R.string.searchLat), "0.0"));
        lng = Double.parseDouble(sharedPref.getString(getString(R.string.searchLng), "0.0"));
        radius = sharedPref.getInt(getString(R.string.newRadius), 500);
        executeHttpRequestWithRetrofit(lat, lng, radius);
    }

    public void setSearchLocation(Bundle bundle) {
        isSearch = bundle.getBoolean("Search");

        if (isSearch) {
            lat = bundle.getDouble("Lat");
            lng = bundle.getDouble("Lng");
            radius = sharedPref.getInt(getString(R.string.newRadius), defaultRadius);

            mMap.clear();

            editor.putString(getString(R.string.searchLat), String.valueOf(lat)).commit();
            editor.putString(getString(R.string.searchLng), String.valueOf(lng)).commit();
        } else {
            lat = Double.parseDouble(sharedPref.getString(getString(R.string.searchLat), "0.0"));
            lng = Double.parseDouble(sharedPref.getString(getString(R.string.searchLng), "0.0"));
            radius = sharedPref.getInt(getString(R.string.newRadius), 500);
        }

        LatLng userLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(userLocation).title("You are Here"));

        executeHttpRequestWithRetrofit(lat, lng, radius);
    }

    // ------------------
    //  PERMISSIONS
    // ------------------

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: You should show an alert dialog");
            }
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                getLastLocation();
            } else {
                //permission denied
                lat = 0;
                lng = 0;
            }
        }
    }


    // ------------------
    //  HTTP REQUEST
    // ------------------

    private void executeHttpRequestWithRetrofit(double lat, double lng, int radius) {
        this.updateUIWhenStartingHTTPRequest();
        String location = lat + "," + lng;
        GoogleCalls.fetchNearbyRestaurant(this, location, String.valueOf(radius), "restaurant", MAPS_API_KEY);
    }

    @Override
    public void onPreExecute() {
        this.updateUIWhenStartingHTTPRequest();
    }

    @Override
    public void doInBackground() {
    }

    @Override
    public void onPostExecute(String json) {
        this.updateUIWhenStoppingHTTPRequest(json);
    }

    @Override
    public void onResponse(@Nullable Restaurant restaurants) {
        // When getting response, we update UI
        if (restaurants != null) this.updateUIWithMarker(restaurants);
    }

    @Override
    public void onFailure() {
        // When getting error, we update UI
        this.updateUIWhenStoppingHTTPRequest("An error happened !");
    }

    // ------------------
    //  UPDATE UI
    // ------------------

    private void updateUIWhenStartingHTTPRequest() {
        Toast.makeText(getActivity(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
    }

    private void updateUIWhenStoppingHTTPRequest(String response) {
        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();

        lat = Double.parseDouble(sharedPref.getString(getString(R.string.searchLat), "0.0"));
        lng = Double.parseDouble(sharedPref.getString(getString(R.string.searchLng), "0.0"));
        LatLng userLocation = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
        mMap.addMarker(new MarkerOptions().position(userLocation).title(getString(R.string.you_are_here)));
    }

    private void updateUIWithMarker(Restaurant restaurants) {
        int height = 175;
        int width = 175;
        @SuppressWarnings("deprecation") @SuppressLint("UseCompatLoadingForDrawables")
        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_marker_restaurant);
        Bitmap b = bitmapDraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        for (Result restaurant : restaurants.getResults()) {
            LatLng restPos = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
            mMap.addMarker(new MarkerOptions()
                    .position(restPos)
                    .title(restaurant.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
        updateUIWhenStoppingHTTPRequest("done");
    }
}