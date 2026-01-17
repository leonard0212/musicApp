package com.example.musicplayer;

import android.content.Context;
import android.util.Log;
import com.example.musicplayer.model.SongModel;
import com.example.musicplayer.model.UserModel;
import com.example.musicplayer.model.PlaylistModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FirestoreHelper {
    private static final String TAG = "FirestoreHelper";
    private FirebaseFirestore db;
    private Context context;

    public FirestoreHelper(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void seedSongsIfEmpty(List<Song> localSongs, Runnable onComplete) {
        db.collection("songs").limit(1).get().addOnSuccessListener(snapshots -> {
            if (snapshots.isEmpty()) {
                Log.d(TAG, "Seeding songs to Firestore...");
                for (Song s : localSongs) {
                    try {
                        String resName = context.getResources().getResourceEntryName(s.getResId());
                        SongModel model = new SongModel(s.getTitle(), s.getArtist(), s.getGenre(), resName);
                        db.collection("songs").add(model);
                    } catch (Exception e) {
                        Log.e(TAG, "Error seeding song: " + s.getTitle(), e);
                    }
                }
            } else {
                Log.d(TAG, "Songs already seeded.");
            }
            if (onComplete != null) onComplete.run();
        }).addOnFailureListener(e -> {
             Log.e(TAG, "Error checking songs", e);
             if (onComplete != null) onComplete.run();
        });
    }

    public void getAllSongs(Consumer<List<Song>> callback) {
        db.collection("songs").get().addOnCompleteListener(task -> {
            List<Song> songs = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    SongModel model = doc.toObject(SongModel.class);
                    model.setId(doc.getId());

                    int resId = context.getResources().getIdentifier(
                        model.getResName(), "raw", context.getPackageName());

                    if (resId != 0) {
                        Song s = new Song(resId, model.getTitle(), model.getArtist(), model.getGenre());
                        // We need to attach the Firestore ID to the Song object somehow if we want to add it to playlist by ID
                        // Or we pass the Song object and look it up again?
                        // Better to add an 'id' field to Song class (transient, not for resId)
                        // But Song class is shared.
                        // I'll create a Map or a temporary wrapper.
                        // For this demo, let's just assume we can match by title/resName.
                        // Actually, I'll pass the ID via a tag or a parallel list.
                        // Or better: Modify Song to hold ID.
                        s.setFirestoreId(model.getId());
                        songs.add(s);
                    }
                }
            }
            callback.accept(songs);
        });
    }

    public void createUser(String uid, String username, String email) {
        UserModel user = new UserModel(uid, username, email);
        db.collection("users").document(uid).set(user);
    }

    public void getUser(String uid, Consumer<UserModel> callback) {
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                callback.accept(doc.toObject(UserModel.class));
            } else {
                callback.accept(null);
            }
        }).addOnFailureListener(e -> callback.accept(null));
    }

    public void getSongsByGenre(String genre, Consumer<List<Song>> callback) {
        getAllSongs(allSongs -> {
            List<Song> filtered = new ArrayList<>();
            for(Song s : allSongs) {
                if(s.getGenre().equalsIgnoreCase(genre)) filtered.add(s);
            }
            callback.accept(filtered);
        });
    }

    public void getPlaylistsForUser(String userId, Consumer<List<PlaylistModel>> callback) {
        db.collection("playlists").whereEqualTo("ownerId", userId).get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            List<PlaylistModel> list = new ArrayList<>();
            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                PlaylistModel p = doc.toObject(PlaylistModel.class);
                p.setId(doc.getId());
                list.add(p);
            }
            callback.accept(list);
        });
    }

    public void addSongToPlaylist(String playlistId, String songId) {
        db.collection("playlists").document(playlistId)
            .update("songIds", com.google.firebase.firestore.FieldValue.arrayUnion(songId));
    }

    public void createPlaylist(String name, String ownerId) {
        PlaylistModel p = new PlaylistModel(name, ownerId, new ArrayList<>());
        db.collection("playlists").add(p);
    }

    public void getSongsForPlaylist(PlaylistModel playlist, Consumer<List<Song>> callback) {
        if (playlist.getSongIds() == null || playlist.getSongIds().isEmpty()) {
            callback.accept(new ArrayList<>());
            return;
        }
        // Ideally fetch by IDs using whereIn, but limit is 10.
        // For simplicity, fetch all and filter (not efficient but easy migration)
        getAllSongs(allSongs -> {
            List<Song> result = new ArrayList<>();
            for(Song s : allSongs) {
                if (playlist.getSongIds().contains(s.getFirestoreId())) {
                    result.add(s);
                }
            }
            callback.accept(result);
        });
    }
}
