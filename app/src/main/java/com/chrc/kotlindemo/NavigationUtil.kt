package com.chrc.kotlindemo

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.chrc.demo.R

/**
 *    @author : chrc
 *    date   : 9/18/21  9:53 AM
 *    desc   :
 */
object NavigationUtil {

    fun getNavOptionsBuilder(): NavOptions.Builder {
        return NavOptions.Builder().apply {
            setEnterAnim(R.anim.one_to_more_fragment_anim_in)
            setExitAnim(R.anim.one_to_more_fragment_anim_no_change)
            setPopExitAnim(R.anim.one_to_more_fragment_anim_out)
        }
    }

    fun jump(navController: NavController?) {
//        navController?.apply {
//            navigatorProvider.addNavigator()
//        }
    }
}