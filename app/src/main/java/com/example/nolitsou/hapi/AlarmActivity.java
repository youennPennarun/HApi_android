package com.example.nolitsou.hapi;

import android.os.Bundle;

public class AlarmActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_main);
        createDefault();
    }

    @Override
    public void loadData() {
        AlarmContainer alarmContainer = (AlarmContainer) findViewById(R.id.alarm_content_layout);
        System.out.println("Refresh data...");
        alarmContainer.loadData();
    }
}



