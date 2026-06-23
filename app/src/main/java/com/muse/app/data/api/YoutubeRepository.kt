package com.muse.app.data.api

import com.muse.app.BuildConfig
import com.muse.app.data.models.Track
import com.muse.app.data.models.YoutubeVideoDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object YoutubeRepository {

    private val api = RetrofitClient.youtubeApi
    private val apiKey = BuildConfig.YOUTUBE_API_KEY

    // Parse ISO 8601 duration PT4M33S → seconds
    private fun parseDuration(iso: String): Long {
        val regex = Regex("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
        val match = regex.find(iso) ?: return 0L
        val h = match.groupValues[1].toLongOrNull() ?: 0L
        val m = match.groupValues[2].toLongOrNull() ?: 0L
        val s = match.groupValues[3].toLongOrNull() ?: 0L
        return h * 3600 + m * 60 + s
    }

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s)
        else "%d:%02d".format(m, s)
    }

    // Parse "Artist - Title" from YouTube title
    private fun parseTitle(raw: String): Pair<String, String> {
        val cleaned = raw
            .replace(Regex("\\((Official|Audio|Video|Lyrics|HD|4K)[^)]*\\)", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\[(Official|Audio|Video|Lyrics|HD|4K)[^\\]]*\\]", RegexOption.IGNORE_CASE), "")
            .replace(Regex("ft\\..*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("feat\\..*", RegexOption.IGNORE_CASE), "")
            .trim()

        val separators = listOf(" - ", " – ", " — ", " | ")
        for (sep in separators) {
            val idx = cleaned.indexOf(sep)
            if (idx > 0) {
                return cleaned.substring(0, idx).trim() to cleaned.substring(idx + sep.length).trim()
            }
        }
        return "Unknown Artist" to cleaned
    }

    private fun videoDetailToTrack(item: YoutubeVideoDetail): Track {
        val (artist, title) = parseTitle(item.snippet.title)
        val secs = parseDuration(item.contentDetails?.duration ?: "")
        return Track(
            videoId = item.id,
            title = title,
            artist = if (artist == "Unknown Artist") item.snippet.channelTitle else artist,
            thumbnailUrl = item.snippet.thumbnails.getBest(),
            duration = formatDuration(secs),
            durationSeconds = secs
        )
    }

    suspend fun getTrending(): Result<List<Track>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTrending(apiKey = apiKey)
            val tracks = response.items?.map { videoDetailToTrack(it) } ?: emptyList()
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun search(query: String, pageToken: String? = null): Result<Pair<List<Track>, String?>> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Search for video IDs
                val searchResponse = api.search(query = query, apiKey = apiKey, pageToken = pageToken)
                val videoIds = searchResponse.items
                    ?.mapNotNull { it.id.videoId }
                    ?.joinToString(",") ?: return@withContext Result.success(emptyList<Track>() to null)

                if (videoIds.isEmpty()) return@withContext Result.success(emptyList<Track>() to null)

                // 2. Get full details (duration, etc.)
                val detailsResponse = api.getVideoDetails(ids = videoIds, apiKey = apiKey)
                val tracks = detailsResponse.items?.map { videoDetailToTrack(it) } ?: emptyList()

                Result.success(tracks to searchResponse.nextPageToken)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
