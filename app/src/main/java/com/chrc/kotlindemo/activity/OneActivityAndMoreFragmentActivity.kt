package com.chrc.kotlindemo.activity

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.chrc.demo.R
import com.chrc.demo.util.LiveNavRouter
import com.chrc.kotlindemo.fragment.FirstOneToMoreFragment
import com.chrc.kotlindemo.fragment.FirstOneToMoreFragmentDirections

/**
 * author : chrc
 * date   : ${DATE}  ${TIME}
 * desc   :
 *
 * fragmentManager.popBackStack(String name, int flags)
 * popBackStack(String name, int flags)参数的意义：
 * name为null，flags为0，弹出栈中最上层的fragment；
 * name为null，flags为1，弹出栈中所有的fragment；
 * name不为null，flags为0，弹出栈中该Fragment之上的Fragment；
 * name不为null，flags为1，弹出栈中该Fragment和之上的Fragment；
 * https://blog.csdn.net/ganduwei/article/details/80049430
 *
 * https://mp.weixin.qq.com/s/gPmAvylpDJj148Wtvz5pbQ
 */
class OneActivityAndMoreFragmentActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var firstFragment: Fragment
    private lateinit var twoFragment: Fragment
    private lateinit var threeFragment: Fragment
    private lateinit var frameLayout: FrameLayout

    private lateinit var navController: NavController
    private var fragments: MutableList<Fragment> = ArrayList()
    private var index = 0
    private var naviIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_and_more_fragment)
        initData()
        initView()
    }

    private fun initData() {
        firstFragment = FirstOneToMoreFragment.newInstance("first", "#ff0000")
        twoFragment = FirstOneToMoreFragment.newInstance("two", "#0000ff")
        threeFragment = FirstOneToMoreFragment.newInstance("three", "#00ff00")

        fragments.add(firstFragment)
        fragments.add(twoFragment)
        fragments.add(threeFragment)
    }

    private fun initView() {
        frameLayout = findViewById(R.id.fl_content)

//        navController = Navigation.findNavController(this, R.id.fragment_navi_container)
        var navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_navi_container) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

        findViewById<View>(R.id.tv_delease).setOnClickListener(this)
        findViewById<View>(R.id.tv_add).setOnClickListener(this)
        findViewById<View>(R.id.tv_n_delease).setOnClickListener(this)
        findViewById<View>(R.id.tv_n_add).setOnClickListener(this)

        addFragment()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_delease -> {
                popFragment()
            }
            R.id.tv_add -> {
                addFragment()
            }
            R.id.tv_n_delease -> {
                navController.popBackStack()
                naviIndex--
            }
            R.id.tv_n_add -> {
//                var navOptons = NavigationUtil.getNavOptionsBuilder().build()
//                navController.navigate(R.id.to_afragment, bundle, navOptons)
                var navOptions = LiveNavRouter.getNavOptionsBuilder().build()
//                var bundle = Bundle().apply {
//                    putString("name", getName())
//                    putString("color", getColor())
//                }
//                LiveNavRouter.Builder()
//                        .setViewId(R.id.fragment_navi_container)
//                        .setActivity(this)
//                        .setNavId(R.id.to_afragment)
//                        .setBundle(bundle)
//                        .setNavOptions(navOptions)
//                        .build()
//                        .activityJump()
                LiveNavRouter.Builder()
                        .setViewId(R.id.fragment_navi_container)
                        .setNavDirections(FirstOneToMoreFragmentDirections.toAfragment().apply {
                            name = this@OneActivityAndMoreFragmentActivity.getName()
                        })
                        .setNavOptions(navOptions)
                        .build()
                        .activityJump(this)
                naviIndex++
            }
        }
    }

    private fun popFragment() {
        index = if (index > 0) index - 1 else 0
        supportFragmentManager.popBackStack(null, 0)
    }

    private fun addFragment() {
        if (index >= 3) {
            return
        }
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.one_to_more_fragment_anim_in, R.anim.one_to_more_fragment_anim_no_change, 0, R.anim.one_to_more_fragment_anim_out)
//                .setCustomAnimations(R.anim.one_to_more_fragment_anim_out, R.anim.one_to_more_fragment_anim_in)
//                .replace(frameLayout.id, getPushFragment(index))
                .add(frameLayout.id, getPushFragment(index))
//                .addToBackStack(getPushFragment(index).javaClass.name)
                .addToBackStack(null)
                .setMaxLifecycle(getPushFragment(index), Lifecycle.State.RESUMED)
                .commit()
        index++
    }

    private fun getPushFragment(index: Int): Fragment {
        var realIndex = if (index < 0) {
            0
        } else {
            if (index < fragments.size) index else fragments.size - 1
        }
        return fragments[realIndex]
    }

    private fun getName(): String {
        return when (naviIndex % 3) {
            0 -> {
                "navi first"
            }
            1 -> {
                "navi second"
            }
            else -> "navi three"
        }
    }

    private fun getColor(): String {
        return when (naviIndex % 3) {
            0 -> {
                "#ff0000"
            }
            1 -> {
                "#00ff00"
            }
            else -> "#0000ff"
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.addOnBackStackChangedListener(object : FragmentManager.OnBackStackChangedListener {
            override fun onBackStackChanged() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    var findFragmentById: Fragment? = getStackTopFragment()
                    Log.i("onetomore===", "addOnBackStackChangedListener  is null = ${findFragmentById == null}" +
                            " size = ${supportFragmentManager.fragments.size}")
                    findFragmentById?.apply {
//                        supportFragmentManager.beginTransaction().show(this).commit()
//                        this.onStart()
//                        this.onResume()
                    }
                    supportFragmentManager.removeOnBackStackChangedListener(this)
                }
            }
        })
        super.onBackPressed()
    }

    fun getStackTopFragment(): Fragment? {
//       return supportFragmentManager.fragments[supportFragmentManager.fragments.size - 1]
       return supportFragmentManager.primaryNavigationFragment
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.fragment_navi_container).navigateUp()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /**
             * fragment回退栈还有fragment可用
             */
            if (Navigation.findNavController(this, R.id.fragment_navi_container).navigateUp()) {
//                return
            }

            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}