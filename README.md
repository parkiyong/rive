# Rive Android & Android TV App

A 100% native Android application built with modern Kotlin and Jetpack Compose, delivering the complete streaming experience of **[rivestream.ru](https://rivestream.ru/)** across **Mobile, Tablet, and Android TV / Google TV**.

---

## Features

- **Native Architecture:** Reverse-engineers backend TMDB and Rive stream scrapers for direct HLS/MP4 playback rather than wrapping a web browser.
- **Dual Form Factor Support:**
  - **Mobile & Tablet:** Touch-based gesture controls, bottom navigation, and Picture-in-Picture (PiP).
  - **Android TV / Google TV:** 10-foot UI with D-pad remote focus highlights, remote shortcuts, and Leanback launcher support.
- **Media3 / ExoPlayer Engine:**
  - Hardware-accelerated HLS (`.m3u8`) and progressive MP4 streaming.
  - Multi-language WebVTT & SubRip subtitle track selection.
  - Automatic watch progress resume (down to the exact second).
- **Multi-Provider Scraper Resolver:**
  - Automatically queries high-reliability providers (`citadel`, `primevids`, `apollo`, `vanguard`, `apogee`, `lumen`, etc.) with automated fallback.
- **Local-First & Offline Ready:**
  - Standalone Room Database for watch progress history and bookmarks (no external accounts or login required).
- **Cinematic Theming:**
  - Recreates Rive's color presets: *Obsidian Luxury* (gold/dark), *Midnight Blue*, and *Aurora Glow*.
  - Configurable custom TMDB API key in Settings.

---

## Project Structure

```text
rive-android/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml       # Leanback launcher + Mobile launcher + PiP
│   │   ├── java/app/rive/
│   │   │   ├── RiveApp.kt            # App initialization & DB setup
│   │   │   ├── MainActivity.kt       # Activity entry point
│   │   │   ├── data/
│   │   │   │   ├── api/              # Retrofit clients (TMDB & Rive Scraper)
│   │   │   │   ├── model/            # Data models (TMDB items, stream sources)
│   │   │   │   ├── local/            # Room Database, DAOs, Entities
│   │   │   │   └── repository/       # MovieRepository, StreamRepository, HistoryRepository
│   │   │   ├── player/
│   │   │   │   └── Media3PlayerManager.kt  # AndroidX Media3 ExoPlayer controller
│   │   │   └── ui/
│   │   │       ├── theme/            # Rive Theme Presets (Obsidian, Midnight, Aurora)
│   │   │       ├── components/       # MediaCard, HeroBanner, MediaRow
│   │   │       ├── navigation/       # NavGraph & Screen routes
│   │   │       └── screens/
│   │   │           ├── home/         # Home catalog screen
│   │   │           ├── detail/       # Movie/TV detail & episode selector
│   │   │           ├── player/       # Fullscreen video player with controls
│   │   │           ├── search/       # Real-time search screen
│   │   │           └── settings/     # Theme & API key settings
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml            # Version catalog (AGP 8.5, Kotlin 2.0, Media3 1.4, Compose)
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

## Building & Running

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer / Koala / Ladybug
- JDK 17
- Android SDK API 35 (minSdk 26)

### In Android Studio
1. Open Android Studio and select **Open**.
2. Navigate to the `rive-android` folder.
3. Allow Gradle to sync dependencies.
4. Select a target device (Android Phone, Tablet, or Android TV Emulator) and click **Run** (`Shift + F10`).

### Via Command Line
To assemble the debug APK:
```bash
./gradlew assembleDebug
```
The generated APK will be located at:
```text
app/build/outputs/apk/debug/app-debug.apk
```

