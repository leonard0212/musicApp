package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private List<Song> allSongs = new ArrayList<>();
    private List<Song> filteredSongs;
    private SearchAdapter adapter;
    private PocketBaseHelper pbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        pbHelper = new PocketBaseHelper(this);

        EditText etSearch = findViewById(R.id.etSearch);
        ListView lvResults = findViewById(R.id.lvSearchResults);

        filteredSongs = new ArrayList<>();
        adapter = new SearchAdapter(filteredSongs);
        lvResults.setAdapter(adapter);

        // Fetch all songs
        pbHelper.getAllSongs(songs -> {
            allSongs = songs;
        });

        // Search Logic
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvResults.setOnItemClickListener((parent, view, position, id) -> {
            MusicPlayerManager.getInstance().playSong(this, filteredSongs, position);
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
        });

        setupMiniPlayer();
        setupBottomNavigation();
    }

    private void filter(String query) {
        filteredSongs.clear();

        if (!query.isEmpty() && !allSongs.isEmpty()) {
            String lowerQuery = query.toLowerCase();
            for (Song song : allSongs) {
                if (song.getTitle().toLowerCase().contains(lowerQuery) ||
                        song.getArtist().toLowerCase().contains(lowerQuery)) {
                    filteredSongs.add(song);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class SearchAdapter extends BaseAdapter {
        private List<Song> songs;
        public SearchAdapter(List<Song> songs) { this.songs = songs; }
        @Override public int getCount() { return songs.size(); }
        @Override public Object getItem(int position) { return songs.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SearchActivity.this)
                        .inflate(R.layout.item_library_song, parent, false);
            }
            Song song = songs.get(position);

            TextView title = convertView.findViewById(R.id.tvLibSongTitle);
            TextView artist = convertView.findViewById(R.id.tvLibArtist);
            ImageView image = convertView.findViewById(R.id.ivLibArtistImage);

            title.setText(song.getTitle());
            artist.setText(song.getArtist());

            if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
                Picasso.get().load(song.getImageUrl()).placeholder(android.R.drawable.ic_menu_gallery).into(image);
            } else {
                image.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            return convertView;
        }
    }
}
