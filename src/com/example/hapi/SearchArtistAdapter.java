package com.example.hapi;

import java.io.InputStream;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ValueAnimator;
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

public class SearchArtistAdapter extends ArrayAdapter<SearchArtist> {
	private final Context context;
	private final ArrayList<SearchArtist> values;

	public SearchArtistAdapter(Context context, ArrayList<SearchArtist> alarms) {
		super(context, R.layout.alarm_list, alarms);
		this.context = context;
		this.values = alarms;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.search_artist_item, parent, false);
		System.out.println("new artist");
		if (values.get(position).imageUrls.size() > 0) {
			new DownloadImageTask((ImageView) rowView.findViewById(R.id.artistImage))
			.execute(values.get(position).imageUrls.get(0));
		}
		((TextView)rowView.findViewById(R.id.artistName)).setText(values.get(position).artistName);
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
