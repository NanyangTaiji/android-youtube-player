package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;

public interface YouTubePlayerListener {
    /**
     * Called when the player is ready to play videos. You should start using with the player only after this method is called.
     * @param youTubePlayer The {@link YouTubePlayer} object.
     */
    void onReady(YouTubePlayer youTubePlayer);

    /**
     * Called every time the state of the player changes. Check {@link PlayerConstants.PlayerState} to see all the possible states.
     * @param state a state from {@link PlayerConstants.PlayerState}
     */
    void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state);

    /**
     * Called every time the quality of the playback changes. Check {@link PlayerConstants.PlaybackQuality} to see all the possible values.
     * @param playbackQuality a state from {@link PlayerConstants.PlaybackQuality}
     */
    void onPlaybackQualityChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackQuality playbackQuality);

    /**
     * Called every time the speed of the playback changes. Check {@link PlayerConstants.PlaybackRate} to see all the possible values.
     * @param playbackRate a state from {@link PlayerConstants.PlaybackRate}
     */
    void onPlaybackRateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate);

    /**
     * Called when an error occurs in the player. Check {@link PlayerConstants.PlayerError} to see all the possible values.
     * @param error a state from {@link PlayerConstants.PlayerError}
     */
    void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError error);

    /**
     * Called periodically by the player, the argument is the number of seconds that have been played.
     * @param second current second of the playback
     */
    void onCurrentSecond(YouTubePlayer youTubePlayer, float second);

    /**
     * Called when the total duration of the video is loaded. <br></br><br></br>
     * Note that getDuration() will return 0 until the video's metadata is loaded, which normally happens just after the video starts playing.
     * @param duration total duration of the video
     */
    void onVideoDuration(YouTubePlayer youTubePlayer, float duration);

    /**
     * Called periodically by the player, the argument is the percentage of the video that has been buffered.
     * @param loadedFraction a number between 0 and 1 that represents the percentage of the video that has been buffered.
     */
    void onVideoLoadedFraction(YouTubePlayer youTubePlayer, float loadedFraction);

    /**
     * Called when the id of the current video is loaded
     * @param videoId the id of the video being played
     */
    void onVideoId(YouTubePlayer youTubePlayer, String videoId);

    /**
     * Called when the player's API changes
     */
    void onApiChange(YouTubePlayer youTubePlayer);
}
