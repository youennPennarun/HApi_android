package com.example.nolitsou.hapi.music.playlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.R;
import com.github.nkzawa.socketio.client.Ack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaylistContainer extends LinearLayout {
    private static final String LOG_STR = "PlaylistFragment";
    public String title = "Playlists";
    private ArrayList<Playlist> playlists;
    private PlaylistsListAdapter adapter;

    public PlaylistContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
    }

    public void loadData() {
        this.playlists = ((AbstractActivity) getContext()).getSocketService().getUser().getPlaylists();
        ListView listView = (ListView) getRootView().findViewById(R.id.playlist_list);
        adapter = new PlaylistsListAdapter((AbstractActivity) getContext(), playlists, listView);
        listView.setAdapter(adapter);
        Log.i(LOG_STR, "Getting playlists");
        if (((AbstractActivity) getContext()).getSocketService().isConnected()) {
            GetPlaylistsTask task = new GetPlaylistsTask(adapter, playlists);
            task.execute();
        } else {
            Toast toast = new Toast(getContext());
            toast.setText("Not connected");
            toast.show();
        }
    }

    private class GetPlaylistsTask extends AsyncTask<String, String, Void> {
        //private ProgressDialog mDialog;
        private ProgressDialog mDialog;
        private PlaylistsListAdapter adapter;
        private boolean done;
        private ArrayList<Playlist> values;

        public GetPlaylistsTask(PlaylistsListAdapter adapter, ArrayList<Playlist> values) {
            this.adapter = adapter;
            this.values = values;
        }

        protected void onPreExecute() {
            mDialog = new ProgressDialog(getContext());
            if (values == null || values.size() == 0) {
                mDialog.setMessage("Please wait...");
                mDialog.show();
            }
        }

        protected Void doInBackground(String... data) {
            done = false;
            System.out.println("GET PLAYLISTS:start");
            ((AbstractActivity) getContext()).getSocketService().getSocket().emit("music:playlists:get", new JSONObject(), new Ack() {
                @Override
                public void call(Object... arg0) {
                    int i;
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
