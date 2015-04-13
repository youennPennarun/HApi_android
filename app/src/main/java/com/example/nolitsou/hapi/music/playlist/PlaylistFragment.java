package com.example.nolitsou.hapi.music.playlist;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nolitsou.hapi.CustomFragment;
import com.example.nolitsou.hapi.MainActivity;
import com.example.nolitsou.hapi.R;
import com.github.nkzawa.socketio.client.Ack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaylistFragment extends CustomFragment {
    private static final String LOG_STR = "PlaylistFragment";
    public String title = "Playlists";
    private ViewGroup rootView;
    private ListView listView;
    private ArrayList<Playlist> playlists;
    private MainActivity activity;
    private PlaylistsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.playlists, container, false);
        this.activity = (MainActivity) getActivity();

        this.playlists = activity.getSocketService().getUser().getPlaylists();
        this.listView = (ListView) rootView.findViewById(R.id.playlist_list);
        adapter = new PlaylistsListAdapter((MainActivity) getActivity(), playlists, listView);
        System.out.println("NB playlists = " + playlists.size());
        listView.setAdapter(adapter);
        loadData();
        return rootView;
    }

    @Override
    public void loadData() {
        Log.i(LOG_STR, "Getting playlists");
        GetPlaylistsTask task = new GetPlaylistsTask(adapter, playlists);
        task.execute();
    }

    class GetPlaylistsTask extends AsyncTask<String, String, Void> {
        //private ProgressDialog mDialog;
        private Exception error;
        private ProgressDialog mDialog;
        private PlaylistsListAdapter adapter;
        private boolean done;
        private ArrayList<Playlist> values;

        public GetPlaylistsTask(PlaylistsListAdapter adapter, ArrayList<Playlist> values) {
            this.adapter = adapter;
            this.values = values;
        }

        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            if (values == null || values.size() == 0) {
                mDialog.setMessage("Please wait...");
                mDialog.show();
            }
        }

        protected Void doInBackground(String... data) {
            done = false;
            System.out.println("GET PLAYLISTS:start");
            ((MainActivity) getActivity()).getSocketService().getSocket().emit("music:playlists:get", new JSONObject(), new Ack() {
                @Override
                public void call(Object... arg0) {
                    int i = 0;
                    System.out.println("GET PLAYLISTS:got it");
                    if (arg0.length > 0) {
                        JSONObject json = ((JSONObject) arg0[0]);
                        try {
                            if (json.has("status") && json.getString("status").equals("success")) {
                                JSONArray playlistsJSON = json.getJSONObject("playlists").getJSONArray("items");
                                for (i = 0; i < playlistsJSON.length(); i++) {
                                    Playlist p = Playlist.spotifyResultToPlaylist(playlistsJSON.getJSONObject(i));
                                    if (!values.contains(p)) {
                                        values.add(p);
                                    }
                                }
                            }
                            Log.i(LOG_STR, "got " + values.size() + " values");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    done = true;
                }
            });
            while (!done) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            adapter.notifyDataSetChanged();
            if (mDialog.isShowing())
                mDialog.cancel();

        }
    }

}
