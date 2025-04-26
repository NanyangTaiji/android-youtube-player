package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.ui;

import android.view.View;
import android.widget.Button;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.utils.VideoIdsProvider;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.cyplayersample.R;

/**
 * Class used to manage the two YouTubePlayers, local and cast.
 *
 * The local YouTubePlayer is supposed to stop playing when the cast player stars and vice versa.
 *
 * When one of the two players stops, the other has to resume the playback from where the previous player stopped.
 */
public class YouTubePlayersManager implements ChromecastConnectionListener {

    private final YouTubePlayerView youtubePlayerView;
    private final YouTubePlayerListener chromecastPlayerListener;
    private final Button nextVideoButton;

    public final SimpleChromeCastUiController chromecastUiController;

    private YouTubePlayer localYouTubePlayer;
    private YouTubePlayer chromecastYouTubePlayer;

    private final YouTubePlayerTracker chromecastPlayerStateTracker = new YouTubePlayerTracker();
    private final YouTubePlayerTracker localPlayerStateTracker = new YouTubePlayerTracker();

    private boolean playingOnCastPlayer = false;

    public YouTubePlayersManager(
            LocalYouTubePlayerInitListener localYouTubePlayerInitListener,
            YouTubePlayerView youtubePlayerView,
            View chromecastControls,
            YouTubePlayerListener chromecastPlayerListener) {

        this.youtubePlayerView = youtubePlayerView;
        this.chromecastPlayerListener = chromecastPlayerListener;

        this.nextVideoButton = chromecastControls.findViewById(R.id.next_video_button);
        this.chromecastUiController = new SimpleChromeCastUiController(chromecastControls);

        initLocalYouTube(localYouTubePlayerInitListener);
        nextVideoButton.setOnClickListener(v -> {
            if (chromecastYouTubePlayer != null) {
                chromecastYouTubePlayer.loadVideo(VideoIdsProvider.getNextVideoId(), 0f);
            }
        });
    }

    @Override
    public void onChromecastConnecting() {
        if (localYouTubePlayer != null) {
            localYouTubePlayer.pause();
        }
    }

    @Override
    public void onChromecastConnected(ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        initializeCastPlayer(chromecastYouTubePlayerContext);
        playingOnCastPlayer = true;
    }

    @Override
    public void onChromecastDisconnected() {
        if (localYouTubePlayer != null) {
            if (chromecastPlayerStateTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                localYouTubePlayer.loadVideo(
                        chromecastPlayerStateTracker.getVideoId(),
                        chromecastPlayerStateTracker.getCurrentSecond()
                );
            } else {
                localYouTubePlayer.cueVideo(
                        chromecastPlayerStateTracker.getVideoId(),
                        chromecastPlayerStateTracker.getCurrentSecond()
                );
            }
        }

        chromecastUiController.resetUi();
        playingOnCastPlayer = false;
    }

    public void togglePlayback() {
        if (playingOnCastPlayer) {
            if (chromecastPlayerStateTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                if (chromecastYouTubePlayer != null) {
                    chromecastYouTubePlayer.pause();
                }
            } else {
                if (chromecastYouTubePlayer != null) {
                    chromecastYouTubePlayer.play();
                }
            }
        } else {
            if (localPlayerStateTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                if (localYouTubePlayer != null) {
                    localYouTubePlayer.pause();
                }
            } else {
                if (localYouTubePlayer != null) {
                    localYouTubePlayer.play();
                }
            }
        }
    }

    private void initLocalYouTube(LocalYouTubePlayerInitListener localYouTubePlayerInitListener) {
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                localYouTubePlayer = youTubePlayer;
                youTubePlayer.addListener(localPlayerStateTracker);

                if (!playingOnCastPlayer) {
                    youTubePlayer.loadVideo(
                            VideoIdsProvider.getNextVideoId(),
                            chromecastPlayerStateTracker.getCurrentSecond()
                    );
                }

                localYouTubePlayerInitListener.onLocalYouTubePlayerInit();
            }

            @Override
            public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
                if (playingOnCastPlayer && localPlayerStateTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                    youTubePlayer.pause();
                }
            }
        });
    }

    private void initializeCastPlayer(ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        chromecastYouTubePlayerContext.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                chromecastYouTubePlayer = youTubePlayer;
                chromecastUiController.youTubePlayer = youTubePlayer;

                youTubePlayer.addListener(chromecastPlayerListener);
                youTubePlayer.addListener(chromecastPlayerStateTracker);
                youTubePlayer.addListener(chromecastUiController);

                youTubePlayer.loadVideo(
                        localPlayerStateTracker.getVideoId(),
                        localPlayerStateTracker.getCurrentSecond()
                );
            }
        });
    }

    /**
     * Interface used to notify its listeners than the local YouTubePlayer is ready to play videos.
     */
    public interface LocalYouTubePlayerInitListener {
        void onLocalYouTubePlayerInit();
    }
}
