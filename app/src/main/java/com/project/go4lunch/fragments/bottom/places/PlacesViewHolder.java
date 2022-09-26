package com.project.go4lunch.fragments.bottom.places;

import static com.project.go4lunch.BuildConfig.MAPS_API_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.go4lunch.R;
import com.project.go4lunch.database.workmates.WorkmatesManager;
import com.project.go4lunch.model.restaurant.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.restaurant_name)
    TextView name;
    @BindView(R.id.restaurant_address)
    TextView address;
    @BindView(R.id.restaurant_is_open)
    TextView isOpen;

    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.workmates_going)
    TextView workmatesGoing;
    @BindView(R.id.restaurant_stars)
    RatingBar rating;

    @BindView(R.id.restaurant_picture)
    ImageView restPicture;

    SharedPreferences sharedPref;
    Context context;
    WorkmatesManager workmatesManager = WorkmatesManager.getInstance();

    public PlacesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bind(Result result, Context context) {
        this.context = context;

        name.setText(result.getName());

        address.setText(result.getVicinity());

        String openState = "";
        if (result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                openState = result.getName() + rating.getContext().getResources().getString(R.string.is_open);
            } else {
                openState = result.getName() + rating.getContext().getResources().getString(R.string.is_close);
            }
        } else {
            openState = result.getName() + rating.getContext().getResources().getString(R.string.no_info);
        }
        isOpen.setText(openState);

        distance.setText(getDistanceInMeters(result));

        workmatesGoing.setText("(0)");
        setNumberWMGoing(result);

        rating.setRating(getRating(result));

        if (result.getPhotos() != null) {
            Glide.with(restPicture.getContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photo_reference=" + result.getPhotos().get(0).getPhotoReference() + "&key=" + MAPS_API_KEY)
                    .into(restPicture);
        } else {
            Glide.with(restPicture.getContext())
                    .load(R.drawable.no_photo_go4l)
                    .into(restPicture);
        }
    }

    private void setNumberWMGoing(Result result) {
        workmatesManager.getThisRestGoingWorkmates(result.getPlaceId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                List<String> results = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("selectRestID") != null) {
                        results.add(doc.getString("selectRestID"));
                    }
                }

                workmatesGoing.setText("(" + results.size() + ")");
            }
        });
    }

    public static float getRating(Result result) {
        float rating;
        if (result.getRating() != null) {
            double rating5Star = result.getRating();
            rating = (float) ((rating5Star * 3) / 5);
        } else {
            rating = 0;
        }
        return rating;
    }

    public String getDistanceInMeters(Result result) {
        String distMeter;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Location startPoint = new Location("locationA");
        startPoint.setLatitude(Double.parseDouble(sharedPref.getString("searchLat", "0.0")));
        startPoint.setLongitude(Double.parseDouble(sharedPref.getString("searchLng", "0.0")));

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(result.getGeometry().getLocation().getLat());
        endPoint.setLongitude(result.getGeometry().getLocation().getLng());

        double dist = startPoint.distanceTo(endPoint);

        if (dist < 1000.0) {
            distMeter = String.format("%.0f m", dist);
        } else {
            distMeter = String.format("%.1f km", dist / 1000);
        }
        return distMeter;
    }
}
