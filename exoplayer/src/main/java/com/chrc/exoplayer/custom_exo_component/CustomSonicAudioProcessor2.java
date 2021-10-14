/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.C.Encoding;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Util;
import com.tencent.qqmusic.mediaplayer.BufferInfo;
import com.tencent.qqmusic.mediaplayer.audiofx.LoudnessInsurer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * An {@link AudioProcessor} that uses the Sonic library to modify audio speed/pitch/sample rate.
 */
public final class CustomSonicAudioProcessor2 implements AudioProcessor {

  /**
   * The maximum allowed playback speed in {@link #setSpeed(float)}.
   */
  public static final float MAXIMUM_SPEED = 8.0f;
  /**
   * The minimum allowed playback speed in {@link #setSpeed(float)}.
   */
  public static final float MINIMUM_SPEED = 0.1f;
  /**
   * The maximum allowed pitch in {@link #setPitch(float)}.
   */
  public static final float MAXIMUM_PITCH = 8.0f;
  /**
   * The minimum allowed pitch in {@link #setPitch(float)}.
   */
  public static final float MINIMUM_PITCH = 0.1f;
  /**
   * Indicates that the output sample rate should be the same as the input.
   */
  public static final int SAMPLE_RATE_NO_CHANGE = -1;

  /**
   * The threshold below which the difference between two pitch/speed factors is negligible.
   */
  private static final float CLOSE_THRESHOLD = 0.01f;

  /**
   * The minimum number of output bytes at which the speedup is calculated using the input/output
   * byte counts, rather than using the current playback parameters speed.
   */
  private static final int MIN_BYTES_FOR_SPEEDUP_CALCULATION = 1024;

  private int pendingOutputSampleRateHz;
  private int channelCount;
  private int sampleRateHz;

//  private Sonic sonic;
  private CustomSonic2 sonic;
  private float speed;
  private float pitch;
  private int outputSampleRateHz;

  private ByteBuffer buffer;
  private IntBuffer shortBuffer;
  private ByteBuffer outputBuffer;
  private long inputBytes;
  private long outputBytes;
  private boolean inputEnded;
  private LoudnessInsurer loudnessInsurer;

  /**
   * Creates a new Sonic audio processor.
   */
  public CustomSonicAudioProcessor2() {
    speed = 1f;
    pitch = 1f;
    channelCount = Format.NO_VALUE;
    sampleRateHz = Format.NO_VALUE;
    outputSampleRateHz = Format.NO_VALUE;
    buffer = EMPTY_BUFFER;
    shortBuffer = buffer.asIntBuffer();
    outputBuffer = EMPTY_BUFFER;
    pendingOutputSampleRateHz = SAMPLE_RATE_NO_CHANGE;
    loudnessInsurer = new LoudnessInsurer();
  }

  /**
   * Sets the playback speed. The new speed will take effect after a call to {@link #flush()}.
   *
   * @param speed The requested new playback speed.
   * @return The actual new playback speed.
   */
  public float setSpeed(float speed) {
    this.speed = Util.constrainValue(speed, MINIMUM_SPEED, MAXIMUM_SPEED);
    return this.speed;
  }

  /**
   * Sets the playback pitch. The new pitch will take effect after a call to {@link #flush()}.
   *
   * @param pitch The requested new pitch.
   * @return The actual new pitch.
   */
  public float setPitch(float pitch) {
    this.pitch = Util.constrainValue(pitch, MINIMUM_PITCH, MAXIMUM_PITCH);
    return pitch;
  }

  /**
   * Sets the sample rate for output audio, in hertz. Pass {@link #SAMPLE_RATE_NO_CHANGE} to output
   * audio at the same sample rate as the input. After calling this method, call
   * {@link #configure(int, int, int)} to start using the new sample rate.
   *
   * @param sampleRateHz The sample rate for output audio, in hertz.
   * @see #configure(int, int, int)
   */
  public void setOutputSampleRateHz(int sampleRateHz) {
    pendingOutputSampleRateHz = sampleRateHz;
  }

  /**
   * Returns the specified duration scaled to take into account the speedup factor of this instance,
   * in the same units as {@code duration}.
   *
   * @param duration The duration to scale taking into account speedup.
   * @return The specified duration scaled to take into account speedup, in the same units as
   *     {@code duration}.
   */
  public long scaleDurationForSpeedup(long duration) {
    if (outputBytes >= MIN_BYTES_FOR_SPEEDUP_CALCULATION) {
      return outputSampleRateHz == sampleRateHz
          ? Util.scaleLargeTimestamp(duration, inputBytes, outputBytes)
          : Util.scaleLargeTimestamp(duration, inputBytes * outputSampleRateHz,
              outputBytes * sampleRateHz);
    } else {
      return (long) ((double) speed * duration);
    }
  }

