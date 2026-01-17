package com.example.musicplayer.model;

import java.util.List;

public class PlaylistModel {
    private String id;
    private String name;
    private String ownerId;
    private List<String> songIds;

    public PlaylistModel() {}

    public PlaylistModel(String name, String ownerId, List<String> songIds) {
        this.name = name;
        this.ownerId = ownerId;
        this.songIds = songIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public List<String> getSongIds() { return songIds; }
    public void setSongIds(List<String> songIds) { this.songIds = songIds; }
}
