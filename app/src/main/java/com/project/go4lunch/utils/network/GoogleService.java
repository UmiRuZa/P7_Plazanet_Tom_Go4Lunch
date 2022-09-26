package com.project.go4lunch.utils.network;

import com.project.go4lunch.model.detail.Detail;
import com.project.go4lunch.model.restaurant.Restaurant;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleService {
    @GET("nearbysearch/json")
    Call<Restaurant> getNearbyRestaurant(@Query("location") String location, @Query("radius") String radius, @Query("type") String type, @Query("key") String key);

    @GET("details/json")
    Call<Detail> getDetailRestaurant(@Query("place_id")String placeID, @Query("key") String key);

    //OkHttpClient okhttpClient = new OkHttpClient();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
