package com.project.go4lunch.fragments.bottom.places;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.project.go4lunch.R;
import com.project.go4lunch.model.restaurant.Result;

import java.util.ArrayList;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

    @NonNull
    private List<Result> restaurants;

    Context context;

    public PlacesAdapter(@NonNull List<Result> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_restaurant_item, parent, false);
        context = parent.getContext();
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        holder.bind(restaurants.get(position), context);
    }

    void updateResult(@NonNull final List<Result> restaurants) {
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }

    public Result getRestaurant(int position) {
        return this.restaurants.get(position);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
