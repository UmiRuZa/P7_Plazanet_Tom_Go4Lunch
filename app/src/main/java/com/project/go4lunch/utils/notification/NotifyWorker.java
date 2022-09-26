package com.project.go4lunch.utils.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.project.go4lunch.R;
import com.project.go4lunch.database.user.UserManager;
import com.project.go4lunch.model.User;
import com.project.go4lunch.ui.MainActivity;

public class NotifyWorker extends Worker {

    UserManager userManager = UserManager.getInstance();

    public NotifyWorker (@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        userManager.getUserData().addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult();
                    if (user != null) {
                        String restName = user.getSelectRestName();
                        String restAddress = user.getSelectRestAddress();
                        boolean isNotify = user.isNotify();

                        sendNotification(restName, restAddress, isNotify);
                    }
                }
            }
        });
        
        return Result.success();
    }

    private void sendNotification(String restName, String restAddress, boolean isNotify) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,i,0);

        if (isNotify) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "go4lunch")
                    .setSmallIcon(R.drawable.ic_logo_auth)
                    .setContentTitle(restName + " est prêt à vous accueillir")
                    .setContentText(restAddress)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(123, builder.build());
        }
    }

}
