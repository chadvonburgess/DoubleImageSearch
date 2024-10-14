package com.cvb.myapplication.api

import retrofit2.http.GET
import retrofit2.http.Query

// https://api.mediastack.com/v1/news?access_key=050c28cd5ed2137ab3a4606dc152ee9e&keywords=tennis&countries=us,gb,de


private interface BlogSearchService {
    companion object {
        private val access = "050c28cd5ed2137ab3a4606dc152ee9e"
    }

    @GET("v1/news") // GET annotation for the API endpoint
    suspend fun searchBlogs(
        @Query("access_key") accessKey: String = access,
        @Query("keywords") keywords: String?,
        @Query("countries") countries: String?
    ): BlogSearchResponse
}

data class BlogSearchResponse(
    val pagination: Pagination,
    val data: List<BlogData>
)

data class Pagination(
    val limit: Int,
    val offset: Int,
    val count: Int,
    val total: Int
)

data class BlogData(
    val author: String?,
    val title: String,
    val description: String,
    val url: String,
    val source: String,
    val image: String?,
    val category: String,
    val language: String,
    val country: String,
    val published_at: String
)