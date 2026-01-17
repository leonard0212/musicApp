package com.example.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.musicplayer.model.PlaylistModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends BaseActivity {

    private List<Song> displayedSongs = new ArrayList<>();

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

        FirestoreHelper fs = new FirestoreHelper(this);

        // Show loading state potentially, but for now just wait

        fs.getAllSongs(allSongs -> {
            final List<Song> filteredSongs;
            if (genreFilter != null) {
                filteredSongs = new ArrayList<>();
                for(Song s : allSongs) {
                    if (s.getGenre() != null && s.getGenre().equalsIgnoreCase(genreFilter)) {
                        filteredSongs.add(s);
                    }
                }
            } else {
                filteredSongs = allSongs;
            }

            displayedSongs = filteredSongs;

            runOnUiThread(() -> {
                if (genreFilter != null) {
                    title.setText(genreFilter);
                    if (genreImageName != null) {
                        int resId = getResources().getIdentifier(genreImageName, "drawable", getPackageName());
                        if (resId != 0) playlistImage.setImageResource(resId);
                    }
                } else {
                    title.setText("All Music");
                    int resId = getResources().getIdentifier("library_cover", "drawable", getPackageName());
                    if (resId != 0) {
                        playlistImage.setImageResource(resId);
                    }
                }

                LibraryAdapter adapter = new LibraryAdapter(displayedSongs);
                listView.setAdapter(adapter);

                btnPlay.setOnClickListener(v -> {
                    if (!displayedSongs.isEmpty()) {
                        MusicPlayerManager.getInstance().playSong(LibraryActivity.this, displayedSongs, 0);
                    }
                });

                listView.setOnItemClickListener((parent, view, position, id) -> {
                    MusicPlayerManager.getInstance().playSong(LibraryActivity.this, displayedSongs, position);
                });

                listView.setOnItemLongClickListener((parent, view, position, id) -> {
                    showAddToPlaylistDialog(displayedSongs.get(position));
                    return true;
                });
            });
        });

        setupMiniPlayer();
        setupBottomNavigation();
    }

    private void showAddToPlaylistDialog(Song song) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        FirestoreHelper fs = new FirestoreHelper(this);
        fs.getPlaylistsForUser(FirebaseAuth.getInstance().getUid(), playlists -> {
            runOnUiThread(() -> {
                if (playlists.isEmpty()) {
                    Toast.makeText(this, "No playlists created yet.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] names = new String[playlists.size()];
                for(int i=0; i<playlists.size(); i++) names[i] = playlists.get(i).getName();

                new AlertDialog.Builder(this)
                    .setTitle("Add to Playlist")
                    .setItems(names, (dialog, which) -> {
                        PlaylistModel p = playlists.get(which);
                        if (song.getFirestoreId() != null) {
                            fs.addSongToPlaylist(p.getId(), song.getFirestoreId());
                            Toast.makeText(this, "Added to " + p.getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error: Song not synced properly", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
            });
        });
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
