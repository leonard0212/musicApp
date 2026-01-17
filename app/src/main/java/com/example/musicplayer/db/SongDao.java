package com.example.musicplayer.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SongDao {
    @Insert
    void insertAll(List<SongEntity> songs);

    @Query("SELECT * FROM songs")
    List<SongEntity> getAllSongs();

    @Query("SELECT * FROM songs WHERE genre LIKE :genre")
    List<SongEntity> getSongsByGenre(String genre);

    @Query("SELECT * FROM songs WHERE resId = :resId LIMIT 1")
    SongEntity getSongByResId(int resId);
}
