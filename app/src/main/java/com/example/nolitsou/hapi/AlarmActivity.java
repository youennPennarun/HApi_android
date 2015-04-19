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
    protected void loadData() {
        AlarmContainer container = (AlarmContainer)findViewById(R.id.alarm_content_layout);
        container.loadData();

    }
}



