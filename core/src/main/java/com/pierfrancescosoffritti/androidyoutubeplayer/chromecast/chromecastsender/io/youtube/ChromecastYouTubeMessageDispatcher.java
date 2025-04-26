package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastCommunicationChannel;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.MessageFromReceiver;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayerBridge;

public class ChromecastYouTubeMessageDispatcher implements ChromecastCommunicationChannel.ChromecastChannelObserver {
    private final YouTubePlayerBridge bridge;

    public ChromecastYouTubeMessageDispatcher(YouTubePlayerBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void onMessageReceived(MessageFromReceiver messageFromReceiver) {
        switch (messageFromReceiver.getType()) {
            case ChromecastCommunicationConstants.IFRAME_API_READY:
                bridge.sendYouTubeIFrameAPIReady();
                break;
            case ChromecastCommunicationConstants.READY:
                bridge.sendReady();
                break;
            case ChromecastCommunicationConstants.STATE_CHANGED:
                bridge.sendStateChange(messageFromReceiver.getData());
                break;
            case ChromecastCommunicationConstants.PLAYBACK_QUALITY_CHANGED:
                bridge.sendPlaybackQualityChange(messageFromReceiver.getData());
                break;
            case ChromecastCommunicationConstants.PLAYBACK_RATE_CHANGED:
                bridge.sendPlaybackRateChange(messageFromReceiver.getData());
                break;
            case ChromecastCommunicationConstants.ERROR:
                bridge.sendError(messageFromReceiver.getData());
                break;
            case ChromecastCommunicationConstants.API_CHANGED:
                bridge.sendApiChange();
                break;
            case ChromecastCommunicationConstants.VIDEO_CURRENT_TIME:
                bridge.sendVideoCurrentTime(messageFromReceiver.getData());
                break;
            case ChromecastCommunicationConstants.VIDEO_DURATION:
                bridge.sendVideoDuration(messageFromReceiver.getData());
                break;
            case ChromecastCommunicationConstants.VIDEO_ID:
                bridge.sendVideoId(messageFromReceiver.getData());
                break;
        }
    }
}
