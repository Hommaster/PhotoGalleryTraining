package com.example.photogallery

import android.app.Application
import com.example.photogallery.repository.PreferencesRepository

class PhotoGalleryApplication: Application() {

    // need add this Application on Manifest under name

    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
    }
}