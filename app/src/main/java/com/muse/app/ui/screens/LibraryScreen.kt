package com.muse.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muse.app.data.models.Track
import com.muse.app.ui.components.TrackRow
import com.muse.app.ui.theme.*
import com.muse.app.viewmodel.LibraryState
import com.muse.app.viewmodel.PlayerState

@Composable
fun LibraryScreen(
    playerState: PlayerState,
    libraryState: LibraryState,
    onTrackPlay: (Track, List<Track>) -> Unit,
    onLike: (Track) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Liked" to Icons.Default.Favorite, "History" to Icons.Default.History)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MuseBlack)
    ) {
        Text(
            "Library",
            color = MuseIvory,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 16.dp)
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MuseBlack,
            contentColor = MuseGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MuseGold
                )
            }
        ) {
            tabs.forEachIndexed { index, (title, icon) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) MuseGold else MuseIvoryDim,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    icon = {
                        Icon(
                            icon, title,
                            tint = if (selectedTab == index) MuseGold else MuseIvoryDim,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }

        val tracks = when (selectedTab) {
            0 -> libraryState.likedTracks
            else -> libraryState.history
        }

        if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (selectedTab == 0) "No liked tracks yet" else "No history yet",
                    color = MuseIvoryDim, fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 8.dp, top = 8.dp)) {
                itemsIndexed(tracks) { idx, track ->
                    TrackRow(
                        track = track,
                        index = idx,
                        isPlaying = playerState.currentTrack?.videoId == track.videoId,
                        isLiked = playerState.likedIds.contains(track.videoId),
                        onPlay = { onTrackPlay(track, tracks) },
                        onLike = { onLike(track) }
                    )
                }
            }
        }
    }
}
