package com.example.nolitsou.hapi.music.userPlaylist;

import android.graphics.Bitmap;

import com.example.nolitsou.hapi.data.SocketData;
import com.example.nolitsou.hapi.music.Track;
import com.github.nkzawa.socketio.client.Ack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserPlaylist extends SocketData {
    private String name;
    private String spotify_id;
    private String spotify_uri;
    private int nbTracks;
    private String imageUri;
    private Bitmap imageBitmap;
    private ArrayList<Track> tracks = new ArrayList<Track>();

    public UserPlaylist(String name, String spotify_id, String spotify_uri, int nbTracks, String imageUri) {
        super();
        this.name = name;
        this.spotify_id = spotify_id;
        this.setSpotify_uri(spotify_uri);
        this.tracks = null;
        this.nbTracks = nbTracks;
        this.imageUri = imageUri;
    }

    public static UserPlaylist spotifyResultToPlaylist(JSONObject json) throws JSONException {
        String name = json.getString("name");
        String spotify_id = json.getString("id");
        String spotify_uri = json.getString("uri");
        int nbTracks = json.getJSONObject("tracks").getInt("total");
        String imageUri = null;
        if (json.has("images") && json.getJSONArray("images").length() > 0) {
            imageUri = json.getJSONArray("images").getJSONObject(0).getString("url");
        }
        return new UserPlaylist(name, spotify_id, spotify_uri, nbTracks, imageUri);
    }

    public void loadTracks(final Ack ack) {
        JSONObject data = new JSONObject();
        try {
            data.put("source", "spotify");
            data.put("id", spotify_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public String getSpotify_uri() {
        return spotify_uri;
    }

    public void setSpotify_uri(String spotify_uri) {
        this.spotify_uri = spotify_uri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof UserPlaylist && this.spotify_id.equals(((UserPlaylist) object).getSpotify_id()));
    }


}
