package com.example.hapi.music;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.hapi.server.ServerLink;
import com.github.nkzawa.socketio.client.Ack;

public class Artist {
	public String artistId;
	public String artistName;
	public ArrayList<String> imageUrls;
	public ArrayList<Track> topTracks;
	public ArrayList<Album> albums;


	public Artist(String artistId, String artistName,
			ArrayList<String> imageUrls, ArrayList<Track> topTracks,
			ArrayList<Album> albums) {
		super();
		this.artistId = artistId;
		this.artistName = artistName;
		this.imageUrls = imageUrls;
		this.topTracks = topTracks;
		this.albums = albums;
	}


	public static void searchArtistById(String id, Ack ack) {
		System.out.println("search artist with id :" + id);
		JSONObject data = new JSONObject();
		JSONObject artist = new JSONObject();
		try {
			artist.put("id", id);
			data.put("artist", artist);
			ServerLink.getSocket().emit("music:artist:get", data, ack);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static Artist handleSearchById(JSONObject jsonObject) {
		int i = 0;
		int o = 0;
		try {
			String artistId;
			String artistName;
			ArrayList<String> imageUrls = new ArrayList<String>();
			ArrayList<Track> topTracks = new ArrayList<Track>();
			ArrayList<Album> albums = new ArrayList<Album>(); 
			
			JSONObject artistData = jsonObject.getJSONObject("data");
			artistName = artistData.getString("name");
			artistId = artistData.getString("id");
			JSONArray jsonImages = artistData.getJSONArray("images");
			for(i = 0; i < jsonImages.length(); i++) {
				imageUrls.add((jsonImages.getJSONObject(i)).getString("url"));
			}
			JSONArray topTrackJSON = jsonObject.getJSONArray("top_tracks");
			for(i = 0; i < topTrackJSON.length(); i++) {
				System.out.println(topTrackJSON.getJSONObject(i).toString());
				topTracks.add(Track.spotifyResultToTrack(topTrackJSON.getJSONObject(i)));
			}
			JSONArray albumsJSON = jsonObject.getJSONArray("albums");
			for(i = 0; i < albumsJSON.length(); i++) {
				albums.add(Album.spotifyResultToAlbum(albumsJSON.getJSONObject(i)));
			}
			return new Artist(artistId, artistName, imageUrls, topTracks, albums);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
