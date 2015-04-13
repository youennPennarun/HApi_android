package com.example.nolitsou.hapi.data;

import com.example.nolitsou.hapi.MainActivity;
import com.github.nkzawa.socketio.client.Socket;

public abstract class SocketData {
    protected static MainActivity activity;

    public static void setActivity(MainActivity activity) {
        SocketData.activity = activity;
    }

    protected Socket getSocket() {
        return activity.getSocketService().getSocket();
    }
}
