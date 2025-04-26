package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.utils;

import android.graphics.Bitmap;

public class VideoInfo {
    private final String videoTitle;
    private final String channelTitle;
    private final Bitmap thumbnail;

    public VideoInfo(String videoTitle, String channelTitle, Bitmap thumbnail) {
        this.videoTitle = videoTitle;
        this.channelTitle = channelTitle;
        this.thumbnail = thumbnail;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
