package com.example.nolitsou.hapi;

import android.os.Bundle;

/**
 * Created by nolitsou on 4/15/15.
 */
public class SettingsActivity extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        createDefault();
        SettingsContainer container = (SettingsContainer)findViewById(R.id.settings);
        container.loadData();

    }
    @Override
    protected void loadData() {

    }
}
