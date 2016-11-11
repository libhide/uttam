package com.ratik.uttam.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.ratik.uttam.services.GetPhotoJob;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ratik on 13/10/16.
 */

public class JobSetReceiver extends BroadcastReceiver {
    public static final String UTTAM_JOB_TAG = "GetPhotoJob";

    @Override
    public void onReceive(Context context, Intent intent) {
        Driver myDriver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(myDriver);

        final int oneHour = (int)TimeUnit.HOURS.toSeconds(1);

        Job job = dispatcher.newJobBuilder()
                .setService(GetPhotoJob.class)
                .setConstraints(
                        Constraint.ON_ANY_NETWORK,
                        Constraint.ON_UNMETERED_NETWORK
                )
                .setTag(UTTAM_JOB_TAG)
                .setTrigger(Trigger.executionWindow(0, oneHour))
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(true)
                .build();

        int result = dispatcher.schedule(job);
        if (result != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS) {
            // TODO: handle error
        }
    }
}