  @Override
  public boolean configure(int sampleRateHz, int channelCount, @Encoding int encoding)
      throws UnhandledFormatException {
    if (encoding != C.ENCODING_PCM_16BIT) {
      throw new UnhandledFormatException(sampleRateHz, channelCount, encoding);
    }
    int outputSampleRateHz = pendingOutputSampleRateHz == SAMPLE_RATE_NO_CHANGE
        ? sampleRateHz : pendingOutputSampleRateHz;
    if (this.sampleRateHz == sampleRateHz && this.channelCount == channelCount
        && this.outputSampleRateHz == outputSampleRateHz) {
      return false;
    }
    this.sampleRateHz = sampleRateHz;
    this.channelCount = channelCount;
    this.outputSampleRateHz = outputSampleRateHz;
    loudnessInsurer.setBytesPerSample(encoding);
    loudnessInsurer.config(70d, 0.1d, 0, 0, 2);
    loudnessInsurer.initInstance(sampleRateHz, channelCount);
    return true;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public int getOutputChannelCount() {
    return channelCount;
  }

  @Override
  public int getOutputEncoding() {
    return C.ENCODING_PCM_16BIT;
  }

  @Override
  public int getOutputSampleRateHz() {
    return outputSampleRateHz;
  }

  @Override
  public void queueInput(ByteBuffer inputBuffer) {
      int inputSize = inputBuffer.remaining();
      int position = inputBuffer.position();
      Log.i("processor2===pro"," position="+position+" limit="+inputBuffer.limit()+" class="+inputBuffer.toString());

      if (inputSize == 0) {
        return;
      }
//      inputBuffer.flip();
      byte[] newByte = new byte[inputSize];
//      byte[] newByte = new byte[inputBuffer.limit()];
      inputBuffer.get(newByte);
//      inputBuffer.get(newByte, 0, inputBuffer.limit());
//      inputBuffer.clear();
//      inputBuffer.position(position);
      inputBuffer.position(position + inputSize);
//      inputBuffer.limit(limit);
      Log.i("processor2===pro2"," position="+position+" limit="+inputBuffer.limit()+" class="+inputBuffer.toString());

//      buffer.position(position);

      BufferInfo info = new BufferInfo();
      info.byteBuffer = newByte;
      info.bufferSize = newByte.length;
      BufferInfo out = new BufferInfo();
      out.byteBuffer = new byte[newByte.length];
      out.bufferSize = newByte.length;
      loudnessInsurer.onPcm(info, out, 0);

//      int capacity = Math.max(buffer.capacity(), out.bufferSize);
//    buffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
//      if (buffer.capacity() < out.bufferSize) {
//        buffer = ByteBuffer.allocateDirect(out.bufferSize).order(ByteOrder.nativeOrder());
//      } else {
//        buffer.flip();
//      }
    Log.i("processor2===pro3"," position+"+buffer.position()+ " cap="+buffer.capacity()+" length="+out.byteBuffer.length+" out.size="+out.bufferSize);

    StringBuilder sb = new StringBuilder();
    sb.append(" sb:");
    for (int i = 0; i < newByte.length / 100; i++) {
        sb.append(newByte[i]).append(" ");
    }
    Log.i("processor2===sb", sb.toString());
    StringBuilder sb2 = new StringBuilder();
    sb2.append(" sb2:");
    for (int i = 0; i < out.bufferSize / 100; i++) {
      sb2.append(out.byteBuffer[i]).append(" ");
    }
    Log.i("processor2===sb", sb2.toString());
    buffer = ByteBuffer.allocateDirect(Math.max(buffer.capacity(), out.byteBuffer.length)).order(ByteOrder.nativeOrder());
    buffer.put(out.byteBuffer);
//      buffer.put(newByte, 0, newByte.length);
//      buffer.clear();
    // TODO: 2021/8/6 chrc 都能播，有什么区别
//      buffer.position(0);
      buffer.position(position);
      outputBuffer = buffer;
//      outputBuffer = inputBuffer;
//    } else {
//      Log.i("processor2===else"," position="+inputBuffer.position()+" limit="+inputBuffer.limit()+" class="+inputBuffer.toString());
//      return;
//      outputBuffer = inputBuffer;
//    }
  }

  @Override
  public void queueEndOfStream() {
    sonic.queueEndOfStream();
    inputEnded = true;
  }

  @Override
  public ByteBuffer getOutput() {
    ByteBuffer outputBuffer = this.outputBuffer;
    this.outputBuffer = EMPTY_BUFFER;
    return outputBuffer;
  }

  @Override
  public boolean isEnded() {
    return inputEnded && (sonic == null || sonic.getSamplesAvailable() == 0);
  }

  @Override
  public void flush() {
    sonic = new CustomSonic2(sampleRateHz, channelCount, speed, pitch, outputSampleRateHz);
    outputBuffer = EMPTY_BUFFER;
    inputBytes = 0;
    outputBytes = 0;
    inputEnded = false;
  }

  @Override
  public void reset() {
    sonic = null;
    buffer = EMPTY_BUFFER;
    shortBuffer = buffer.asIntBuffer();
    outputBuffer = EMPTY_BUFFER;
    channelCount = Format.NO_VALUE;
    sampleRateHz = Format.NO_VALUE;
    outputSampleRateHz = Format.NO_VALUE;
    inputBytes = 0;
    outputBytes = 0;
    inputEnded = false;
    pendingOutputSampleRateHz = SAMPLE_RATE_NO_CHANGE;
  }

}
