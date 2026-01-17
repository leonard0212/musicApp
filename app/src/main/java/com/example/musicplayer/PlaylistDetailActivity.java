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
import com.example.musicplayer.db.AppDatabase;
import com.example.musicplayer.db.SongEntity;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {
    ListView listView;
    TextView tvName;
    AppDatabase db;
    int playlistId;
    String playlistName;
    List<Song> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        db = AppDatabase.getDatabase(this);
        playlistId = getIntent().getIntExtra("PLAYLIST_ID", -1);
        playlistName = getIntent().getStringExtra("PLAYLIST_NAME");

        if (playlistId == -1) {
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
        List<SongEntity> entities = db.playlistDao().getSongsForPlaylist(playlistId);
        songs.clear();
        List<String> titles = new ArrayList<>();
        for (SongEntity e : entities) {
            songs.add(new Song(e.resId, e.title, e.artist, e.genre));
            titles.add(e.title + " - " + e.artist);
        }

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
    }
}
