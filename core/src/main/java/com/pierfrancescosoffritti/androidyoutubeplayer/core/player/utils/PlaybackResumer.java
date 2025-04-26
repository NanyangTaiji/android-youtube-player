package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

public class PlaybackResumer extends AbstractYouTubePlayerListener {
    private boolean canLoad = false;
    private boolean isPlaying = false;
    private PlayerConstants.PlayerError error = null;
    private String currentVideoId = null;
    private float currentSecond = 0f;

    public void resume(YouTubePlayer youTubePlayer) {
        if (currentVideoId == null) return;

        if (isPlaying && error == PlayerConstants.PlayerError.HTML_5_PLAYER) {
            youTubePlayer.loadVideo(currentVideoId, currentSecond);
            //TODO ny
            //youTubePlayer.loadOrCueVideo(canLoad, currentVideoId, currentSecond);
        } else if (!isPlaying && error == PlayerConstants.PlayerError.HTML_5_PLAYER) {
            youTubePlayer.cueVideo(currentVideoId, currentSecond);
        }

        error = null;
    }

    @Override
    public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
        switch (state) {
            case ENDED:
            case PAUSED:
                isPlaying = false;
                break;
            case PLAYING:
                isPlaying = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError error) {
        if (error == PlayerConstants.PlayerError.HTML_5_PLAYER) {
            this.error = error;
        }
    }

    @Override
    public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
        currentSecond = second;
    }

    @Override
    public void onVideoId(YouTubePlayer youTubePlayer, String videoId) {
        currentVideoId = videoId;
    }

    public void onLifecycleResume() {
        canLoad = true;
    }

    public void onLifecycleStop() {
        canLoad = false;
    }
}
