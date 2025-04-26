package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.annotation.VisibleForTesting;
import com.pierfrancescosoffritti.androidyoutubeplayer.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayerBridge;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

class YouTubePlayerImpl implements YouTubePlayer {
    private final WebView webView;
    private final Handler mainThread;
    final Set<YouTubePlayerListener> listeners = new HashSet<>();

    YouTubePlayerImpl(WebView webView) {
        this.webView = webView;
        this.mainThread = new Handler(Looper.getMainLooper());
    }

    @Override
    public void loadVideo(String videoId, float startSeconds) {
        invoke("loadVideo", videoId, startSeconds);
    }

    @Override
    public void cueVideo(String videoId, float startSeconds) {
        invoke("cueVideo", videoId, startSeconds);
    }

    @Override
    public void play() {
        invoke("playVideo");
    }

    @Override
    public void pause() {
        invoke("pauseVideo");
    }

    @Override
    public void nextVideo() {
        invoke("nextVideo");
    }

    @Override
    public void previousVideo() {
        invoke("previousVideo");
    }

    @Override
    public void playVideoAt(int index) {
        invoke("playVideoAt", index);
    }

    @Override
    public void setLoop(boolean loop) {
        invoke("setLoop", loop);
    }

    @Override
    public void setShuffle(boolean shuffle) {
        invoke("setShuffle", shuffle);
    }

    @Override
    public void mute() {
        invoke("mute");
    }

    @Override
    public void unMute() {
        invoke("unMute");
    }

    @Override
    public void setVolume(int volumePercent) {
        if (volumePercent < 0 || volumePercent > 100) {
            throw new IllegalArgumentException("Volume must be between 0 and 100");
        }
        invoke("setVolume", volumePercent);
    }

    @Override
    public void seekTo(float time) {
        invoke("seekTo", time);
    }

    @Override
    public void setPlaybackRate(PlayerConstants.PlaybackRate playbackRate) {
        invoke("setPlaybackRate", playbackRate.toFloat());
    }

    @Override
    public void toggleFullscreen() {
        invoke("toggleFullscreen");
    }

    @Override
    public boolean addListener(YouTubePlayerListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(YouTubePlayerListener listener) {
        return listeners.remove(listener);
    }

    void release() {
        listeners.clear();
        mainThread.removeCallbacksAndMessages(null);
    }

    private void invoke(String function, Object... args) {
        StringBuilder stringArgs = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                stringArgs.append(",");
            }

            if (args[i] instanceof String) {
                stringArgs.append("'").append(args[i]).append("'");
            } else {
                stringArgs.append(args[i].toString());
            }
        }

        final String jsCode = "javascript:" + function + "(" + stringArgs.toString() + ")";
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(jsCode);
            }
        });
    }
}

class FakeWebViewYouTubeListener implements FullscreenListener {
    private static final FakeWebViewYouTubeListener INSTANCE = new FakeWebViewYouTubeListener();

    static FakeWebViewYouTubeListener getInstance() {
        return INSTANCE;
    }

    private FakeWebViewYouTubeListener() {}

    @Override
    public void onEnterFullscreen(View fullscreenView, Runnable exitFullscreen) {

    }

    @Override
    public void onExitFullscreen() {}
}

/**
 * WebView implementation of {@link YouTubePlayer}. The player runs inside the WebView, using the IFrame Player API.
 */
class WebViewYouTubePlayer extends WebView implements YouTubePlayerBridge.YouTubePlayerBridgeCallbacks {

    private final FullscreenListener listener;
    private final YouTubePlayerImpl youTubePlayer;
    private YouTubePlayerCallback youTubePlayerInitListener;

    boolean isBackgroundPlaybackEnabled = false;

    /** Constructor used by tools */
    public WebViewYouTubePlayer(Context context) {
        this(context, FakeWebViewYouTubeListener.getInstance());
    }

    public WebViewYouTubePlayer(Context context, FullscreenListener listener) {
        this(context, listener, null, 0);
    }

    public WebViewYouTubePlayer(Context context, FullscreenListener listener, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.listener = listener;
        this.youTubePlayer = new YouTubePlayerImpl(this);
    }

    void initialize(YouTubePlayerCallback initListener, IFramePlayerOptions playerOptions, String videoId) {
        youTubePlayerInitListener = initListener;
        initWebView(playerOptions != null ? playerOptions : IFramePlayerOptions.defaultOptions, videoId);
    }

    @Override
    public Collection<YouTubePlayerListener> getListeners() {
        return new HashSet<>(youTubePlayer.listeners);
    }

    @Override
    public YouTubePlayer getInstance() {
        return youTubePlayer;
    }

    @Override
    public void onYouTubeIFrameAPIReady() {
        youTubePlayerInitListener.onYouTubePlayer(youTubePlayer);
    }

    public boolean addListener(YouTubePlayerListener listener) {
        return youTubePlayer.listeners.add(listener);
    }

    public boolean removeListener(YouTubePlayerListener listener) {
        return youTubePlayer.listeners.remove(listener);
    }

    @Override
    public void destroy() {
        youTubePlayer.release();
        super.destroy();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(IFramePlayerOptions playerOptions, String videoId) {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        addJavascriptInterface(new YouTubePlayerBridge(this), "YouTubePlayerBridge");

        String videoIdParam = videoId != null ? "'" + videoId + "'" : "undefined";

        String htmlPage = WebViewUtils.readHTMLFromUTF8File(getResources().openRawResource(R.raw.ayp_youtube_player))
                .replace("<<injectedVideoId>>", videoIdParam)
                .replace("<<injectedPlayerVars>>", playerOptions.toString());

        loadDataWithBaseURL(playerOptions.getOrigin(), htmlPage, "text/html", "utf-8", null);

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                listener.onEnterFullscreen(view, new Runnable() {
                    @Override
                    public void run() {
                        callback.onCustomViewHidden();
                    }
                });
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                listener.onExitFullscreen();
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                Bitmap result = super.getDefaultVideoPoster();
                // if the video's thumbnail is not in memory, show a black screen
                return result != null ? result : Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
            }
        });
    }

    @Override
    public void onWindowVisibilityChanged(int visibility) {
        if (isBackgroundPlaybackEnabled && (visibility == View.GONE || visibility == View.INVISIBLE)) {
            return;
        }

        super.onWindowVisibilityChanged(visibility);
    }
}

class WebViewUtils {
    static String readHTMLFromUTF8File(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            boolean isFirstLine = true;

            while ((line = bufferedReader.readLine()) != null) {
                if (!isFirstLine) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(line);
                isFirstLine = false;
            }

            inputStream.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Can't parse HTML file.");
        }
    }
}
