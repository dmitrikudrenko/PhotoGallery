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
    single { createFlickrApi(createRetrofit(createClient())) }
    single { FlickrFetcher(get()) }
    viewModel { PhotoGalleryViewModel(androidApplication(), get()) }
}

private fun createFlickrApi(retrofit: Retrofit): FlickrApi {
    return retrofit.create()
}

inline fun <reified T : Any> Retrofit.create(): T {
    return create(T::class.java)
}

private fun createRetrofit(client: OkHttpClient) = Retrofit.Builder()
    .baseUrl("https://api.flickr.com/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private fun createClient() = OkHttpClient.Builder()
    .addInterceptor(PhotoInterceptor())
    .build()
