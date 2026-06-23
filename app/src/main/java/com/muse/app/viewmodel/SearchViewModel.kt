package com.muse.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muse.app.data.api.YoutubeRepository
import com.muse.app.data.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val results: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val nextPageToken: String? = null
)

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _state.value = _state.value.copy(query = query, results = emptyList())
        searchJob?.cancel()
        if (query.isBlank()) return
        searchJob = viewModelScope.launch {
            delay(350) // debounce
            _state.value = _state.value.copy(isLoading = true, error = null)
            YoutubeRepository.search(query).fold(
                onSuccess = { (tracks, next) ->
                    _state.value = _state.value.copy(
                        results = tracks,
                        isLoading = false,
                        nextPageToken = next
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(isLoading = false, error = it.message)
                }
            )
        }
    }

    fun loadMore() {
        val token = _state.value.nextPageToken ?: return
        val q = _state.value.query.ifBlank { return }
        viewModelScope.launch {
            YoutubeRepository.search(q, token).fold(
                onSuccess = { (tracks, next) ->
                    _state.value = _state.value.copy(
                        results = _state.value.results + tracks,
                        nextPageToken = next
                    )
                },
                onFailure = {}
            )
        }
    }
}
