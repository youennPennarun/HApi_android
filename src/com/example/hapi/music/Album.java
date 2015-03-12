package com.example.hapi.music;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Album {
	private String name;
	private String spotify_id;
	private ArrayList<Track> tracks = new ArrayList<Track>();
	public Album(String name, String spotify_id, ArrayList<Track> tracks) {
		super();
		this.name = name;
		this.spotify_id = spotify_id;
		this.tracks = tracks;
	}
	public static Album spotifyResultToAlbum(JSONObject json) throws JSONException {
		String albumName = json.getString("name");
		String album_spotify_id = json.getString("id");
		ArrayList<Track> trackList = new ArrayList<Track>(); 
		
		JSONArray tracksJSON = json.getJSONObject("tracks").getJSONArray("items");
		for(int i = 0; i < tracksJSON.length(); i++) {
			trackList.add(Track.spotifyResultToTrack(tracksJSON.getJSONObject(i)));
		}
		return new Album(albumName, album_spotify_id, trackList);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSpotify_id() {
		return spotify_id;
	}
	public void setSpotify_id(String spotify_id) {
		this.spotify_id = spotify_id;
	}
	public ArrayList<Track> getTracks() {
		return tracks;
	}
	public void setTracks(ArrayList<Track> tracks) {
		this.tracks = tracks;
	}
	
	
}
