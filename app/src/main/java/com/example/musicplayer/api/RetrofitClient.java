package com.example.musicplayer.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // CHANGE THIS TO YOUR SERVER IP. Example: "http://192.168.1.50:8090/"
    public static String BASE_URL = "http://10.0.2.2:8090/";

    private static RetrofitClient instance;
    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public PocketBaseService getApi() {
        return retrofit.create(PocketBaseService.class);
    }

    public static void setBaseUrl(String url) {
        if (!url.endsWith("/")) url += "/";
        BASE_URL = url;
        instance = new RetrofitClient(); // Rebuild
    }
}
