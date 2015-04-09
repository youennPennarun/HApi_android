package com.example.hapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.example.hapi.music.Album;
import com.example.hapi.music.Track;
import com.example.hapi.music.playlist.Playlist;
import com.example.hapi.server.ServerLink;

public class PlayerControl {
	private static final String STATUS_PLAY = "PLAY";
	private static final String STATUS_PAUSE = "PAUSE";
	private static String trackName;
	private static String trackArtist;
	private static int volume=0;
	private static String status;
	private static PlayerFragment playerFragment;
	private static Context activity;
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
			Toast toast = Toast.makeText(getActivity(), "Playing " + track.getName(), Toast.LENGTH_SHORT);
			toast.show();
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
				list.put(track.toJSON());
			}
			json.put("tracks", list);
			ServerLink.getSocket().emit("pi:sound:play", json);
			Toast toast = Toast.makeText(getActivity(), "Playing " + album.getName(), Toast.LENGTH_SHORT);
			toast.show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void playPlaylist(Playlist playlist) {
		JSONObject json = new JSONObject();
		JSONObject playlistJSON = new JSONObject();
		try {
			json.put("type", "playlist");
			playlistJSON.put("id", playlist.getSpotify_id());
			json.put("playlist", playlistJSON);
			ServerLink.getSocket().emit("pi:sound:play", json);

			Toast toast = Toast.makeText(getActivity(), "Playing " + playlist.getName(), Toast.LENGTH_SHORT);
			toast.show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void setStatus(String status) {
		PlayerControl.status = status;
		if(playerFragment != null) {
			System.out.println("update notif player");
			playerFragment.playerStatusChanged();
		} else {
			System.out.println("missing pf");
		}
	}
	public static String getStatus() {
		return PlayerControl.status;
	}
	public static void setPlayerFragment(PlayerFragment pf) {
		playerFragment = pf;
	}
	public static void setPlayingTrackData(String trackArtists,
			String trackTitle) {
		setTrackArtist(trackArtists);
		setTrackName(trackTitle);
		setStatus(STATUS_PLAY);
		System.out.println("set track data");
		if (playerFragment != null) {
			ServerLink.activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					playerFragment.updateTrack();
					if (PlayerControl.getTrackArtist().equals("") && PlayerControl.getTrackName().equals("")) {
						playerFragment.dismissPlayerNotif();
					} else {
						playerFragment.playerNotif();
					}
				}
			});
		}

	}
	public static Context getActivity() {
		return activity;
	}
	public static void setActivity(Context activity) {
		PlayerControl.activity = activity;
	}
}
