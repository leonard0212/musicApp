package com.example.musicplayer;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MusicPlayerManager {

    private static MusicPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private List<Song> currentPlaylist = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private List<Runnable> listeners = new ArrayList<>();
    private Stack<Integer> songHistory = new Stack<>();

    private MusicPlayerManager() {}

    public static synchronized MusicPlayerManager getInstance() {
        if (instance == null) instance = new MusicPlayerManager();
        return instance;
    }

    public void addSongChangedListener(Runnable listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeSongChangedListener(Runnable listener) {
        listeners.remove(listener);
    }

    public void playSong(Context context, List<Song> songs, int index) {
        if (!songs.equals(currentPlaylist)) {
            songHistory.clear();
        } else {
            if (!currentPlaylist.isEmpty()) {
                songHistory.push(currentIndex);
            }
        }

        this.currentPlaylist = new ArrayList<>(songs);
        this.currentIndex = index;
        playCurrentSong(context);
    }

    private void playCurrentSong(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (currentPlaylist.isEmpty()) return;

        Song song = currentPlaylist.get(currentIndex);

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            );

            // Play from URL
            mediaPlayer.setDataSource(song.getFileUrl());

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                notifyUI();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeat) {
                    playCurrentSong(context);
                } else {
                    playNext(context);
                }
            });

            // Prepare async because it's a network stream
            mediaPlayer.prepareAsync();
            notifyUI(); // Update UI immediately to show loading or title

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.pause();
            else mediaPlayer.start();
            notifyUI();
        }
    }

    public void playNext(Context context) {
        if (currentPlaylist.isEmpty()) return;
        songHistory.push(currentIndex);

        if (isShuffle) {
            currentIndex = (int) (Math.random() * currentPlaylist.size());
        } else {
            currentIndex = (currentIndex + 1) % currentPlaylist.size();
        }
        playCurrentSong(context);
    }

    public void playPrevious(Context context) {
        if (currentPlaylist.isEmpty()) return;
        if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > 5000) {
            mediaPlayer.seekTo(0);
            return;
        }
        if (!songHistory.isEmpty()) {
            currentIndex = songHistory.pop();
        } else {
            currentIndex = (currentIndex - 1 + currentPlaylist.size()) % currentPlaylist.size();
        }
        playCurrentSong(context);
    }

    private void notifyUI() {
        for (Runnable listener : listeners) {
            if (listener != null) listener.run();
        }
    }

    public boolean toggleShuffle() { isShuffle = !isShuffle; return isShuffle; }
    public boolean toggleRepeat() { isRepeat = !isRepeat; return isRepeat; }
    public boolean isShuffleEnabled() { return isShuffle; }
    public boolean isRepeatEnabled() { return isRepeat; }
    public boolean isPlaying() { return mediaPlayer != null && mediaPlayer.isPlaying(); }

    public Song getCurrentSong() {
        if (currentPlaylist.isEmpty() || currentIndex < 0 || currentIndex >= currentPlaylist.size()) return null;
        return currentPlaylist.get(currentIndex);
    }
    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
}
