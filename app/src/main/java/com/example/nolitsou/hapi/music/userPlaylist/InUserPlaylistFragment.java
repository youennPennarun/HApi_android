package com.example.nolitsou.hapi.music.userPlaylist;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.R;
import com.example.nolitsou.hapi.music.Track;
import com.example.nolitsou.hapi.music.TrackListAdapter;
import com.github.nkzawa.socketio.client.Ack;

import java.util.ArrayList;

public class InUserPlaylistFragment extends Fragment {

    private ViewGroup rootView;
    private UserPlaylist userPlaylist;
    private TrackListAdapter adapter;

    public InUserPlaylistFragment(UserPlaylist userPlaylist) {
        this.userPlaylist = userPlaylist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        rootView = (ViewGroup) inflater.inflate(
                R.layout.tracklist, container, false);
        ListView trackLV = (ListView) rootView.findViewById(R.id.trackList);
        if (userPlaylist.getTracks() == null) {
            userPlaylist.setTracks(new ArrayList<Track>());
        }
        adapter = new TrackListAdapter(((AbstractActivity) getActivity()), R.layout.tracklist, userPlaylist.getTracks());
        trackLV.setAdapter(adapter);
        userPlaylist.loadTracks(new Ack() {
            @Override
            public void call(Object... arg0) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
        return rootView;

    }

}
