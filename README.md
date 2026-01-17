# Music Player App with Authentication & Database

This project is an Android Music Player application that includes User Authentication, Playlist management, and a local Database architecture.

## Features

*   **User Authentication:** Sign Up and Sign In functionality.
*   **Database:** Local SQLite database using **Room Persistence Library**.
*   **Profile Management:** Edit username/password, view account details.
*   **Playlists:** Create custom playlists, add songs from the library (Long-press), and play them.
*   **Music Library:** Browse and play songs.

## Getting Started

### Prerequisites

*   **Android Studio** (Latest version recommended).
*   **Java Development Kit (JDK) 11**.
*   Android SDK (API 34/35).

### How to Run

1.  **Open Project:**
    *   Open Android Studio.
    *   Select `File > Open...` and choose the root directory of this project.

2.  **Sync Gradle:**
    *   Wait for Android Studio to download dependencies and sync the project.
    *   If prompted, update the Android Gradle Plugin to the suggested version.

3.  **Run the App:**
    *   Connect an Android device or create an Emulator (AVD).
    *   Click the green **Run** button (Shift+F10).

## Database Setup

**Good news! You don't need to do anything manually.**

The application is designed to handle the database setup automatically:

1.  **Auto-Creation:** The Room database (`music_player_database`) is created automatically when you launch the app for the first time.
2.  **Auto-Seeding:** On the very first run, the app checks if the song library is empty. If it is, it automatically populates the database with the default songs found in `MusicLibrary.java`.

**Note:** If you want to reset the database completely, simply **uninstall the app** from your device/emulator and run it again.

## How to Use

1.  **Register:** On the first screen, click "Sign Up" to create a new account.
2.  **Login:** Use your credentials to log in.
3.  **Create Playlist:** Go to your **Profile** (top-right icon on Home screen) -> Click "New Playlist".
4.  **Add Songs:**
    *   Go to **Music Library**.
    *   **Long-press** on any song.
    *   Select the playlist you want to add the song to.
5.  **Play Playlist:** Go to **Profile** -> Click on a playlist name to open and play it.
