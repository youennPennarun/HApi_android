package com.example.nolitsou.hapi;

import android.os.Bundle;

import com.example.nolitsou.hapi.music.userPlaylist.UserPlaylistContainer;

/**
 * Created by nolitsou on 4/15/15.
 */
public class PlaylistActivity extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_playlist_main);
        createDefault();
    }

    @Override
    public void loadData() {
        UserPlaylistContainer container = (UserPlaylistContainer) findViewById(R.id.playlists);
        container.loadData();

    }
}
