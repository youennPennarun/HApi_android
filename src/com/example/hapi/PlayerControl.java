package com.example.hapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.hapi.music.Album;
import com.example.hapi.music.Track;
import com.example.hapi.server.ServerLink;

public class PlayerControl {
	private static String trackName;
	private static String trackArtist;
	private static int volume=0;
	public static void play(){
		if (ServerLink.getSocket() != null) {
			ServerLink.getSocket().emit("pi:sound:resume", new JSONObject());
		}
	}
	public static void pause(){
		if (ServerLink.getSocket() != null) {
			ServerLink.getSocket().emit("pi:sound:pause", new JSONObject());
		}
	}
	public static void previous(){
		if (ServerLink.getSocket() != null) {
			ServerLink.getSocket().emit("pi:sound:previous", new JSONObject());
		}
	}
	public static void next(){
		if (ServerLink.getSocket() != null) {
			ServerLink.getSocket().emit("pi:sound:next", new JSONObject());
		}
	}
	public static String getTrackName() {
		return trackName;
	}
	public static void setTrackName(String trackName) {
		PlayerControl.trackName = trackName;
	}
	public static String getTrackArtist() {
		return trackArtist;
	}
	public static void setTrackArtist(String trackArtist) {
		PlayerControl.trackArtist = trackArtist;
	}
	public static int getVolume() {
		return volume;
	}
	public static void saveVolume(int volume) {
		if (ServerLink.getSocket() != null) {
			JSONObject data = new JSONObject();
			try {
				data.put("volume", volume+50);
				ServerLink.getSocket().emit("pi:sound:volume:set", data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void setVolume(int volume) {
		PlayerControl.volume = volume;
	}
	public static void playRandom() {
        ServerLink.getSocket().emit("music:discovering", new JSONObject());
	}
	public static void playTrack(Track track) {
		JSONObject data = new JSONObject();
		JSONObject trackData = new JSONObject();
		try {
			data.put("type", "track");
			trackData.put("name", track.getName());
			trackData.put("id", track.getSpotify_id());
			trackData.put("uri", track.getSpotify_uri());
			data.put("track", trackData);
			ServerLink.getSocket().emit("pi:sound:play", data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void playAlbum(Album album) {
		JSONObject json = new JSONObject();
		try {
			json.put("type", "trackset");
			JSONArray list = new JSONArray();
			for(Track track :  album.getTracks()) {
				System.out.println(track.getName());
				list.put(track.toJSON());
			}
			json.put("tracks", list);
			ServerLink.getSocket().emit("pi:sound:play", json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
