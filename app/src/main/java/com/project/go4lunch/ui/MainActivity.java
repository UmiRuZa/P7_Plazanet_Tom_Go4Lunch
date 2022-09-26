package com.project.go4lunch.ui;

import static com.project.go4lunch.BuildConfig.MAPS_API_KEY;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.project.go4lunch.R;
import com.project.go4lunch.databinding.ActivityMainBinding;
import com.project.go4lunch.fragments.bottom.MapFragment;
import com.project.go4lunch.fragments.bottom.places.PlacesFragment;
import com.project.go4lunch.database.user.UserManager;
import com.project.go4lunch.utils.notification.NotifyWorker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private final UserManager userManager = UserManager.getInstance();

    NavHostFragment navHostFragment;

    Fragment currentFragment;

    NavigationView navigationView;
    BottomNavigationView bottomNav;

    ImageView profilePic;
    TextView uName;
    TextView uMail;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = "TAG";

    SharedPreferences sharePref;
    SharedPreferences.Editor editor;

    private WorkManager workManager;
    private PeriodicWorkRequest workRequest;

    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), MAPS_API_KEY);
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharePref.edit();
        editor.putBoolean(getString(R.string.isSearch), false).commit();

        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);


        updateUIWithUserData();

        setUpNavigation();

        configureToolBar();
        setNavigationViewUserInfo();
        setPlacesAutocompleteOverlay();

        createNotificationChannel();
        createNotification();
    }


    // ------------------
    //  TOOLBAR
    // ------------------

    public void configureToolBar() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        AppBarConfiguration configuration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setDrawerLayout(drawerLayout)
                        .build();

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, configuration);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    ViewParent parent = navigationView.getParent();
                    if (parent instanceof DrawerLayout) {
                        ((DrawerLayout) parent).closeDrawer(navigationView);
                    }
                } else if (item.getItemId() == R.id.drawer_logOutButton) {
                    userManager.signOut(MainActivity.this).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                        }
                    });
                }

                return true;
            }
        });
    }


    // ------------------
    //  BOTTOM NAV
    // ------------------

    public void setUpNavigation() {
        bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav,
                navHostFragment.getNavController());
    }


    // ------------------
    //  LOCATION
    // ------------------

    private void setPlacesAutocompleteOverlay() {

        ImageButton searchPlaces = findViewById(R.id.search_button);

        searchPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(getApplicationContext());
                //noinspection deprecation
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bundle bundle = new Bundle();

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                bundle.putDouble("Lat", place.getLatLng().latitude);
                bundle.putDouble("Lng", place.getLatLng().longitude);
                bundle.putBoolean("Search", true);
                editor.putBoolean(getString(R.string.isSearch), true).commit();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
                bundle.putBoolean("Search", false);
                editor.putBoolean(getString(R.string.isSearch), false).commit();
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
                bundle.putBoolean("Search", false);
                editor.putBoolean(getString(R.string.isSearch), false).commit();
            }
        }
        Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (currentFragment instanceof MapFragment) {
            ((MapFragment) currentFragment).setSearchLocation(bundle);
        } else if (currentFragment instanceof PlacesFragment) {
            ((PlacesFragment) currentFragment).setPlacesSearchLocation(bundle);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // ------------------
    //  USER UI
    // ------------------

    private void updateUIWithUserData() {
        if (userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();
            setNavigationViewUserInfo();
        }
    }

    private void setNavigationViewUserInfo() {
        userManager.getUserData().addOnSuccessListener(user -> {

            navigationView = findViewById(R.id.nav_view);
            View hView = navigationView.getHeaderView(0);

            profilePic = hView.findViewById(R.id.profile_picture);
            uName = hView.findViewById(R.id.profile_name);
            uMail = hView.findViewById(R.id.profile_email);

            if (user.getUrlPicture() != null) {
                Glide.with(this).load(user.getUrlPicture()).into(profilePic);
            } else {
                Glide.with(this).load(R.drawable.no_photo_go4l).into(profilePic);//profile pic null
            }
            uName.setText(user.getUsername());
            uMail.setText(user.getUserMail());
        });
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "go4lunchNotificationChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("go4lunch", name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void createNotification() {
        Calendar currentTime = Calendar.getInstance();
        Calendar dueTime = Calendar.getInstance();

        long timeDiff = calculateTimeDiff(currentTime, dueTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            workRequest = new PeriodicWorkRequest.Builder(NotifyWorker.class, 24, TimeUnit.HOURS)
                    .setConstraints(Constraints.NONE)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .build();
        }
        WorkManager.getInstance(this).enqueue(workRequest);
    }

    public static long calculateTimeDiff(Calendar currentTime, Calendar dueTime) {
        dueTime.set(Calendar.HOUR_OF_DAY, 12);
        dueTime.set(Calendar.MINUTE, 0);
        dueTime.set(Calendar.SECOND, 0);
        dueTime.set(Calendar.MILLISECOND, 0);

        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.HOUR_OF_DAY, 24);
        }

        return dueTime.getTimeInMillis() - currentTime.getTimeInMillis();
    }
}
