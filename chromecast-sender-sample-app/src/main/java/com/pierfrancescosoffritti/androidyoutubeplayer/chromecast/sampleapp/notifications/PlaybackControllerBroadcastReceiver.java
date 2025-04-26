package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;

/**
 * This broadcast receiver is used to react to notification actions.
 */
public class PlaybackControllerBroadcastReceiver extends BroadcastReceiver implements ChromecastConnectionListener {

    public static final String TOGGLE_PLAYBACK =
            "com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.TOGGLE_PLAYBACK";
    public static final String STOP_CAST_SESSION =
            "com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.STOP_CAST_SESSION";

    private ChromecastYouTubePlayerContext chromecastYouTubePlayerContext;
    private Runnable togglePlayback;

    public PlaybackControllerBroadcastReceiver() {
        this.togglePlayback = () -> Log.d(
                PlaybackControllerBroadcastReceiver.class.getSimpleName(),
                "no-op"
        );
    }

    public PlaybackControllerBroadcastReceiver(Runnable togglePlayback) {
        this.togglePlayback = togglePlayback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "intent received " + intent.getAction());
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case TOGGLE_PLAYBACK:
                    togglePlayback.run();
                    break;
                case STOP_CAST_SESSION:
                    chromecastYouTubePlayerContext.endCurrentSession();
                    break;
            }
        }
    }

    @Override
    public void onChromecastConnected(ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        this.chromecastYouTubePlayerContext = chromecastYouTubePlayerContext;
    }

    @Override
    public void onChromecastConnecting() {
        // Not implemented in original code
    }

    @Override
    public void onChromecastDisconnected() {
        // Not implemented in original code
    }

    public void setTogglePlayback(Runnable togglePlayback) {
        this.togglePlayback = togglePlayback;
    }
}
