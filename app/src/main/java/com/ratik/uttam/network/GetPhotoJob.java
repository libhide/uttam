package com.ratik.uttam.network;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.ratik.uttam.data.PhotoStore;
import com.ratik.uttam.di.Injector;
import com.ratik.uttam.data.model.Photo;
import com.ratik.uttam.util.NotificationHelper;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ratik on 23/10/17.
 */

public class GetPhotoJob extends JobService {
    private static final String TAG = GetPhotoJob.class.getSimpleName();

    @Inject
    NotificationHelper notificationHelper;

    @Inject
    FetchService service;

    @Inject
    PhotoStore photoStore;

    private CompositeDisposable compositeDisposable;

    private JobParameters jobParams;
    boolean isWorking = false;
    boolean jobCancelled = false;

    public GetPhotoJob() {
        Injector.getAppComponent().inject(this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        isWorking = true;
        jobParams = jobParameters;
        fetchPhoto();
        return isWorking;
    }

    private void fetchPhoto() {
        Log.i(TAG, "Fetching new photo...");
        Disposable fetchPhotoDisposable = service.getFetchPhotoCompletable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchSuccess, this::onFetchFailure);
        compositeDisposable.add(fetchPhotoDisposable);
    }

    private void pushNotification() {
        compositeDisposable.add(
                photoStore.getPhoto()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onSuccessfulNotificationPush,
                                this::onNotificationPushFailure
                        )
        );
    }

    private void onSuccessfulNotificationPush(Photo photo) {
        notificationHelper.pushNewNotification(photo);
        isWorking = false;
        compositeDisposable.dispose();
        jobFinished(jobParams, false);
    }

    private void onNotificationPushFailure(Throwable throwable) {
        notificationHelper.pushErrorNotification(throwable);
        isWorking = false;
        compositeDisposable.dispose();
        jobFinished(jobParams, false);
    }

    private void onFetchSuccess() {
        Log.i(TAG, "Photo saved successfully!");
        pushNotification();
    }

    private void onFetchFailure(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }
}
