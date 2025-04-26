package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;

public interface ChromecastConnectionListener {
    void onChromecastConnecting();
    void onChromecastConnected(ChromecastYouTubePlayerContext chromecastYouTubePlayerContext);
    void onChromecastDisconnected();
}
