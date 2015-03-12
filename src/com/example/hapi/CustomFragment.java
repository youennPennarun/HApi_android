package com.example.hapi;

import android.support.v4.app.Fragment;

public class CustomFragment extends Fragment{
	public String title;
	
	public String getTitle () {
		if (title == null) {
			return getActivity().getString(R.string.app_name);
		} else {
			return title;
		}
	}
}
