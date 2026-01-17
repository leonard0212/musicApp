package com.example.musicplayer;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicplayer.model.PlaylistModel;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {
    ListView listView;
    TextView tvName;
    FirestoreHelper fs;
    String playlistId;
    String playlistName;
    ArrayList<String> songIds;
    List<Song> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        fs = new FirestoreHelper(this);

        playlistId = getIntent().getStringExtra("PLAYLIST_ID");
        playlistName = getIntent().getStringExtra("PLAYLIST_NAME");
        songIds = getIntent().getStringArrayListExtra("SONG_IDS");

        if (playlistId == null) {
            finish();
            return;
        }

        tvName = findViewById(R.id.tvPlaylistName);
        listView = findViewById(R.id.lvPlaylistSongs);

        tvName.setText(playlistName);

        loadSongs();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            MusicPlayerManager.getInstance().playSong(this, songs, position);
        });
    }

    private void loadSongs() {
        PlaylistModel tempModel = new PlaylistModel();
        tempModel.setSongIds(songIds);

        fs.getSongsForPlaylist(tempModel, loadedSongs -> {
            songs = loadedSongs;
            List<String> titles = new ArrayList<>();
            for (Song s : songs) {
                titles.add(s.getTitle() + " - " + s.getArtist());
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles) {
                     @NonNull
                     @Override
                     public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                         View view = super.getView(position, convertView, parent);
                         TextView text = view.findViewById(android.R.id.text1);
                         text.setTextColor(getResources().getColor(android.R.color.white));
                         return view;
                     }
                };
                listView.setAdapter(adapter);
            });
        });
    }
}
