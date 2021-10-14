package com.tencent.qqmusic.mediaplayer.audiofx;

import android.util.Log;

//import com.tencent.mediaplayer.SoLibraryManager;
import com.tencent.qqmusic.mediaplayer.AudioInformation;
import com.tencent.qqmusic.mediaplayer.BufferInfo;
import com.tencent.qqmusic.mediaplayer.FloatBufferInfo;
import com.tencent.qqmusic.mediaplayer.audiofx.IAudioListener;
//import com.tencent.qqmusiccommon.util.MLog;
//import com.tencent.qqmusicplayerprocess.audio.playlist.MusicListManager;
//import com.tencent.qqmusicplayerprocess.songinfo.definition.SongType;


/**
 * 音量均衡
 *
 * @author haodongyuan on 17/3/28.
 * @since 7.5
 */
@SuppressWarnings("JniMissingFunction")
public class LoudnessInsurer implements IAudioListener {
    private static final String TAG = "LoudnessInsurer";
    private final static int ERR_OK = 0;
    private final static int ERR_LIB_NOT_LOADED = -1;
    private final static int ERR_INVALID_ARG = -2;
    private final static int ERR_INIT_FAILED = -3;

    private static boolean libLoaded;
    private long mNativeRef;

    private int channels;

    static {
        String soName = "loudnessInsurer";
        try {
//            SoLibraryManager.loadAndDownloadLibrary(soName);
            System.loadLibrary(soName);
            libLoaded = true;
        } catch (Throwable e) {
//            MLog.i(TAG, "[static initializer] failed to load lib: " + soName, e);
            Log.i(TAG, "[static initializer] failed to load lib: " + soName, e);
        }
    }

    private boolean configured = false;
    private double gain;
    private double peak;
    private double upper;
    private double lower;
    private int method;
    private int bytesPerSample;

    private synchronized native int nRelease(long instance);

    /**
     * @param inSamples sample per channel
     * @return sample per channel
     */
    private synchronized native int nProcess_8B_I8(long instance, byte[] in, byte[] out, int inSamples);

    /**
     * @param inSamples sample per channel
     * @return sample per channel
     */
    private synchronized native int nProcess_16B_I16_NE(long instance, short[] in, short[] out, int inSamples);
    private synchronized native int nProcess_32B_F32_NE(long instance, float[] in, float[] out, int inSamples);

    private synchronized native long nInit(int inChannels, int outChannels, int samplerate);
    private synchronized native int nConfig(long instance, double lower, int method, double upper, double peek, double gain, double range);
    private synchronized native int nFlush(long instance);

    @Override
    public synchronized boolean isEnabled() {
        return mNativeRef != 0 /*&& configured*/ && isSongCanEnableLoudnessInsurer();
    }

    public boolean isSongCanEnableLoudnessInsurer() {
        // 本地扫描歌曲匹配库内，SongType = SongType.SONGTYPE_LOCAL 的统一不使用音量均衡
//        return MusicListManager.getInstance().getPlaySong()!= null &&
//                MusicListManager.getInstance().getPlaySong().getType() != SongType.SONGTYPE_LOCAL;
        return true;
    }

    @Override
    public synchronized boolean onPcm(BufferInfo in, BufferInfo out, long decodeTimeMs) {
        if (!isEnabled()) {
            return false;
        }

        if (bytesPerSample <= 0) {
            return false;
        }

        int i = nProcess_8B_I8(mNativeRef, in.byteBuffer, out.byteBuffer, in.bufferSize / bytesPerSample / channels);

        if (i <= 0) {
            return false;
        }
        out.bufferSize = i * channels * bytesPerSample;
        return true;
    }

    @Override
    public synchronized boolean onPcm(FloatBufferInfo in, FloatBufferInfo out, long decodeTimeMs) {
        if (!isEnabled()) {
            return false;
        }

        // for 32bit float. a float is a single frame for a channel.
        // So we can use bufferSize/channels for sampleCount in a channel
        int i = nProcess_32B_F32_NE(mNativeRef, in.floatBuffer, out.floatBuffer, in.bufferSize / channels);

        if (i <= 0) {
            return false;
        }
        out.bufferSize = i * channels;
        return true;
    }

    public void setBytesPerSample(int bytesPerSample) {
        this.bytesPerSample = bytesPerSample;
    }

