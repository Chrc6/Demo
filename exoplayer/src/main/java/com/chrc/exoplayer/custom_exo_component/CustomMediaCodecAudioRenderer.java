/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chrc.exoplayer.custom_exo_component;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.audiofx.Virtualizer;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlayerMessage.Target;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener.EventDispatcher;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.DefaultAudioSink;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.util.MediaClock;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.tencent.qqmusic.mediaplayer.BufferInfo;
import com.tencent.qqmusic.mediaplayer.audiofx.LoudnessInsurer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Decodes and renders audio using {@link MediaCodec} and an {@link AudioSink}.
 *
 * <p>This renderer accepts the following messages sent via {@link ExoPlayer#createMessage(Target)}
 * on the playback thread:
 *
 * <ul>
 *   <li>Message with type {@link C#MSG_SET_VOLUME} to set the volume. The message payload should be
 *       a {@link Float} with 0 being silence and 1 being unity gain.
 *   <li>Message with type {@link C#MSG_SET_AUDIO_ATTRIBUTES} to set the audio attributes. The
 *       message payload should be an {@link com.google.android.exoplayer2.audio.AudioAttributes}
 *       instance that will configure the underlying audio track.
 * </ul>
 */
@TargetApi(16)
public class CustomMediaCodecAudioRenderer extends MediaCodecRenderer implements MediaClock {

    private final EventDispatcher eventDispatcher;
    private final AudioSink audioSink;
    private final LoudnessInsurer loudnessInsurer;
    private  ByteBuffer tempBuffer = null;
    private  ByteBuffer pcmAfterBuffer = null;

    private boolean passthroughEnabled;
    private boolean codecNeedsDiscardChannelsWorkaround;
    private android.media.MediaFormat passthroughMediaFormat;
    @C.Encoding
    private int pcmEncoding;
    private int channelCount;
    private int sampleRate;
    private int encoderDelay;
    private int encoderPadding;
    private long currentPositionUs;
    private boolean allowFirstBufferPositionDiscontinuity;
    private boolean allowPositionDiscontinuity;

    /**
     * @param mediaCodecSelector A decoder selector.
     */
    public CustomMediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector) {
        this(mediaCodecSelector, null, true);
    }

    /**
     * @param mediaCodecSelector A decoder selector.
     * @param drmSessionManager For use with encrypted content. May be null if support for encrypted
     *     content is not required.
     * @param playClearSamplesWithoutKeys Encrypted media may contain clear (un-encrypted) regions.
     *     For example a media file may start with a short clear region so as to allow playback to
     *     begin in parallel with key acquisition. This parameter specifies whether the renderer is
     *     permitted to play clear regions of encrypted media files before {@code drmSessionManager}
     *     has obtained the keys necessary to decrypt encrypted regions of the media.
     */
    public CustomMediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector,
                                   @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                   boolean playClearSamplesWithoutKeys) {
        this(mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, null, null);
    }

    /**
     * @param mediaCodecSelector A decoder selector.
     * @param eventHandler A handler to use when delivering events to {@code eventListener}. May be
     *     null if delivery of events is not required.
     * @param eventListener A listener of events. May be null if delivery of events is not required.
     */
    public CustomMediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector,
                                   @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener) {
        this(mediaCodecSelector, null, true, eventHandler, eventListener);
    }

    /**
     * @param mediaCodecSelector A decoder selector.
     * @param drmSessionManager For use with encrypted content. May be null if support for encrypted
     *     content is not required.
     * @param playClearSamplesWithoutKeys Encrypted media may contain clear (un-encrypted) regions.
     *     For example a media file may start with a short clear region so as to allow playback to
     *     begin in parallel with key acquisition. This parameter specifies whether the renderer is
     *     permitted to play clear regions of encrypted media files before {@code drmSessionManager}
     *     has obtained the keys necessary to decrypt encrypted regions of the media.
     * @param eventHandler A handler to use when delivering events to {@code eventListener}. May be
     *     null if delivery of events is not required.
     * @param eventListener A listener of events. May be null if delivery of events is not required.
     */
    public CustomMediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector,
                                   @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                   boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler,
                                   @Nullable AudioRendererEventListener eventListener) {
        this(mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler,
                eventListener, (AudioCapabilities) null);
    }

    /**
     * @param mediaCodecSelector A decoder selector.
     * @param drmSessionManager For use with encrypted content. May be null if support for encrypted
     *     content is not required.
     * @param playClearSamplesWithoutKeys Encrypted media may contain clear (un-encrypted) regions.
     *     For example a media file may start with a short clear region so as to allow playback to
     *     begin in parallel with key acquisition. This parameter specifies whether the renderer is
     *     permitted to play clear regions of encrypted media files before {@code drmSessionManager}
     *     has obtained the keys necessary to decrypt encrypted regions of the media.
     * @param eventHandler A handler to use when delivering events to {@code eventListener}. May be
     *     null if delivery of events is not required.
     * @param eventListener A listener of events. May be null if delivery of events is not required.
     * @param audioCapabilities The audio capabilities for playback on this device. May be null if the
     *     default capabilities (no encoded audio passthrough support) should be assumed.
     * @param audioProcessors Optional {@link AudioProcessor}s that will process PCM audio before
     *     output.
     */
    public CustomMediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector,
                                   @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                   boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler,
                                   @Nullable AudioRendererEventListener eventListener,
                                   @Nullable AudioCapabilities audioCapabilities, AudioProcessor... audioProcessors) {
        this(mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys,
//                eventHandler, eventListener, new DefaultAudioSink(audioCapabilities, audioProcessors));
                eventHandler, eventListener, new CustomAudioSink(audioCapabilities, audioProcessors));
    }

    /**
     * @param mediaCodecSelector A decoder selector.
     * @param drmSessionManager For use with encrypted content. May be null if support for encrypted
     *     content is not required.
     * @param playClearSamplesWithoutKeys Encrypted media may contain clear (un-encrypted) regions.
     *     For example a media file may start with a short clear region so as to allow playback to
     *     begin in parallel with key acquisition. This parameter specifies whether the renderer is
     *     permitted to play clear regions of encrypted media files before {@code drmSessionManager}
     *     has obtained the keys necessary to decrypt encrypted regions of the media.
     * @param eventHandler A handler to use when delivering events to {@code eventListener}. May be
     *     null if delivery of events is not required.
     * @param eventListener A listener of events. May be null if delivery of events is not required.
     * @param audioSink The sink to which audio will be output.
     */
    public CustomMediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector,
                                         @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager,
                                         boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler,
                                         @Nullable AudioRendererEventListener eventListener, AudioSink audioSink) {
        super(C.TRACK_TYPE_AUDIO, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys);
        eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        this.audioSink = audioSink;
        audioSink.setListener(new AudioSinkListener());
        loudnessInsurer = new LoudnessInsurer();
    }

    @Override
    protected int supportsFormat(MediaCodecSelector mediaCodecSelector,
                                 DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format)
            throws DecoderQueryException {
        String mimeType = format.sampleMimeType;
        if (!MimeTypes.isAudio(mimeType)) {
            return FORMAT_UNSUPPORTED_TYPE;
        }
        int tunnelingSupport = Util.SDK_INT >= 21 ? TUNNELING_SUPPORTED : TUNNELING_NOT_SUPPORTED;
        boolean supportsFormatDrm = supportsFormatDrm(drmSessionManager, format.drmInitData);
        if (supportsFormatDrm && allowPassthrough(mimeType)
                && mediaCodecSelector.getPassthroughDecoderInfo() != null) {
            return ADAPTIVE_NOT_SEAMLESS | tunnelingSupport | FORMAT_HANDLED;
        }
        if ((MimeTypes.AUDIO_RAW.equals(mimeType) && !audioSink.isEncodingSupported(format.pcmEncoding))
                || !audioSink.isEncodingSupported(C.ENCODING_PCM_16BIT)) {
            // Assume the decoder outputs 16-bit PCM, unless the input is raw.
            return FORMAT_UNSUPPORTED_SUBTYPE;
        }
        boolean requiresSecureDecryption = false;
        DrmInitData drmInitData = format.drmInitData;
        if (drmInitData != null) {
            for (int i = 0; i < drmInitData.schemeDataCount; i++) {
                requiresSecureDecryption |= drmInitData.get(i).requiresSecureDecryption;
            }
        }
        MediaCodecInfo decoderInfo = mediaCodecSelector.getDecoderInfo(mimeType,
                requiresSecureDecryption);
        if (decoderInfo == null) {
            return requiresSecureDecryption && mediaCodecSelector.getDecoderInfo(mimeType, false) != null
                    ? FORMAT_UNSUPPORTED_DRM : FORMAT_UNSUPPORTED_SUBTYPE;
        }
        if (!supportsFormatDrm) {
            return FORMAT_UNSUPPORTED_DRM;
        }
        // Note: We assume support for unknown sampleRate and channelCount.
        boolean decoderCapable = Util.SDK_INT < 21
                || ((format.sampleRate == Format.NO_VALUE
                || decoderInfo.isAudioSampleRateSupportedV21(format.sampleRate))
                && (format.channelCount == Format.NO_VALUE
                ||  decoderInfo.isAudioChannelCountSupportedV21(format.channelCount)));
        int formatSupport = decoderCapable ? FORMAT_HANDLED : FORMAT_EXCEEDS_CAPABILITIES;
        return ADAPTIVE_NOT_SEAMLESS | tunnelingSupport | formatSupport;
    }

    @Override
    protected MediaCodecInfo getDecoderInfo(MediaCodecSelector mediaCodecSelector,
                                            Format format, boolean requiresSecureDecoder) throws DecoderQueryException {
        if (allowPassthrough(format.sampleMimeType)) {
            MediaCodecInfo passthroughDecoderInfo = mediaCodecSelector.getPassthroughDecoderInfo();
            if (passthroughDecoderInfo != null) {
                passthroughEnabled = true;
                return passthroughDecoderInfo;
            }
        }
        passthroughEnabled = false;
        return super.getDecoderInfo(mediaCodecSelector, format, requiresSecureDecoder);
    }

    /**
     * Returns whether encoded audio passthrough should be used for playing back the input format.
     * This implementation returns true if the {@link AudioSink} indicates that encoded audio output
     * is supported.
     *
     * @param mimeType The type of input media.
     * @return Whether passthrough playback is supported.
     */
    protected boolean allowPassthrough(String mimeType) {
        @C.Encoding int encoding = MimeTypes.getEncoding(mimeType);
        return encoding != C.ENCODING_INVALID && audioSink.isEncodingSupported(encoding);
    }

    @Override
    protected void configureCodec(MediaCodecInfo codecInfo, MediaCodec codec, Format format,
                                  MediaCrypto crypto) {
        codecNeedsDiscardChannelsWorkaround = codecNeedsDiscardChannelsWorkaround(codecInfo.name);
        MediaFormat mediaFormat = getMediaFormatForPlayback(format);
        if (passthroughEnabled) {
            // Override the MIME type used to configure the codec if we are using a passthrough decoder.
            passthroughMediaFormat = mediaFormat;
            passthroughMediaFormat.setString(MediaFormat.KEY_MIME, MimeTypes.AUDIO_RAW);
            codec.configure(passthroughMediaFormat, null, crypto, 0);
            passthroughMediaFormat.setString(MediaFormat.KEY_MIME, format.sampleMimeType);
        } else {
            codec.configure(mediaFormat, null, crypto, 0);
            passthroughMediaFormat = null;
        }
    }

    @Override
    public MediaClock getMediaClock() {
        return this;
    }

    @Override
    protected void onCodecInitialized(String name, long initializedTimestampMs,
                                      long initializationDurationMs) {
        eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
    }

    @Override
    protected void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        super.onInputFormatChanged(newFormat);
        eventDispatcher.inputFormatChanged(newFormat);
        // If the input format is anything other than PCM then we assume that the audio decoder will
        // output 16-bit PCM.
        pcmEncoding = MimeTypes.AUDIO_RAW.equals(newFormat.sampleMimeType) ? newFormat.pcmEncoding
                : C.ENCODING_PCM_16BIT;
        channelCount = newFormat.channelCount;
        sampleRate = newFormat.sampleRate;
        encoderDelay = newFormat.encoderDelay != Format.NO_VALUE ? newFormat.encoderDelay : 0;
        encoderPadding = newFormat.encoderPadding != Format.NO_VALUE ? newFormat.encoderPadding : 0;
    }

    @Override
    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat)
            throws ExoPlaybackException {
        @C.Encoding int encoding;
        MediaFormat format;
        if (passthroughMediaFormat != null) {
            encoding = MimeTypes.getEncoding(passthroughMediaFormat.getString(MediaFormat.KEY_MIME));
            format = passthroughMediaFormat;
        } else {
            encoding = pcmEncoding;
            format = outputFormat;
        }
        int channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int[] channelMap;
        if (codecNeedsDiscardChannelsWorkaround && channelCount == 6 && this.channelCount < 6) {
            channelMap = new int[this.channelCount];
            for (int i = 0; i < this.channelCount; i++) {
                channelMap[i] = i;
            }
        } else {
            channelMap = null;
        }

        try {
            audioSink.configure(encoding, channelCount, sampleRate, 0, channelMap, encoderDelay,
                    encoderPadding);
        } catch (AudioSink.ConfigurationException e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
    }

    /**
     * Called when the audio session id becomes known. The default implementation is a no-op. One
     * reason for overriding this method would be to instantiate and enable a {@link Virtualizer} in
     * order to spatialize the audio channels. For this use case, any {@link Virtualizer} instances
     * should be released in {@link #onDisabled()} (if not before).
     *
     * @see AudioSink.Listener#onAudioSessionId(int)
     */
    protected void onAudioSessionId(int audioSessionId) {
        // Do nothing.
    }

    /**
     * @see AudioSink.Listener#onPositionDiscontinuity()
     */
    protected void onAudioTrackPositionDiscontinuity() {
        // Do nothing.
    }

    /**
     * @see AudioSink.Listener#onUnderrun(int, long, long)
     */
    protected void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs,
                                        long elapsedSinceLastFeedMs) {
        // Do nothing.
    }

    @Override
    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        super.onEnabled(joining);
        eventDispatcher.enabled(decoderCounters);
        int tunnelingAudioSessionId = getConfiguration().tunnelingAudioSessionId;
        if (tunnelingAudioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            audioSink.enableTunnelingV21(tunnelingAudioSessionId);
        } else {
            audioSink.disableTunneling();
        }
    }

    @Override
    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        super.onPositionReset(positionUs, joining);
        audioSink.reset();
        currentPositionUs = positionUs;
        allowFirstBufferPositionDiscontinuity = true;
        allowPositionDiscontinuity = true;
    }

    @Override
    protected void onStarted() {
        super.onStarted();
        audioSink.play();
    }

    @Override
    protected void onStopped() {
        audioSink.pause();
        updateCurrentPosition();
        super.onStopped();
    }

    @Override
    protected void onDisabled() {
        try {
            audioSink.release();
        } finally {
            try {
                super.onDisabled();
            } finally {
                decoderCounters.ensureUpdated();
                eventDispatcher.disabled(decoderCounters);
            }
        }
    }

    @Override
    public boolean isEnded() {
        return super.isEnded() && audioSink.isEnded();
    }

    @Override
    public boolean isReady() {
        return audioSink.hasPendingData() || super.isReady();
    }

    @Override
    public long getPositionUs() {
        if (getState() == STATE_STARTED) {
            updateCurrentPosition();
        }
        return currentPositionUs;
    }

    @Override
    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
        return audioSink.setPlaybackParameters(playbackParameters);
    }

    @Override
    public PlaybackParameters getPlaybackParameters() {
        return audioSink.getPlaybackParameters();
    }

    @Override
    protected void onQueueInputBuffer(DecoderInputBuffer buffer) {
        if (allowFirstBufferPositionDiscontinuity && !buffer.isDecodeOnly()) {
            // TODO: Remove this hack once we have a proper fix for [Internal: b/71876314].
            // Allow the position to jump if the first presentable input buffer has a timestamp that
            // differs significantly from what was expected.
            if (Math.abs(buffer.timeUs - currentPositionUs) > 500000) {
                currentPositionUs = buffer.timeUs;
            }
            allowFirstBufferPositionDiscontinuity = false;
        }
    }

    @Override
    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec,
                                          ByteBuffer buffer, int bufferIndex, int bufferFlags, long bufferPresentationTimeUs,
                                          boolean shouldSkip) throws ExoPlaybackException {
        if (passthroughEnabled && (bufferFlags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // Discard output buffers from the passthrough (raw) decoder containing codec specific data.
            codec.releaseOutputBuffer(bufferIndex, false);
            return true;
        }

        if (shouldSkip) {
            codec.releaseOutputBuffer(bufferIndex, false);
            decoderCounters.skippedOutputBufferCount++;
            audioSink.handleDiscontinuity();
            return true;
        }

        try {
            if (tempBuffer != buffer) {
                tempBuffer = buffer;
//                pcmAfterBuffer = buffer;
                pcmAfterBuffer = ByteBuffer.allocate(buffer.capacity());
                pcmAfterBuffer.position(buffer.position());
                pcmAfterBuffer.limit(buffer.limit());
//                pcmAfterBuffer = buffer.slice();
            } else {
                pcmAfterBuffer.position(buffer.position());
                pcmAfterBuffer.limit(buffer.limit());
            }
            Log.i("customrender==="," pcmEncoding="+pcmEncoding + " samplerate="+sampleRate
                    +" channelCount="+channelCount
                    +" position="+buffer.position()+" cap="+buffer.capacity()+" limit="+buffer.limit() + " remaining="+buffer.remaining()+" "+buffer.toString());
            int position = buffer.position();
            int limit = buffer.limit();
            byte[] newByte = new byte[position + 1];
//            buffer.flip();
//            if (buffer.hasRemaining()) {
//                while (buffer.hasRemaining()) {
//                    newByte[buffer.position()] = buffer.get();
//                }
//                buffer.clear();

//                loudnessInsurer.setBytesPerSample(pcmEncoding);
//                BufferInfo info = new BufferInfo();
//                info.byteBuffer = newByte;
//                info.bufferSize = newByte.length;
//                BufferInfo out = new BufferInfo();
//                out.byteBuffer = new byte[newByte.length];
//                out.bufferSize = newByte.length;
//                loudnessInsurer.initInstance(sampleRate, channelCount);
//                loudnessInsurer.onPcm(info, out, 0);

//                pcmAfterBuffer.clear();
//                pcmAfterBuffer.put(newByte);
////                pcmAfterBuffer.put(out.byteBuffer);
//                pcmAfterBuffer.position(position);
////                pcmAfterBuffer.limit(out.bufferSize);
//                pcmAfterBuffer.limit(limit);
//            }
//            buffer.position(position);
//            buffer.limit(limit);
            Log.i("customrender===","handleBuffer before position="+pcmAfterBuffer.position()+" cap="+pcmAfterBuffer.capacity()+" limit="+pcmAfterBuffer.limit());

//            if (audioSink.handleBuffer(pcmAfterBuffer, bufferPresentationTimeUs)) {
            if (audioSink.handleBuffer(buffer, bufferPresentationTimeUs)) {
                Log.i("customrender==="," position="+pcmAfterBuffer.position()+" cap="+pcmAfterBuffer.capacity()+" limit="+pcmAfterBuffer.limit());
//                buffer.position(pcmAfterBuffer.position());
//                buffer.limit(pcmAfterBuffer.limit());
                codec.releaseOutputBuffer(bufferIndex, false);
                decoderCounters.renderedOutputBufferCount++;
                return true;
            }
        } catch (AudioSink.InitializationException | AudioSink.WriteException e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
        Log.i("customrender==="," position="+pcmAfterBuffer.position()+" cap="+pcmAfterBuffer.capacity()+" limit="+pcmAfterBuffer.limit());
//        buffer.position(pcmAfterBuffer.position());
//        buffer.limit(pcmAfterBuffer.limit());
        return false;
    }

    @Override
    protected void renderToEndOfStream() throws ExoPlaybackException {
        try {
            audioSink.playToEndOfStream();
        } catch (AudioSink.WriteException e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
    }

    @Override
    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        switch (messageType) {
            case C.MSG_SET_VOLUME:
                audioSink.setVolume((Float) message);
                break;
            case C.MSG_SET_AUDIO_ATTRIBUTES:
                AudioAttributes audioAttributes = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    audioAttributes = (AudioAttributes) message;
                    audioSink.setAudioAttributes(audioAttributes);
                }
                break;
            default:
                super.handleMessage(messageType, message);
                break;
        }
    }

    private void updateCurrentPosition() {
        long newCurrentPositionUs = audioSink.getCurrentPositionUs(isEnded());
        if (newCurrentPositionUs != AudioSink.CURRENT_POSITION_NOT_SET) {
            currentPositionUs =
                    allowPositionDiscontinuity
                            ? newCurrentPositionUs
                            : Math.max(currentPositionUs, newCurrentPositionUs);
            allowPositionDiscontinuity = false;
        }
    }

    /**
     * Returns whether the decoder is known to output six audio channels when provided with input with
     * fewer than six channels.
     * <p>
     * See [Internal: b/35655036].
     */
    private static boolean codecNeedsDiscardChannelsWorkaround(String codecName) {
        // The workaround applies to Samsung Galaxy S6 and Samsung Galaxy S7.
        return Util.SDK_INT < 24 && "OMX.SEC.aac.dec".equals(codecName)
                && "samsung".equals(Util.MANUFACTURER)
                && (Util.DEVICE.startsWith("zeroflte") || Util.DEVICE.startsWith("herolte")
                || Util.DEVICE.startsWith("heroqlte"));
    }

    private final class AudioSinkListener implements AudioSink.Listener {

        @Override
        public void onAudioSessionId(int audioSessionId) {
            eventDispatcher.audioSessionId(audioSessionId);
            CustomMediaCodecAudioRenderer.this.onAudioSessionId(audioSessionId);
        }

        @Override
        public void onPositionDiscontinuity() {
            onAudioTrackPositionDiscontinuity();
            // We are out of sync so allow currentPositionUs to jump backwards.
           allowPositionDiscontinuity = true;
        }

        @Override
        public void onUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            eventDispatcher.audioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
            onAudioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
        }

    }

}
