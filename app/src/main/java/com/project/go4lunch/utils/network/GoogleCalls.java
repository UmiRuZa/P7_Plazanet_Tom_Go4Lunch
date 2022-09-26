package com.project.go4lunch.utils.network;

import androidx.annotation.Nullable;

import com.project.go4lunch.model.detail.Detail;
import com.project.go4lunch.model.restaurant.Restaurant;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleCalls {

    // Creating a callback
    public interface Callbacks {
        void onResponse(@Nullable Restaurant restaurants);
        void onFailure();
    }

    public interface CallbacksDetail {
        void onResponse(@Nullable Detail detail);
        void onFailure();
    }

    // Public method to start fetching users following by Jake Wharton
    public static void fetchNearbyRestaurant(Callbacks callbacks, String location, String radius, String type, String key){

        // Create a weak reference to callback (avoid memory leaks)
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<Callbacks>(callbacks);

        // Get a Retrofit instance and the related endpoints
        GoogleService googleService = GoogleService.retrofit.create(GoogleService.class);

        // Create the call on Github API
        Call<Restaurant> call = googleService.getNearbyRestaurant(location, radius, type, key);
        // Start the call
        call.enqueue(new Callback<Restaurant>() {

            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                // Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                // Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }

    // Public method to start fetching users following by Jake Wharton
    public static void fetchDetailRestaurant(CallbacksDetail callbacks, String placeID, String key){

        // Create a weak reference to callback (avoid memory leaks)
        final WeakReference<CallbacksDetail> callbacksWeakReference = new WeakReference<CallbacksDetail>(callbacks);

        // Get a Retrofit instance and the related endpoints
        GoogleService googleService = GoogleService.retrofit.create(GoogleService.class);

        // Create the call on Github API
        Call<Detail> call = googleService.getDetailRestaurant(placeID, key);
        // Start the call
        call.enqueue(new Callback<Detail>() {

            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                // Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onResponse(response.body());
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                // Call the proper callback used in controller (MainFragment)
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });
    }
}
