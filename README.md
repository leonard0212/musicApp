# Music Player App - Self-Hosted Setup (Windows Server 2022)

This app connects to a **PocketBase** backend hosted on your Windows Server. This allows you to host your own database, MP3s, and images without any file size limits or recurring cloud costs.

## 1. Server Setup (On Windows Server 2022)

### Step A: Download PocketBase
1.  Go to [pocketbase.io/docs](https://pocketbase.io/docs) and download the **Windows** zip file (`pocketbase_x.x.x_windows_amd64.zip`).
2.  Extract the zip file to a folder, e.g., `C:\PocketBase`.

### Step B: Run the Server
1.  Open **PowerShell** or **Command Prompt** as Administrator.
2.  Navigate to the folder:
    ```powershell
    cd C:\PocketBase
    ```
3.  Run the server to listen on all IP addresses (important for external access):
    ```powershell
    .\pocketbase.exe serve --http="0.0.0.0:8090"
    ```
4.  Keep this window open.

### Step C: Configure Firewall
1.  Open **Windows Defender Firewall with Advanced Security**.
2.  Click **Inbound Rules** -> **New Rule**.
3.  Select **Port** -> **TCP**.
4.  Specific local ports: `8090`.
5.  Allow the connection.
6.  Name it "PocketBase".

### Step D: Admin Setup & Data Structure
1.  Open a browser on your server and go to `http://localhost:8090/_/`.
2.  Create your Admin account (email/password).
3.  **Create Collections:**
    *   **songs**:
        *   Field: `title` (Text)
        *   Field: `artist` (Text)
        *   Field: `genre` (Text)
        *   Field: `audio_file` (File) -> **Important:** In options, allow MIME types `audio/mpeg`, `audio/mp3`.
        *   Field: `album_art` (File) -> Allow images.
    *   **playlists**:
        *   Field: `name` (Text)
        *   Field: `owner` (Relation -> users)
        *   Field: `songs` (Relation -> songs, **Enable Multiple**)
    *   **users**: (Already exists). Add a `name` text field if you want.

4.  **Add API Rules (Permissions):**
    *   Click the "Settings" (gear) icon next to each collection.
    *   Select **API Rules**.
    *   For **songs**: Set "List/Search" and "View" to empty (public) or `@request.auth.id != ""`.
    *   For **playlists**: Set all rules to `@request.auth.id != ""` (only logged in users).
    *   For **users**: Default rules are usually fine.

5.  **Upload Music:**
    *   Go to the `songs` collection in the Admin UI.
    *   Click "New Record".
    *   Fill in Title, Artist, Genre.
    *   **Upload your MP3 file** and **Image file**.
    *   Click Save.

## 2. App Configuration

1.  Open `app/src/main/java/com/example/musicplayer/api/RetrofitClient.java`.
2.  Find the line:
    ```java
    public static String BASE_URL = "http://10.0.2.2:8090/";
    ```
3.  Change `10.0.2.2` to the **Public IP Address** of your Windows Server.
    *   Example: `http://192.168.1.50:8090/` (if on same WiFi)
    *   Example: `http://203.0.113.5:8090/` (if accessing over internet - requires Port Forwarding on your router).

4.  Build and Run the App.

## 3. Usage
1.  Register a new account in the App.
2.  Login.
3.  You will see the songs you uploaded to the server!
