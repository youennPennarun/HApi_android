package com.example.nolitsou.hapi;

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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nolitsou.hapi.data.Settings;
import com.example.nolitsou.hapi.data.SocketData;
import com.example.nolitsou.hapi.music.PlayerContainer;
import com.example.nolitsou.hapi.server.SocketConnectionEnum;
import com.example.nolitsou.hapi.server.SocketService;
import com.example.nolitsou.hapi.widgets.SimpleSideDrawer;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by nolitsou on 4/15/15.
 */
public abstract class AbstractActivity extends FragmentActivity {
    public final static String ACTION_PLAYER_SHOW = "PLAYER_SHOW";
    private final static String LOG_STR = "AbstractActivity:";
    private SimpleSideDrawer mSlidingMenu;
    private ActionBarContainer actionBar;
    private Messenger messenger = new Messenger(new IncomingHandler(this));
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            Log.i(LOG_STR, "Binded to SocketService");
            setSocketService(((SocketService.SocketBinder) binder).getService());
            Messenger serviceMessenger = new Messenger(getSocketService().getMessenger().getBinder());
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
    private SocketService socketService;
    private PlayerContainer playerContainer;
    private SlidingUpPanelLayout slidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected void createDefault () {Log.i(LOG_STR, "Stating AbstractActivity");
        SocketData.setActivity(this);
        Settings.loadSettings(this);
        PlayerControl.setPlayer(playerContainer);
        playerContainer = (PlayerContainer) findViewById(R.id.player);
        try {
            slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.app_main_frame);
            slidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View view, float v) {
                }
                @Override
                public void onPanelCollapsed(View view) {
                    playerContainer.viewPanelCollapsed.setVisibility(View.VISIBLE);
                    playerContainer.viewPanelExpanded.setVisibility(View.GONE);
                }

                @Override
                public void onPanelExpanded(View view) {
                    playerContainer.viewPanelCollapsed.setVisibility(View.GONE);
                    playerContainer.viewPanelExpanded.setVisibility(View.VISIBLE);
                }
                @Override
                public void onPanelAnchored(View view) {}
                @Override
                public void onPanelHidden(View view) {}
            });
        } catch(ClassCastException e) {}
        setmSlidingMenu(new SimpleSideDrawer(this));
        getmSlidingMenu().setLeftBehindContentView(R.layout.drawermenu_left);
        LinearLayout menuGotoAlarm = ((LinearLayout) getmSlidingMenu().getRootView().findViewById(R.id.menu_goto_alarms));
        actionBar = (ActionBarContainer)findViewById(R.id.app_frame_actionBar);
        Intent i= new Intent(this, SocketService.class);
        this.startService(i);
        actionBar.setListeners();
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
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if(action != null) {
            System.out.println("new intent (action=" + action + ")");
            switch (action) {
                case ACTION_PLAYER_SHOW:
                    slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    break;
                default:
                    break;
            }
        }
    }

    public void setActionBarTitle(String title) {
        ((TextView)actionBar.findViewById(R.id.title_text)).setText(title);
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


    class IncomingHandler extends Handler {
        private final Context context;

        IncomingHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SocketConnectionEnum.CONNECTED.getCode()) {
                Toast toast = Toast.makeText(context, "Connected", Toast.LENGTH_SHORT);
                toast.show();
                Log.i(LOG_STR, "connected->send loadData");
                loadData();
                if(playerContainer != null) {
                    playerContainer.setListeners();
                    if (socketService.getPlayer().getPlaying() != null) {
                        playerContainer.setPlaying(socketService.getPlayer().getPlaying());
                    }
                }
            } else if (msg.what == SocketConnectionEnum.INVALID_CREDENTIALS.getCode()) {
                // TODO
            } else if (msg.what == PlayerControl.TRACK_UPDATE) {
                System.out.println("!!!!!!!!!!!!!!!!!!!");
                System.out.println(socketService.getPlayer().getPlaying());
                if (socketService.getPlayer().getPlaying() != null) {
                    playerContainer.setPlaying(socketService.getPlayer().getPlaying());
                    playerContainer.playlistUpdated();
                }
            } else if (msg.what == PlayerControl.TRACK_STATUS && playerContainer != null) {
                playerContainer.playerStatusChanged();
            } else if (msg.what == PlayerControl.PLAYLIST_UPDATE && playerContainer != null) {
                System.out.println("msg.what == PlayerControl.PLAYLIST_UPDATE && playerContainer != null");
                playerContainer.playlistUpdated();
            } else {
                super.handleMessage(msg);
            }
        }
    }

    protected abstract void loadData();
}
