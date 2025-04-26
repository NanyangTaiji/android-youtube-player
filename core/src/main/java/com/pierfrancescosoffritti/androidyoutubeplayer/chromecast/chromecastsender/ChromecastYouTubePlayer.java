package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender;

import android.os.Build;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastCommunicationChannel;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube.ChromecastCommunicationConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube.ChromecastYouTubeMessageDispatcher;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils.JSONUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayerBridge;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ChromecastYouTubePlayer implements YouTubePlayer, YouTubePlayerBridge.YouTubePlayerBridgeCallbacks {
    private final ChromecastCommunicationChannel chromecastCommunicationChannel;
    private Consumer<YouTubePlayer> youTubePlayerInitListener;
    private final ChromecastYouTubeMessageDispatcher inputMessageDispatcher;
    private final Set<YouTubePlayerListener> youTubePlayerListeners = new HashSet<>();

    ChromecastYouTubePlayer(ChromecastCommunicationChannel chromecastCommunicationChannel) {
        this.chromecastCommunicationChannel = chromecastCommunicationChannel;
        this.inputMessageDispatcher = new ChromecastYouTubeMessageDispatcher(new YouTubePlayerBridge(this));
    }

    void initialize(Consumer<YouTubePlayer> initListener) {
        youTubePlayerListeners.clear();
        youTubePlayerInitListener = initListener;
        chromecastCommunicationChannel.addObserver(inputMessageDispatcher);
    }

    @Override
    public void onYouTubeIFrameAPIReady() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            youTubePlayerInitListener.accept(this);
        }
    }

    @Override
    public Collection<YouTubePlayerListener> getListeners() {
        // TODO: Implement this method
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public YouTubePlayer getInstance() {
        return this;
    }

    @Override
    public void loadVideo(String videoId, float startSeconds) {
        String message = JSONUtils.buildFlatJson(
                "command", ChromecastCommunicationConstants.LOAD,
                "videoId", videoId,
                "startSeconds", String.valueOf(startSeconds)
        );
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void cueVideo(String videoId, float startSeconds) {
        String message = JSONUtils.buildFlatJson(
                "command", ChromecastCommunicationConstants.CUE,
                "videoId", videoId,
                "startSeconds", String.valueOf(startSeconds)
        );
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void play() {
        String message = JSONUtils.buildFlatJson("command", ChromecastCommunicationConstants.PLAY);
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void pause() {
        String message = JSONUtils.buildFlatJson("command", ChromecastCommunicationConstants.PAUSE);
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void nextVideo() {
        String message = JSONUtils.buildFlatJson("command", ChromecastCommunicationConstants.PLAY_NEXT_VIDEO);
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void previousVideo() {
        String message = JSONUtils.buildFlatJson("command", ChromecastCommunicationConstants.PLAY_PREVIOUS_VIDEO);
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void playVideoAt(int index) {
        String message = JSONUtils.buildFlatJson(
                "command", ChromecastCommunicationConstants.PLAY_VIDEO_AT,
                "index", String.valueOf(index));
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void setLoop(boolean loop) {
        String message = JSONUtils.buildFlatJson(
                "command", ChromecastCommunicationConstants.SET_LOOP,
                "loop", String.valueOf(loop));
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void setShuffle(boolean shuffle) {
        String message = JSONUtils.buildFlatJson(
                "command", ChromecastCommunicationConstants.SET_SHUFFLE,
                "shuffle", String.valueOf(shuffle));
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void mute() {
        String message = JSONUtils.buildFlatJson("command", ChromecastCommunicationConstants.MUTE);
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void unMute() {
        String message = JSONUtils.buildFlatJson("command", ChromecastCommunicationConstants.UNMUTE);
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void setVolume(int volumePercent) {
        String message = JSONUtils.buildFlatJson(
                "command", ChromecastCommunicationConstants.SET_VOLUME,
                "volumePercent", String.valueOf(volumePercent));
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void seekTo(float time) {
        String message = JSONUtils.buildFlatJson(
                "command", ChromecastCommunicationConstants.SEEK_TO,
                "time", String.valueOf(time));
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void setPlaybackRate(PlayerConstants.PlaybackRate playbackRate) {
        String message = JSONUtils.buildFlatJson(
                "command", ChromecastCommunicationConstants.SET_PLAYBACK_RATE,
                "playbackRate", String.valueOf(playbackRate.toFloat()));
        chromecastCommunicationChannel.sendMessage(message);
    }

    @Override
    public void toggleFullscreen() {
        // No implementation needed for Chromecast
    }

    public Collection<YouTubePlayerListener> getListenersCollection() {
        return youTubePlayerListeners;
    }

    @Override
    public boolean addListener(YouTubePlayerListener listener) {
        return youTubePlayerListeners.add(listener);
    }

    @Override
    public boolean removeListener(YouTubePlayerListener listener) {
        return youTubePlayerListeners.remove(listener);
    }
}
