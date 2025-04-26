package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure;

public final class MessageFromReceiver {
    private final String type;
    private final String data;

    public MessageFromReceiver(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
