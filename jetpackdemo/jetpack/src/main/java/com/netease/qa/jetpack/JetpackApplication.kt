package com.netease.qa.jetpack

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 *    @author : chrc
 *    date   : 10/12/21  9:08 AM
 *    desc   :
 */
class JetpackApplication: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}