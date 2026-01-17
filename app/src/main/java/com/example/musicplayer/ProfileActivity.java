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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.musicplayer.db.AppDatabase;
import com.example.musicplayer.db.Playlist;
import com.example.musicplayer.db.User;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    TextView tvName, tvEmail;
    Button btnEdit, btnLogout, btnCreatePlaylist;
    ListView lvPlaylists;

    SessionManager session;
    AppDatabase db;
    User currentUser;
    List<Playlist> playlists;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new SessionManager(this);
        db = AppDatabase.getDatabase(this);

        if (!session.isLoggedIn()) {
            finish();
            return;
        }

        int uid = session.getUserId();
        currentUser = db.userDao().getUserById(uid);

        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnEdit = findViewById(R.id.btnEditAccount);
        btnLogout = findViewById(R.id.btnLogout);
        btnCreatePlaylist = findViewById(R.id.btnCreatePlaylist);
        lvPlaylists = findViewById(R.id.lvPlaylists);

        updateUserInfo();
        loadPlaylists();

        btnEdit.setOnClickListener(v -> showEditDialog());
        btnCreatePlaylist.setOnClickListener(v -> showCreatePlaylistDialog());

        btnLogout.setOnClickListener(v -> {
            session.logoutUser();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateUserInfo() {
        if (currentUser != null) {
            tvName.setText(currentUser.username);
            tvEmail.setText(currentUser.email);
        }
    }

    private void loadPlaylists() {
        if (currentUser != null) {
            playlists = db.playlistDao().getPlaylistsForUser(currentUser.uid);
            List<String> names = new ArrayList<>();
            for (Playlist p : playlists) {
                names.add(p.name);
            }

            // Custom adapter to ensure white text
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
                Playlist p = playlists.get(position);
                Intent intent = new Intent(ProfileActivity.this, PlaylistDetailActivity.class);
                intent.putExtra("PLAYLIST_ID", p.playlistId);
                intent.putExtra("PLAYLIST_NAME", p.name);
                startActivity(intent);
            });
        }
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Account");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_account, null);
        EditText etUser = view.findViewById(R.id.etEditUsername);
        EditText etPass = view.findViewById(R.id.etEditPassword);

        etUser.setText(currentUser.username);
        // Don't show hashed password
        etPass.setHint("New Password (leave empty to keep)");

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUser = etUser.getText().toString();
            String newPass = etPass.getText().toString();
            if (!newUser.isEmpty()) {
                currentUser.username = newUser;
                if (!newPass.isEmpty()) {
                    currentUser.password = SecurityUtil.hashPassword(newPass);
                }
                db.userDao().update(currentUser);
                updateUserInfo();
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
                Playlist p = new Playlist(currentUser.uid, name);
                db.playlistDao().insert(p);
                loadPlaylists();
                Toast.makeText(this, "Playlist created!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
