package com.pierfrancescosoffritti.androidyoutubeplayer.core.player;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import androidx.annotation.RestrictTo;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;

import java.util.Collection;

/**
 * Bridge used for Javascript-Java communication.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class YouTubePlayerBridge {
    // these constants correspond to the values in the Javascript player
    private static final String STATE_UNSTARTED = "UNSTARTED";
    private static final String STATE_ENDED = "ENDED";
    private static final String STATE_PLAYING = "PLAYING";
    private static final String STATE_PAUSED = "PAUSED";
    private static final String STATE_BUFFERING = "BUFFERING";
    private static final String STATE_CUED = "CUED";

    private static final String QUALITY_SMALL = "small";
    private static final String QUALITY_MEDIUM = "medium";
    private static final String QUALITY_LARGE = "large";
    private static final String QUALITY_HD720 = "hd720";
    private static final String QUALITY_HD1080 = "hd1080";
    private static final String QUALITY_HIGH_RES = "highres";
    private static final String QUALITY_DEFAULT = "default";

    private static final String RATE_0_25 = "0.25";
    private static final String RATE_0_5 = "0.5";
    private static final String RATE_0_75 = "0.75";
    private static final String RATE_1 = "1";
    private static final String RATE_1_25 = "1.25";
    private static final String RATE_1_5 = "1.5";
    private static final String RATE_1_75 = "1.75";
    private static final String RATE_2 = "2";

    private static final String ERROR_INVALID_PARAMETER_IN_REQUEST = "2";
    private static final String ERROR_HTML_5_PLAYER = "5";
    private static final String ERROR_VIDEO_NOT_FOUND = "100";
    private static final String ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER1 = "101";
    private static final String ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER2 = "150";

    private final YouTubePlayerBridgeCallbacks youTubePlayerOwner;
    private final Handler mainThreadHandler;

    public interface YouTubePlayerBridgeCallbacks {
        Collection<YouTubePlayerListener> getListeners();
        YouTubePlayer getInstance();
        void onYouTubeIFrameAPIReady();
    }

    public YouTubePlayerBridge(YouTubePlayerBridgeCallbacks youTubePlayerOwner) {
        this.youTubePlayerOwner = youTubePlayerOwner;
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    @JavascriptInterface
    public void sendYouTubeIFrameAPIReady() {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                youTubePlayerOwner.onYouTubeIFrameAPIReady();
            }
        });
    }

    @JavascriptInterface
    public void sendReady() {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onReady(youTubePlayerOwner.getInstance());
                }
            }
        });
    }

    @JavascriptInterface
    public void sendStateChange(final String state) {
        final PlayerConstants.PlayerState playerState = parsePlayerState(state);

        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onStateChange(youTubePlayerOwner.getInstance(), playerState);
                }
            }
        });
    }

    @JavascriptInterface
    public void sendPlaybackQualityChange(final String quality) {
        final PlayerConstants.PlaybackQuality playbackQuality = parsePlaybackQuality(quality);

        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onPlaybackQualityChange(youTubePlayerOwner.getInstance(), playbackQuality);
                }
            }
        });
    }

    @JavascriptInterface
    public void sendPlaybackRateChange(final String rate) {
        final PlayerConstants.PlaybackRate playbackRate = parsePlaybackRate(rate);

        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onPlaybackRateChange(youTubePlayerOwner.getInstance(), playbackRate);
                }
            }
        });
    }

    @JavascriptInterface
    public void sendError(final String error) {
        final PlayerConstants.PlayerError playerError = parsePlayerError(error);

        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onError(youTubePlayerOwner.getInstance(), playerError);
                }
            }
        });
    }

    @JavascriptInterface
    public void sendApiChange() {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onApiChange(youTubePlayerOwner.getInstance());
                }
            }
        });
    }

    @JavascriptInterface
    public void sendVideoCurrentTime(String seconds) {
        float currentTimeSeconds;
        try {
            currentTimeSeconds = Float.parseFloat(seconds);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        final float finalCurrentTimeSeconds = currentTimeSeconds;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onCurrentSecond(youTubePlayerOwner.getInstance(), finalCurrentTimeSeconds);
                }
            }
        });
    }

    @JavascriptInterface
    public void sendVideoDuration(String seconds) {
        float videoDuration;
        try {
            String finalSeconds = TextUtils.isEmpty(seconds) ? "0" : seconds;
            videoDuration = Float.parseFloat(finalSeconds);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        final float finalVideoDuration = videoDuration;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onVideoDuration(youTubePlayerOwner.getInstance(), finalVideoDuration);
                }
            }
        });
    }

    @JavascriptInterface
    public void sendVideoLoadedFraction(String fraction) {
        float loadedFraction;
        try {
            loadedFraction = Float.parseFloat(fraction);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        final float finalLoadedFraction = loadedFraction;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onVideoLoadedFraction(youTubePlayerOwner.getInstance(), finalLoadedFraction);
                }
            }
        });
    }

    @JavascriptInterface
    public void sendVideoId(final String videoId) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (YouTubePlayerListener listener : youTubePlayerOwner.getListeners()) {
                    listener.onVideoId(youTubePlayerOwner.getInstance(), videoId);
                }
            }
        });
    }

    private PlayerConstants.PlayerState parsePlayerState(String state) {
        if (STATE_UNSTARTED.equalsIgnoreCase(state)) {
            return PlayerConstants.PlayerState.UNSTARTED;
        } else if (STATE_ENDED.equalsIgnoreCase(state)) {
            return PlayerConstants.PlayerState.ENDED;
        } else if (STATE_PLAYING.equalsIgnoreCase(state)) {
            return PlayerConstants.PlayerState.PLAYING;
        } else if (STATE_PAUSED.equalsIgnoreCase(state)) {
            return PlayerConstants.PlayerState.PAUSED;
        } else if (STATE_BUFFERING.equalsIgnoreCase(state)) {
            return PlayerConstants.PlayerState.BUFFERING;
        } else if (STATE_CUED.equalsIgnoreCase(state)) {
            return PlayerConstants.PlayerState.VIDEO_CUED;
        } else {
            return PlayerConstants.PlayerState.UNKNOWN;
        }
    }

    private PlayerConstants.PlaybackQuality parsePlaybackQuality(String quality) {
        if (QUALITY_SMALL.equalsIgnoreCase(quality)) {
            return PlayerConstants.PlaybackQuality.SMALL;
        } else if (QUALITY_MEDIUM.equalsIgnoreCase(quality)) {
            return PlayerConstants.PlaybackQuality.MEDIUM;
        } else if (QUALITY_LARGE.equalsIgnoreCase(quality)) {
            return PlayerConstants.PlaybackQuality.LARGE;
        } else if (QUALITY_HD720.equalsIgnoreCase(quality)) {
            return PlayerConstants.PlaybackQuality.HD720;
        } else if (QUALITY_HD1080.equalsIgnoreCase(quality)) {
            return PlayerConstants.PlaybackQuality.HD1080;
        } else if (QUALITY_HIGH_RES.equalsIgnoreCase(quality)) {
            return PlayerConstants.PlaybackQuality.HIGH_RES;
        } else if (QUALITY_DEFAULT.equalsIgnoreCase(quality)) {
            return PlayerConstants.PlaybackQuality.DEFAULT;
        } else {
            return PlayerConstants.PlaybackQuality.UNKNOWN;
        }
    }

    private PlayerConstants.PlaybackRate parsePlaybackRate(String rate) {
        if (RATE_0_25.equalsIgnoreCase(rate)) {
            return PlayerConstants.PlaybackRate.RATE_0_25;
        } else if (RATE_0_5.equalsIgnoreCase(rate)) {
            return PlayerConstants.PlaybackRate.RATE_0_5;
        } else if (RATE_0_75.equalsIgnoreCase(rate)) {
            return PlayerConstants.PlaybackRate.RATE_0_75;
        } else if (RATE_1.equalsIgnoreCase(rate)) {
            return PlayerConstants.PlaybackRate.RATE_1;
        } else if (RATE_1_25.equalsIgnoreCase(rate)) {
            return PlayerConstants.PlaybackRate.RATE_1_25;
        } else if (RATE_1_5.equalsIgnoreCase(rate)) {
            return PlayerConstants.PlaybackRate.RATE_1_5;
        } else if (RATE_1_75.equalsIgnoreCase(rate)) {
            return PlayerConstants.PlaybackRate.RATE_1_75;
        } else if (RATE_2.equalsIgnoreCase(rate)) {
            return PlayerConstants.PlaybackRate.RATE_2;
        } else {
            return PlayerConstants.PlaybackRate.UNKNOWN;
        }
    }

    private PlayerConstants.PlayerError parsePlayerError(String error) {
        if (ERROR_INVALID_PARAMETER_IN_REQUEST.equalsIgnoreCase(error)) {
            return PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST;
        } else if (ERROR_HTML_5_PLAYER.equalsIgnoreCase(error)) {
            return PlayerConstants.PlayerError.HTML_5_PLAYER;
        } else if (ERROR_VIDEO_NOT_FOUND.equalsIgnoreCase(error)) {
            return PlayerConstants.PlayerError.VIDEO_NOT_FOUND;
        } else if (ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER1.equalsIgnoreCase(error) ||
                ERROR_VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER2.equalsIgnoreCase(error)) {
            return PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER;
        } else {
            return PlayerConstants.PlayerError.UNKNOWN;
        }
    }
}
