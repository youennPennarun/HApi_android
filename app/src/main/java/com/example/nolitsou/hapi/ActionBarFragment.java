package com.example.nolitsou.hapi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionBarFragment extends CustomFragment {

    private ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = (ViewGroup) inflater.inflate(
                R.layout.actionbar, container, false);
        TextView mTitleTextView = (TextView) rootView.findViewById(R.id.title_text);
        mTitleTextView.setText("Alarms");

        ((MainActivity) getActivity()).hidePlayer();
        ImageView homeBtn = (ImageView) rootView
                .findViewById(R.id.actionbar_home);
        homeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getmSlidingMenu().toggleLeftDrawer();
            }
        });
        ImageButton actionbar_player = (ImageButton) rootView
                .findViewById(R.id.actionbar_player);


        return rootView;

    }

    public ViewGroup getRootView() {
        return rootView;
    }

    public void hide(MainActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .hide(this)
                .commitAllowingStateLoss();
    }

    public void show() {

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .show(this)
                .commitAllowingStateLoss();
    }

    public void setTitle(String title) {
        TextView mTitleTextView = (TextView) rootView.findViewById(R.id.title_text);
        mTitleTextView.setText(title);
    }

    @Override
    public void loadData() {
        // TODO Auto-generated method stub

    }

}
