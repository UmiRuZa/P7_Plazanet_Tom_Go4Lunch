package com.project.go4lunch.database.user;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.project.go4lunch.model.User;

public class UserManager {

    private static volatile UserManager instance;
    private UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    /*************
     * FIRESTORE *
     *************/

    public void createUser(FirebaseUser user) {
        userRepository.createUser(user);
    }

    public Task<User> getUserData() {
        // Get the user from Firestore and cast it to a User model Object
        return userRepository.getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    public Task<Void> updateRestaurant(String restID, String restName, String restAddress) {
        return userRepository.updateRestaurant(restID, restName, restAddress);
    }

    public Task<Void> addRestaurantLiked(String restLiked) {
        return userRepository.addRestaurantLiked(restLiked);
    }

    public Task<Void> deleteRestaurantLiked(String restLiked) {
        return userRepository.deleteRestaurantLiked(restLiked);
    }

    public Query getIsRestaurantLiked(String currentRest) {
        return userRepository.getIsRestaurantLiked(currentRest);
    }

    public Task<Void> updateIsNotify(Boolean isNotify) {
        return userRepository.updateIsNotify(isNotify);
    }

    public Task<Void> deleteUser(Context context) {
        // Delete the user account from the Auth
        return userRepository.deleteUser(context).addOnCompleteListener(task -> {
            // Once done, delete the user data from Firestore
            userRepository.deleteUserFromFirestore();
        });
    }
}
