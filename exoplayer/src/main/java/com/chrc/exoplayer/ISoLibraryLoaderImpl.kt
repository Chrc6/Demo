package com.chrc.exoplayer

import android.util.Log
import com.tencent.qqmusic.mediaplayer.ISoLibraryLoader

/**
 *    @author : chrc
 *    date   : 2021/7/26  9:22 AM
 *    desc   :
 */
class ISoLibraryLoaderImpl: ISoLibraryLoader {
    override fun load(p0: String?): Boolean {
        Log.i("ilog===", "load. p0 = $p0")
        System.loadLibrary(p0?:"")
        return p0 != null
    }

    override fun findLibPath(p0: String?): String {
        Log.i("ilog===", "findLibPath. p0 = $p0 ")
        var libName = ""
        if (p0?.startsWith("lib") != true) {
            libName = "lib$p0"
        }

        if (p0?.endsWith(".so") != true) {
            libName = "$libName.so"
        }

        return libName
    }
}