package com.example.nolitsou.hapi.music.data;

import com.example.nolitsou.hapi.data.SocketData;
import com.example.nolitsou.hapi.music.Track;
import com.github.nkzawa.socketio.client.Ack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Artist extends SocketData {
    public String artistId;
    public String spotify_uri;
    public ArrayList<String> imageUrls;
    public ArrayList<Track> topTracks;
    public ArrayList<Album> albums;
    private String name;


    public Artist(String artistId, String artistName,
                  ArrayList<String> imageUrls, ArrayList<Track> topTracks,
                  ArrayList<Album> albums) {
        super();
        this.artistId = artistId;
        this.setName(artistName);
        this.imageUrls = imageUrls;
        this.topTracks = topTracks;
        this.albums = albums;
    }

    public Artist(String name, String spotify_id, String spotify_uri) {
        this.setName(name);
        this.artistId = spotify_id;
        this.spotify_uri = spotify_uri;
    }


    public static void searchArtistById(String id, Ack ack) {
        System.out.println("search artist with id :" + id);
        JSONObject data = new JSONObject();
        JSONObject artist = new JSONObject();
        try {
            artist.put("id", id);
            data.put("artist", artist);
            activity.getSocketService().getSocket().emit("music:artist:get", data, ack);
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
            for (i = 0; i < jsonImages.length(); i++) {
                imageUrls.add((jsonImages.getJSONObject(i)).getString("url"));
            }
            JSONArray topTrackJSON = jsonObject.getJSONArray("top_tracks");
            for (i = 0; i < topTrackJSON.length(); i++) {
                topTracks.add(Track.spotifyResultToTrack(topTrackJSON.getJSONObject(i)));
            }
            JSONArray albumsJSON = jsonObject.getJSONArray("albums");
            for (i = 0; i < albumsJSON.length(); i++) {
                albums.add(Album.spotifyResultToAlbum(albumsJSON.getJSONObject(i)));
            }
            return new Artist(artistId, artistName, imageUrls, topTracks, albums);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public static Artist jsonToArtist(JSONObject json) {
        try {
            String name = json.getString("name");
            JSONObject spotifyData = json.getJSONObject("spotifyData");
            String spotify_id = spotifyData.getString("id");
            String spotify_uri = spotifyData.getString("uri");
            return new Artist(name, spotify_id, spotify_uri);
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
}
