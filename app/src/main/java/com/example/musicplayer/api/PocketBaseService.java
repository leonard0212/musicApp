package com.example.musicplayer.api;

import com.example.musicplayer.api.model.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.Map;

public interface PocketBaseService {
    @POST("api/collections/users/auth-with-password")
    Call<PbAuthResponse> login(@Body Map<String, String> body);

    @POST("api/collections/users/records")
    Call<PbUser> register(@Body PbUser user);

    @GET("api/collections/songs/records")
    Call<PbCollection<PbSong>> getSongs(@Query("perPage") int perPage, @Query("sort") String sort);

    @GET("api/collections/playlists/records")
    Call<PbCollection<PbPlaylist>> getPlaylists(@Header("Authorization") String token, @Query("filter") String filter);

    @POST("api/collections/playlists/records")
    Call<PbPlaylist> createPlaylist(@Header("Authorization") String token, @Body Map<String, Object> body);

    @PATCH("api/collections/playlists/records/{id}")
    Call<PbPlaylist> updatePlaylist(@Header("Authorization") String token, @Path("id") String id, @Body Map<String, Object> body);
}
