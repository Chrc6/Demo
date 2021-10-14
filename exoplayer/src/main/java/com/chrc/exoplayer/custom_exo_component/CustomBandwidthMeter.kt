package com.chrc.exoplayer.custom_exo_component

import android.os.Handler
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.SlidingPercentile
import kotlin.math.sqrt

/**
 *    @author : chrc
 *    date   : 2021/8/10  11:06 AM
 *    desc   :
 */
class CustomBandwidthMeter: BandwidthMeter, TransferListener<Any> {

    /**
     * The default maximum weight for the sliding window.
     */
    val DEFAULT_MAX_WEIGHT = 2000

    private val ELAPSED_MILLIS_FOR_ESTIMATE = 2000
    private val BYTES_TRANSFERRED_FOR_ESTIMATE = 512 * 1024

    private var eventHandler: Handler? = null
    private var eventListener: BandwidthMeter.EventListener? = null
    private var slidingPercentile: SlidingPercentile? = null
    private var clock: Clock? = null

    private var streamCount = 0
    private var sampleStartTimeMs: Long = 0
    private var sampleBytesTransferred: Long = 0

    private var totalElapsedTimeMs: Long = 0
    private var totalBytesTransferred: Long = 0
    private var bitrateEstimate: Long = 0

    constructor (): this(null, null) {

    }

    constructor(eventHandler: Handler?, eventListener: BandwidthMeter.EventListener?)
        : this(eventHandler, eventListener, DefaultBandwidthMeter.DEFAULT_MAX_WEIGHT){

    }

    constructor(eventHandler: Handler?, eventListener: BandwidthMeter.EventListener?, maxWeight: Int)
        : this(eventHandler, eventListener, maxWeight, Clock.DEFAULT) {

        }

    constructor(eventHandler: Handler?, eventListener: BandwidthMeter.EventListener?, maxWeight: Int, clock: Clock = Clock.DEFAULT) {
        this.eventHandler = eventHandler
        this.eventListener = eventListener
        slidingPercentile = SlidingPercentile(maxWeight)
        this.clock = clock
        bitrateEstimate = BandwidthMeter.NO_ESTIMATE
    }

    override fun getBitrateEstimate(): Long {
        return bitrateEstimate
    }

    override fun onTransferStart(source: Any?, dataSpec: DataSpec?) {
        if (streamCount == 0) {
            sampleStartTimeMs = clock!!.elapsedRealtime()
        }
        streamCount++
    }

    override fun onBytesTransferred(source: Any?, bytesTransferred: Int) {
        sampleBytesTransferred += bytesTransferred.toLong()
    }

    override fun onTransferEnd(source: Any?) {
        Assertions.checkState(streamCount > 0)
        val nowMs = clock!!.elapsedRealtime()
        val sampleElapsedTimeMs = (nowMs - sampleStartTimeMs).toInt()
        totalElapsedTimeMs += sampleElapsedTimeMs.toLong()
        totalBytesTransferred += sampleBytesTransferred
        if (sampleElapsedTimeMs > 0) {
            val bitsPerSecond = (sampleBytesTransferred * 8000 / sampleElapsedTimeMs).toFloat()
            slidingPercentile!!.addSample(sqrt(sampleBytesTransferred.toDouble()).toInt(), bitsPerSecond)
            if (totalElapsedTimeMs >= ELAPSED_MILLIS_FOR_ESTIMATE
                    || totalBytesTransferred >= BYTES_TRANSFERRED_FOR_ESTIMATE) {
                val bitrateEstimateFloat = slidingPercentile!!.getPercentile(0.5f)
                bitrateEstimate = if (java.lang.Float.isNaN(bitrateEstimateFloat)) BandwidthMeter.NO_ESTIMATE else bitrateEstimateFloat.toLong()
            }
        }
        notifyBandwidthSample(sampleElapsedTimeMs, sampleBytesTransferred, bitrateEstimate)
        if (--streamCount > 0) {
            sampleStartTimeMs = nowMs
        }
        sampleBytesTransferred = 0
    }

    private fun notifyBandwidthSample(elapsedMs: Int, bytes: Long, bitrate: Long) {
        if (eventHandler != null && eventListener != null) {
            eventHandler!!.post { eventListener!!.onBandwidthSample(elapsedMs, bytes, bitrate) }
        }
    }
}