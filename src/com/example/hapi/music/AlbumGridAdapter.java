package com.example.hapi.music;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hapi.PlayerControl;
import com.example.hapi.R;
import com.example.hapi.server.ServerLink;

public class AlbumGridAdapter extends BaseAdapter {

	final ArrayList<Album> mItems;
	final int mCount;

	/**
	 * Default constructor
	 * @param items to fill data to
	 */
	public AlbumGridAdapter(final ArrayList<Album> items) {

		mCount = items.size();
		mItems = items;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.albumlist_item, parent, false);
		}
		final ImageView albumCover = (ImageView)view.findViewById(R.id.albumitem_cover);
		final TextView albumName = (TextView) view.findViewById(R.id.albumitem_name);
		//albumCover.set
		albumName.setText(mItems.get(position).getName());
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Album album = mItems.get(position);
				System.out.println("play album "+album.getName()+":");
				PlayerControl.playAlbum(album);			
			}
		});

		return view;
	}
	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public Object getItem(final int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
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
			options.inSampleSize = 6;
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