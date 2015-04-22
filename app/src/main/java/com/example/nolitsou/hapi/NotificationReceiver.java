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
        switch (action) {
            case PLAYER_PLAY:
                //playerControl.play();
                GCMSender.send("pi:sound:resume");
                break;
            case PLAYER_PAUSE:
                GCMSender.send("pi:sound:pause");
                break;
            case PLAYER_PREVIOUS:
                // playerControl.previous();
                GCMSender.send("music:player:previous");
                break;
            case PLAYER_NEXT:
                //playerControl.next();
                GCMSender.send("music:player:next");
                break;
            default:
                System.out.println("unknown task");
                break;
        }
    }

}
