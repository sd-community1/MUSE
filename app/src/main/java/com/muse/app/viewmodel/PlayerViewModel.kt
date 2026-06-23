package com.muse.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muse.app.data.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val durationMs: Long = 0L,
    val queue: List<Track> = emptyList(),
    val currentIndex: Int = -1,
    val shuffle: Boolean = false,
    val repeat: RepeatMode = RepeatMode.OFF,
    val volume: Float = 1f,
    val likedIds: Set<String> = emptySet()
)

enum class RepeatMode { OFF, ALL, ONE }

class PlayerViewModel : ViewModel() {

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    // Callback that the UI layer sets to actually trigger WebView playback
    var onPlayRequest: ((videoId: String) -> Unit)? = null
    var onPauseRequest: (() -> Unit)? = null
    var onSeekRequest: ((ms: Long) -> Unit)? = null

    fun setTrack(track: Track, queue: List<Track> = emptyList()) {
        val q = if (queue.isEmpty()) listOf(track) else queue
        val idx = q.indexOfFirst { it.videoId == track.videoId }
        _state.value = _state.value.copy(
            currentTrack = track,
            isPlaying = true,
            currentTimeMs = 0L,
            durationMs = track.durationSeconds * 1000L,
            queue = q,
            currentIndex = idx.coerceAtLeast(0)
        )
        onPlayRequest?.invoke(track.videoId)
    }

    fun togglePlayPause() {
        val playing = !_state.value.isPlaying
        _state.value = _state.value.copy(isPlaying = playing)
        if (playing) onPlayRequest?.invoke(_state.value.currentTrack?.videoId ?: return)
        else onPauseRequest?.invoke()
    }

    fun seekTo(fraction: Float) {
        val ms = (fraction * _state.value.durationMs).toLong()
        _state.value = _state.value.copy(currentTimeMs = ms)
        onSeekRequest?.invoke(ms)
    }

    fun updateProgress(currentMs: Long, durationMs: Long) {
        _state.value = _state.value.copy(
            currentTimeMs = currentMs,
            durationMs = if (durationMs > 0) durationMs else _state.value.durationMs
        )
    }

    fun nextTrack() {
        val s = _state.value
        if (s.queue.isEmpty()) return
        val nextIdx = if (s.shuffle) (0 until s.queue.size).random()
        else (s.currentIndex + 1) % s.queue.size
        setTrack(s.queue[nextIdx], s.queue)
    }

    fun prevTrack() {
        val s = _state.value
        if (s.currentTimeMs > 3000L) { seekTo(0f); return }
        if (s.queue.isEmpty()) return
        val prevIdx = if (s.currentIndex > 0) s.currentIndex - 1 else s.queue.size - 1
        setTrack(s.queue[prevIdx], s.queue)
    }

    fun toggleShuffle() = _state.value.let {
        _state.value = it.copy(shuffle = !it.shuffle)
    }

    fun cycleRepeat() = _state.value.let {
        val next = when (it.repeat) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _state.value = it.copy(repeat = next)
    }

    fun toggleLike(videoId: String) {
        val liked = _state.value.likedIds.toMutableSet()
        if (liked.contains(videoId)) liked.remove(videoId) else liked.add(videoId)
        _state.value = _state.value.copy(likedIds = liked)
        // Update current track isLiked
        _state.value.currentTrack?.let { t ->
            if (t.videoId == videoId) {
                _state.value = _state.value.copy(
                    currentTrack = t.copy(isLiked = liked.contains(videoId))
                )
            }
        }
    }

    fun onTrackEnded() {
        when (_state.value.repeat) {
            RepeatMode.ONE -> onPlayRequest?.invoke(_state.value.currentTrack?.videoId ?: return)
            else -> nextTrack()
        }
    }
}
