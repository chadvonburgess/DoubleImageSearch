package com.cvb.myapplication.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface ImgurAPI {

    @Headers("Authorization: Client-ID {clientID}") // move eventually
    @GET("https://api.imgur.com/3/gallery/search/")
    suspend fun getImageListBasic(@Query("q_any") query: String): List<String>

    @Headers("Authorization: Client-ID {clientID}") // move eventually
    @GET("https://api.imgur.com/3/gallery/search/{sort}/{window}/{page}")
    suspend fun getImageListInclusive(
         @Path("sort") sort: String = "time",
         @Path("window") window: String = "all",
         @Path("page") page: Int = 1,
         @Query("q_any") searchQuery: String
         ): ImgurResponse
}

data class GalleryObject(
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: Array<ImgurImage>
)

data class ImgurImage(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("link") val link: String
)

data class ImgurResponse(
    @SerializedName("data") val data : Array<GalleryObject>
)
