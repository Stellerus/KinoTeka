# KinoTeKa — Movie & Series Tracker

An Android app for tracking movies and series with persistent storage in a local JSON file.

## Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- Kotlin 1.9+
- Gradle 8.x
- Minimum Android version: API 26 (Android 8.0)
- Target version: API 34

## Project Structure
- `app/src/main/java/com/example/kinoteka/` — source code
  - `MainActivity.kt` — main screen with list
  - `DetailActivity.kt` — detail screen (edge‑to‑edge)
  - `model/` — data models (Movie, Series)
  - `repository/` — repository for JSON storage
  - `adapter/` — RecyclerView adapter
- `app/src/main/res/layout/` — screen layouts

## Build & Run
1. Open the project in Android Studio
2. Sync Gradle
3. Run on a device or emulator with API ≥ 26

## Note
All data is saved to the `kinoteka.json` file in the app's internal storage.
Uninstalling the app removes the file.
