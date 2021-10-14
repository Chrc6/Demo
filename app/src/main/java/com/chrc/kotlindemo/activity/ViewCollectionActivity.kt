package com.chrc.kotlindemo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.chrc.demo.R

class ViewCollectionActivity : AppCompatActivity() {

    private lateinit var tvOne: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_collection2)
        initView()
    }

    private fun initView() {
        tvOne = findViewById(R.id.tv_one)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tvOne, 15, 20, 2, TypedValue.COMPLEX_UNIT_DIP)
    }
}