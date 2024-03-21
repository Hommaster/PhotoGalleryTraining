package com.example.photogallery

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.photogallery.repository.PhotoRepository
import com.example.photogallery.repository.PreferencesRepository
import kotlinx.coroutines.flow.first

class PollWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {

        val preferencesRepository = PreferencesRepository.get()
        val photoRepository = PhotoRepository()

        val query = preferencesRepository.storedQuery.first()
        val lastId = preferencesRepository.lastResultId.first()

        if(query.isEmpty()) {
            Log.i("PollWorker", "No saved query, finishing early.")
            return Result.success()
        }

        return try {
            val items = photoRepository.searchPhotos(query)

            if (items.isNotEmpty()) {
                val newResultId = items.first().id
                if (newResultId == lastId) {
                    Log.i("PollWorker", "Still have the same result: $newResultId")
                } else {
                    Log.i("PollWorker", "Got a new result: $newResultId")
                    preferencesRepository.setLastResultId(newResultId)
                }
            }

            Result.success()
        } catch (ex: Exception) {
            Log.i("PollWorker", "Background update failed", ex)
            Result.failure()
        }
    }
}