package com.example.nolitsou.hapi;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 2;
    private static final String TAG = "GcmIntentService";

    private static final String SET_VOLUME = "pi:notify:sound:volume";
    private static final String PLAYER_STATUS = "pi:player:status";
    private static final String PLAYER_PLAYING_ID = "music:playlist:playing:id";
    private static final String PLAYER_PLAYLIST_UPDATE = "music:playlist:set";
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;
    private boolean done = false;
    private boolean connected = false;
    private Intent intent;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            System.out.println("received: "+extras.toString());
            this.intent = intent;

            final PlayerControl player = PlayerControl.getInstance();
            System.out.println("GOT------->" + extras);
            if (extras.containsKey(PLAYER_STATUS)) {
                System.out.println("player set status: "+extras.getString(PLAYER_STATUS));
                player.setStatus(extras.getString(PLAYER_STATUS));
            }
            if (extras.containsKey(SET_VOLUME)) {
                player.setVolume(extras.getInt(SET_VOLUME) - 50);
            }
            if (extras.containsKey(PLAYER_PLAYING_ID)) {
                player.setPlaying(Integer.valueOf(extras.getString(PLAYER_PLAYING_ID)));
            }
            if (extras.containsKey(PLAYER_PLAYLIST_UPDATE)) {
                PlayerControl.getInstance().updatePlaylist();
            }

        } else {
            System.out.println("empty msg");
        }
        GcmReceiver.completeWakefulIntent(intent);
    }

}