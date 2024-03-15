package com.example.photogallery.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

private const val API_KEY = "59837b3eb8c4d576208f5d0b03b89317"

class PhotoInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newUrl: HttpUrl = originalRequest.url.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format", "json")
            .addQueryParameter("nojsoncallback", "1")
            .addQueryParameter("extras", "url_s")
            .addQueryParameter("safesearch", "1")
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}