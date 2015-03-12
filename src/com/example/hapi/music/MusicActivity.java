package com.example.hapi.music;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

import com.example.hapi.R;
import com.example.hapi.data.Settings;

public class MusicActivity extends FragmentActivity {
	private MenuItem menuItem;
	protected View notSetView;
	private SearchMusicFragment searchMusicFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music);
		Intent intent = getIntent();
		String args = intent.getStringExtra("searchQuery");
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>(); 
		this.searchMusicFragment = (SearchMusicFragment) Fragment.instantiate(this,SearchMusicFragment.class.getName());
		fragmentList.add(searchMusicFragment);
		if(args != null) {
			Bundle bundle=new Bundle();
			bundle.putString("searchQuery", args);
			searchMusicFragment.setArguments(bundle);
		}
		//ServerLink.setMainActivity(this);
		Settings.loadSettings(this);
		
		getSupportFragmentManager().beginTransaction().add(R.id.music_frame, searchMusicFragment).addToBackStack(null).commit();
	}
	public void changeFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.music_frame, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void sendToSettings() {
	}
}
