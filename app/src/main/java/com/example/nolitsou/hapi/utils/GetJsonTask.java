package com.example.nolitsou.hapi.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by nolitsou on 4/22/15.
 */
public abstract class GetJsonTask extends AsyncTask<String, Integer, ServerResponse> {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @Override
    protected ServerResponse doInBackground(String... params) {
        ServerResponse result = null;
        if (params.length > 0) {
            StringBuilder builder = new StringBuilder();
            HttpGet httpGet = new HttpGet(params[0]);
            try {
                HttpResponse response = ServerLink.getHttpClient().execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line = "";
                    String resultStr = "";
                    while ((line = reader.readLine()) != null) {
                        resultStr += line;
                    }
                    reader.close();

                    result = ServerLink.handleResponse(new JSONObject(resultStr));
                } else {
                    Log.e(getClass().toString(), "Failedet JSON object");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected abstract void onPostExecute(ServerResponse result);
}
