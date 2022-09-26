package com.project.go4lunch.fragments.bottom.chat;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.project.go4lunch.R;
import com.project.go4lunch.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private final int colorCurrentUser;
    private final int colorRemoteUser;

    private boolean isSender;

    TextView messageTextView;
    TextView dateTextView;
    ImageView profileImage;
    ImageView senderImageView;
    LinearLayout messageTextContainer;
    LinearLayout profileContainer;
    LinearLayout messageContainer;

    public MessageViewHolder(@NonNull View itemView, boolean isSender) {
        super(itemView);
        this.isSender = isSender;

        messageTextView = itemView.findViewById(R.id.messageTextView);
        dateTextView = itemView.findViewById(R.id.dateTextView);
        profileImage = itemView.findViewById(R.id.profileImage);
        senderImageView = itemView.findViewById(R.id.senderImageView);
        messageTextContainer = itemView.findViewById(R.id.messageTextContainer);
        profileContainer = itemView.findViewById(R.id.profileContainer);
        messageContainer = itemView.findViewById(R.id.messageContainer);

        // Setup default colors
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
    }

    public void updateWithMessage(Message message, RequestManager glide){

        // Update message
        messageTextView.setText(message.getMessage());
        messageTextView.setTextAlignment(isSender ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        // Update date
        if (message.getDateCreated() != null) dateTextView.setText(this.convertDateToHour(message.getDateCreated()));

        // Update profile picture
        if (message.getUserSender().getUrlPicture() != null)
            glide.load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);

        // Update image sent
        if (message.getUrlImage() != null){
            glide.load(message.getUrlImage())
                    .into(senderImageView);
            senderImageView.setVisibility(View.VISIBLE);
        } else {
            senderImageView.setVisibility(View.GONE);
        }

        updateLayoutFromSenderType();
    }

    private void updateLayoutFromSenderType(){

        //Update Message Bubble Color Background
        ((GradientDrawable) messageTextContainer.getBackground()).setColor(isSender ? colorCurrentUser : colorRemoteUser);
        messageTextContainer.requestLayout();

        if(!isSender){
            updateProfileContainer();
            updateMessageContainer();
        }
    }

    private void updateProfileContainer(){
        // Update the constraint for the profile container (Push it to the left for receiver message)
        ConstraintLayout.LayoutParams profileContainerLayoutParams = (ConstraintLayout.LayoutParams) profileContainer.getLayoutParams();
        profileContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
        profileContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        profileContainer.requestLayout();
    }

    private void updateMessageContainer(){
        // Update the constraint for the message container (Push it to the right of the profile container for receiver message)
        ConstraintLayout.LayoutParams messageContainerLayoutParams = (ConstraintLayout.LayoutParams) messageContainer.getLayoutParams();
        messageContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.startToEnd = profileContainer.getId();
        messageContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        messageContainerLayoutParams.horizontalBias = 0.0f;
        messageContainer.requestLayout();

        // Update the constraint (gravity) for the text of the message (content + date) (Align it to the left for receiver message)
        LinearLayout.LayoutParams messageTextLayoutParams = (LinearLayout.LayoutParams) messageTextContainer.getLayoutParams();
        messageTextLayoutParams.gravity = Gravity.START;
        messageTextContainer.requestLayout();

        LinearLayout.LayoutParams dateLayoutParams = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();
        dateLayoutParams.gravity = Gravity.BOTTOM | Gravity.START;
        dateTextView.requestLayout();

    }

    public static String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dfTime.format(date);
    }

}
