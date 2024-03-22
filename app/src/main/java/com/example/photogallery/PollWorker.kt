package com.example.photogallery

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.photogallery.constance.Constance
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
                    notifyUser()
                    preferencesRepository.setLastResultId(newResultId)
                }
            }

            Result.success()
        } catch (ex: Exception) {
            Log.i("PollWorker", "Background update failed", ex)
            Result.failure()
        }
    }

    private fun notifyUser() {

        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val resources = context.resources

        val notification = NotificationCompat
            .Builder(context, Constance.NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.new_pictures_title))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_pictures_title))
            .setContentText(resources.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        // in sdk_version Tiramisu and older need to request permission for POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(context).notify(0, notification)
            }
        } else {
            NotificationManagerCompat.from(context).notify(0, notification)
        }

    }
}