package com.example.photogallery.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.WorkerThread
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.FlickrResponse
import retrofit2.Response

private const val TAG = "FlickrFetcher"

class FlickrFetcher(private val flickrApi: FlickrApi) {

    suspend fun fetchPhotos(): List<GalleryItem> {
        return fetchMetadata(::fetchContent)
    }

    suspend fun fetchContent() = flickrApi.fetchContent()

    suspend fun searchPhotos(query: String): List<GalleryItem> {
        return fetchMetadata { searchContent(query) }
    }

    suspend fun searchContent(query: String) = flickrApi.searchContent(query)

    private suspend fun fetchMetadata(block: suspend () -> Response<FlickrResponse>): List<GalleryItem> {
        val response = block.invoke()
        return if (response.isSuccessful) {
            response.body()?.photos?.galleryItems?.filter { it.url.isNotBlank() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    @WorkerThread
    fun fetchPhotoContent(url: String): Bitmap? {
        val body = flickrApi.fetchUrlBytes(url).execute()
        return body.body()?.byteStream()?.use { BitmapFactory.decodeStream(it) }
    }
}