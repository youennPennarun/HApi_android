package com.example.nolitsou.hapi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.example.nolitsou.hapi.music.Track;
import com.example.nolitsou.hapi.server.SocketService;
import com.github.nkzawa.socketio.client.Ack;

public class PlayerNotification {
    public final static int PLAYER_NOTIF_ID = 1;
    public static RemoteViews playerRemoteViews;
    private static SocketService socketService;
    private static NotificationCompat.Builder mBuilder;

    public static NotificationCompat.Builder getBuilder(SocketService socketService) {
        final Track playing = socketService.getPlayer().getPlaying();
        NotificationReceiver.socketService = socketService;
        PlayerNotification.socketService = socketService;

        playerRemoteViews = new RemoteViews(socketService.getPackageName(),
                R.layout.player_notification);
        playerRemoteViews.setTextViewText(R.id.player_notif_trackName, playing.getName());
        playerRemoteViews.setTextViewText(R.id.player_notif_artist, playing.getArtistsStr());
        /*
        if (socketService.getPlayer().getPlaying().getCover() != null) {
            playerRemoteViews.setImageViewBitmap(R.id.player_notif_cover, socketService.getPlayer().getPlaying().getCover());
        }
        */
        if (playing.getCover() != null) {
            playerRemoteViews.setImageViewBitmap(R.id.player_notif_cover, playing.getCover());
        } else {
            playing.loadCover(socketService, new Ack() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0) {
                        playerRemoteViews.setImageViewBitmap(R.id.player_notif_cover, playing.getCover());
                    }
                }
            });
        }

        Intent openAppReveive = new Intent(socketService, AbstractActivity.class);
        openAppReveive.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        openAppReveive.setAction(AbstractActivity.ACTION_PLAYER_SHOW);
        openAppReveive.putExtra("action", AbstractActivity.ACTION_PLAYER_SHOW);
        PendingIntent pendingIntentShow = PendingIntent.getActivity(socketService, 0, openAppReveive, 0);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_cover, pendingIntentShow);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_trackData, pendingIntentShow);

        Intent playReceive = new Intent(socketService, NotificationReceiver.class);
        playReceive.setAction(NotificationReceiver.PLAYER_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(socketService, 0, playReceive, 0);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_play, pendingIntentPlay);

        Intent pauseReceive = new Intent(socketService, NotificationReceiver.class);
        pauseReceive.setAction(NotificationReceiver.PLAYER_PAUSE);
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(socketService, 12345, pauseReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_pause, pendingIntentPause);
        Intent nextReceive = new Intent(socketService, NotificationReceiver.class);
        nextReceive.setAction(NotificationReceiver.PLAYER_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(socketService, 12345, nextReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_next, pendingIntentNext);
        Intent previousReceive = new Intent(socketService, NotificationReceiver.class);
        previousReceive.setAction(NotificationReceiver.PLAYER_PREVIOUS);
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(socketService, 12345, previousReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_previous, pendingIntentPrevious);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                socketService).setSmallIcon(R.drawable.ic_launcher).setContent(
                playerRemoteViews);
        return mBuilder;
    }

    public static RemoteViews create(SocketService socketService) {
        NotificationCompat.Builder mBuilder = getBuilder(socketService);
        NotificationManager mNotificationManager = (NotificationManager) socketService.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(PLAYER_NOTIF_ID, mBuilder.build());
        playerStatusChanged();
        return playerRemoteViews;
    }

    public static void dismissPlayerNotif(SocketService socketService) {
        PlayerNotification.socketService = socketService;
        try {
            NotificationManager mNotificationManager =
                    (NotificationManager) socketService.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(PLAYER_NOTIF_ID);
        } catch (NullPointerException e) {

        }
    }

    public static void playerStatusChanged() {
        if (playerRemoteViews != null) {
            if (socketService.getPlayer().getStatus().equals("PLAY")) {
                playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.GONE);
                playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.VISIBLE);
            } else if (socketService.getPlayer().getStatus().equals("PAUSE")) {
                playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.VISIBLE);
                playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.GONE);
            }
            NotificationManager mNotificationManager = (NotificationManager) socketService.getSystemService(Context.NOTIFICATION_SERVICE);
            PlayerNotification.mBuilder = new NotificationCompat.Builder(
                    socketService).setSmallIcon(R.drawable.ic_launcher).setContent(
                    playerRemoteViews);
            mNotificationManager.notify(PLAYER_NOTIF_ID, mBuilder.build());

        }
    }
    public static void update(SocketService socketService) {
        AppWidgetManager manager = AppWidgetManager.getInstance(socketService);
        System.out.println("---UPDATE---");
        final PlayerControl player = socketService.getPlayer();
        if (playerRemoteViews == null) {
            create(socketService);
        }
        if (player.getPlaying() != null) {
            System.out.println("STATUS="+socketService.getPlayer().getStatus());
            if (socketService.getPlayer().getStatus().equals("PLAY")) {
                playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.GONE);
                playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.VISIBLE);
            } else if (socketService.getPlayer().getStatus().equals("PAUSE")) {
                playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.VISIBLE);
                playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.GONE);
            }
            playerRemoteViews.setTextViewText(R.id.player_notif_trackName, player.getPlaying().getName());
            playerRemoteViews.setTextViewText(R.id.player_notif_artist, player.getPlaying().getArtistsStr());
            if (player.getPlaying().getCover() != null) {
                playerRemoteViews.setImageViewBitmap(R.id.player_notif_cover, player.getPlaying().getCover());
            } else {
                player.getPlaying().loadCover(socketService, new Ack() {
                    @Override
                    public void call(Object... args) {
                        if (args.length > 0) {
                            playerRemoteViews.setImageViewBitmap(R.id.player_notif_cover, player.getPlaying().getCover());
                        }
                    }
                });
            }
        } else {
            playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.VISIBLE);
            playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.GONE);
            playerRemoteViews.setTextViewText(R.id.player_notif_trackName, "");
            playerRemoteViews.setTextViewText(R.id.player_notif_artist, "");
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                socketService).setSmallIcon(R.drawable.ic_launcher).setContent(
                playerRemoteViews);
        NotificationManager mNotificationManager = (NotificationManager) socketService.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(PLAYER_NOTIF_ID, mBuilder.build());
    }


}