    @Override
    public long onPlayerReady(int bytePerSample, AudioInformation info, long currentPosition) {
        bytesPerSample = bytePerSample;
        return initInstance(info.getSampleRate(), info.getChannels());
    }

    @Override
    public void onPlayerSeekComplete(long l) {
        flush();
    }

    @Override
    public synchronized void onPlayerStopped() {
        if (!isEnabled()) {
            return;
        }
//        MLog.i(TAG, "[onPlayerStopped] enter");
        Log.i(TAG, "[onPlayerStopped] enter");
        nRelease(mNativeRef);
        mNativeRef = 0;
        configured = false;
//        MLog.i(TAG, "[onPlayerStopped] exit");
        Log.i(TAG, "[onPlayerStopped] exit");
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    /**
     * 设置参数，可以在任意时候设置
     * @return 是否成功
     * @param gain //!< 歌曲增益（按-18LKFS为参考响度）  per Song
     * @param peak//!< 歌曲峰值, 0~1.0 per Song
     * @param upper//!< 目标响度的上限，0表示 -18LKFS
     * @param lower//!< 目标响度的下限，0表示 -18LKFS
     * @param method  //!< 提升响度的策略:
     *          METHOD_MIN = 0,
     * 			METHOD_LIMITED_BY_PEAK = 0, //!< 提升音量并确保能防止破音
     * 			METHOD_FORCE_CLIPPING = 1, //!<  提升音量，溢出时削波到0dB
     * 			METHOD_REALTIME_LIMITER = 2, //!<  通过限制器提升音量，提升倍率是不固定的
     * 			METHOD_OFFLINE_LIMITER = 3, //!<  为了避免抽搐感，会使用更长的延迟，调用侧必须根据GetActuralLookaheadInSamples处理延迟补偿
     * 			METHOD_MAX = METHOD_OFFLINE_LIMITER
     */
    public synchronized boolean config(double gain, double peak, double upper, double lower, int method) {
        this.gain = gain;
        this.peak = peak;
        this.upper = upper;
        this.lower = lower;
        this.method = method;
        configured = true;

        if (!isEnabled()) {
//            MLog.i(TAG, "[config] not enabled! config will be set when enabled.");
            Log.i(TAG, "[config] not enabled! config will be set when enabled.");
            return false;
        }
//        MLog.i(TAG, "[config] called with: gain = [" + gain + "], peak = [" + peak + "], upper = [" + upper + "], lower = [" + lower + "], method = [" + method + "]");
        Log.i(TAG, "[config] called with: gain = [" + gain + "], peak = [" + peak + "], upper = [" + upper + "], lower = [" + lower + "], method = [" + method + "]");
        int ret = nConfig(mNativeRef, lower, method, upper, peak, gain, 0);
//        MLog.i(TAG, "[config] ret: " + ret);
        Log.i(TAG, "[config] ret: " + ret);
        return ret == ERR_OK;
    }

    public synchronized void flush() {
        if (!isEnabled()) {
            return;
        }
        nFlush(mNativeRef);
    }

    public synchronized long initInstance(long samplerate, int channels) {
//        MLog.i(TAG, "[initInstance] called with: samplerate = [" + samplerate + "], channels = [" + channels + "]");
        Log.i(TAG, "[initInstance] called with: samplerate = [" + samplerate + "], channels = [" + channels + "]");
        if (mNativeRef != 0) {
            return ERR_OK;
        }
        if (!libLoaded) {
            return ERR_LIB_NOT_LOADED;
        }
        if (samplerate == 0 || channels == 0) {
            return ERR_INVALID_ARG;
        }
        this.channels = channels;
        mNativeRef = nInit(channels, channels, (int) samplerate);
        if (mNativeRef == 0) {
//            MLog.i(TAG, "[initInstance] failed!");
            Log.i(TAG, "[initInstance] failed!");
            return ERR_INIT_FAILED;
        }
        if (configured) {
//            MLog.i(TAG, "[initInstance] got config. config now.");
            Log.i(TAG, "[initInstance] got config. config now.");
            config(gain, peak, upper, lower, method);
        }
//        MLog.i(TAG, "[initInstance] succeed.");
        Log.i(TAG, "[initInstance] succeed.");
        return ERR_OK;
    }

    @Override
    public long getActualTime(long timeMs) {
        return timeMs;
    }
}
