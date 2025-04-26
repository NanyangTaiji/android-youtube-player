package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public final class PlayServicesUtils {
    private PlayServicesUtils() {} // Prevent instantiation

    /**
     * It's not safe to use Google Cast functionalities if GooglePlay Services aren't available,
     * this utility function is used to check if GooglePlay Services are available and up to date.
     *
     * The answer will be available in onActivityResult.
     */
    public static void checkGooglePlayServicesAvailability(
            Activity activity,
            int googlePlayServicesAvailabilityRequestCode,
            Runnable onSuccess
    ) {
        int googlePlayServicesAvailabilityResult =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
        if (googlePlayServicesAvailabilityResult == ConnectionResult.SUCCESS) {
            onSuccess.run();
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(
                    activity,
                    googlePlayServicesAvailabilityResult,
                    googlePlayServicesAvailabilityRequestCode,
                    null
            ).show();
        }
    }
}