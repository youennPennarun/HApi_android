package com.example.hapi;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.hapi.music.SearchMusicFragment;
import com.example.hapi.server.RemotePlayer;
import com.example.hapi.server.ServerLink;

public class PlayerFragment extends CustomFragment {

	private ViewGroup rootView;
	protected View notSetView;
	private Activity mActivity;
	private final int PLAYER_NOTIF_ID = 1;
	private RemoteViews playerRemoteViews;
	public PlayerFragment() {
		this.mActivity = getActivity();
	}
	public PlayerFragment(Activity activity) {
		this.mActivity = activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRootView((ViewGroup) inflater.inflate(
				R.layout.player, container, false));
		RemotePlayer.setPlayerFragment(this);
		PlayerControl.setPlayerFragment(this);
		final TextView trackNameTV = (TextView) getRootView().findViewById(R.id.trackName);
		final TextView trackArtistTV = (TextView) getRootView().findViewById(R.id.trackArtist);

		final ImageButton playButton = (ImageButton) getRootView().findViewById(R.id.playButton);
		final ImageButton pauseButton = (ImageButton) getRootView().findViewById(R.id.pauseButton);
		final ImageButton nextButton = (ImageButton) getRootView().findViewById(R.id.nextButton);
		final ImageButton previousButton = (ImageButton) getRootView().findViewById(R.id.previousButton);

		final Button searchArtistBtn = (Button) getRootView().findViewById(R.id.searchArtist);
		final Button playRandom = (Button) getRootView().findViewById(R.id.playRandom);

		trackNameTV.setText(PlayerControl.getTrackName());
		trackArtistTV.setText(PlayerControl.getTrackArtist());

		playRandom.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PlayerControl.playRandom();
			}
		});
		playButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PlayerControl.play();
			}
		});
		pauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PlayerControl.pause();
			}
		});
		previousButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayerControl.previous();
			}
		});
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayerControl.next();
			}
		});
		final SeekBar volumeBar=(SeekBar) getRootView().findViewById(R.id.volumeBar);   
		volumeBar.setProgress(PlayerControl.getVolume());
		volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser) {
					PlayerControl.saveVolume(progress);
				}

			}
		});
		searchArtistBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity)getActivity()).changeFragment(new SearchMusicFragment());
			}
		});
		setGestureDetector();

		if(ServerLink.piConnected) {
			setPiConnectedView();
		} else {
			setPiNotConnectedView();
		}
		return getRootView();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	private void setGestureDetector() {
		final GestureDetector gesture = new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {
			final int SWIPE_MIN_DISTANCE = 100;
			final int SWIPE_THRESHOLD_VELOCITY = 200;
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
				if (distanceY > 5) {
					getRootView().setY(getRootView().getY() - distanceY);
				}

				return super.onScroll(e1, e2, distanceX, distanceY);	
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				try {
					if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
							&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
						((MainActivity)getActivity()).hidePlayer();
						return true;
					} else {
						return false;
					}
				} catch (Exception e) {
					// nothing
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});

		getRootView().setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean detectedUp = event.getAction() == MotionEvent.ACTION_UP;
				if (!gesture.onTouchEvent(event) && detectedUp)
				{
					ObjectAnimator anim = ObjectAnimator.ofFloat(getRootView(), "translationY", getRootView().getY(), 0);
					anim.setDuration(100);
					anim.start();
				}
				return gesture.onTouchEvent(event);
			}
		});
	}

	public void setVolume(int value) {
		((TextView)getRootView().findViewById(R.id.volumeValue)).setText(String.valueOf(value));
	}
	public void updateTrack() {
		if(getRootView() != null) {
			((TextView)getRootView().findViewById(R.id.trackArtist)).setText(PlayerControl.getTrackArtist());
			((TextView)getRootView().findViewById(R.id.trackName)).setText(PlayerControl.getTrackName());
		}
	}
	public void setPiNotConnectedView() {

		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable()  {
				public void run() 
				{
					if(mActivity != null && LayoutInflater.from(mActivity) != null && getRootView() != null) {
						notSetView = LayoutInflater.from(mActivity).inflate(R.layout.pi_not_connected, null);
						getRootView().addView(notSetView,
								new ViewGroup.LayoutParams(
										ViewGroup.LayoutParams.MATCH_PARENT,
										ViewGroup.LayoutParams.MATCH_PARENT));
					}
				}
			});
		}

	}
	public void setPiConnectedView() {
		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable()  {
				public void run() 
				{
					if (notSetView != null) {
						ViewGroup vg = (ViewGroup)(notSetView.getParent());
						vg.removeView(notSetView);
						notSetView = null;
					}
				}
			});
		}
	}
	public ViewGroup getRootView() {
		return rootView;
	}
	public void setRootView(ViewGroup rootView) {
		this.rootView = rootView;
	}
	public void playerNotif() {
		NotificationManager mNotificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);  
		playerRemoteViews = new RemoteViews(mActivity.getPackageName(),  
				R.layout.player_notification);
		playerRemoteViews.setTextViewText(R.id.player_notif_trackName, PlayerControl.getTrackName());
		playerRemoteViews.setTextViewText(R.id.player_notif_artist, PlayerControl.getTrackArtist());

		Intent openAppReveive = new Intent(mActivity, MainActivity.class);
		openAppReveive.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
				Intent.FLAG_ACTIVITY_SINGLE_TOP | 
				Intent.FLAG_ACTIVITY_NEW_TASK);
		openAppReveive.setAction(MainActivity.ACTION_PLAYER_SHOW);
		PendingIntent pendingIntentShow=PendingIntent.getActivity(mActivity,0,openAppReveive, 0);
		playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_cover, pendingIntentShow);
		playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_trackData, pendingIntentShow);
		Intent playReceive = new Intent(mActivity, NotificationActivity.class);  
		playReceive.setAction(NotificationActivity.PLAYER_PLAY);
		PendingIntent pendingIntentPlay=PendingIntent.getBroadcast(mActivity,0,playReceive,0);
		playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_play, pendingIntentPlay);
		Intent pauseReceive = new Intent(mActivity, NotificationActivity.class);  
		pauseReceive.setAction(NotificationActivity.PLAYER_PAUSE);
		PendingIntent pendingIntentPause = PendingIntent.getBroadcast(mActivity, 12345, pauseReceive, PendingIntent.FLAG_UPDATE_CURRENT);
		playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_pause, pendingIntentPause);
		Intent nextReceive = new Intent(mActivity, NotificationActivity.class); 
		nextReceive.setAction(NotificationActivity.PLAYER_NEXT);
		PendingIntent pendingIntentNext = PendingIntent.getBroadcast(mActivity, 12345, nextReceive, PendingIntent.FLAG_UPDATE_CURRENT);
		playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_next, pendingIntentNext);
		Intent previousReceive = new Intent(mActivity, NotificationActivity.class); 
		previousReceive.setAction(NotificationActivity.PLAYER_PREVIOUS);
		PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(mActivity, 12345, previousReceive, PendingIntent.FLAG_UPDATE_CURRENT);
		playerRemoteViews.setOnClickPendingIntent(R.id.player_notif_previous, pendingIntentPrevious);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(  
				mActivity).setSmallIcon(R.drawable.ic_launcher).setContent(  
						playerRemoteViews);
		mNotificationManager.notify(PLAYER_NOTIF_ID, mBuilder.build());
		playerStatusChanged();
	}

	public void dismissPlayerNotif() {
		try {
			NotificationManager mNotificationManager =
					(NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(PLAYER_NOTIF_ID);
		} catch(NullPointerException e) {

		}
	}
	public void playerStatusChanged() {
		if(playerRemoteViews != null) {
			if (PlayerControl.getStatus().equals("PLAY")) {
				playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.GONE);
				playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.VISIBLE);
			} else if (PlayerControl.getStatus().equals("PAUSE")) {
				playerRemoteViews.setViewVisibility(R.id.player_notif_play, View.VISIBLE);
				playerRemoteViews.setViewVisibility(R.id.player_notif_pause, View.GONE);
			}
			NotificationManager mNotificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE); 
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(  
					mActivity).setSmallIcon(R.drawable.ic_launcher).setContent(  
							playerRemoteViews);
			mNotificationManager.notify(PLAYER_NOTIF_ID, mBuilder.build());
		}
	}
}