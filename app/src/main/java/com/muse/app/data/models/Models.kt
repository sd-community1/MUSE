package com.muse.app.data.models

data class YoutubeSearchResponse(
    val items: List<YoutubeVideoItem>? = emptyList(),
    val nextPageToken: String? = null
)

data class YoutubeVideoItem(
    val id: YoutubeVideoId,
    val snippet: YoutubeSnippet
)

data class YoutubeVideoId(
    val videoId: String? = null
)

data class YoutubeSnippet(
    val title: String = "",
    val channelTitle: String = "",
    val thumbnails: YoutubeThumbnails
)

data class YoutubeThumbnails(
    val default: YoutubeThumbnail? = null,
    val medium: YoutubeThumbnail? = null,
    val high: YoutubeThumbnail? = null,
    val maxres: YoutubeThumbnail? = null
) {
    fun getBest(): String =
        maxres?.url ?: high?.url ?: medium?.url ?: default?.url ?: ""
}

data class YoutubeThumbnail(
    val url: String = ""
)

data class YoutubeVideosResponse(
    val items: List<YoutubeVideoDetail>? = emptyList()
)

data class YoutubeVideoDetail(
    val id: String = "",
    val snippet: YoutubeSnippet,
    val contentDetails: YoutubeContentDetails? = null,
    val statistics: YoutubeStatistics? = null
)

data class YoutubeContentDetails(
    val duration: String = ""
)

data class YoutubeStatistics(
    val viewCount: String? = null
)

// App-level track model (parsed, clean)
data class Track(
    val videoId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String,
    val duration: String = "",
    val durationSeconds: Long = 0L,
    val album: String? = null,
    val isLiked: Boolean = false
)
