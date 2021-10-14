package com.chrc.exoplayer.listener

import android.media.session.PlaybackState
import android.util.Log
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.gson.Gson

/**
 *    @author : chrc
 *    date   : 2021/7/26  4:13 PM
 *    desc   :
 */
class EventListenerImpl: Player.EventListener {
    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        Log.i("exoplayer===event", "onTimelineChanged")
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        Log.i("exoplayer===event", "onTracksChanged")
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.i("exoplayer===event", "onLoadingChanged")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.i("exoplayer===event", "onPlayerStateChanged playbackState=$playbackState")
        when(playbackState) {
            Player.STATE_BUFFERING -> {
                Log.i("exoplayer===event", "onPlayerStateChanged STATE_BUFFERING")
            }
            Player.STATE_READY -> {
                Log.i("exoplayer===event", "onPlayerStateChanged STATE_PLAYING playWhenReady=$playWhenReady")
            }
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        Log.i("exoplayer===event", "onRepeatModeChanged")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        Log.i("exoplayer===event", "onShuffleModeEnabledChanged")
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        Log.i("exoplayer===event", "onPlayerError")
    }

    override fun onPositionDiscontinuity(reason: Int) {
        Log.i("exoplayer===event", "onPositionDiscontinuity")
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        Log.i("exoplayer===event", "onPlaybackParametersChanged")
    }

    override fun onSeekProcessed() {
        Log.i("exoplayer===event", "onSeekProcessed")
    }
}