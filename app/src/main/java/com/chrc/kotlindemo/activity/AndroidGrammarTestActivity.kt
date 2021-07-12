package com.chrc.kotlindemo.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LifecycleObserver
import com.chrc.demo.R
import com.chrc.kotlindemo.activity_contract.observer.RequestImageLifecycleObserver
import com.chrc.kotlindemo.activity_contract.observer.RequestImageLifecycleObserver_Two

class AndroidGrammarTestActivity : AppCompatActivity() {

    lateinit var tv: TextView
    lateinit var tvSec: TextView

    lateinit var observer : RequestImageLifecycleObserver_Two

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android_grammar_test)

        initView()
        initData()

    }

    private fun initData() {
        val name = intent.getStringExtra("name")
        println("=====接收到的数据为：$name")
        tv.text = name

        observer = RequestImageLifecycleObserver_Two(activityResultRegistry)
        lifecycle.addObserver(observer)
    }

    private fun initView() {
        tv = findViewById(R.id.android_grammar_test_tv)
        tvSec = findViewById(R.id.android_grammar_test_tv_sec)
        tv.setOnClickListener() {
            val intent = Intent().apply {
                putExtra("result","Hello，我是回传的数据！")
            }
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        tvSec.setOnClickListener() {
            observer.selectImage()
        }
    }
}