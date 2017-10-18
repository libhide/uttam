package com.ratik.uttam.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ratik on 13/10/16.
 */

public class JobSetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        ComponentName serviceName = new ComponentName(context, GetPhotoJob.class);
//        JobInfo jobInfo = new JobInfo.Builder(MainActivity.WALL_JOB_ID, serviceName)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                .setPersisted(true)
//                .setPeriodic(TimeUnit.DAYS.toMillis(1))
//                .setRequiresDeviceIdle(false)
//                .build();
//        JobScheduler scheduler = (JobScheduler)
//                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        int result = scheduler.schedule(jobInfo);
//        if (result == JobScheduler.RESULT_SUCCESS)
//            Log.d(TAG, "Job scheduled successfully!");
    }
}
