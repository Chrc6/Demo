package com.chrc.kotlindemo.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.chrc.demo.R

/**
 *    @author : chrc
 *    date   : 2021/8/31  2:20 PM
 *    desc   :
 */
class SmallWindowView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context, attributeSet, defStyleAttr) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        var view = LayoutInflater.from(context).inflate(R.layout.view_small_window, this)
    }
}