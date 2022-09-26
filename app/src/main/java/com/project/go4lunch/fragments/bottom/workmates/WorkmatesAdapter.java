package com.project.go4lunch.fragments.bottom.workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.util.Listener;
import com.project.go4lunch.R;
import com.project.go4lunch.model.User;
import com.project.go4lunch.model.Workmates;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewHolder> {

    private List<Workmates> workmatesList;

    private final RequestManager glide;
    private Listener callback;

    public WorkmatesAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide,
                            Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_workmates_item, parent, false);

        return new WorkmatesViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position, @NonNull User model) {
        holder.updateWithWorkmates(model);
    }
}
