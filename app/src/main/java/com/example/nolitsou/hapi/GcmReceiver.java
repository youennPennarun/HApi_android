package com.example.nolitsou.hapi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by nolitsou on 4/20/15.
 */
public class GcmReceiver extends WakefulBroadcastReceiver {
    private static final int NOTIFICATION_ID = 456;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("RECEIVED");
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}

