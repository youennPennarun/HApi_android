package com.example.nolitsou.hapi.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nolitsou on 4/22/15.
 */
public class ServerResponseError extends ServerResponse {
    private int code = -1;
    private String message = "";
    private String source = "unknown";

    public ServerResponseError(JSONObject data) throws JSONException {
        super(false, data);
        if (data.has("code")) {
            this.code = data.getInt("code");
        }
        if (data.has("messge")) {
            this.message = data.getString("message");
        }
        if (data.has("source")) {
            this.source = data.getString("source");
        }
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Error " + code + ": " + message + " (source: " + source + ")";
    }
}
