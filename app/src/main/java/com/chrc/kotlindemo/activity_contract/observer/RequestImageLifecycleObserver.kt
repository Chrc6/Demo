package com.chrc.kotlindemo.activity_contract.observer

import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 *    @author : chrc
 *    date   : 2021/1/12  2:24 PM
 *    desc   :
 */
class RequestImageLifecycleObserver(private val registry : ActivityResultRegistry)
    : DefaultLifecycleObserver {

    lateinit var getContent : ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        println("=====RequestImageLifecycleObserver onCreate")
        getContent = registry.register("key", owner, ActivityResultContracts.GetContent(), ActivityResultCallback<Uri?> {
            println("=====${it?.toString()}")
        })
    }

    override fun onDestroy(owner: LifecycleOwner) {
        println("=====RequestImageLifecycleObserver onDestroy")
    }

    fun selectImage() {
        getContent.launch("image/*")
    }
}