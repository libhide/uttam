package com.ratik.uttam.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.ratik.uttam.asyncs.WallpaperTask;

/**
 * Created by Ratik on 26/03/17.
 */

public class GetPhotoJob2 extends JobService {
    private WallpaperTask downloadWallpaperTask = null;

    @Override
    public boolean onStartJob(final JobParameters params) {
        downloadWallpaperTask = new WallpaperTask(this);
        downloadWallpaperTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (downloadWallpaperTask != null) {
            downloadWallpaperTask.cancel(true);
        }
        return false;
    }
}
