package com.example.nolitsou.hapi.music;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.R;
import com.example.nolitsou.hapi.music.data.Artist;
import com.example.nolitsou.hapi.widgets.ExpandableGridView;
import com.example.nolitsou.hapi.widgets.ExpandableListView;
import com.github.nkzawa.socketio.client.Ack;

import org.json.JSONException;
import org.json.JSONObject;

public class ArtistDetailFragment extends Fragment {
    public ProgressDialog dialog;
    private ViewGroup rootView;
    private String artistId = null;
    private Artist artist;
    private LayoutInflater inflater;

    public ArtistDetailFragment(String artistId) {
        this.artistId = artistId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.artist_detail, container, false);
        this.inflater = inflater;
        dialog = ProgressDialog.show(getActivity(), "",
                "Searching...", true);
        if (artistId != null) {
            Artist.searchArtistById(artistId, new Ack() {
                @Override
                public void call(Object... arg0) {
                    if (arg0.length > 0) {
                        JSONObject response = (JSONObject) arg0[0];

                        try {
                            Artist artist = Artist.handleSearchById(response.getJSONObject("result"));
                            updateView(artist);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return rootView;
    }

    private void updateView(final Artist artist) {
        this.artist = artist;
        final ListView list = (ListView) getActivity().findViewById(R.id.artistSearchList);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.artistDetail_popularList);
                listView.setAdapter(new TrackListAdapter(((AbstractActivity) getActivity()), R.layout.artist_detail, artist.topTracks));
                listView.setExpanded(true);

                ExpandableGridView albumList = (ExpandableGridView) rootView.findViewById(R.id.artistDetail_albumList);

                albumList.setAdapter(new AlbumGridAdapter(((AbstractActivity) getActivity()), artist.albums));
                albumList.setExpanded(true);
                ((AbstractActivity) getActivity()).setActionBarTitle(artist.getName());
                dialog.dismiss();
            }
        });
    }

}
