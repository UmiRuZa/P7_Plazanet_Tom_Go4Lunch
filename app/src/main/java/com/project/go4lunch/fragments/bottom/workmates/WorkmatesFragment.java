package com.project.go4lunch.fragments.bottom.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.project.go4lunch.R;
import com.project.go4lunch.database.workmates.WorkmatesManager;
import com.project.go4lunch.model.User;
import com.project.go4lunch.model.Workmates;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends Fragment {

    @BindView(R.id.workmates_rv)
    RecyclerView recyclerView;

    private List<Workmates> workmatesList;
    private WorkmatesAdapter adapter;
    private String workmatesQ;

    private WorkmatesManager workmatesManager = WorkmatesManager.getInstance();

    public WorkmatesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView();
        return view;
    }

    private void configureRecyclerView() {
        this.workmatesList = new ArrayList<>();
        this.adapter = new WorkmatesAdapter(
                generateOptionsForAdapter(workmatesManager.getAllWorkmates()),
                Glide.with(this), null);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void updateUI(List<Workmates> workmates) {
        workmates.addAll(workmates);
        adapter.notifyDataSetChanged();
    }
}
