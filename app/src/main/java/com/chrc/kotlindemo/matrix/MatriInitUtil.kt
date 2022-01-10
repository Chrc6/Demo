package com.chrc.kotlindemo.matrix

import android.app.Application
import android.content.Context
import com.tencent.matrix.Matrix
import com.tencent.matrix.iocanary.IOCanaryPlugin
import com.tencent.matrix.iocanary.config.IOConfig
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.config.TraceConfig
import com.tencent.matrix.util.MatrixLog
import java.io.File

/**
 *    @author : chrc
 *    date   : 12/30/21  10:46 AM
 *    desc   :
 */
object MatriInitUtil {
    private const val TAG = "Matrix.MatriInitUtil"

    fun initMatrix(application: Application) {
        val builder = Matrix.Builder(application) // build matrix
        builder.pluginListener(TestPluginListener(application)) // add general pluginListener

        val dynamicConfig = DynamicConfigImplDemo() // dynamic config
        // Configure trace canary.
        val tracePlugin = configureTracePlugin(application, dynamicConfig)
        builder.plugin(tracePlugin)

        // init plugin
        val ioCanaryPlugin = IOCanaryPlugin(IOConfig.Builder()
                .dynamicConfig(dynamicConfig)
                .build())
        //add to matrix
        builder.plugin(ioCanaryPlugin)

        //init matrix
        Matrix.init(builder.build())
        // start plugin
        tracePlugin.start()
        MatrixLog.i(TAG, "Matrix configurations done.")
    }

    private fun configureTracePlugin(context: Context, dynamicConfig: DynamicConfigImplDemo): TracePlugin {
        val fpsEnable = dynamicConfig.isFPSEnable()
        val traceEnable = dynamicConfig.isTraceEnable()
        val signalAnrTraceEnable = dynamicConfig.isSignalAnrTraceEnable()
        val traceFileDir: File = File(context.applicationContext.filesDir, "matrix_trace")
        if (!traceFileDir.exists()) {
            if (traceFileDir.mkdirs()) {
                MatrixLog.e(TAG, "failed to create traceFileDir")
            }
        }
        val anrTraceFile = File(traceFileDir, "anr_trace") // path : /data/user/0/sample.tencent.matrix/files/matrix_trace/anr_trace
        val printTraceFile = File(traceFileDir, "print_trace") // path : /data/user/0/sample.tencent.matrix/files/matrix_trace/print_trace
        val traceConfig = TraceConfig.Builder()
                .dynamicConfig(dynamicConfig)
                .enableFPS(fpsEnable)
                .enableEvilMethodTrace(traceEnable)
                .enableAnrTrace(traceEnable)
                .enableStartup(traceEnable)
                .enableIdleHandlerTrace(traceEnable) // Introduced in Matrix 2.0
                .enableMainThreadPriorityTrace(true) // Introduced in Matrix 2.0
                .enableSignalAnrTrace(signalAnrTraceEnable) // Introduced in Matrix 2.0
                .anrTracePath(anrTraceFile.absolutePath)
                .printTracePath(printTraceFile.absolutePath)
                .splashActivities("sample.tencent.matrix.SplashActivity;")
                .isDebug(true)
                .isDevEnv(false)
                .build()

        //Another way to use SignalAnrTracer separately
        //useSignalAnrTraceAlone(anrTraceFile.getAbsolutePath(), printTraceFile.getAbsolutePath());
        return TracePlugin(traceConfig)
    }
}