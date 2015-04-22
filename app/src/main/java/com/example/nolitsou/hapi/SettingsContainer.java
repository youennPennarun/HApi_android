package com.example.nolitsou.hapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.nolitsou.hapi.data.Preferences;
import com.example.nolitsou.hapi.data.Settings;

public class SettingsContainer extends RelativeLayout {
    public SettingsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {

    }

    public void loadData() {
        EditText hostValue = (EditText) getRootView().findViewById(R.id.hostValue);
        EditText usernameValue = (EditText) getRootView().findViewById(R.id.usernameValue);
        EditText passwordValue = (EditText) getRootView().findViewById(R.id.passwordValue);
        hostValue.setText(Settings.host);
        usernameValue.setText(Settings.username);
        passwordValue.setText(Settings.password);
        Button saveSettingsBtn = (Button) getRootView().findViewById(R.id.saveSettings);

        //Listening to button event
        saveSettingsBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Starting a new Intent
                EditText hostValue = (EditText) getRootView().findViewById(R.id.hostValue);
                EditText usernameValue = (EditText) getRootView().findViewById(R.id.usernameValue);
                EditText passwordValue = (EditText) getRootView().findViewById(R.id.passwordValue);
                final SharedPreferences prefs = new Preferences(
                        getContext(), getContext().getSharedPreferences(Settings.PREF_FILE, Context.MODE_PRIVATE));
                prefs.edit().putString(Settings.HOST_PREF_STR, hostValue.getText().toString()).commit();
                prefs.edit().putString(Settings.USERNAME_PREF_STR, usernameValue.getText().toString()).commit();
                prefs.edit().putString(Settings.PASSWORD_PREF_STR, passwordValue.getText().toString()).commit();
                Settings.loadSettings(getContext());
            }
        });
    }

}