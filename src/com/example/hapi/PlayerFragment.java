package com.example.hapi;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.hapi.music.SearchMusicFragment;
import com.example.hapi.server.RemotePlayer;
import com.example.hapi.server.ServerLink;

public class PlayerFragment extends CustomFragment {

	private ViewGroup rootView;
	protected View notSetView;
	private Activity mActivity;
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
		final TextView trackNameTV = (TextView) getRootView().findViewById(R.id.trackName);
		final TextView trackArtistTV = (TextView) getRootView().findViewById(R.id.trackArtist);

		final ImageButton playButton = (ImageButton) getRootView().findViewById(R.id.playButton);
		final ImageButton pauseButton = (ImageButton) getRootView().findViewById(R.id.pauseButton);

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
				System.out.println("play...");
				PlayerControl.play();
			}
		});
		pauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PlayerControl.pause();
			}
		});
		final SeekBar volumeBar=(SeekBar) getRootView().findViewById(R.id.volumeBar);     
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
		((TextView)getRootView().findViewById(R.id.trackArtist)).setText(PlayerControl.getTrackArtist());
		((TextView)getRootView().findViewById(R.id.trackName)).setText(PlayerControl.getTrackName());
	}
	public void setPiNotConnectedView() {

		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable()  {
				public void run() 
				{
					System.out.println(mActivity);
					if(mActivity != null && LayoutInflater.from(mActivity) != null && getRootView() != null) {
						notSetView = LayoutInflater.from(mActivity).inflate(R.layout.pi_not_connected, null);
						System.out.println(getRootView());
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
}