package com.muse.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.muse.app.data.models.Track
import com.muse.app.ui.components.TrackGridCard
import com.muse.app.ui.components.TrackRow
import com.muse.app.ui.theme.*
import com.muse.app.viewmodel.HomeViewModel
import com.muse.app.viewmodel.PlayerState
import java.util.Calendar

@Composable
fun HomeScreen(
    playerState: PlayerState,
    onTrackPlay: (Track, List<Track>) -> Unit,
    onLike: (Track) -> Unit,
    homeVm: HomeViewModel = viewModel()
) {
    val state by homeVm.state.collectAsState()

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11  -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else      -> "Good Evening"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MuseBlack),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Hero section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MuseBlackSurf)
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "MUSE",
                        color = MuseGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = greeting + ".",
                        color = MuseIvory,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Your soundtrack awaits.",
                        color = MuseIvoryDim,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Trending heading
        item {
            SectionHeader(title = "Trending Now")
        }

        // Trending grid (horizontal scroll)
        item {
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MuseGold, modifier = Modifier.size(32.dp))
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(state.trending.take(8)) { _, track ->
                        Box(modifier = Modifier.width(150.dp)) {
                            TrackGridCard(
                                track = track,
                                isPlaying = playerState.currentTrack?.videoId == track.videoId,
                                onPlay = { onTrackPlay(track, state.trending) }
                            )
                        }
                    }
                }
            }
        }

        // Moods section
        item { SectionHeader(title = "Browse by Mood") }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val moods = listOf("Focus", "Chill", "Worship", "Energy", "Sleep", "Workout")
                itemsIndexed(moods) { _, mood ->
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MuseBlackCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mood, color = MuseIvory, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Top Hits list
        item { SectionHeader(title = "Top Hits") }

        if (!state.isLoading) {
            itemsIndexed(state.trending.drop(8).take(10)) { idx, track ->
                TrackRow(
                    track = track,
                    index = idx,
                    isPlaying = playerState.currentTrack?.videoId == track.videoId,
                    isLiked = playerState.likedIds.contains(track.videoId),
                    onPlay = { onTrackPlay(track, state.trending) },
                    onLike = { onLike(track) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = MuseIvory,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp, end = 20.dp)
    )
}
