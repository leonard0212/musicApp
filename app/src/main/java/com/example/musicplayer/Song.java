package com.example.musicplayer;

public class Song {
    private String id;
    private String title;
    private String artist;
    private String genre;
    private String fileUrl;
    private String imageUrl;

    public Song(String id, String title, String artist, String genre, String fileUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getGenre() { return genre; }
    public String getFileUrl() { return fileUrl; }
    public String getImageUrl() { return imageUrl; }
}
