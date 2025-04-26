package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils;

import androidx.lifecycle.Lifecycle;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;

public final class YouTubePlayerUtils {
    public static void loadOrCueVideo(YouTubePlayer youTubePlayer, Lifecycle lifecycle, String videoId, float startSeconds) {
        loadOrCueVideo(youTubePlayer, lifecycle.getCurrentState() == Lifecycle.State.RESUMED, videoId, startSeconds);
    }

    public static void loadOrCueVideo(YouTubePlayer youTubePlayer, boolean canLoad, String videoId, float startSeconds) {
        if (canLoad) {
            youTubePlayer.loadVideo(videoId, startSeconds);
        } else {
            youTubePlayer.cueVideo(videoId, startSeconds);
        }
    }
}