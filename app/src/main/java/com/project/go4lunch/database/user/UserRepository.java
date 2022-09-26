package com.project.go4lunch.database.user;

import android.content.Context;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String REST_ID_FIELD = "selectRestID";
    private static final String REST_NAME_FIELD = "selectRestName";
    private static final String REST_ADDRESS_FIELD = "selectRestAddress";
    private static final String REST_LIKED_FIELD = "restLikedList";
    private static final String IS_NOTIFY_FIELD = "isNotify";


    private static volatile UserRepository instance;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }

    @Nullable
    public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    /*************
     * FIRESTORE *
     *************/

    // Get the Collection Reference
    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createUser(FirebaseUser user) {
        List<String> restLikedList = new ArrayList<>();
        boolean isNotify = true;

        if (user != null) {
            String username = user.getDisplayName();
            String uid = user.getUid();
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String userMail = user.getEmail();

            User userToCreate = new User(uid, username, urlPicture, userMail, "userRestID", "userRestName", "userRestAddress", true, restLikedList);

            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exist in Firestore, we get his data
            userData.addOnFailureListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    // Update User Restaurant
    public Task<Void> updateRestaurant(String restID, String restName, String restAddress) {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(REST_ID_FIELD, restID, REST_NAME_FIELD, restName, REST_ADDRESS_FIELD, restAddress);
        } else {
            return null;
        }
    }

    // Update User RestaurantLiked
    public Task<Void> addRestaurantLiked(String restLiked) {
        String uid = this.getCurrentUserUID();

        if (uid != null) {
            return this.getUsersCollection().document(uid).update(REST_LIKED_FIELD, FieldValue.arrayUnion(restLiked));
        } else {
            return null;
        }
    }

    // Delete User RestaurantLiked
    public Task<Void> deleteRestaurantLiked(String restLiked) {
        String uid = this.getCurrentUserUID();

        if (uid != null) {
            return this.getUsersCollection().document(uid).update(REST_LIKED_FIELD, FieldValue.arrayRemove(restLiked));
        } else {
            return null;
        }
    }

    // Get User RestaurantLiked
    public Query getIsRestaurantLiked(String currentRest) {
        return this.getUsersCollection().whereArrayContains(REST_LIKED_FIELD, currentRest).whereEqualTo("uid", getCurrentUserUID());
    }

    // Update User isNotify
    public Task<Void> updateIsNotify(Boolean isNotify) {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).update(IS_NOTIFY_FIELD, isNotify);
        } else {
            return null;
        }
    }

    // Delete the User from Firestore
    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            this.getUsersCollection().document(uid).delete();
        }
    }
}
