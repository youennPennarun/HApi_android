package com.example.hapi.music.playlist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hapi.CustomFragment;
import com.example.hapi.MainActivity;
import com.example.hapi.PlayerControl;
import com.example.hapi.R;
import com.example.hapi.SearchArtistAdapter;
import com.example.hapi.data.Settings;
import com.example.hapi.music.ArtistDetailFragment;
import com.example.hapi.music.SearchArtist;
import com.example.hapi.server.ServerLink;
import com.github.nkzawa.socketio.client.Ack;

public class PlaylistFragment extends CustomFragment{
	private ViewGroup rootView;
	private ListView listView;
	private ArrayList<Playlist> playlists = new ArrayList<Playlist>();
	private MainActivity activity;
	private PlaylistsListAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(
				R.layout.playlists, container, false);
		this.activity = (MainActivity) getActivity();
		this.listView = (ListView)rootView.findViewById(R.id.playlist_list);
		adapter = new PlaylistsListAdapter(getActivity(), playlists, listView);
		listView.setAdapter(adapter);
		loadPlaylists();
		return rootView;
	}
	private void loadPlaylists() {
		GetPlaylistsTask task = new GetPlaylistsTask(activity, adapter, playlists);
		task.execute();
	}
}

class GetPlaylistsTask extends AsyncTask<String, String, Void> {
	private MainActivity mainActivity;
	//private ProgressDialog mDialog;
	private Exception error;
	private ProgressDialog mDialog;
	private PlaylistsListAdapter adapter;
	private boolean done;
	private ArrayList<Playlist> values;

	public GetPlaylistsTask(MainActivity mainActivity, PlaylistsListAdapter adapter, ArrayList<Playlist> values) {
		this.mainActivity = mainActivity;
		this.adapter = adapter;
		this.values = values;
	}

	protected void onPreExecute() {
		mDialog = new ProgressDialog(mainActivity);
		mDialog.setMessage("Please wait...");
		mDialog.show();
	}

	protected Void doInBackground(String... data) {
		done = false;
		ServerLink.getSocket().emit("music:playlists:get", new JSONObject(), new Ack() {
			@Override
			public void call(Object... arg0) {
				int i = 0;
				if (arg0.length > 0) {
					JSONObject json = ((JSONObject)arg0[0]);
					values.clear();
					try {
						if (json.has("status") && json.getString("status").equals("success")) {
							JSONArray playlistsJSON = json.getJSONObject("playlists").getJSONArray("items");
							for (i = 0; i < playlistsJSON.length(); i ++) {
								values.add(Playlist.spotifyResultToPlaylist(playlistsJSON.getJSONObject(i)));
							}
							System.out.println("REALLY DONE!!");
							done = true;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		while(!done) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("returning");
		return null;
	}
	protected void onPostExecute(Void v) {
		adapter.notifyDataSetChanged();
		if(mDialog.isShowing())
			mDialog.cancel();

	}
	private void showError(String error) {
		//mDialog.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setMessage(error)
		.setTitle("Error!");
		AlertDialog dialog = builder.create();
		dialog.show();

	}
}
