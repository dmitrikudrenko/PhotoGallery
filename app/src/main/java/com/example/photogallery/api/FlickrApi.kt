package com.example.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchContent(): Response<FlickrResponse>

    @GET("services/rest/?method=flickr.photos.search")
    suspend fun searchContent(@Query("text") query: String): Response<FlickrResponse>

    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>
}