package com.chrc.kotlindemo.custom

import android.animation.*
import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.transition.ChangeClipBounds
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.chrc.demo.widget.RoundRectangleLayoutWithClipPath
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView

/**
 *    @author : chrc
 *    date   : 2021/1/15  5:30 PM
 *    desc   :
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class UserCoverChangeClipBounds: Transition {

    private val TAG = "ChangeTransform"

    private val PROPNAME_CLIP = "android:clipBounds:clip"
    private val PROPNAME_BOUNDS = "android:clipBounds:bounds"

    private val sTransitionProperties = arrayOf(
            PROPNAME_CLIP)

    private var context: Context? = null

    constructor() {

    }
    constructor(context: Context?) {
        this.context = context
    }
    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs) {

    }

    override fun getTransitionProperties(): Array<String> {
        return sTransitionProperties
    }

    private fun captureValues(values: TransitionValues) {
        val view = values.view
        if (view.visibility == View.GONE) {
            return
        }
        val clip = view.clipBounds
        values.values[PROPNAME_CLIP] = clip
        if (clip == null) {
            val bounds = Rect(0, 0, view.width, view.height)
            values.values[PROPNAME_BOUNDS] = bounds
        }
    }

    override fun captureStartValues(transitionValues: TransitionValues?) {
        captureValues(transitionValues!!)
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        captureValues(transitionValues!!)
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues,
                                endValues: TransitionValues): Animator? {
        if (startValues == null || endValues == null || !startValues.values.containsKey(PROPNAME_CLIP)
                || !endValues.values.containsKey(PROPNAME_CLIP)) {
            return null
        }
        var start = startValues.values[PROPNAME_CLIP] as Rect?
        var end = endValues.values[PROPNAME_CLIP] as Rect?
        val endIsNull = end == null
        if (start == null && end == null) {
            return null // No animation required since there is no clip.
        }
        if (start == null) {
            start = startValues.values[PROPNAME_BOUNDS] as Rect?
        } else if (end == null) {
            end = endValues.values[PROPNAME_BOUNDS] as Rect?
        }
        if (start == end) {
            return null
        }
        endValues.view.clipBounds = start
        val evaluator = RectEvaluator(Rect())
//        val animator = ObjectAnimator.ofObject(endValues.view, "clipBounds", evaluator, start, end)
        if (endIsNull) {
            val endView = endValues.view
//            animator.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator) {
//                    endView.clipBounds = null
//                }
//            })
        }
        val animator: ValueAnimator = ValueAnimator.ofFloat(1f, 20f)
                animator.duration = 2000
                animator.addUpdateListener {
                    val curValueFloat = animator.animatedValue as Float
                    val curValue = curValueFloat.toInt()
                    val endView = endValues.view
                    if (endView is RoundRectangleLayoutWithClipPath) {
                        endView.setRate(curValue/20f)
                    }
                }
                animator.start()
        return animator
    }
}