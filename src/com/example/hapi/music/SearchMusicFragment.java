package com.example.hapi.music;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.hapi.R;
import com.example.hapi.SearchArtistAdapter;
import com.github.nkzawa.socketio.client.Ack;

public class SearchMusicFragment extends CustomFragment{
	private ViewGroup rootView;
	private ListView listView;
	private ArrayList<SearchArtist> results = new ArrayList<SearchArtist>();
	private MainActivity activity;
	private String searchQuery;
	public SearchMusicFragment(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	public SearchMusicFragment() {
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(
				R.layout.search_music, container, false);
		this.activity = (MainActivity) getActivity();
		final TextView searchQueryTV = (TextView)rootView.findViewById(R.id.searchQuery);
		ImageView searchBtn = (ImageView)rootView.findViewById(R.id.searchBtn);
		this.listView = (ListView)rootView.findViewById(R.id.artistSearchList);
		listView.setAdapter(new SearchArtistAdapter(getActivity(), results));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				SearchArtist clicked = ((SearchArtist)listView.getItemAtPosition(position));
				activity.changeFragment(new ArtistDetailFragment(clicked.artistId));
			}
		});
		if(searchQuery != null) {
			searchQueryTV.setText(searchQuery);
			SearchArtist.searchArtist(searchQuery, new SearchArtistAck());
		}
		searchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
			
				SearchArtist.searchArtist(searchQueryTV.getText().toString(), new SearchArtistAck());
			}
		});
		return rootView;
	}
	private void updateList(final ArrayList<SearchArtist> results){
		this.results = results;
		final ListView list = (ListView) getActivity().findViewById(R.id.artistSearchList);
		getActivity().runOnUiThread(new Runnable(){
			@Override
			public void run() {
				listView.setAdapter(new SearchArtistAdapter(getActivity(), results));
			}
		});
	}
	private class SearchArtistAck implements Ack {

		@Override
		public void call(Object... arg0) {
			if (arg0.length > 0) {
				JSONObject json = (JSONObject)arg0[0];
				updateList(SearchArtist.handleSearchResult(json));
				
			}
		}

	}
}
