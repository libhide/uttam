package com.ratik.uttam.bg

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ratik.uttam.core.DispatcherProvider
import com.ratik.uttam.domain.PhotoRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext

@HiltWorker
internal class RefreshWallpaperWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val photoRepo: PhotoRepo,
    private val dispatcherProvider: DispatcherProvider,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.io) {
            try {
                var workResult = Result.failure()
                photoRepo.fetchRandomPhoto().collect { photo ->
                    // Implement local notification
                    workResult = Result.success()
                }
                workResult
            } catch (exception: Exception) {
                Result.failure()
            }
        }
    }
}