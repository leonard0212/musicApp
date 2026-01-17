package com.example.musicplayer.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlists")
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    public int playlistId;

    public int ownerId;
    public String name;

    public Playlist(int ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
    }
}
