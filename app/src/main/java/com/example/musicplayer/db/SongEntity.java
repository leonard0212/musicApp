package com.example.musicplayer.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class SongEntity {
    @PrimaryKey(autoGenerate = true)
    public int songId;

    public int resId;
    public String title;
    public String artist;
    public String genre;

    public SongEntity(int resId, String title, String artist, String genre) {
        this.resId = resId;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }
}
