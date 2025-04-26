package com.pierfrancescosoffritti.androidyoutubeplayer.core.player;

public class PlayerConstants {

    public enum PlayerState {
        UNKNOWN, UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, VIDEO_CUED
    }

    public enum PlaybackQuality {
        UNKNOWN, SMALL, MEDIUM, LARGE, HD720, HD1080, HIGH_RES, DEFAULT
    }

    public enum PlayerError {
        UNKNOWN, INVALID_PARAMETER_IN_REQUEST, HTML_5_PLAYER, VIDEO_NOT_FOUND, VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER
    }

    public enum PlaybackRate {
        UNKNOWN, RATE_0_25, RATE_0_5, RATE_0_75, RATE_1, RATE_1_25, RATE_1_5, RATE_1_75, RATE_2;

        public float toFloat() {
            switch (this) {
                case UNKNOWN: return 1f;
                case RATE_0_25: return 0.25f;
                case RATE_0_5: return 0.5f;
                case RATE_0_75: return 0.75f;
                case RATE_1: return 1f;
                case RATE_1_25: return 1.25f;
                case RATE_1_5: return 1.5f;
                case RATE_1_75: return 1.75f;
                case RATE_2: return 2f;
                default: return 1f;
            }
        }
    }
}
