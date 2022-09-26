package com.project.go4lunch.database.workmates;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.go4lunch.database.user.UserManager;

public class WorkmatesRepository {

    String userRestID;
    int numGoingWM = 0;

    private static final String COLLECTION_NAME = "users";

    private static volatile WorkmatesRepository instance;

    private UserManager userManager;

    public static WorkmatesRepository getInstance() {
        WorkmatesRepository result = instance;
        if (result != null) {
            return result;
        }

        synchronized (WorkmatesRepository.class) {
            if (instance == null) {
                instance = new WorkmatesRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }


    /*************
     * FIRESTORE *
     *************/

    private CollectionReference getWorkmatesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public Query getAllWorkmates() {
        return this.getWorkmatesCollection().whereNotEqualTo("uid", getCurrentUserUID());
    }

    public Query getThisRestGoingWorkmates(String restID) {
        return this.getWorkmatesCollection().whereEqualTo("selectRestID", restID).whereNotEqualTo("uid", getCurrentUserUID());
    }
}
