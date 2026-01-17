package com.example.musicplayer.db;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.musicplayer.MusicLibrary;
import com.example.musicplayer.Song;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Playlist.class, SongEntity.class, PlaylistSongCrossRef.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PlaylistDao playlistDao();
    public abstract SongDao songDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "music_player_database")
                            .allowMainThreadQueries() // Still required for simplifying the migration
                            .build();

                    // Synchronous Check and Seed (Fixes race condition)
                    if (INSTANCE.songDao().getAllSongs().isEmpty()) {
                        List<Song> librarySongs = MusicLibrary.getSongList();
                        List<SongEntity> entities = new ArrayList<>();
                        for (Song s : librarySongs) {
                            entities.add(new SongEntity(s.getResId(), s.getTitle(), s.getArtist(), s.getGenre()));
                        }
                        INSTANCE.songDao().insertAll(entities);
                    }
                }
            }
        }
        return INSTANCE;
    }
}
