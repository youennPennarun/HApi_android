package com.example.nolitsou.hapi.music;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.PlayerControl;
import com.example.nolitsou.hapi.R;
import com.github.nkzawa.socketio.client.Ack;

public class PlayerContainer extends FrameLayout {
    public View viewPanelExpanded;
    public View viewPanelCollapsed;
    private ImageView playerCover;
    private ImageView playerHeaderCover;
    private TextView trackName;
    private TextView trackArtist;
    private View playButton;
    private View pauseButton;
    private View nextButton;
    private View previousButton;
    private View showVolume;
    private View playButtonHead;
    private View pauseButtonHead;

    private View showPlaylist;

    private SeekBar volumeBar;
    private View playRandom;
    private View playerContentView;
    private View playlistView;
    private ListView playlistListView;
    private PlaylistListAdapter playlistAdapter;

    public PlayerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        PlayerControl.getInstance();
        this.viewPanelExpanded = getRootView().findViewById(R.id.viewPanelExpanded);
        this.viewPanelCollapsed = getRootView().findViewById(R.id.viewPanelCollapsed);
        this.showPlaylist = getRootView().findViewById(R.id.player_header_show_playlist);
        this.playerCover = (ImageView) getRootView().findViewById(R.id.player_cover);
        this.playerHeaderCover = (ImageView) getRootView().findViewById(R.id.player_header_cover);
        this.trackName = (TextView) getRootView().findViewById(R.id.trackName);
        this.trackArtist = (TextView) getRootView().findViewById(R.id.trackArtist);

        this.playButtonHead = getRootView().findViewById(R.id.player_header_play);
        this.pauseButtonHead = getRootView().findViewById(R.id.player_header_pause);
        this.playButton = getRootView().findViewById(R.id.player_playButton);
        this.pauseButton = getRootView().findViewById(R.id.player_pauseButton);
        this.nextButton = getRootView().findViewById(R.id.player_nextButton);
        this.previousButton = getRootView().findViewById(R.id.player_previousButton);
        this.showVolume = getRootView().findViewById(R.id.showVolume);
        this.playRandom = getRootView().findViewById(R.id.player_random);

        this.playlistView = getRootView().findViewById(R.id.playlistView);
        this.playerContentView = getRootView().findViewById(R.id.player_content);

        this.playlistListView = (ListView) getRootView().findViewById(R.id.playlistList);

        updatePlaying();
        updateStatus();
        setListeners();
    }

    public void setListeners() {
        final PlayerControl player = PlayerControl.getInstance();
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                player.play();
            }
        });
        playButtonHead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                player.play();
            }
        });
        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
            }
        });
        pauseButtonHead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
            }
        });

        showPlaylist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlistView.getVisibility() == View.VISIBLE) {
                    playlistView.setVisibility(View.GONE);
                    playerContentView.setVisibility(View.VISIBLE);
                } else {
                    playlistView.setVisibility(View.VISIBLE);
                    playerContentView.setVisibility(View.GONE);
                }
            }
        });

        showVolume.setOnClickListener(new OnClickListener() {
            PlayerControl player = PlayerControl.getInstance();
            @Override
            public void onClick(View v) {
                if (volumeBar == null) {
                    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int windowWidth = size.x;
                    int[] buttonPos = {0, 0};
                    showVolume.getLocationOnScreen(buttonPos);
                    volumeBar = new SeekBar(getContext());
                    volumeBar.setMax(50);
                    if (player != null) {
                        volumeBar.setProgress(player.getVolume());
                    }
                    volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                player.saveVolume(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((windowWidth / 4) * 3, 100);
                    params.leftMargin = buttonPos[0];
                    params.topMargin = buttonPos[1] - showVolume.getHeight() - 5;
                    ((PlayerContainer) findViewById(R.id.player)).addView(volumeBar, params);
                } else {
                    ((PlayerContainer) findViewById(R.id.player)).removeView(volumeBar);
                    volumeBar = null;
                }
            }
        });

    }

    public void playlistUpdated() {

    }
    public void updateStatus() {
        ((AbstractActivity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("PlayerContainre: setting status");
                PlayerControl player = PlayerControl.getInstance();
                if (player.getStatus().equals(PlayerControl.STATUS_PLAY)) {
                    pauseButton.setVisibility(View.VISIBLE);
                    pauseButtonHead.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                    playButtonHead.setVisibility(View.GONE);
                } else if (player.getStatus().equals(PlayerControl.STATUS_PAUSE)) {
                    pauseButton.setVisibility(View.GONE);
                    pauseButtonHead.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                    playButtonHead.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    public void updatePlaying() {
        ((AbstractActivity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Track track = PlayerControl.getInstance().getPlaying();
                if (track != null) {
                    if (track.getCover() == null) {
                        track.loadCover(getContext(), new Ack() {
                            @Override
                            public void call(Object... args) {
                                if (args.length > 0) {
                                    playerCover.setImageBitmap(track.getCover());
                                    playerHeaderCover.setImageBitmap(track.getCover());
                                }
                            }
                        });
                    } else {
                        playerCover.setImageBitmap(track.getCover());
                        playerHeaderCover.setImageBitmap(track.getCover());
                    }
                    trackName.setText(track.getName());
                    trackArtist.setText(track.getArtistsStr());
                } else {
                    trackName.setText("");
                    trackArtist.setText("");
                }
            }
        });

    }
}
