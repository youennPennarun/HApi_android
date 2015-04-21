package com.example.nolitsou.hapi;

import android.util.Log;
import android.widget.Toast;

import com.example.nolitsou.hapi.music.PlayerContainer;
import com.example.nolitsou.hapi.music.Track;
import com.example.nolitsou.hapi.music.data.Album;
import com.example.nolitsou.hapi.music.userPlaylist.UserPlaylist;
import com.example.nolitsou.hapi.server.SocketService;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayerControl {
    public static final int VOLUME_UPDATE = 10;
    public static final int TRACK_UPDATE = 11;
    public static final int TRACK_STATUS = 12;
    public static final int PLAYLIST_UPDATE = 13;
    private static PlayerContainer player;
    private final String STATUS_PLAY = "PLAY";
    private final String STATUS_PAUSE = "PAUSE";
    private int volume = 0;
    private String status = "PAUSE";
    private ArrayList<Track> playlist = new ArrayList();
    private int idPlaying;
    private Track playing;
    private SocketService socketService;

    public PlayerControl(SocketService socketService) {
        this.socketService = socketService;
    }

    public static void setPlayer(PlayerContainer player) {

    }

    public void play() {
        if (socketService.getSocket() != null) {
            socketService.getSocket().emit("pi:sound:resume", new JSONObject());
            Log.i("PlayerControl", "requesting resume");
        }
    }

    public void pause() {
        if (socketService.getSocket() != null) {
            socketService.getSocket().emit("pi:sound:pause", new JSONObject());
            Log.i("PlayerControl", "requesting pause");
        } else {
            Log.w("PlayerControl", "Not connected");
        }
    }

    public void previous() {
        if (socketService.getSocket() != null) {
            socketService.getSocket().emit("music:player:previous", new JSONObject());
            Log.i("PlayerControl", "requesting previous");
        } else {
            Log.w("PlayerControl", "Not connected");
        }
    }

    public void next() {
        if (socketService.getSocket() != null) {
            socketService.getSocket().emit("music:player:next", new JSONObject());
            Log.i("PlayerControl", "requesting next");
        } else {
            Log.w("PlayerControl", "Not connected");
        }
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void saveVolume(int volume) {
        if (socketService.getSocket() != null) {
            JSONObject data = new JSONObject();
            try {
                data.put("volume", volume + 50);
                socketService.getSocket().emit("pi:sound:volume:set", data);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void playRandom() {
        socketService.getSocket().emit("music:discovering", new JSONObject());
    }

    public void playTrack(Track track) {
        JSONObject data = new JSONObject();
        JSONObject trackData = new JSONObject();
        try {
            data.put("type", "track");
            trackData.put("name", track.getName());
            trackData.put("id", track.getSpotify_id());
            trackData.put("uri", track.getSpotify_uri());
            data.put("track", trackData);
            socketService.getSocket().emit("music:playlist:set", data);
            Toast toast = Toast.makeText(socketService, "Playing " + track.getName(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void playTrackInPlaylist(int position) {
        JSONObject data = new JSONObject();
        try {
            data.put("idPlaying", position);
            socketService.getSocket().emit("music:playlist:playing:setId", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void playAlbum(Album album) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "trackset");
            JSONArray list = new JSONArray();
            for (Track track : album.getTracks()) {
                list.put(track.toJSON());
            }
            json.put("tracks", list);
            socketService.getSocket().emit("music:playlist:set", json);
            Toast toast = Toast.makeText(socketService, "Playing " + album.getName(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void playPlaylist(UserPlaylist userPlaylist) {
        JSONObject json = new JSONObject();
        JSONObject playlistJSON = new JSONObject();
        try {
            json.put("type", "playlist");
            playlistJSON.put("id", userPlaylist.getSpotify_id());
            json.put("playlist", playlistJSON);
            socketService.getSocket().emit("music:playlist:set", json);

            Toast toast = Toast.makeText(socketService, "Playing " + userPlaylist.getName(), Toast.LENGTH_SHORT);
            toast.show();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
        System.out.println("got status : " + status);
        //PlayerNotification.create(socketService);
        socketService.broadcast(TRACK_STATUS);

    }
    public int getIdPlaying() {
        return idPlaying;
    }

    public void setPlaylist(ArrayList<Track> playlist) {
        this.playlist = playlist;
    }

    public void setPlaylist(ArrayList<Track> playlist, int idPlaying) {
        this.playlist = playlist;
        this.idPlaying = idPlaying;
        socketService.broadcast(PLAYLIST_UPDATE);
        setPlaying();
    }

    public void setPlaying() {
        if (idPlaying > -1 && idPlaying < playlist.size()) {
            playing = this.playlist.get(idPlaying);
            setStatus("PLAY");
            //PlayerNotification.create(socketService);
        } else {
            playing = null;
        }
        socketService.broadcast(TRACK_UPDATE);
    }

    public void setPlaying(int id) {
        idPlaying = id;
        setPlaying();
    }

    public Track getPlaying() {
        return playing;
    }
    public ArrayList<Track> getPlaylist() {
        return playlist;
    }
    public void setPlaying(Track playing) {
        this.playing = playing;
    }

    public void setSocket(Socket socket) {
        socket.on("pi:notify:sound:volume", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String trackArtists = "";
                int i = 0;
                try {
                    if (((JSONObject) args[0]).has("volume")) {
                        int volume = ((JSONObject) args[0]).getJSONArray("volume").getInt(0);
                        setVolume(volume - 50);
                        socketService.broadcast(PlayerControl.VOLUME_UPDATE);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        })
                /*.on("pi:player:status", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String status = null;
                try {
                    JSONObject data = ((JSONObject) args[0]);
                    if (data.has("status")) {
                        status = data.getString("status");
                        setStatus(status);
                    }0
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }).on("music:playlist:playing:id", new Emitter.Listener() {
            @Override
            public void call(Object... arg0) {
                if (arg0.length > 0) {
                    JSONObject json = (JSONObject) arg0[0];
                    if (json.has("idPlaying")) {
                        try {
                            setPlaying(json.getInt("idPlaying"));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            }
        })*/.on("music:playlist:get", new Emitter.Listener() {
            @Override
            public void call(Object... arg0) {
                if (arg0.length > 0) {
                    JSONObject response = ((JSONObject) arg0[0]);
                    setPlaylist(response);
                }

            }
        }).on("music:playlist:set", new Emitter.Listener() {
            @Override
            public void call(Object... arg0) {
                if (arg0.length > 0) {
                    JSONObject response = ((JSONObject) arg0[0]);
                    try {
                        ArrayList<Track> playlist = new ArrayList<Track>();
                        if (response.getString("type").equals("trackset")) {
                            JSONArray playlistJson;
                            playlistJson = response.getJSONArray("tracks");
                            for (int i = 0; i < playlistJson.length(); i++) {
                                playlist.add(Track.jsonToTrack(playlistJson.getJSONObject(i)));
                            }
                        } else if (response.getString("type").equals("trackset")) {
                            JSONObject trackJson = response.getJSONObject("track");
                            playlist.add(Track.jsonToTrack(trackJson));
                        }
                        setPlaylist(playlist, 0);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void setPlaylist(JSONObject response) {
        JSONArray playlistJson;
        try {
            playlistJson = response.getJSONArray("playlist");
            ArrayList<Track> playlist = new ArrayList<Track>();
            for (int i = 0; i < playlistJson.length(); i++) {
                playlist.add(Track.jsonToTrack(playlistJson.getJSONObject(i)));
            }
            setPlaylist(playlist, response.getInt("idPlaying"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
