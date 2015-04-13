package com.example.nolitsou.hapi.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SocketReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SocketReceiver received!");
    }
} 