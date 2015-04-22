package com.example.nolitsou.hapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nolitsou.hapi.data.Settings;
import com.example.nolitsou.hapi.music.PlayerContainer;
import com.example.nolitsou.hapi.server.SocketService;
import com.example.nolitsou.hapi.utils.LoginTask;
import com.example.nolitsou.hapi.utils.ServerLink;
import com.example.nolitsou.hapi.widgets.SimpleSideDrawer;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolitsou on 4/15/15.
 */
public abstract class AbstractActivity extends FragmentActivity {

    public static final String PROJECT_NUMBER = "585409898471";
    public final static String ACTION_PLAYER_SHOW = "PLAYER_SHOW";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String LOG_STR = "AbstractActivity:";
    public GoogleCloudMessaging gcm;
    public String regid;
    private SimpleSideDrawer mSlidingMenu;
    private ActionBarContainer actionBar;

    private PlayerContainer playerContainer;
    private SlidingUpPanelLayout slidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void createDefault() {
        PlayerNotification.update(this);
        Log.i(LOG_STR, "Stating AbstractActivity");
        GCMSender.setGcm(GoogleCloudMessaging.getInstance(this));
        Settings.loadSettings(this);
        playerContainer = (PlayerContainer) findViewById(R.id.player);
        PlayerControl.getInstance().setPlayerContainer(playerContainer);
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
                public void onPanelAnchored(View view) {
                }

                @Override
                public void onPanelHidden(View view) {
                }
            });
        } catch (ClassCastException e) {
        }
        setmSlidingMenu(new SimpleSideDrawer(this));
        getmSlidingMenu().setLeftBehindContentView(R.layout.drawermenu_left);
        LinearLayout menuGotoAlarm = ((LinearLayout) getmSlidingMenu().getRootView().findViewById(R.id.menu_goto_alarms));
        actionBar = (ActionBarContainer) findViewById(R.id.app_frame_actionBar);
        actionBar.setListeners();
        getRegId();
        onNewIntent(getIntent());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra("HOST", Settings.host);
    }
    /*
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_STR, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    */

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null) {
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
        ((TextView) actionBar.findViewById(R.id.title_text)).setText(title);
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


    public SimpleSideDrawer getmSlidingMenu() {
        return mSlidingMenu;
    }

    public void setmSlidingMenu(SimpleSideDrawer mSlidingMenu) {
        this.mSlidingMenu = mSlidingMenu;
    }


    public abstract void loadData();

    public void notLoggedIn() {
        new LoginTask(this).execute();
    }


    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    System.out.println("reg id = " + regid);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                sendRegId();
            }
        }.execute(null, null, null);
    }

    protected void sendRegId() {
        if (regid != null) {
            System.out.println("send reg id = " + regid);
            new SendGCMTask(regid).execute();
        }
    }

    ;

    class SendGCMTask extends AsyncTask<Void, Void, Void> {
        private final String regId;

        SendGCMTask(String regId) {
            this.regId = regId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("register to " + Settings.host + "user/GCM with id " + regId);
            List<Cookie> cookies;
            HttpPost httppost = new HttpPost(Settings.host + "user/GCM");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("regId", regId));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = ServerLink.getHttpClient().execute(httppost);
                HttpEntity entity = response.getEntity();
                System.out.println("Login form get: " + response.getStatusLine());
                PlayerControl.getInstance().updatePlaylist();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }
    }

}
