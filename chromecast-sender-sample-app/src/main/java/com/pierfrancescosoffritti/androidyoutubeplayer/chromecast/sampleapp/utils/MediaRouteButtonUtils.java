package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.utils;

import android.content.Context;
import android.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.mediarouter.R;
import androidx.mediarouter.app.MediaRouteButton;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.ui.MediaRouteButtonContainer;

public class MediaRouteButtonUtils {

    public static MediaRouteButton initMediaRouteButton(Context context) {
        MediaRouteButton mediaRouteButton = new MediaRouteButton(context);
        CastButtonFactory.setUpMediaRouteButton(context, mediaRouteButton);
        return mediaRouteButton;
    }

    public static MediaRouteButton initMediaRouteButton(MediaRouteButton mediaRouteButton) {
        CastButtonFactory.setUpMediaRouteButton(mediaRouteButton.getContext(), mediaRouteButton);
        return mediaRouteButton;
    }

    public static void addMediaRouteButtonToPlayerUi(
            MediaRouteButton mediaRouteButton, int tintColor,
            MediaRouteButtonContainer disabledContainer, MediaRouteButtonContainer activatedContainer) {

        setMediaRouterButtonTint(mediaRouteButton, tintColor);

        if (disabledContainer != null) {
            disabledContainer.removeMediaRouteButton(mediaRouteButton);
        }

        if (mediaRouteButton.getParent() != null) return;
        activatedContainer.addMediaRouteButton(mediaRouteButton);
    }

    private static void setMediaRouterButtonTint(MediaRouteButton mediaRouterButton, int color) {
        ContextThemeWrapper castContext = new ContextThemeWrapper(mediaRouterButton.getContext(), R.style.Theme_MediaRouter);
        android.content.res.TypedArray styledAttributes = castContext.obtainStyledAttributes(
                null,
                R.styleable.MediaRouteButton,
                R.attr.mediaRouteButtonStyle,
                0
        );
        android.graphics.drawable.Drawable drawable =
                styledAttributes.getDrawable(R.styleable.MediaRouteButton_externalRouteEnabledDrawable);

        styledAttributes.recycle();

        if (drawable != null) {
            DrawableCompat.setTint(
                    drawable,
                    ContextCompat.getColor(mediaRouterButton.getContext(), color)
            );
        }

        mediaRouterButton.setRemoteIndicatorDrawable(drawable);
    }
}