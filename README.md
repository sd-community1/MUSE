# MUSE — Android App

A premium music streaming Android app powered by YouTube Data API v3.

## Features
- 🎵 YouTube Music library search
- 🔥 Trending tracks
- ❤️ Liked songs & history
- 🎨 Black & Gold premium UI (Material 3)
- 🔊 Background audio playback via YouTube IFrame API

## Build via GitHub Actions (from phone, no PC needed)

1. Create a new repository on GitHub named **MUSE**
2. Upload all these files to the repo
3. Go to **Actions** tab → the build starts automatically
4. Wait ~5 minutes → click the workflow run → scroll to **Artifacts**
5. Download **MUSE-debug-apk**
6. Install on your Android device (enable "Install unknown apps" first)

## Manual trigger
Go to Actions → "Build MUSE APK" → "Run workflow" button

## API Key
Already embedded in `app/build.gradle.kts` via `BuildConfig.YOUTUBE_API_KEY`.
