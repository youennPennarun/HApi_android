package com.example.hapi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.TextView;

import com.example.hapi.MainActivity;
import com.example.hapi.PlayerControl;
import com.example.hapi.data.Alarm;
import com.example.hapi.music.SearchArtist;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class ServerLink {
	private static Socket socket;
	public static MainActivity activity;
	public static boolean piConnected = false;

	public static boolean connect(String url, String token) {
		if (ServerLink.getSocket() != null) {
			getSocket().disconnect();
		}
		try {
			System.out.println(url + "/?" + token);
			ServerLink.setSocket(IO.socket(url + "?token=" + token));
			ServerLink.setListeners();
			getSocket().connect();
			socket.emit("pi:is-logged-in");

		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void setListeners() {
		getSocket().on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				System.out.println("connected");
				socket.emit("music:playing:get");
				socket.emit("pi:sound:volume:get");
			}

		}).on("pi:is-logged-in", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				if(args.length > 0) {
					if ((boolean) args[0]) {
						activity.getPlayerFragment().setPiConnectedView();
						piConnected = true;
						activity.dismissPiNotif();
					} else {
						activity.getPlayerFragment().setPiNotConnectedView();
						piConnected=false;
						activity.notifPiDisconnected();
					}
				}
			}
		}).on("pi:logged-out", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				activity.getPlayerFragment().setPiNotConnectedView();
				piConnected=false;
				System.out.println("pi logged out");
				activity.notifPiDisconnected();
			}
		}).on("pi:logged-in", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				activity.getPlayerFragment().setPiConnectedView();
				piConnected = true;
				activity.dismissPiNotif();
			}
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {}

		});
		Alarm.setSocket(getSocket());
		SearchArtist.setSocket(getSocket());
		RemotePlayer.setSocket(getSocket());
	}

	public static Socket getSocket() {
		return socket;
	}

	public static void setSocket(Socket socket) {
		ServerLink.socket = socket;
	}

	public static void setMainActivity(MainActivity mainActivity) {
		ServerLink.activity = mainActivity;
	}
}
