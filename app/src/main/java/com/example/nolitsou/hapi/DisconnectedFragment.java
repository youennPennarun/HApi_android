package com.example.nolitsou.hapi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DisconnectedFragment extends CustomFragment {

    private ViewGroup rootView;
    private AlarmsAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = (ViewGroup) inflater.inflate(
                R.layout.disconnected, container, false);

        return rootView;
    }

    @Override
    public void loadData() {
        // TODO Auto-generated method stub

    }

}