package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.app.MediaRouteButton;

import com.google.android.gms.cast.framework.CastContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.ChromecastYouTubePlayerContext;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastConnectionListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils.PlayServicesUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.notifications.NotificationManager;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.notifications.PlaybackControllerBroadcastReceiver;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.utils.MediaRouteButtonUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.cyplayersample.R;

public class LocalPlayerInitExampleActivity extends AppCompatActivity
        implements YouTubePlayersManager.LocalYouTubePlayerInitListener, ChromecastConnectionListener {

    private static final int GOOGLE_PLAY_SERVICES_REQUEST_CODE = 1;

    private YouTubePlayersManager youTubePlayersManager;
    private MediaRouteButton mediaRouteButton;

    private NotificationManager notificationManager;
    private PlaybackControllerBroadcastReceiver playbackControllerBroadcastReceiver;

    private boolean connectedToChromecast = false;

    private YouTubePlayerView youtubePlayerView;
    private ViewGroup chromecastControlsRoot;
    private ViewGroup mediaRouteButtonRoot;

    private final MediaRouteButtonContainer chromecastPlayerUiMediaRouteButtonContainer = new MediaRouteButtonContainer() {
        @Override
        public void addMediaRouteButton(MediaRouteButton mediaRouteButton) {
            youTubePlayersManager.chromecastUiController.addView(mediaRouteButton);
        }

        @Override
        public void removeMediaRouteButton(MediaRouteButton mediaRouteButton) {
            youTubePlayersManager.chromecastUiController.removeView(mediaRouteButton);
        }
    };

    private final MediaRouteButtonContainer localPlayerUiMediaRouteButtonContainer = new MediaRouteButtonContainer() {
        @Override
        public void addMediaRouteButton(MediaRouteButton mediaRouteButton) {
            mediaRouteButtonRoot.addView(mediaRouteButton);
        }

        @Override
        public void removeMediaRouteButton(MediaRouteButton mediaRouteButton) {
            mediaRouteButtonRoot.removeView(mediaRouteButton);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_and_cast_player_example);

        youtubePlayerView = findViewById(R.id.youtube_player_view);


        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1) // enable full screen button
                .build();

        // we need to initialize manually in order to pass IFramePlayerOptions to the player
      /*  youtubePlayerView.setEnableAutomaticInitialization(false);

        youtubePlayerView.addFullscreenListener(new FullscreenListener() {

            @Override
            public void onEnterFullscreen(View fullscreenView, ExitFullscreenCallback exitFullscreen) {
                isFullscreen = true;

                // the video will continue playing in fullscreenView
                youtubePlayerView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);
            }

            @Override
            public void onExitFullscreen() {
                isFullscreen = false;

                // the video will continue playing in the player
                youtubePlayerView.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
            }
        });*/

        chromecastControlsRoot = findViewById(R.id.chromecast_controls_root);
        mediaRouteButtonRoot = findViewById(R.id.media_route_button_root);

        getLifecycle().addObserver(youtubePlayerView);

        notificationManager = new NotificationManager(this, LocalPlayerInitExampleActivity.class);

        youTubePlayersManager = new YouTubePlayersManager(
                this,
                youtubePlayerView,
                chromecastControlsRoot,
                notificationManager
        );

        mediaRouteButton = MediaRouteButtonUtils.initMediaRouteButton(this);

        registerBroadcastReceiver();

        // can't use CastContext until I'm sure the user has GooglePlayServices
        PlayServicesUtils.checkGooglePlayServicesAvailability(
                this,
                GOOGLE_PLAY_SERVICES_REQUEST_CODE,
                () -> initChromecast()
        );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(playbackControllerBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // can't use CastContext until I'm sure the user has GooglePlayServices
        if (requestCode == GOOGLE_PLAY_SERVICES_REQUEST_CODE) {
            PlayServicesUtils.checkGooglePlayServicesAvailability(
                    this,
                    GOOGLE_PLAY_SERVICES_REQUEST_CODE,
                    () -> initChromecast()
            );
        }
    }

    private void initChromecast() {
        new ChromecastYouTubePlayerContext(
                CastContext.getSharedInstance(this).getSessionManager(),
                this,
                playbackControllerBroadcastReceiver,
                youTubePlayersManager
        );
    }

    @Override
    public void onChromecastConnecting() {
        // Not implemented in original code
    }

    @Override
    public void onChromecastConnected(ChromecastYouTubePlayerContext chromecastYouTubePlayerContext) {
        connectedToChromecast = true;
        updateUi(true);
        notificationManager.showNotification();
    }

    @Override
    public void onChromecastDisconnected() {
        connectedToChromecast = false;
        updateUi(false);
        notificationManager.dismissNotification();
    }

    @Override
    public void onLocalYouTubePlayerInit() {
        if (connectedToChromecast) {
            return;
        }

        MediaRouteButtonUtils.addMediaRouteButtonToPlayerUi(
                mediaRouteButton,
                android.R.color.black,
                null,
                localPlayerUiMediaRouteButtonContainer
        );
    }

    private void registerBroadcastReceiver() {
        playbackControllerBroadcastReceiver = new PlaybackControllerBroadcastReceiver(
                () -> youTubePlayersManager.togglePlayback()
        );

        IntentFilter filter = new IntentFilter(PlaybackControllerBroadcastReceiver.TOGGLE_PLAYBACK);
        filter.addAction(PlaybackControllerBroadcastReceiver.STOP_CAST_SESSION);

        ContextCompat.registerReceiver(
                getApplicationContext(),
                playbackControllerBroadcastReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    private void updateUi(boolean connected) {
        MediaRouteButtonContainer disabledContainer = connected ?
                localPlayerUiMediaRouteButtonContainer : chromecastPlayerUiMediaRouteButtonContainer;

        MediaRouteButtonContainer enabledContainer = connected ?
                chromecastPlayerUiMediaRouteButtonContainer : localPlayerUiMediaRouteButtonContainer;

        // the media route button has a single instance.
        // therefore it has to be moved from the local YouTube player Ui to the chromecast YouTube player Ui, and vice versa.
        MediaRouteButtonUtils.addMediaRouteButtonToPlayerUi(
                mediaRouteButton,
                android.R.color.black,
                disabledContainer,
                enabledContainer
        );

        youtubePlayerView.setVisibility(connected ? View.GONE : View.VISIBLE);
        chromecastControlsRoot.setVisibility(connected ? View.VISIBLE : View.GONE);
    }
}
