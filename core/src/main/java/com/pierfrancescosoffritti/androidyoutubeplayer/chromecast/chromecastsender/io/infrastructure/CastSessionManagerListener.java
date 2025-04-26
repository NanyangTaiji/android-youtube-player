package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure;

import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;

public class CastSessionManagerListener implements SessionManagerListener<CastSession> {
    private final CastSessionListener castSessionListener;

    public CastSessionManagerListener(CastSessionListener castSessionListener) {
        this.castSessionListener = castSessionListener;
    }

    @Override
    public void onSessionEnding(CastSession castSession) {}

    @Override
    public void onSessionSuspended(CastSession castSession, int p1) {}

    @Override
    public void onSessionStarting(CastSession castSession) {
        castSessionListener.onCastSessionConnecting();
    }

    @Override
    public void onSessionResuming(CastSession castSession, String p1) {
        castSessionListener.onCastSessionConnecting();
    }

    @Override
    public void onSessionEnded(CastSession castSession, int error) {
        castSessionListener.onCastSessionDisconnected(castSession);
    }

    @Override
    public void onSessionResumed(CastSession castSession, boolean wasSuspended) {
        castSessionListener.onCastSessionConnected(castSession);
    }

    @Override
    public void onSessionResumeFailed(CastSession castSession, int p1) {
        castSessionListener.onCastSessionDisconnected(castSession);
    }

    @Override
    public void onSessionStarted(CastSession castSession, String sessionId) {
        castSessionListener.onCastSessionConnected(castSession);
    }

    @Override
    public void onSessionStartFailed(CastSession castSession, int p1) {
        castSessionListener.onCastSessionConnected(castSession);
    }
}
