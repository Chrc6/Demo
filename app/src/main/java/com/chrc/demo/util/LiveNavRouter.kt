package com.chrc.demo.util

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.chrc.demo.R


/**
 *    @author : chrc
 *    date   : 9/18/21  9:53 AM
 *    desc   :
 */
class LiveNavRouter {

    var view: View? = null
    var navId: Int = -1
    var bundle: Bundle? = null
    var navOptions: NavOptions? = null
//    var activity: Activity? = null
    var viewId: Int = -1
    var directions: NavDirections? = null

    constructor(builder: Builder) {
        this.view = builder.view
        this.navId = builder.navId
        this.bundle = builder.bundle
        this.navOptions = builder.navOptions
//        this.activity = builder.activity
        this.viewId = builder.viewId
        this.directions = builder.directions

        if (isInValidParam()) {
            throw IllegalStateException("navId is not init")
        }
    }

    companion object {
        fun getNavOptionsBuilder(): NavOptions.Builder {
            return NavOptions.Builder().apply {
                setEnterAnim(R.anim.one_to_more_fragment_anim_in)
                setExitAnim(R.anim.one_to_more_fragment_anim_no_change)
                setPopExitAnim(R.anim.one_to_more_fragment_anim_out)
//                setPopUpTo()
            }
        }
    }

    private fun isInValidParam(): Boolean {
        return directions == null && navId == -1
    }

    fun fragmentJump() {
        view?.apply {
            Navigation.findNavController(this).navigate(navId, bundle, navOptions)
        } ?: run {
            Log.i("livenav===", "view is not ini")
        }
    }

    fun activityJump(activity: Activity?) {
        if (viewId == -1) {
            throw IllegalStateException("viewId is not init")
        }
        if (activity is AppCompatActivity) {
            when {
               directions != null -> {
                   Navigation.findNavController(activity as AppCompatActivity, viewId).navigate(directions!!.actionId, directions!!.arguments, navOptions)
               }
                else -> {
                    Navigation.findNavController(activity as AppCompatActivity, viewId).navigate(navId, bundle, navOptions)
                }
            }
        }
    }

    class Builder {
        var view: View? = null
        var navId: Int = -1
        var bundle: Bundle? = null
        var navOptions: NavOptions? = null
        var activity: Activity? = null
        var viewId: Int = -1
        var directions: NavDirections? = null

        fun setView(view: View?): Builder {
            this.view = view
            return this
        }

        fun setNavId(navId: Int): Builder {
            this.navId = navId
            return this
        }

        fun setBundle(bundle: Bundle?): Builder {
            this.bundle = bundle
            return this
        }

        fun setNavOptions(navOptions: NavOptions?): Builder {
            this.navOptions = navOptions
            return this
        }

//        fun setActivity(activity: Activity?): Builder {
//            this.activity = activity
//            return this
//        }

        fun setViewId(viewId: Int): Builder {
            this.viewId = viewId
            return this
        }

        fun setNavDirections(directions: NavDirections): Builder {
            this.directions = directions
            return this
        }

        fun build(): LiveNavRouter {
            return LiveNavRouter(this)
        }
    }

}