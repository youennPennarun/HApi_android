package com.example.nolitsou.hapi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nolitsou.hapi.data.Settings;
import com.example.nolitsou.hapi.data.SocketData;
import com.example.nolitsou.hapi.music.PlayerContainer;
import com.example.nolitsou.hapi.music.playlist.PlaylistFragment;
import com.example.nolitsou.hapi.server.SocketConnectionEnum;
import com.example.nolitsou.hapi.server.SocketService;
import com.example.nolitsou.hapi.widgets.SimpleSideDrawer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MainActivity extends FragmentActivity {
    public final static String ACTION_PLAYER_SHOW = "PLAYER_SHOW";
    private final static String LOG_STR = "MainActivity:";
    private static final int NUM_PAGES = 2;
    private SimpleSideDrawer mSlidingMenu;
    private CustomFragment mainFragment = new AlarmFragment();
    private ActionBarFragment actionBar = new ActionBarFragment();
    private int notificationID = 125;
    private Messenger messenger = new Messenger(new IncomingHandler(this));
    private Messenger serviceMessenger;
    private SocketService socketService;
    private PlayerContainer playerContainer;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            Log.i(LOG_STR, "Binded to SocketService");
            setSocketService(((SocketService.SocketBinder) binder).getService());
            serviceMessenger = new Messenger(getSocketService().getMessenger().getBinder());
            Message msg = Message.obtain(null, SocketService.REGISTER_CLIENT);
            Log.i(LOG_STR, "Registering to socketService");
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
    private SlidingUpPanelLayout slidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SocketData.setActivity(this);
        super.onCreate(savedInstanceState);
        Log.i(LOG_STR, "Stating mainActivity");
        setContentView(R.layout.app_activity_main);
        Settings.loadSettings(this);
        PlayerControl.setPlayer(playerContainer);
        getSupportFragmentManager().beginTransaction().add(R.id.app_frame_actionBar, getActionBarFragment()).commit();
        // Instantiate a ViewPager and a PagerAdapter.
        if (Settings.host == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.frame_content, new SettingsFragment()).addToBackStack(null).commit();
        } else {
            //task.execute(Settings.host);
            getSupportFragmentManager().beginTransaction().add(R.id.frame_content, getMainFragment()).commit();
            System.out.println("starting service...");

            Intent intent = new Intent(this, SocketService.class);
            startService(intent);
            bindService(intent, mConnection,
                    Context.BIND_AUTO_CREATE);
        }
        playerContainer = (PlayerContainer) findViewById(R.id.player);
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.app_main_frame);
        setmSlidingMenu(new SimpleSideDrawer(this));
        getmSlidingMenu().setLeftBehindContentView(R.layout.drawermenu_left);
        LinearLayout menuGotoAlarm = ((LinearLayout) getmSlidingMenu().getRootView().findViewById(R.id.menu_goto_alarms));
        menuGotoAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getMainFragment().getClass().equals(AlarmFragment.class)) {
                    changeFragment(new AlarmFragment());
                }
                if (!getmSlidingMenu().isClosed()) {
                    getmSlidingMenu().toggleLeftDrawer();
                }
            }
        });
        LinearLayout menuGotoSettings = ((LinearLayout) getmSlidingMenu().getRootView().findViewById(R.id.menu_goto_settings));
        menuGotoSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getMainFragment().getClass().equals(SettingsFragment.class)) {
                    changeFragment(new SettingsFragment());
                }
                if (!getmSlidingMenu().isClosed()) {
                    getmSlidingMenu().toggleLeftDrawer();
                }
            }
        });
        LinearLayout menuGotoPlaylists = ((LinearLayout) getmSlidingMenu().getRootView().findViewById(R.id.menu_goto_playlist));
        menuGotoPlaylists.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getMainFragment().getClass().equals(PlaylistFragment.class)) {
                    changeFragment(new PlaylistFragment());
                }
                if (!getmSlidingMenu().isClosed()) {
                    getmSlidingMenu().toggleLeftDrawer();
                }
            }
        });
        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra("HOST", Settings.host);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("HEHOH");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        System.out.println("new intent (action=" + action + ")");
        switch (action) {
            case ACTION_PLAYER_SHOW:
                showPlayer();
                break;
            default:
                break;
        }

    }

    public void changeFragment(CustomFragment fragment) {
        setActionBarTitle(fragment.title);
        setMainFragment(fragment);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setActionBarTitle(String title) {
        getActionBarFragment().setTitle(title);
    }

    public void showPlayer() {
        showPlayer(false);
    }

    public void showPlayer(boolean fromNotif) {
        /*
        LayoutInflater inflater = LayoutInflater.from(this);
		if(PlayerFragment.playerBody == null) {
			PlayerFragment.playerBody = inflater.inflate(R.layout.player, (ViewGroup)PlayerFragment.getRootView().findViewById(R.id.player_body));
		}
		PlayerFragment.hide();
		PlayerFragment.show();
		PlayerFragment.createBody();
		PlayerFragment.setBodyVisible(true);
*/

    }

    public void hidePlayer() {
		/*
		if(PlayerFragment.isBodyVisible()) {
			PlayerFragment.hide();
			if(PlayerFragment.playerBody != null) {
			}
		}
		*/
    }

    @Override
    public void onBackPressed() {
        if (slidingLayout != null &&
                (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setText(final int trackname, final String string) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView textView = (TextView) findViewById(trackname);
                textView.setText(string);

            }

        });
    }

    public void setBarValue(final int trackname, final int value) {
        runOnUiThread(new Runnable() {
            public void run() {
                SeekBar seek = (SeekBar) findViewById(trackname);
                seek.setProgress(value);
            }

        });
    }

    public void sendToSettings() {
        // TODO Auto-generated method stub

    }

    public Fragment getMainFragment() {
        return mainFragment;
    }

    public void setMainFragment(CustomFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    public void notifPiDisconnected() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.jupiter)
                        .setContentTitle("HAPi Alert")
                        .setContentText("Raspberry pi disconnected")
                        .setSound(soundUri);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    public void dismissPiNotif() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationID);
    }

    public SocketService getSocketService() {
        return socketService;
    }

    public void setSocketService(SocketService socketService) {
        this.socketService = socketService;
    }

    public boolean socketConnected() {
        return (getSocketService() != null && getSocketService().isConnected());
    }

    public SimpleSideDrawer getmSlidingMenu() {
        return mSlidingMenu;
    }

    public void setmSlidingMenu(SimpleSideDrawer mSlidingMenu) {
        this.mSlidingMenu = mSlidingMenu;
    }

    public ActionBarFragment getActionBarFragment() {
        return actionBar;
    }

    public void setActionBar(ActionBarFragment actionBar) {
        this.actionBar = actionBar;
    }

    class IncomingHandler extends Handler {
        private final Context context;

        IncomingHandler(Context context) {
            this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            System.out.println("hay!!"+msg.what);
            if(msg.what == SocketConnectionEnum.CONNECTED.getCode()) {
                Toast toast = Toast.makeText(context, "Connected", Toast.LENGTH_SHORT);
                toast.show();
                Log.i(LOG_STR, "connected->send loadData");
                ((CustomFragment) mainFragment).loadData();
                playerContainer.setListeners();
                if (socketService.getPlayer().getPlaying() != null) {
                    playerContainer.setPlaying(socketService.getPlayer().getPlaying());
                } else {
                    System.out.println("nothing is playing");
                }
            } else if(msg.what == SocketConnectionEnum.INVALID_CREDENTIALS.getCode()) {
                changeFragment(new SettingsFragment());
            } else if(msg.what == PlayerControl.TRACK_UPDATE) {
                System.out.println("!!!!!!!!!!!!!!!!!!!");
                System.out.println(socketService.getPlayer().getPlaying());
                if (socketService.getPlayer().getPlaying() != null) {
                    playerContainer.setPlaying(socketService.getPlayer().getPlaying());
                }
            } else if(msg.what == PlayerControl.TRACK_STATUS) {
                playerContainer.playerStatusChanged();
            } else {
                super.handleMessage(msg);
            }
        }
    }
    public PlayerContainer getPlayerContainer() {
        return playerContainer;
    }
}



