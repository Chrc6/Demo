package com.chrc.kotlindemo

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*

/**
 *    @author : chrc
 *    date   : 2021/8/2  12:57 PM
 *    desc   :
 */
class TestService: Service() {
    inner class MyBinder : Binder() {
        val service: TestService
            get() = this@TestService
    }

    //通过binder实现调用者client与Service之间的通信
    private val binder = MyBinder()

    private val generator: Random = Random()

    override fun onCreate() {
        Log.i("DemoLog", "TestService -> onCreate, Thread: " + Thread.currentThread().name)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("DemoLog", "TestService -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().name)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i("DemoLog", "TestService -> onBind, Thread: " + Thread.currentThread().name)
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.i("DemoLog", "TestService -> onUnbind, from:" + intent.getStringExtra("from"))
        return false
    }

    override fun onDestroy() {
        Log.i("DemoLog", "TestService -> onDestroy, Thread: " + Thread.currentThread().name)
        super.onDestroy()
    }

    //getRandomNumber是Service暴露出去供client调用的公共方法
    fun getRandomNumber(): Int {
        return generator.nextInt()
    }
}