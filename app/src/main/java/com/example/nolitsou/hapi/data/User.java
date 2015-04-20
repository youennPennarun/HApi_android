package com.example.nolitsou.hapi.data;

import com.example.nolitsou.hapi.music.userPlaylist.UserPlaylist;

import java.util.ArrayList;

;

public class User {
    private ArrayList<UserPlaylist> userPlaylists = new ArrayList<UserPlaylist>();

    public User() {
    }

    public ArrayList<UserPlaylist> getUserPlaylists() {
        return userPlaylists;
    }

    public void setUserPlaylists(ArrayList<UserPlaylist> userPlaylists) {
        this.userPlaylists = userPlaylists;
    }


}
