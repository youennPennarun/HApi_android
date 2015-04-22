package com.example.nolitsou.hapi;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nolitsou on 4/22/15.
 */
public class GCMSender {
    private static GoogleCloudMessaging gcm = null;
    private static AtomicInteger msgId = new AtomicInteger();

    public static void setGcm(GoogleCloudMessaging gcm) {
        GCMSender.gcm = gcm;
    }

    public static void send(String message) {
        new Send(gcm, message).execute();
    }

    public static void send(String message, JSONObject data) {
        new Send(gcm, message, data).execute();
    }

    static class Send extends AsyncTask {
        private String message;
        private GoogleCloudMessaging gcm;
        private JSONObject param = new JSONObject();

        Send(GoogleCloudMessaging gcm, String message) {
            this.message = message;
            this.gcm = gcm;
            System.out.println("--->sending:" + message);
        }

        public Send(GoogleCloudMessaging gcm, String message, JSONObject data) {
            this.message = message;
            this.param = data;
            this.gcm = gcm;
            System.out.println("--->sending:" + message);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            String msg = "";
            try {
                Bundle data = new Bundle();
                data.putString("msg", message);
                data.putString("data", param.toString());
                String id = Integer.toString(msgId.incrementAndGet());
                System.out.println("--->Bundle created");
                gcm.send(AbstractActivity.PROJECT_NUMBER + "@gcm.googleapis.com", id, data);
                msg = "cool...";
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                ex.printStackTrace();
            }
            System.out.println("finally: " + msg);
            return msg;
        }
    }
}
