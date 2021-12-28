package com.chrc.exoplayer

import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chrc.exoplayer.custom_exo_component.CustomBandwidthMeter
import com.chrc.exoplayer.listener.AudioRendererEventListenerImpl
import com.chrc.exoplayer.listener.EventListenerImpl
import com.chrc.exoplayer.listener.MedaDataOutputImpl
import com.chrc.exoplayer.listener.VideoListenerImpl
import com.chrc.exoplayer.okhttp.OkHttpDataSourceFactory
import com.chrc.exoplayer.okhttp.OkHttpEventListenerImpl
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.tencent.qqmusic.mediaplayer.AudioPlayerConfigure
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tvPlay: TextView
    private lateinit var tvPause: TextView
    private lateinit var tvChangeSpeed: TextView
    private lateinit var exoPlayerView: PlayerView

    private lateinit var player: SimpleExoPlayer

    var videoPlayUrl =  "asset:///test_lounder_and_smaller_music.mp3"
//    var videoPlayUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    //穿越女特工，嗜血太子妃
//    var videoPlayUrl = "http://vb.wting.info/youshengxiaoshuo/chuanyuejiakong/cyntgsxtzf/lrgfwbukblt.m4a?token=fP3Ig0-sqkLP1SZKF6GjFg**_rt2JW9TnzLHzDiGw1f6YWQ**&e=1630456009095" +
//        "&t=2&res=623371787&source=ANDROID&sign=5792e8c7e51924c697e6e19c8d48016e&d=1&ct=1630031209095&sk=-209472062"

    var bandwidthMeter: CustomBandwidthMeter? = null
    var strTest: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initAudioConfigure()
        initPlayer()
    }

    private fun initAudioConfigure() {
        //设置日志代理
        //默认使用系统日志输出
        AudioPlayerConfigure.setLog(ILogImpl())
        //设置so加载器
        //默认使用System.loadLibrary(String soName);
        //findLibPath 默认添加“lib”前缀以及“.so”后缀
        AudioPlayerConfigure.setSoLibraryLoader(ISoLibraryLoaderImpl())
        //设置开启Native层的Log //logFolder:日志目录，为空将会回调ILog
//        AudioPlayerConfigure.enableNativeLog(String logFolder)

        System.loadLibrary("loudnessInsurer")
    }

    private fun initPlayer() {
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        var mTrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this),
//        player = ExoPlayerFactory.newSimpleInstance(CustomRenderersFactory(this),
                mTrackSelector)
        player.addAudioDebugListener(AudioRendererEventListenerImpl())
        player.addMetadataOutput(MedaDataOutputImpl())
        player.addListener(EventListenerImpl())
        player.addVideoListener(VideoListenerImpl())
        player.volume = 1f
//        player.seekTo()

//        mediaExtractorInfo()

        exoPlayerView.visibility = View.VISIBLE
        exoPlayerView.player = player
        player.addListener(listener)
        player.repeatMode = Player.REPEAT_MODE_OFF
