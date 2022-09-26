package com.project.go4lunch.database.workmates;

import com.google.firebase.firestore.Query;

public class WorkmatesManager {

    private static volatile WorkmatesManager instance;
    private WorkmatesRepository workmatesRepository;

    private WorkmatesManager() {
        workmatesRepository = WorkmatesRepository.getInstance();
    }

    public static WorkmatesManager getInstance() {
        WorkmatesManager result = instance;
        if(result != null){
            return result;
        }
        synchronized (WorkmatesManager.class) {
            if (instance == null){
                instance = new WorkmatesManager();
            }
            return instance;
        }
    }

    public Query getAllWorkmates () {
        return workmatesRepository.getAllWorkmates();
    }

    public Query getThisRestGoingWorkmates(String restID) {
        return workmatesRepository.getThisRestGoingWorkmates(restID);
    }
}
