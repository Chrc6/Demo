package com.chrc.kotlindemo.activity_contract.observer

import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*

/**
 *    @author : chrc
 *    date   : 2021/1/12  2:51 PM
 *    desc   :
 */
class RequestImageLifecycleObserver_Two(private val registry : ActivityResultRegistry)
    : LifecycleObserver {

    lateinit var getContent : ActivityResultLauncher<String>

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        println("=====RequestImageLifecycleObserver_Two onCreate")
        getContent = registry.register("key", owner, ActivityResultContracts.GetContent(), ActivityResultCallback<Uri?> {
            println("=====${it?.toString()}")
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        println("=====RequestImageLifecycleObserver_Two onDestroy")
    }

    fun selectImage() {
        getContent.launch("image/*")
    }
}