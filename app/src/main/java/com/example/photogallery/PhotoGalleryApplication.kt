package com.example.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.PhotoInterceptor
import com.example.photogallery.di.appModule
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhotoGalleryApplication: Application() {
    lateinit var koinApplication: KoinApplication

    override fun onCreate() {
        super.onCreate()

        koinApplication = startKoin {
            androidLogger()
            androidContext(this@PhotoGalleryApplication)
            modules(appModule)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                "New photos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL = "new_photos"
    }
}