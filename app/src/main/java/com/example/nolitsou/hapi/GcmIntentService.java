package com.example.nolitsou.hapi;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nolitsou.hapi.server.SocketConnectionEnum;
import com.example.nolitsou.hapi.server.SocketService;
import com.github.nkzawa.socketio.client.Ack;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 2;
    private static final String TAG = "GcmIntentService";

    private static final String SET_VOLUME = "pi:notify:sound:volume";
    private static final String PLAYER_STATUS = "pi:player:status";
    private static final String PLAYER_PLAYING_ID = "music:playlist:playing:id";
    private static final String PLAYER_PLAYLIST_UPDATE = "music:playlist:set";

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private boolean done = false;
    private boolean connected = false;
    private Intent intent;

    private Messenger messenger = new Messenger(new IncomingHandler(this));
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            Log.i(TAG, "Binded to SocketService");
            socketService = ((SocketService.SocketBinder) binder).getService();
            Messenger serviceMessenger = new Messenger(socketService.getMessenger().getBinder());
            Message msg = Message.obtain(null, SocketService.REGISTER_CLIENT);
            Log.i(TAG, "Registering to socketService");
            msg.replyTo = messenger;
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {

        }
    };
    private SocketService socketService;

    @Override
    public void onDestroy() {
        unbindService(mConnection);
    }

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent i= new Intent(this, SocketService.class);
        this.startService(i);
        bindService(i, mConnection,
                Context.BIND_AUTO_CREATE);

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            this.intent = intent;
            execute();


        } else {
            System.out.println("empty msg");
        }
    }
    private void execute() {
        if (connected && !done && intent != null) {
            final PlayerControl player = socketService.getPlayer();
            Bundle extras = intent.getExtras();
            if (!extras.isEmpty()) {
                System.out.println("GOT------->"+extras);
                if (extras.containsKey(PLAYER_STATUS)) {
                    player.setStatus(extras.getString(PLAYER_STATUS));
                    PlayerNotification.update(socketService);
                }
                if (extras.containsKey(SET_VOLUME)) {
                    player.setVolume(extras.getInt(SET_VOLUME) - 50);
                    PlayerNotification.update(socketService);
                }
                if (extras.containsKey(PLAYER_PLAYING_ID)) {
                    player.setPlaying(Integer.valueOf(extras.getString(PLAYER_PLAYING_ID)));
                    PlayerNotification.update(socketService);
                }
                if (extras.containsKey(PLAYER_PLAYLIST_UPDATE)) {
                    socketService.getSocket().emit("music:playlist:get", new Ack() {
                        @Override
                        public void call(Object... args) {
                            if (args.length > 0) {
                                player.setPlaylist((JSONObject)args[0]);
                                PlayerNotification.update(socketService);
                            }
                        }
                    });
                }
            }
            done = true;
            GcmReceiver.completeWakefulIntent(intent);
        }
    }


    class IncomingHandler extends Handler {
        private final Context context;

        IncomingHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SocketConnectionEnum.CONNECTED.getCode()) {
                connected = true;
                execute();
            } else {
                super.handleMessage(msg);
            }
        }
    }
}