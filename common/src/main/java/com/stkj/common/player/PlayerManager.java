package com.stkj.common.player;

import android.content.Context;
import android.content.Intent;

import com.stkj.common.player.exoplayer.ExoPlayerActivity;
import com.stkj.common.player.mediaplayer.MediaPlayerActivity;

public class PlayerManager {

    public static void goToMediaPlay(Context context, String videoUrl, String videoTitle) {
        Intent intent = new Intent(context, MediaPlayerActivity.class);
        intent.putExtra(MediaPlayerActivity.INTENT_VIDEO_URI, videoUrl);
        intent.putExtra(MediaPlayerActivity.INTENT_VIDEO_TITLE, videoTitle);
        context.startActivity(intent);
    }

    public static void goToExoPlay(Context context, String videoUrl, String videoTitle) {
        Intent intent = new Intent(context, ExoPlayerActivity.class);
        intent.putExtra(ExoPlayerActivity.INTENT_VIDEO_URI, videoUrl);
        intent.putExtra(ExoPlayerActivity.INTENT_VIDEO_TITLE, videoTitle);
        context.startActivity(intent);
    }

}
