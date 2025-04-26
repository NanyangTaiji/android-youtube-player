package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.lifecycle.*;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.NetworkObserver;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.PlaybackResumer;

import java.util.HashSet;
import java.util.Set;

/**
 * Legacy internal implementation of YouTubePlayerView. The user facing YouTubePlayerView delegates
 * most of its actions to this one.
 */
class LegacyYouTubePlayerView extends SixteenByNineFrameLayout {

    final WebViewYouTubePlayer webViewYouTubePlayer;

    private final NetworkObserver networkObserver;
    private final PlaybackResumer playbackResumer;

    boolean isYouTubePlayerReady = false;
    private Runnable initialize;
    private final Set<YouTubePlayerCallback> youTubePlayerCallbacks = new HashSet<>();

    private boolean canPlay = true;

    public LegacyYouTubePlayerView(Context context) {
        this(context, FakeWebViewYouTubeListener.getInstance(), null, 0);
    }

    public LegacyYouTubePlayerView(Context context, FullscreenListener listener, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        webViewYouTubePlayer = new WebViewYouTubePlayer(context, listener);
        networkObserver = new NetworkObserver(context.getApplicationContext());
        playbackResumer = new PlaybackResumer();

        addView(
                webViewYouTubePlayer,
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
        webViewYouTubePlayer.addListener(playbackResumer);

        // stop playing if the user loads a video but then leaves the app before the video starts playing.
        webViewYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                if (state == PlayerConstants.PlayerState.PLAYING && !isEligibleForPlayback()) {
                    youTubePlayer.pause();
                }
            }
        });

        webViewYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                isYouTubePlayerReady = true;

                for (YouTubePlayerCallback callback : youTubePlayerCallbacks) {
                    callback.onYouTubePlayer(youTubePlayer);
                }
                youTubePlayerCallbacks.clear();

                youTubePlayer.removeListener(this);
            }
        });

        networkObserver.listeners.add(new NetworkObserver.Listener() {
            @Override
            public void onNetworkAvailable() {
                if (!isYouTubePlayerReady) {
                    initialize.run();
                } else {
                    playbackResumer.resume(webViewYouTubePlayer.getInstance());
                }
            }

            @Override
            public void onNetworkUnavailable() {
            }
        });
    }

    /**
     * Initialize the player. You must call this method before using the player.
     *
     * @param youTubePlayerListener listener for player events
     * @param handleNetworkEvents   if set to true a broadcast receiver will be registered and network events will be handled automatically.
     *                              If set to false, you should handle network events with your own broadcast receiver.
     * @param playerOptions         customizable options for the embedded video player, can be null.
     * @param videoId               optional, used to load a video right after initialization.
     */
    public void initialize(
            final YouTubePlayerListener youTubePlayerListener,
            boolean handleNetworkEvents,
            final IFramePlayerOptions playerOptions,
            final String videoId
    ) {
        if (isYouTubePlayerReady) {
            throw new IllegalStateException("This YouTubePlayerView has already been initialized.");
        }

        if (handleNetworkEvents) {
            networkObserver.observeNetwork();
        }

        initialize = new Runnable() {
            @Override
            public void run() {
                webViewYouTubePlayer.initialize(new YouTubePlayerCallback() {
                    @Override
                    public void onYouTubePlayer(YouTubePlayer youTubePlayer) {
                        youTubePlayer.addListener(youTubePlayerListener);
                    }
                }, playerOptions, videoId);
            }
        };

        if (!handleNetworkEvents) {
            initialize.run();
        }
    }

    /**
     * Initialize the player.
     *
     * @param playerOptions customizable options for the embedded video player.
     * @see LegacyYouTubePlayerView#initialize
     */
    public void initialize(YouTubePlayerListener youTubePlayerListener, boolean handleNetworkEvents, IFramePlayerOptions playerOptions) {
        initialize(youTubePlayerListener, handleNetworkEvents, playerOptions, null);
    }

    /**
     * Initialize the player.
     *
     * @param handleNetworkEvents if set to true a broadcast receiver will be registered and network events will be handled automatically.
     *                            If set to false, you should handle network events with your own broadcast receiver.
     * @see LegacyYouTubePlayerView#initialize
     */
    public void initialize(YouTubePlayerListener youTubePlayerListener, boolean handleNetworkEvents) {
        initialize(youTubePlayerListener, handleNetworkEvents, IFramePlayerOptions.defaultOptions);
    }

    /**
     * Initialize the player. Network events are automatically handled by the player.
     *
     * @param youTubePlayerListener listener for player events
     * @see LegacyYouTubePlayerView#initialize
     */
    public void initialize(YouTubePlayerListener youTubePlayerListener) {
        initialize(youTubePlayerListener, true);
    }

    /**
     * @param youTubePlayerCallback A callback that will be called when the YouTubePlayer is ready.
     *                              If the player is ready when the function is called, the callback is called immediately.
     *                              This function is called only once.
     */
    public void getYouTubePlayerWhenReady(YouTubePlayerCallback youTubePlayerCallback) {
        if (isYouTubePlayerReady) {
            youTubePlayerCallback.onYouTubePlayer(webViewYouTubePlayer.getInstance());
        } else {
            youTubePlayerCallbacks.add(youTubePlayerCallback);
        }
    }

    /**
     * Use this method to replace the default Ui of the player with a custom Ui.
     * <p>
     * You will be responsible to manage the custom Ui from your application,
     * the default controller obtained through {@link LegacyYouTubePlayerView#//getPlayerUiController} won't be available anymore.
     *
     * @param layoutId the ID of the layout defining the custom Ui.
     * @return The inflated View
     */
    public View inflateCustomPlayerUi(@LayoutRes int layoutId) {
        removeViews(1, getChildCount() - 1);
        return View.inflate(getContext(), layoutId, this);
    }

    public void setCustomPlayerUi(View view) {
        removeViews(1, getChildCount() - 1);
        addView(view);
    }

    /**
     * Call this method before destroying the host Fragment/Activity, or register this View as an observer of its host lifecycle
     */
    public void release() {
        networkObserver.destroy();
        removeView(webViewYouTubePlayer);
        webViewYouTubePlayer.removeAllViews();
        webViewYouTubePlayer.destroy();
    }

    void onResume() {
        playbackResumer.onLifecycleResume();
        canPlay = true;
    }

    void onStop() {
        webViewYouTubePlayer.getInstance().pause();
        playbackResumer.onLifecycleStop();
        canPlay = false;
    }

    /**
     * Checks whether the player is in an eligible state for playback in
     * respect of the {@link WebViewYouTubePlayer#isBackgroundPlaybackEnabled}
     * property.
     */
    boolean isEligibleForPlayback() {
        return canPlay || webViewYouTubePlayer.isBackgroundPlaybackEnabled;
    }

    /**
     * Don't use this method if you want to publish your app on the PlayStore. Background playback is against YouTube terms of service.
     */
    public void enableBackgroundPlayback(boolean enable) {
        webViewYouTubePlayer.isBackgroundPlaybackEnabled = enable;
    }
}
