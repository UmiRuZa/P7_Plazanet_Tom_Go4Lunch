package com.project.go4lunch.fragments.drawer.yourLunch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.util.Listener;
import com.project.go4lunch.R;
import com.project.go4lunch.fragments.bottom.workmates.WorkmatesViewHolder;
import com.project.go4lunch.model.User;
import com.project.go4lunch.model.Workmates;

import java.util.List;

public class YourLunchAdapter extends FirestoreRecyclerAdapter<User, YourLunchViewHolder> {

    public YourLunchAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide,
                            Listener callback) {
        super(options);
    }

    @NonNull
    @Override
    public YourLunchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_workmates_item, parent, false);

        return new YourLunchViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull YourLunchViewHolder holder, int position, @NonNull User model) {
        holder.updateWithWorkmates(model);
    }
}
