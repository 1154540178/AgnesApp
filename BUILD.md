# AgnesApp — Android APK Build Instructions

This is a complete Android project for an Agnes AI client app with Chat, Image Generation, and Video Generation features.

## Build Requirements

- **Android Studio** (Arctic Fox or later)
- **JDK 17**
- **Gradle 8.2**

## Quick Start

1. Open `AgnesApp/` in Android Studio
2. Wait for Gradle sync to complete
3. Connect an Android device or start an emulator (API 24+)
4. Click **Run** (Shift+F10)

## Build APK Manually

```bash
cd AgnesApp
./gradlew assembleDebug    # Debug APK
./gradlew assembleRelease  # Release APK (needs signing config)
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`

## Features

- **Chat Fragment**: Real-time text conversation with Agnes AI (`agnes-2.0-flash`)
- **Image Fragment**: Text-to-image generation with size/count options (`agnes-image-2.1-flash`)
- **Video Fragment**: Text-to-video generation with polling for async completion (`agnes-video-v2.0`)

## API Configuration

Go to **Settings** (gear icon) to enter your Agnes AI API Key.

Default API endpoint: `https://api.agnes-ai.com/v1/`

## Project Structure

```
AgnesApp/
├── app/
│   ├── src/main/java/com/agnes/app/
│   │   ├── api/          — AgnesApiService, ApiClient, AgnesRepository
│   │   ├── model/        — Message data classes
│   │   ├── ui/           — Activities, Fragments, Adapters
│   │   └── util/         — Preferences, API constants
│   └── src/main/res/     — Layouts, drawables, strings, styles
├── build.gradle          — Project-level build config
├── settings.gradle       — Project settings
└── gradle.properties     — Gradle properties
```
