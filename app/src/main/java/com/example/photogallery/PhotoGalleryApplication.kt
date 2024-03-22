package com.example.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.photogallery.constance.Constance
import com.example.photogallery.repository.PreferencesRepository


class PhotoGalleryApplication: Application() {

    // need add this Application on Manifest under name

    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constance.NOTIFICATION_CHANNEL_ID, name, importance)

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }
}