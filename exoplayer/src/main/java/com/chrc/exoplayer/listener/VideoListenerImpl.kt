package com.chrc.exoplayer.listener

import android.util.Log
import com.google.android.exoplayer2.video.VideoListener
import com.google.gson.Gson

/**
 *    @author : chrc
 *    date   : 2021/7/26  4:16 PM
 *    desc   :
 */
class VideoListenerImpl: VideoListener {
    override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        Log.i("exoplayer===video", " onVideoSizeChanged")
    }

    override fun onRenderedFirstFrame() {
        Log.i("exoplayer===video", " onRenderedFirstFrame")
    }
}