package com.project.go4lunch.fragments.drawer.yourLunch;

import static android.widget.Toast.LENGTH_SHORT;
import static com.project.go4lunch.BuildConfig.MAPS_API_KEY;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.project.go4lunch.R;
import com.project.go4lunch.database.user.UserManager;
import com.project.go4lunch.database.workmates.WorkmatesManager;
import com.project.go4lunch.model.User;
import com.project.go4lunch.model.Workmates;
import com.project.go4lunch.model.detail.Detail;
import com.project.go4lunch.model.detail.ResultDetail;
import com.project.go4lunch.utils.network.GoogleCalls;
import com.project.go4lunch.utils.network.NetworkAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class YourLunchFragment extends Fragment implements NetworkAsyncTask.Listeners, GoogleCalls.CallbacksDetail {

    ResultDetail restaurant;

    ConstraintLayout constraintLayout;

    ImageView restaurantPicture;
    FloatingActionButton fabRestChosen;
    String defaultRest = "";

    TextView restaurantName;
    TextView restaurantAddress;

    RecyclerView wmGoingToThisRest;

    ImageButton callButton;
    ImageButton likeButton;
    ImageButton websiteButton;

    String placeID;
    ResultDetail resultDetail;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private List<Workmates> workmatesList;
    private YourLunchAdapter adapter;
    private String workmatesQ;
    private WorkmatesManager workmatesManager = WorkmatesManager.getInstance();

    private UserManager userManager = UserManager.getInstance();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_lunch, container, false);

        constraintLayout = view.findViewById(R.id.your_lunch_root_view);
        restaurantPicture = view.findViewById(R.id.your_lunch_photo);
        fabRestChosen = view.findViewById(R.id.your_lunch_fab);

        restaurantName = view.findViewById(R.id.your_lunch_name);
        restaurantAddress = view.findViewById(R.id.your_lunch_address);
        wmGoingToThisRest = view.findViewById(R.id.your_lunch_workmates);

        callButton = view.findViewById(R.id.your_lunch_call);
        likeButton = view.findViewById(R.id.your_lunch_like);
        websiteButton = view.findViewById(R.id.your_lunch_website);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPref.edit();

        this.updateUIWithUserData();

        return view;
    }

    private void updateUIWithUserData() {
        if (userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();
            setNavigationViewUserInfo();
        }
    }

    private void setNavigationViewUserInfo() {
        userManager.getUserData().addOnSuccessListener(user -> {
            String isRestSelect = user.getSelectRestID();

            if (!isRestSelect.equals(getString(R.string.userRestID))) {
                placeID = user.getSelectRestID();
                this.executeHttpRequestWithRetrofit(placeID);
            } else {
                setContentWhenNoRestaurantSelected();
            }
        });
    }


    // ------------------
    //  INFO DISPLAY
    // ------------------

    public void setRestaurant(ResultDetail result) {
        this.restaurant = result;

        this.updateUI();
        this.configureRecyclerView(true);
        this.setOnClickInfo();
        this.configureFABButtonState();
        this.setOnClickOnFABButton();
    }

    private void updateUI() {
        if (restaurant.getPhotos() != null) {
            Glide.with(restaurantPicture.getContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + MAPS_API_KEY)
                    .into(restaurantPicture);
        } else {
            Glide.with(restaurantPicture.getContext())
                    .load(R.drawable.no_photo_go4l)
                    .into(restaurantPicture);
        }

        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getVicinity());
    }

    private void setOnClickInfo() {
        if (resultDetail.getFormattedPhoneNumber() != null) {
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(constraintLayout, "Le numéro est le " + resultDetail.getFormattedPhoneNumber(), Snackbar.LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            callButton.getDrawable().mutate().setTint(getResources().getColor(R.color.buttonNoInfo));
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(constraintLayout, "Ce restaurant ne transmet pas cette information", Snackbar.LENGTH_SHORT)
                            .show();
                }
            });
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(constraintLayout, "Vous avez aimé se restaurant", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        if (resultDetail.getWebsite() != null) {
            websiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(constraintLayout, "Le site internet est " + resultDetail.getWebsite(), Snackbar.LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            websiteButton.getDrawable().mutate().setTint(getResources().getColor(R.color.buttonNoInfo));
            websiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(constraintLayout, "Ce restaurant ne transmet pas cette information", Snackbar.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    private void setContentWhenNoRestaurantSelected() {
        Glide.with(restaurantPicture.getContext())
                .load(R.drawable.no_photo_go4l)
                .into(restaurantPicture);
        fabRestChosen.hide();

        restaurantName.setText(getString(R.string.no_restaurant));
        restaurantAddress.setText(getString(R.string.no_restaurant_address));

        callButton.getDrawable().mutate().setTint(getResources().getColor(R.color.buttonNoInfo));
        callButton.setClickable(false);
        likeButton.getDrawable().mutate().setTint(getResources().getColor(R.color.buttonNoInfo));
        likeButton.setClickable(false);
        websiteButton.getDrawable().mutate().setTint(getResources().getColor(R.color.buttonNoInfo));
        websiteButton.setClickable(false);

        configureRecyclerView(false);
    }


    // ------------------
    //  RECYCLER VIEW
    // ------------------

    private void configureRecyclerView(boolean bool) {
        if (bool) {
            wmGoingToThisRest.setVisibility(View.VISIBLE);
            this.workmatesList = new ArrayList<>();
            this.adapter = new YourLunchAdapter(
                    generateOptionsForAdapter(workmatesManager.getThisRestGoingWorkmates(restaurant.getPlaceId())),
                    Glide.with(this), null);
            this.wmGoingToThisRest.setAdapter(this.adapter);
            this.wmGoingToThisRest.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            wmGoingToThisRest.setVisibility(View.GONE);
        }
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void updateUI(List<Workmates> workmates) {
        workmates.addAll(workmates);
        adapter.notifyDataSetChanged();
    }


    // ------------------
    //  FAB CONFIG
    // ------------------

    private void configureFABButtonState() {
        boolean isTrue = sharedPref.getBoolean(getString(R.string.isFABClick), false);

        fabRestChosen.setEnabled(true);
        fabRestChosen.setElevation(16);

        if (isTrue && sharedPref.getString(getString(R.string.isRestaurantSelect), defaultRest).equals(restaurant.getPlaceId())) {
            fabRestChosen.getDrawable().mutate().setTint(getResources().getColor(R.color.colorCheckButton));
        } else {
            fabRestChosen.getDrawable().mutate().setTint(getResources().getColor(R.color.colorCheckButtonFalse));
        }
    }

    private void setOnClickOnFABButton() {
        boolean isClick = sharedPref.getBoolean(getString(R.string.isFABClick), false);
        String selectRestFAB = sharedPref.getString(getString(R.string.isRestaurantSelect), defaultRest);

        fabRestChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClick && selectRestFAB.equals(restaurant.getPlaceId())) {
                    Toast.makeText(getActivity(), getString(R.string.restDEselected), LENGTH_SHORT).show();
                    editor.putBoolean(getString(R.string.isFABClick), false).commit();
                    editor.putString(getString(R.string.isRestaurantSelect), "").commit();
                    userManager.updateRestaurant(getString(R.string.userRestID)
                            , getString(R.string.userRestName)
                            , getString(R.string.userRestAddress));
                    updateUIWithUserData();
                }
                configureFABButtonState();
            }
        });
    }


    // ------------------
    //  HTTP REQUEST
    // ------------------

    private void executeHttpRequestWithRetrofit(String placeID) {
        this.updateUIWhenStartingHTTPRequest();
        GoogleCalls.fetchDetailRestaurant(this, placeID, MAPS_API_KEY);
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
    public void onResponse(@Nullable Detail details) {
        // When getting response, we update UI
        if (details != null) this.updateUIWithDetails(details);
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
        Toast.makeText(getActivity(), getString(R.string.loading), LENGTH_SHORT).show();
    }

    private void updateUIWhenStoppingHTTPRequest(String response) {
        Toast.makeText(getActivity(), response, LENGTH_SHORT).show();
    }

    private void updateUIWithDetails(Detail details) {
        resultDetail = details.getResult();
        setRestaurant(resultDetail);

        updateUIWhenStoppingHTTPRequest("done");
    }
}
