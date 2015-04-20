package com.example.nolitsou.hapi.music.userPlaylist;

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

public class UserPlaylistsListAdapter extends ArrayAdapter<UserPlaylist> {
    private final AbstractActivity activity;
    private final ArrayList<UserPlaylist> values;
    private AbsListView lv;
    private boolean scrolling;

    public UserPlaylistsListAdapter(AbstractActivity context, ArrayList<UserPlaylist> userPlaylists, AbsListView lv) {
        super(context, R.layout.alarm_list, userPlaylists);
        this.activity = context;
        this.values = userPlaylists;
        this.lv = lv;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final UserPlaylist userPlaylist = values.get(position);
        final LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.user_playlists_item, parent, false);
        ((TextView) rowView.findViewById(R.id.playlistName)).setText(values.get(position).getName());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.changeFragment(new InPlaylistFragment(values.get(position)));
            }
        });
        ImageView cover = (ImageView) rowView.findViewById(R.id.playlistImage);
        if (userPlaylist.getImageBitmap() == null) {
            if (userPlaylist.getImageUri() != null) {
                GetPlaylistImageTask task = new GetPlaylistImageTask(activity, userPlaylist, cover);
                task.execute();
            }
        } else {
            cover.setImageBitmap(userPlaylist.getImageBitmap());
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
                    activity.getSocketService().getPlayer().playPlaylist(userPlaylist);
                }
                return false;  // avoid extra click events
            }
        });
        return rowView;
    }


}

class GetPlaylistImageTask extends LoadImageTask {

    private UserPlaylist userPlaylist;

    public GetPlaylistImageTask(Context activity, UserPlaylist userPlaylist, ImageView view) {
        super(activity, userPlaylist.getImageUri(), view);
        this.userPlaylist = userPlaylist;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        userPlaylist.setImageBitmap(result);

    }

}
