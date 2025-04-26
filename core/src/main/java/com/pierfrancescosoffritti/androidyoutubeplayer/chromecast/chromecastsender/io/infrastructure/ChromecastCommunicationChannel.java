package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import java.util.HashSet;

public interface ChromecastCommunicationChannel extends Cast.MessageReceivedCallback {
    String getNamespace();
    HashSet<ChromecastChannelObserver> getObservers();

    void sendMessage(String message);
    @Override
    void onMessageReceived(CastDevice castDevice, String namespace, String message);

    default void addObserver(ChromecastChannelObserver channelObserver) {
        getObservers().add(channelObserver);
    }

    default void removeObserver(ChromecastChannelObserver channelObserver) {
        getObservers().remove(channelObserver);
    }

    interface ChromecastChannelObserver {
        void onMessageReceived(MessageFromReceiver messageFromReceiver);
    }
}
