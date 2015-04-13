package com.example.nolitsou.hapi.music;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.nolitsou.hapi.utils.LoadImageTask;
import com.github.nkzawa.socketio.client.Ack;


public class GetTrackCover extends LoadImageTask {

    private Ack ack;
    public Track track;

    public GetTrackCover(Context activity, Track track, Ack ack) {
        super(activity, track.getAlbum().getImageUrls().get(0));
        this.ack = ack;
        this.track = track;
        options.requiredSize = 300;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        track.setCover(result);
        ack.call(result);
    }
}