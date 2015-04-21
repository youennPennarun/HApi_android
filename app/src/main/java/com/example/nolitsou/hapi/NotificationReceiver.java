package com.example.nolitsou.hapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.nolitsou.hapi.server.SocketService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationReceiver extends BroadcastReceiver {
    public final static String PLAYER_PLAY = "PLAYER_PLAY";
    public final static String PLAYER_PAUSE = "PLAYER_PAUSE";
    public final static String PLAYER_PREVIOUS = "PLAYER_PREVIOUS";
    public final static String PLAYER_NEXT = "PLAYER_NEXT";
    private static AtomicInteger msgId = new AtomicInteger();

    public static SocketService socketService;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = socketService.gcm;

        String action = intent.getAction();
        System.out.println("received " + action);
        PlayerControl playerControl = socketService.getPlayer();
        Send task = null;
        switch (action) {
            case PLAYER_PLAY:
                //playerControl.play();
                task = new Send(gcm, "pi:sound:resume");
                break;
            case PLAYER_PAUSE:
                playerControl.pause();
                task = new Send(gcm, "pi:sound:pause");
                break;
            case PLAYER_PREVIOUS:
               // playerControl.previous();
                task = new Send(gcm, "music:player:previous");
                break;
            case PLAYER_NEXT:
                //playerControl.next();
                task = new Send(gcm, "music:player:next");
                break;
            default:
                break;
        }
        if (task != null) {
            task.execute();
        }
    }
    class Send extends AsyncTask {
        private final String message;
        private final GoogleCloudMessaging gcm;

        Send(GoogleCloudMessaging gcm, String message) {
            this.message = message;
            this.gcm = gcm;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            String msg = "";
            try {
                Bundle data = new Bundle();
                data.putString("msg", message);
                String id = Integer.toString(msgId.incrementAndGet());
                gcm.send(SocketService.PROJECT_NUMBER + "@gcm.googleapis.com", id, data);
                System.out.println("--->sending:"+message);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }
    }
}