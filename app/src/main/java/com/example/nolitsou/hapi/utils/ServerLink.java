package com.example.nolitsou.hapi.utils;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nolitsou on 4/22/15.
 */
public class ServerLink {
    private static DefaultHttpClient httpclient;

    public static DefaultHttpClient getHttpClient() {
        if (httpclient == null) {
            httpclient = new DefaultHttpClient();
        }
        return httpclient;
    }

    public static ServerResponse handleResponse(JSONObject response) {
        ServerResponse data = null;
        try {
            if (response.has("status")) {
                if (response.getString("status").equals("SUCCESS")) {
                    if (response.has("data")) {
                        data = new ServerResponse(true, response.getJSONObject("data"));
                    }
                } else if (response.getString("status").equals("ERROR")) {
                    if (response.has("data")) {
                        data = new ServerResponseError(response.getJSONObject("data"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}


