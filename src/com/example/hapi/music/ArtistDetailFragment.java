package com.example.hapi.music;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hapi.CustomFragment;
import com.example.hapi.MainActivity;
import com.example.hapi.R;
import com.example.hapi.TrackListAdapter;
import com.example.hapi.widgets.ExpandableGridView;
import com.example.hapi.widgets.ExpandableListView;
import com.github.nkzawa.socketio.client.Ack;

public class ArtistDetailFragment extends CustomFragment{
	private ViewGroup rootView;
	private String artistId = null;
	private Artist artist;
	public ProgressDialog dialog;
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

	private void updateView(final Artist artist){
		this.artist = artist;
		final ListView list = (ListView) getActivity().findViewById(R.id.artistSearchList);
		getActivity().runOnUiThread(new Runnable(){
			@Override
			public void run() {				
				ExpandableListView listView = (ExpandableListView)rootView.findViewById(R.id.artistDetail_popularList);
				listView.setAdapter(new TrackListAdapter(getActivity(), R.layout.artist_detail,artist.topTracks));
				listView.setExpanded(true);
				
				ExpandableGridView albumList = (ExpandableGridView)rootView.findViewById(R.id.artistDetail_albumList);

				albumList.setAdapter(new AlbumGridAdapter(artist.albums));
				albumList.setExpanded(true);
				((MainActivity)getActivity()).setActionBarTitle(artist.artistName);
				dialog.dismiss();
			}
		});
	}
}
