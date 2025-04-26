package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure;

import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube.ChromecastCommunicationConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube.ChromecastYouTubeIOChannel;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils.JSONUtils;

import java.io.IOException;
import java.util.Set;

public class ChromecastManager implements CastSessionListener {
    private final ChromecastYouTubePlayerContext chromecastYouTubePlayerContext;
    private final SessionManager sessionManager;
    private final Set<ChromecastConnectionListener> chromecastConnectionListeners;
    private final ChromecastYouTubeIOChannel chromecastCommunicationChannel;
    private final CastSessionManagerListener castSessionManagerListener;

    public ChromecastManager(
            ChromecastYouTubePlayerContext chromecastYouTubePlayerContext,
            SessionManager sessionManager,
            Set<ChromecastConnectionListener> chromecastConnectionListeners
    ) {
        this.chromecastYouTubePlayerContext = chromecastYouTubePlayerContext;
        this.sessionManager = sessionManager;
        this.chromecastConnectionListeners = chromecastConnectionListeners;
        this.chromecastCommunicationChannel = new ChromecastYouTubeIOChannel(sessionManager);
        this.castSessionManagerListener = new CastSessionManagerListener(this);
    }

    public ChromecastYouTubeIOChannel getChromecastCommunicationChannel() {
        return chromecastCommunicationChannel;
    }

    @Override
    public void onCastSessionConnecting() {
        for (ChromecastConnectionListener listener : chromecastConnectionListeners) {
            listener.onChromecastConnecting();
        }
    }

    @Override
    public void onCastSessionConnected(CastSession castSession) {
        try {
            castSession.removeMessageReceivedCallbacks(chromecastCommunicationChannel.getNamespace());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            castSession.setMessageReceivedCallbacks(
                    chromecastCommunicationChannel.getNamespace(),
                    chromecastCommunicationChannel
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sendCommunicationConstants(chromecastCommunicationChannel);

        chromecastYouTubePlayerContext.onChromecastConnected(chromecastYouTubePlayerContext);
        for (ChromecastConnectionListener listener : chromecastConnectionListeners) {
            listener.onChromecastConnected(chromecastYouTubePlayerContext);
        }
    }

    @Override
    public void onCastSessionDisconnected(CastSession castSession) {
        try {
            castSession.removeMessageReceivedCallbacks(chromecastCommunicationChannel.getNamespace());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        chromecastYouTubePlayerContext.onChromecastDisconnected();
        for (ChromecastConnectionListener listener : chromecastConnectionListeners) {
            listener.onChromecastDisconnected();
        }
    }

    public void restoreSession() {
        CastSession currentCastSessions = sessionManager.getCurrentCastSession();
        if (currentCastSessions != null) {
            onCastSessionConnected(currentCastSessions);
        }
    }

    public void endCurrentSession() {
        sessionManager.endCurrentSession(true);
    }

    public void addSessionManagerListener() {
        sessionManager.addSessionManagerListener(castSessionManagerListener, CastSession.class);
    }

    public void removeSessionManagerListener() {
        sessionManager.removeSessionManagerListener(castSessionManagerListener, CastSession.class);
    }

    public void release() {
        removeSessionManagerListener();
    }

    private void sendCommunicationConstants(ChromecastCommunicationChannel chromecastCommunicationChannel) {
        String communicationConstants = ChromecastCommunicationConstants.asJson();

        String message = JSONUtils.buildCommunicationConstantsJson(
                "command", ChromecastCommunicationConstants.INIT_COMMUNICATION_CONSTANTS,
                "communicationConstants", communicationConstants
        );

        chromecastCommunicationChannel.sendMessage(message);
    }
}
