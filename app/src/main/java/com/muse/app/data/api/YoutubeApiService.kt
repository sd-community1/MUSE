package com.muse.app.data.api

import com.muse.app.data.models.YoutubeSearchResponse
import com.muse.app.data.models.YoutubeVideosResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {

    @GET("search")
    suspend fun search(
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("videoCategoryId") category: String = "10",
        @Query("maxResults") maxResults: Int = 25,
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("pageToken") pageToken: String? = null
    ): YoutubeSearchResponse

    @GET("videos")
    suspend fun getTrending(
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("chart") chart: String = "mostPopular",
        @Query("videoCategoryId") category: String = "10",
        @Query("maxResults") maxResults: Int = 25,
        @Query("regionCode") regionCode: String = "US",
        @Query("key") apiKey: String
    ): YoutubeVideosResponse

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "snippet,contentDetails,statistics",
        @Query("id") ids: String,
        @Query("key") apiKey: String
    ): YoutubeVideosResponse
}
