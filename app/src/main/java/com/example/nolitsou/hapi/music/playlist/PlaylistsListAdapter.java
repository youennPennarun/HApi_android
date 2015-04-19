package com.example.nolitsou.hapi.music.playlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.R;
import com.example.nolitsou.hapi.utils.LoadImageTask;

import java.util.ArrayList;

public class PlaylistsListAdapter extends ArrayAdapter<Playlist> {
    private final AbstractActivity activity;
    private final ArrayList<Playlist> values;
    private AbsListView lv;
    private boolean scrolling;

    public PlaylistsListAdapter(AbstractActivity context, ArrayList<Playlist> playlists, AbsListView lv) {
        super(context, R.layout.alarm_list, playlists);
        this.activity = context;
        this.values = playlists;
        this.lv = lv;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Playlist playlist = values.get(position);
        final LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.playlists_item, parent, false);
        ((TextView) rowView.findViewById(R.id.playlistName)).setText(values.get(position).getName());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.changeFragment(new InPlaylistFragment(values.get(position)));
            }
        });
        ImageView cover = (ImageView) rowView.findViewById(R.id.playlistImage);
        if (playlist.getImageBitmap() == null) {
            if (playlist.getImageUri() != null) {
                GetPlaylistImageTask task = new GetPlaylistImageTask(activity, playlist, cover);
                task.execute();
            }
        } else {
            cover.setImageBitmap(playlist.getImageBitmap());
        }

        lv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                scrolling = (scrollState == SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
        cover.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (!scrolling) {
                    activity.getSocketService().getPlayer().playPlaylist(playlist);
                }
                return false;  // avoid extra click events
            }
        });
        return rowView;
    }


}

class GetPlaylistImageTask extends LoadImageTask {

    private Playlist playlist;

    public GetPlaylistImageTask(Context activity, Playlist playlist, ImageView view) {
        super(activity, playlist.getImageUri(), view);
        this.playlist = playlist;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        playlist.setImageBitmap(result);

    }

}
