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
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TextView tvName, tvEmail;
    Button btnEdit, btnLogout, btnCreatePlaylist;
    ListView lvPlaylists;

    FirebaseAuth mAuth;
    FirestoreHelper fs;
    List<PlaylistModel> playlists = new ArrayList<>();
    ArrayAdapter<String> adapter;
    UserModel currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        fs = new FirestoreHelper(this);

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnEdit = findViewById(R.id.btnEditAccount);
        btnLogout = findViewById(R.id.btnLogout);
        btnCreatePlaylist = findViewById(R.id.btnCreatePlaylist);
        lvPlaylists = findViewById(R.id.lvPlaylists);

        // Initial UI
        tvEmail.setText(mAuth.getCurrentUser().getEmail());

        loadUserInfo();
        loadPlaylists();

        btnEdit.setOnClickListener(v -> showEditDialog());
        btnCreatePlaylist.setOnClickListener(v -> showCreatePlaylistDialog());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserInfo() {
        fs.getUser(mAuth.getUid(), user -> {
            if (user != null) {
                currentUserModel = user;
                runOnUiThread(() -> tvName.setText(user.getUsername()));
            } else {
                // Fallback if user doc doesn't exist yet
                 runOnUiThread(() -> tvName.setText("User"));
            }
        });
    }

    private void loadPlaylists() {
        fs.getPlaylistsForUser(mAuth.getUid(), list -> {
            playlists = list;
            List<String> names = new ArrayList<>();
            for (PlaylistModel p : list) {
                names.add(p.getName());
            }

            runOnUiThread(() -> {
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = view.findViewById(android.R.id.text1);
                        text.setTextColor(getResources().getColor(android.R.color.white));
                        return view;
                    }
                };
                lvPlaylists.setAdapter(adapter);
                lvPlaylists.setOnItemClickListener((parent, view, position, id) -> {
                    PlaylistModel p = playlists.get(position);
                    Intent intent = new Intent(ProfileActivity.this, PlaylistDetailActivity.class);
                    // Pass ID and Name. Need to make PlaylistModel Parcelable ideally, or just pass ID.
                    // But DetailActivity needs the song IDs which are in the model.
                    // Or DetailActivity fetches the playlist again.
                    // Let's pass ID and Name, and let DetailActivity fetch songs.
                    // But DetailActivity currently uses 'playlistId' (int) from my previous code.
                    // I need to update PlaylistDetailActivity to use String ID.
                    intent.putExtra("PLAYLIST_ID", p.getId());
                    intent.putExtra("PLAYLIST_NAME", p.getName());
                    intent.putStringArrayListExtra("SONG_IDS", (ArrayList<String>) p.getSongIds());
                    startActivity(intent);
                });
            });
        });
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Account");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_account, null);
        EditText etUser = view.findViewById(R.id.etEditUsername);
        EditText etPass = view.findViewById(R.id.etEditPassword);

        if (currentUserModel != null) {
            etUser.setText(currentUserModel.getUsername());
        }
        etPass.setVisibility(View.GONE); // Password change handled by Firebase Auth separately, simplified here.

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUser = etUser.getText().toString();
            if (!newUser.isEmpty()) {
                fs.createUser(mAuth.getUid(), newUser, mAuth.getCurrentUser().getEmail());
                loadUserInfo();
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Playlist");
        final EditText input = new EditText(this);
        input.setHint("Playlist Name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = input.getText().toString();
            if (!name.isEmpty()) {
                fs.createPlaylist(name, mAuth.getUid());
                // Refresh list with delay or listener
                // For simplicity, just delay a bit or assume success.
                // Firestore listeners are real-time, but I'm using get().
                // I'll just reload after a second.
                new android.os.Handler().postDelayed(this::loadPlaylists, 1000);
                Toast.makeText(this, "Playlist created!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
