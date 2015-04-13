package com.example.nolitsou.hapi.music.playlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nolitsou.hapi.CustomFragment;
import com.example.nolitsou.hapi.MainActivity;
import com.example.nolitsou.hapi.R;
import com.example.nolitsou.hapi.TrackListAdapter;
import com.example.nolitsou.hapi.music.Track;
import com.github.nkzawa.socketio.client.Ack;

import java.util.ArrayList;

public class InPlaylistFragment extends CustomFragment {

    private ViewGroup rootView;
    private Playlist playlist;
    private TrackListAdapter adapter;

    public InPlaylistFragment(Playlist playlist) {
        this.title = playlist.getName();
        this.playlist = playlist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        rootView = (ViewGroup) inflater.inflate(
                R.layout.tracklist, container, false);
        ListView trackLV = (ListView) rootView.findViewById(R.id.trackList);
        if (playlist.getTracks() == null) {
            playlist.setTracks(new ArrayList<Track>());
        }
        adapter = new TrackListAdapter(((MainActivity) getActivity()), R.layout.tracklist, playlist.getTracks());
        trackLV.setAdapter(adapter);
        playlist.loadTracks(new Ack() {
            @Override
            public void call(Object... arg0) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        System.out.println("NOW:" + playlist.getTracks().size());
                    }
                });

            }
        });
        return rootView;

    }

    @Override
    public void loadData() {
        // TODO Auto-generated method stub

    }
}
