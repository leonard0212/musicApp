package com.example.musicplayer.api.model;

public class PbAuthResponse {
    public String token;
    public Record record;

    public static class Record {
        public String id;
        public String username;
        public String email;
    }
}
