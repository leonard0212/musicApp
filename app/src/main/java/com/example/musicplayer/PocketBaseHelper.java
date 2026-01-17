package com.example.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.musicplayer.api.*;
import com.example.musicplayer.api.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PocketBaseHelper {
    private static final String PREFS = "pb_prefs";
    private static final String KEY_TOKEN = "pb_token";
    private static final String KEY_USER_ID = "pb_user_id";

    private Context context;
    private PocketBaseService api;
    private SharedPreferences prefs;

    public PocketBaseHelper(Context context) {
        this.context = context;
        this.api = RetrofitClient.getInstance().getApi();
        this.prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void login(String identity, String password, Consumer<Boolean> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("identity", identity);
        body.put("password", password);

        api.login(body).enqueue(new Callback<PbAuthResponse>() {
            @Override
            public void onResponse(Call<PbAuthResponse> call, Response<PbAuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveAuth(response.body().token, response.body().record.id);
                    callback.accept(true);
                } else {
                    callback.accept(false);
                }
            }
            @Override
            public void onFailure(Call<PbAuthResponse> call, Throwable t) {
                callback.accept(false);
            }
        });
    }

    public void register(String username, String email, String password, Consumer<Boolean> callback) {
        PbUser user = new PbUser(username, email, password, password);
        api.register(user).enqueue(new Callback<PbUser>() {
            @Override
            public void onResponse(Call<PbUser> call, Response<PbUser> response) {
                callback.accept(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<PbUser> call, Throwable t) {
                callback.accept(false);
            }
        });
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_TOKEN);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    private void saveAuth(String token, String userId) {
        prefs.edit().putString(KEY_TOKEN, token).putString(KEY_USER_ID, userId).apply();
    }

    public void getAllSongs(Consumer<List<Song>> callback) {
        api.getSongs(100, "-created").enqueue(new Callback<PbCollection<PbSong>>() {
            @Override
            public void onResponse(Call<PbCollection<PbSong>> call, Response<PbCollection<PbSong>> response) {
                List<Song> songs = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    for (PbSong item : response.body().items) {
                        String fileUrl = RetrofitClient.BASE_URL + "api/files/" + item.collectionId + "/" + item.id + "/" + item.audio_file;
                        String imgUrl = RetrofitClient.BASE_URL + "api/files/" + item.collectionId + "/" + item.id + "/" + item.album_art;
                        songs.add(new Song(item.id, item.title, item.artist, item.genre, fileUrl, imgUrl));
                    }
                }
                callback.accept(songs);
            }
            @Override
            public void onFailure(Call<PbCollection<PbSong>> call, Throwable t) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    public void getPlaylists(Consumer<List<PbPlaylist>> callback) {
        String filter = "owner='" + getUserId() + "'";
        api.getPlaylists(getToken(), filter).enqueue(new Callback<PbCollection<PbPlaylist>>() {
            @Override
            public void onResponse(Call<PbCollection<PbPlaylist>> call, Response<PbCollection<PbPlaylist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.accept(response.body().items);
                } else {
                    callback.accept(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<PbCollection<PbPlaylist>> call, Throwable t) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    public void createPlaylist(String name, Consumer<Boolean> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("owner", getUserId());

        api.createPlaylist(getToken(), body).enqueue(new Callback<PbPlaylist>() {
            @Override
            public void onResponse(Call<PbPlaylist> call, Response<PbPlaylist> response) {
                callback.accept(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<PbPlaylist> call, Throwable t) {
                callback.accept(false);
            }
        });
    }

    public void addSongToPlaylist(String playlistId, List<String> currentSongs, String newSongId, Consumer<Boolean> callback) {
        List<String> updatedSongs = new ArrayList<>();
        if (currentSongs != null) updatedSongs.addAll(currentSongs);
        if (!updatedSongs.contains(newSongId)) {
            updatedSongs.add(newSongId);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("songs", updatedSongs);

        api.updatePlaylist(getToken(), playlistId, body).enqueue(new Callback<PbPlaylist>() {
            @Override
            public void onResponse(Call<PbPlaylist> call, Response<PbPlaylist> response) {
                callback.accept(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<PbPlaylist> call, Throwable t) {
                callback.accept(false);
            }
        });
    }
}
