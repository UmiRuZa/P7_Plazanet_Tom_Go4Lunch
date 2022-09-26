package com.project.go4lunch.fragments.bottom.workmates;


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

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmates_picture)
    ImageView imageView;

    @BindView(R.id.workmates_lunch)
    TextView textView;


    public WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressWarnings("deprecation")
    public void updateWithWorkmates(User user){
        if (user.getSelectRestID() != null && !user.getSelectRestID().equals("userRestID")) {
            this.textView.setText(Html.fromHtml(user.getUsername() +
                    textView.getContext().getString(R.string.is_eating) +
                    "<b>" + user.getSelectRestName() + "</b>"));
        } else {
            this.textView.setText(Html.fromHtml(user.getUsername() +
                    textView.getContext().getString(R.string.hasnt_decided_yet)));
            textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.noRestSelectedWM));
        }
        Glide.with(imageView.getContext()).load(user.getUrlPicture()).into(this.imageView);
    }
}