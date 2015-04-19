package com.example.nolitsou.hapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.nolitsou.hapi.server.SocketService;

public class NotificationReceiver extends BroadcastReceiver {
    public final static String PLAYER_PLAY = "PLAYER_PLAY";
    public final static String PLAYER_PAUSE = "PLAYER_PAUSE";
    public final static String PLAYER_PREVIOUS = "PLAYER_PREVIOUS";
    public final static String PLAYER_NEXT = "PLAYER_NEXT";

    public static SocketService socketService;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        System.out.println("received " + action);
        PlayerControl playerControl = socketService.getPlayer();
        switch (action) {
            case PLAYER_PLAY:
                playerControl.play();
                break;
            case PLAYER_PAUSE:
                playerControl.pause();
                break;
            case PLAYER_PREVIOUS:
                playerControl.previous();
                break;
            case PLAYER_NEXT:
                playerControl.next();
                break;
            default:
                break;
        }
    }
}