package com.muse.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.muse.app.ui.theme.*
import com.muse.app.viewmodel.PlayerState
import com.muse.app.viewmodel.RepeatMode

fun Long.toTimeString(): String {
    if (this <= 0L) return "0:00"
    val totalSec = this / 1000L
    val m = totalSec / 60
    val s = totalSec % 60
    return "%d:%02d".format(m, s)
}

@Composable
fun FullPlayerScreen(
    state: PlayerState,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onLike: () -> Unit,
    onClose: () -> Unit
) {
    val track = state.currentTrack ?: return
    val progress = if (state.durationMs > 0)
        (state.currentTimeMs.toFloat() / state.durationMs.toFloat()).coerceIn(0f, 1f)
    else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MuseBlack)
    ) {
        // Blurred background art
        AsyncImage(
            model = track.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .then(Modifier)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MuseBlack.copy(alpha = 0.85f), MuseBlack.copy(alpha = 0.97f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.KeyboardArrowDown, "Close",
                        tint = MuseIvory, modifier = Modifier.size(28.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NOW PLAYING", color = MuseIvoryDim,
                        fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, "More",
                        tint = MuseIvory, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            // Album art
            AsyncImage(
                model = track.thumbnailUrl,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(Modifier.height(36.dp))

            // Title + artist + like
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        color = MuseIvory,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = track.artist,
                        color = MuseIvoryDim,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onLike) {
                    Icon(
                        imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (track.isLiked) MuseGold else MuseIvoryDim,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Seek bar
            Slider(
                value = progress,
                onValueChange = onSeek,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MuseGold,
                    activeTrackColor = MuseGold,
                    inactiveTrackColor = MuseBorder
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(state.currentTimeMs.toTimeString(),
                    color = MuseIvoryDim, fontSize = 11.sp)
                Text(state.durationMs.toTimeString(),
                    color = MuseIvoryDim, fontSize = 11.sp)
            }

            Spacer(Modifier.height(20.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        Icons.Default.Shuffle, "Shuffle",
                        tint = if (state.shuffle) MuseGold else MuseIvoryDim,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(
                    onClick = onPrev,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.SkipPrevious, "Prev",
                        tint = MuseIvory, modifier = Modifier.size(34.dp))
                }
                // Play/Pause big button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MuseGold),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onTogglePlay) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = MuseBlack,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.SkipNext, "Next",
                        tint = MuseIvory, modifier = Modifier.size(34.dp))
                }
                IconButton(onClick = onCycleRepeat) {
                    Icon(
                        imageVector = when (state.repeat) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (state.repeat != RepeatMode.OFF) MuseGold else MuseIvoryDim,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
