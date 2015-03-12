package com.example.hapi;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hapi.music.SearchArtist;
import com.example.hapi.music.Track;

public class TrackListAdapter extends ArrayAdapter<Track> {
	private final Context context;
	private final ArrayList<Track> values;

	public TrackListAdapter(Context context, int listLayoutId, ArrayList<Track> tracks) {
		super(context, listLayoutId, tracks);
		this.context = context;
		this.values = tracks;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.tracklist_item, parent, false);
		((TextView)rowView.findViewById(R.id.tracklist_item_name)).setText(values.get(position).getName());
		LinearLayout trackItem = (LinearLayout)rowView.findViewById(R.id.tracklist_item);
		trackItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PlayerControl.playTrack(values.get(position));
			}
		});
		return rowView;
	}
}
