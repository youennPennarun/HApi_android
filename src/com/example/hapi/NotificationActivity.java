package com.example.hapi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotificationActivity extends BroadcastReceiver {
	public final static  String PLAYER_PLAY = "PLAYER_PLAY";
	public final static  String PLAYER_PAUSE = "PLAYER_PAUSE";
	public final static  String PLAYER_PREVIOUS = "PLAYER_PREVIOUS";
	public final static  String PLAYER_NEXT = "PLAYER_NEXT";


	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		System.out.println("received "+action);
		switch (action) {
		case PLAYER_PLAY:
			PlayerControl.play();
			break;
		case PLAYER_PAUSE:
			PlayerControl.pause();
			break;
		case PLAYER_PREVIOUS:
			PlayerControl.previous();
			break;
		case PLAYER_NEXT:
			PlayerControl.next();
			break;
		default:
			break;
		}
	}
} 