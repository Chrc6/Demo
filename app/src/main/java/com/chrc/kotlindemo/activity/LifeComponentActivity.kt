package com.chrc.kotlindemo.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.chrc.demo.R
import com.chrc.kotlindemo.modle.viewmodel.TestViewModel

class LifeComponentActivity : KotlinBaseActivity() {
    val TAG = "LifeComponent==="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_component)

        var viewModel = ViewModelProviders.of(this).get(TestViewModel::class.java)
        var nameEvent = viewModel.getNameEvent()
        nameEvent?.observe(this, Observer<String?> {
            t -> Log.i(TAG, "onChanged: t =${t} ")
        })

        //测试上一个activity的finish时间
//        Log.i("finishtest===","LifeComponentActivity onCreate")
//        Thread.sleep(10000)
    }

    override fun onResume() {
        super.onResume()
        Log.i("finishtest===","LifeComponentActivity onResume")
    }
}