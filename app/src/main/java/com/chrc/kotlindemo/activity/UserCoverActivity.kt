package com.chrc.kotlindemo.activity

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.transition.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.chrc.demo.R
import com.chrc.demo.widget.RoundRectangleLayoutWithClipPath
import com.chrc.kotlindemo.custom.UserCoverChangeClipBounds
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView


class UserCoverActivity : AppCompatActivity() {

    lateinit var const: ConstraintLayout
    lateinit var userSv: SimpleDraweeView
    lateinit var userSv_two: SimpleDraweeView
    lateinit var clipView: RoundRectangleLayoutWithClipPath

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_cover)
        initView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initAnimator()
        }
    }

    private fun initView() {
        const = findViewById(R.id.cons_layout)
        userSv = findViewById(R.id.usc_sv_use_cover)
        userSv_two = findViewById(R.id.usc_sv_use_cover_2)
        clipView = findViewById(R.id.clip_parent_view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initAnimator() {
//        ViewAnimationUtils.createCircularReveal()

        val changeClipBounds = UserCoverChangeClipBounds()
        val values_start = TransitionValues()
        val values_end = TransitionValues()

        values_start.view = userSv_two
        values_end.view = clipView
        //TODO setClipBounds(Rect rect),直接指定当前view的可视区域，当前的Rect使用的view的自身的坐标系。
        //TODO startView原始大小200*200，endView原始大小600*600
        values_start.view.clipBounds = Rect(0, 0, 0, 0)
        //TODO 通过分析源码我们知道这个方法虽然可省，但是之建立在要扩展的视图显示区域是本身大小的基础上
        values_end.view.clipBounds = Rect(0, 0, 600, 600)
        changeClipBounds.captureStartValues(values_start)
        changeClipBounds.captureEndValues(values_end)
        changeClipBounds.createAnimator(const, values_start, values_end)?.setDuration(2000)?.start()
        val set = TransitionSet()
        set.addTransition(changeClipBounds)
        //TODO 顺序播放时1，同时播放是0
        set.ordering = 0


        var changeBounds = ChangeBounds()
//        var changeBounds = ChangeClipBounds()
//        changeBounds.resizeClip = true
        changeBounds.duration = 2000
        changeBounds.addListener(object: Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                println("===UserCoverActivity onTransitionEnd")
            }

            override fun onTransitionResume(transition: Transition) {
                println("===UserCoverActivity onTransitionResume")
            }

            override fun onTransitionPause(transition: Transition) {
                println("===UserCoverActivity onTransitionPause")
            }

            override fun onTransitionCancel(transition: Transition) {
                println("===UserCoverActivity onTransitionCancel")
            }

            override fun onTransitionStart(transition: Transition) {
                println("===UserCoverActivity onTransitionStart")
//                val animator: ValueAnimator = ValueAnimator.ofFloat(1f, 20f)
//                animator.duration = 2000
//                animator.addUpdateListener {
//                    val curValueFloat = animator.animatedValue as Float
//                    val curValue = curValueFloat.toInt()
//                    if (curValue == 1) {
//
//                        resetViewParams(userSv, 100f * (1 - curValue))
//                    }
//                }
//                animator.start()
            }
        })

        set.addTransition(changeBounds)

        window?.sharedElementEnterTransition = set
//        window?.sharedElementExitTransition =
    }

    private fun resetViewParams(userSv: SimpleDraweeView, radius: Float) {
        var roundingParams = RoundingParams()
        //这里是设置你希望的圆角的值
        roundingParams.setCornersRadius(radius)
        var builder = GenericDraweeHierarchyBuilder(resources)
        var hierarchy = builder.build()
        hierarchy.roundingParams = roundingParams
        //一定要先设置Hierarchy，再去加载图片，否则会加载不出来图片
        userSv.hierarchy = hierarchy
        //这里传入你的服务器提供的图片地址
//        userSv.setImageURI(“”)
    }
}