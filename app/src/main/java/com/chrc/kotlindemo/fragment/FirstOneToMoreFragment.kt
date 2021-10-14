package com.chrc.kotlindemo.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chrc.demo.R

/**
 *    @author : chrc
 *    date   : 9/17/21  10:02 AM
 *    desc   :
 */
class FirstOneToMoreFragment: BaseOneToMoreFragment() {

    private lateinit var titleTv: TextView
    private var name: String? = ""
    private var color: String? = ""

    companion object {
        fun newInstance(name: String?, color: String?): Fragment {
            var fragment = FirstOneToMoreFragment()
            var bundle = Bundle().apply {
                putString("name",name)
                putString("color",color)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("onetomore===", "onCreateView name=${name}")
        var view: View = inflater.inflate(R.layout.fragment_onetomore_first, container, false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        arguments?.apply {
            name = getString("name") ?: "no data"
            color = getString("color") ?: "#ff0000"
            titleTv.text = name
            titleTv.setBackgroundColor(Color.parseColor(color))
        }
    }

    private fun initView(view: View?) {
        view?.apply {
            titleTv = findViewById(R.id.tv_frag_title)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("onetomore===", "onResume name=${name}")
    }

    override fun onPause() {
        super.onPause()
        Log.i("onetomore===", "onPause name=${name}")
    }

    override fun onStart() {
        super.onStart()
        Log.i("onetomore===", "onStart name=${name}")
    }

    override fun onStop() {
        super.onStop()
        Log.i("onetomore===", "onStop name=${name}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onetomore===", "onDestroy name=${name}")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.i("onetomore===", "onHiddenChanged name=${name}")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("onetomore===", "onAttach name=${name}")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("onetomore===", "onDetach name=${name}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("onetomore===", "onDestroyView name=${name}")
    }

}