package com.chrc.kotlindemo.activity

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.SparseArrayCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.chrc.demo.R
import com.chrc.kotlindemo.fragment.GPreviewFragment
import com.chrc.kotlindemo.modle.PreImageInfoResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.barlibrary.ImmersionBar


class GPreviewActivity : AppCompatActivity() {

    companion object {
        const val PREIMAGE_INFOS_KEY = "preImage_infos"
    }

    lateinit var viewPager: ViewPager
    private var preImageInfoResult: PreImageInfoResult? = null
    private var curFragmentAdapter: FragmentStatePagerAdapter? = null
    private var fragments: MutableMap<Int, Fragment> = HashMap()

    private var firstIn: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_g_preview)
        ImmersionBar.setFitsSystemWindows(this)
        initData()
        initView()
//        layoutImageView(preImageInfo?.imageUrl)
    }

    private fun initData() {
        var stringExtra = intent.getStringExtra(PREIMAGE_INFOS_KEY)
        try {
            preImageInfoResult = Gson().fromJson(stringExtra, object: TypeToken<PreImageInfoResult>(){}.type)
        } catch (e: Exception) {
        }
    }

    private fun initView() {
        viewPager = findViewById(R.id.gpre_viewpager)
        preImageInfoResult?.apply {
            var pos = this.position
            preImageInfos?.let {
                curFragmentAdapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
//                    override fun getItemPosition(`object`: Any): Int {
//                        return PagerAdapter.POSITION_NONE
//                    }

                    override fun getItem(position: Int): Fragment {
                        var newInstance = fragments[position]
                        if (newInstance == null) {
                            newInstance = GPreviewFragment.newInstance(it[position], firstIn && position == pos)
                            fragments[position] = newInstance
                        }
                        Log.i("anifrag===","getItem position=${position}")
                        return newInstance
                    }

                    override fun getCount(): Int {
                        return it.size
                    }

                    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                        super.destroyItem(container, position, `object`)
                        firstIn = false
                        fragments.remove(position)
                        Log.i("anifrag===","destroyItem position=${position}")
                    }

                }
               viewPager.adapter = curFragmentAdapter
           }
            viewPager.currentItem = this.position
//            var item = curFragmentAdapter?.getItem(viewPager.currentItem)
//            if (item is GPreviewFragment) {
//                item.enterAnimator()
//            }
        }
    }


    override fun finish() {
        super.finish()
        this.overridePendingTransition(0, 0);
    }

    override fun onBackPressed() {
        var item = fragments[viewPager.currentItem]
        if (item is GPreviewFragment) {
            item.exitAnimator()
        }
    }

}