package com.example.musicplayer.api.model;
import java.util.List;

public class PbPlaylist {
    public String id;
    public String name;
    public String owner; // Relation ID
    public List<String> songs; // Relation IDs
}
