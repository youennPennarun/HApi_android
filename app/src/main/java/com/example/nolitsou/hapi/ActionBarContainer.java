package com.example.nolitsou.hapi;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nolitsou.hapi.widgets.SimpleSideDrawer;

public class ActionBarContainer extends RelativeLayout {
    private SimpleSideDrawer mSlidingMenu;


    private SimpleSideDrawer drawer;

    public ActionBarContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        TextView mTitleTextView = (TextView) getRootView().findViewById(R.id.title_text);
        mTitleTextView.setText("Alarms");

        ImageView homeBtn = (ImageView) getRootView()
                .findViewById(R.id.actionbar_home);
        homeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.toggleLeftDrawer();
            }
        });

    }
    public void setListeners() {
        drawer = ((AbstractActivity) getContext()).getmSlidingMenu();
        drawer.getRootView().findViewById(R.id.menu_goto_alarms).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(getContext() instanceof AlarmActivity)) {
                    Intent intent = new Intent(getContext(), AlarmActivity.class);
                    getContext().startActivity(intent);
                }
            }
        });
        drawer.getRootView().findViewById(R.id.menu_goto_playlist).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(getContext() instanceof PlaylistActivity)) {
                    Intent intent = new Intent(getContext(), PlaylistActivity.class);
                    getContext().startActivity(intent);
                }
            }
        });
        drawer.getRootView().findViewById(R.id.menu_goto_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(getContext() instanceof SettingsActivity)) {
                    Intent intent = new Intent(getContext(), SettingsActivity.class);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    public void setTitle(String title) {
        TextView mTitleTextView = (TextView) getRootView().findViewById(R.id.title_text);
        mTitleTextView.setText(title);
    }
}
