package com.pierfrancescosoffritti.androidyoutubeplayer.core.sampleapp.utils;

import java.util.Random;

public class VideoIdsProvider {
  private static final String[] videoIds = {"C-9PbZw8CEo", "Una0L5sysk8", "njX2bu-_Vw4", "w3ZvTDSCPuI"};
  private static final String[] liveVideoIds = {"hHW1oY26kxQ"};
  private static final Random random = new Random();

  public static String getNextVideoId() {
    return videoIds[random.nextInt(videoIds.length)];
  }

  public static String getNextLiveVideoId() {
    return liveVideoIds[random.nextInt(liveVideoIds.length)];
  }
}
