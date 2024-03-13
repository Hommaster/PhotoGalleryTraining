package com.example.photogallery.api

import retrofit2.http.GET

private const val API_KEY = "59837b3eb8c4d576208f5d0b03b89317"

interface FlickrApi {
    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
        "&api_key=$API_KEY" +
        "&format=json" +
        "&nojsoncallback=1" +
        "&extras=url_s"
    )
    suspend fun fetchPhotos(): FlickrResponse
}