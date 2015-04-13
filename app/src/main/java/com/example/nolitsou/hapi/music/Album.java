package com.example.nolitsou.hapi.music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Album {
    private String name;
    private String spotify_id;
    private String type;
    private ArrayList<Track> tracks = null;
    private String spotify_uri;
    private ArrayList<String> imageUrls;

    public Album(String name, String spotify_id, ArrayList<Track> tracks) {
        super();
        this.name = name;
        this.spotify_id = spotify_id;
        this.tracks = tracks;
    }

    public Album(String name, String type, ArrayList<String> imageUrls, String spotify_id, String spotify_uri) {
        this.name = name;
        this.setImageUrls(imageUrls);
        this.type = type;
        this.spotify_id = spotify_id;
        this.spotify_uri = spotify_uri;
    }

    public static Album spotifyResultToAlbum(JSONObject json) throws JSONException {
        String albumName = json.getString("name");
        String album_spotify_id = json.getString("id");
        ArrayList<Track> trackList = new ArrayList<Track>();

        JSONArray tracksJSON = json.getJSONObject("tracks").getJSONArray("items");
        for (int i = 0; i < tracksJSON.length(); i++) {
            trackList.add(Track.spotifyResultToTrack(tracksJSON.getJSONObject(i)));
        }
        return new Album(albumName, album_spotify_id, trackList);
    }

    public static Album jsonToAlbum(JSONObject json) {
        try {
            String name = json.getString("name");
            String type = json.getString("type");
            JSONArray imagesJson = json.getJSONArray("images");
            ArrayList<String> imageUrls = new ArrayList<String>();
            for (int i = 0; i < imagesJson.length(); i++) {
                imageUrls.add(imagesJson.getJSONObject(i).getString("url"));
            }
            JSONObject spotifyData = json.getJSONObject("spotifyData");
            String spotify_id = spotifyData.getString("id");
            String spotify_uri = spotifyData.getString("uri");
            return new Album(name, type, imageUrls, spotify_id, spotify_uri);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
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

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }


}
