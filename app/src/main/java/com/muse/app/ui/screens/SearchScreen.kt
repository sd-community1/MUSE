package com.muse.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.muse.app.data.models.Track
import com.muse.app.ui.components.TrackRow
import com.muse.app.ui.theme.*
import com.muse.app.viewmodel.PlayerState
import com.muse.app.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    playerState: PlayerState,
    onTrackPlay: (Track, List<Track>) -> Unit,
    onLike: (Track) -> Unit,
    searchVm: SearchViewModel = viewModel()
) {
    val state by searchVm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MuseBlack)
    ) {
        // Search bar header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MuseBlack)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                "Search", color = MuseIvory,
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            OutlinedTextField(
                value = state.query,
                onValueChange = { searchVm.onQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Songs, artists, albums…", color = MuseIvoryDim, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, "Search", tint = MuseIvoryDim)
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { searchVm.onQueryChange("") }) {
                            Icon(Icons.Default.Close, "Clear", tint = MuseIvoryDim)
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MuseBlackSurf,
                    unfocusedContainerColor = MuseBlackSurf,
                    focusedBorderColor = MuseGold,
                    unfocusedBorderColor = MuseBorder,
                    cursorColor = MuseGold,
                    focusedTextColor = MuseIvory,
                    unfocusedTextColor = MuseIvory
                ),
                singleLine = true
            )
        }

        // Results
        when {
            state.query.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Search, "Search",
                            tint = MuseGold.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Search MUSE", color = MuseIvory,
                            fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Find any song, artist, or album",
                            color = MuseIvoryDim, fontSize = 13.sp)
                    }
                }
            }

            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MuseGold, modifier = Modifier.size(36.dp))
                }
            }

            state.results.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results for \"${state.query}\"",
                        color = MuseIvoryDim, fontSize = 14.sp)
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    itemsIndexed(state.results) { idx, track ->
                        TrackRow(
                            track = track,
                            index = idx,
                            isPlaying = playerState.currentTrack?.videoId == track.videoId,
                            isLiked = playerState.likedIds.contains(track.videoId),
                            onPlay = { onTrackPlay(track, state.results) },
                            onLike = { onLike(track) }
                        )
                        if (idx == state.results.lastIndex && state.nextPageToken != null) {
                            LaunchedEffect(idx) { searchVm.loadMore() }
                        }
                    }
                }
            }
        }
    }
}
