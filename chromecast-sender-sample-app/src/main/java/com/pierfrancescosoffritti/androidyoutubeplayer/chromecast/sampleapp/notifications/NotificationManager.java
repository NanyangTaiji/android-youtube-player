package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.notifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.cyplayersample.R;

public class NotificationManager extends AbstractYouTubePlayerListener {
    private final Context context;
    private final Class<?> notificationHostActivity;
    private final int notificationId = 1;
    private final String channelId = "CHANNEL_ID";
    private final NotificationCompat.Builder notificationBuilder;

    public NotificationManager(Context context, Class<?> notificationHostActivity) {
        this.context = context;
        this.notificationHostActivity = notificationHostActivity;

        initNotificationChannel();
        notificationBuilder = initNotificationBuilder();
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "chromecast-youtube-player",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("sample-app");
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private NotificationCompat.Builder initNotificationBuilder() {
        int flags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_IMMUTABLE;
        }

        Intent openActivityExplicitIntent = new Intent(context.getApplicationContext(), notificationHostActivity);
        PendingIntent openActivityPendingIntent = PendingIntent.getActivity(
                context.getApplicationContext(),
                0,
                openActivityExplicitIntent,
                flags
        );

        Intent togglePlaybackImplicitIntent = new Intent(PlaybackControllerBroadcastReceiver.TOGGLE_PLAYBACK);
        PendingIntent togglePlaybackPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                togglePlaybackImplicitIntent,
                flags
        );

        Intent stopCastSessionImplicitIntent = new Intent(PlaybackControllerBroadcastReceiver.STOP_CAST_SESSION);
        PendingIntent stopCastSessionPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                stopCastSessionImplicitIntent,
                flags
        );

        return new NotificationCompat.Builder(context, channelId)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_cast_connected_24dp)
                .setContentIntent(openActivityPendingIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_play_arrow_24dp, "Toggle Playback", togglePlaybackPendingIntent)
                .addAction(
                        R.drawable.ic_cast_connected_24dp,
                        "Disconnect from chromecast",
                        stopCastSessionPendingIntent
                )
                .setStyle(new MediaStyle().setShowActionsInCompactView(0, 1));
    }

    public void showNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public void dismissNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
        if (state == PlayerConstants.PlayerState.PLAYING) {
            notificationBuilder.mActions.get(0).icon = R.drawable.ic_pause_24dp;
        } else {
            notificationBuilder.mActions.get(0).icon = R.drawable.ic_play_arrow_24dp;
        }

        showNotification();
    }
}
