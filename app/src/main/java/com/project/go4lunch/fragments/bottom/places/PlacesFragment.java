package com.project.go4lunch.fragments.bottom.places;

import static com.project.go4lunch.BuildConfig.MAPS_API_KEY;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.project.go4lunch.R;
import com.project.go4lunch.model.restaurant.Restaurant;
import com.project.go4lunch.model.restaurant.Result;
import com.project.go4lunch.utils.ItemClickSupport;
import com.project.go4lunch.utils.network.GoogleCalls;
import com.project.go4lunch.utils.network.NetworkAsyncTask;

import java.util.ArrayList;
import java.util.List;


public class PlacesFragment extends Fragment implements NetworkAsyncTask.Listeners, GoogleCalls.Callbacks {

    private final List<Result> restList = new ArrayList<>();
    private PlacesAdapter adapter;

    RecyclerView recyclerView;

    double defaultLat, defaultLng, lat, lng;
    int defaultRadius, radius;
    boolean isSearch;
    boolean isRadiusSet;

    FusedLocationProviderClient fusedLocationProviderClient;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        recyclerView = view.findViewById(R.id.restaurant_rv);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPref.edit();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        swipeRefreshLayout = view.findViewById(R.id.places_swipe_refresh);

        defaultRadius = 500;
        isSearch = sharedPref.getBoolean(getString(R.string.isSearch), false);
        isRadiusSet = false;

        this.configureSwipeRefreshLayout();

        this.configureOnClickRecyclerView();
        this.configureRecyclerView(restList);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isSearch) {
            setLastLocationSearched();
        } else {
            getLastLocation();
        }
    }


    // ------------------
    //  RECYCLER VIEW
    // ------------------

    private void configureRecyclerView(List<Result> restList) {
        adapter = new PlacesAdapter(restList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_places)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Result result = adapter.getRestaurant(position);

                        RestaurantFragment restaurantFragment = new RestaurantFragment();

                        restaurantFragment.setRestaurant(result);

                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, restaurantFragment).commit();
                    }
                });
    }


    // ------------------
    //  LOCATION
    // ------------------

    public void getLastLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    defaultLat = location.getLatitude();
                    defaultLng = location.getLongitude();
                }

                editor.putString(getString(R.string.searchLat), String.valueOf(defaultLat)).commit();
                editor.putString(getString(R.string.searchLng), String.valueOf(defaultLng)).commit();

                radius = sharedPref.getInt(getString(R.string.newRadius), defaultRadius);

                executeHttpRequestWithRetrofit(defaultLat, defaultLng, radius);
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "onFailure: " + e.getLocalizedMessage());
            }
        });
    }

    private void setLastLocationSearched() {
        lat = Double.parseDouble(sharedPref.getString(getString(R.string.searchLat), "0.0"));
        lng = Double.parseDouble(sharedPref.getString(getString(R.string.searchLng), "0.0"));
        radius = sharedPref.getInt(getString(R.string.newRadius), 500);
        executeHttpRequestWithRetrofit(lat, lng, radius);
    }

    public void setPlacesSearchLocation(Bundle bundle) {
        isSearch = bundle.getBoolean("Search");

        if (isSearch) {
            lat = bundle.getDouble("Lat");
            lng = bundle.getDouble("Lng");
        }

        editor.putString(getString(R.string.searchLat), String.valueOf(lat)).commit();
        editor.putString(getString(R.string.searchLng), String.valueOf(lng)).commit();

        radius = sharedPref.getInt(getString(R.string.newRadius), defaultRadius);

        executeHttpRequestWithRetrofit(lat, lng, radius);
    }


    // ------------------
    //  HTTP REQUEST
    // ------------------

    public void executeHttpRequestWithRetrofit(double lat, double lng, int radius) {
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
        // 2.1 - When getting response, we update UI
        if (restaurants != null) this.updateUIWithListOfUsers(restaurants);
    }

    @Override
    public void onFailure() {
        // 2.2 - When getting error, we update UI
        this.updateUIWhenStoppingHTTPRequest("An error happened !");
    }


    // ------------------
    //  UPDATE UI
    // ------------------

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lat = Double.parseDouble(sharedPref.getString(getString(R.string.searchLat), "0.0"));
                lng = Double.parseDouble(sharedPref.getString(getString(R.string.searchLng), "0.0"));
                radius = sharedPref.getInt(getString(R.string.newRadius), 500);
                executeHttpRequestWithRetrofit(lat, lng, radius);
            }
        });
    }

    private void updateUIWhenStartingHTTPRequest() {
        Toast.makeText(getActivity(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
    }

    private void updateUIWhenStoppingHTTPRequest(String response) {
        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
    }

    // 3 - Update UI showing only name of users
    private void updateUIWithListOfUsers(Restaurant restaurants) {
        swipeRefreshLayout.setRefreshing(false);
        restList.clear();
        for (Result restaurant : restaurants.getResults()) {
            restList.add(restaurant);
            adapter.updateResult(restList);
        }
        updateUIWhenStoppingHTTPRequest("Done");
    }
}
