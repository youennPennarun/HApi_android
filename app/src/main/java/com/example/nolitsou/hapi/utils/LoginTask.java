package com.example.nolitsou.hapi.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.data.Settings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolitsou on 4/22/15.
 */
public class LoginTask extends AsyncTask<Void, Integer, JSONObject> {
    private static final String TAG = "LoginTask";
    private final AbstractActivity activity;

    public LoginTask(AbstractActivity activity) {
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        System.out.println("Login to " + Settings.host);
        List<Cookie> cookies;
        HttpPost httppost = new HttpPost(Settings.host + "user/login");
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", Settings.username));
            nameValuePairs.add(new BasicNameValuePair("password", Settings.password));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = ServerLink.getHttpClient().execute(httppost);
            HttpEntity entity = response.getEntity();
            System.out.println("Login form get: " + response.getStatusLine());
            Log.d(TAG, "Login form get: " + response.getStatusLine());
            if (entity != null) {
                entity.consumeContent();
            }
            cookies = ServerLink.getHttpClient().getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                Log.d(TAG, "None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    Log.d(TAG, "- " + cookies.get(i));
                }
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        activity.loadData();
    }
}
