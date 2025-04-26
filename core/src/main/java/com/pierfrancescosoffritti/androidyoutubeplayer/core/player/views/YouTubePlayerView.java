package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.LayoutRes;
import androidx.lifecycle.*;
import com.pierfrancescosoffritti.androidyoutubeplayer.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.*;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;

import java.util.ArrayList;
import java.util.List;

public class YouTubePlayerView extends SixteenByNineFrameLayout implements LifecycleEventObserver {

    private static final String AUTO_INIT_ERROR = "YouTubePlayerView: If you want to initialize this view manually, " +
            "you need to set 'enableAutomaticInitialization' to false.";

    private static FrameLayout.LayoutParams getMatchParentLayoutParams() {
        return new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    private final List<FullscreenListener> fullscreenListeners = new ArrayList<>();

    /**
     * A single {@link FullscreenListener} that is always added to the WebView,
     * responsible for calling all optional listeners added from clients of the library.
     */
    private final FullscreenListener webViewFullscreenListener = new FullscreenListener() {

        @Override
        public void onEnterFullscreen(View fullscreenView, Runnable exitFullscreen) {
            if (fullscreenListeners.isEmpty()) {
                throw new IllegalStateException("To enter fullscreen you need to first register a FullscreenListener.");
            }
            for (FullscreenListener listener : fullscreenListeners) {
                listener.onEnterFullscreen(fullscreenView, exitFullscreen);
            }
        }

        @Override
        public void onExitFullscreen() {
            if (fullscreenListeners.isEmpty()) {
                throw new IllegalStateException("To enter fullscreen you need to first register a FullscreenListener.");
            }
            for (FullscreenListener listener : fullscreenListeners) {
                listener.onExitFullscreen();
            }
        }
    };

    private final LegacyYouTubePlayerView legacyTubePlayerView;

    // this is a publicly accessible API
    public boolean enableAutomaticInitialization;

    public YouTubePlayerView(Context context) {
        this(context, null);
    }

    public YouTubePlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YouTubePlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        legacyTubePlayerView = new LegacyYouTubePlayerView(context, webViewFullscreenListener, attrs, defStyleAttr);
        addView(legacyTubePlayerView, getMatchParentLayoutParams());

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.YouTubePlayerView, 0, 0);

        enableAutomaticInitialization = typedArray.getBoolean(R.styleable.YouTubePlayerView_enableAutomaticInitialization, true);
        boolean autoPlay = typedArray.getBoolean(R.styleable.YouTubePlayerView_autoPlay, false);
        boolean handleNetworkEvents = typedArray.getBoolean(R.styleable.YouTubePlayerView_handleNetworkEvents, true);
        String videoId = typedArray.getString(R.styleable.YouTubePlayerView_videoId);

        typedArray.recycle();

        if (autoPlay && videoId == null) {
            throw new IllegalStateException("YouTubePlayerView: videoId is not set but autoPlay is set to true. This combination is not allowed.");
        }

        YouTubePlayerListener youTubePlayerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                if (videoId != null) {
                    if (legacyTubePlayerView.isEligibleForPlayback() && autoPlay) {
                        youTubePlayer.loadVideo(videoId, 0);
                    } else {
                        youTubePlayer.cueVideo(videoId, 0);
                    }
                }

