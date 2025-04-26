package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.List;

public class NetworkObserver {
    public interface Listener {
        void onNetworkAvailable();
        void onNetworkUnavailable();
    }

    public final List<Listener> listeners = new ArrayList<>();
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private ConnectivityManager.NetworkCallback networkCallback;
    private final Context context;

    public NetworkObserver(Context context) {
        this.context = context;
    }

    public void observeNetwork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            doObserveNetwork(context);
        } else {
            doObserveNetworkLegacy(context);
        }
    }

    public void destroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (networkCallback == null) return;
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } else {
            if (networkBroadcastReceiver == null) return;
            try {
                context.unregisterReceiver(networkBroadcastReceiver);
            } catch (Exception ignored) {}
        }

        listeners.clear();
        networkCallback = null;
        networkBroadcastReceiver = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void doObserveNetwork(Context context) {
        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onAvailable(Network network) {
                mainThreadHandler.post(() -> {
                    for (Listener listener : listeners) {
                        listener.onNetworkAvailable();
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                mainThreadHandler.post(() -> {
                    for (Listener listener : listeners) {
                        listener.onNetworkUnavailable();
                    }
                });
            }
        };
        networkCallback = callback;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(callback);
    }

    private void doObserveNetworkLegacy(Context context) {
        networkBroadcastReceiver = new NetworkBroadcastReceiver(
                () -> {
                    for (Listener listener : listeners) {
                        listener.onNetworkAvailable();
                    }
                },
                () -> {
                    for (Listener listener : listeners) {
                        listener.onNetworkUnavailable();
                    }
                }
        );
        context.registerReceiver(networkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private static class NetworkBroadcastReceiver extends BroadcastReceiver {
        private final Runnable onNetworkAvailable;
        private final Runnable onNetworkUnavailable;

        NetworkBroadcastReceiver(Runnable onNetworkAvailable, Runnable onNetworkUnavailable) {
            this.onNetworkAvailable = onNetworkAvailable;
            this.onNetworkUnavailable = onNetworkUnavailable;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnectedToInternet(context)) {
                onNetworkAvailable.run();
            } else {
                onNetworkUnavailable.run();
            }
        }
    }

    private static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (networkCapabilities == null) return false;
            return isConnectedToInternet(networkCapabilities);
        } else {
            android.net.NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean isConnectedToInternet(NetworkCapabilities networkCapabilities) {
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
    }
}
