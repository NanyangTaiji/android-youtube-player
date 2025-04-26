package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.sampleapp.ui;

import android.content.Context;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.List;

/**
 * Class providing setup info to the Chromecast framework, declared in manifest file.
 *
 * [see doc here](https://developers.google.com/cast/docs/android_sender_integrate#initialize_the_cast_context)
 */
public class CastOptionsProvider implements OptionsProvider {
    // This is the receiver id of the sample receiver.
    // Remember to change it with the ID of your own receiver. See documentation for more info.
    private final String receiverId = "C5CBE8CA";

    @Override
    public CastOptions getCastOptions(Context appContext) {
        return new CastOptions.Builder()
                .setReceiverApplicationId(receiverId)
                .build();
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}
