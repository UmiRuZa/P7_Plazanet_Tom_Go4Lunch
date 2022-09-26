package com.project.go4lunch.fragments.drawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.go4lunch.R;
import com.project.go4lunch.database.user.UserManager;
import com.project.go4lunch.fragments.bottom.MapFragment;
import com.project.go4lunch.fragments.bottom.places.PlacesFragment;
import com.project.go4lunch.model.User;
import com.project.go4lunch.ui.MainActivity;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    TextView tvProgressLabel;
    SeekBar seekBar;
    int loadedProgress;
    int savedProgress;

    SwitchCompat notify;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String radius;

    UserManager userManager = UserManager.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        seekBar = view.findViewById(R.id.settings_radius);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        tvProgressLabel = view.findViewById(R.id.settings_radius_text);
        tvProgressLabel.setText(getText(R.string.radius_distance) + " " + progress * 100 + "m");

        notify = view.findViewById(R.id.settings_notify);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPref.edit();

        setNotifyButton();

        return view;
    }

    // ------------------
    //  RADIUS SEEKBAR
    // ------------------

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            radius = progressFormat(progress);

            tvProgressLabel.setText(getText(R.string.radius_distance) + " " + radius);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setSecondaryProgress(seekBar.getProgress());
        }
    };

    public static String progressFormat(int progress) {
        String radiusFormat;
        int radiusNum;

        if (progress < 10) {
            radiusNum = progress * 100;
            radiusFormat = radiusNum + "m";
        } else {
            radiusNum = progress * 100;
            radiusFormat = String.format("%.1f km", (double) progress / 10);
        }

        return radiusFormat;
    }

    public void onResume() {
        super.onResume();

        loadedProgress = sharedPref.getInt(getString(R.string.newRadius), 0);
        seekBar.setProgress(loadedProgress/100);
    }

    public void onPause() {
        super.onPause();

        savedProgress = seekBar.getProgress();
        editor.putInt(getString(R.string.newRadius), savedProgress*100).commit();
    }

    // ------------------
    //  NOTIFY BUTTON
    // ------------------

    private void setNotifyButton() {

        userManager.getUserData().addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
              if (task.isSuccessful()) {
                  User user = task.getResult();
                  if (user != null) {
                      notify.setChecked(user.isNotify());
                  } else {
                      notify.setChecked(false);
                  }
              }
            }
        });

        notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                userManager.updateIsNotify(b);
            }
        });
    }
}
