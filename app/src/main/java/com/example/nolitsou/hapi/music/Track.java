package com.example.nolitsou.hapi.music;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.nolitsou.hapi.data.SocketData;
import com.example.nolitsou.hapi.music.data.Album;
import com.example.nolitsou.hapi.music.data.Artist;
import com.github.nkzawa.socketio.client.Ack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Track extends SocketData {
    private Bitmap cover = null;
    private String name;
    private String spotify_id;
    private long duration_ms;
    private String spotify_uri;
    private ArrayList<Artist> artists;
    private Album album;

    public Track(String name, String spotify_id, long duration_ms, String spotify_uri) {

        this.setName(name);
        this.setSpotify_id(spotify_id);
        this.setDuration_ms(duration_ms);
        this.setSpotify_uri(spotify_uri);
        getTrackData();
    }

    public Track(String name, Artist artist, String spotify_id, long duration_ms, String spotify_uri) {
        this.setName(name);
        this.artists = new ArrayList<Artist>();
        this.artists.add(artist);
        this.setSpotify_id(spotify_id);
        this.setDuration_ms(duration_ms);
        this.setSpotify_uri(spotify_uri);
        getTrackData();
    }

    public Track(String name, String spotify_id, long duration_ms, String spotify_uri, boolean loadData) {
        this.setName(name);
        this.setSpotify_id(spotify_id);
        this.setDuration_ms(duration_ms);
        this.setSpotify_uri(spotify_uri);
        if (loadData) {
            getTrackData();
        }
    }

    public Track(String trackName, long duration_ms, String spotify_id,
                 String spotify_uri, ArrayList<Artist> artists, Album album) {
        this.name = trackName;
        this.duration_ms = duration_ms;
        this.spotify_id = spotify_id;
        this.spotify_uri = spotify_uri;
        this.artists = artists;
        this.setAlbum(album);
    }

    public static Track spotifyResultToTrack(JSONObject json) throws JSONException {
        String trackName = json.getString("name");
        String track_spotify_id = json.getString("id");
        long duration_ms = json.getLong("duration_ms");
        String spotify_uri = json.getString("uri");
        return new Track(trackName, track_spotify_id, duration_ms, spotify_uri);
    }

    public static Track jsonToTrack(JSONObject json) {
        try {
            String trackName = json.getString("name");
            long duration_ms = json.getLong("duration_ms");
            JSONObject spotifyData = json.getJSONObject("spotifyData");
            String spotify_id = spotifyData.getString("id");
            String spotify_uri = spotifyData.getString("uri");
            JSONArray artistsJson = json.getJSONArray("artists");
            ArrayList<Artist> artists = new ArrayList<Artist>();
            for (int i = 0; i < artistsJson.length(); i++) {
                artists.add(Artist.jsonToArtist(artistsJson.getJSONObject(i)));
            }
            Album album = Album.jsonToAlbum(json.getJSONObject("album"));
            return new Track(trackName, duration_ms, spotify_id, spotify_uri, artists, album);
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

    public void getTrackData() {
        JSONObject json = new JSONObject();
        try {
            json.put("source", "spotify");
            json.put("id", spotify_id);
            getSocket().emit("music:track:get", json, new Ack() {
                @Override
                public void call(Object... arg0) {
                    if (arg0.length > 0) {
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public String getArtistsStr() {
        String artists = "";
        for (Artist artist : this.artists) {
            artists += artist.getName();
        }
        return artists;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void loadCover(Context context, final Ack ack) {
        final GetTrackCover task = new GetTrackCover(context, this, new Ack() {
            @Override
            public void call(Object... args) {
                if (args.length > 0) {
                    setCover((Bitmap) args[0]);
                }
                ack.call(args);
            }
        });
        task.execute();
    }
}
