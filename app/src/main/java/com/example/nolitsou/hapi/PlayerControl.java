package com.example.nolitsou.hapi;

import com.example.nolitsou.hapi.data.Settings;
import com.example.nolitsou.hapi.music.PlayerContainer;
import com.example.nolitsou.hapi.music.Track;
import com.example.nolitsou.hapi.music.data.Album;
import com.example.nolitsou.hapi.music.userPlaylist.UserPlaylist;
import com.example.nolitsou.hapi.utils.GetJsonTask;
import com.example.nolitsou.hapi.utils.ServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayerControl {
    private static PlayerControl playerControl;
    public final static String STATUS_PLAY = "PLAY";
    public final static String STATUS_PAUSE = "PAUSE";

    private int volume = 0;
    private String status = "PAUSE";
    private final ArrayList<Track> playlist = new ArrayList();
    private int idPlaying;
    private Track playing;
    private static PlayerContainer playerContainer;

    private PlayerControl() {
        updatePlaylist();
    }

    public static PlayerControl getInstance() {
        if (playerControl == null) {
            playerControl = new PlayerControl();
        }
        return playerControl;
    }

    public static void playPlaylist(UserPlaylist userPlaylist) {
        JSONObject json = new JSONObject();
        JSONObject playlistJSON = new JSONObject();
        try {
            json.put("type", "playlist");
            playlistJSON.put("id", userPlaylist.getSpotify_id());
            json.put("playlist", playlistJSON);
            System.out.println("=============GCMSEND");
            GCMSender.send("music:playlist:set", json);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void play() {
        GCMSender.send("pi:sound:resume");
    }

    public void pause() {
        GCMSender.send("pi:sound:pause");
    }

    public void previous() {
    }

    public void next() {
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void saveVolume(int volume) {
    }

    public void playRandom() {
    }

    public void playTrack(Track track) {
    }

    public void playTrackInPlaylist(int position) {
    }

    public void playAlbum(Album album) {
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        if(status.equals(STATUS_PAUSE) || status.equals(STATUS_PLAY)) {
            this.status = status;
            System.out.println("settings status: "+status);
            if(playerContainer != null)
                playerContainer.updateStatus();
        } else {
            System.out.println("PlayerControl.setStatus: unknown status");
        }

    }

    public int getIdPlaying() {
        return idPlaying;
    }

    public void setPlaylist(ArrayList<Track> playlist) {
        this.playlist.addAll(playlist);
    }

    public void setPlaylist(ArrayList<Track> playlist, int idPlaying) {
        this.playlist.addAll(playlist);
        this.idPlaying = idPlaying;
        if (idPlaying >= 0 && idPlaying < playlist.size()) {
            playing = playlist.get(idPlaying);
        }
        if (playerContainer != null) {
            playerContainer.updatePlaying();
        }
    }

    public void setPlaying() {
    }

    public void setPlaying(int id) {
    }

    public Track getPlaying() {
        return playing;
    }

    public void setPlaying(Track playing) {
        this.playing = playing;
    }

    public ArrayList<Track> getPlaylist() {
        return playlist;
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

    public static void setPlayerContainer(PlayerContainer playerContainer) {
        PlayerControl.playerContainer = playerContainer;
    }

    public void updatePlaylist() {
        System.out.println("!!!!!!!!!!!!!!! get playlist");
        new GetJsonTask() {
            @Override
            protected void onPostExecute(ServerResponse result) {
                System.out.println(result.data);
                if (result.success) {
                    setPlaylist(result.data);
                }
            }

        }.execute(Settings.host+"api/player/playlist");
    }
}
