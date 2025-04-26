package com.pierfrancescosoffritti.androidyoutubeplayer.core.player;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;

/**
 * Use this interface to control the playback of YouTube videos and to listen to their events.
 */
public interface YouTubePlayer {
    /**
     * Loads and automatically plays the video.
     * @param videoId id of the video
     * @param startSeconds the time from which the video should start playing
     */
    void loadVideo(String videoId, float startSeconds);

    /**
     * Loads the video's thumbnail and prepares the player to play the video. Does not automatically play the video.
     * @param videoId id of the video
     * @param startSeconds the time from which the video should start playing
     */
    void cueVideo(String videoId, float startSeconds);

    void play();
    void pause();

    /** If the player is playing a playlist, play the next video. */
    void nextVideo();
    /** If the player is playing a playlist, play the previous video. */
    void previousVideo();
    /** If the player is playing a playlist, play the video at position [index]. */
    void playVideoAt(int index);

    /** If the player is playing a playlist, enable or disable looping of the playlist. */
    void setLoop(boolean loop);

    /** If the player is playing a playlist, enable or disable shuffling of the playlist. */
    void setShuffle(boolean shuffle);

    void mute();
    void unMute();

    /**
     * @param volumePercent Integer between 0 and 100
     */
    void setVolume(int volumePercent);

    /**
     *
     * @param time The absolute time in seconds to seek to
     */
    void seekTo(float time);

    void setPlaybackRate(PlayerConstants.PlaybackRate playbackRate);

    /**
     * Tries to enter or exit fullscreen in the player.
     *
     * Might require setting the `origin` parameter to "https://www.youtube.com".
     */
    void toggleFullscreen();

    boolean addListener(YouTubePlayerListener listener);
    boolean removeListener(YouTubePlayerListener listener);
}
