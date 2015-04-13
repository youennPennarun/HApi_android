package com.example.nolitsou.hapi.data;

import com.example.nolitsou.hapi.music.playlist.Playlist;

import java.util.ArrayList;

;

public class User {
    private ArrayList<Playlist> playlists = new ArrayList<Playlist>();

    public User() {
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }


}
