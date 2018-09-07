package com.ratik.uttam.receiver;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ratik.uttam.Constants;
import com.ratik.uttam.network.GetPhotoJob;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ratik on 13/10/16.
 */

public class JobSetReceiver extends BroadcastReceiver {

    private static final String TAG = JobSetReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName serviceName = new ComponentName(context, GetPhotoJob.class);
        JobInfo jobInfo = new JobInfo.Builder(Constants.Fetch.WALLPAPER_FETCH_JOB_ID, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(TimeUnit.DAYS.toMillis(1))
                .setRequiresDeviceIdle(false)
                .build();
        JobScheduler scheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS)
            Log.d(TAG, "Job scheduled successfully!");
    }
}
