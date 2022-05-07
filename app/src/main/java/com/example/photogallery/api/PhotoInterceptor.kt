package com.example.photogallery.api

import okhttp3.Interceptor
import okhttp3.Response

class PhotoInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = originalRequest
            .url()
            .newBuilder()
            .addQueryParameter("api_key", "60b20c9ea97be4891f2d4cd54e1e892d")
            .addQueryParameter("format", "json")
            .addQueryParameter("nojsoncallback", "1")
            .addQueryParameter("extras", "url_s")
            .build()
        val request = originalRequest.newBuilder().url(newUrl).build()
        return chain.proceed(request)
    }
}