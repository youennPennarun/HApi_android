package com.example.hapi;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.hapi.data.Settings;
import com.example.hapi.server.ServerLink;
import com.example.hapi.server.ServerLinkTask;
import com.example.hapi.widgets.SimpleSideDrawer;

public class MainActivity extends FragmentActivity {
	private static final int NUM_PAGES = 2;

	private SimpleSideDrawer mSlidingMenu;
	private Fragment mainFragment = new AlarmFragment();
	private PlayerFragment PlayerFragment = new PlayerFragment(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_activity_main);

		ServerLink.setMainActivity(this);
		Settings.loadSettings(this);
		ServerLinkTask task = new ServerLinkTask(this);
		task.execute(Settings.host);
		// Instantiate a ViewPager and a PagerAdapter.

		getSupportFragmentManager().beginTransaction().add(R.id.app_main_frame, mainFragment).addToBackStack(null).commit();
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

		View mCustomView = mInflater.inflate(R.layout.actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
		mTitleTextView.setText("Alarms");

	    hidePlayer();
		ImageView homeBtn = (ImageView) mCustomView
				.findViewById(R.id.actionbar_home);
		homeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSlidingMenu.toggleLeftDrawer();
			}
		});
		ImageButton actionbar_settings = (ImageButton) mCustomView
				.findViewById(R.id.actionbar_settings);
		actionbar_settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				changeFragment(new SettingsFragment());
			}
		});
		ImageButton actionbar_player = (ImageButton) mCustomView
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
		TextView menuGotoAlarm = ((TextView)mSlidingMenu.getRootView().findViewById(R.id.menu_goto_alarm));
		menuGotoAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mainFragment.getClass().equals(AlarmFragment.class)) {
					changeFragment(new AlarmFragment());
				}
			}
		});
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}

	public void changeFragment(CustomFragment fragment) {
		hidePlayer();
		setActionBarTitle(fragment.title);
		mainFragment = fragment;
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
		if (!PlayerFragment.isVisible()) {
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.app_main_frame, PlayerFragment)
			.setCustomAnimations(R.animator.player_show, R.animator.player_hide)
			.show(PlayerFragment)
			.addToBackStack(null).commit();
			if(ServerLink.piConnected) {
				PlayerFragment.setPiConnectedView();
			} else {
				PlayerFragment.setPiNotConnectedView();
			}
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
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.actionbar_settings:
			changeFragment(new SettingsFragment());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public void sendToSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStart()
	{   
		// TODO Auto-generated method stub
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
		((AlarmFragment)mainFragment).loadData();
	}
	public PlayerFragment getPlayerFragment() {
		return PlayerFragment;
	}

}
