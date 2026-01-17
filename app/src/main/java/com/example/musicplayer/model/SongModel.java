package com.example.musicplayer.model;

public class SongModel {
    private String id;
    private String title;
    private String artist;
    private String genre;
    private String resName;

    public SongModel() {}

    public SongModel(String title, String artist, String genre, String resName) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.resName = resName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getResName() { return resName; }
    public void setResName(String resName) { this.resName = resName; }
}
