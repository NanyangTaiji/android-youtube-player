package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.cyplayersample.R;

/**
 * Class used to control a simple Ui for the cast player.
 */
public class SimpleChromeCastUiController extends AbstractYouTubePlayerListener implements SeekBar.OnSeekBarChangeListener {
    public YouTubePlayer youTubePlayer;

    private boolean isPlaying = false;

    private final View controls_view;
    private final View progressBar;
    private final ImageView playPauseButton;
    private final TextView currentTimeTextView;
    private final TextView totalTimeTextView;
    private final SeekBar seekBar;
    private final ImageView youTubeButton;

    private final FrameLayout newViewsContainer;

    private boolean seekBarTouchStarted = false;

    // I need this variable because onCurrentSecond gets called every 100 mill, so without the proper checks on this variable in onCurrentSeconds the seek bar glitches when touched.
    private int newSeekBarProgress = -1;

    public SimpleChromeCastUiController(View controls_view) {
        this.controls_view = controls_view;

        progressBar = controls_view.findViewById(R.id.progress_bar);
        playPauseButton = controls_view.findViewById(R.id.toggle_playback_button);
        currentTimeTextView = controls_view.findViewById(R.id.current_time_text_view);
        totalTimeTextView = controls_view.findViewById(R.id.total_time_text_view);
        seekBar = controls_view.findViewById(R.id.seek_bar);
        youTubeButton = controls_view.findViewById(R.id.youtube_button);

        newViewsContainer = controls_view.findViewById(R.id.cast_button_container);

        progressBar.setVisibility(View.INVISIBLE);

        seekBar.setOnSeekBarChangeListener(this);
        playPauseButton.setOnClickListener(v -> onPlayButtonPressed());
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
        newSeekBarProgress = -1;

        updateControlsState(state);

        if (state == PlayerConstants.PlayerState.PLAYING ||
                state == PlayerConstants.PlayerState.PAUSED ||
                state == PlayerConstants.PlayerState.VIDEO_CUED ||
                state == PlayerConstants.PlayerState.UNSTARTED) {
            progressBar.setVisibility(View.INVISIBLE);
            playPauseButton.setVisibility(View.VISIBLE);
        } else if (state == PlayerConstants.PlayerState.BUFFERING) {
            progressBar.setVisibility(View.VISIBLE);
            playPauseButton.setVisibility(View.INVISIBLE);
        }

        boolean playing = state == PlayerConstants.PlayerState.PLAYING;
        updatePlayPauseButtonIcon(playing);
    }

    @Override
    public void onVideoDuration(YouTubePlayer youTubePlayer, float duration) {
        totalTimeTextView.setText(formatTime(duration));
        seekBar.setMax((int) duration);
    }

    @Override
    public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
        if (seekBarTouchStarted)
            return;

        // ignore if the current time is older than what the user selected with the SeekBar
        if (newSeekBarProgress > 0 && !formatTime(second).equals(formatTime((float) newSeekBarProgress)))
            return;

        newSeekBarProgress = -1;

        seekBar.setProgress((int) second);
    }

    @Override
    public void onVideoLoadedFraction(YouTubePlayer youTubePlayer, float loadedFraction) {
        seekBar.setSecondaryProgress((int) loadedFraction);
    }

    @Override
    public void onVideoId(YouTubePlayer youTubePlayer, String videoId) {
        youTubeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
            controls_view.getContext().startActivity(intent);
        });
    }

    public void addView(View view) {
        newViewsContainer.addView(view);
    }

    public void removeView(View view) {
        newViewsContainer.removeView(view);
    }

    private void updateControlsState(PlayerConstants.PlayerState state) {
        switch (state) {
            case ENDED:
            case PAUSED:
            case BUFFERING:
                isPlaying = false;
                break;
            case PLAYING:
                isPlaying = true;
                break;
            case UNSTARTED:
                resetUi();
                break;
            default:
                // Do nothing
        }

        updatePlayPauseButtonIcon(!isPlaying);
    }

    public void resetUi() {
        seekBar.setProgress(0);
        seekBar.setMax(0);
        playPauseButton.setVisibility(View.INVISIBLE);
        currentTimeTextView.post(() -> currentTimeTextView.setText(""));
        totalTimeTextView.post(() -> totalTimeTextView.setText(""));
    }

    private void updatePlayPauseButtonIcon(boolean playing) {
        int img = playing ? R.drawable.ayp_ic_pause_36dp : R.drawable.ayp_ic_play_36dp;
        playPauseButton.setImageResource(img);
    }

    private void onPlayButtonPressed() {
        if (isPlaying)
            youTubePlayer.pause();
        else
            youTubePlayer.play();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        currentTimeTextView.setText(formatTime((float) i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        seekBarTouchStarted = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (isPlaying)
            newSeekBarProgress = seekBar.getProgress();

        youTubePlayer.seekTo((float) seekBar.getProgress());
        seekBarTouchStarted = false;
    }

    /**
     * Transform the time in seconds in a string with format "M:SS".
     */
    private String formatTime(float timeInSeconds) {
        int minutes = (int) (timeInSeconds / 60);
        int seconds = (int) (timeInSeconds % 60);
        return String.format("%d:%02d", minutes, seconds);
    }
}
