package com.example.nolitsou.hapi.data;

import com.example.nolitsou.hapi.AbstractActivity;
import com.example.nolitsou.hapi.AlarmContainer;

public abstract class SocketData {
    protected static AbstractActivity activity;

    public static void setActivity(AbstractActivity activity) {
        SocketData.activity = activity;
    }

    public static void setAlarmContainer(AlarmContainer alarmContainer) {

    }

}
