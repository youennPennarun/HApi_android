package com.example.nolitsou.hapi.server;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.nolitsou.hapi.PlayerControl;
import com.example.nolitsou.hapi.data.Alarm;
import com.example.nolitsou.hapi.data.Settings;
import com.example.nolitsou.hapi.data.User;
import com.example.nolitsou.hapi.music.SearchArtist;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.IO.Options;
import com.github.nkzawa.socketio.client.Socket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class SocketService extends Service {
    public final static int REGISTER_CLIENT = -1;
    private final static String LOG_STR = "SocketService:";
    public static boolean piConnected = false;
    private final IBinder mBinder = new SocketBinder();
    public SocketConnectionEnum status = SocketConnectionEnum.DISCONNECTED;
    ArrayList<Messenger> clients = new ArrayList<>();
    private Messenger messenger = new Messenger(new IncomingHandler(this));
    private PlayerControl player = new PlayerControl(this);
    private Socket socket;
    private String host;
    private User user = new User();

    public boolean connect(String url, String token) {
        if (getSocket() != null) {
            getSocket().disconnect();
            getSocket().close();
        }
        try {
            Options opts = new Options();
            opts.reconnection = true;
            System.out.println(url + "/?" + token);
            socket = (IO.socket(url + "?token=" + token, opts));
            System.out.println("socket set");
            setListeners();
            getSocket().connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        while (!socket.connected()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return socket.connected();
    }

    private void setListeners() {
        getSocket().on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(LOG_STR, "socket connceted");
                socket.emit("music:playing:get");
                socket.emit("pi:sound:volume:get");
                socket.emit("pi:is-logged-in");
                socket.emit("music:playlist:get");
            }

        }).on("pi:is-logged-in", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // TODO

            }
        }).on("pi:logged-out", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //activity.getPlayerFragment().setPiNotConnectedView();
                piConnected = false;
                System.out.println("pi logged out");
                //activity.notifPiDisconnected();
            }
        }).on("pi:logged-in", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                /*
                activity.getPlayerFragment().setPiConnectedView();
				piConnected = true;
				activity.dismissPiNotif();
				 */
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.w(LOG_STR, "socket disconnected");
            }

        }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                System.out.println("RECONNECTING!!!!!");
            }
        }).on("error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0) {
                    JSONObject error = (JSONObject) args[0];
                    Log.e(LOG_STR, "socket connection error: " + args[0]);
                    try {
                        if (error.getString("type").equals("UnauthorizedError") || error.getString("code").equals("invalid_token")) {
                            socket.disconnect();
                            reconnect();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        });
        Alarm.setSocket(getSocket());
        SearchArtist.setSocket(getSocket());
        player.setSocket(getSocket());
    }

    public void reconnect() {
        Log.i(LOG_STR, "reconnecting");
        if (socket.connected()) {
            getSocket().disconnect();
            getSocket().close();
        }
        ServerLinkTask task = new ServerLinkTask();
        task.execute();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (socket == null || !socket.connected()) {
            Log.i(LOG_STR, "Starting service");
            Settings.loadSettings(this);
            this.setHost(Settings.host);
            ServerLinkTask task = new ServerLinkTask();
            task.execute(getHost());
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_STR, "Got binding request");
        return mBinder;
    }

    public void connectionTaskDone(SocketConnectionEnum e) {
        Log.e(LOG_STR, "Connection error:" + e);
        this.status = e;
        broadcast(e.getCode());
    }

    public void connectionTaskDone() {
        broadcast(SocketConnectionEnum.CONNECTED.getCode());
    }

    public void broadcast(int msg) {
        for (int i = 0; i < clients.size(); i++) {
            try {
                clients.get(i).send(Message.obtain(null, msg));
            } catch (RemoteException e) {
                clients.remove(i);
            }
        }
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SocketService.REGISTER_CLIENT:
                clients.add(msg.replyTo);
                Log.i(LOG_STR, "new client registered");
                Message serverStatusMsg = getServerStatusMessage();
                serverStatusMsg.replyTo = messenger;
                try {
                    msg.replyTo.send(Message.obtain(null, getStatus().getCode()));
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                Log.w(LOG_STR, "Unknown message '" + msg.what + "'");

        }
    }

    private Message getServerStatusMessage() {
        return Message.obtain(null, status.getCode());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SocketConnectionEnum getStatus() {
        if (isConnected()) {
            return SocketConnectionEnum.CONNECTED;
        } else {
            return status;
        }
    }

    public boolean isConnected() {
        return (socket != null && socket.connected());
    }

    public PlayerControl getPlayer() {
        return player;
    }

    static class IncomingHandler extends Handler {
        private final WeakReference<SocketService> mService;

        IncomingHandler(SocketService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            SocketService service = mService.get();
            if (service != null) {
                service.handleMessage(msg);
            }
        }
    }

    public class SocketBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    class ServerLinkTask extends AsyncTask<String, String, Void> {

        InputStream inputStream = null;
        String result = "";
        String url;
        //private ProgressDialog mDialog;
        private SocketConnectionEnum status = null;

        public ServerLinkTask() {
        }

        protected void onPreExecute() {
        }

        protected Void doInBackground(String... data) {
            Log.i(LOG_STR, "Executing ServerLinkTask");
            this.url = Settings.host;
            if (!this.url.equals("")) {
                if (!url.endsWith("/")) {
                    url += "/";
                }
                String url_select = url + "user/login/token";
                ArrayList<NameValuePair> param = new ArrayList<>();
                param.add(new BasicNameValuePair("username", Settings.username));
                param.add(new BasicNameValuePair("password", Settings.password));
                Log.i(LOG_STR, "Getting token with url " + url_select);
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
                } catch (IOException e1) {
                    Log.e("UnsupportedEncoding", e1.toString());
                    e1.printStackTrace();
                    this.status = SocketConnectionEnum.UNKNOWN_ERROR;
                }
                if (this.status != null) {
                    return null;
                }
                // Convert response to string using String Builder
                try {
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    StringBuilder sBuilder = new StringBuilder();

                    String line;
                    while ((line = bReader.readLine()) != null) {
                        sBuilder.append(line + "\n");
                    }

                    inputStream.close();
                    result = sBuilder.toString();

                } catch (Exception e) {
                    Log.e("StringBuilding", "Error converting result " + e.toString());
                }
            } else {
                this.status = SocketConnectionEnum.INVALID_CREDENTIALS;
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            //parse JSON data
            if (this.status != null) {
                System.out.println("there is an error");
                connectionTaskDone(this.status);
            } else {
                JSONObject data;
                try {
                    System.out.println("!!!-> " + result);
                    data = (new JSONObject(result)).getJSONObject("data");
                    if (data.has("error")) {
                        String error = data.getString("error");
                        if (error.equals("invalid user")) {
                            connectionTaskDone(SocketConnectionEnum.INVALID_CREDENTIALS);
                        } else {
                            connectionTaskDone(SocketConnectionEnum.UNKNOWN_ERROR);
                        }
                    } else if (data.has("token")) {
                        String token = data.getString("token");
                        Log.i(LOG_STR, "Connecting to socket");
                        //TextView t=(TextView)AbstractActivity.findViewById(R.id.connectionStatus);
                        if (connect(url, token)) {
                            connectionTaskDone();
                        } else {
                            connectionTaskDone(SocketConnectionEnum.UNKNOWN_ERROR);
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }


        }
    }
}