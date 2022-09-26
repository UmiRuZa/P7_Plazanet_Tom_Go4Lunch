package com.project.go4lunch.fragments.drawer.yourLunch;


import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.go4lunch.R;
import com.project.go4lunch.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YourLunchViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmates_picture)
    ImageView imageView;

    @BindView(R.id.workmates_lunch)
    TextView textView;


    public YourLunchViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressWarnings("deprecation")
    public void updateWithWorkmates(User user){
        if (user.getSelectRestID() != null) {
            this.textView.setText(Html.fromHtml(user.getUsername() +
                    textView.getContext().getString(R.string.is_joining)));

            Glide.with(imageView.getContext()).load(user.getUrlPicture()).into(this.imageView);
        }
    }
}