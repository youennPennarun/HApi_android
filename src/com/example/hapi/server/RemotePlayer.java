package com.example.hapi.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.hapi.PlayerControl;
import com.example.hapi.PlayerFragment;
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
				System.out.println(args[0]);
				try {
					int volume= ((JSONObject)args[0]).getJSONArray("volume").getInt(0);
					PlayerControl.setVolume(Math.round(volume/2));	
					if (playerFragment != null) {
						ServerLink.activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								playerFragment.setVolume(PlayerControl.getVolume());
							}
						});
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
				int i = 0;
				System.out.println(args[0]);
				try {
					System.out.println("trackData="+args[0]);
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
						PlayerControl.setTrackName(track.getString("name"));
					} else {
						PlayerControl.setTrackName("");
					}
					if (playerFragment != null) {
						ServerLink.activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								playerFragment.updateTrack();
							}
						});
					}
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
