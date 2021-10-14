package com.chrc.exoplayer.listener

import android.util.Log
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.gson.Gson
import com.tencent.qqmusic.streaming.util.GsonUtil

/**
 *    @author : chrc
 *    date   : 2021/7/26  3:27 PM
 *    desc   :
 */
class AudioRendererEventListenerImpl: AudioRendererEventListener {

    override fun onAudioEnabled(counters: DecoderCounters?) {
        Log.i("exoplayer===render", "onAudioEnabled counters=${Gson().toJson(counters)}")
    }

    override fun onAudioSessionId(audioSessionId: Int) {
        Log.i("exoplayer===render", "onAudioSessionId audioSessionId=$audioSessionId")
    }

    override fun onAudioDecoderInitialized(decoderName: String?, initializedTimestampMs: Long, initializationDurationMs: Long) {
        Log.i("exoplayer===render", "onAudioDecoderInitialized decoderName=$decoderName initializedTimestampMs=$initializedTimestampMs initializationDurationMs=$initializationDurationMs")
    }

    override fun onAudioInputFormatChanged(format: Format?) {
        Log.i("exoplayer===render", "onAudioInputFormatChanged format=${Gson().toJson(format)}")
        Log.i("exoplayer===render", "onAudioInputFormatChanged channelCount=${format?.channelCount} sampleRate=${format?.sampleRate}")
    }

    override fun onAudioSinkUnderrun(bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {
        Log.i("exoplayer===render", "onAudioSinkUnderrun bufferSize=$bufferSize bufferSizeMs=$bufferSizeMs elapsedSinceLastFeedMs=$elapsedSinceLastFeedMs")
    }

    override fun onAudioDisabled(counters: DecoderCounters?) {
        Log.i("exoplayer===render", "onAudioDisabled counters=${Gson().toJson(counters)}")
    }
}