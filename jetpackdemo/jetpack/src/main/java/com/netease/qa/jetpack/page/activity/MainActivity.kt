package com.netease.qa.jetpack.page.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.navigation.Navigation
import com.netease.qa.jetpack.R

class MainActivity : AppCompatActivity() {

    private lateinit var preTv: TextView
    private lateinit var nextTv: TextView
    private var pos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        preTv = findViewById(R.id.tv_pre)
        nextTv = findViewById(R.id.tv_next)

        preTv.setOnClickListener {
            if (pos != 0) {
                pos--
                Navigation.findNavController(this, R.id.fragment_container).popBackStack()
            }
        }

        nextTv.setOnClickListener {
            when (pos) {
                0 -> {
                    pos++
//                    var apply = TitleFragmentDirections.toGameFragment().apply {
//                        title = "directions 跳转"
//                    }
//                    Navigation.findNavController(this, R.id.fragment_container).navigate(apply.actionId, apply.arguments)
//                    Navigation.findNavController(this, R.id.fragment_container).navigate(R.id.to_game_fragment)
                }
                1 -> {
                    pos++
                    Navigation.findNavController(this, R.id.fragment_container).navigate(R.id.to_score_fragment)
                }
                else -> {

                }
            }
        }
    }
}