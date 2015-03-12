package com.example.hapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.hapi.data.Preferences;
import com.example.hapi.data.Settings;

public class SettingsFragment extends CustomFragment {
	private ViewGroup rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.rootView = (ViewGroup) inflater.inflate(
				R.layout.settings, container, false);
		title = "Settings";
		EditText hostValue = (EditText) rootView.findViewById(R.id.hostValue);
		EditText usernameValue = (EditText) rootView.findViewById(R.id.usernameValue);
		EditText passwordValue = (EditText) rootView.findViewById(R.id.passwordValue);
		hostValue.setText(Settings.host);
		usernameValue.setText(Settings.username);
		passwordValue.setText(Settings.password);
		Button saveSettingsBtn = (Button) rootView.findViewById(R.id.saveSettings);

		//Listening to button event
		saveSettingsBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				//Starting a new Intent
				EditText hostValue = (EditText) rootView.findViewById(R.id.hostValue);
				EditText usernameValue = (EditText) rootView.findViewById(R.id.usernameValue);
				EditText passwordValue = (EditText) rootView.findViewById(R.id.passwordValue);
				final SharedPreferences prefs = new Preferences( 
						getActivity(), getActivity().getSharedPreferences(Settings.PREF_FILE, Context.MODE_PRIVATE) );
				prefs.edit().putString(Settings.HOST_PREF_STR, hostValue.getText().toString()).commit();
				prefs.edit().putString(Settings.USERNAME_PREF_STR, usernameValue.getText().toString()).commit();
				prefs.edit().putString(Settings.PASSWORD_PREF_STR, passwordValue.getText().toString()).commit();
				Settings.loadSettings(getActivity());
				((MainActivity)getActivity()).changeFragment(new AlarmFragment());
			}
		});
		return rootView;
	}
}