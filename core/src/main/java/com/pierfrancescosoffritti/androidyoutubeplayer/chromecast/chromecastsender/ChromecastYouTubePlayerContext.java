package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender;

import com.google.android.gms.cast.framework.SessionManager;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastManager;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import java.util.HashSet;
import java.util.Set;

public class ChromecastYouTubePlayerContext implements ChromecastConnectionListener {
    private final Set<ChromecastConnectionListener> chromecastConnectionListeners = new HashSet<>();
    private final ChromecastManager chromecastManager;
    private final ChromecastYouTubePlayer chromecastYouTubePlayer;
    private boolean chromecastConnected = false;

    public ChromecastYouTubePlayerContext(
            SessionManager sessionManager,
            ChromecastConnectionListener... chromecastConnectionListeners
    ) {
        for (ChromecastConnectionListener listener : chromecastConnectionListeners) {
            this.chromecastConnectionListeners.add(listener);
        }

        this.chromecastManager = new ChromecastManager(
                this,
                sessionManager,
                this.chromecastConnectionListeners
        );
        this.chromecastYouTubePlayer = new ChromecastYouTubePlayer(
                chromecastManager.getChromecastCommunicationChannel()
        );

        onCreate();
    }

    private void onCreate() {
        chromecastManager.restoreSession();
        chromecastManager.addSessionManagerListener();
    }

    public void initialize(YouTubePlayerListener youTubePlayerListener) {
        if (!chromecastConnected) {
            throw new RuntimeException("ChromecastYouTubePlayerContext, can't initialize before Chromecast connection is established.");
        }

        chromecastYouTubePlayer.initialize(youTubePlayer -> {
            youTubePlayer.addListener(youTubePlayerListener);
        });
    }

    public void release() {
        endCurrentSession();
        chromecastManager.release();
        chromecastConnectionListeners.clear();
    }

    public void endCurrentSession() {
        chromecastManager.endCurrentSession();
    }

    @Override
    public void onChromecastConnecting() {
        // No implementation needed
    }

    @Override
    public void onChromecastConnected(ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        chromecastConnected = true;
    }

    @Override
    public void onChromecastDisconnected() {
        chromecastConnected = false;
    }

    public boolean addChromecastConnectionListener(ChromecastConnectionListener chromecastConnectionListener) {
        return chromecastConnectionListeners.add(chromecastConnectionListener);
    }

    public boolean removeChromecastConnectionListener(ChromecastConnectionListener chromecastConnectionListener) {
        return chromecastConnectionListeners.remove(chromecastConnectionListener);
    }
}
