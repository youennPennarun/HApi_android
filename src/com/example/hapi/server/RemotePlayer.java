package com.example.hapi.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.SeekBar;

import com.example.hapi.PlayerControl;
import com.example.hapi.PlayerFragment;
import com.example.hapi.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

public class RemotePlayer {
	private static PlayerFragment playerFragment;
	public static void setSocket(Socket socket) {
		socket.on("pi:notify:sound:volume", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				String trackArtists = "";
				int i = 0;
				try {
					System.out.println("got vol?" + args[0]);
					if(((JSONObject)args[0]).has("volume")) {
						int volume= ((JSONObject)args[0]).getJSONArray("volume").getInt(0);
						PlayerControl.setVolume(volume-50);	
						if (playerFragment != null) {
							ServerLink.activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									playerFragment.setVolume(PlayerControl.getVolume());
									((SeekBar)playerFragment.getRootView().findViewById(R.id.volumeBar)).setProgress(PlayerControl.getVolume());
								}
							});
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).on("pi:player:status", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				String status = null;
				System.out.println(args[0]);
				try {
					JSONObject data = ((JSONObject)args[0]);
					if(data.has("status")) {
						status = data.getString("status");
						System.out.println("player set to status "+status);
						PlayerControl.setStatus(status);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}).on("music:playing", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				String trackArtists = "";
				String trackTitle = "";
				int i = 0;
				try {
					JSONObject track = ((JSONObject)args[0]).getJSONObject("track");
					if(track.has("artist")) {
						JSONArray artistsArray = track.getJSONArray("artist");
						for (i = 0; i < artistsArray.length(); i++) {
							trackArtists += artistsArray.getString(i) + ";";
						}
					} else {
						trackArtists = "";
					}
					PlayerControl.setTrackArtist(trackArtists);
					if(track.has("name")) {
						trackTitle = track.getString("name");
					}
					PlayerControl.setPlayingTrackData(trackArtists, trackTitle);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
	}
	public static void setPlayerFragment (PlayerFragment fragment) {
		playerFragment = fragment;
	}
}
