package com.example.nolitsou.hapi.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.PlayerControl;
import com.example.nolitsou.hapi.R;
import com.github.nkzawa.socketio.client.Ack;

import java.util.ArrayList;

public class PlaylistListAdapter extends ArrayAdapter<Track> {
    private final AbstractActivity context;
    private final ArrayList<Track> values;

    public PlaylistListAdapter(AbstractActivity context, ArrayList<Track> tracks) {
        super(context, R.layout.playlist, tracks);
        this.context = context;
        this.values = tracks;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PlayerControl player = context.getSocketService().getPlayer();
        final Track track = values.get(position);
        final View rowView = inflater.inflate(R.layout.playlist_item, parent, false);
        ((TextView) rowView.findViewById(R.id.track_item_name)).setText(track.getName());
        LinearLayout trackItem = (LinearLayout) rowView.findViewById(R.id.playlist_item);
        final ImageView coverView = (ImageView)rowView.findViewById(R.id.track_cover);
        if (track.getCover() == null) {
            track.loadCover(getContext(), new Ack() {
                @Override
                public void call(Object... args) {
                    if (args.length > 0) {
                        coverView.setImageBitmap(track.getCover());
                    }
                }
            });
        } else {
            coverView.setImageBitmap(track.getCover());
        }
        if (player.getIdPlaying() == position) {
            rowView.findViewById(R.id.track_isPlaying).setVisibility(View.VISIBLE);
        } else {
            rowView.findViewById(R.id.track_isPlaying).setVisibility(View.GONE);
        }
        trackItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null) {
                    player.playTrackInPlaylist(position);
                }
            }
        });
        return rowView;
    }


}
