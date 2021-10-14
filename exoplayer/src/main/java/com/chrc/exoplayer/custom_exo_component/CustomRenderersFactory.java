package com.chrc.exoplayer.custom_exo_component;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * @author : chrc
 * date   : 2021/7/29  8:19 AM
 * desc   :
 */
public class CustomRenderersFactory extends DefaultRenderersFactory {
    private static final String TAG = "CustomRenderersFactory";
    private Context context;
    private DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private @ExtensionRendererMode int extensionRendererMode;
    private long allowedVideoJoiningTimeMs;

    public CustomRenderersFactory(Context context) {
        this(context, null);
    }

    public CustomRenderersFactory(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        this(context, drmSessionManager, EXTENSION_RENDERER_MODE_OFF);
    }

    public CustomRenderersFactory(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode) {
        this(context, drmSessionManager, extensionRendererMode, DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public CustomRenderersFactory(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, long allowedVideoJoiningTimeMs) {
        super(context, drmSessionManager, extensionRendererMode, allowedVideoJoiningTimeMs);
        this.context = context;
        this.drmSessionManager = drmSessionManager;
        this.extensionRendererMode = extensionRendererMode;
        this.allowedVideoJoiningTimeMs = allowedVideoJoiningTimeMs;
    }

    @Override
    public Renderer[] createRenderers(Handler eventHandler, VideoRendererEventListener videoRendererEventListener, AudioRendererEventListener audioRendererEventListener, TextOutput textRendererOutput, MetadataOutput metadataRendererOutput) {
//        return super.createRenderers(eventHandler, videoRendererEventListener, audioRendererEventListener, textRendererOutput, metadataRendererOutput);
        ArrayList<Renderer> renderersList = new ArrayList<>();
        buildVideoRenderers(context, drmSessionManager, allowedVideoJoiningTimeMs,
                eventHandler, videoRendererEventListener, extensionRendererMode, renderersList);
        buildAudioRenderers(context, drmSessionManager, buildAudioProcessors(),
                eventHandler, audioRendererEventListener, extensionRendererMode, renderersList);
        buildTextRenderers(context, textRendererOutput, eventHandler.getLooper(),
                extensionRendererMode, renderersList);
        buildMetadataRenderers(context, metadataRendererOutput, eventHandler.getLooper(),
                extensionRendererMode, renderersList);
        buildMiscellaneousRenderers(context, eventHandler, extensionRendererMode, renderersList);
        return renderersList.toArray(new Renderer[renderersList.size()]);
    }

    @Override
    protected void buildAudioRenderers(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, AudioProcessor[] audioProcessors, Handler eventHandler, AudioRendererEventListener eventListener, int extensionRendererMode, ArrayList<Renderer> out) {
//        super.buildAudioRenderers(context, drmSessionManager, audioProcessors, eventHandler, eventListener, extensionRendererMode, out);
        out.add(new CustomMediaCodecAudioRenderer(MediaCodecSelector.DEFAULT, drmSessionManager, true,
                eventHandler, eventListener, AudioCapabilities.getCapabilities(context), audioProcessors));

        if (extensionRendererMode == EXTENSION_RENDERER_MODE_OFF) {
            return;
        }
        int extensionRendererIndex = out.size();
        if (extensionRendererMode == EXTENSION_RENDERER_MODE_PREFER) {
            extensionRendererIndex--;
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer");
            Constructor<?> constructor =
                    clazz.getConstructor(
                            android.os.Handler.class,
                            com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
                            com.google.android.exoplayer2.audio.AudioProcessor[].class);
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            Renderer renderer =
                    (Renderer) constructor.newInstance(eventHandler, eventListener, audioProcessors);
            out.add(extensionRendererIndex++, renderer);
            Log.i(TAG, "Loaded LibopusAudioRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating Opus extension", e);
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            Class<?> clazz = Class.forName("com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer");
            Constructor<?> constructor =
                    clazz.getConstructor(
                            android.os.Handler.class,
                            com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
                            com.google.android.exoplayer2.audio.AudioProcessor[].class);
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            Renderer renderer =
                    (Renderer) constructor.newInstance(eventHandler, eventListener, audioProcessors);
            out.add(extensionRendererIndex++, renderer);
            Log.i(TAG, "Loaded LibflacAudioRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating FLAC extension", e);
        }

        try {
            // Full class names used for constructor args so the LINT rule triggers if any of them move.
            // LINT.IfChange
            Class<?> clazz =
                    Class.forName("com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer");
            Constructor<?> constructor =
                    clazz.getConstructor(
                            android.os.Handler.class,
                            com.google.android.exoplayer2.audio.AudioRendererEventListener.class,
                            com.google.android.exoplayer2.audio.AudioProcessor[].class);
            // LINT.ThenChange(../../../../../../../proguard-rules.txt)
            Renderer renderer =
                    (Renderer) constructor.newInstance(eventHandler, eventListener, audioProcessors);
            out.add(extensionRendererIndex++, renderer);
            Log.i(TAG, "Loaded FfmpegAudioRenderer.");
        } catch (ClassNotFoundException e) {
            // Expected if the app was built without the extension.
        } catch (Exception e) {
            // The extension is present, but instantiation failed.
            throw new RuntimeException("Error instantiating FFmpeg extension", e);
        }
    }
}
