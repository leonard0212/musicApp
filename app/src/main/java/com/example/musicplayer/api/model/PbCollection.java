package com.example.musicplayer.api.model;
import java.util.List;

public class PbCollection<T> {
    public int page;
    public int perPage;
    public int totalItems;
    public List<T> items;
}
