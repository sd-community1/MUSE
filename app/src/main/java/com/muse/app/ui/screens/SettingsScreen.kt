package com.muse.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muse.app.ui.theme.*

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MuseBlack)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Settings",
            color = MuseIvory,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
        )

        Spacer(Modifier.height(8.dp))

        SettingsGroup(title = "Playback") {
            SettingsToggle(
                icon = Icons.Default.AudioFile,
                title = "High Quality Audio",
                subtitle = "Use highest available quality",
                checked = true,
                onToggle = {}
            )
            SettingsToggle(
                icon = Icons.Default.MusicNote,
                title = "Gapless Playback",
                subtitle = "No silence between tracks",
                checked = true,
                onToggle = {}
            )
            SettingsToggle(
                icon = Icons.Default.Shuffle,
                title = "Autoplay",
                subtitle = "Continue playing similar tracks",
                checked = false,
                onToggle = {}
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsGroup(title = "About") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Version",
                subtitle = "MUSE 1.0.0"
            )
            SettingsItem(
                icon = Icons.Default.Code,
                title = "GitHub",
                subtitle = "sd-community1/MUSE"
            )
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            title,
            color = MuseGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MuseBlackSurf)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, title, tint = MuseGold, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MuseIvory, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = MuseIvoryDim, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MuseBlack,
                checkedTrackColor = MuseGold,
                uncheckedThumbColor = MuseIvoryDim,
                uncheckedTrackColor = MuseBorder
            )
        )
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, title, tint = MuseGold, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, color = MuseIvory, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = MuseIvoryDim, fontSize = 12.sp)
        }
    }
}
