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
import com.example.musicplayer.api.model.PbPlaylist;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends BaseActivity {
    private List<Song> displayedSongs = new ArrayList<>();
    PocketBaseHelper pbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        pbHelper = new PocketBaseHelper(this);
        ListView listView = findViewById(R.id.lvAllSongs);
        TextView title = findViewById(R.id.tvLibraryTitle);
        ImageView playlistImage = findViewById(R.id.ivLibraryImage);
        FloatingActionButton btnPlay = findViewById(R.id.btnPlayPlaylist);

        pbHelper.getAllSongs(songs -> {
            displayedSongs = songs;

            LibraryAdapter adapter = new LibraryAdapter(displayedSongs);
            listView.setAdapter(adapter);

            btnPlay.setOnClickListener(v -> {
                if (!displayedSongs.isEmpty())
                    MusicPlayerManager.getInstance().playSong(LibraryActivity.this, displayedSongs, 0);
            });

            listView.setOnItemClickListener((parent, view, position, id) -> {
                MusicPlayerManager.getInstance().playSong(LibraryActivity.this, displayedSongs, position);
            });

            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                showAddToPlaylistDialog(displayedSongs.get(position));
                return true;
            });
        });

        setupMiniPlayer();
        setupBottomNavigation();
    }

    private void showAddToPlaylistDialog(Song song) {
        pbHelper.getPlaylists(playlists -> {
            if (playlists.isEmpty()) {
                Toast.makeText(this, "No playlists", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] names = new String[playlists.size()];
            for(int i=0; i<playlists.size(); i++) names[i] = playlists.get(i).name;

            new AlertDialog.Builder(this)
                .setTitle("Add to Playlist")
                .setItems(names, (dialog, which) -> {
                    PbPlaylist p = playlists.get(which);
                    pbHelper.addSongToPlaylist(p.id, p.songs, song.getId(), success -> {
                        if(success) Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                    });
                })
                .show();
        });
    }

    private class LibraryAdapter extends BaseAdapter {
        private List<Song> songs;
        public LibraryAdapter(List<Song> songs) { this.songs = songs; }
        @Override public int getCount() { return songs.size(); }
        @Override public Object getItem(int position) { return songs.get(position); }
        @Override public long getItemId(int position) { return position; }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(LibraryActivity.this).inflate(R.layout.item_library_song, parent, false);
            Song song = songs.get(position);

            ImageView iv = convertView.findViewById(R.id.ivLibArtistImage);
            ((TextView)convertView.findViewById(R.id.tvLibSongTitle)).setText(song.getTitle());
            ((TextView)convertView.findViewById(R.id.tvLibArtist)).setText(song.getArtist());

            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                 Picasso.get().load(song.getImageUrl()).placeholder(android.R.drawable.ic_menu_gallery).into(iv);
            }
            return convertView;
        }
    }
}
