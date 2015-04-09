package com.example.hapi.music.playlist;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hapi.PlayerControl;
import com.example.hapi.R;

public class PlaylistsListAdapter extends ArrayAdapter<Playlist> {
	private final Context context;
	private final ArrayList<Playlist> values;
	private AbsListView lv;
	private boolean scrolling;

	public PlaylistsListAdapter(Context context, ArrayList<Playlist> playlists, AbsListView lv) {
		super(context, R.layout.alarm_list, playlists);
		this.context = context;
		this.values = playlists;
		this.lv = lv;
	}


	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final Playlist playlist = values.get(position);
		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.playlists_item, parent, false);
		((TextView)rowView.findViewById(R.id.playlistName)).setText(values.get(position).getName());

		ImageView cover = (ImageView)rowView.findViewById(R.id.playlistImage);
		if(playlist.getImageBitmap() == null) {
			if(playlist.getImageUri() != null) {
				GetPlaylistImageTask task = new GetPlaylistImageTask(playlist, cover);
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
		cover.setOnTouchListener(new View.OnTouchListener() {
			final GestureDetector gesture = new GestureDetector(parent.getContext(),
					new GestureDetector.SimpleOnGestureListener() {
				public void onLongPress(MotionEvent e) {
					if(!scrolling) {
						PlayerControl.playPlaylist(playlist);
					}
				}
				public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
					return true;
				}
			});
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gesture.onTouchEvent(event);
			}
		});
		return rowView;
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;
			options.inSampleSize = 12;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in, null, options);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}
}
class GetPlaylistImageTask extends AsyncTask<String, String, Bitmap> {


	private Playlist playlist;
	private ImageView view;
	public GetPlaylistImageTask(Playlist playlist, ImageView view) {
		this.playlist = playlist;
		this.view = view;
	}

	protected Bitmap  doInBackground(String... data) {
		System.out.println(playlist.getImageUri());
		String urldisplay = playlist.getImageUri();
		Bitmap mIcon11 = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.RGB_565;
		options.inSampleSize = 7;
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in, null, options);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return mIcon11;
	}

	protected void onPostExecute(Bitmap result) {
		playlist.setImageBitmap(result);
		view.setImageBitmap(playlist.getImageBitmap());
	}
}
