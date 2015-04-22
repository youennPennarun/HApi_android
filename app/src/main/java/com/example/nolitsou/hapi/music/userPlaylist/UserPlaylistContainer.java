package com.example.nolitsou.hapi.music.userPlaylist;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.R;
import com.example.nolitsou.hapi.data.Settings;
import com.example.nolitsou.hapi.utils.GetJsonTask;
import com.example.nolitsou.hapi.utils.ServerResponse;
import com.example.nolitsou.hapi.utils.ServerResponseError;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class UserPlaylistContainer extends LinearLayout {
    private static final String LOG_STR = "PlaylistFragment";
    public String title = "Playlists";
    private ArrayList<UserPlaylist> userPlaylists;
    private UserPlaylistsListAdapter adapter;

    public UserPlaylistContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        System.out.println("======HEY==============");
        userPlaylists = new ArrayList<>();
        ListView listView = (ListView) getRootView().findViewById(R.id.playlist_list);
        adapter = new UserPlaylistsListAdapter((AbstractActivity) getContext(), userPlaylists, listView);
        listView.setAdapter(adapter);
        loadData();
    }

    public void loadData() {
        Log.i(LOG_STR, "Getting playlists on url: " + Settings.host + "user/playlists");

        GetPlaylistsTask get = new GetPlaylistsTask();
        Settings.loadSettings(getContext());
        get.execute(Settings.host + "user/playlists");
    }

    class GetPlaylistsTask extends GetJsonTask {
        @Override
        protected void onPostExecute(ServerResponse result) {
            int i = 0;
            System.out.println(result);
            if (result.success) {
                System.out.println(result.data);
                userPlaylists.clear();
                try {
                    JSONArray list = result.data.getJSONArray("items");
                    for (i = 0; i < list.length(); i++) {
                        userPlaylists.add(UserPlaylist.spotifyResultToPlaylist(list.getJSONObject(i)));
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (((ServerResponseError) result).getCode() == 401) {
                System.out.println((ServerResponseError) result);
                ((AbstractActivity) getContext()).notLoggedIn();
            }
        }
    }


}
