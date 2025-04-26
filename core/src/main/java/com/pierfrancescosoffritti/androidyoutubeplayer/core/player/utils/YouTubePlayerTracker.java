package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

public class YouTubePlayerTracker extends AbstractYouTubePlayerListener {
    private PlayerConstants.PlayerState state = PlayerConstants.PlayerState.UNKNOWN;
    private float currentSecond = 0f;
    private float videoDuration = 0f;
    private String videoId = null;

    public PlayerConstants.PlayerState getState() {
        return state;
    }

    public float getCurrentSecond() {
        return currentSecond;
    }

    public float getVideoDuration() {
        return videoDuration;
    }

    public String getVideoId() {
        return videoId;
    }

    @Override
    public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
        this.state = state;
    }

    @Override
    public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
        currentSecond = second;
    }

    @Override
    public void onVideoDuration(YouTubePlayer youTubePlayer, float duration) {
        videoDuration = duration;
    }

    @Override
    public void onVideoId(YouTubePlayer youTubePlayer, String videoId) {
        this.videoId = videoId;
    }
}
