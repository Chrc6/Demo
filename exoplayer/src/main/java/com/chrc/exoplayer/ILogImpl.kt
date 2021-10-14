package com.chrc.exoplayer

import android.util.Log
import com.tencent.qqmusic.mediaplayer.ILog

/**
 *    @author : chrc
 *    date   : 2021/7/26  9:18 AM
 *    desc   :
 */
class ILogImpl: ILog {
    override fun d(p0: String?, p1: String?) {
        Log.i("ilog===", "d. p0 = $p0 p1 = $p1")
    }

    override fun w(p0: String?, p1: String?) {
        Log.i("ilog===", "w. p0 = $p0 p1 = $p1")
    }

    override fun e(p0: String?, p1: String?) {
        Log.i("ilog===", "e. p0 = $p0 p1 = $p1")
    }

    override fun e(p0: String?, p1: Throwable?) {
        Log.i("ilog===2", "e. p0 = $p0 p1 = ${p1?.fillInStackTrace()}")
    }

    override fun e(p0: String?, p1: String?, p2: Throwable?) {
        Log.i("ilog===3", "d. p0 = $p0 p1 = $p1 p2 = ${p2?.fillInStackTrace()}")
    }

    override fun e(p0: String?, p1: String?, vararg p2: Any?) {
        Log.i("ilog===4", "d. p0 = $p0 p1 = $p1 p2 = ${p2?.toString()}")
    }

    override fun i(p0: String?, p1: String?) {
        Log.i("ilog===", "i. p0 = $p0 p1 = $p1")
    }

    override fun i(p0: String?, p1: String?, p2: Throwable?) {
        Log.i("ilog===2", "i. p0 = $p0 p1 = $p1 p2 = ${p2?.fillInStackTrace()}")
    }
}