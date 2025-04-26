package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils.JSONUtils;

public final class ChromecastCommunicationConstants {
    // receiver to sender
    public static final String INIT_COMMUNICATION_CONSTANTS = "INIT_COMMUNICATION_CONSTANTS";
    public static final String IFRAME_API_READY = "IFRAME_API_READY";
    public static final String READY = "READY";
    public static final String STATE_CHANGED = "STATE_CHANGED";
    public static final String PLAYBACK_QUALITY_CHANGED = "PLAYBACK_QUALITY_CHANGED";
    public static final String PLAYBACK_RATE_CHANGED = "PLAYBACK_RATE_CHANGED";
    public static final String ERROR = "ERROR";
    public static final String API_CHANGED = "API_CHANGED";
    public static final String VIDEO_CURRENT_TIME = "VIDEO_CURRENT_TIME";
    public static final String VIDEO_DURATION = "VIDEO_DURATION";
    public static final String VIDEO_ID = "VIDEO_ID";

    // sender to receiver
    public static final String LOAD = "LOAD";
    public static final String CUE = "CUE";
    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String SET_VOLUME = "SET_VOLUME";
    public static final String SEEK_TO = "SEEK_TO";
    public static final String MUTE = "MUTE";
    public static final String UNMUTE = "UNMUTE";
    public static final String SET_PLAYBACK_RATE = "SET_PLAYBACK_RATE";
    public static final String PLAY_NEXT_VIDEO = "PLAY_NEXT_VIDEO";
    public static final String PLAY_PREVIOUS_VIDEO = "PLAY_PREVIOUS_VIDEO";
    public static final String PLAY_VIDEO_AT = "PLAY_VIDEO_AT";
    public static final String SET_LOOP = "SET_LOOP";
    public static final String SET_SHUFFLE = "SET_SHUFFLE";

    private ChromecastCommunicationConstants() {} // Prevent instantiation

    public static String asJson() {
        return JSONUtils.buildFlatJson(
                IFRAME_API_READY, IFRAME_API_READY,
                READY, READY,
                STATE_CHANGED, STATE_CHANGED,
                PLAYBACK_QUALITY_CHANGED, PLAYBACK_QUALITY_CHANGED,
                PLAYBACK_RATE_CHANGED, PLAYBACK_RATE_CHANGED,
                ERROR, ERROR,
                API_CHANGED, API_CHANGED,
                VIDEO_CURRENT_TIME, VIDEO_CURRENT_TIME,
                VIDEO_DURATION, VIDEO_DURATION,
                VIDEO_ID, VIDEO_ID,

                LOAD, LOAD,
                CUE, CUE,
                PLAY, PLAY,
                PAUSE, PAUSE,
                SET_VOLUME, SET_VOLUME,
                SEEK_TO, SEEK_TO,
                MUTE, MUTE,
                UNMUTE, UNMUTE,
                SET_PLAYBACK_RATE, SET_PLAYBACK_RATE
        );
    }
}
