package com.example.musicplayer.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.List;

@Dao
public interface PlaylistDao {
    @Insert
    long insert(Playlist playlist);

    @Insert
    void addSongToPlaylist(PlaylistSongCrossRef crossRef);

    @Query("SELECT * FROM playlists WHERE ownerId = :userId")
    List<Playlist> getPlaylistsForUser(int userId);

    @Transaction
    @Query("SELECT songs.* FROM songs INNER JOIN PlaylistSongCrossRef ON songs.songId = PlaylistSongCrossRef.songId WHERE PlaylistSongCrossRef.playlistId = :playlistId")
    List<SongEntity> getSongsForPlaylist(int playlistId);

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    void deletePlaylist(int playlistId);
}
