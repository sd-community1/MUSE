package com.muse.app.viewmodel

import androidx.lifecycle.ViewModel
import com.muse.app.data.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LibraryState(
    val likedTracks: List<Track> = emptyList(),
    val history: List<Track> = emptyList()
)

class LibraryViewModel : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()

    fun addToHistory(track: Track) {
        val current = _state.value.history.toMutableList()
        current.removeAll { it.videoId == track.videoId }
        current.add(0, track)
        _state.value = _state.value.copy(history = current.take(50))
    }

    fun toggleLike(track: Track) {
        val liked = _state.value.likedTracks.toMutableList()
        if (liked.any { it.videoId == track.videoId }) {
            liked.removeAll { it.videoId == track.videoId }
        } else {
            liked.add(0, track)
        }
        _state.value = _state.value.copy(likedTracks = liked)
    }

    fun isLiked(videoId: String) = _state.value.likedTracks.any { it.videoId == videoId }
}