                youTubePlayer.removeListener(this);
            }
        };

        if (enableAutomaticInitialization) {
            legacyTubePlayerView.initialize(
                    youTubePlayerListener,
                    handleNetworkEvents,
                    IFramePlayerOptions.defaultOptions,
                    videoId
            );
        }
    }

    // TODO: Use @JvmOverloads instead of duplicating the method. Unfortunately that will cause a breaking change.
    public void initialize(YouTubePlayerListener youTubePlayerListener, boolean handleNetworkEvents, IFramePlayerOptions playerOptions, String videoId) {
        if (enableAutomaticInitialization) {
            throw new IllegalStateException(AUTO_INIT_ERROR);
        } else {
            legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents, playerOptions, videoId);
        }
    }

    /**
     * Initialize the player. You must call this method before using the player.
     * @param youTubePlayerListener listener for player events
     * @param handleNetworkEvents if set to true a broadcast receiver will be registered and network events will be handled automatically.
     * If set to false, you should handle network events with your own broadcast receiver.
     * @param playerOptions customizable options for the embedded video player.
     * @param //videoId optional, used to load an initial video.
     */
    public void initialize(YouTubePlayerListener youTubePlayerListener, boolean handleNetworkEvents, IFramePlayerOptions playerOptions) {
        if (enableAutomaticInitialization) {
            throw new IllegalStateException(AUTO_INIT_ERROR);
        } else {
            legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents, playerOptions, null);
        }
    }

    /**
     * Initialize the player.
     * @param handleNetworkEvents if set to true a broadcast receiver will be registered and network events will be handled automatically.
     * If set to false, you should handle network events with your own broadcast receiver.
     *
     * @see YouTubePlayerView#initialize
     */
    public void initialize(YouTubePlayerListener youTubePlayerListener, boolean handleNetworkEvents) {
        if (enableAutomaticInitialization) {
            throw new IllegalStateException(AUTO_INIT_ERROR);
        } else {
            legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents, IFramePlayerOptions.defaultOptions);
        }
    }

    /**
     * Initialize the player with player options.
     *
     * @see YouTubePlayerView#initialize
     */
    public void initialize(YouTubePlayerListener youTubePlayerListener, IFramePlayerOptions playerOptions) {
        if (enableAutomaticInitialization) {
            throw new IllegalStateException(AUTO_INIT_ERROR);
        } else {
            legacyTubePlayerView.initialize(youTubePlayerListener, true, playerOptions);
        }
    }

    /**
     * Initialize the player. Network events are automatically handled by the player.
     * @param youTubePlayerListener listener for player events
     *
     * @see YouTubePlayerView#initialize
     */
    public void initialize(YouTubePlayerListener youTubePlayerListener) {
        if (enableAutomaticInitialization) {
            throw new IllegalStateException(AUTO_INIT_ERROR);
        } else {
            legacyTubePlayerView.initialize(youTubePlayerListener, true);
        }
    }



    public void getYouTubePlayerWhenReady(YouTubePlayerCallback youTubePlayerCallback) {
        legacyTubePlayerView.getYouTubePlayerWhenReady(youTubePlayerCallback);
    }

    public View inflateCustomPlayerUi(@LayoutRes int layoutId) {
        return legacyTubePlayerView.inflateCustomPlayerUi(layoutId);
    }

    public void setCustomPlayerUi(View view) {
        legacyTubePlayerView.setCustomPlayerUi(view);
    }

    public void enableBackgroundPlayback(boolean enable) {
        legacyTubePlayerView.enableBackgroundPlayback(enable);
    }

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        switch (event) {
            case ON_RESUME:
                onResume();
                break;
            case ON_STOP:
                onStop();
                break;
            case ON_DESTROY:
                release();
                break;
            case ON_CREATE:
            case ON_START:
            case ON_PAUSE:
            case ON_ANY:
                break;
        }
    }

    public void release() {
        legacyTubePlayerView.release();
    }

    private void onResume() {
        legacyTubePlayerView.onResume();
    }

    private void onStop() {
        legacyTubePlayerView.onStop();
    }

    public void addYouTubePlayerListener(YouTubePlayerListener youTubePlayerListener) {
        legacyTubePlayerView.webViewYouTubePlayer.addListener(youTubePlayerListener);
    }

    public void removeYouTubePlayerListener(YouTubePlayerListener youTubePlayerListener) {
        legacyTubePlayerView.webViewYouTubePlayer.removeListener(youTubePlayerListener);
    }

    public void addFullscreenListener(FullscreenListener fullscreenListener) {
        fullscreenListeners.add(fullscreenListener);
    }

    public void removeFullscreenListener(FullscreenListener fullscreenListener) {
        fullscreenListeners.remove(fullscreenListener);
    }

    public void matchParent() {
        setLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    public void wrapContent() {
        setLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private void setLayoutParams(int targetWidth, int targetHeight) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = targetWidth;
        layoutParams.height = targetHeight;
        setLayoutParams(layoutParams);
    }
}