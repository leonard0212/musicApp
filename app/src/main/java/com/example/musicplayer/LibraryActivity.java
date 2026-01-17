package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.ArrayList;
import com.example.musicplayer.db.AppDatabase;
import com.example.musicplayer.db.SongEntity;
import com.example.musicplayer.db.Playlist;
import com.example.musicplayer.db.PlaylistSongCrossRef;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

public class LibraryActivity extends BaseActivity {

    private List<Song> displayedSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        ListView listView = findViewById(R.id.lvAllSongs);
        TextView title = findViewById(R.id.tvLibraryTitle);
        ImageView playlistImage = findViewById(R.id.ivLibraryImage);

        FloatingActionButton btnPlay = findViewById(R.id.btnPlayPlaylist);

        String genreFilter = getIntent().getStringExtra("GENRE_FILTER");
        String genreImageName = getIntent().getStringExtra("GENRE_IMAGE");

        AppDatabase db = AppDatabase.getDatabase(this);
        displayedSongs = new ArrayList<>();

        // 1. Setup Data and UI
        if (genreFilter != null) {
            // Case A: Opened from Genres Page (Filtered)
            List<SongEntity> entities = db.songDao().getSongsByGenre(genreFilter);
            for(SongEntity e : entities) displayedSongs.add(new Song(e.resId, e.title, e.artist, e.genre));

            title.setText(genreFilter);

            if (genreImageName != null) {
                int resId = getResources().getIdentifier(genreImageName, "drawable", getPackageName());
                if (resId != 0) playlistImage.setImageResource(resId);
            }
        } else {
            // Case B: Opened "Music Library" (All Songs)
            List<SongEntity> entities = db.songDao().getAllSongs();
            for(SongEntity e : entities) displayedSongs.add(new Song(e.resId, e.title, e.artist, e.genre));

            title.setText("All Music");

            // NEW: Set the specific image for the main library
            int resId = getResources().getIdentifier("library_cover", "drawable", getPackageName());
            if (resId != 0) {
                playlistImage.setImageResource(resId);
            }
        }

        LibraryAdapter adapter = new LibraryAdapter(displayedSongs);
        listView.setAdapter(adapter);

        // 2. Play Button Logic
        btnPlay.setOnClickListener(v -> {
            if (!displayedSongs.isEmpty()) {
                MusicPlayerManager.getInstance().playSong(LibraryActivity.this, displayedSongs, 0);
            }
        });

        // 3. List Click Logic
        listView.setOnItemClickListener((parent, view, position, id) -> {
            MusicPlayerManager.getInstance().playSong(LibraryActivity.this, displayedSongs, position);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showAddToPlaylistDialog(displayedSongs.get(position));
            return true;
        });

        setupMiniPlayer();
        setupBottomNavigation();
    }

    private void showAddToPlaylistDialog(Song song) {
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) return;

        AppDatabase db = AppDatabase.getDatabase(this);
        List<Playlist> playlists = db.playlistDao().getPlaylistsForUser(session.getUserId());

        if (playlists.isEmpty()) {
            Toast.makeText(this, "No playlists created yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[playlists.size()];
        for(int i=0; i<playlists.size(); i++) names[i] = playlists.get(i).name;

        new AlertDialog.Builder(this)
            .setTitle("Add to Playlist")
            .setItems(names, (dialog, which) -> {
                Playlist p = playlists.get(which);
                SongEntity entity = db.songDao().getSongByResId(song.getResId());
                if (entity != null) {
                    db.playlistDao().addSongToPlaylist(new PlaylistSongCrossRef(p.playlistId, entity.songId));
                    Toast.makeText(this, "Added to " + p.name, Toast.LENGTH_SHORT).show();
                }
            })
            .show();
    }

    private class LibraryAdapter extends BaseAdapter {
        private List<Song> songs;

        public LibraryAdapter(List<Song> songs) {
            this.songs = songs;
        }

        @Override
        public int getCount() { return songs.size(); }
        @Override
        public Object getItem(int position) { return songs.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(LibraryActivity.this)
                        .inflate(R.layout.item_library_song, parent, false);
            }
            Song song = songs.get(position);

            ImageView ivImage = convertView.findViewById(R.id.ivLibArtistImage);
            TextView tvTitle = convertView.findViewById(R.id.tvLibSongTitle);
            TextView tvArtist = convertView.findViewById(R.id.tvLibArtist);

            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());

            int artistImageRes = ArtistImageHelper.getArtistImageResource(
                    LibraryActivity.this, song.getArtist());
            ivImage.setImageResource(artistImageRes);

            return convertView;
        }
    }
}