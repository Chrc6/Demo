package com.chrc.exoplayer.listener

import android.util.Log
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.gson.Gson

/**
 *    @author : chrc
 *    date   : 2021/7/26  3:37 PM
 *    desc   :
 */
class MedaDataOutputImpl: MetadataOutput {
    override fun onMetadata(metadata: Metadata?) {
        /**
         * metadata中有samplereate和accessibilityChannel?
         */
        Log.i("exoplayer===metadata", " onMetadata metadata=${Gson().toJson(metadata)}")
    }
}