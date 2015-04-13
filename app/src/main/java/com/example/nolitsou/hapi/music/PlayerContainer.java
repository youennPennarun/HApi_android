package com.example.nolitsou.hapi.music;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nolitsou.hapi.MainActivity;
import com.example.nolitsou.hapi.PlayerControl;
import com.example.nolitsou.hapi.R;
import com.github.nkzawa.socketio.client.Ack;

public class PlayerContainer  extends RelativeLayout {
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

    public PlayerContainer (Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        this.playerCover = (ImageView)getRootView().findViewById(R.id.player_cover);
        this.playerHeaderCover = (ImageView)getRootView().findViewById(R.id.player_header_cover);
        this.trackName = (TextView)getRootView().findViewById(R.id.trackName);
        this.trackArtist = (TextView)getRootView().findViewById(R.id.trackArtist);

        this.playButtonHead = getRootView().findViewById(R.id.player_header_play);
        this.pauseButtonHead = getRootView().findViewById(R.id.player_header_pause);
        this.playButton = getRootView().findViewById(R.id.player_playButton);
        this.pauseButton = getRootView().findViewById(R.id.player_pauseButton);
        this.nextButton = getRootView().findViewById(R.id.player_nextButton);
        this.previousButton = getRootView().findViewById(R.id.player_previousButton);
        this.showVolume = getRootView().findViewById(R.id.showVolume);
        System.out.println("!!!!CREATED!!!!");
        System.out.println(trackName);
        if (((MainActivity)getContext()).getSocketService() != null) {
            setListeners();
            if (((MainActivity)getContext()).getSocketService().getPlayer().getPlaying() != null) {
                setPlaying(((MainActivity)getContext()).getSocketService().getPlayer().getPlaying());
            }
        }
    }

    public void setPlaying(final Track track) {
        if(trackName != null) {
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
                //playerCover.setImageBitmap(Bitmap.createBitmap(getContext().getResources().getDrawable(R.id.albumitem_cover)));
                //playerHeaderCover.setImageBitmap(track.getCover());
            }
        } else {
            System.out.println("track name undefined");
        }
    }

    public void setListeners() {
        if (playButton != null) {
            final PlayerControl player = ((MainActivity) getContext()).getSocketService().getPlayer();
            playButton.setOnClickListener(new OnClickListener() {
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
            playButtonHead.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.play();
                }
            });
            pauseButtonHead.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.pause();
                }
            });
            nextButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.next();
                }
            });
            previousButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.previous();
                }
            });
        }
    }
    public void playerStatusChanged() {
        final PlayerControl player = ((MainActivity) getContext()).getSocketService().getPlayer();
        if(player.getStatus().equals("PLAY")) {
            playButtonHead.setVisibility(View.GONE);
            pauseButtonHead.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else if(player.getStatus().equals("PAUSE")) {
            playButtonHead.setVisibility(View.VISIBLE);
            pauseButtonHead.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
    }
}
