package com.chrc.kotlindemo.matrix

import android.util.Log
import com.tencent.matrix.util.MatrixLog
import com.tencent.mrs.plugin.IDynamicConfig
import com.tencent.mrs.plugin.IDynamicConfig.ExptEnum
import java.util.concurrent.TimeUnit


/**
 *    @author : chrc
 *    date   : 12/28/21  7:15 PM
 *    desc   :
 */
class DynamicConfigImplDemo : IDynamicConfig {
    fun isFPSEnable(): Boolean {
        return true
    }

    fun isTraceEnable(): Boolean {
        return true
    }

    fun isSignalAnrTraceEnable(): Boolean {
        return true
    }

    fun isMatrixEnable(): Boolean {
        return true
    }

    override fun get(key: String, defStr: String): String {
        //TODO here return default value which is inside sdk, you can change it as you wish. matrix-sdk-key in class MatrixEnum.

        // for Activity leak detect
        if (ExptEnum.clicfg_matrix_resource_detect_interval_millis.name == key || ExptEnum.clicfg_matrix_resource_detect_interval_millis_bg.name == key) {
            Log.d("DynamicConfig", "Matrix.ActivityRefWatcher: clicfg_matrix_resource_detect_interval_millis 10s")
            return java.lang.String.valueOf(TimeUnit.SECONDS.toMillis(5))
        }
        if (ExptEnum.clicfg_matrix_resource_max_detect_times.name == key) {
            Log.d("DynamicConfig", "Matrix.ActivityRefWatcher: clicfg_matrix_resource_max_detect_times 5")
            return 3.toString()
        }
        return defStr
    }

    override fun get(key: String, defInt: Int): Int {
        //TODO here return default value which is inside sdk, you can change it as you wish. matrix-sdk-key in class MatrixEnum.
        if (MatrixEnum.clicfg_matrix_resource_max_detect_times.name == key) {
            MatrixLog.i(TAG, "key:$key, before change:$defInt, after change, value:2")
            return 2 //new value
        }
        if (MatrixEnum.clicfg_matrix_trace_fps_report_threshold.name == key) {
            return 10000
        }
        return if (MatrixEnum.clicfg_matrix_trace_fps_time_slice.name == key) {
            12000
        } else defInt
    }

    override fun get(key: String, defLong: Long): Long {
        //TODO here return default value which is inside sdk, you can change it as you wish. matrix-sdk-key in class MatrixEnum.
        if (MatrixEnum.clicfg_matrix_trace_fps_report_threshold.name == key) {
            return 10000L
        }
        if (MatrixEnum.clicfg_matrix_resource_detect_interval_millis.name == key) {
            MatrixLog.i(TAG, "$key, before change:$defLong, after change, value:2000")
            return 2000
        }
        return defLong
    }

    override fun get(key: String?, defBool: Boolean): Boolean {
        //TODO here return default value which is inside sdk, you can change it as you wish. matrix-sdk-key in class MatrixEnum.
        return defBool
    }

    override fun get(key: String?, defFloat: Float): Float {
        //TODO here return default value which is inside sdk, you can change it as you wish. matrix-sdk-key in class MatrixEnum.
        return defFloat
    }

    companion object {
        private const val TAG = "Matrix.DynamicConfigImplDemo"
    }
}