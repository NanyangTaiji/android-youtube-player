package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils;

import android.os.Build;

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.MessageFromReceiver;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JSONUtils {
    private JSONUtils() {} // Prevent instantiation

    public static String buildFlatJson(String... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Arguments must be in pairs");
        }

        StringBuilder jsonBuilder = new StringBuilder("{");
        for (int i = 0; i < args.length; i += 2) {
            jsonBuilder.append("\"").append(args[i]).append("\": \"").append(args[i+1]).append("\",");
        }
        if (args.length > 0) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }
        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

    public static String buildCommunicationConstantsJson(String commandKey, String commandValue,
                                                         String constantsKey, String constantsValue) {
        StringBuilder jsonBuilder = new StringBuilder("{");
        jsonBuilder.append("\"").append(commandKey).append("\": \"").append(commandValue).append("\",");
        jsonBuilder.append("\"").append(constantsKey).append("\": ").append(constantsValue);
        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

    public static MessageFromReceiver parseMessageFromReceiverJson(String json) {
        String[] strings = json.split(",");
        List<String> values = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            values = Arrays.stream(strings)
                    .map(s -> s.split(":")[1].trim().replace("\"", "").replace("{", "").replace("}", ""))
                    .collect(Collectors.toList());
        }

        return new MessageFromReceiver(values.get(0), values.get(1));
    }
}
