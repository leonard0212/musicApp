package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicplayer.api.model.PbPlaylist;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TextView tvName;
    Button btnEdit, btnLogout, btnCreatePlaylist;
    ListView lvPlaylists;

    PocketBaseHelper pbHelper;
    List<PbPlaylist> playlists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        pbHelper = new PocketBaseHelper(this);

        if (!pbHelper.isLoggedIn()) {
            finish(); return;
        }

        tvName = findViewById(R.id.tvProfileName);
        btnEdit = findViewById(R.id.btnEditAccount);
        btnLogout = findViewById(R.id.btnLogout);
        btnCreatePlaylist = findViewById(R.id.btnCreatePlaylist);
        lvPlaylists = findViewById(R.id.lvPlaylists);

        tvName.setText("User: " + pbHelper.getUserId());

        loadPlaylists();

        btnEdit.setVisibility(View.GONE);

        btnCreatePlaylist.setOnClickListener(v -> showCreatePlaylistDialog());

        btnLogout.setOnClickListener(v -> {
            pbHelper.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadPlaylists() {
        pbHelper.getPlaylists(list -> {
            playlists = list;
            List<String> names = new ArrayList<>();
            for (PbPlaylist p : list) names.add(p.name);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names) {
                @NonNull @Override public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ((TextView)view.findViewById(android.R.id.text1)).setTextColor(getResources().getColor(android.R.color.white));
                    return view;
                }
            };
            lvPlaylists.setAdapter(adapter);
            lvPlaylists.setOnItemClickListener((parent, view, position, id) -> {
                PbPlaylist p = playlists.get(position);
                Intent intent = new Intent(ProfileActivity.this, PlaylistDetailActivity.class);
                intent.putExtra("PLAYLIST_ID", p.id);
                intent.putExtra("PLAYLIST_NAME", p.name);
                intent.putStringArrayListExtra("SONG_IDS", new ArrayList<>(p.songs));
                startActivity(intent);
            });
        });
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Playlist");
        final EditText input = new EditText(this);
        input.setHint("Name");
        builder.setView(input);
        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = input.getText().toString();
            if (!name.isEmpty()) {
                pbHelper.createPlaylist(name, success -> {
                    if(success) loadPlaylists();
                });
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
