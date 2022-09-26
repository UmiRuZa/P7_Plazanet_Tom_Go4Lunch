package com.project.go4lunch.fragments.bottom.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.project.go4lunch.R;
import com.project.go4lunch.database.chat.ChatManager;
import com.project.go4lunch.database.user.UserManager;
import com.project.go4lunch.model.Message;

public class ChatFragment extends Fragment implements MessageChatAdapter.Listener{

    private MessageChatAdapter mentorChatAdapter;
    private String chatName = "General";

    private UserManager userManager = UserManager.getInstance();
    private ChatManager chatManager = ChatManager.getInstance();

    RecyclerView chatRecyclerView;
    TextView emptyRecyclerView;
    Button sendButton;
    EditText chatEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_general, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        emptyRecyclerView = view.findViewById(R.id.emptyRecyclerView);
        sendButton = view.findViewById(R.id.sendButton);
        chatEditText = view.findViewById(R.id.chatEditText);

        this.configureRecyclerView();
        this.setupListeners();

        return view;
    }

    private void setupListeners(){
        // Send button
        sendButton.setOnClickListener(view -> { sendMessage(); });
    }

    private void sendMessage(){
        // Check if user can send a message (Text not null + user logged)
        boolean canSendMessage = !TextUtils.isEmpty(chatEditText.getText()) && userManager.isCurrentUserLogged();

        if (canSendMessage){
            // Create a new message for the chat
            chatManager.createMessageForChat(chatEditText.getText().toString(), this.chatName);
            // Reset text field
            chatEditText.setText("");
        }
    }

    // Configure RecyclerView
    private void configureRecyclerView(){
        //Configure Adapter & RecyclerView
        this.mentorChatAdapter = new MessageChatAdapter(
                generateOptionsForAdapter(chatManager.getAllMessageForChat(this.chatName)),
                Glide.with(this), this);

        mentorChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                chatRecyclerView.smoothScrollToPosition(mentorChatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatRecyclerView.setAdapter(this.mentorChatAdapter);
    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    public void onDataChanged() {
        // Show TextView in case RecyclerView is empty
        emptyRecyclerView.setVisibility(this.mentorChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
