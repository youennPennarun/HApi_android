package com.example.hapi.music;

import org.json.JSONException;
import org.json.JSONObject;

public class Track {
	private String name;
	private String spotify_id;
	private long duration_ms;
	private String spotify_uri;
	
	public Track(String name, String spotify_id, long duration_ms, String spotify_uri) {
		this.setName(name);
		this.setSpotify_id(spotify_id);
		this.setDuration_ms(duration_ms);
		this.setSpotify_uri(spotify_uri);
	}
	public static Track spotifyResultToTrack(JSONObject json) throws JSONException {
		String trackName = json.getString("name");
		String track_spotify_id = json.getString("id");
		long duration_ms = json.getLong("duration_ms");
		String spotify_uri = json.getString("uri");
		return new Track(trackName, track_spotify_id, duration_ms, spotify_uri);
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

	public long getDuration_ms() {
		return duration_ms;
	}

	public void setDuration_ms(long duration_ms) {
		this.duration_ms = duration_ms;
	}
	public String getSpotify_uri() {
		return spotify_uri;
	}
	public void setSpotify_uri(String spotify_uri) {
		this.spotify_uri = spotify_uri;
	}
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("name", name);
			json.put("id", spotify_id);
			json.put("uri", spotify_uri);
			return json;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
