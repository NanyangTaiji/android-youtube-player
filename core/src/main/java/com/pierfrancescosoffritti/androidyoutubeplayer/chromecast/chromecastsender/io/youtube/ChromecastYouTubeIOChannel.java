package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.SessionManager;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastCommunicationChannel;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.MessageFromReceiver;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils.JSONUtils;
import java.util.HashSet;
import java.util.Set;

public class ChromecastYouTubeIOChannel implements ChromecastCommunicationChannel {
    private final SessionManager sessionManager;
    private final Set<ChromecastCommunicationChannel.ChromecastChannelObserver> observers = new HashSet<>();

    public ChromecastYouTubeIOChannel(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String getNamespace() {
        return "urn:x-cast:com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.communication";
    }

    @Override
    public HashSet<ChromecastChannelObserver> getObservers() {
        return (HashSet<ChromecastChannelObserver>) observers;
    }

    @Override
    public void sendMessage(String message) {
        try {
            if (sessionManager.getCurrentCastSession() != null) {
                sessionManager.getCurrentCastSession().sendMessage(getNamespace(), message);
                // Optional result callback could be added here
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        MessageFromReceiver parsedMessage = JSONUtils.parseMessageFromReceiverJson(message);
        for (ChromecastCommunicationChannel.ChromecastChannelObserver observer : observers) {
            observer.onMessageReceived(parsedMessage);
        }
    }
}
