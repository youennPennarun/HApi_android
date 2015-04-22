package com.example.nolitsou.hapi.utils;

import org.json.JSONObject;

/**
 * Created by nolitsou on 4/22/15.
 */
public class ServerResponse {
    public boolean success;
    public JSONObject data;

    public ServerResponse(boolean success, JSONObject data) {
        this.success = success;
        this.data = data;
    }
}
