package com.example.photogallery.scheduler

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.photogallery.PhotoGalleryApplication
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.FlickrResponse
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.QueryStorage
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

private const val TAG = "PhotoWorker"

class PhotoWorker(context: Context, wp: WorkerParameters): Worker(context, wp), KoinComponent {
    private val fetcher by inject<FlickrFetcher>()

    override fun doWork(): Result {
        return runBlocking {
            val query = QueryStorage.getQuery(applicationContext)
            val response: Response<FlickrResponse> = if (query.isBlank()) {
                fetcher.fetchContent()
            } else {
                fetcher.searchContent(query)
            }
            if (response.isSuccessful) {
                val id = response.body()?.photos?.galleryItems?.first()?.id
                id?.let {
                    val lastResultId = QueryStorage.getLastResultId(applicationContext)
                    if (it != lastResultId) {
                        QueryStorage.saveLastResultId(applicationContext, it)
                        applicationContext.sendOrderedBroadcast(
                            Intent(
                                ACTION_NOTIFICATION_NEW_PHOTOS
                            ), PERMISSION
                        )
                        Log.d(TAG, "YEAH!!! NEW PHOTOS")
                    } else {
                        Log.d(TAG, "NO NEW PHOTOS :(")
                    }
                }
                Result.success()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val PERMISSION = "com.example.photogallery.permission"
        const val ACTION_NOTIFICATION_NEW_PHOTOS = "action_notification_new_photos"
    }
}