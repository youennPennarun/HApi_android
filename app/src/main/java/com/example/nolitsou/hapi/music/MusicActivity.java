package com.example.nolitsou.hapi.music;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

import com.example.nolitsou.hapi.R;

public class MusicActivity extends FragmentActivity {
    protected View notSetView;
    private MenuItem menuItem;
    private SearchMusicFragment searchMusicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.music);
        Intent intent = getIntent();
        String args = intent.getStringExtra("searchQuery");
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        this.searchMusicFragment = (SearchMusicFragment) Fragment.instantiate(this, SearchMusicFragment.class.getName());
        fragmentList.add(searchMusicFragment);
        if (args != null) {
            Bundle bundle = new Bundle();
            bundle.putString("searchQuery", args);
            searchMusicFragment.setArguments(bundle);
        }
        //ServerLink.setAbstractActivity(this);
        Settings.loadSettings(this);

        getSupportFragmentManager().beginTransaction().add(R.id.music_frame, searchMusicFragment).addToBackStack(null).commit();
        */
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
