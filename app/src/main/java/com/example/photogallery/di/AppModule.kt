package com.example.photogallery.di

import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.PhotoInterceptor
import com.example.photogallery.api.ThumbnailLoader
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.ui.gallery.PhotoGalleryViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { createFlickrApi() }
    single { FlickrFetcher(get()) }
    viewModel { PhotoGalleryViewModel(androidApplication(), get()) }
}

private fun createFlickrApi(): FlickrApi {
    val client = OkHttpClient.Builder()
        .addInterceptor(PhotoInterceptor())
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.flickr.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(FlickrApi::class.java)
}