# Music Player App with Firebase Cloud Backend

This project is an Android Music Player application that uses **Firebase Authentication** and **Cloud Firestore** to manage users, playlists, and music metadata in the cloud.

## Features

*   **Cloud Authentication:** Secure Sign Up and Sign In using Firebase Auth (Email/Password).
*   **Cloud Database:** Playlists and User profiles are stored in Firestore, accessible from any device logged into the same account.
*   **Music Library:** Music metadata is synced to the cloud. The app automatically seeds the database on the first run.
*   **Playlists:** Create playlists, add songs, and play them.

## ⚠️ CRITICAL SETUP INSTRUCTIONS ⚠️

To make this app work, you **MUST** connect it to your own Firebase project.

### 1. Create a Firebase Project
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Click **Add project** and give it a name (e.g., "MusicPlayerApp").
3.  Disable Google Analytics for simplicity (optional).
4.  Click **Create project**.

### 2. Add Android App to Firebase
1.  In your Firebase project overview, click the **Android** icon.
2.  **Package name:** `com.example.musicplayer` (This must match exactly).
3.  Click **Register app**.
4.  **Download config file:** Download `google-services.json`.
5.  **Move the file:** Place `google-services.json` inside the `app/` directory of this project (`MusicPlayer/app/google-services.json`).

### 3. Enable Authentication
1.  In Firebase Console, go to **Build > Authentication**.
2.  Click **Get started**.
3.  Select **Email/Password** from the Sign-in method list.
4.  Enable **Email/Password** and click **Save**.

### 4. Enable Cloud Firestore
1.  In Firebase Console, go to **Build > Firestore Database**.
2.  Click **Create database**.
3.  Select a location (e.g., `eur3` or `us-central`).
4.  **Start in Test Mode:** Select "Start in test mode" (This allows read/write access for development).
    *   *Note: In production, you should set up proper security rules.*

### 5. Run the App
1.  Open Android Studio.
2.  Sync Gradle files.
3.  Run the app on an Emulator or Device.
4.  **First Run:** Sign up for an account. The app will automatically upload the song list to your new Firestore database.

## Technical Details

*   **Firebase SDK:** Auth, Firestore.
*   **Architecture:** Async callbacks for data fetching.
*   **Data Model:**
    *   `users`: Stores username/email.
    *   `songs`: Stores song metadata and resource name mapping.
    *   `playlists`: Stores playlist name, owner ID, and list of song IDs.

## Troubleshooting

*   **Crash on startup:** Did you add `google-services.json` to the `app/` folder?
*   **"Registration Failed":** Check if Email/Password Auth is enabled in Firebase Console.
*   **Empty Library:** Wait a few seconds for the initial sync/seed to complete on the first login.
