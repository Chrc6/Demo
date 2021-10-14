package com.chrc.kotlindemo.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chrc.demo.R
import com.chrc.demo.util.Util
import com.chrc.kotlindemo.TestService

class ResorceChooseActivity : AppCompatActivity() {

    private lateinit var tvOne:TextView
    private var datas: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resorce_choose)
        initView()
        bindServiceTest()
    }

    private fun initView() {
        tvOne = findViewById(R.id.tv_resource_one)
        tvOne.post {
            Log.i("height===", "height=${tvOne.height} devicewidth=${Util.getDeviceWidthPixels(this)} height=${Util.getDeviceHeightPixels(this)}")
            val format = String.format("%02x", (80.0 / 100 * 255).toInt())
            Log.i("height===", "format=$format ")
        }
    }

    private fun bindServiceTest() {
        try {
            val intent: Intent = Intent(this, TestService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            datas.add(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val intent: Intent = Intent(this, TestService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            datas.add(2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            unbindService(serviceConnection);
            datas.remove(1)
        } catch (e: Exception) {
            e.printStackTrace();
        }

    }
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.i("player===", "serviceConnection o size=${datas.size}")
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }
}