//        player.playWhenReady = true

        var videoUrl = videoPlayUrl

        var uri = Uri.parse(videoUrl)
        val mediaDataSourceFactory: DataSource.Factory = buildDataSourceFactory(uri, true)
        var mVideoSource = ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)

        player.prepare(mVideoSource)
        player.playWhenReady = true
    }

    private fun mediaExtractorInfo() {
        Log.i("exoplayer===extractor", " extractor start")
        var mediaExtractor = MediaExtractor()
        try {
            mediaExtractor.setDataSource(videoPlayUrl)
            val mf: MediaFormat = mediaExtractor.getTrackFormat(0)
            val bitRate: Int = mf.getInteger(MediaFormat.KEY_BIT_RATE)
            val sampleRate: Int = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            var channelCount = mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            var channelMask = mf.getString(MediaFormat.KEY_CHANNEL_MASK)
            Log.i("exoplayer===extractor", " bitRate=$bitRate sampleRate=$sampleRate channelCount=$channelCount channelMask=$channelMask")
        } catch (e: Exception) {
            Log.i("exoplayer===extractor", " exception msg=${e.fillInStackTrace()}")
        }
        Log.i("exoplayer===extractor", " extractor end")
    }

    private fun initView() {
        tvPlay = findViewById(R.id.tv_play)
        tvPause = findViewById(R.id.tv_pause)
        exoPlayerView = findViewById(R.id.exo_player_view)
        tvChangeSpeed = findViewById(R.id.tv_change_speed)

        tvPlay.setOnClickListener(this)
        tvPause.setOnClickListener(this)
        tvChangeSpeed.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_play -> {
                player.playWhenReady = true
//                strTest = "strTest"
//                strTest!!.isPhone("click")
//                strTest.isMail("click")
            }
            R.id.tv_pause -> {
                player.playWhenReady = false
//                strTest = null
//                try {
//                    strTest?.isPhone("pause")
//                    strTest.isMail("pause")
//                } catch (e: java.lang.Exception) {
//                    Log.i("test==="," error=${e.fillInStackTrace()}")
//                }
            }
            R.id.tv_change_speed -> {
                var videoUrl = videoPlayUrl
                var uri = Uri.parse(videoUrl)
                val mediaDataSourceFactory: DataSource.Factory = buildDataSourceFactory(uri, true)
                var mVideoSource = ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)

                player.prepare(mVideoSource)
                val playbackParameters = PlaybackParameters(2.0f, 1.0f)
                player.playbackParameters = playbackParameters
                player.playWhenReady = true
            }
        }
    }

    private fun buildDataSourceFactory(uri: Uri?, useBandwidthMeter: Boolean): DataSource.Factory {
        bandwidthMeter = if (useBandwidthMeter) CustomBandwidthMeter(Handler()) { elapsedMs, bytes, bitrate ->
            Log.i("exoplayer===bit", " buildDataSourceFactory elapsedMs=$elapsedMs bytes=$bytes bitrate=$bitrate") } else null
        return DefaultDataSourceFactory(this, bandwidthMeter, buildOkHttpDataSourceFactory(uri, bandwidthMeter))
    }

    private fun buildOkHttpDataSourceFactory(uri: Uri?, bandwidthMeter: CustomBandwidthMeter?): DataSource.Factory? {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.eventListener(OkHttpEventListenerImpl())
        return OkHttpDataSourceFactory(builder.build(), "exo_test", bandwidthMeter)
    }

    private var listener = object : Player.EventListener {
        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            Log.i("player===", " onTimelineChanged")
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            Log.i("player===", " onTracksChanged")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Log.i("player===", " onLoadingChanged")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when(playbackState) {
                Player.STATE_BUFFERING -> {

                }
                Player.STATE_READY -> {
                    if (!playWhenReady) {
                        var bitrateEstimate = bandwidthMeter?.bitrateEstimate
                        Log.i("exoplayer===bit", " bitrateEstimate=$bitrateEstimate")
                    }
                }
            }
            Log.i("player===", " onPlayerStateChanged playWhenReady=$playWhenReady playbackState=$playbackState")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            Log.i("player===", " onRepeatModeChanged")
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            Log.i("player===", " onShuffleModeEnabledChanged")
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            Log.i("player===", " onPlayerError msg=${error?.rendererException?.message}")
        }

        override fun onPositionDiscontinuity(reason: Int) {
            Log.i("player===", " onPositionDiscontinuity")
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            Log.i("player===", " onPlaybackParametersChanged")
        }

        override fun onSeekProcessed() {
            Log.i("player===", " onSeekProcessed")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        player?.apply {
            stop()
            release()
        }
    }
}

private fun String.isPhone(tag: String?) {
    Log.i("test===", " isPhone tag=$tag")
}

private fun String?.isMail(tag: String?) {
    Log.i("test===", " isMail tag=$tag")
}
