package com.example.hapi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.hapi.MainActivity;
import com.example.hapi.data.Settings;

public class ServerLinkTask extends AsyncTask<String, String, Void> {

	InputStream inputStream = null;
	String result = ""; 
	String url;
	private MainActivity mainActivity;
	//private ProgressDialog mDialog;
	private Exception error;
	private ProgressDialog mDialog;

	public ServerLinkTask(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	protected void onPreExecute() {
		mDialog = new ProgressDialog(mainActivity);
		mDialog.setMessage("Please wait...");
		mDialog.show();
	}

	protected Void doInBackground(String... data) {
		this.url = data[0];
		this.error=null;
		if(!url.endsWith("/")) {
			url += "/";
		}
		String url_select = url + "user/login/token";
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("username", Settings.username));
		param.add(new BasicNameValuePair("password", Settings.password));
		System.out.println("getting token on url '" + url_select + "'...");
		try {
			// Set up HTTP post

			// HttpClient is more then less deprecated. Need to change to URLConnection
			HttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url_select);
			httpPost.setEntity(new UrlEncodedFormEntity(param));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			// Read content & Log
			inputStream = httpEntity.getContent();
		} catch (UnsupportedEncodingException e1) {
			Log.e("UnsupportedEncodingException", e1.toString());
			e1.printStackTrace();
			this.error=e1;
		} catch (ClientProtocolException e2) {
			Log.e("ClientProtocolException", e2.toString());
			e2.printStackTrace();
			this.error=e2;
		} catch (IllegalStateException e3) {
			Log.e("IllegalStateException", e3.toString());
			e3.printStackTrace();
			this.error=e3;
		} catch (IOException e4) {
			Log.e("IOException", e4.toString());
			e4.printStackTrace();
			this.error=e4;
		}
		if(error != null) {
			return null;
		}
		// Convert response to string using String Builder
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
			StringBuilder sBuilder = new StringBuilder();

			String line = null;
			while ((line = bReader.readLine()) != null) {
				sBuilder.append(line + "\n");
			}

			inputStream.close();
			result = sBuilder.toString();

		} catch (Exception e) {
			Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
		}
		return null;
	}
	protected void onPostExecute(Void v) {
		//parse JSON data
		if(error != null) {
			System.out.println("there is an error");
			showError(error.toString());
		} else {
			JSONObject data;
			try {
				data = (new JSONObject(result)).getJSONObject("data");
				if (data.has("error")) {
					String error = data.getString("error");
					showError(error);
					if (error.equals("invalid user")) {
						mainActivity.sendToSettings();
					}
				} else if(data.has("token")) {
					String token = data.getString("token");
					System.out.println("token="+token);

					//TextView t=(TextView)mainActivity.findViewById(R.id.connectionStatus); 
					if (ServerLink.connect(url, token)) {
						mainActivity.onConnectionSet();
						mDialog.dismiss();
					} else {
						showError("connection error");
					}
				} else {

				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}


	}
	private void showError(String error) {
		//mDialog.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setMessage(error)
		.setTitle("Error!");
		AlertDialog dialog = builder.create();
		dialog.show();

	}
}