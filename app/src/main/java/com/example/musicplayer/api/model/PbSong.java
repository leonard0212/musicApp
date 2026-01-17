package com.example.musicplayer.api.model;

public class PbSong {
    public String id;
    public String collectionId; // Needed to build file URL
    public String title;
    public String artist;
    public String genre;
    public String audio_file; // Use snake_case to match JSON or use @SerializedName
    public String album_art;
}
