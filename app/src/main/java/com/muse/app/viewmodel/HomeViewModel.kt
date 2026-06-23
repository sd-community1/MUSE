package com.muse.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muse.app.data.api.YoutubeRepository
import com.muse.app.data.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val trending: List<Track> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init { loadTrending() }

    fun loadTrending() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            YoutubeRepository.getTrending().fold(
                onSuccess = { _state.value = HomeState(trending = it, isLoading = false) },
                onFailure = { _state.value = HomeState(isLoading = false, error = it.message) }
            )
        }
    }
}
