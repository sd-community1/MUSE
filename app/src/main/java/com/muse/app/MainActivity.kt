package com.muse.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.muse.app.data.models.Track
import com.muse.app.ui.screens.*
import com.muse.app.ui.components.MiniPlayer
import com.muse.app.ui.theme.*
import com.muse.app.viewmodel.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home     : Screen("home",     "Home",     Icons.Default.Home)
    object Search   : Screen("search",   "Search",   Icons.Default.Search)
    object Library  : Screen("library",  "Library",  Icons.Default.LibraryMusic)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MuseTheme {
                MuseApp(
                    onSetupWebView = { wv -> webView = wv },
                    onPlay  = { id  -> webView?.evaluateJavascript("playVideo('$id')", null) },
                    onPause = { webView?.evaluateJavascript("pauseVideo()", null) },
                    onSeek  = { ms  -> webView?.evaluateJavascript("seekTo($ms)", null) }
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MuseApp(
    onSetupWebView: (WebView) -> Unit,
    onPlay: (String) -> Unit,
    onPause: () -> Unit,
    onSeek: (Long) -> Unit
) {
    val playerVm:  PlayerViewModel  = viewModel()
    val libraryVm: LibraryViewModel = viewModel()

    val playerState by playerVm.state.collectAsState()
    val libraryState by libraryVm.state.collectAsState()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showFullPlayer by remember { mutableStateOf(false) }

    val screens = listOf(Screen.Home, Screen.Search, Screen.Library, Screen.Settings)

    // Wire VM callbacks to WebView
    LaunchedEffect(Unit) {
        playerVm.onPlayRequest  = { id -> onPlay(id) }
        playerVm.onPauseRequest = { onPause() }
        playerVm.onSeekRequest  = { ms -> onSeek(ms) }
    }

    fun handlePlay(track: Track, queue: List<Track>) {
        playerVm.setTrack(track, queue)
        libraryVm.addToHistory(track)
    }

    fun handleLike(track: Track) {
        playerVm.toggleLike(track.videoId)
        libraryVm.toggleLike(track)
    }

    Box(modifier = Modifier.fillMaxSize().background(MuseBlack)) {
        // Hidden YouTube IFrame WebView (drives all audio)
        HiddenYouTubeWebView(
            onSetupWebView = onSetupWebView,
            onProgress = { current, duration ->
                playerVm.updateProgress(current, duration)
            },
            onEnded = { playerVm.onTrackEnded() }
        )

        // Main scaffold
        Column(modifier = Modifier.fillMaxSize()) {
            // Content area
            Box(modifier = Modifier.weight(1f)) {
                when (currentScreen) {
                    Screen.Home     -> HomeScreen(
                        playerState = playerState,
                        onTrackPlay = ::handlePlay,
                        onLike = ::handleLike
                    )
                    Screen.Search   -> SearchScreen(
                        playerState = playerState,
                        onTrackPlay = ::handlePlay,
                        onLike = ::handleLike
                    )
                    Screen.Library  -> LibraryScreen(
                        playerState = playerState,
                        libraryState = libraryState,
                        onTrackPlay = ::handlePlay,
                        onLike = ::handleLike
                    )
                    Screen.Settings -> SettingsScreen()
                }
            }

            // Mini player
            if (playerState.currentTrack != null) {
                MiniPlayer(
                    state = playerState,
                    onTogglePlay = { playerVm.togglePlayPause() },
                    onNext = { playerVm.nextTrack() },
                    onPrev = { playerVm.prevTrack() },
                    onExpand = { showFullPlayer = true }
                )
            }

            // Bottom navigation
            NavigationBar(
                containerColor = MuseBlackSurf,
                tonalElevation = 0.dp
            ) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(screen.icon, screen.label, modifier = Modifier.size(22.dp))
                        },
                        label = { Text(screen.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MuseGold,
                            selectedTextColor = MuseGold,
                            unselectedIconColor = MuseIvoryDim,
                            unselectedTextColor = MuseIvoryDim,
                            indicatorColor = MuseGold.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }

        // Full player overlay
        AnimatedVisibility(
            visible = showFullPlayer,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            FullPlayerScreen(
                state = playerState,
                onTogglePlay = { playerVm.togglePlayPause() },
                onNext = { playerVm.nextTrack() },
                onPrev = { playerVm.prevTrack() },
                onSeek = { playerVm.seekTo(it) },
                onToggleShuffle = { playerVm.toggleShuffle() },
                onCycleRepeat = { playerVm.cycleRepeat() },
                onLike = {
                    playerState.currentTrack?.let { t ->
                        playerVm.toggleLike(t.videoId)
                        libraryVm.toggleLike(t)
                    }
                },
                onClose = { showFullPlayer = false }
            )
        }
    }
}

// ── Hidden WebView that drives actual YouTube audio playback ──────────────────
// Uses YouTube IFrame API embedded in a local HTML page.
// The WebView is 1x1dp invisible — only its audio output matters.
// A JavascriptInterface bridges JS → Kotlin for progress/ended events.
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HiddenYouTubeWebView(
    onSetupWebView: (WebView) -> Unit,
    onProgress: (Long, Long) -> Unit,
    onEnded: () -> Unit
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()

                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun onProgress(currentMs: Long, durationMs: Long) {
                            onProgress(currentMs, durationMs)
                        }

                        @JavascriptInterface
                        fun onEnded() {
                            onEnded()
                        }
                    },
                    "MuseBridge"
                )

                val html = buildYouTubePlayerHtml()
                loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
                onSetupWebView(this)
            }
        },
        modifier = Modifier.size(1.dp)
    )
}

fun buildYouTubePlayerHtml(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width">
<style>body{margin:0;background:#000;}</style>
</head>
<body>
<div id="player"></div>
<script>
var player;
var progressInterval;

function onYouTubeIframeAPIReady() {
    player = new YT.Player('player', {
        height: '1', width: '1',
        playerVars: { autoplay: 0, controls: 0, playsinline: 1, rel: 0 },
        events: {
            onStateChange: function(e) {
                if (e.data === YT.PlayerState.ENDED) {
                    MuseBridge.onEnded();
                }
                if (e.data === YT.PlayerState.PLAYING) {
                    clearInterval(progressInterval);
                    progressInterval = setInterval(function() {
                        var cur = Math.round(player.getCurrentTime() * 1000);
                        var dur = Math.round(player.getDuration() * 1000);
                        MuseBridge.onProgress(cur, dur);
                    }, 500);
                } else {
                    clearInterval(progressInterval);
                }
            }
        }
    });
}

function playVideo(videoId) {
    if (player && player.loadVideoById) {
        player.loadVideoById(videoId);
    }
}
function pauseVideo() {
    if (player) player.pauseVideo();
}
function resumeVideo() {
    if (player) player.playVideo();
}
function seekTo(ms) {
    if (player) player.seekTo(ms / 1000, true);
}
</script>
<script src="https://www.youtube.com/iframe_api"></script>
</body>
</html>
""".trimIndent()
