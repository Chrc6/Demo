package com.netease.qa.jetpack.page.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.netease.qa.jetpack.R
import com.netease.qa.jetpack.Utils

class MainActivity : AppCompatActivity() {

    private lateinit var preTv: TextView
    private lateinit var nextTv: TextView
    private lateinit var fragmentContainerView: FragmentContainerView
    private var pos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        Log.i("lightning==="," jetpackdemo MainActivity onCreate ")
        Utils.printStr("MainActivity onCreate 1112")
    }

    private fun initView() {
        preTv = findViewById(R.id.tv_pre)
        nextTv = findViewById(R.id.tv_next)
        fragmentContainerView = findViewById(R.id.fragment_container)

        val myNavHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val inflater = myNavHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.all_graph_navigation)
        myNavHostFragment.navController.graph = graph

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
                    Log.i("next===","id1===${R.id.to_score_fragment}")
                }
                else -> {

                }
            }
        }
    }
}