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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ArtistActivity extends BaseActivity {

    private ImageView ivArtistImage;
    private TextView tvArtistName;
    private ListView lvArtistSongs;
    private List<Song> artistSongs = new ArrayList<>();
    private String currentArtistName;
    private PocketBaseHelper pbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        pbHelper = new PocketBaseHelper(this);

        ivArtistImage = findViewById(R.id.ivArtistImage);
        tvArtistName = findViewById(R.id.tvArtistName);
        lvArtistSongs = findViewById(R.id.lvArtistSongs);

        currentArtistName = getIntent().getStringExtra("ARTIST_NAME");
        if (currentArtistName == null) currentArtistName = "Unknown Artist";

        tvArtistName.setText(currentArtistName);
        ivArtistImage.setImageResource(android.R.drawable.ic_menu_gallery);

        pbHelper.getAllSongs(songs -> {
            artistSongs.clear();
            for(Song s : songs) {
                if (s.getArtist().equalsIgnoreCase(currentArtistName)) {
                    artistSongs.add(s);
                    if (artistSongs.size() == 1 && s.getImageUrl() != null && !s.getImageUrl().isEmpty()) {
                         Picasso.get().load(s.getImageUrl()).placeholder(android.R.drawable.ic_menu_gallery).into(ivArtistImage);
                    }
                }
            }
            SongAdapter adapter = new SongAdapter(artistSongs);
            lvArtistSongs.setAdapter(adapter);
        });

        lvArtistSongs.setOnItemClickListener((parent, view, position, id) -> {
            MusicPlayerManager.getInstance().playSong(ArtistActivity.this, artistSongs, position);
        });

        setupMiniPlayer();
        setupBottomNavigation();
    }

    private class SongAdapter extends BaseAdapter {
        private List<Song> songs;
        public SongAdapter(List<Song> songs) { this.songs = songs; }
        @Override public int getCount() { return songs.size(); }
        @Override public Object getItem(int position) { return songs.get(position); }
        @Override public long getItemId(int position) { return position; }
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(ArtistActivity.this).inflate(R.layout.item_song, parent, false);
            Song song = songs.get(position);
            ((TextView)convertView.findViewById(R.id.tvSongTitleItem)).setText(song.getTitle());
            ((TextView)convertView.findViewById(R.id.tvSongGenreItem)).setText(song.getGenre());
            return convertView;
        }
    }
}
