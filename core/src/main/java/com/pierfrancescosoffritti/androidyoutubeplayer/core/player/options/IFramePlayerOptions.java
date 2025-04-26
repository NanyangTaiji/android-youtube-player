package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Options used to configure the IFrame Player. All the options are listed here:
 * [IFrame player parameters](https://developers.google.com/youtube/player_parameters#Parameters)
 */
public class IFramePlayerOptions {
    private final JSONObject playerOptions;

    public static final IFramePlayerOptions defaultOptions = new Builder().controls(1).build();

    private IFramePlayerOptions(JSONObject playerOptions) {
        this.playerOptions = playerOptions;
    }

    @Override
    public String toString() {
        return playerOptions.toString();
    }

    public String getOrigin() {
        try {
            return playerOptions.getString(Builder.ORIGIN);
        } catch (JSONException e) {
            throw new RuntimeException("Error getting origin from JSON", e);
        }
    }

    public static class Builder {
        private static final String AUTO_PLAY = "autoplay";
        private static final String MUTE = "mute";
        private static final String CONTROLS = "controls";
        private static final String ENABLE_JS_API = "enablejsapi";
        private static final String FS = "fs";
        static final String ORIGIN = "origin";
        private static final String REL = "rel";
        private static final String IV_LOAD_POLICY = "iv_load_policy";
        private static final String CC_LOAD_POLICY = "cc_load_policy";
        private static final String CC_LANG_PREF = "cc_lang_pref";
        private static final String LIST = "list";
        private static final String LIST_TYPE = "listType";
        private static final String START = "start";
        private static final String END = "end";

        private final JSONObject builderOptions;

        public Builder() {
            builderOptions = new JSONObject();
            addInt(AUTO_PLAY, 0);
            addInt(MUTE, 0);
            addInt(CONTROLS, 0);
            addInt(ENABLE_JS_API, 1);
            addInt(FS, 0);
            addString(ORIGIN, "https://www.youtube.com");
            addInt(REL, 0);
            addInt(IV_LOAD_POLICY, 3);
            addInt(CC_LOAD_POLICY, 0);
        }

        public IFramePlayerOptions build() {
            return new IFramePlayerOptions(builderOptions);
        }

        /**
         * Controls whether the web-based UI of the IFrame player is used or not.
         * @param controls If set to 0: web UI is not used. If set to 1: web UI is used.
         */
        public Builder controls(int controls) {
            addInt(CONTROLS, controls);
            return this;
        }

        /**
         * Controls if the video is played automatically after the player is initialized.
         * @param autoplay if set to 1: the player will start automatically. If set to 0: the player will not start automatically
         */
        public Builder autoplay(int autoplay) {
            addInt(AUTO_PLAY, autoplay);
            return this;
        }

        /**
         * Controls if the player will be initialized mute or not.
         * @param mute if set to 1: the player will start muted and without acquiring Audio Focus. If set to 0: the player will acquire Audio Focus
         */
        public Builder mute(int mute) {
            addInt(MUTE, mute);
            return this;
        }

        /**
         * Controls the related videos shown at the end of a video.
         * @param rel If set to 0: related videos will come from the same channel as the video that was just played. If set to 1: related videos will come from multiple channels.
         */
        public Builder rel(int rel) {
            addInt(REL, rel);
            return this;
        }

        /**
         * Controls video annotations.
         * @param ivLoadPolicy if set to 1: the player will show video annotations. If set to 3: they player won't show video annotations.
         */
        public Builder ivLoadPolicy(int ivLoadPolicy) {
            addInt(IV_LOAD_POLICY, ivLoadPolicy);
            return this;
        }

        /**
         *  This parameter specifies the default language that the player will use to display captions.
         *  If you use this parameter and also set the cc_load_policy parameter to 1, then the player
         *  will show captions in the specified language when the player loads.
         *  If you do not also set the cc_load_policy parameter, then captions will not display by default,
         *  but will display in the specified language if the user opts to turn captions on.
         *
         * @param languageCode ISO 639-1 two-letter language code
         */
        public Builder langPref(String languageCode) {
            addString(CC_LANG_PREF, languageCode);
            return this;
        }

        /**
         * Controls video captions. It doesn't work with automatically generated captions.
         * @param ccLoadPolicy if set to 1: the player will show captions. If set to 0: the player won't show captions.
         */
        public Builder ccLoadPolicy(int ccLoadPolicy) {
            addInt(CC_LOAD_POLICY, ccLoadPolicy);
            return this;
        }

        /**
         * This parameter specifies the domain from which the player is running.
         * Since the player in this library is not running from a website there should be no reason to change this.
         * Using "https://www.youtube.com" (the default value) is recommended as some functions from the IFrame Player are only available
         * when the player is running on a trusted domain.
         * @param origin your domain.
         */
        public Builder origin(String origin) {
            addString(ORIGIN, origin);
            return this;
        }

        /**
         * 	The list parameter, in conjunction with the [listType] parameter, identifies the content that will load in the player.
         * 	If the [listType] parameter value is "playlist", then the [list] parameter value specifies a YouTube playlist ID.
         * 	@param list The playlist ID to be played.
         * 	You need to prepend the playlist ID with the letters PL, for example:
         * 	if playlist id is 1234, you should pass PL1234.
         */
        public Builder list(String list) {
            addString(LIST, list);
            return this;
        }

        /**
         * Controls if the player is playing from video IDs or from playlist IDs.
         * If set to "playlist", you should use the "list" parameter to set the playlist ID
         * to be played.
         * See original documentation for more info: https://developers.google.com/youtube/player_parameters#Selecting_Content_to_Play
         * @param listType Set to "playlist" to play playlists. Then pass the playlist id to the [list] parameter.
         */
        public Builder listType(String listType) {
            addString(LIST_TYPE, listType);
            return this;
        }

        /**
         * Setting this parameter to 0 prevents the fullscreen button from displaying in the player.
         * See original documentation for more info: https://developers.google.com/youtube/player_parameters#Parameters
         * @param fs if set to 1: the player fullscreen button will be show. If set to 0: the player fullscreen button will not be shown.
         */
        public Builder fullscreen(int fs) {
            addInt(FS, fs);
            return this;
        }

        /**
         * This parameter causes the player to begin playing the video at the given number of seconds from the start of the video.
         * The parameter value is a positive integer.
         * @param startSeconds positive integer, number of seconds to offset playback from the start of the video.
         */
        public Builder start(int startSeconds) {
            addInt(START, startSeconds);
            return this;
        }

        /**
         * This parameter specifies the time, measured in seconds from the beginning of the video, when the player should stop playing the video.
         * The parameter value is a positive integer.
         * @param endSeconds positive integer specifying the time, measured in seconds from the beginning of the video, when the player should stop playing the video.
         */
        public Builder end(int endSeconds) {
            addInt(END, endSeconds);
            return this;
        }

        /**
         * The modestbranding parameter is deprecated and will have no effect.
         * To align with YouTube's branding requirements, the player now determines the appropriate branding treatment based on a combination of factors, including player size, other API parameters (e.g. controls), and additional signals.
         * See August 15, 2023 deprecation announcement: https://developers.google.com/youtube/player_parameters#release_notes_08_15_2023
         */
        @Deprecated(since = "Deprecated and will have no effect")
        public Builder modestBranding(int modestBranding) {
            return this;
        }

        private void addString(String key, String value) {
            try {
                builderOptions.put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException("Illegal JSON value " + key + ": " + value);
            }
        }

        private void addInt(String key, int value) {
            try {
                builderOptions.put(key, value);
            } catch (JSONException e) {
                throw new RuntimeException("Illegal JSON value " + key + ": " + value);
            }
        }
    }
}
