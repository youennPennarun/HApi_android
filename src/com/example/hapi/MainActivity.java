package com.example.hapi;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hapi.data.Alarm;
import com.example.hapi.data.Settings;
import com.example.hapi.music.SearchMusicFragment;
import com.example.hapi.music.playlist.Playlist;
import com.example.hapi.music.playlist.PlaylistFragment;
import com.example.hapi.server.ServerLink;
import com.example.hapi.server.ServerLinkTask;
import com.example.hapi.widgets.SimpleSideDrawer;
import com.github.nkzawa.socketio.client.Ack;

public class MainActivity extends FragmentActivity {
	private static final int NUM_PAGES = 2;
	public final static  String ACTION_PLAYER_SHOW = "PLAYER_SHOW";

	private SimpleSideDrawer mSlidingMenu;
	private Fragment mainFragment = new AlarmFragment();
	private PlayerFragment PlayerFragment = new PlayerFragment(this);
	private int notificationID = 125;
	private int playerNotificationID = 100;

	private View actionBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_activity_main);
		ServerLink.setMainActivity(this);
		Settings.loadSettings(this);
		PlayerControl.setPlayerFragment(PlayerFragment);
		PlayerControl.setActivity(this);
		// Instantiate a ViewPager and a PagerAdapter.
		ServerLinkTask task = new ServerLinkTask(this);
		if(Settings.host == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.app_main_frame, new SettingsFragment()).addToBackStack(null).commit();
		} else {
			task.execute(Settings.host);
			getSupportFragmentManager().beginTransaction().add(R.id.app_main_frame, getMainFragment()).addToBackStack(null).commit();
		}
		mSlidingMenu = new SimpleSideDrawer( this );
		mSlidingMenu.setLeftBehindContentView( R.layout.drawermenu_left );
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);

		LayoutInflater mInflater = LayoutInflater.from(this);

		actionBar = mInflater.inflate(R.layout.actionbar, null);
		TextView mTitleTextView = (TextView) actionBar.findViewById(R.id.title_text);
		mTitleTextView.setText("Alarms");

		hidePlayer();
		ImageView homeBtn = (ImageView) actionBar
				.findViewById(R.id.actionbar_home);
		homeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSlidingMenu.toggleLeftDrawer();
			}
		});
		ImageButton actionbar_player = (ImageButton) actionBar
				.findViewById(R.id.actionbar_player);

		actionbar_player.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!PlayerFragment.isVisible()) {
					showPlayer();
				} else {
					hidePlayer();
				}
			}
		});
		ImageButton actionBarSearch = (ImageButton)actionBar.findViewById(R.id.actionbar_search);
		actionBarSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (PlayerFragment.isVisible()) {
					hidePlayer();
				}
				changeFragment(new SearchMusicFragment());
			}
		});
		LinearLayout menuGotoAlarm = ((LinearLayout)mSlidingMenu.getRootView().findViewById(R.id.menu_goto_alarms));
		menuGotoAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!getMainFragment().getClass().equals(AlarmFragment.class)) {
					changeFragment(new AlarmFragment());
				}
				if(!mSlidingMenu.isClosed()) {
					mSlidingMenu.toggleLeftDrawer();
				}
			}
		});
		LinearLayout menuGotoSettings = ((LinearLayout)mSlidingMenu.getRootView().findViewById(R.id.menu_goto_settings));
		menuGotoSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!getMainFragment().getClass().equals(SettingsFragment.class)) {
					changeFragment(new SettingsFragment());
				}
				if(!mSlidingMenu.isClosed()) {
					mSlidingMenu.toggleLeftDrawer();
				}
			}
		});
		LinearLayout menuGotoPlaylists = ((LinearLayout)mSlidingMenu.getRootView().findViewById(R.id.menu_goto_playlist));
		menuGotoPlaylists.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!getMainFragment().getClass().equals(PlaylistFragment.class)) {
					changeFragment(new PlaylistFragment());
				}
				if(!mSlidingMenu.isClosed()) {
					mSlidingMenu.toggleLeftDrawer();
				}
			}
		});
		mActionBar.setCustomView(actionBar);
		mActionBar.setDisplayShowCustomEnabled(true);
		onNewIntent(getIntent());
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onNewIntent(Intent intent) {
		String action = intent.getAction();
		System.out.println("new intent (action="+action+")");
		switch(action){
		case ACTION_PLAYER_SHOW:
			showPlayer();
			break;
		default:
			break;
		}

	}
	public void changeFragment(CustomFragment fragment) {
		hidePlayer();
		setActionBarTitle(fragment.title);
		setMainFragment(fragment);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.app_main_frame, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	public void setActionBarTitle(String title) {
		View mCustomView = getActionBar().getCustomView();
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
		mTitleTextView.setText(title);
	}
	public void showPlayer() {
		showPlayer(false);
	}
	public void showPlayer(boolean fromNotif) {
		if (!PlayerFragment.isVisible()) {
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.app_main_frame, PlayerFragment)
			.setCustomAnimations(R.animator.player_show, R.animator.player_hide)
			.show(PlayerFragment)
			.addToBackStack(null)
			.commitAllowingStateLoss();
			if(ServerLink.piConnected) {
				PlayerFragment.setPiConnectedView();
			} else {
				PlayerFragment.setPiNotConnectedView();
			}
			ImageButton actionBarSearch = (ImageButton)actionBar.findViewById(R.id.actionbar_search);
			actionBarSearch.setVisibility(View.VISIBLE);
		}
	}
	public void hidePlayer() {
		if (PlayerFragment.isVisible()) {
			getSupportFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.animator.player_show, R.animator.player_hide)
			.hide(PlayerFragment)
			.remove(PlayerFragment)
			.commit();

			if(ServerLink.piConnected) {
				PlayerFragment.setPiConnectedView();
			} else {
				PlayerFragment.setPiNotConnectedView();
			}
			ImageButton actionBarSearch = (ImageButton)actionBar.findViewById(R.id.actionbar_search);
			actionBarSearch.setVisibility(View.GONE);
		}
	}
	@Override
	protected void onStart()
	{  
		super.onStart();
	}
	public void setText(final int trackname, final String string) {
		runOnUiThread(new Runnable() 
		{
			public void run() 
			{
				TextView textView=(TextView)findViewById(trackname);
				textView.setText(string);

			}

		});
	}
	public void setBarValue(final int trackname, final int value) {
		runOnUiThread(new Runnable() 
		{
			public void run() 
			{
				SeekBar seek=(SeekBar)findViewById(trackname);
				seek.setProgress(value);
			}

		});
	}

	public void onConnectionSet() {

		Toast toast = Toast.makeText(this, "Connected", Toast.LENGTH_SHORT);
		toast.show();
		if (getMainFragment() instanceof AlarmFragment) {
			((AlarmFragment)getMainFragment()).loadData();
		}
		System.out.println("music:playlists:get ...");
		ServerLink.getSocket().emit("music:playlists:get", new JSONObject(), new Ack() {
			@Override
			public void call(Object... arg0) {
				System.out.println("playlists:");
				if(arg0.length > 0) {
					System.out.println(arg0[0]);
				} else {
					System.out.println("empty...");
				}
			}
		});
	}
	public PlayerFragment getPlayerFragment() {
		return PlayerFragment;
	}
	public void sendToSettings() {
		// TODO Auto-generated method stub

	}
	public Fragment getMainFragment() {
		return mainFragment;
	}
	public void setMainFragment(Fragment mainFragment) {
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
	public void dismissPiNotif(){
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(notificationID);
		PlayerFragment.dismissPlayerNotif();
	}

}
