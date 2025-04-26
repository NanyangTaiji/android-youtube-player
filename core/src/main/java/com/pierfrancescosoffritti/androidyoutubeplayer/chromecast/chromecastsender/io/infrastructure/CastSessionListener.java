package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure;

import com.google.android.gms.cast.framework.CastSession;

public interface CastSessionListener {
    void onCastSessionConnecting();
    void onCastSessionConnected(CastSession castSession);
    void onCastSessionDisconnected(CastSession castSession);
}
