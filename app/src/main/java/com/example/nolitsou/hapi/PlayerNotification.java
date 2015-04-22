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
import com.github.nkzawa.socketio.client.Ack;

public class PlayerNotification {
    public final static int PLAYER_NOTIF_ID = 1;
    public static RemoteViews playerRemoteViews;
    private static NotificationCompat.Builder mBuilder;

    public static NotificationCompat.Builder getBuilder(Context context) {
        final Track playing = PlayerControl.getInstance().getPlaying();

        playerRemoteViews = new RemoteViews(context.getPackageName(),
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
            playing.loadCover(context, new Ack() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0) {
                        playerRemoteViews.setImageViewBitmap(R.id.player_notif_cover, playing.getCover());
                    }
                }
            });
        }

        Intent openAppReveive = new Intent(context, AbstractActivity.class);
        openAppReveive.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        openAppReveive.setAction(AbstractActivity.ACTION_PLAYER_SHOW);
        openAppReveive.putExtra("action", AbstractActivity.ACTION_PLAYER_SHOW);
        PendingIntent pendingIntentShow = PendingIntent.getActivity(context, 0, openAppReveive, 0);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_cover, pendingIntentShow);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_trackData, pendingIntentShow);

        Intent playReceive = new Intent(context, NotificationReceiver.class);
        playReceive.setAction(NotificationReceiver.PLAYER_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, playReceive, 0);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_play, pendingIntentPlay);

        Intent pauseReceive = new Intent(context, NotificationReceiver.class);
        pauseReceive.setAction(NotificationReceiver.PLAYER_PAUSE);
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(context, 12345, pauseReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_pause, pendingIntentPause);
        Intent nextReceive = new Intent(context, NotificationReceiver.class);
        nextReceive.setAction(NotificationReceiver.PLAYER_NEXT);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 12345, nextReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_next, pendingIntentNext);
        Intent previousReceive = new Intent(context, NotificationReceiver.class);
        previousReceive.setAction(NotificationReceiver.PLAYER_PREVIOUS);
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 12345, previousReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_previous, pendingIntentPrevious);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher).setContent(
                playerRemoteViews);
        return mBuilder;
    }

    public static RemoteViews create(Context context) {
        NotificationCompat.Builder mBuilder = getBuilder(context);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(PLAYER_NOTIF_ID, mBuilder.build());
        playerStatusChanged(context);
        return playerRemoteViews;
    }

    public static void dismissPlayerNotif(Context context) {
        try {
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(PLAYER_NOTIF_ID);
        } catch (NullPointerException e) {

        }
    }

    public static void playerStatusChanged(Context context) {
        if (playerRemoteViews != null) {
            if (PlayerControl.getInstance().getStatus().equals("PLAY")) {
                playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.GONE);
                playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.VISIBLE);
            } else if (PlayerControl.getInstance().getStatus().equals("PAUSE")) {
                playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.VISIBLE);
                playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.GONE);
            }
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PlayerNotification.mBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.drawable.ic_launcher).setContent(
                    playerRemoteViews);
            mNotificationManager.notify(PLAYER_NOTIF_ID, mBuilder.build());

        }
    }

    public static void update(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        System.out.println("---UPDATE---");
        final PlayerControl player = PlayerControl.getInstance();
        if (playerRemoteViews == null) {
            create(context);
        }
        if (player.getPlaying() != null) {
            System.out.println("STATUS=" + PlayerControl.getInstance().getStatus());
            if (PlayerControl.getInstance().getStatus().equals("PLAY")) {
                playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.GONE);
                playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.VISIBLE);
            } else if (PlayerControl.getInstance().getStatus().equals("PAUSE")) {
                playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.VISIBLE);
                playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.GONE);
            }
            playerRemoteViews.setTextViewText(R.id.player_notif_trackName, player.getPlaying().getName());
            playerRemoteViews.setTextViewText(R.id.player_notif_artist, player.getPlaying().getArtistsStr());
            if (player.getPlaying().getCover() != null) {
                playerRemoteViews.setImageViewBitmap(R.id.player_notif_cover, player.getPlaying().getCover());
            } else {
                player.getPlaying().loadCover(context, new Ack() {
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
                context).setSmallIcon(R.drawable.ic_launcher).setContent(
                playerRemoteViews);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(PLAYER_NOTIF_ID, mBuilder.build());
    }


}
