package com.example.nolitsou.hapi;

import android.os.Bundle;

import com.example.nolitsou.hapi.music.playlist.PlaylistContainer;

/**
 * Created by nolitsou on 4/15/15.
 */
public class PlaylistActivity extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_main);
        createDefault();
    }
    @Override
    protected void loadData() {
        PlaylistContainer container = (PlaylistContainer)findViewById(R.id.playlists);
        container.loadData();

    }
}
